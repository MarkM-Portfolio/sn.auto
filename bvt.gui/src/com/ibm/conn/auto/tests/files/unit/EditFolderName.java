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
import com.ibm.conn.auto.util.menu.Files_Folder_Dropdown_Menu;
import com.ibm.conn.auto.webui.FilesUI;

public class EditFolderName extends FilesUnitBaseSetUp{
private static Logger log = LoggerFactory.getLogger(EditFolderName.class);

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
	*<li><B>Info:</B> Perform actions on create a folder</li>
	*<li><B>Step:</B> Name Folder With Invalid Character</li>
	*<li><B>Verify:</B> Cannot create successfully</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testCreateFolderWithInvalidCharacter() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String gk_flag = "FILES_NESTED_FOLDER";
		if (gkc.getSetting(gk_flag)) {
		    ui.startTest();
			BaseFolder folder = new BaseFolder.Builder("< folder />"
					+ Helper.genDateBasedRand()).build();
			
			// create folder
			logger.strongStep("Create folder");
			log.info("INFO: Create folder");
			//Click on the New Folder button
			ui.clickNewFolderInGlobalNewButton(false);

			//Fill in the form
			ui.typeText(FilesUIConstants.CreateFolderName, folder.getName());
			//Save the form
			ui.clickCreateButton();
			
			Assert.assertTrue(driver.getPageSource().contains("The folder name cannot include the following characters:"), "Error: there is no error message");
			
			// Verify folder A cannot be created.
			logger.strongStep("Verify folder A can be created.");
			log.info("INFO: Verify folder A can be created.");
			Assert.assertFalse(driver.isElementPresent(FilesUI.selectMyFolder(folder)), "ERROR: The folder with name includes invalid characters is created sucessfully ");
			ui.clickCancelButton();
			ui.endTest();
		}
	}
	
	/**
	*<ul>SAMPLE TEST TO DEMONSTRATE SUCCESS 
	*<li><B>Info:</B> Perform actions on create a folder</li>
	*<li><B>Step:</B> Name Folder With Invalid Character</li>
	*<li><B>Verify:</B> Cannot name successfully</li>
	*</ul>
	*/
	@Test(groups = {"unit"})
	public void testEditFolderWithInvalidCharacter() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String gk_flag = "FILES_NESTED_FOLDER";
		if (gkc.getSetting(gk_flag)) {
		    ui.startTest();
			BaseFolder folder = new BaseFolder.Builder("folder"
					+ Helper.genDateBasedRand()).build();
			clickMyFoldersView();
			// create folder
			logger.strongStep("Create folder");
			log.info("INFO: Create folder");
			//Click a folder
			folder.create(ui, false);
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			ui.clickLinkWait(FilesUI.selectMyFolder(folder));
			//Click on the Edit Properties button
			Files_Folder_Dropdown_Menu.EDIT_PROPERTIES.select(ui);
			
			//Change Folder Name
			folder.setName("<folder>");
			ui.clearText(FilesUIConstants.CreateFolderName);
			//Fill in the form
			ui.typeText(FilesUIConstants.CreateFolderName, folder.getName());
			ui.clickSaveButton();
			
			Assert.assertTrue(driver.getPageSource().contains("The folder name cannot include the following characters:"), "Error: there is no error message");
			
			ui.endTest();
		}
	}
	
}
