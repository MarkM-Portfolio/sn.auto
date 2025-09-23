package com.ibm.conn.auto.tests.files;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APISearchHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Files_Folder_Dropdown_Menu;
import com.ibm.conn.auto.util.menu.Files_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.cloud.FilesUICloud;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class BVT_Level_2_Files extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Files.class);
	private FilesUI ui;
	private FileViewerUI uiViewer;
	private TestConfigCustom cfg;	
	private User testUser, testUser1, guestUser, searchUser, adminUser, efssUser;
	private APICommunitiesHandler apiOwner;
	private CommunitiesUI cUI;
	private GatekeeperConfig gkc;
	private String serverURL;

	int fileSearchTime = 30 * 60 * 1000;
	int newFileWaitingTime = 60 * 1000;
	
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass(){
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		adminUser = cfg.getUserAllocator().getAdminUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		uiViewer = FileViewerUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		ui.addOnLoginScript(ui.getCloseTourScript());
	}	

	/**
	 *<ul>
	 *<li><B>Info:</B> Validate options within the Mega Menu item</li>
	 *<li><B>Step:</B> Click Mega Menu item</li>
	 *<li><B>Verify:</B> Files option is present</li>
	 *<li><B>Verify:</B> Shared With Me option is present</li>
	 *<li><B>Verify:</B> Pinned Folders option is present</li>
	 *</ul>
	 */

	@Test(groups = {"level2", "bvt", "regressioncloud", "infra"})
	public void validateMegaMenu() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		User testUser = cfg.getUserAllocator().getUser();
		
		// Load the component and login
		logger.strongStep("Load Files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		//Click Mega Menu item
		logger.strongStep("Select File Mega Menu option");
		log.info("INFO: Select Files Mega Menu option");
		ui.clickLinkWait(ui.getMegaMenuApps());
		
		//Validate Files option is contained with in drop down menu
		logger.weakStep("Validate 'Files' option is contained with in the drop down menu");
		log.info("INFO: Validate 'Files' option is contained with in drop down menu");
		ui.selectMegaMenu(ui.getMegaMenuApps());
		Assert.assertTrue(ui.fluentWaitPresent(ui.getFilesOption()),
						  "Unable to validate Mega Menu 'Files' option in drop down menu");

		//Validate Shared With Me option is contained with in drop down menu
		logger.weakStep("Validate 'Share With Me' option is contained with in the drop down menu");
		log.info("INFO: Validate 'Shared With Me' option is contained with in drop down menu");
		ui.selectMegaMenu(ui.getMegaMenuApps());
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.filesSharedWithMe ),
						  "Unable to validate Mega Menu 'Shared With Me' option in drop down menu");
		
		//Validate Pinned Folders option is contained with in drop down menu
		logger.weakStep("Validate 'Pinned Folders' option is contained with in the drop down menu");
		log.info("INFO: Validate 'Pinned Folders' option is contained with in drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.filesPinnedFolders),
						  "Unable to validate Mega Menu 'Pinned Folders' option in drop down menu");

		ui.endTest();
	
	}
	
/**
	*<ul>
	*<li><B>Info:</B>Upload a file</li>
	*<li><B>Step:</B> Upload a file, rename before save.</li>
	*<li><B>Verify:</B> File is upload and all actions are performed</li>
	*</ul>
	*/
	@Test(groups = {"regression", "bvt", "regressioncloud"})
	public void uploadPrivateFile() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();
		
		//Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		//upload file
		logger.strongStep("Upload the file");
		log.info("INFO: Upload the file");
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			file.upload(ui,gkc);
		else
			file.upload(ui);

		
		//Validate message displays stating file was successfully uploaded
		logger.weakStep("Verify that the message displays stating the file was successfully uploaded");
		log.info("INFO: Verify message displays stating file was successfully uploaded");
		Assert.assertTrue(ui.fluentWaitTextPresent("Successfully uploaded " + file.getName()),
				  "ERROR: File was not uploaded");
		
		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Like a file</li>
	*<li><B>Step:</B> Upload a file via API</li>
	*<li><B>Step:</B> Select Details display button</li>
	*<li><B>Step:</B> Like the file</li> 
	*<li><B>Step:</B> Unlike the file</li>
	*<li><B>Verify:</B> The file can be liked/unliked</li>
	*</ul>
	*/  
    @Test(groups = {"regression", "bvt", "regressioncloud"})
	public void likeFile() throws Exception {
    	
    	DefectLogger logger=dlog.get(Thread.currentThread().getId());

		BaseFile file = new BaseFile.Builder(Data.getData().file3)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();
		
		//Upload a file
		logger.strongStep("Upload a file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());
		
		//Load the component
		logger.strongStep("Load Files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		//Switch the display from default Tile to Details
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		ui.clickMyFilesView();
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
	
		//like file
		logger.weakStep("Like the file and validate the message");
		log.info("INFO: Like the file and validate message");
		file.like(ui);
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().LikeMessage),
						  "ERROR: Unable to find text " + Data.getData().LikeMessage);
		
		//unlike file
		logger.weakStep("Unlike the file and validate the message");
		log.info("INFO: Unlike the file and validate message");
		file.like(ui);
		Assert.assertTrue(ui.fluentWaitTextNotPresent(Data.getData().LikeMessage), 
						  "ERROR: Found text " + Data.getData().LikeMessage);

		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Follow and stop following a file</li>
	*<li><B>Step:</B> Upload a file via API</li>
	*<li><B>Step:</B> Select Details display button</li>
	*<li><B>Step:</B> Stop following the file</li> 
	*<li><B>Verify:</B> Check the file is no longer being followed</li>
	*<li><B>Step:</B> Follow the file</li>
	*<li><B>Verify:</B> Check the file is being followed</li>
	*</ul>
	*/
	
	@Test(groups = {"level2", "bvt", "regressioncloud","cnx8ui-level2"})
	public void followFile() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		BaseFile file = new BaseFile.Builder(Data.getData().file5)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();
		
		//Upload a file
		logger.strongStep("Upload a file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());
		
		//Load the component
		logger.strongStep("Load Files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		//Switch the display from default Tile to Details
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		ui.clickMyFilesView();
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		//Stop following
		logger.strongStep("Stop following the file and validate the message");
		log.info("INFO: Stop following the file and validate message");
		file.following(ui);
		ui.fluentWaitTextPresent(Data.getData().StopFollowingMessage);
			
		//follow
		logger.strongStep("Follow the file and validate the message");
		log.info("INFO: Follow the file and validate message");
		file.following(ui);
		ui.fluentWaitTextPresent(Data.getData().FollowingMessage);
		
		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Unpin a file by unliking it</li>
	*<li><B>Step:</B> Select Details display button</li>	
	*<li><B>Step:</B> Upload a file via API</li>
	*<li><B>Step:</B> Pin the file</li> 
	*<li><B>Verify:</B> The file has been pinned and a message appears</li>
	*<li><B>Step:</B> Unlike the file</li>
	*<li><B>Verify:</B> The file has been unliked and is no longer pinned</li>
	*</ul>
	*/ 
	
	@Test(groups = {"regression", "bvt", "regressioncloud"})
	public void pinFile() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		BaseFile file = new BaseFile.Builder(Data.getData().file2)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();
		
		//Upload a file
		logger.strongStep("Upload a file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());
		
		//Load the component
		logger.strongStep("Load the files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
	
		//Switch the display from default Tile to Details
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		ui.clickMyFilesView();
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.weakStep("Pin the file and validate the message");
		log.info("INFO: Pin the file and validate message");
		file.pin(ui);
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().PinMessage),
						  "ERROR: Unable to find text " + Data.getData().PinMessage);
		
		//unlike file
		logger.weakStep("Unlike the file and validate the message");
		log.info("INFO: Unlike the file and validate message");
		file.pin(ui);
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().NewUnPinMessage),
				  "ERROR: Unable to find text " + Data.getData().NewUnPinMessage);	
		
		ui.endTest();
	}
	
/**
	*<ul>
	*<li><B>Info:</B> Delete a file that is in 'Trash'</li>
	*<li><B>Step:</B> Upload a file</li>
	*<li><B>Step:</B> Select Details display button</li>
	*<li><B>Step:</B> Move the file to the trash</li>
	*<li><B>Step:</B> Delete the file</li>
	*<li><B>Verify:</B> File is deleted from 'Trash'</li>
	*</ul>
	*/
	
	@Test(groups = { "level2", "bvt", "bvtcloud", "regressioncloud", "infra", "smokeonprem", "smokecloud","cnx8ui-level2"})
	public void fileMoveToTrash() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		BaseFile file = new BaseFile.Builder(Data.getData().file3)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
		
		
		ui.startTest();
		
		User testUser = cfg.getUserAllocator().getUser();

		//Upload a file
		logger.strongStep("Upload a file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());
		
		//Load the component
		logger.strongStep("Load Files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		//Switch the display from default Tile to Details
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		ui.getCloseTourScript();
		ui.clickMyFilesView();
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		//Move file to trash and verify the files is deleted
		logger.strongStep("Move file to trash");
		log.info("INFO: Move the file to trash");
		file.trash(ui);

		//Delete file
		//NOTE: The assert is in the above delete action method
		logger.strongStep("Delete the file and empty trash ");
		log.info("INFO: Delete the file and empty trash");
		file.delete(ui);
		
		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B> Create a sub-folder for files</li>
	*<li><B>Step:</B> Create a folder</li>
	*<li><B>Step:</B> Click on My folders to see your folder</li>
	*<li><B>Step:</B> Click on your folder to create a sub-folder</li>
	*<li><B>Step:</B> Create a sub-folder for your folder</li>
	*<li><B>Verify:</B>Verify a sub-folder for your folder that was created</li>
	*</ul>
	*/
	@Test(groups = {"level2", "bvt", "regressioncloud", "smokeonprem","smokecloud", "cnx8ui-level2"})
    public void createSubFolder() throws Exception{
	
	    DefectLogger logger=dlog.get(Thread.currentThread().getId());
	    String testName = ui.startTest();
	    
	    //Load the component
	    User testUser = cfg.getUserAllocator().getUser();
 	    logger.strongStep("Load files and login");
 	    ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
	    if(ui.checkNestedFolderGK()){
	    	//Simplify the step of subfolder creation,there is no need to add description for top folder
	 	    BaseFolder folder = new BaseFolder.Builder(Data.getData().FolderName + Helper.genDateBasedRand())
	 	                                      .build();
	 	    
	 	    BaseFolder subfolder = new BaseFolder.Builder(testName + Helper.genDateBasedRand())	 	                                         
	                                             .description(Data.getData().FolderDescription)
	                                             .build();
			
			//Click on My folders to see your folders
            ui.clickMyFoldersView();

	 	    log.info("INFO: Close guided tour if obscurs page element");
			driver.executeScript(ui.getCloseTourScript());
            
	 	    //create a folder
	 	    logger.strongStep("Create a new folder");
	 	    log.info("INFO: Create a new folder");
	        folder.create(ui);
	        
	        //Click on display list to show folders in list
	        logger.strongStep("Click on 'Display List'");
	        log.info("INFO: Click on Display List ");
	        ui.scrollIntoViewElement(FilesUIConstants.DisplayList);
	        ui.clickLinkWait(FilesUIConstants.DisplayList);
	         
	        //Click on your folder to create a sub-folder
	        logger.strongStep("Click on your folder");
	        log.info("INFO: Click on your folder");
	        ui.clickLinkWait(FilesUI.selectMyFolder(folder));
	         
	        //Click on the New Folder button to create subfolder
	        logger.strongStep("Click on the New Folder button to create subfolder");
	        log.info("INFO: Click on the New Folder button to create subfolder");
			ui.clickNewFolderInGlobalNewButton(false);
	        
	        //create a sub-folder for your folder
	        logger.strongStep("Create a sub-folder for your folder");
	        log.info("INFO: Create a sub-folder for your folder");
	        ui.createSubFolder(subfolder);
	         
	        //Verify the sub-folder was created
	        logger.strongStep("Verify that the sub-folder was created");
	        log.info("INFO: Verify that the sub-folder was created");
	        Assert.assertTrue(driver.isElementPresent(FilesUI.selectMyFolder(subfolder)),
					  "ERROR: Unable to find the sub folder");
	         
	        ui.endTest();
	    	
	    }else{
	    	//  Skip this test case
			log.info("INFO: nested Folder is not enabled");
			throw new SkipException("Test Create Sub Folder Is Skipped");		
	    }
	   
        
}	
    
    /**
	*<ul>
	*<li><B>Info:</B> Create a sub-folder by clicking on the folder's title dropdown menu</li>
	*<li><B>Step:</B> Create a folder</li>
	*<li><B>Step:</B> Click on My folders to see your folder</li>
	*<li><B>Step:</B> Click on your folder to create a sub-folder</li>
	*<li><B>Step:</B> Create a sub-folder by clicking on folder's title dropdown menu</li>
	*<li><B>Verify:</B>Verify a sub-folder for your folder that was created</li>
	*</ul>
	*/
    @Test(groups = {"regression", "bvt", "regressioncloud"})
    public void createSubFolderByFolderTiltleMenu() throws Exception{
	
	    DefectLogger logger=dlog.get(Thread.currentThread().getId());
	    String gk_flag = "FILES_NESTED_FOLDER";
	    
	    if(gkc.getSetting(gk_flag)){
	    	String testName = ui.startTest();
	 	    
	    	//Simplify the step of subfolder creation,there is no need to add description for top folder
	    	BaseFolder folder = new BaseFolder.Builder(Data.getData().FolderName + Helper.genDateBasedRand())
	 	                                      .build();
	 	    
	 	    BaseFolder subfolder = new BaseFolder.Builder(testName + Helper.genDateBasedRand())	 	                                         
	                                             .description(Data.getData().FolderDescription)
	                                             .build();
	 	 
	 	    //Load the component
	 	    logger.strongStep("Load files and login");
	 	    ui.loadComponent(Data.getData().ComponentFiles);
	 	    ui.login(testUser);	 	    
	         
            //Click on My folders to see your folder
            ui.clickMyFoldersView();
            
	 	    log.info("INFO: Close guided tour if obscurs page element");
			driver.executeScript(ui.getCloseTourScript());
            
	 	    //create a folder
	 	    logger.strongStep("Create a new folder");
	 	    log.info("INFO: Create a new folder");
	        folder.create(ui);

	        //Click on display list to show folders in  list
	        logger.strongStep("Click on 'Display List'");
	        log.info("INFO: Click on Display List ");
	        ui.clickLinkWait(FilesUIConstants.DisplayList);
	         
	        //Click on your folder to create a sub-folder
	        logger.strongStep("Click on your folder");
	        log.info("INFO: Click on your folder");
	        ui.clickLinkWait(FilesUI.selectMyFolder(folder));
	         
	    	//Select New Folder from the Folder's title menu
			logger.strongStep("Select New Folder from the Folder's title menu");
			log.info("INFO: Select New Folder from the Folder's title menu");
			Files_Folder_Dropdown_Menu.NEW_FOLDER.select(ui);

	        //create a sub-folder for your folder
	        logger.strongStep("Create a sub-folder by clicking on folder's title dropdown menu");
	        log.info("INFO: Create a sub-folder by clicking on folder's title dropdown menu");
	        ui.createSubFolder(subfolder);
	             
	        //Verify the sub-folder was created
	        logger.strongStep("Verify that the sub-folder was created");
	        log.info("INFO: Verify that the sub-folder was created");
	        Assert.assertTrue(driver.isElementPresent(FilesUI.selectMyFolder(subfolder)),
					  "ERROR: Unable to find the sub folder");
	         
	        ui.endTest();
	    	
	    }else{
	    	//  Skip this test case
			log.info("INFO: nested Folder is not enabled");
			throw new SkipException("Test Create Sub Folder Is Skipped");		
	    }
	   
        
}	
	
	/**
	*<ul>
	*<li><B>Info:</B> Move a file into a folder</li>
	*<li><B>Step:</B> Upload a file</li>
	*<li><B>Step:</B> Create a folder</li>
	*<li><B>Step:</B> Add file to folder</li>
	*<li><B>Step:</B> Move the file to the folder</li>
	*<li><B>Verify:</B> File is moved to the folder that was created</li>
	*</ul>
	*/
	
	@Test(groups = {"level2", "bvt", "regressioncloud", "icStageSkip","cnx8ui-level2"})
	public void fileAddToFolder() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName= ui.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
		
		BaseFolder folder = new BaseFolder.Builder(testName + Helper.genDateBasedRand())
										   .description(Data.getData().FolderDescription)
										   .build();
		
		//Upload a file
		logger.strongStep("Upload a file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());

		//Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		//Create a folder
		logger.strongStep("Create a folder");
		log.info("INFO: Creating a folder");
		folder.create(ui);

 	    log.info("INFO: Close guided tour if obscurs page element");
		driver.executeScript(ui.getCloseTourScript());
		
		//add file to folder
		logger.strongStep("Add file to the folder");
		log.info("INFO: Add file to folder");
		file.addToFolder(ui, folder);
		
		//go to My Folders view
		ui.clickMyFoldersView();
		
		//Click on your folder to view the file added
		logger.strongStep("Open the folder");
		log.info("INFO: Open the folder");
		ui.clickLinkWait(FilesUI.selectMyFolder(folder));

		//Verify the File added
		logger.weakStep("Find the file");
		log.info("INFO: Find the file");
		Assert.assertTrue(driver.isElementPresent(FilesUI.getFileIsUploaded(file)),
						  "ERROR: Unable to find File");

		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B> Upload multiple files for testing purposes</li>
	*<li><B>Step:</B> All at once, upload 3 files and rename each file</li>
	*<li><B>Verify:</B> An alert message displays stating files were successfully uploaded</li>
	*</ul>
	*/
	
	@Test(groups = { "level2", "bvt", "regressioncloud","cnx8ui-level2"})
	public void uploadMultipleFiles() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		BaseFile file = new BaseFile.Builder(Data.getData().file5)
									.rename(Helper.genDateBasedRand())
									.extension(".jpg")
									.build();
		
		ui.startTest();

		//Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		//Upload 3 files to use in this test
		logger.strongStep("Upload 3 files at once");
		log.info("INFO: Upload 3 files at once");
		ui.multipleFileUpload(3, file.getName(), file.getRename());
		
		//Validate alert message displays stating files were successfully uploaded
		logger.weakStep("Verify that an alert message displays stating files were successfully uploaded");
		log.info("INFO: Verify an alert message displays stating files were successfully uploaded");		
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().UploadMessage), "Successfully uploaded alert not found");

		ui.endTest();
	}
	

	/**
	*<ul>
	*<li><B>Info:</B> Load the Public Files Component Anonymously</li>
	*<li><B>Step:</B> Open Files URL</li>
	*<li><B>Step:</B> Load files component</li>
	*<li><B>Verify:</B> Page title contains Public Files</li>
	*<li><B>Verify:</B> Text on page says "Public Files"</li> 
	*<li><B>Verify:</B> URL redirects to Public Files</li>
	*</ul>
	*Note: The cloud does not support this and so it is not included in the regressioncloud group
	*/
	
	@Test(groups = { "level2", "mt-exclude", "bvt", "bvtonprem","cnx8ui-level2"})
	public void anonymousAccess() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Get files' public URL
		String filesURL = Data.getData().ComponentFiles.split("login")[0];
		
		ui.startTest();
		
		logger.strongStep("Load files");
		log.info("INFO: Load files component");
		ui.loadComponent(filesURL);
		
		logger.weakStep("Check that page title contains 'Public Files' ");
		log.info("Check page title contains Public Files");
		ui.fluentWaitTextPresentRefresh("Public Files");
		String pageTitle = driver.getTitle();
		Assert.assertTrue(pageTitle.contains("Public Files"), "Page title: '" + pageTitle + "', Page title did not contain Public Files");
		
		logger.weakStep("Check text on page: Public Files");
		log.info("Check Text on page: Public Files");
		Assert.assertTrue(ui.isTextPresent("Public Files"), "Public Files text not present on page.");
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Community owner uploads two files into a community</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Select Files in the left nav drop down menu</li>
	 *<li><B>Step:</B> Upload fileA</li>
	 *<li><B>Step:</B> Upload fileB</li>
	 *<li><B>Verify:</B> File upload messages are present for each file</li>
	 *<li><B>Verify:</B> The files are viewable in widget</li>
	 *</ul>
	 */
	
	@Test(groups = {"cplevel2", "level2", "bvt", "bvtcloud", "regressioncloud","cnx8ui-level2"})
	public void communityFilesUpload() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.tags(Data.getData().commonTag + rndNum)
													.commHandle(Data.getData().commonHandle + rndNum)
													.description("Test description for testcase " + testName)
													.build();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();
		
		//create community
		logger.strongStep("Create community");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//GUI
		//Load component and login
		logger.strongStep("Load communities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned community views");
		log.info("INFO: Navigate to the owned communtiy views");
		
		String gk_flag_card = "catalog-card-view";
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
				gk_flag_card = "CATALOG_CARD_VIEW";
				gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
				gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean isCardView = gkc.getSetting(gk_flag_card);
		cUI.goToDefaultIamOwnerView(isCardView);

		//navigate to the API community
		logger.strongStep("Navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);
		
		//Select Files from left menu
		log.info("INFO: Select Fiels from left navigation menu");
		Community_LeftNav_Menu.FILES.select(cUI);
		
		logger.strongStep("Add a new file A");
		log.info("INFO: Add a new file: " + fileA.getName());
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			fileA.upload(ui,gkc);
		else
			fileA.upload(ui);
		
		//validate upload message for fileA
		logger.strongStep("Validate that file upload message is present for fileA");
		log.info("INFO: Validate file upload message is present for fileA");
		if (!ui.fluentWaitTextPresent(Data.getData().UploadMessage)){
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			ui.reClickUploadLink(fileA, gkc);
			ui.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
		
		logger.strongStep("Add a new file B");
		log.info("INFO: Add a new file: " + fileB.getName());
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			fileB.upload(ui,gkc);
		else
			fileB.upload(ui);

		//validate upload message for fileB
		logger.strongStep("Validate that file upload message is present for fileB");
		log.info("INFO: Validate file upload message is present for fileB");
		if (!ui.fluentWaitTextPresent(Data.getData().UploadMessage)){
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			ui.reClickUploadLink(fileB, gkc);
			ui.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
		
		//Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.weakStep("Validate that file A is visible");
		log.info("INFO: Validate the fileA is visible");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(fileA)),
						  "ERROR: Unable to find the file " + fileA.getName());

		logger.weakStep("Validate that file B is visible");
		log.info("INFO: Validate the fileB is visible");	
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(fileB)),
				  		  "ERROR: Unable to find the file " + fileB.getName());

		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Validate that you can add a community file to a folder</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Select Apps < Files</li>
	 * <li><B>Step: </B>Select Community Files from the left navigation panel</li>
	 * <li><B>Step: </B>Upload a file into Community Files</li>
	 * <li><B>Step: </B>Create new Folder called 'BVT Folder'</li>
	 * <li><B>Step: </B>Add File to 'BVT Folder'</li>
	 * <li><B>Verify: </B>The file is in the folder</li>
	 * </ul>
	 */
	
	@Test(groups = { "level2","cnx8ui-level2", "bvt", "regressioncloud"})
	public void communityFileAddToFolder() throws Exception {
		
		String testName = ui.startTest();
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + testName + Helper.genDateBasedRand())
		 							.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		 							.tags(Data.getData().commonTag)
		 							.description("Test Gallery inside community")
		 							.build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file2)
									 .comFile(true)
									 .rename(Helper.genDateBasedRand())
									 .extension(".jpg")
									 .build();

		BaseFolder folderA = new BaseFolder.Builder(Data.getData().FolderName)
											.description(Data.getData().FolderDescription)
											.build();

		ui.startTest();

		//create community
		logger.strongStep("Create community");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Load communities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		adminUser = cfg.getUserAllocator().getAdminUser();
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
				gk_flag = "COMMUNITIES_TABBED_NAV";
				gk_flag_card = "CATALOG_CARD_VIEW";
				gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
				gkc = GatekeeperConfig.getInstance(driver);
		}
		
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		boolean value = gkc.getSetting(gk_flag);
				
		//Navigate to owned communities
		logger.strongStep("Navigate to owned community views");
		log.info("INFO: Navigate to the owned communtiy views");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		cUI.goToDefaultIamOwnerView(isCardView);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);
		
		// upload a file via API
		logger.strongStep("Upload File A");
		log.info("INFO: Upload file: " + fileA.getName());
		log.info("INFO: File message: folder: " + fileA.getFolder() + " Local : " + fileA.getFileLocal());
		uiViewer.upload(fileA, testConfig, testUser, comAPI);
		fileA.setName(fileA.getRename()+ fileA.getExtension());

		// Navigate to files widget
		if(value) {
			// Select Files from tabbed menu
			logger.strongStep("Select files from the tabbed navigation menu");
			log.info("INFO: Select Files from tabbed nav menu");
			Community_TabbedNav_Menu.FILES.select(ui);
		} else {
			// Select Files from left menu
			logger.strongStep("Select files from the left navigation menu");
			log.info("INFO: Select Files from left nav menu");
			Community_LeftNav_Menu.FILES.select(ui);
		}

		logger.strongStep("Add folder A");
		log.info("INFO: Add Folder");
		folderA.add(ui);
		
		//Switch the display from default Tile to Details
		logger.strongStep("Select the 'Details' display button");
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);

		//add file to folder
		logger.strongStep("Add file A to folder");
		log.info("INFO: Add file to folder");
		fileA.addToFolder(ui, folderA);
		
		//Click on My Folder to see your folder
		logger.strongStep("Click on 'My Folder' ");
		log.info("INFO: Click on My Folder");
		String gk_dnd_flag = "FILES_TREE_DND";
		if (gkc.getSetting(gk_dnd_flag))
			ui.clickLinkWait(FilesUIConstants.navCommunityFolders);
		else
			ui.clickLinkWait(FilesUIConstants.ComFolderTab);
	
		//Click on your folder to view the file added
		logger.strongStep("Open the folder");
		log.info("INFO: Open the folder");
		ui.clickLinkWait(FilesUI.selectMyFolder(folderA));

		//Verify the File added
		logger.weakStep("Find file A");
		log.info("INFO: Find the file");
		Assert.assertTrue(driver.isElementPresent(FilesUI.getFileIsUploaded(fileA)),
						  "ERROR: Unable to find file");

		logger.strongStep("Delete community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Create a sub-folder for community files</li>
	*<li><B>Step: </B>Create a community</li>
	*<li><B>Step:</B> Create a folder for the community</li>
	*<li><B>Step:</B> Click on Folders to see your folder</li>
	*<li><B>Step:</B> Click on your folder to create a sub-folder</li>
	*<li><B>Step:</B> Create a sub-folder for your folder</li>
	*<li><B>Verify: </B>Verify a sub-folder for your folder that was created</li>
	*</ul>
	*/
	
	@Test(groups = {"regression", "bvt", "regressioncloud"})
	public void communityCreateSubFolder() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_NESTED_FOLDER";
		String gk_dnd_flag = "FILES_TREE_DND";
		    
		if(gkc.getSetting(gk_flag)){
			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
			 							               .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
			 							               .tags(Data.getData().commonTag)
			 							               .description("Test description for testcase " + testName)
			 							               .build();

			//Simplify the step of subfolder creation,there is no need to add description for top folder
			BaseFolder folder = new BaseFolder.Builder(Data.getData().FolderName + Helper.genDateBasedRand())
											  .build();
			
			BaseFolder subfolder = new BaseFolder.Builder(testName + Helper.genDateBasedRand())
			                                     .description(Data.getData().FolderDescription)
			                                     .build();

			//create community
			logger.strongStep("Create community using API");
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
			
			logger.strongStep("Load communities and login");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);

			//Navigate to owned communities
			logger.strongStep("Navigate to owned community views");
			log.info("INFO: Navigate to the owned communtiy views");
			
			String gk_flag_card = "catalog-card-view";
			if(cfg.getProductName().equalsIgnoreCase("onprem")){
					gk_flag_card = "CATALOG_CARD_VIEW";
					gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
			} else{
					gkc = GatekeeperConfig.getInstance(driver);
			}
			boolean isCardView = gkc.getSetting(gk_flag_card);
			cUI.goToDefaultIamOwnerView(isCardView);
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(cUI);

			// Select Files from left menu
			logger.strongStep("Select files from the left navigation menu");
			log.info("INFO: Select Files from left navigation menu");
			Community_LeftNav_Menu.FILES.select(ui);

		    //Add folder
			logger.strongStep("Add a new folder");
			log.info("INFO: Add a new folder");
			folder.add(ui);
			
			//Click on My Folder to see your folder
		    logger.strongStep("Click on  Folders ");
			log.info("INFO: Click on Folders");
			if (gkc.getSetting(gk_dnd_flag))
				ui.clickLinkWait(FilesUIConstants.navCommunityFolders);
			else
				ui.clickLinkWait(FilesUIConstants.ComFolderTab);
				
			//Click on display list to show folders in list
	        logger.strongStep("Click on 'Display List'");
	        log.info("INFO: Click on Display List ");
	        ui.clickLinkWait(FilesUIConstants.DisplayList);
	         
			//Click on your folder to create a sub-folder
	        logger.strongStep("Open your folder");
			log.info("INFO: Open your folder");
			ui.clickLinkWait(FilesUI.selectMyFolder(folder));
			
			//Click on add button to show menu
	        logger.strongStep("Click on add button to show menu");
			log.info("INFO: Click on add button to show menu");
			ui.clickAddButtonInCommunityGlobal();
			
			//Select New Folder from menu
	        logger.strongStep("Select New Folder from menu");
			log.info("INFO: Select New Folder from menu");
			ui.clickLink(FilesUIConstants.NEW_FOLDER_IN_GLOBAL_ADD);

			//create a sub-folder for your folder
	        logger.strongStep("Create a sub-folder for your folder");
	        log.info("INFO: Create a sub-folder for your folder");
			ui.createSubFolder(subfolder);
			
	        //Verify the sub-folder was created
	        logger.strongStep("Verify that the sub-folder was created");
	        log.info("INFO: Verify that the sub-folder was created");
	        Assert.assertTrue(driver.isElementPresent(FilesUI.selectMyFolder(subfolder)),
					  "ERROR: Unable to find the sub folder");
	        
	        //Delete community
	    	logger.strongStep("Delete community");
	    	log.info("INFO: Delete community");
			apiOwner.deleteCommunity(comAPI);
			
			ui.endTest();
		}else{
		    //  Skip this test case
			log.info("INFO: nested Folder is not enabled");
			throw new SkipException("Test Create Community Sub Folder Is Skipped");
		}
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Create a sub-folder by clicking on folder's title dropdown menu for community files</li>
	*<li><B>Step: </B>Create a community</li>
	*<li><B>Step:</B> Create a folder for the community</li>
	*<li><B>Step:</B> Click on Folders to see your folder</li>
	*<li><B>Step:</B> Click on your folder to create a sub-folder</li>
	*<li><B>Step:</B> Create a sub-folder by clicking on folder's title dropdown menu for your folder</li>
	*<li><B>Verify: </B>Verify a sub-folder for your folder that was created</li>
	*</ul>
	*/
	
	@Test(groups = {"level2", "bvt", "regressioncloud","cnx8ui-level2"})
	public void communityCreateSubFolderByFolderTitleMenu() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String gk_flag = "FILES_NESTED_FOLDER";
		String gk_dnd_flag = "FILES_TREE_DND";
		    
		if(gkc.getSetting(gk_flag)){
			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + testName + Helper.genDateBasedRand())
			 							               .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
			 							               .tags(Data.getData().commonTag)
			 							               .description("Test description for testcase " + testName)
			 							               .build();

			//Simplify the step of subfolder creation,there is no need to add description for top folder
			BaseFolder folder = new BaseFolder.Builder(Data.getData().FolderName + Helper.genDateBasedRand())
											  .build();
			
			BaseFolder subfolder = new BaseFolder.Builder(testName + Helper.genDateBasedRand())
			                                     .description(Data.getData().FolderDescription)
			                                     .build();

			//create community
			logger.strongStep("Create community using API");
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
			
			logger.strongStep("Load communities and login");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

			//Navigate to owned communities
			logger.strongStep("Navigate to owned community views");
			log.info("INFO: Navigate to the owned communtiy views");
			String gk_flag_card = "catalog-card-view";
			if(cfg.getProductName().equalsIgnoreCase("onprem")){
					gk_flag_card = "CATALOG_CARD_VIEW";
					gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
			} else{
					gkc = GatekeeperConfig.getInstance(driver);
			}
			boolean isCardView = gkc.getSetting(gk_flag_card);
			cUI.goToDefaultIamOwnerView(isCardView);
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(cUI);

			// Select Files from left menu
			logger.strongStep("Select files from the left navigation menu");
			log.info("INFO: Select Files from left navigation menu");
			Community_LeftNav_Menu.FILES.select(ui);

		    //Add folder
			logger.strongStep("Add a new folder");
			log.info("INFO: Add a new folder");
			folder.add(ui);
			
			//Click on My Folder to see your folder
		    logger.strongStep("Click on  Folders ");
			log.info("INFO: Click on Folders");
			if (gkc.getSetting(gk_dnd_flag))
				ui.clickLinkWait(FilesUIConstants.navCommunityFolders);
			else
				ui.clickLinkWait(FilesUIConstants.ComFolderTab);
				
			//Click on display list to show folders in list
	        logger.strongStep("Click on 'Display List'");
	        log.info("INFO: Click on Display List ");
	        ui.clickLinkWait(FilesUIConstants.DisplayList);
	         
			//Click on your folder to create a sub-folder
	        logger.strongStep("Open your folder");
			log.info("INFO: Open your folder");
			ui.clickLinkWait(FilesUI.selectMyFolder(folder));
			
			//Select New Folder from the Folder's title menu
			logger.strongStep("Select New Folder from the Folder's title menu");
			log.info("INFO: Select New Folder from the Folder's title menu");
			Files_Folder_Dropdown_Menu.NEW_FOLDER.select(ui);
			
			//create a sub-folder for your folder
	        logger.strongStep("Create a sub-folder for your folder");
	        log.info("INFO: Create a sub-folder for your folder");
			ui.createSubFolder(subfolder);
			
	        //Verify the sub-folder was created
	        logger.strongStep("Verify that the sub-folder was created");
	        log.info("INFO: Verify that the sub-folder was created");
	        Assert.assertTrue(driver.isElementPresent(FilesUI.selectMyFolder(subfolder)),
					  "ERROR: Unable to find the sub folder");
	        
	        //Delete community
	    	logger.strongStep("Delete community");
	    	log.info("INFO: Delete community");
			apiOwner.deleteCommunity(comAPI);
			
			ui.endTest();
		}else{
		    //  Skip this test case
			log.info("INFO: nested Folder is not enabled");
			throw new SkipException("Test Create Community Sub Folder Is Skipped");
		}
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Upload a file to the organization</li>
	 *<li><B>Step:</B> Upload a private file</li>
	 *<li><B>Step:</B> Share the file</li>
	 *<li><B>Verify:</B> The UI message is available</li>
	 *<li><B>Verify:</B> The file uploaded and the file was shared</li>
	 *</ul>
	 */
	@Test(groups = {"level2", "regressioncloud", "bvtcloud","smokeonprem","smokecloud", "unit","cnx8ui-level2"})
	public void fileUploadAndShare() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();

		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.shareLevel(ShareLevel.NO_ONE)
									.build();
		
		//Start of test
		ui.startTest();
		
		//Upload a file
		logger.strongStep("Upload a private file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());
		
		//Load component and login
		logger.strongStep("Load files and login");
		log.info("INFO: Load component and login");	
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
						
		// change the view list format
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		ui.clickMyFilesView();
		log.info("INFO: Select Details display button");
		ui.clickLinkWait(FilesUICloud.listView);

 	    log.info("INFO: Close guided tour if obscurs page element");
		driver.executeScript(ui.getCloseTourScript());
		
		//Now select this file and then share the file
		logger.strongStep("Select the file and share it");
		log.info("INFO: Now select this file and then share the file");	
		file.share(ui);
				
		//End of test
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Upload/share a file as user A then download the file as user B</li>
	 *<li><B>Step:</B> Upload a file</li>
	 *<li><B>Step:</B> Allow public access to the file</li>
	 *<li><B>Step:</B> File was uploaded</li>
	 *<li><B>Verify:</B> Public access of shared file</li>
	 *<li><B>Step:</B> Logout as User A </li>
	 *<li><B>Step:</B> Login as User B</li>
	 *<li><B>Step:</B> Open and download file</li>
	 *<li><B>Verify:</B> File was download</li>
	 *</ul>
	 */
	@Test (groups = {"cplevel2", "level2", "regressioncloud", "bvtcloud","smokeonprem","smokecloud", "unit", "cnx8ui-level2"} )
	public void fileDownload() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.rename(testName + Helper.genDateBasedRand())
									.build();
		
		
		//Upload a file
		logger.strongStep("Upload a file via API");
		User testUser = cfg.getUserAllocator().getUser();
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());
		log.info("Uploaded file name: " + file.getRename() + file.getExtension());
		
		//GUI
		//Login
		logger.strongStep("Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		// change the view list format
		logger.weakStep("Go to My Files. Change the 'View List' format");
		ui.clickMyFilesView();
		ui.clickLinkWait(FilesUICloud.listView);
		Assert.assertTrue(driver.isElementPresent(ui.getUploadFileName(file)),
				"ERROR: File not present");

 	    log.info("INFO: Close guided tour if obscurs page element");
		driver.executeScript(ui.getCloseTourScript());
		
		//Share file with public
		logger.strongStep("Share file with public");
		log.info("INFO: Share file with public");
		ui.share(file);
		
		//Validate that file was publicly shared
		logger.weakStep("Validate that the file was shared with public");
		log.info("INFO: Check if file was publicly shared");
		Assert.assertTrue(ui.fluentWaitTextPresent("File was shared successfully"),
				  			"ERROR: File not shared");

//		//log out as user A 
//		logger.strongStep("Logout as User A");
//		log.info("INFO: Log out as user A");
//		ui.logout();
//		//ui.close(cfg); 	This line is commented to maintain single session in BS
//		
//		//log in as user B
//		logger.strongStep("Login as user B");
//		log.info("INFO: Log in as user B");
//		ui.loadComponent(Data.getData().ComponentFiles,true);
//		ui.login(testUser1);
		
		//Verify that the UI is available
		logger.strongStep("Verify that the UI is available");
		log.info("INFO: Verify the UI is Available");	
		ui.waitForPageLoaded(driver);
		ui.clickMyFilesView();
		if(!cfg.getUseNewUI())
			ui.fluentWaitTextPresent("My Files");
		
		//Check if user B can see shared folder
		logger.strongStep("Check if user B can see the file");
		log.info("INFO: Check if user B can see file");
		if(cfg.getUseNewUI())
			cUI.clickLinkWd(By.xpath(FilesUIConstants.publicFilesSecTopNav));
		else
			ui.clickLinkWait(FilesUIConstants.PublicFilesInNav);

		// change the view list format
		logger.strongStep("Change the 'View List' format");
		ui.clickLinkWait(FilesUICloud.listView);
				
		//Open and download file
		logger.strongStep("Open and download file");
		log.info("INFO: Open and download file");
		ui.download(file);
		
		//Validate successful file download
		logger.strongStep("Validate that the file was downloaded");
		log.info("INFO: Check if file was downloaded");
		ui.verifyFileDownloaded(file.getRename() + file.getExtension());

		//Logout of Wiki
		ui.endTest();	

	}
	
	
	/**********************************************************************************************************************************
	 * This is the beginning of the test cases from BVT_Cloud.All these test cases are deprecated as IBM Cloud is no longer supported *
	 **********************************************************************************************************************************/
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Create a folder and share with another org member</li>
	 *<li><B>Step:</B> Create new folder</li>
	 *<li><B>Step:</B> Share folder with the company</li>
	 *<li><B>Step:</B> Logout as User A </li>
	 *<li><B>Step:</B> Login as User B </li>
	 *<li><B>Verify:</B> Validate the creation of folder</li>
	 *<li><B>Verify:</B> Validate folder being shared</li>
	 *<li><B>Verify:</B> Validate that user B can see the folder</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void folderCreation() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Create a folder and file base state object
		BaseFolder folder = new BaseFolder.Builder(testName + Helper.genDateBasedRand())
		   								  .description(Data.getData().FolderDescription)
		   								  .build();

		//GUI
		//Login
		logger.strongStep("Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser1);

		//Create new folder
		logger.strongStep("Create a new folder");
		log.info("INFO: Create new folder");
		folder.create(ui);
		
		//Validate creation of new folder
		logger.weakStep("Verify that the folder was created");
		log.info("INFO: Check if folder was created");
		Assert.assertTrue(ui.fluentWaitTextPresent(folder.getName()),
							"ERROR: Folder not present");
		
		//Select RalphCo folders in left nav
		logger.strongStep("Select the 'folders' tab");
		log.info("INFO: Select folders tab");
		Files_LeftNav_Menu.MYFOLDERS.select(cUI);
		
		//Select the folder
		logger.strongStep("Select 'my folders'");
		log.info("INFO: Select the folder under my folders");
		ui.clickLinkWait(FilesUI.selectMyFolder(folder));
		
		//Share folder with company
		logger.strongStep("Share created folder with company");
		log.info("INFO: Share created folder");
		folder.share(ui);
		
		//verify the UI message
		logger.weakStep("Verify UI message that 'folder was shared successfully'");
		Assert.assertTrue(ui.fluentWaitTextPresent("The folder was shared successfully."),
						 "ERROR: Folder share message not found");
		
		//log out as user A
		logger.strongStep("Log out as User A");
		log.info("INFO: Log out as user A");
		ui.logout();
		ui.close(cfg);
		
		//login in as different user
		logger.strongStep("Login as User B");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser1);
		
		//Check if user B can see shared folder
		logger.strongStep("Check is User B can see the folder");
		log.info("INFO: Check if user B can see folder");
		ui.clickLinkWait("link=" + FilesUI.getOrganizationName(driver) + " Folders");	
		
		//Select the folder
		logger.strongStep("Select the folder under 'public folders'");
		log.info("INFO: Select the folder under public folders");
		ui.clickLinkWait(FilesUI.selectMyFolder(folder));

		//Logout of Wiki
		ui.endTest();

	}
	/**
	 *<ul>
	 *<li><B>Info:</B> Upload a file and share it with another User</li>
	 *<li><B>Step:</B> Upload file</li>
	 *<li><B>Step:</B> Share file with the company</li>
	 *<li><B>Step:</B> Search for file as User A</li>
	 *<li><B>Verify:</B> Validate the file upload</li>
	 *<li><B>Verify:</B> Validate file shared with user B</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void fileSearchAndTest() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file2)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
		
		//Upload a file
		logger.strongStep("Upload a file via API");	
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());
		
		//GUI
		//Login
		logger.strongStep("Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser1);
		
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		ui.clickMyFilesView();
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);

		//share the file
		logger.strongStep("Share file with User B");
		log.info("INFO: Share file with user B");
		file.share(ui);

		//Validate that file was shared
		logger.weakStep("Check if file was shared with User B");
		log.info("INFO: Check if file was shared with User B");
		Assert.assertTrue(driver.isTextPresent("The file was shared successfully."),
						  						"ERROR: File not present to User B");

		//Search for file
		logger.strongStep("Enter file name in the serach text box");
		log.info("INFO: Enter file name in the search text box");
		ui.typeText(FilesUIConstants.inputFileName, file.getName());
		
		logger.strongStep("Select the magnification icon to search");
		log.info("INFO: Select the magification icon to search");
		ui.clickLinkWait(FilesUIConstants.selectSearch);
		
		//Validate successful file search
		logger.weakStep("Validate that the file search was successful");
		log.info("INFO: Check if file was searched");
		Assert.assertTrue(ui.fluentWaitTextPresent(file.getRename() + ".jpg"),
				  "ERROR: File not searched");

		//Logout of Wiki
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Check File Function for a efss filesonly User</li>
	 *<li><B>Step:</B> Log in as the efss_filesOnly_user</li>
	 *<li><B>Step:</B> Select Apps > Files</li>
	 *<li><B>Verify:</B> Left panel displays My Files link</li>
	 *</ul>
	 */
	@Deprecated
	@Test(groups = {"smokecloud_efssUser"})
	public void testEFSSUsersLogin() throws Exception{
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		efssUser = cfg.getUserAllocator().getGroupUser("efss_filesOnly_users");
		// Start of test
		ui.startTest();

		// Load component and login
		logger.strongStep("Load files and login");
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(efssUser);

		// Verify that the UI is available
		logger.strongStep("Verify that UI is available");
		log.info("INFO: Verify the UI is Available");
		ui.waitForPageLoaded(driver);
		ui.fluentWaitTextPresent("My Drive");

		// End of test
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Search for an Existing File</li>
	 *<li><B>Step:</B> [API] Open an already existing file</li>
	 *<li><B>Step: </B>If there are no existing files, create one</li>
	 *<li><B>Step:</B> In the search bar type in the existing file name</li>
	 *<li><B>Verify:</B> The search has performed and returns information </li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud","smokecloud"} )
	public void searchExistingFile() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Testing taking place inside of searchExistingFile");
		log.info("in side of  searchExistingFile");
		boolean ret = false;
		String fileName = null;
		String uploadedFile;
		
		long currentGMTTime, fileTime;
		Feed myFilesFeed = null;
			
		// get search user
		logger.strongStep("Get search user");
		searchUser = cfg.getUserAllocator().getGroupUser("search_users");
		
		Calendar cal = Calendar.getInstance();		
		Date date = cal.getTime();		
		TimeZone tz = cal.getTimeZone();

		//Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT 
		logger.strongStep("Return the number of milliseconds since January 1, 1970, 00:00:00 GMT  ");
		long msFromEpochGmt = date.getTime();
		//gives you the current offset in ms from GMT at the current date
		logger.strongStep("Return the current offset in milliseconds from GMT at the current date");
		int offsetFromUTC = tz.getOffset(msFromEpochGmt);
		currentGMTTime = cal.getTimeInMillis() - offsetFromUTC;	
		
		FilesUI fileUI = FilesUI.getGui(cfg.getProductName(), driver);
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());		
	
		// Get File api handle	
		
		APIFileHandler apiFileOwner = new APIFileHandler(serverURL, searchUser.getEmail(), searchUser.getPassword());
	
		// connect with My Library
		logger.strongStep("Connect with 'My Library'");
		myFilesFeed = (Feed) apiFileOwner.getService().getMyLibraryFeed();

		// try to check user's file
		logger.strongStep("Search for desired file");
		if ( myFilesFeed != null )
		{	
			for (Entry e : myFilesFeed.getEntries()) {	
				log.info("INFO: getUpdated Time = " + e.getUpdatedElement());
				
				fileTime = fileUI.getFileCreatedTime( e.getUpdatedElement().toString());
			
				if ( (currentGMTTime - fileTime) > (fileSearchTime)) {
						// get the activity name which be searched for
						fileName = e.getTitle();
					}				
				} 
				// get document name from an entry
			    logger.strongStep("Get the file name from an entry");
				log.info("INFO: get file name = " + fileName);
					
				fileUI.startTest();
				
				//Load component and login
				logger.strongStep("Load Files and login");
				fileUI.loadComponent(Data.getData().ComponentFiles);
				fileUI.login(searchUser);
				
				//Wait for sametime to load if enabled
				logger.strongStep("Wait for SameTime to load if enabled");
				fileUI.waitForSameTime();
			
				if ( fileName != null) {
					fileUI.searchFile(fileName);
					log.info("INFO: Found File: " +fileName);		
				} else { 
					logger.strongStep("Upload a new file");
					log.info("INFO: Need upload a new file ");
				
					BaseFile file = new BaseFile.Builder(Data.getData().file1)
												.extension(".jpg")
												.rename(Helper.genDateBasedRand())
												.build();
					Assert.assertNotNull(file);
					
					file.upload(ui);

					//Switch the display from default Tile to Details
					logger.strongStep("Select the 'Details' display button");
					log.info("INFO: Select Details display button");
					Files_Display_Menu.DETAILS.select(ui);
					
					//Validate existence of file
					logger.weakStep("Validate that the file exists");
					fileName = ui.getUploadFileName(file);
					log.info("INFO: Check if file exists for " + fileName);
					ui.fluentWaitPresent(FilesUICloud.fileUploadedSuccessImg);
					Assert.assertTrue(driver.isElementPresent(fileName),
										"ERROR: File not present");				
					
					// css=a[title='020092656841.jpg']		
					uploadedFile = fileName.substring(fileName.indexOf("'") + 1, fileName.length() - 2); 
					APISearchHandler apiSearch = new APISearchHandler(serverURL, searchUser.getEmail(), searchUser.getPassword());
				
					ui.sleep(newFileWaitingTime);
				
					// wait for 20 minutes
					logger.strongStep("Wait for 20 minutes to make sure search index finishes");
					ret= apiSearch.waitForIndexer("files", uploadedFile, 20) ;
						
					if ( ret) {
						log.info("*** Got apiSearch back ***");
						driver.getSingleElement(FilesUICloud.SearchTextArea).clear();
						ui.sleep(newFileWaitingTime);
						ui.searchFile(uploadedFile);
						driver.getSingleElement(FilesUICloud.SearchButton).click();
						ui.fluentWaitPresent(FilesUICloud.SearchResult + uploadedFile + "']");
							
						Assert.assertTrue(driver.isElementPresent(FilesUICloud.SearchResult + uploadedFile + "']"),
										" Found file for " + uploadedFile);		
					}
					else
						Assert.assertTrue(ret,
								"Index time out for file: " + uploadedFile);		
			}	
				ui.endTest();
		} else {
			    //  Skip this test case
			log.info("INFO: myFilesFeed got NULL");
			throw new SkipException("Test search Existin File Is Skipped");		
		}
	}


	/**
	 *<ul>
	 *<li><B>Info:</B> Check File Function for a Guest User</li>
	 *<li><B>Step:</B> Log in as a guest user</li>
	 *<li><B>Step:</B> Select Apps > Files</li>
	 *<li><B>Verify:</B> Left panel displays My Files link</li>
	 *<li><B>Verify:</B> Left panel displays Shared With Me link</li>
	 *<li><B>Verify:</B> Left panel displays My Folders link</li>
	 *</ul>
	 */
	@Deprecated
	@Test(groups = {"regressioncloud", "bvtcloud","smokecloud"})
	public void guestUserFile() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		guestUser = cfg.getUserAllocator().getGuestUser();
		//Start of test
		ui.startTest();
		
		//Load component and login
		logger.strongStep("Load files and login");
		log.info("INFO: Load component and login");	
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(guestUser);
		
		//Verify that the UI is available
		logger.strongStep("Verify that UI is available");
		log.info("INFO: Verify the UI is Available");	
		ui.waitForPageLoaded(driver);
		ui.fluentWaitTextPresent("My Drive");
		
		// Verify my files link
		logger.weakStep("Validate that the 'My Files' link is present");
		log.info("INFO: Check My Files Link");	
		ui.fluentWaitPresent(FilesUIConstants.MyFilesInNav);
		Assert.assertTrue( driver.isElementPresent(FilesUIConstants.MyFilesInNav),
						"ERROR: My Drive Link is not present.");
		
		// Verify SharedWithMe Link
		logger.weakStep("Validate that the 'Shared with Me' link is present");
		log.info("INFO: Check Shared With Me Link");	
		ui.fluentWaitPresent(FilesUIConstants.filesSharedWithMe);
		Assert.assertTrue( driver.isElementPresent(FilesUIConstants.filesSharedWithMe),
						"ERROR:  files Shared With Me Link is not present.");
		
		// Verify my folder link
		logger.weakStep("Validate that the 'My Folder' link is present");
		log.info("INFO: Check My Folder Link");	
		ui.fluentWaitPresent(FilesUIConstants.MyFolderLink);
		Assert.assertTrue( driver.isElementPresent(FilesUIConstants.MyFolderLink),
				"ERROR: My Folder Link is not present.");
				
		//End of test
		ui.endTest();		
	}
}
