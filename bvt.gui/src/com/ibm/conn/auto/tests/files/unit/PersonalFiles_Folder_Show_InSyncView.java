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

public class PersonalFiles_Folder_Show_InSyncView extends FilesUnitBaseSetUp{
	private static Logger log = LoggerFactory.getLogger(PersonalFiles_Folder_Show_InSyncView.class);

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
	*<li><B>Step:</B> click Sync menu</li>
	*<li><B>Verify:</B> folder exist</li>
	*<li><B>Step:</B>click Remove From Sync Button</li>
	*<li><B>Verify:</B> folder not exist</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testShowFolderInSyncViewByMoreLink() throws Exception {
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
			
			// click more link
			logger.strongStep("click more link.");
			ui.selectFolderMoreLink(folder);
			
			// click "add to Sync" button in more link.			
			logger.strongStep("Add Folder A to Sync.");
			ui.clickLinkWait(AddToMyDriveMoreLink);
			
			// click sync view
			ui.clickLinkWait(SyncView);
			
			// Verify folder A exist.
			//logger.strongStep("Verify folder A exist.");
			//log.info("INFO: Verify folder A exist.");` 
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectOneFolder(folder)),
					"ERROR: Folder A is not found ");
			log.info("INFO: Verify folder A was added to Sync successfully.");
			
			//remove from sync,folder A disappears
			logger.strongStep("click more link.");
			logger.strongStep("Remove Folder A from Sync.");
			ui.selectFolderMoreLink(folder);
			ui.clickLinkWait(RemoveFromMyDriveMoreLink);
			logger.strongStep("verify if folder A was removed from Sync successfully.");
			Assert.assertFalse(
					driver.isElementPresent(FilesUI.selectOneFolder(folder)),
					"ERROR: Folder A is not removed ");
			log.info("INFO: Remove folder A was removed from Sync successfully.");
			
			ui.endTest();
		}
	}
	
	
	/**
	*<ul>Create Folder in Sync view 
	*<li><B>Info:</B> Go to Sync View, create new folder</li>
	*<li><B>Verify:</B> folder exist in Sync view</li>
	*<li><B>Verify:</B> folder exist in My Folders</li>
	*<li><B>Info:</B> Go to My Folders View, create new folder</li>
	*<li><B>Verify:</B> folder doesn't exist in Sync view</li>
	*<li><B>Verify:</B> folder exist in My Folders</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testCreateFolderInSyncView() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();
			
			BaseFolder folder1 = new BaseFolder.Builder("folder A"
					+ Helper.genDateBasedRand()).build();
			BaseFolder folder2 = new BaseFolder.Builder("folder B"
					+ Helper.genDateBasedRand()).build();
			
			//go to sync view
			ui.clickLinkWait(SyncView);
			
			//Create a new folder
			logger.strongStep("Create a new folder");
			log.info("INFO: Create a new folder");
			folder1.create(ui, false);
			
			//change to list view
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			//verify folder1 add to sync view successfully
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectOneFolder(folder1)),
					"ERROR: Folder A is not found ");
			log.info("INFO: Verify folder A was added to Sync successfully.");

			// Click on My folders
            clickMyFoldersView();
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			//verify folder1 add to My Folders view successfully
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectOneFolder(folder1)),
					"ERROR: Folder A is not found ");
			log.info("INFO: Verify folder A was added to Sync successfully.");
			
			//In My Folders view, create new folder
			logger.strongStep("Create a new folder");
			log.info("INFO: Create a new folder");
			folder2.create(ui, false);
			
			//Verify folder2 shows in My Folders view successfully
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectOneFolder(folder2)),
					"ERROR: Folder A is not found ");
			log.info("INFO: Verify folder A was added to Sync successfully.");
			
			//Go to Sync view
			ui.clickLinkWait(SyncView);
			
			//change to list view
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			//Verify folder2 doesn't show in Sync view
			Assert.assertFalse(
					driver.isElementPresent(FilesUI.selectOneFolder(folder2)),
					"ERROR: Folder A is found ");
			log.info("INFO: Verify folder A was NOT add to Sync because it's created under My Folders.");
			
			
			ui.endTest();
		}
	}
	
}
