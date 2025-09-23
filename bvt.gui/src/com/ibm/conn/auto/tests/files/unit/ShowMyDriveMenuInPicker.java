package com.ibm.conn.auto.tests.files.unit;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;

public class ShowMyDriveMenuInPicker extends FilesUnitBaseSetUp{
private static Logger log = LoggerFactory.getLogger(ShowMyDriveMenuInPicker.class);
	
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
	*<li><B>Step:</B> Create a folder in My folder</li>
	*<li><B>Step:</B> Go to My folder View</li>
	*<li><B>Step:</B> Click more button</li>
	*<li><B>Step:</B> Click move to button</li>
	*<li><B>Verify:</B>  Folder picker has My Drive menu</li>
	*</ul>
	*/
	@Test(groups = {"unit", "bvt", "unit-sync"})
	public void testShowMyDriveMenuInFolderPicker() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
            ui.startTest();
			
			BaseFolder folderA = new BaseFolder.Builder("folder A"
					+ Helper.genDateBasedRand()).build();
			
			// Click on My folders
            clickMyFoldersView();
			//Create new folders
			logger.strongStep("Create a folderA");
			log.info("INFO: Create a folderA");
			folderA.create(ui, false);
			
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			// click more link
			logger.strongStep("click more link.");
			ui.selectFolderMoreLink(folderA);
			
			// click "Move to..." button in more link.			
			logger.strongStep("click move to... button.");
			ui.clickLinkWait(MoveToMoreLink);
			
			//select "My Drive" Menu
			selectMenuInPicker("myDrive");
			
			ui.endTest();
		}
	}
	
	/**
	*<ul>SAMPLE TEST TO DEMONSTRATE SUCCESS 
	*<li><B>Step:</B> Create a folder in My folder</li>
	*<li><B>Step:</B> Go to My folder View</li>
	*<li><B>Step:</B> Click more button</li>
	*<li><B>Step:</B> Click Add Files button</li>
	*<li><B>Verify:</B>  File picker has My Drive menu</li>
	*</ul>
	*/
	@Test(groups = {"unit", "bvt", "unit-sync"})
	public void testShowMyDriveMenuInFilePicker() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
            ui.startTest();
			
			BaseFolder folderA = new BaseFolder.Builder("folder A"
					+ Helper.genDateBasedRand()).build();
			
			// Click on My folders
            clickMyFoldersView();
			//Create new folders
			logger.strongStep("Create a folderA");
			log.info("INFO: Create a folderA");
			folderA.create(ui, false);
			
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			// click more link
			logger.strongStep("click more link.");
			ui.selectFolderMoreLink(folderA);
			
			// click "Add Files..." button in more link.			
			logger.strongStep("click Add Files... button.");
			ui.clickLinkWait(AddFilesMoreLink);
			
			//select "My Drive" Menu
			selectMenuInPicker("myDrive");
			
			ui.endTest();
		}
	}
	
}
