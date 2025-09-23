package com.ibm.conn.auto.tests.files.unit;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.FilesUI;

public class CommunityFiles_Fullpage extends FilesUnitBaseSetUp {

	private static Logger log = LoggerFactory.getLogger(CommunityFiles_Fullpage.class);
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {
		communityFilesSetUpClass();
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		communityFilesSetUp();
	}
	
	/**
	* <ul>
	* <li><B>Info:</B> Move a folder to another folder for Community Files by clicking on More Link</li>
	* <li><B>Step:</B>Create a community</li>
	* <li><B>Step:</B> Create folder A</li>
	* <li><B>Step:</B> Create folder B</li>
	* <li><B>Step:</B> Move folder B to folder A</li>
	* <li><B>Verify:</B> Verify folder B was moved to folder A</li>
	* <li><B>Step:</B> Move folder B back to My Folders</li>
	* <li><B>Verify:</B> Click to My Folders to verify folder B is exist</li>
	* <li><B>Verify:</B> Click to folder A to verify folder B is not exist</li>
	*/
	@Test(groups = {"unit"})
	public void communityFilesMoveFolderByMoreLink() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String gk_flag = "FILES_NESTED_FOLDER";
		String gk_dnd_flag = "FILES_TREE_DND";
		String folder_view = gkc.getSetting(gk_dnd_flag) ? FilesUIConstants.navCommunityFolders : FilesUIConstants.ComFolderTab;
		
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();

			BaseFolder folderA = new BaseFolder.Builder("folderA" + Helper.genDateBasedRand()).build();

			BaseFolder folderB = new BaseFolder.Builder("folderB" + Helper.genDateBasedRand()).build();

			// select Files from left menu
			logger.strongStep("Select Files from left navigetion menu");
			log.info("INFO: Select Files from left navigation menu");
			community.navViaUUID(cUI);
			Community_LeftNav_Menu.FILES.select(ui);

			// create folders
			logger.strongStep("Create folders");
			log.info("INFO: Create folders");
			folderA.add(ui);
			folderB.add(ui);
			ui.clickLinkWait(folder_view);
			ui.clickLinkWait(FilesUIConstants.DisplayList);

			// Move folder B to folder A
			logger.strongStep("Move folder B to folder A");
			log.info("INFO: Move folder B to Folder A");
			ui.selectFolderMoreLink(folderB);
			ui.clickLinkWait(FilesUIConstants.MoveToActionMoreLink);
			ui.moveFolder(folderA);

			// Verify Folder B was moved to the Folder A
			logger.strongStep("Verify Folder B was moved to the Folder A");
			log.info("INFO: Verify Folder B was moved to the Folder A");
			ui.clickLinkWait(FilesUI.selectMyFolder(folderA));
			Assert.assertTrue(driver.isElementPresent(FilesUI.selectMyFolder(folderB)),"ERROR: Move folder B to the folder A was failed ");

			// Move folder B to Top Level Folders
			logger.strongStep("Move folder B to Top Level Folders");
			log.info("INFO: Move folder B to Top Level Folders");
			ui.selectFolderMoreLink(folderB);
			ui.clickLinkWait(FilesUIConstants.MoveToActionMoreLink);
			ui.fluentWaitElementVisible(FilesUIConstants.DialogmovetoButton);
			ui.clickLinkWait(FilesUIConstants.DialogmovetoButton);

			// Verify Folder B was moved to Top Level Folders
			logger.strongStep("Verify Folder B was moved to Top Level Folders");
			log.info("INFO: Verify Folder B was moved to Top Level Folders");
			Community_LeftNav_Menu.FILES.select(ui);
			ui.clickLinkWait(folder_view);
			Assert.assertTrue(driver.isElementPresent(FilesUI.selectMyFolder(folderB)),"ERROR: Move folder B to My Folders was failed ");
			
			ui.endTest();
		} else {
			// Skip this test case
			log.info("INFO: nested Folder is not enabled");
			throw new SkipException("Test Commmunity Files move Folder Is Skipped");
		}
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Upload a file for Community Files</li>
	*<li><B>Step:</B> Create a community</li>
	*<li><B>Step:</B> Upload a file</li>
	*<li><B>Verify:</B> Verify the file upload successfully</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void communityFilesUploadFile() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String gk_flag = "FILES_NESTED_FOLDER";
	
		if (gkc.getSetting(gk_flag)) {
			ui.startTest();
			
			BaseFile file = new BaseFile.Builder(Data.getData().file1)
										.comFile(true)
										.extension(".jpg")
										.build();
			
			// select Files from left menu
			logger.strongStep("Select Files from left navigetion menu");
			log.info("INFO: Select Files from left navigation menu");
			community.navViaUUID(cUI);
			Community_LeftNav_Menu.FILES.select(ui);
			
			//upload a file
			logger.strongStep("Upload a file");
			log.info("INFO: Upload a file");
			if(!cfg.getSecurityType().equalsIgnoreCase("false"))
				file.upload(ui,gkc);
			else
				file.upload(ui);
			
			//Verify the file was successfully uploaded
			logger.strongStep("Verify the file was successfully uploaded");
			log.info("INFO: Verify the file was successfully uploaded");
			Assert.assertTrue(ui.fluentWaitTextPresent("Successfully uploaded " + file.getName()),
					  "ERROR: File was not uploaded");
			
			ui.endTest();
		}else{
	    	//  Skip this test case
			log.info("INFO: nested Folder is not enabled");
			throw new SkipException("Test upload in the view of 'PinnedFiles' Is Skipped");		
	    }
	 	
	}
}
