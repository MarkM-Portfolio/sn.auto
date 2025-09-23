package com.ibm.conn.auto.tests.files.unit;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.webui.FilesUI;

public class AddFoldersToSync extends FilesUnitBaseSetUp {

	private static Logger log = LoggerFactory
			.getLogger(AddFoldersToSync.class);

	@BeforeClass(alwaysRun=true)
    public void SetUpClass(){
        personalFilesSetUpClass();
    }
    
    
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        personalFilesSetUp();
    }

	@Test(groups = { "unit"})
	public void testAddMultiFolderSync() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
			
			String testName = ui.startTest();

			// Create a folder and file base state object
			BaseFolder SfolderA = new BaseFolder.Builder(testName
					+ Helper.genDateBasedRand() + " A").description(
					Data.getData().FolderDescription).build();

			BaseFolder SfolderB = new BaseFolder.Builder(testName
					+ Helper.genDateBasedRand() + " B").description(
					Data.getData().FolderDescription).build();

			clickMyFoldersView();
			// Create new folder
			logger.strongStep("Create 2 new folders to add to sync");
			log.info("INFO: Create 2 new folders to add to sync");
			SfolderA.create(ui, false);
			SfolderB.create(ui, false);

			// Click on display list to show folders in list
			logger.strongStep("Click on 'Display List'");
			log.info("INFO: Click on Display List ");
			ui.clickLinkWait(FilesUIConstants.DisplayList);

			logger.strongStep("select the 2 new folders");
			log.info("INFO: select the 2 new folders to drag");
			ui.selectFolderCheckmark(SfolderA);
			ui.selectFolderCheckmark(SfolderB);

			log.info("INFO: get source folder element ");
			String Sfoldercss = FilesUI.selectMyFolder(SfolderA);
			ui.fluentWaitPresent(Sfoldercss);

			ui.getFirstVisibleElement(
					"css=button[id^='lconn_files_action_addcollectionstosync']")
					.click();
			Assert.assertTrue(ui
					.fluentWaitTextPresent("The 2 selected folders can now be accessed in the root of My Drive."));
			ui.endTest();
		}
	}

	@Test(groups = { "unit"})
	public void testAddSingleFolderSync() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {

			String testName = ui.startTest();

			// Create a folder and file base state object
			BaseFolder SfolderA = new BaseFolder.Builder(testName
					+ Helper.genDateBasedRand() + " A").description(
					Data.getData().FolderDescription).build();

			clickMyFoldersView();
			// Create new folder
			logger.strongStep("Create one new folder to sync");
			log.info("INFO: Create one new folder to sync");
			SfolderA.create(ui, false);

			// Click on display list to show folders in list
			logger.strongStep("Click on 'Display List'");
			log.info("INFO: Click on Display List ");
			ui.clickLinkWait(FilesUIConstants.DisplayList);

			logger.strongStep("select the new folder");
			log.info("INFO: select the new folders to add to sync");
			ui.selectFolderCheckmark(SfolderA);

			ui.getFirstVisibleElement(
					"css=button[id^='lconn_files_action_addcollectionstosync']")
					.click();
			Assert.assertTrue(ui
					.fluentWaitTextPresent("The folder can now be accessed in the root of My Drive."));
			ui.endTest();
		}

	}

}
