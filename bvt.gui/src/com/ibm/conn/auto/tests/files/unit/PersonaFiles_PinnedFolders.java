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
import com.ibm.conn.auto.webui.FilesUI;

public class PersonaFiles_PinnedFolders extends FilesUnitBaseSetUp {

	private static Logger log = LoggerFactory.getLogger(PersonaFiles_PinnedFolders.class);
	
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
	*<li><B>Info:</B> Create a new folder in the view of 'Pinned Folders' and verify the folder is pinned</li>
	*<li><B>Step:</B> Click 'Pinned Folders'</li>
	*<li><B>Step:</B> Create a new folder</li>
	*<li><B>Verify:</B> Verify the folder is pinned</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testCreateFolderPinned() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String gk_flag = "FILES_NESTED_FOLDER";
		String gk_simple_left_nav_flag = "FILES_ENABLE_SIMPLIFY_LEFT_NAV";
		if (gkc.getSetting(gk_flag)) {   
			ui.startTest();
			BaseFolder folder = new BaseFolder.Builder("folder"
					+ Helper.genDateBasedRand()).build();
			
			//Click on Pinned Folders
			logger.strongStep("Click on Pinned Folders");
			log.info("INFO: Click on Pinned Folders");
			if (gkc.getSetting(gk_simple_left_nav_flag)) {
				ui.clickPinnedFoldersInFoldersView();
			} else {
				ui.clickLinkWait(FilesUIConstants.PinnedFoldersLeftMenu);
			}
			
			//Create a new folder
			logger.strongStep("Create a new folder");
			log.info("INFO: Create a new folder");
			folder.create(ui, false);

			//Click on display list to show folders in list
			logger.strongStep("Click on display list");
			log.info("INFO: Click on display list");
			ui.clickLinkWait(FilesUIConstants.DisplayList);
			
			//Verify the folder is pinned
			logger.strongStep("Verify the folder is pinned");
			log.info("INFO: Verify the folder is pinned");
			Assert.assertTrue(
					driver.isElementPresent(FilesUI.selectMyFolder(folder)),
					"ERROR: Folder is not pinned ");
			
			ui.endTest();

		}else{
	    	//  Skip this test case
			log.info("INFO: nested Folder is not enabled");
			throw new SkipException("Create a folder in the view of 'PinnedFolders' Is Skipped");		
	    }
	   

	}
	
}
