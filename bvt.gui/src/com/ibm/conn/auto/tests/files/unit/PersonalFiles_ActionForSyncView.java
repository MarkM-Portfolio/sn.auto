package com.ibm.conn.auto.tests.files.unit;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;

import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Files_Folder_Dropdown_Menu;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class PersonalFiles_ActionForSyncView extends FilesUnitBaseSetUp{
    private static Logger log = LoggerFactory.getLogger(PersonalFiles_ActionForSyncView.class);
	private String serverURL;
	private CommunitiesUI comUI;
    
    @BeforeClass(alwaysRun=true)
    public void SetUpClass(){
        personalFilesSetUpClass();
    }
    
    
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        personalFilesSetUp();
    }
    
    /**
    *<ul>SAMPLE TEST TO DEMONSTRATE SUCCESS
    *<li><B>Info:</B> Perform actions on add a top level folder in Sync View</li>
    *<li><B>Step:</B> Create a folder</li>
    *<li><B>Step:</B> go to list view</li>
    *<li><B>Step:</B> click more link</li>
    *<li><B>Step:</B> click Add to Sync Button</li>
    *<li><B>Verify:</B> Add successfully</li>
    *<li><B>Step:</B>click Remove From Sync Button</li>
    *<li><B>Verify:</B> Removed successfully</li>
    *</ul>
    */
    
    @Test(groups = {"unit"})
    public void testAddFolderToSyncByMoreLink() throws Exception {
        DefectLogger logger = dlog.get(Thread.currentThread().getId());
        String gk_flag = "FILES_FOLDER_SYNCABLE";
        if (gkc.getSetting(gk_flag)) {
            ui.startTest();
            BaseFolder folder = new BaseFolder.Builder("folder A"
                    + Helper.genDateBasedRand()).build();
            
            // Click on My folders
            clickMyFoldersView();
            //Create a new folder
            logger.strongStep("Create a new folder");
            log.info("INFO: Create a new folder");
            folder.create(ui, false);
            
            ui.clickLinkWait(FilesUIConstants.DisplayList);
            
            // Verify folder A exist.
            logger.strongStep("Verify folder A exist.");
            log.info("INFO: Verify folder A exist.");
            Assert.assertTrue(
                    driver.isElementPresent(FilesUI.selectMyFolder(folder)),
                    "ERROR: Folder A is not found ");
            
            // click more link
            logger.strongStep("click more link.");
            ui.selectFolderMoreLink(folder);
            
            // click "add to Sync" button in more link.
            logger.strongStep("Add Folder A to Sync.");
            ui.clickLinkWait(AddToMyDriveMoreLink);
            
            if(driver.isElementPresent(ui.getSelectorForMoreLink(folder.getName()))){
                ui.selectFolderMoreLink(folder);
            }
            
            logger.strongStep("verify if folder A was added to Sync successfully.");
            // Check if the "Remove From Sync" button shows and "Add to Sync" button disappears, if yes, added successfully.
            Assert.assertTrue(
                    driver.isElementPresent(RemoveFromMyDriveMoreLink),
                    "ERROR: Folder A is not added to Sync Successfully");
            Assert.assertFalse(
                    driver.isElementPresent(AddToMyDriveMoreLink),
                    "ERROR: Folder A is not added to Sync Successfully");
            
            log.info("INFO: Verify folder A was added to Sync successfully.");
            
            // Remove Folder A from Sync.
            logger.strongStep("Remove Folder A from Sync.");
            ui.clickLinkWait(RemoveFromMyDriveMoreLink);
            
            
            if(driver.isElementPresent(ui.getSelectorForMoreLink(folder.getName()))){
                ui.selectFolderMoreLink(folder);
            }
            
            logger.strongStep("verify if folder A was removed from Sync successfully.");
            // Check if the "Remove From Sync" button disappears and "Add to Sync" button shows, if yes, removed successfully.
            Assert.assertFalse(
                    driver.isElementPresent(RemoveFromMyDriveMoreLink),
                    "ERROR: Folder A is not removed from Sync Successfully");
            Assert.assertTrue(
                    driver.isElementPresent(AddToMyDriveMoreLink),
                    "ERROR: Folder A is not removed from Sync Successfully");
            
            log.info("INFO: Remove folder A was removed from Sync successfully.");
            
            ui.endTest();
        }
    }
    
    /**
    *<ul>SAMPLE TEST TO DEMONSTRATE SUCCESS 
    *<li><B>Info:</B> Perform actions on add a top level folder in Sync View by click title menu</li>
    *<li><B>Step:</B> Create a folder</li>
    *<li><B>Step:</B> click into folder</li>
    *<li><B>Step:</B> click title menu</li>
    *<li><B>Step:</B> click Add to Sync Button</li>
    *<li><B>Verify:</B> Added successfully</li>
    *<li><B>Step:</B> click title menu</li>
    *<li><B>Step:</B>click Remove From Sync Button</li>
    *<li><B>Verify:</B> Removed successfully</li>
    *</ul>
    */
    
    @Test(groups = {"unit"})
    public void testAddFolderToSyncByTitleMenu() throws Exception {
        DefectLogger logger = dlog.get(Thread.currentThread().getId());
        String gk_flag = "FILES_FOLDER_SYNCABLE";
        if (gkc.getSetting(gk_flag)) {
            ui.startTest();
            
            BaseFolder folder = new BaseFolder.Builder("folder A"
                    + Helper.genDateBasedRand()).build();
            
            // Click on My folders
            clickMyFoldersView();
            //Create a new folder
            logger.strongStep("Create a new folder");
            log.info("INFO: Create a new folder");
            folder.create(ui, false);
            
            //Click on display list to show folders in list
            logger.strongStep("Click on 'Display List'");
            log.info("INFO: Click on Display List ");
            ui.clickLinkWait(FilesUIConstants.DisplayList);
            
            logger.strongStep("click folder A");
            log.info("INFO: click folder A");
            ui.clickLinkWait(FilesUI.selectMyFolder(folder));
            
            log.info("INFO: Click folder's title dropdown menu");
            ui.clickLinkWait(FilesUIConstants.FolderTitleDropdownMenu);
            
            log.info("INFO: Click Add to Sync Button");
            ui.clickLinkWait(AddToMyDriveTitleMenu);
            
            logger.strongStep("verify if folder A was added to Sync successfully.");
            log.info("INFO: Click folder's title dropdown menu");
            ui.clickLinkWait(FilesUIConstants.FolderTitleDropdownMenu);
            
            // Check if the "Remove From Sync" button shows and "Add to Sync" button disappears, if yes, added successfully.
            Assert.assertTrue(
                    driver.isElementPresent(RemoveFromMyDriveTitleMenu),
                    "ERROR: Folder A is not added to Sync Successfully");
            Assert.assertFalse(
                    driver.isElementPresent(AddToMyDriveTitleMenu),
                    "ERROR: Folder A is not added to Sync Successfully");
            
            log.info("INFO: Verify folder A was added to Sync successfully.");
            
            
            // Remove Folder A from Sync.
            logger.strongStep("Remove Folder A from Sync.");
            ui.clickLinkWait(RemoveFromMyDriveTitleMenu);
            
            logger.strongStep("verify if folder A was removed from Sync successfully.");
            ui.clickLinkWait(FilesUIConstants.FolderTitleDropdownMenu);
            
            // Check if the "Remove From Sync" button disappears and "Add to Sync" button shows, if yes, removed successfully.
            Assert.assertFalse(
                    driver.isElementPresent(RemoveFromMyDriveTitleMenu),
                    "ERROR: Folder A is not removed from Sync Successfully");
            Assert.assertTrue(
                    driver.isElementPresent(AddToMyDriveTitleMenu),
                    "ERROR: Folder A is not removed from Sync Successfully");
            
            log.info("INFO: Remove folder A was removed from Sync successfully.");
            
            ui.endTest();
        }
    }
    
    /**
        *<ul>SAMPLE TEST TO DEMONSTRATE SUCCESS 
        *<li><B>Info:</B> Perform actions on add a file to Sync View by picker</li>
        *<li><B>Step:</B> upload a file via the API</li>
        *<li><B>Step:</B> click "Add To..." button</li>
        *<li><B>Step:</B> select My Drive menu</li>
        *<li><B>Step:</B> click "Add Here" button</li>
        *<li><B>Step:</B> go to My drive view</li>
        *<li><B>Verify:</B> verify if the file exists</li>
        *</ul>
    */
    @Test(groups = {"unit"})
    public void testAddFilesToSyncByPicker() throws Exception {
         DefectLogger logger=dlog.get(Thread.currentThread().getId());
         String gk_flag = "FILES_FOLDER_SYNCABLE";
         if (gkc.getSetting(gk_flag)) {
             ui.startTest();

             BaseFile file1 = new BaseFile.Builder(Data.getData().file1)
                             .extension(".jpg")
                             .rename(Helper.genDateBasedRand())
                             .build();
             
         	// upload a file via API
             logger.strongStep("Upload a file via the API");
         	 uiViewer.upload(file1, testConfig, testUser);
         	 file1.setName(file1.getRename()+ file1.getExtension());
             
             //Click on My Files and click on display list to show files in list
             logger.strongStep("Click on My Files");
             clickMyFilesView();
             logger.strongStep("Click on 'Display List'");
             log.info("INFO: Click on Display List ");
             ui.clickLinkWait(FilesUIConstants.DisplayList);
             
             logger.strongStep("Select the file by check box");
             log.info("INFO: Select the file by check box");
             selectItemsByCheckBox(file1.getName());
             
             logger.strongStep("click Add To button");
             log.info("INFO: click Add To button");
             ui.clickLinkWait(BulkAddToCollection);
             ui.selectComboValue(FilesUIConstants.pickerMenu, "My Drive");
             ui.clickButton("Add Here");
             
             logger.strongStep("Go to My Drive");
             log.info("INFO: Go to My Drive");
             ui.clickLinkWait(SyncView);
             log.info("INFO: Click on Display List ");
             ui.clickLinkWait(FilesUIConstants.DisplayList);
             
             logger.strongStep("Verify if file exist");
             log.info("INFO: Verify if file exist");
             
             Assert.assertTrue(
                 driver.isElementPresent(FilesUI.selectFile(file1)),
                 "ERROR: The file is not added to My Drive Successfully");
             
             ui.endTest();
         }
    }
    
    //@Test(groups = {"unit"})
    public void testAddFilesToSyncByDragAndDrop() throws Exception {
        DefectLogger logger=dlog.get(Thread.currentThread().getId());
        String gk_flag = "FILES_FOLDER_SYNCABLE";
        if (gkc.getSetting(gk_flag)) {
            ui.startTest();
            
            BaseFile file = new BaseFile.Builder(Data.getData().file1)
                            .extension(".jpg")
                            .rename(Helper.genDateBasedRand())
                            .build();
            
         	// upload a file via API
            logger.strongStep("Upload a file via the API");
            uiViewer.upload(file, testConfig, testUser);
            file.setName(file.getRename()+ file.getExtension());
            
            //Click on My Files and display list to show files in list
            logger.strongStep("Click on My Files");
            log.info("INFO: Click on My Files");
            ui.clickLinkWait(FilesUIConstants.openMyFilesView);
            logger.strongStep("Click on 'Display List'");
            log.info("INFO: Click on Display List ");
            ui.clickLinkWait(FilesUIConstants.DisplayList);
            
            String sourceSelector = FilesUI.selectFile(file);
            Element source = ui.getFirstVisibleElement(sourceSelector);
            
            String targetSelector = "css=div[dndtype='folder'][dndelementtitle='My Drive']";
            Element target = ui.getFirstVisibleElement(targetSelector);
            
            DragAndDrop(source, target);
            
            logger.strongStep("Go to My Drive");
            log.info("INFO: Go to My Drive");
            ui.clickLinkWait(SyncView);
            log.info("INFO: Click on Display List ");
            ui.clickLinkWait(FilesUIConstants.DisplayList);
            
            logger.strongStep("Verify if file exist");
            log.info("INFO: Verify if file exist");
            
            Assert.assertTrue(
                driver.isElementPresent(FilesUI.selectFile(file)),
                "ERROR: The file is not added to My Drive Successfully");
            
            ui.endTest();
        }
    }
    
    //@Test(groups = {"unit"})
    public void testAddFoldersToSyncByDragAndDrop() throws Exception {
        DefectLogger logger=dlog.get(Thread.currentThread().getId());
        String gk_flag = "FILES_FOLDER_SYNCABLE";
        if (gkc.getSetting(gk_flag)) {
            ui.startTest();
            BaseFolder folder = new BaseFolder.Builder("folder A"
                + Helper.genDateBasedRand()).build();
        
            //Create a new folder
            logger.strongStep("Create a new folder");
            log.info("INFO: Create a new folder");
            folder.create(ui, false);
            
            // Click on My Folders
            logger.strongStep("Click on My Folders");
            log.info("INFO: Click on My Folders");
            ui.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
            
            //Click on display list to show folders in list
            logger.strongStep("Click on 'Display List'");
            log.info("INFO: Click on Display List ");
            ui.clickLinkWait(FilesUIConstants.DisplayList);
            
            String sourceSelector = FilesUI.selectOneFolder(folder);
            Element source = ui.getFirstVisibleElement(sourceSelector);
            
            String targetSelector = "css=div[dndtype='folder'][dndelementtitle='My Drive']";
            Element target = ui.getFirstVisibleElement(targetSelector);
            
            DragAndDrop(source, target);
            
            logger.strongStep("Go to My Drive");
            log.info("INFO: Go to My Drive");
            ui.clickLinkWait(SyncView);
            log.info("INFO: Click on Display List ");
            ui.clickLinkWait(FilesUIConstants.DisplayList);
            
            logger.strongStep("Verify if folder exist");
            log.info("INFO: Verify if folder exist");
            
            Assert.assertTrue(
                driver.isElementPresent(FilesUI.selectOneFolder(folder)),
                "ERROR: The folder is not added to My Drive Successfully");
            
            ui.endTest();
        }
    }
    
    @Test(groups = {"unit"})
    public void testGoToFolderByMyDriveIndicator() throws Exception {
        DefectLogger logger=dlog.get(Thread.currentThread().getId());
        String gk_flag = "FILES_FOLDER_SYNCABLE";
        if (gkc.getSetting(gk_flag)) {
            ui.startTest();
            BaseFolder folder = new BaseFolder.Builder("folder A"
                + Helper.genDateBasedRand()).build();
            
            BaseFile file = new BaseFile.Builder(Data.getData().file1)
            .extension(".jpg")
            .rename(Helper.genDateBasedRand())
            .build();
            
            // upload a file via API
            logger.strongStep("Upload a file via the API");
            uiViewer.upload(file, testConfig, testUser);
            file.setName(file.getRename()+ file.getExtension());
            clickMyFilesView();
            
            //Create a new folder
            logger.strongStep("Create a new folder");
            log.info("INFO: Create a new folder");
            folder.create(ui, false);
            
            file.addToFolder(ui, folder);
            
            clickMyFoldersView();
            ui.clickLinkWait(FilesUIConstants.DisplayList);
            
            // click more link
            logger.strongStep("click more link.");
            ui.selectFolderMoreLink(folder);
            
            // click "add to Sync" button in more link.
            logger.strongStep("Add Folder A to Sync.");
            ui.clickLinkWait(AddToMyDriveMoreLink);
            
            logger.strongStep("Click on My Files");
            log.info("INFO: Click on My Files");
            ui.clickLinkWait(FilesUIConstants.openMyFilesView);
            
            clickMoreButton(file.getName());
            ui.clickLinkWait(MyDriveIndicator);
            ui.clickLinkWait(MyDriveIndicatorItem);
            
            Assert.assertTrue(
                driver.isElementPresent(FilesUI.selectFile(file)),
                "ERROR: The file is not in the folder");
            ui.endTest();
        }
    }
    
    
	/**
	*<ul>Show 'In My Drive' for synced folder 
	*<li><B>Info:</B> Go to My Folders View, create new folder</li>
	*<li><B>Info:</B> Add the folder to sync</li>
	*<li><B>Info:</B> Go to this root folder</li>
	*<li><B>Verify:</B> 'In My Drive' shows after the folder name in title</li>
	*<li><B>Info:</B> Create sub folder in this folder</li>
	*<li><B>Info:</B> Go to the sub folder</li>
	*<li><B>Verify:</B> 'In My Drive' shows after the sub folder name in title</li>
	*<li><B>Info:</B> Go to My Folders View, create new folder</li>
	*<li><B>Info:</B> Go to this new folder</li>
	*<li><B>Verify:</B> No 'In My Drive' shows after the folder name in title</li>
	*<li><B>Info:</B> Create sub folder and go this sub folder</li>
	*<li><B>Verify:</B> No 'In My Drive' shows after the sub folder name in title</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testInMyDriveIndicatorForPersonalFolder() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();
			
			BaseFolder folder1 = new BaseFolder.Builder("folder A"
					+ Helper.genDateBasedRand()).build();
			BaseFolder folder1_subFolder = new BaseFolder.Builder("folder A_Sub Folder"
					+ Helper.genDateBasedRand()).build();
			BaseFolder folder2 = new BaseFolder.Builder("folder B"
					+ Helper.genDateBasedRand()).build();
			BaseFolder folder2_subFolder = new BaseFolder.Builder("folder B_Sub Folder"
					+ Helper.genDateBasedRand()).build();
			
			//Go to My Folders view
			logger.strongStep("Click on My Folders");
			log.info("INFO: Click on My Folders");
            clickMyFoldersView();
            
            //Create a new folder
            logger.strongStep("Create a new folder");
            log.info("INFO: Create a new folder");
            folder1.create(ui, false);
            
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			// click more link
			logger.strongStep("click more link.");
			log.info("INFO: click more link");
			ui.selectFolderMoreLink(folder1);
						
			// click "add to Sync" button in more link.			
			logger.strongStep("Add Folder A to Sync");
			ui.clickLinkWait(AddToMyDriveMoreLink);
			
			//Go to folder1
			logger.strongStep("Go to Folder A");
			log.info("INFO: Go to Folder A");
			ui.clickLinkWait(FilesUI.selectMyFolder(folder1));
			
			//verify 'In My Drive' shows
			Assert.assertTrue(driver.isTextPresent(InMyDriveText),
					"ERROR: Unable to find text " + InMyDriveText + " for folder: " + folder1.getName());
			
			//Create sub folder
			logger.strongStep("Create Folder A's sub folder");
			log.info("INFO: Create Folder A's sub folder");
			Files_Folder_Dropdown_Menu.NEW_FOLDER.select(ui);
			ui.createSubFolder(folder1_subFolder);
			
			//Go to sub folder
			logger.strongStep("Go to Folder A's sub folder");
			ui.clickLinkWait(FilesUI.selectMyFolder(folder1_subFolder));
			
			//verify 'In My Drive' shows
			Assert.assertTrue(driver.isTextPresent(InMyDriveText),
					"ERROR: Unable to find text " + InMyDriveText + " for folder: " + folder1_subFolder.getName());
			
			//Go to My Folders
			logger.strongStep("Go to My Folders view");
			log.info("INFO: Click on My Folders");
			clickMyFoldersView();
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			//Create new folder B
			logger.strongStep("Create new folder B");
			log.info("INFO: Create new folder B");
			folder2.create(ui, false);
			
			//Go to folder B
			log.info("INFO: Go to folder B");
			ui.clickLinkWait(FilesUI.selectMyFolder(folder2));
			
			//verify no 'In My Drive' show
			Assert.assertFalse(driver.isTextPresent(InMyDriveText),
					"ERROR: Find text " + InMyDriveText + " for folder: " + folder2.getName());
			
			//Create sub folder for folder B
			log.info("Create sub folder for folder B");
			Files_Folder_Dropdown_Menu.NEW_FOLDER.select(ui);
			ui.createSubFolder(folder2_subFolder);
			
			//Go to folder B's sub folder
			log.info("INFO: Go to folder B's sub folder");
			ui.clickLinkWait(FilesUI.selectMyFolder(folder2_subFolder));
			
			//verify no 'In My Drive' show
			Assert.assertFalse(driver.isTextPresent(InMyDriveText),
					"ERROR: Find text " + InMyDriveText + " for folder: " + folder2_subFolder.getName());
			
			ui.endTest();
		}
	}
	
	/**
	*<ul>Show 'In My Drive' for synced folder 
	*<li><B>Info:</B> Go to My Folders View, create new folder</li>
	*<li><B>Info:</B> Add the folder to sync</li>
	*<li><B>Info:</B> Share the folder to community</li>
	*<li><B>Info:</B> Go to community folders, then go to the community shared folder</li>
	*<li><B>Verify:</B> 'In My Drive' shows for community shared folder</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testInMyDriveIndicatorForCommunitySharedFolder() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		String gk_flag_card = "CATALOG_CARD_VIEW";
		if (gkc.getSetting(gk_flag)) {

			String testName = ui.startTest();
			User testUser = cfg.getUserAllocator().getUser();
			serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			comUI = CommunitiesUI.getGui(cfg.getProductName(), driver);	
			
			BaseFolder folder = new BaseFolder.Builder("folder"
					+ Helper.genDateBasedRand()).build();
			
			APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													   .description("Test description for testcase " + testName)
													   .build();
	
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
			ui.logout();
			//driver.close();
			
			//load Files components
			ui.loadComponent(Data.getData().ComponentFiles,true);
			//ui.replaceProductionCookies();
			ui.login(testUser);
			
			//Create a new folder in My Drive view (default)
			logger.strongStep("Create a new folder");
			log.info("INFO: Create a new folder");
			folder.create(ui, false);
			
			//click on my drive
			ui.clickLinkWait(FilesUIConstants.MyDriveLeftMenu);
			
			//change display list
			ui.fluentWaitElementVisible(FilesUIConstants.DisplayList);
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			// click more link
			logger.strongStep("click more link.");
			log.info("INFO: click more link");
			ui.selectFolderMoreLink(folder);
			
			//click share button
			ui.clickLinkWait(ShareButtonInDetailsMenu);
			log.info("INFO: Select Share with community from drop down");
			//click 'A Community' option
			ui.clickLink(ShareCommunityOption);
			//String communityName = community.getName();
			ui.typeText(searchPeopleField, community.getName());
			
			//select searched result
			ui.clickLink(searchCommunityResult);
			//Share
			ui.clickButton("Share");
			//verify the UI message
			ui.isTextPresent("The folder was shared successfully.");
			ui.logout();
			//driver.close();
			
			//load community component
			ui.loadComponent(Data.getData().ComponentCommunities,true);
			ui.login(testUser);
			
			// Navigate to owned communities
			
			// check GK setting
			boolean isCardView = gkc.getSetting(gk_flag_card);

			logger.strongStep("Navigate to owned community views");
			log.info("INFO: Navigate to the owned communtiy views");
			comUI.goToDefaultIamOwnerView(isCardView);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(comUI);

			// Select Files from left menu
			logger.strongStep("Select files from the left navigation menu");
			log.info("INFO: Select Files from left navigation menu");
			Community_LeftNav_Menu.FILES.select(comUI);
			
			//go to folder shared with community
			ui.clickLinkWait(FilesUIConstants.navCommunityFolders);
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			ui.clickLinkWait(FilesUI.selectMyFolder(folder));
			
			//verify no 'In My Drive' show
			Assert.assertTrue(driver.isTextPresent(InMyDriveText),
					"ERROR: Cannot find text " + InMyDriveText + " for folder: " + folder.getName());
			
			ui.endTest();
			
		} 
		
	}
	
   @Test(groups = {"unit"})
    public void testMultiSelectMoveButtonInMyDrive() throws Exception {
        String gk_flag = "FILES_FOLDER_SYNCABLE";
        DefectLogger logger = dlog.get(Thread.currentThread().getId());
        if (gkc.getSetting(gk_flag)) {
            ui.startTest();
            BaseFolder folder = new BaseFolder.Builder("folder A"
                + Helper.genDateBasedRand()).build();
            
            BaseFile file = new BaseFile.Builder(Data.getData().file1)
            .extension(".jpg")
            .rename(Helper.genDateBasedRand())
            .build();
            
            //upload a new file
            logger.strongStep("Upload the file");
            ui.clickUploadButtonInGlobalNewButton(false);
            ui.fileToUpload(file.getName(), BaseUIConstants.FileInputField);
            //Upload the file
            ui.fluentWaitPresent(FilesUIConstants.fileUpload);
            ui.clickLinkWithJavascript(FilesUIConstants.fileUpload);
                
            //Create a new folder
            logger.strongStep("Create a new folder");
            folder.create(ui, false);
            
            //click on my drive
	    ui.clickLinkWait(FilesUIConstants.MyDriveLeftMenu);
            
            //Click on display list to show files in list
            logger.strongStep("Click on 'Display List'");
            ui.clickLinkWait(FilesUIConstants.DisplayList);
            logger.strongStep("Select the file by check box");
            selectItemsByCheckBox(file.getName());
            
            if(ui.isElementPresent(BulkMoveToCollection)) {
                logger.strongStep("click Move To button");
                ui.clickLinkWait(BulkMoveToCollection);
                ui.selectComboValue(FilesUIConstants.pickerMenu, "My Drive");
                ui.clickFolderItemInFilePicker(folder.getName(), true);
                ui.clickButton("Move Here");
                logger.strongStep("click folder");
                ui.clickLinkWait(FilesUI.selectMyFolder(folder));
                Assert.assertTrue(
                    driver.isElementPresent(FilesUI.selectFile(file)),
                    "ERROR: The file was not moved to Target Folder");
            }
            ui.endTest();
        }
    }
   
    /**
    *<ul> can upload a file to My Drive root although a folder in My Drive root also includes a file with the same name
    *<li><B>Info:</B> Go to My Drive View</li>
    *<li><B>Info:</B> create a folder</li>
    *<li><B>Info:</B> upload a file into the folder</li>
    *<li><B>Info:</B> go to my drive view</li>
    *<li><B>Info</B> upload a file with the same name</li>
    *<li><B>Verify</B> upload successfully </li>
    *</ul>
    */
    @Test(groups = {"unit"})
    public void testUploadSameNameFileInMyDrive() throws Exception{
       String gk_flag = "FILES_FOLDER_SYNCABLE";
       DefectLogger logger = dlog.get(Thread.currentThread().getId());
       if (gkc.getSetting(gk_flag)) {
           ui.startTest();
           BaseFolder folder = new BaseFolder.Builder("folder A"
               + Helper.genDateBasedRand()).build();
           
           BaseFile file = new BaseFile.Builder(Data.getData().file1)
           .extension(".jpg")
           .rename(Helper.genDateBasedRand())
           .build();
           
           //Create a new folder
           logger.strongStep("Create a new folder");
           folder.create(ui, false);
           
           //click on my drive
           ui.clickLinkWait(FilesUIConstants.MyDriveLeftMenu);
           
           //Click on display list to show files in list
           logger.strongStep("Click on 'Display List'");
           ui.clickLinkWait(FilesUIConstants.DisplayList);
           
           // click into the folder
           ui.clickLinkWait(FilesUI.selectMyFolder(folder));
           
           //upload a new file
           logger.strongStep("Upload the file");
           ui.clickUploadButtonInGlobalNewButton(false);
           ui.fileToUpload(file.getName(), BaseUIConstants.FileInputField);
           //Upload the file
           ui.fluentWaitPresent(FilesUIConstants.fileUpload);
           ui.clickLinkWithJavascript(FilesUIConstants.fileUpload);
           
           //go back My Drive view
           logger.strongStep("go back My Drive View");
           ui.clickLinkWait(SyncView);
           
           //Upload the file again
           logger.strongStep("Upload the file with the same name");
           ui.clickUploadButtonInGlobalNewButton(false);
           ui.fileToUpload(file.getName(), BaseUIConstants.FileInputField);
           ui.fluentWaitPresent(FilesUIConstants.fileUpload);
           ui.clickLinkWithJavascript(FilesUIConstants.fileUpload);
           
           
           //Validate message displays stating file was successfully uploaded
           log.info("INFO: Verify message displays stating file was successfully uploaded");
           Assert.assertTrue(ui.fluentWaitTextPresent("Successfully uploaded " + file.getName()),
                     "ERROR: File was not uploaded");
           
           ui.endTest();
       }
    }
}


