package com.ibm.conn.auto.tests.files.unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.conn.auto.util.DefectLogger;

public class PersonalFiles_Expand_SYNC_InNavigation extends FilesUnitBaseSetUp{
	private static Logger log = LoggerFactory.getLogger(PersonalFiles_Expand_SYNC_InNavigation.class);

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
	*<li><B>Step:</B> click Sync Navigation Button</li>
	*<li><B>Verify:</B> can find DivElement id="menu_fileSync_childContainer" </li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testExpandSyncInLeftNavigation() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_FOLDER_SYNCABLE";
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();
			
			// click sync view
			ui.clickLinkWait(SyncExpandLink);
			// Verify DivElement id="menu_fileSync_childContainer"  exist. 
			logger.strongStep("Verify DivElement id='menu_fileSync_childContainer' exist.");
			Assert.assertTrue(
					driver.isElementPresent(SyncChildContainer),
					"ERROR: DivElement id='menu_fileSync_childContainer' is not found ");
			log.info("INFO: DivElement id='menu_fileSync_childContainer' is found.");
			
			ui.endTest();
		}
	}
}
