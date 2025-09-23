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

public class PersonalFiles_Remove_CustomMode_InSyncView extends FilesUnitBaseSetUp{
	private static Logger log = LoggerFactory.getLogger(PersonalFiles_Remove_CustomMode_InSyncView.class);

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
	*<li><B>Verify:</B> Custom Mode not exist</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testRemoveCustomModeInSyncViewByMoreLink() throws Exception {
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
			
			// Verify CustomView not exist.
			//logger.strongStep("Verify CustomView not exist.");
			//log.info("INFO: Verify CustomView not exist.");` 
			
			logger.strongStep("Verify CustomView not exist.");
			Assert.assertFalse(
					driver.isElementPresent(CustomView),
					"ERROR: CustomView exist ");
			log.info("INFO: CustomView not exist.");
			
			ui.endTest();
		}
	}
	
	
	/**
	*<ul>Create Folder in Sync view 
	*<li><B>Info:</B> Go to Sync View, create new folder</li>
	*<li><B>Verify:</B> folder exist in Sync view</li>
	*<li><B>Verify:</B> Custom Mode not exist</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testRemoveCustomModeByCreateFolderInSyncView() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();
			
			BaseFolder folder1 = new BaseFolder.Builder("folder A"
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
			
			// Verify CustomView not exist.
			//logger.strongStep("Verify CustomView not exist.");
			//log.info("INFO: Verify CustomView not exist.");` 
			logger.strongStep("Verify CustomView not exist.");
			Assert.assertFalse(
					driver.isElementPresent(CustomView),
					"ERROR: CustomView exist ");
			log.info("INFO: CustomView not exist.");
			
			
			ui.endTest();
		}
	}
	
}
