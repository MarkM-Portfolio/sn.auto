package com.ibm.conn.auto.tests.files.unit;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.webui.FilesUI;

public class PersonaFiles_MyFolders extends FilesUnitBaseSetUp{
	
	private static Logger log = LoggerFactory.getLogger(PersonaFiles_MyFolders.class);
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass(){
		personalFilesSetUpClass();
	}
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		personalFilesSetUp();
	}	

    /**
	*<ul>
	*<li><B>Info:</B> Move a folder to another folder by clicking on Move button</li>
	*<li><B>Step:</B> Create folder A</li>
	*<li><B>Step:</B> Create folder B</li>
	*<li><B>Step:</B> Move folder B to folder A </li>
	*<li><B>Step:</B> Verify  folder B was moved to folder A</li>
	*<li><B>Step:</B> Move folder B back to My Folders</li>
	*<li><B>Verify:</B> Click to My Folders to verify folder B is exist</li>
	*<li><B>Verify:</B> Click to folder A to verify folder B is not exist</li>
	*</ul>
	*/
	@Test(groups = {"unit"})
	public void testFilesMoveFolderByMoveButton() throws Exception {
	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String gk_flag = "FILES_NESTED_FOLDER";
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();
	
			BaseFolder folderA = new BaseFolder.Builder("folderA"
					+ Helper.genDateBasedRand()).build();
	
			BaseFolder folderB = new BaseFolder.Builder("folderB"
					+ Helper.genDateBasedRand()).build();
	        
			// Click on My folders
			clickMyFoldersView();
			// create folder
			logger.strongStep("Create folder");
			log.info("INFO: Create folder");
			folderA.create(ui, false);
			folderB.create(ui, false);
			
			ui.clickLinkWait(FilesUIConstants.DisplayList);
	
			// Verify folder A and B exist.
			logger.strongStep("Verify folder A and B exist.");
			log.info("INFO: Verify folder A and B exist.");
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectMyFolder(folderA)),
					"ERROR: Folder A is not found ");
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectMyFolder(folderB)),
					"ERROR: Folder B is not found ");
			
			// Move folder B to folder A
			logger.strongStep("Move folder B to folder A");
			log.info("INFO: Move folder B to folder A");
			ui.selectFolderCheckmark(folderB);
			ui.clickLinkWait(FilesUIConstants.MoveToFolderButton);
			ui.selectComboValue(FilesUIConstants.pickerMenu, "My Folders");
			ui.moveFolder(folderA);
	
			// Verify Folder B was moved to the Folder A
			logger.strongStep("Verify folder B was moved to the folder A");
			log.info("INFO: Verify Folder B was moved to the Folder A");
			ui.clickLinkWait(FilesUI.selectOneFolder(folderA));
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectMyFolder(folderB)),
					"ERROR: Move folder B to the folder A was failed ");
	
			// Move folder B to My Drive Root
			logger.strongStep("Move folder B to My Drive");
			log.info("INFO: Move folder B to My Drive");
			ui.selectFolderCheckmark(folderB);
			ui.clickLinkWait(FilesUIConstants.MoveToFolderButton);
			ui.selectComboValue(FilesUIConstants.pickerMenu, "My Drive");
			ui.clickLinkWait(FilesUIConstants.DialogmovetoButton);
	
			// Verify Folder B was moved to My Folders
			logger.strongStep("Verify Folder B was moved to My Folders");
			log.info("INFO: Verify Folder B was moved to My Folders");
			ui.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectMyFolder(folderB)),
					"ERROR: Move folder B to My Folders was failed ");
	
			ui.endTest();
		}else{
	    	//  Skip this test case
			log.info("INFO: nested Folder is not enabled");
			throw new SkipException("Test move Folder Is Skipped");		
	    }
	   
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Move a folder to another folder by clicking on More Link</li>
	 * <li><B>Step:</B> Create folder A</li>
	 * <li><B>Step:</B> Create folder B</li>
	 * <li><B>Step:</B> Move folder B to folder A</li>
	 * <li><B>Verify:</B> Verify folder B was moved to folder A</li>
	 * <li><B>Step:</B> Move folder B back to My Folders</li>
	 * <li><B>Verify:</B> Click to My Folders to verify folder B is exist</li>
	 * <li><B>Verify:</B> Click to folder A to verify folder B is not exist</li>
	 */
	@Test(groups = {"unit"})
	public void testFilesMoveFoldersByMoreLink() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String gk_flag = "FILES_NESTED_FOLDER";
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();

			BaseFolder folderA = new BaseFolder.Builder("folderA"
					+ Helper.genDateBasedRand()).build();
			BaseFolder folderB = new BaseFolder.Builder("folderB"
					+ Helper.genDateBasedRand()).build();
            
			// Click on My folders
            clickMyFoldersView();
			// Create folders
			logger.strongStep("Create folder A and folder B");
			log.info("INFO: Create folder A and folder B");
			folderA.create(ui, false);
			folderB.create(ui, false);

			ui.clickLinkWait(FilesUIConstants.DisplayList);

			// Verify folder A and B exist.
			logger.strongStep("Verify folder A and B exist.");
			log.info("INFO: Verify folder A and B exist.");
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectMyFolder(folderA)),
					"ERROR: Folder A is not found ");
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectMyFolder(folderB)),
					"ERROR: Folder B is not found ");
			
			// Move folder B to folder A
			logger.strongStep("Move folder B to Folder A");
			log.info("INFO: Move folder B to Folder A");
			ui.selectFolderMoreLink(folderB);
			ui.clickLinkWait(FilesUIConstants.MoveToActionMoreLink);
			ui.selectComboValue(FilesUIConstants.pickerMenu, "My Folders");
			ui.moveFolder(folderA);

			// Verify Folder B was moved to the Folder A
			logger.strongStep("Verify Folder B was moved to the folder A");
			log.info("INFO: Verify Folder B was moved to the Folder A");
			ui.clickLinkWait(FilesUI.selectOneFolder(folderA));
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectMyFolder(folderB)),
					"ERROR: Move folder B to the folder A was failed ");

			// Move folder B to My Drive
			logger.strongStep("Move folder B to My Drive");
			log.info("INFO: Move folder B to My Drive");
			ui.selectFolderMoreLink(folderB);
			ui.clickLinkWait(FilesUIConstants.MoveToActionMoreLink);
			ui.selectComboValue(FilesUIConstants.pickerMenu, "My Drive");
			ui.clickLinkWait(FilesUIConstants.DialogmovetoButton);

			// Verify Folder B was moved to My Folders
			logger.strongStep("Verify Folder B was moved to My Folders");
			log.info("INFO: Verify Folder B was moved to My Folders");
			ui.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectMyFolder(folderB)),
					"ERROR: Move folder B to My Folders was failed ");

			ui.endTest();
		} else {
			// Skip this test case
			log.info("INFO: nested Folder is not enabled");
			throw new SkipException("Test move Folder Is Skipped");
		}

	}
	
	//@Test(groups = {"unit"})
	public void testFolderMoveFolderByDragAndDropRTL() throws Exception {
	    DefectLogger logger = dlog.get(Thread.currentThread().getId());
	    
        ui.startTest();
        
        BaseFolder folderA = new BaseFolder.Builder("folderA"
                + Helper.genDateBasedRand()).build();

        BaseFolder folderB = new BaseFolder.Builder("folderB"
                + Helper.genDateBasedRand()).build();

        // create folder
        logger.strongStep("Create folder");
        log.info("INFO: Create folder");
        folderA.create(ui, false);
        folderB.create(ui, false);

        // Click on My folders
        logger.strongStep("Click on My folders");
        log.info("INFO: Click on My folders");
        ui.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
        ui.clickLinkWait(FilesUIConstants.DisplayList);
        
        String sourceSelector = FilesUI.selectMyFolder(folderB);
        Element source = ui.getFirstVisibleElement(sourceSelector);
        
        String targetSelector = "css=div[dndtype='folder'][dndelementtitle='"+ folderA.getName() +"']";
        Element target = ui.getFirstVisibleElement(targetSelector);
        
        DragAndDrop(source, target);
        
        logger.strongStep("Verify folder B was moved to the folder A");
        log.info("INFO: Verify Folder B was moved to the Folder A");
        ui.clickLinkWithJavascript(FilesUI.selectMyFolder(folderA));
        Assert.assertTrue(
            driver.isElementPresent(FilesUI.selectMyFolder(folderB)),
            "ERROR: Move folder B to the folder A was failed ");
        ui.endTest();
	}
	
	    //@Test(groups = {"unit"})
	    public void testFolderMoveFolderByDragAndDropRTR() throws Exception {
	        DefectLogger logger = dlog.get(Thread.currentThread().getId());
	        
	        ui.startTest();
	        
	        BaseFolder folderA = new BaseFolder.Builder("folderA"
	                + Helper.genDateBasedRand()).build();

	        BaseFolder folderB = new BaseFolder.Builder("folderB"
	                + Helper.genDateBasedRand()).build();

	        // create folder
	        logger.strongStep("Create folder");
	        log.info("INFO: Create folder");
	        folderA.create(ui, false);
	        folderB.create(ui, false);

	        // Click on My folders
	        logger.strongStep("Click on My folders");
	        log.info("INFO: Click on My folders");
	        ui.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
	        ui.clickLinkWait(FilesUIConstants.DisplayList);
	        
	        String sourceSelector = FilesUI.selectMyFolder(folderB);
	        Element source = ui.getFirstVisibleElement(sourceSelector);
	        
	        String targetSelector = FilesUI.selectMyFolder(folderA);
	        Element target = ui.getFirstVisibleElement(targetSelector);
	        
	        DragAndDrop(source, target);
	        
	        logger.strongStep("Verify folder B was moved to the folder A");
	        log.info("INFO: Verify Folder B was moved to the Folder A");
	        ui.clickLinkWithJavascript(FilesUI.selectMyFolder(folderA));
	        Assert.assertTrue(
	            driver.isElementPresent(FilesUI.selectMyFolder(folderB)),
	            "ERROR: Move folder B to the folder A was failed ");
	        ui.endTest();
	    }

}
