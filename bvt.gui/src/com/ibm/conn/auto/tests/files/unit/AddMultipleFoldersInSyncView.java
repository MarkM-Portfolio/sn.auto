package com.ibm.conn.auto.tests.files.unit;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.webui.FilesUI;

public class AddMultipleFoldersInSyncView extends FilesUnitBaseSetUp{
	private static Logger log = LoggerFactory.getLogger(AddMultipleFoldersInSyncView.class);
	
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
	*<li><B>Step:</B> Create a folder in My folder</li>
	*<li><B>Step:</B> go to Sync View</li>
	*<li><B>Step:</B> click Add Folders link in Sync banner</li>
	*<li><B>Step:</B> Add folders to Sync</li>
	*<li><B>Verify:</B>  Folders are in Sync View</li>
	*</ul>
	*/
	
	@Test(groups = {"unit", "bvt"})
	public void testAddFolderInSyncViewBySyncBanner() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();
			
			BaseFolder folderA = new BaseFolder.Builder("folder A"
					+ Helper.genDateBasedRand()).build();
			
			BaseFolder folderB = new BaseFolder.Builder("folder B"
					+ Helper.genDateBasedRand()).build();
			
			clickMyFoldersView();
			//Create new folders
			logger.strongStep("Create a folderA");
			log.info("INFO: Create a folderA");
			folderA.create(ui, false);
			
			logger.strongStep("Create a folderB");
			log.info("INFO: Create a folderB");
			folderB.create(ui, false);
			
			// Click on Sync View
			logger.strongStep("Click on Sync");
			log.info("INFO: Click on Sync");
			ui.clickLinkWait(SyncView);
			
			// Verify sync banner exist.
			logger.strongStep("Verify sync banner exist.");
			log.info("INFO: Verify sync banner exist.");
			
			if(driver.isElementPresent(SyncBanner)) {
				// click add folders link
				logger.strongStep("click addfolders link.");
				ui.clickLink(AddFoldersLink);
				
				//select My Folders menu in picker
				logger.strongStep("Select 'My Folders' option from the dropdown");
				ui.selectComboValue(FilesUIConstants.pickerMenu, "My Folders");

				//choose folderA and folderB in picker
				logger.strongStep("choose folderA and folderB in picker");
				ui.clickLinkWait(selectFolderinPicker(folderA.getName()));
				ui.clickLinkWait(selectFolderinPicker(folderB.getName()));
				
				//click OK button
				ui.clickOKButton();
				
				ui.clickLinkWait(FilesUIConstants.DisplayList);
				// Verify folder A and folder B exist.
				logger.strongStep("Verify folder A exist.");
				log.info("INFO: if Verify folder A exist.");
				Assert.assertTrue(
						driver.isElementPresent(FilesUI.selectOneFolder(folderA)),
						"ERROR: Folder A is not found ");
				log.info("INFO: Verify folder A was added to Sync successfully.");
				
				logger.strongStep("Verify folder B exist.");
				log.info("INFO: Verify if folder B exist.");
				Assert.assertTrue(
						driver.isElementPresent(FilesUI.selectOneFolder(folderB)),
						"ERROR: Folder B is not found ");
				log.info("INFO: Verify folder B was added to Sync successfully.");
			}

			ui.endTest();
		}
	}
	
	@Test(groups = {"unit", "bvt"})
	public void testAddFolderInSyncView() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();
			
			BaseFolder folderA = new BaseFolder.Builder("folder A"
					+ Helper.genDateBasedRand()).build();
			
			BaseFolder folderB = new BaseFolder.Builder("folder B"
					+ Helper.genDateBasedRand()).build();
			
			BaseFolder folderC = new BaseFolder.Builder("folder C"
					+ Helper.genDateBasedRand()).build();
			
			clickMyFoldersView();
			//Create new folders
			logger.strongStep("Create a folderA");
			log.info("INFO: Create a folderA");
			folderA.create(ui, false);
			
			logger.strongStep("Create a folderB");
			log.info("INFO: Create a folderB");
			folderB.create(ui, false);
			
			logger.strongStep("Create a folderC");
			log.info("INFO: Create a folderC");
			folderC.create(ui, false);
			
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			// click more link
			logger.strongStep("click more link.");
			clickMoreButton(folderC.getName());
			
			// click "add to Sync" button in more link.			
			logger.strongStep("Add Folder C to Sync.");
			ui.clickLinkWait(AddToMyDriveMoreLink);
			
			// Click on Sync View
			logger.strongStep("Click on Sync");
			log.info("INFO: Click on Sync");
			ui.clickLinkWait(SyncView);

			// click addfolders button
			logger.strongStep("click addfolders button.");
			ui.clickLink(BulkAddFoldersToSync);
			
			ui.selectComboValue(FilesUIConstants.pickerMenu, "My Folders");
			
			//choose folderA and folderB in picker
			logger.strongStep("choose folderA and folderB in picker");
			ui.clickLinkWait(selectFolderinPicker(folderA.getName()));
			ui.clickLinkWait(selectFolderinPicker(folderB.getName()));
			
			//click OK button
			ui.clickOKButton();
			
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			// Verify folder A and folder B exist.
			logger.strongStep("Verify folder A exist.");
			log.info("INFO: if Verify folder A exist.");
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectOneFolder(folderA)),
					"ERROR: Folder A is not found ");
			log.info("INFO: Verify folder A was added to Sync successfully.");
			
			logger.strongStep("Verify folder B exist.");
			log.info("INFO: Verify if folder B exist.");
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectOneFolder(folderB)),
					"ERROR: Folder B is not found ");
			log.info("INFO: Verify folder B was added to Sync successfully.");

			ui.endTest();
		}
	}
	
	@Test(groups = {"unit", "bvt"})
	public void testRemoveMulitpleFoldersInSyncView() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();
			
			BaseFolder folderA = new BaseFolder.Builder("folder A"
					+ Helper.genDateBasedRand()).build();
			
			BaseFolder folderB = new BaseFolder.Builder("folder B"
					+ Helper.genDateBasedRand()).build();
			
			clickMyFoldersView();
			//Create new folders
			logger.strongStep("Create a folderA");
			log.info("INFO: Create a folderA");
			folderA.create(ui, false);
			
			logger.strongStep("Create a folderB");
			log.info("INFO: Create a folderB");
			folderB.create(ui, false);
			
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			// click more link
			logger.strongStep("click more link.");
			clickMoreButton(folderA.getName());
			
			// click "add to Sync" button in more link.			
			logger.strongStep("Add Folder A to Sync.");
			ui.clickLinkWait(AddToMyDriveMoreLink);
			
			// click more link
			logger.strongStep("click more link.");
			clickMoreButton(folderB.getName());
			
			// click "add to Sync" button in more link.			
			logger.strongStep("Add Folder B to Sync.");
			ui.clickLinkWait(AddToMyDriveMoreLink);
			
			// Click on Sync View
			logger.strongStep("Click on Sync");
			log.info("INFO: Click on Sync");
			ui.clickLinkWait(SyncView);
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			// Select folder by checkbox
			ui.selectFolderCheckmark(folderA);
			ui.selectFolderCheckmark(folderB);
			
			ui.clickLinkWait(BulkrRmoveFromSync);
			ui.clickOKButton();
			
			ui.endTest();
		}
	}
	
}
