/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited 2019,2020                      */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.files.regression;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;

public class BVT_BoschUAT_Files extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_BoschUAT_Files.class);
	private FilesUI ui;
	private CommunitiesUI commUI;
	private TestConfigCustom cfg;
	private User testUser;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();

		// Load users
		testUser = cfg.getUserAllocator().getUser();

	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Verify user is able to add file from my computer</li>
	 * <li><B>Step:</B>Login to connections</li>
	 * <li><B>Verify:</B>Verify user should be able login and Homepage should be displayed after login</li>
	 * <li><B>Step:</B>Close guided tour popup</li>
	 * <li><B>Step:</B>From Homepage navigate to Files: from mega-menu select Apps > Files </li>
	 * <li><B>Step:</B>Close guided tour popup</li>
	 * <li><B>Step:</B>Select new option from file menu and create new folder</li>
	 * <li><B>Verify:</B>Verify new folder gets created under my drive</li>
	 * <li><B>Step:</B>Click on new created folder</li>
	 * <li><B>Step:</B>Click on action menu adjacent to folder name and select add files</li>
	 * <li><B>Verify:</B>'Add Files' pop-up should be displayed with FILES and MY COMPUTER tabs.</li>
	 * <li><B>Step:</B>Upload files from My computer tab</li>
	 * <li><B>Verify:</B>Add file should be successful without any error</li>
	 * <li><B>Verify:</B>'Add Files' pop-up should be displayed with FILES and MY COMPUTER tabs.</li>
	 * </ul>
	 */
	@Test(groups = { "regression"})
	public void verifyUploadFilesFromMyComputer() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseFolder folder = new BaseFolder.Builder(testName + Helper.genDateBasedRand())
		.description(Data.getData().FolderDescription)
		.build();

		ui.startTest();

		// Login with user to files
		logger.strongStep("INFO:Login to Connections System - Homepage");
		log.info("INFO: Login to Connections System - Homepage");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		
		logger.strongStep("INFO:Close guided tour popup");
		log.info("INFO:Close guided tour popup");
		commUI.closeGuidedTourPopup();
		
		// Click Mega Menu item
		logger.strongStep("INFO:Select Apps Mega Menu option");
		log.info("INFO:Select Apps Mega Menu option");
		ui.clickLinkWait(ui.getMegaMenuApps());
		
		logger.strongStep("INFO:Select File option");
		log.info("INFO:Select File option");
		ui.clickLinkWithJavascript(FilesUIConstants.filesOption);
		
		logger.strongStep("INFO:Close guided tour popup");
		log.info("INFO:Close guided tour popup");
		commUI.closeGuidedTourPopup();
		
		logger.strongStep("INFO:Verify my drive is opened");
		log.info("INFO:Verify my drive is opened");
		ui.fluentWaitPresent(FilesUIConstants.GLOBAL_NEW_BUTTON);
		
		//Create a folder
		logger.strongStep("INFO: Creating a folder");
		log.info("INFO: Creating a folder");
		folder.create(ui,false);
		
		logger.strongStep("INFO: Verify if new folder is created under My Drive");
		log.info("INFO: Verify if new folder is created under My Drive");
		ui.fluentWaitElementVisible(FilesUIConstants.myDriveFolder);
		String expfolderName = ui.getFirstVisibleElement(FilesUIConstants.myDriveFolder).getText().trim();
		Assert.assertTrue(expfolderName.contains(folder.getName()));
		
		logger.strongStep("INFO: Click on newly created folder");
		log.info("INFO: Click on newly created folder");
		ui.clickLink("link=" + folder.getName());
		
		logger.strongStep("INFO: Click on folder action menu");
		log.info("INFO: Click on folder action menu");
		ui.clickLinkWithJavascript(ui.getFolderActionMenu(folder.getName()));
		
		logger.strongStep("INFO: Click on add files menu");
		log.info("INFO: Click on add files menu");
		ui.clickLinkWithJavascript(FilesUIConstants.addFilesMenu);
		
		logger.strongStep("INFO: Click on My Computer to add files");
		log.info("INFO: Click on My Computer to add files");
		ui.clickLinkWait(FilesUIConstants.LinkToconnectionsFilesUpload);
		try {
			logger.strongStep("INFO: Add files from local");
			log.info("INFO: Add files from local");
			ui.fileToUpload(Data.getData().file1, BaseUIConstants.FileInputField);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.strongStep("INFO: Click on Add files button");
		log.info("INFO: Click on Add files button");
		ui.clickLinkWait(FilesUIConstants.addFileButton);
		
		ui.isTextPresent("Successfully uploaded "+Data.getData().file1+". Check it out in your My Files view.");

		ui.endTest();

	}

}
