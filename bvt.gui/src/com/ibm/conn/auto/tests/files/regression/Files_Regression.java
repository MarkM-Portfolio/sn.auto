package com.ibm.conn.auto.tests.files.regression;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;

import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.FilesUI.FilesListView;

public class Files_Regression extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(Files_Regression.class);
	private FilesUI ui;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
	
	}	

	
	/**
	 * Upload a file, select the detail view and then select the file just uploaded
	 * and then download this file and verify that the file has being downloaded.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void filesSingleUploadAndSingleDownloadInDetailedView() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		User testUser = cfg.getUserAllocator().getUser();

		BaseFile file = new BaseFile.Builder(Data.getData().file6)
		.extension(".jpg")
		.rename(Helper.genDateBasedRand())								
		.build();


		ui.startTest();

		//Login with user to files
		logger.strongStep("INFO: Log into Files as: " + testUser.getDisplayName());
		log.info("INFO: Log into Files as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		//Set the directory for the download and ensure that it is empty
		if (testConfig.serverIsLegacyGrid()) {
			logger.strongStep("INFO: Set the directory for the download and ensure that it is empty");
			log.info("INFO: Set the directory for the download and ensure that it is empty");
			ui.setupDirectory();
		}

		//Upload private file
		logger.strongStep("INFO: Upload a file");
		log.info("INFO: Upload a file");
		file.upload(ui);

		//select view and then select the file to download
		logger.strongStep("INFO: Select 'Detail' view and then select the file to download");
		log.info("INFO: Select 'Detail' view and then select the file to download");
		ui.changeViewAndSelectFile("Detail");

		//Now perform a download
		logger.strongStep("INFO: Download the file");
		log.info("INFO: Download the file");
		file.download(ui);

		//Verify the file has being downloaded - localhost currently
		logger.strongStep("INFO: Verify the file has being downloaded - localhost currently");
		log.info("INFO: Verify the file has being downloaded - localhost currently");
		ui.verifyFileDownloaded(file.getRename() + file.getExtension());
		
		ui.endTest();

	}

	/**
	 * Upload a file, select the list view and then select the file just uploaded
	 * and then download this file and verify that the file has being downloaded.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void filesSingleUploadAndSingleDownloadInListView() throws Exception{

		User testUser = cfg.getUserAllocator().getUser();

		BaseFile file = new BaseFile.Builder(Data.getData().file1)
		.extension(".jpg")
		.rename(Helper.genDateBasedRand())								
		.build();

		ui.startTest();

		//Login with user to files
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		//Set the directory for the download and ensure that it is empty
		if (testConfig.serverIsLegacyGrid())  {
			ui.setupDirectory();
		}

		//Upload private file
		file.upload(ui);

		//select view and then select the file to download
		ui.changeViewAndSelectFile("List");

		//Now perform a download
		file.download(ui);

		//Verify the file has being downloaded - localhost currently
		ui.verifyFileDownloaded(file.getRename() + file.getExtension());

		ui.endTest();
	}

	/**
	 * This test depends on the {@link #filesFifteenFiles()} test.
	 * The setupDirectory will clean the directory so we are sure that any
	 * downloaded files belong to this test. Open the file component and switch 
	 * to the list view and then click Download and then Download again. This downloads 
	 * the file as a zipped file. Verify that the file is download to the directory
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void filesBulkDownloadZipAllListView() throws Exception{
		filesBulkDownloadZip(true, FilesListView.LIST);
	}

	/**
	 * This test depends on the {@link #filesFifteenFiles()} test.
	 * The setupDirectory will clean the directory so we are sure that any
	 * downloaded files belong to this test. Open the file component and switch 
	 * to the detail view and then click Download and then Download again. This downloads 
	 * the file as a zipped file. Verify that the file is download to the directory.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void filesBulkDownloadZipAllDetailsView() throws Exception {
		filesBulkDownloadZip(true, FilesListView.LIST);
	}

	/**
	 * This test depends on the {@link #filesFifteenFiles()} test.
	 * The setupDirectory will clean the directory so we are sure that any
	 * downloaded files belong to this test. Open the file component and switch 
	 * to the details view and select two of the uploaded files and select download.
	 * This downloads the files as a zipped file. Verify that the file is download 
	 * to the directory.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void filesBulkDownloadZipDetailsView() throws Exception{
		filesBulkDownloadZip(false, FilesListView.DETAILS);
	}

	/**
	 * This test depends on the {@link #filesFifteenFiles()} test.
	 * The setupDirectory will clean the directory so we are sure that any
	 * downloaded files belong to this test. Open the file component and switch 
	 * to the list view and select two of the uploaded files and select download.
	 * This downloads the files as a zipped file. Verify that the file is download 
	 * to the directory.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void filesBulkDownloadZipListView() throws Exception{
		filesBulkDownloadZip(false, FilesListView.LIST);
	}
	
	public void filesBulkDownloadZip(boolean downloadAll, FilesListView view) throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		User testUser = cfg.getUserAllocator().getUser();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		.extension(".jpg")
		.rename(Helper.genStrongRand())
		.build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
		.extension(".jpg")
		.rename(Helper.genStrongRand())
		.build();

		BaseFile fileC = new BaseFile.Builder(Data.getData().file2)
		.extension(".jpg")
		.rename(Helper.genStrongRand())
		.build();

		ui.startTest();
		String zippedFile = "files.zip";

		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		cUI.closeGuidedTourPopup();

		//upload file
		logger.strongStep("INFO: Upload the file");
		log.info("INFO: Upload the file");
		if (downloadAll) {
			ui.multipleFileUpload(fileA, fileB);
		}else{
			ui.multipleFileUpload(fileA, fileB, fileC);
		}

		//Set the directory for the download and ensure that it is empty
		if (testConfig.serverIsLegacyGrid())  {
			ui.setupDirectory();
		}

		if (downloadAll) {
			logger.strongStep("INFO: Open the My Files view");
			log.info("INFO: Open the My Files view");
			ui.clickLinkWait(FilesUIConstants.openMyFilesView);
			
			//select view and then select the file to download
			ui.changeViewAndSelectAllFiles(view);

			//click on the download button and then agree to compress the files into a zip
			ui.downloadAllAsCompressedFile();
		} else {
			logger.strongStep("INFO: Open the My Files view");
			log.info("INFO: Open the My Files view");
			ui.clickLinkWait(FilesUIConstants.openMyFilesView);
			
			ui.clickLinkWait(view.getActivateSelector());
			
			//Select the view
			ui.clickLinkWait(view.getIsActiveSelector());
			ui.fluentWaitPresent(view.getIsActiveSelector());

			//Select file A and B but not C
			ui.selectFileCheckmark(fileA);
			ui.selectFileCheckmark(fileB);

			//click on the download button and then agree to compress the files into a zip
			ui.downloadAsCompressedFile();			
		}

		//verify that files.zip has being loaded to the default location
		ui.verifyFileDownloaded(zippedFile);
		
		ui.endTest();
	}

	/**
	 * Create a folder and verify that it was created successfully.  Then chose to delete this folder
	 * and verify that the folder no longer exists in the UI.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void filesDeletingACollection() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();

		BaseFolder folder = new BaseFolder.Builder(testName + Helper.genDateBasedRand())
		.description(Data.getData().FolderDescription)
		.build();



		//Login with user to files
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		cUI.closeGuidedTourPopup();

		//Click on the new folder button
		ui.clickLink(FilesUIConstants.NewFolder_Button);

		//Create a folder
		logger.strongStep("INFO: Creating a folder");
		log.info("INFO: Creating a folder");
		folder.create(ui);	

		//Select the list view
		ui.clickLinkWait(FilesListView.LIST.getActivateSelector());
		ui.fluentWaitPresent(FilesListView.LIST.getIsActiveSelector());

		//click on the more actions and chose Delete
		ui.clickLink(FilesUIConstants.MoreButton);
		ui.clickLink(FilesUIConstants.filesMoreActionsBtn);
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.ClickForActionsOptionDelete));
		ui.clickLink(FilesUIConstants.ClickForActionsOptionDelete);
		ui.clickButton(Data.getData().buttonDelete);

		//Verify that the folder has now being deleted
		Assert.assertTrue(driver.isTextPresent("The folder was deleted"));
		Assert.assertTrue(driver.isTextNotPresent(folder.getName()));
		
		ui.endTest();

	}

	/**
	 * Create a folder and verify that it was created. Then chose to edit this folder's
	 * name and description and verify that the changes were successful.
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void filesEditACollectionDescription() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		User testUser = cfg.getUserAllocator().getUser();
		String testName = ui.startTest();

		BaseFolder folder = new BaseFolder.Builder(testName + Helper.genDateBasedRand())
		.description(Data.getData().FolderDescription)
		.build();

		String EditProperties = "Edit Folder Properties";
		String editedFolderName = "new folder name for this test"+Helper.genDateBasedRand();
		String editFolderDesc = "this is a regression test for the files component testing the edit properties";

		//login to files
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		cUI.closeGuidedTourPopup();

		//click on the collections button
		ui.clickLink(FilesUIConstants.MyFolderLink);

		//create and select a collection from the list
		ui.clickLink(FilesUIConstants.NewFolder_Button);

		//Create a folder
		logger.strongStep("INFO: Creating a folder");
		log.info("INFO: Creating a folder");
		folder.create(ui);	

		//verify that you can see the selection and the edit button
		Assert.assertTrue(driver.isTextPresent(folder.getName()));
		
		logger.strongStep("INFO: Click on list view button for Files");
		log.info("INFO: Click on list view button for Files");
		ui.clickLinkWait(FilesListView.LIST.getActivateSelector());
		
		logger.strongStep("INFO: Verify list view button for Files is present");
		log.info("INFO: Verify list view button for Files is present");
		ui.fluentWaitPresent(FilesListView.LIST.getIsActiveSelector());

		//click on the more actions and chose Delete
		ui.clickLink(FilesUIConstants.MoreButton);
		ui.clickLink(FilesUIConstants.filesMoreActionsBtn);
		
		//click the edit button
		ui.clickLink(FilesUIConstants.ClickForActionsOptionEditProp);

		//verify and edit the properties dialog and save the changes
		ui.fluentWaitTextPresent(EditProperties);
		ui.clearText(FilesUIConstants.editPropertiesName);
		ui.typeText(FilesUIConstants.editPropertiesName, editedFolderName);
		ui.clearText(FilesUIConstants.editPropertiesDesc);
		ui.typeText(FilesUIConstants.editPropertiesDesc, editFolderDesc);
		ui.clickButton(Data.getData().buttonSave);

		//verify that the changes are made
		ui.fluentWaitTextPresent(editedFolderName+" was saved successfully");

		Assert.assertTrue(driver.isTextNotPresent(folder.getName()));
		Assert.assertTrue(driver.isTextNotPresent(folder.getDescription()));
		Assert.assertTrue(driver.isTextPresent(editedFolderName));
		Assert.assertTrue(driver.isTextPresent(editFolderDesc));
		
		ui.endTest();

	}
	
}
