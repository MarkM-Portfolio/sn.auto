package com.ibm.conn.auto.tests.files.unit;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.menu.Files_Folder_Dropdown_Menu;
import com.ibm.conn.auto.webui.FilesUI;



public class PersonaFiles_FolderDetailPage extends FilesUnitBaseSetUp {

	private static Logger log = LoggerFactory.getLogger(PersonaFiles_FolderDetailPage.class);
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {
		personalFilesSetUpClass();
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		personalFilesSetUp();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Move a folder to another folder by clicking on Folder's dropdown menu
	 * </li>
	 * <li><B>Step:</B> Create folder A</li>
	 * <li><B>Step:</B> Create folder B</li>
	 * <li><B>Step:</B> Move folder B to folder A</li>
	 * <li><B>Verify:</B> Verify folder B was moved to folder A</li>
	 * <li><B>Step:</B> Move folder B back to My Folders</li>
	 * <li><B>Verify:</B> Click to My Folders to verify folder B is exist</li>
	 * <li><B>Verify:</B> Click to folder A to verify folder B is not exist</li>
	 * </ul>
	 */
    @Test(groups = {"unit"})
	public void filesMoveFolderByFolderDropdownMenu() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String gk_flag = "FILES_NESTED_FOLDER";
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();

			BaseFolder folderA = new BaseFolder.Builder("folderA" + Helper.genDateBasedRand()).build();

			BaseFolder folderB = new BaseFolder.Builder("folderB" + Helper.genDateBasedRand()).build();
             
			// Click on My folders
            clickMyFoldersView();
			// Create folders
			logger.strongStep("Create folder A and folder B");
			log.info("INFO: Create fodler A and folder B");
			folderA.create(ui,false);
			folderB.create(ui,false);
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
			ui.clickLinkWait(FilesUI.selectMyFolder(folderB));
			Files_Folder_Dropdown_Menu.MOVE_TO.select(ui);
			ui.selectComboValue(FilesUIConstants.pickerMenu, "My Folders");
			ui.moveFolder(folderA);

			// Verify folder B was moved to the Folder A
			logger.strongStep("Verify folder B was moved to the folder A");
			log.info("INFO: Verify folder B was move to the folder A");
			ui.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
			ui.clickLinkWait(FilesUI.selectMyFolder(folderA));
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectMyFolder(folderB)),
					"ERROR: Move folder B to the folder A was failed");

			// Move folder B to My Drive
			logger.strongStep("Move folder B to My Drive");
			log.info("INFO: Move folder B to My Drive");
			ui.clickLinkWait(FilesUI.selectMyFolder(folderB));
			Files_Folder_Dropdown_Menu.MOVE_TO.select(ui);
			ui.selectComboValue(FilesUIConstants.pickerMenu, "My Drive");
			ui.clickLinkWait(FilesUIConstants.DialogmovetoButton);

			// Verify folder B was moved to My Folders
			logger.strongStep("Verify folder B was moved to My Folders");
			log.info("INFO: Verify folder B was moved to My Folders");
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
}
