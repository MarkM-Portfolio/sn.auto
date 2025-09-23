package com.ibm.conn.auto.tests.files.unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.webui.FilesUI;

public class PersonaFiles_PinnedFiles extends FilesUnitBaseSetUp{
	
	private static Logger log = LoggerFactory.getLogger(PersonaFiles_PinnedFiles.class);
	
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
	*<li><B>Info:</B> Upload a file in the view of 'Pinned Files' and verify the file is pinned</li>
	*<li><B>Step:</B> Click 'Pinned Files'</li>
	*<li><B>Step:</B> Upload a file</li>
	*<li><B>Verify:</B> Verify the file upload successfully</li>
	*<li><B>Verify:</B> Verify the file is pinned</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testUploadPinnedFiles() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String gk_flag = "FILES_NESTED_FOLDER";
		if (gkc.getSetting(gk_flag)) {   
			BaseFile file = new BaseFile.Builder(Data.getData().file1)
										.extension(".jpg")
										.rename(Helper.genDateBasedRand())
										.build();
			ui.startTest();
			
			//Click on Pinned Files
			logger.strongStep("Click on Pinned Files");
			log.info("INFO: Click on Pinned Files");
			ui.clickLinkWait(PinnedFilesLeftMenu);
			
			//upload a file
			logger.strongStep("Upload a file");
			log.info("INFO: Upload a file");
			file.upload(ui,gkc);
			
			//Verify the file was successfully uploaded
			logger.strongStep("Verify the file was successfully uploaded");
			log.info("INFO: Verify the file was successfully uploaded");
			Assert.assertTrue(ui.fluentWaitTextPresent("Successfully uploaded " + file.getName()),
					  "ERROR: File was not uploaded");
			
			//Select the 'Details' display button
			logger.strongStep("Select the 'Details' display button");
			log.info("INFO: Select Details display button");
			Files_Display_Menu.DETAILS.select(ui);
			
			//Verify the file is pinned
			logger.strongStep("Verify the file is pinned");
			log.info("INFO: Verify the file is pinned");
			Assert.assertTrue(driver.isElementPresent(FilesUI.getFileIsUploaded(file)),
					  "ERROR:File is unpinned");
			
			ui.endTest();
		}else{
	    	//  Skip this test case
			log.info("INFO: nested Folder is not enabled");
			throw new SkipException("Test upload in the view of 'PinnedFiles' Is Skipped");		
	    }
	   
		


		
		
	}
	
}
