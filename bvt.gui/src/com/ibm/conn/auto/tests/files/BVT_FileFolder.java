package com.ibm.conn.auto.tests.files;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_FileFolder extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Files.class);
	private FilesUI ui;
	private TestConfigCustom cfg;	
	private User testUser;
	private APIFileHandler folderOwner;
	private CommunitiesUI Cui;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		Cui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		folderOwner = new APIFileHandler(serverURL, testUser.getEmail(), testUser.getPassword());
	}
	
	/**
	* folderPanelDefaultFolderViews() 
	*<ul>
	*<li><B>Info:</B> Validate the default folder views appear in the Folder Panel</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Verify:</B> The folder panel icon displays</li>
	*<li><B>Verify:</B> There is no 'All Folder' link</li>
	*<li><B>Verify:</B> The 'Folder' link appears</li>
	*<li><B>Step:</B> Click on the 'Folder' link to expand the view</li>
	*<li><B>Verify:</B> The 'Pinned Folders' link appears</li>
	*<li><B>Verify:</B> The 'Share With Me' link appears</li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/1FBDB0B897837DFC852582CB0081A2E3">TTT - NAVIGATE WITH LESS CLICKS (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void folderPanelDefaultFolderViews() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		driver.changeImplicitWaits(5);
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			logger.strongStep("If the guided tour pop-up dialog appears, close it");
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			Cui.closeGuidedTourPopup();
		}
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Verify the Folder Panel icon displays and Selected");
		log.info("INFO: Verify the Folder Panel icon displays");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.folderNavClosePanel),
				"The folder panel icon got appear");
				
		log.info("INFO: Verify the Folder Panel icon is selected");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.folderNavClosePanel+"[class='actionOn']"),
				"The folder panel icon is selected");
		
		//This section will verify the default folders in the left panel
		driver.changeImplicitWaits(2);
		logger.strongStep("Verify the'All Folder' link no longer appears");
		log.info("INFO: Verify the'All Folder' link no longer appears");
		Assert.assertFalse(driver.isElementPresent(FilesUIConstants.AllFoldersLeftMenu),
				"The 'All Folder' link no longer appears");
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Verify the 'Folder' link appears");
		log.info("INFO: Verify the 'Folder' link appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.FoldersLeftMenu),
				"'Folder' link appears on the folder panel");

		logger.strongStep("Click on the Folder link to expand the view");
		log.info("INFO: Click on the Folder link to expand the view");
		ui.clickLinkWait(FilesUIConstants.FoldersLeftMenu);

		logger.strongStep("Verify the 'Pinned Folder' link appears");
		log.info("INFO: Verify the 'Pinned Folder' link appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.PinnedFoldersLeftMenu),	
				"'Pinned Folder' link appears on the folder panel");

		logger.strongStep("Verify the 'Shared With Me' link appears");
		log.info("INFO: Verify the 'Shared With Me' link appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.SharedWithMeInNav),
				"'Shared With Me' link appears on the folder panel");

		ui.endTest();
	}
	
	/**
	* expandCollapseMyDriveView() 
	*<ul>
	*<li><B>Info:</B> Validate the ability to expand/collapse My Drive</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Verify:</B> The 'My Drive' view is collapsed by default </li>
	*<li><B>Step:</B> Expand 'My Drive' view - click on twisty </li>
	*<li><B>Verify:</B> 'My Drive' view is now expanded </li>
	*<li><B>Step:</B> Collapse 'My Drive' view</li>
	*<li><B>Verify:</B> 'My Drive' view is collapsed again</>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/1FBDB0B897837DFC852582CB0081A2E3">TTT - NAVIGATE WITH LESS CLICKS (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void expandCollapseMyDriveView() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		driver.changeImplicitWaits(5);
		logger.strongStep("Check for the guided tour pop-up dialog.");
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			Cui.closeGuidedTourPopup();
		}
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Verify 'My Drive' view is collapsed by default");
		log.info("INFO: Verify 'My Drive' view is collapsed by default");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileMyDriveExpand),
				"'My Drive' view is collapsed by default");

		logger.strongStep("Expand My Drive");
		log.info("INFO: Expand My Drive");
		ui.clickLinkWait(FilesUIConstants.fileMyDriveExpand);

		logger.strongStep("Verify 'My Drive' view is expanded");
		log.info("INFO: Verify 'My Drive' view is expanded");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileMyDriveCollapse),
				"'My Drive' view is expanded");	   
		
		logger.strongStep("Click on 'My Drive' to collapse the view");
		log.info("INFO: Click on 'My Drive' to collapse the view");
		ui.clickLinkWait(FilesUIConstants.fileMyDriveCollapse);
		
		logger.strongStep("Verify 'My Drive' view is collapsed again");
		log.info("INFO: Verify 'My Drive' view is collapsed again");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileMyDriveExpand),
				"'My Drive' view is collapsed again");		

		ui.endTest();
	}
	/**
	* expandCollapseFoldersView() 
	*<ul>
	*<li><B>Info:</B> Validate the ability to expand/collapse Folders</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Verify:</B> The 'Folders' view is collapsed by default </li>
	*<li><B>Step:</B> Expand 'Folders' view - click on twisty </li>
	*<li><B>Verify:</B> 'Folders' view is now expanded </li>
	*<li><B>Step:</B> Collapse 'Folders' view</li>
	*<li><B>Verify:</B> 'Folders' view is collapsed again</>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/1FBDB0B897837DFC852582CB0081A2E3">TTT - NAVIGATE WITH LESS CLICKS (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void expandCollapseFoldersView() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		//Load the component
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Check for the guided tour pop-up dialog.");
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Verify the 'Folders' view is collapsed by default");
		log.info("INFO: Verify the 'Folders' view is collapsed by default");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileFolderExpand),
				"The 'Folders' view is collapsed by default");

		logger.strongStep("Expand the Folders view");
		log.info("INFO: Expand the Folders view");
		ui.clickLinkWait(FilesUIConstants.FoldersLeftMenu);

		logger.strongStep("Verify 'Folders' view is expanded");
		log.info("INFO: Verify 'Folders' view is expanded");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileFolderCollapse),
				"The 'Folders' view is expanded");	   
		
		logger.strongStep("Collapse the Folders view");
		log.info("INFO: Collapse the Folders view");
		ui.clickLinkWait(FilesUIConstants.fileFolderCollapse);
		
		logger.strongStep("Verify 'Folders' view is collapsed again");
		log.info("INFO: Verify 'Folders' view is collapsed again");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileFolderExpand),
				"The 'Folders' view is collapsed again");		

		ui.endTest();
	}
	
	
	/**
	* addFolderFromMyDrive() 
	*<ul>
	*<li><B>Info:</B> Select My Drive and add a folder: folder appears in My Drive & Folders views</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Step:</B> Click on My Drive view </li>
	*<li><B>Step:</B> Create a folder</li>
	*<li><B>Verify:</B> The successfully created message displays </li>
	*<li><B>Step:</B> Get a list of the folders </li>
	*<li><B>Verify:</B> The personal folder appears twice - it will appear in both My Drive & Folders views</li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/1FBDB0B897837DFC852582CB0081A2E3">TTT - NAVIGATE WITH LESS CLICKS (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void addFolderFromMyDriveView() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		int folderCount=0;
		String folderName="MyDriveTestFolder"+ Helper.genDateBasedRand();
		String folderDescription="folder created from My Drive view - automation test";
		
		BaseFolder folder = new BaseFolder.Builder(folderName)
				.description(folderDescription)
				.build();
		
		//Load the component
		logger.strongStep("Login and load File Component.");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
				
		driver.changeImplicitWaits(5);
		logger.strongStep("Check for the guided tour pop-up dialog.");
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){

			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			Cui.closeGuidedTourPopup();
		}
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Select MyDrive and create a Folder.");
		log.info("INFO: Select the 'My Drive' view");
		ui.clickLinkWait(FilesUIConstants.fileMyDrive);
		
		log.info("INFO: Creating a folder");
		folder.create(ui,true);
		  
		logger.strongStep("Verify the successfully created message appears");
		log.info("INFO: Verify the successfully created message appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileFolderCreatedMsg), "The successfully created message appear");
		 		
		
		log.info("INFO: Collect the list of folders in the Folders view");
		List<Element> listOfFolders = collectListOfFolders();

		logger.strongStep("Verify and Compare the personal folder name with list of folders returned");
		log.info("INFO: Verify the personal folder appears in the view");
		log.info("INFO: Compare the personal folder name with list of folders returned");
		for (Element tempfolder : listOfFolders) {
			String title = tempfolder.getText().trim();			
			log.info("INFO: Personal folder name: <"+folderName+">,folder in the view: <"+title+">");
			if (folderName.equals(title))
				folderCount++;					
		}
		log.info("INFO: Folder "+ folderName +" was found "+ folderCount +" times.");
		
		Assert.assertEquals(folderCount,1,
		     "Incorrect folderCount. Expected 1 actual, but was "+ folderCount);
				
		ui.endTest();
	}
	
	
	/**
	* addFolderFromFoldersView() 
	*<ul>
	*<li><B>Info:</B> Select Folders view & add a folder: folder appears in Folders view</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Step:</B> Click on Folders</li>
	*<li><B>Step:</B> Create a folder</li>
	*<li><B>Verify:</B> The successfully created message displays </li>
	*<li><B>Step:</B> Get a list of the folders </li>
	*<li><B>Verify:</B> The personal folder appears once - it will appear in the Folders view</li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/85258425005wic2A03C/A3B1F5A7FAF7FB158525703C006F870C/1FBDB0B897837DFC852582CB0081A2E3">TTT - NAVIGATE WITH LESS CLICKS (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void addFolderFromFoldersView() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testname = ui.startTest();
		
		int folderCount=0;
		String folderName="Folder"+ testname + Helper.genStrongRand();
		String folderDescription="folder created as part of an automation test";
		
		BaseFile baseFolder = new BaseFile.Builder(folderName)
				.shareLevel(ShareLevel.NO_ONE)
				.description(folderDescription)
				.build();
		
		//Create the BaseFolder instance of the same private folder
		
		logger.strongStep("Create a new private folder as its owner");
		log.info("INFO: Create a new private folder as its owner");
		FileEntry folderentry=folderOwner.createFolder(baseFolder, Role.OWNER);
		
		//Load the component
		logger.strongStep("Login and Load Files Component");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
				
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){

			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Select Folders link and Validate the Created Folder is Present.");
		log.info("INFO: Click on 'Folders' to select the view");
		ui.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu+":contains(Folders)");

		log.info("INFO: Collect the list of folders in the Folders view");
		List<Element> listOfFolders = collectListOfFolders();

		//verifying that the personal folder appears once (Folders views)
		for (Element tempfolder : listOfFolders) {
			String title = tempfolder.getText().trim();
			log.info("INFO: folderName <"+folderName+"> title <"+title+">");
			if (folderName.equals(title))
				folderCount++;					
		}
		log.info("INFO: Folder "+ folderName +" was found "+ folderCount +" times.");
		
		Assert.assertEquals(folderCount,1,
		     "Folder Count is 1 ");
		
		logger.strongStep("Delete the folder");
		log.info("INFO: Delete the folder");
		folderOwner.deleteFile(folderentry);
		
		ui.endTest();
	}
	
	
	/**
	* closeFolderPanel
	*<ul>
	*<li><B>Info:</B> Validate the ability to close the folder panel</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Verify:</B> The folder nav panel appears by default </li>
	*<li><B>Verify:</B> The 'Folders' link appears </li>
	*<li><B>Step:</B> Close the folder nav panel</li>
	*<li><B>Verify:</B> The folder nav panel no longer appears </li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/1FBDB0B897837DFC852582CB0081A2E3">TTT - NAVIGATE WITH LESS CLICKS (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void closeFolderPanel() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
						
		//Load the component
		logger.strongStep("Login and Load File Component.");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
				
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){

			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Verify the folder nav panel appears by default");
		log.info("INFO: Verify the folder nav panel appears by default");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.folderNavPanel),
				"The folder nav panel appear by default");
		
		logger.strongStep("Verify the 'Folders' link appears");
		log.info("INFO: Verify the 'Folders' link appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.FoldersLeftMenu),
				"The 'Folder' link appears on the folder panel");
		
		logger.strongStep("Close the folder nav panel");
		log.info("INFO: Close the folder nav panel");
		ui.clickLinkWait(FilesUIConstants.folderNavClosePanel);
		
		driver.changeImplicitWaits(2);
		logger.strongStep("Verify the folder nav panel no longer appears");
		log.info("INFO: Verify the folder nav panel no longer appears");
		Assert.assertFalse(driver.isElementPresent(FilesUIConstants.folderNavPanel),
				"The folder nav panel no longer appears");
		driver.turnOnImplicitWaits();
		ui.endTest();
	}
		
	/**
	* defaultFilterPanelViews() 
	*<ul>
	*<li><B>Info:</B> Select the filter panel icon and verify the views on the panel</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Verify:</B> The filter panel icon displays</li>
	*<li><B>Step:</B> Click on the filter icon</li>
	*<li><B>Verify:</B> 'Public Tags' displays</li>
	*<li><B>Verify:</B> 'Date Updated' displays</li>
	*<li><B>Verify:</B> 'Shared' displays</li>
	*<li><B>Verify:</B> 'Role' displays</li>
	*<li><B>Verify:</B> 'Created By' displays</li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/1FBDB0B897837DFC852582CB0081A2E3">TTT - NAVIGATE WITH LESS CLICKS (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void defaultFilterPanelViews() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		//Load the component
		logger.strongStep("Login and Load Files Component");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Verify the Filter icon displays");
		log.info("INFO: Verify the Filter icon displays");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineView),
				"The filter icon appear");
				
		logger.strongStep("Click on the Filter icon");
		log.info("INFO: Click on the Filter icon");
		ui.clickLinkWait(FilesUIConstants.fileRefineView);

		logger.strongStep("Verify the 'Public Tags' view appears");
		log.info("INFO: Verify the 'Public Tags' view appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOption.replaceAll("PLACEHOLDER", "Public Tags")),
				"'Public Tags' view appear");
		
		logger.strongStep("Verify the 'Date Updated' view appears");
		log.info("INFO: Verify the 'Date Updated' view appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOption.replaceAll("PLACEHOLDER", "Date Updated")),
				"'Date Updated' view appear");

		logger.strongStep("Verify the 'Shared' view appears");
		log.info("INFO: Verify the 'Shared' view appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeftOption+":contains(Shared)"),
				"'Shared' view appear");

		logger.strongStep("Verify the 'Role' view appears");
		log.info("INFO: Verify the 'Role' view appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOption.replaceAll("PLACEHOLDER", "Role")),
				"'Role' view appear");
		
		logger.strongStep("Verify the 'Created By' view appears");
		log.info("INFO: Verify the 'Created By' view appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOption.replaceAll("PLACEHOLDER", "Created By")),
				"'Created By' view appear");

		ui.endTest();
	}
	
	/**
	* filterPanelViewsAfterSelectingFolders() 
	*<ul>
	*<li><B>Info:</B> Click on Folders and then switch to the filter panel - verify views</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Step:</B> Click on Folders</li>
	*<li><B>Step:</B> Click on the filter icon</li>
	*<li><B>Verify:</B> 'Public Tags' link is not clickable</li>
	*<li><B>Verify:</B> 'Date Updated' link is not clickable</li>
	*<li><B>Step:</B> Expand the 'Shared' view</li>
	*<li><B>Verify:</B> 'With everyone (public)' view appears</li>
	*<li><B>Verify:</B> 'With one or more people' view appears</li>
	*<li><B>Verify:</B> 'With no one (private)' view appears</li>
	*<li><B>Verify:</B> 'Role' view appears</li>
	*<li><B>Verify:</B> 'Created By' view appears</li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/1FBDB0B897837DFC852582CB0081A2E3">TTT - NAVIGATE WITH LESS CLICKS (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void filterPanelViewsAfterSelectingFolders() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		//Load the component
		logger.strongStep("Login and Load Files component");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Click on the 'Folders' link");
		log.info("INFO: Click on the 'Folders' link");
		ui.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu+":contains(Folders)");
						
		logger.strongStep("Click on the Filter icon");
		log.info("INFO: Click on the Filter icon");
		ui.clickLinkWait(FilesUIConstants.fileRefineView);	
		
		logger.strongStep("Verify 'Public Tags' link is not clickable");
		log.info("INFO: Verify 'Public Tags' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Public Tags")),
				"Public Tags link is not clickable");	
		
		logger.strongStep("Verify 'Date Updated' link is not clickable");
		log.info("INFO: Verify 'Date Updated' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Date Updated")),
				"Date Updated link is not clickable");	
					
		logger.strongStep("Expand the Shared view");
		log.info("INFO: Expand the Shared view");
		ui.clickLinkWait(FilesUIConstants.fileRefineViewLeftOption+":contains(Shared)");
		
		logger.strongStep("Verify the 'With everyone (public)' view appears in the Shared view");
		log.info("INFO: Verify the 'With everyone (public)' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With everyone (public))"),
				"The 'With everyone (public)' view appear");
		
		logger.strongStep("Verify the 'With one or more people' view appears in the Shared view");
		log.info("INFO: Verify the 'With one or more people' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With specific people)"),
				"'With specific people' view appear");

		logger.strongStep("Verify the 'With no one (private)' view appears in the Shared view");
		log.info("INFO: Verify the 'With no one (private)' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With no one (private))"),
				"'With no one (private)' view appear");
		
		logger.strongStep("Verify 'Role' link is not clickable");
		log.info("INFO: Verify 'Role' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Role")),
				"'Role' link is not clickable");	
		
		logger.strongStep("Verify 'Created By' link is not clickable");
		log.info("INFO: Verify 'Created By' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Created By")),
				"'Created By' link is not clickable");	
		 

		ui.endTest();
	}
	
	/**
	* filterPanelViewsAfterSelectingMyFiles() 
	*<ul>
	*<li><B>Info:</B> Click on My Files and then switch to the filter panel - verify views</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Step:</B> Click on the 'My Files' link</li>
	*<li><B>Step:</B> Click on the filter icon</li>
	*<li><B>Verify:</B> 'Tags' appears</li>
	*<li><B>Verify:</B> 'Tags' view is expanded</li>
	*<li><B>Verify:</B> 'Date Updated' appears </li>
	*<li><B>Step:</B> Click on the 'Date Updated' link to expand it</li>
	*<li><B>Verify:</B> 'Today' appears in the Date Updated section</li>
	*<li><B>Verify:</B> 'Last 7 days' appears in the Date Updated section</li>
	*<li><B>Verify:</B> 'Last 30 days' appears in the Date Updated section</li>
	*<li><B>Verify:</B> 'Last 365 days' appears in the Date Updated section</li>
	*<li><B>Step:</B> Expand the 'Shared' view</li>
	*<li><B>Verify:</B> 'With everyone (public)' appears in the Shared section</li>
	*<li><B>Verify:</B> 'With one or more people' appears in the Shared section</li>
	*<li><B>Verify:</B> 'With no one (private)' appears in the Shared section</li>
	*<li><B>Verify:</B> 'Role' link is not clickable</li>
	*<li><B>Verify:</B> 'Created By' link is not clickable</li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/1FBDB0B897837DFC852582CB0081A2E3">TTT - NAVIGATE WITH LESS CLICKS (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void filterPanelViewsAfterSelectingMyFiles() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());	
		ui.startTest();
		
		//Load the component
		logger.strongStep("Login and Load Files component");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Click on the 'My Files' Linka and Filter Icon");
		log.info("INFO: Click on the 'My Files' link");
		ui.clickMyFilesView();
								
		log.info("INFO: Click on the Filter icon");
		ui.clickLinkWait(FilesUIConstants.fileRefineView);	
		
		logger.strongStep("Verify 'Tags' link , 'Tags' section and 'Date Updated' link appears");
		log.info("INFO: Verify 'Tags' link appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileTags),
				"Tags link appear");	
		
		log.info("INFO: Verify the 'Tags' section is expanded");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileTagsCollapse),
				"ERROR The 'Tags' section is expanded");
		
		log.info("INFO: Verify 'Date Updated' link appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileDatestatus+":contains(Date Updated)"),
				"Date Updated link is clickable");
		
		logger.strongStep("Click on the Date Updated link to expand the section");
		log.info("INFO: Click on the Date Updated link to expand the section");
		ui.clickLinkWait(FilesUIConstants.fileDatestatus+":contains(Date Updated)");
		
		logger.strongStep("Verify the option 'Today' appears in the Date Updated section");
		log.info("INFO: Verify the option 'Today' appears in the Date Updated section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Today)"),
				"'Today' appear in the Date Updated section");
		
		logger.strongStep("Verify the option 'Last 7 days' appears in the Date Updated section");
		log.info("INFO: Verify the option 'Last 7 days' appears in the Date Updated section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 7 days)"),
				"'Last 7 days' appear in the Date Updated section");
		
		logger.strongStep("Verify the option 'Last 30 days' appears in the Date Updated section");
		log.info("INFO: Verify the option 'Last 30 days' appears in the Date Updated section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 30 days)"),
				"'Last 30 days' appear in the Date Updated section");
		
		logger.strongStep("Verify the option 'Last 365 days' appears in the Date Updated section");
		log.info("INFO: Verify the option 'Last 365 days' appears in the Date Updated section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 365 days)"),
				"'Last 365 days' appear in the Date Updated section");
					
		log.info("INFO: Expand the Shared view");
		ui.clickLinkWait(FilesUIConstants.fileRefineViewLeftOption+":contains(Shared)");
		
		log.info("INFO: Verify the 'With everyone (public)' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With everyone (public))"),
				"The 'With everyone (public)' view appear");
		
		log.info("INFO: Verify the 'With one or more people' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With one or more people)"),
				"'With specific people' view appear");

		log.info("INFO: Verify the 'With no one (private)' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With no one (private))"),
				"'With no one (private)' view appear");
		
		log.info("INFO: Verify 'Role' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Role")),
				"Role link is not clickable");	
		
		log.info("INFO: Verify 'Created By' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Created By")),
				"Created By link is not clickable");	
		 

		ui.endTest();
	}
	
	/**
	* filterPanelViewsAfterSelectingPinnedFiles() 
	*<ul>
	*<li><B>Info:</B> Click on Pinned Files and then switch to the filter panel - verify views</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Step:</B> Click on Pinned Files</li>
	*<li><B>Step:</B> Click on the filter icon</li>
	*<li><B>Verify:</B> 'Public Tags' is expanded </li>
	*<li><B>Step:</B> Click on 'Date Updated' to expand the view </li>
	*<li><B>Step:</B> Click on the 'Date Updated' link to expand it</li>
	*<li><B>Verify:</B> 'Today' appears in the Date Updated section</li>
	*<li><B>Verify:</B> 'Last 7 days' appears in the Date Updated section</li>
	*<li><B>Verify:</B> 'Last 30 days' appears in the Date Updated section</li>
	*<li><B>Verify:</B> 'Last 365 days' appears in the Date Updated section</li>
	*<li><B>Step:</B> Expand the 'Shared' view</li>
	*<li><B>Verify:</B> 'With everyone (public)' appears in the Shared section</li>
	*<li><B>Verify:</B> 'With one or more people' appears in the Shared section</li>
	*<li><B>Verify:</B> 'With no one (private)' appears in the Shared section</li>
	*<li><B>Verify:</B> 'Role' link is not clickable</li>
	*<li><B>Verify:</B> 'Created By' link is not clickable</li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/1FBDB0B897837DFC852582CB0081A2E3">TTT - NAVIGATE WITH LESS CLICKS (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void filterPanelViewsAfterSelectingPinnedFiles() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		//Load the component
		logger.strongStep("Login and Load Files Component");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Click on the 'Pinned Files' link and Filter Icon");
		log.info("INFO: Click on the 'Pinned Files' link");
		ui.clickLinkWait(FilesUIConstants.filePinnedFile);
								
		log.info("INFO: Click on the Filter icon");
		ui.clickLinkWait(FilesUIConstants.fileRefineView);	
				
		logger.strongStep("Verify the 'Public Tags' section is expanded");
		log.info("INFO: Verify the 'Public Tags' section is expanded");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.filePublicTagCollapse),
				"The 'Tags' section is expanded");
		
		logger.strongStep("Click on the Date Updated link to expand the section");
		log.info("INFO: Click on the Date Updated link to expand the section");
		ui.clickLinkWait(FilesUIConstants.fileDatestatus+":contains(Date Updated)");
		
		logger.strongStep("Verify the option 'Today' appears in the Date Updated section");
		log.info("INFO: Verify the option 'Today' appears in the Date Updated section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Today)"),
				"'Today' appear in the Date Updated section");
		
		logger.strongStep("Verify the option 'Last 7 days' appears in the Date Updated section");
		log.info("INFO: Verify the option 'Last 7 days' appears in the Date Updated section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 7 days)"),
				"'Last 7 days' appear in the Date Updated section");
		
		logger.strongStep("Verify the option 'Last 30 days' appears in the Date Updated section");
		log.info("INFO: Verify the option 'Last 30 days' appears in the Date Updated section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 30 days)"),
				"'Last 30 days' appear in the Date Updated section");
		
		log.info("INFO: Verify the option 'Last 365 days' appears in the Date Updated section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 365 days)"),
				"'Last 365 days' appear in the Date Updated section");
					
		log.info("INFO: Expand the Shared view");
		ui.clickLinkWait(FilesUIConstants.fileRefineViewLeftOption+":contains(Shared)");
		
		logger.strongStep("Verify the 'With everyone (public)' , 'With one or more people' , 'Role' and 'Created By' view appears in the Shared view");
		log.info("INFO: Verify the 'With everyone (public)' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With everyone (public))"),
				"The 'With everyone (public)' view appear");
		
		log.info("INFO: Verify the 'With one or more people' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With one or more people)"),
				"'With specific people' view appear");

		log.info("INFO: Verify the 'With no one (private)' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With no one (private))"),
				"'With no one (private)' view appear");
		
		log.info("INFO: Verify 'Role' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Role")),
				"'Role' link is not clickable");	
		
		log.info("INFO: Verify 'Created By' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Created By")),
				"'Created By' link is not clickable");		
		 

		ui.endTest();
	}
	
	/**
	* filterPanelViewsAfterSelectingFilesSharedWithMe() 
	*<ul>
	*<li><B>Info:</B> Click on Files Shared With Me and then switch to the filter panel - verify views</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Step:</B> Click on 'Shared With Me'</li>
	*<li><B>Step:</B> Click on the filter icon</li>
	*<li><B>Verify:</B> 'Public Tags' section is expanded</li>
	*<li><B>Verify:</B> 'Date Shared' link appears</li>
	*<li><B>Step:</B> Click on 'Date Shared' to expand the view</li>
	*<li><B>Verify:</B> 'Today' appears in the Date Updated section</li>
	*<li><B>Verify:</B> 'Last 7 days' appears in the Date Updated section</li>
	*<li><B>Verify:</B> 'Last 30 days' appears in the Date Updated section</li>
	*<li><B>Verify:</B> 'Last 365 days' appears in the Date Updated section</li>
	*<li><B>Step:</B> Expand the 'Shared' view</li>
	*<li><B>Verify:</B> 'With everyone (public)' appears in the Shared section</li>
	*<li><B>Verify:</B> 'With one or more people' appears in the Shared section</li>
	*<li><B>Verify:</B> 'With no one (private)' appears in the Shared section</li>
	*<li><B>Verify:</B> 'Role' link is not clickable</li>
	*<li><B>Verify:</B> 'Created By' link is not clickable</li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/74A2CBE3CFBD6C14852582CC0015ADEF">TTT - REFINE SIMPLIFIED - PANEL DEDICATED TO FILTERING (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void filterPanelViewsAfterSelectingFilesSharedWithMe() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		ui.startTest();
		
		//Load the component
		logger.strongStep("Login and Load File Component");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Click on the 'Files Shared With Me' link and Filter Icon");
		log.info("INFO: Click on the 'Files Shared With Me' link");
		ui.clickLinkWait(FilesUIConstants.filesSharedWithMe);
								
		log.info("INFO: Click on the Filter icon");
		ui.clickLinkWait(FilesUIConstants.fileRefineView);	
		
		logger.strongStep("Verify the 'Public Tags' section is expanded");
		log.info("INFO: Verify the 'Public Tags' section is expanded");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.filePublicTagCollapse),
				"ERROR The 'Tags' section is expanded");
		
		logger.strongStep("Verify 'Date Shared' link appears");
		log.info("INFO: Verify 'Date Shared' link appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileDatestatus+":contains(Date Shared)"),
				"Date Shared link is clickable");
		
		logger.strongStep("Click on the Date Shared link to expand the section");
		log.info("INFO: Click on the Date Shared link to expand the section");
		ui.clickLinkWait(FilesUIConstants.fileDatestatus+":contains(Date Shared)");
		
		logger.strongStep("Verify the option 'Today' appears in the Date Shared section");
		log.info("INFO: Verify the option 'Today' appears in the Date Shared section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Today)"),
				"'Today' appear in the Date Shared section");
		
		logger.strongStep("Verify the option 'Last 7 days' appears in the Date Shared section");
		log.info("INFO: Verify the option 'Last 7 days' appears in the Date Shared section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 7 days)"),
				"'Last 7 days' appear in the Date Shared section");
		
		logger.strongStep("Verify the option 'Last 30 days' appears in the Date Shared section");
		log.info("INFO: Verify the option 'Last 30 days' appears in the Date Shared section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 30 days)"),
				"'Last 30 days' appear in the Date Shared section");
		
		logger.strongStep("Verify the option 'Last 365 days' appears in the Date Shared section");
		log.info("INFO: Verify the option 'Last 365 days' appears in the Date Shared section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 365 days)"),
				"'Last 365 days' appear in the Date Shared section");
			
		logger.strongStep("Expand the Shared view");
		log.info("INFO: Expand the Shared view");
		ui.clickLinkWait(FilesUIConstants.fileSharedViewExpand);
		
		logger.strongStep("Verify the 'With an organization' , 'With one or more people'  , 'By a specific person:' view appears in the Shared view");
		log.info("INFO: Verify the 'With an organization' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With an organization)"),
				"The 'With an organization' view appear");
		
		log.info("INFO: Verify the 'With one or more people' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(With one or more people)"),
				"'With specific people' view appear");

		log.info("INFO: Verify the 'By a specific person:' view appears in the Shared view");
		Assert.assertTrue(driver.isElementPresent("css=span[class='lotusLeft'][title*='by a specific person']:contains(By a specific person:)"),
				"'By a specific person:' view appear");
		
		logger.strongStep("Verify 'Role' and 'Created By' link is not clickable");
		log.info("INFO: Verify 'Role' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeftOption+":contains(Role)"),
				"Role link is not clickable");	
		
		log.info("INFO: Verify 'Created By' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Created By")),
				"Created By link is not clickable");	
		 

		ui.endTest();
	}
	
	/**
	* filterPanelViewsAfterSelectingCommunityFile() 
	*<ul>
	*<li><B>Info:</B> Click on Community Files and then switch to the filter panel - verify views</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Step:</B> Click on Community Files folder</li>
	*<li><B>Step:</B> Click on the filter icon</li>
	*<li><B>Verify:</B> 'Public Tags' is expanded</li>
	*<li><B>Verify:</B> 'Date Updated' is not clickable</li>
	*<li><B>Verify:</B> 'Shared' is not clickable</li>
	*<li><B>Verify:</B> 'Role' is not clickable</li>
	*<li><B>Verify:</B> 'Created By' is not clickable</li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/74A2CBE3CFBD6C14852582CC0015ADEF">TTT - REFINE SIMPLIFIED - PANEL DEDICATED TO FILTERING (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void filterPanelViewsAfterSelectingCommunityFiles() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());	
		ui.startTest();
		
		//Load the component
		logger.strongStep("Login and Load Files component");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Click on the 'Community Files' link and click on Filter Icon");
		log.info("INFO: Click on the 'Community Files' link");
		ui.clickLinkWait(FilesUIConstants.fileCommunityFileLink);
								
		log.info("INFO: Click on the Filter icon");
		ui.clickLinkWait(FilesUIConstants.fileRefineView);	
		
		logger.strongStep("Verify the 'Public Tags' section is expanded");
		log.info("INFO: Verify the 'Public Tags' section is expanded");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.filePublicTagCollapse),
				"The 'Tags' section is expanded");
		
		logger.strongStep("Verify 'Date Updated' , 'Shared , 'Role','Created By' link is not clickable");
		log.info("INFO: Verify 'Date Updated' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Date Updated")),
				"Date Updated link is not clickable");
		
		log.info("INFO: Verify 'Shared' link is not clickable");
		ui.clickLinkWait(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Shared"));
						
		log.info("INFO: Verify 'Role' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Role")),
				"Role link is not clickable");	
		
		log.info("INFO: Verify 'Created By' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Created By")),
				"Created By link is not clickable");	
		 

		ui.endTest();
	}
	
	/**
	* filterPanelViewsAfterSelectingPublicFiles() 
	*<ul>
	*<li><B>Info:</B> Click on Public Files and then switch to the filter panel - verify views</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Step:</B> Click on the Public Files folder</li>
	*<li><B>Step:</B> Click on the filter panel icon</li>
	*<li><B>Verify:</B> 'Public Tags' appears</li>
	*<li><B>Verify:</B> 'Public Tags' is expanded </li>
	*<li><B>Step:</B> Click on 'Public Tags' to collapse the section</li>
	*<li><B>Verify:</B> 'Public Tags' is now collapsed</li>
	*<li><B>Verify:</B> 'Date Created' appears</li>
	*<li><B>Step:</B> Click on 'Date Created' to expand it</li>
	*<li><B>Verify:</B> The following appear after expanding 'Date Created': Today, Last 7 days, Last 30 days, and Last 365 days</li>
	*<li><B>Verify:</B> 'Share' is not clickable - grayed out</li>
	*<li><B>Verify:</B> 'Role' is not clickable - grayed out</li>
	*<li><B>Verify:</B> 'Created By' is not clickable - grayed out</li>
	*<li><a HREF="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/74A2CBE3CFBD6C14852582CC0015ADEF">TTT - REFINE SIMPLIFIED - PANEL DEDICATED TO FILTERING (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void filterPanelViewsAfterSelectingPublicFiles() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		//Load the component
		logger.strongStep("Login and Load Files Component");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Click on the 'Public Files' link and click Filter Icon");
		log.info("INFO: Click on the 'Public Files' link");
		ui.clickLinkWait(FilesUIConstants.filePublicFileLink);
								
		log.info("INFO: Click on the Filter icon");
		ui.clickLinkWait(FilesUIConstants.fileRefineView);	
		
		//this section tests 'Public Tags':
		logger.strongStep("Verify 'Public Tags' link appears");
		log.info("INFO: Verify 'Public Tags' link appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.filePublicTag),
				"'Public Tags' link appear");	
		
		logger.strongStep("Verify the 'Public Tags' section is expanded");
		log.info("INFO: Verify the 'Public Tags' section is expanded");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.filePublicTagCollapse),
				"'Public Tags' section is expanded");
		
		logger.strongStep("Click on 'Public Tags' link to collapse it");
		log.info("INFO: Click on 'Public Tags' link to collapse it");
		ui.clickLinkWait(FilesUIConstants.filePublicTag);
		
		logger.strongStep("Verify 'Public Tags' is now collapsed");
		log.info("INFO: Verify 'Public Tags' is now collapsed");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.filePublicTagsExpand),
				"'Public Tags' link is collapsed");
		
		//this section tests 'Date Created': 
		logger.strongStep("Verify 'Date Created' link appears");
		log.info("INFO: Verify 'Date Created' link appears");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeftOption+":contains(Date Created)"),
				"'Date Created' link appear");
		
		logger.strongStep("Click on 'Date Created' to expand it");
		log.info("INFO: Click on 'Date Created' to expand it");
		ui.clickLinkWait(FilesUIConstants.fileRefineViewLeftOption+":contains(Date Created)");
		
		logger.strongStep("Verify the option 'Today' , 'Last 7 days' , 'Last 30 days' , 'Last 365 days' appears in the 'Date Created' section");
		log.info("INFO: Verify the option 'Today' appears in the 'Date Created' section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Today)"),
				"'Today' appear in the Date Created section");
		
		log.info("INFO: Verify the option 'Last 7 days' appears in the 'Date Created' section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 7 days)"),
				"'Last 7 days' appear in the Date Created section");
		
		log.info("INFO: Verify the option 'Last 30 days' appears in the 'Date Created' section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 30 days)"),
				"'Last 30 days' appear in the Date Created section");
		
		log.info("INFO: Verify the option 'Last 365 days' appears in the 'Date Created' section");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewLeft+":contains(Last 365 days)"),
				"'Last 365 days' appear in the Date Created section");
		
		//this section verifies that the Shared, Role and Created By links are not selectable: 
		logger.strongStep("Verify 'Shared' link is not clickable");
		log.info("INFO: Verify 'Shared' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Shared")),
				"Share link is not clickable");
						
		logger.strongStep("Verify 'Role' , 'Created By' link is not clickable");
		log.info("INFO: Verify 'Role' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Role")),
				"'Role' link is not clickable");	
		
		log.info("INFO: Verify 'Created By' link is not clickable");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileRefineViewOptionDisabled.replaceAll("PLACEHOLDER", "Created By")),
				"'Created By' link is not clickable");		
		 

		ui.endTest();
	}
	
	/**
	* noFilterPanelIconDisabledForTrash() 
	*<ul>
	*<li><B>Info:</B> Verify that there is no filter icon when Trash folder is selected</li>
	*<li><B>Step:</B> Login to stand-alone Files</li>
	*<li><B>Step:</B> Check to see if the guided tour pop-up dialog displays</li>
	*<li><B>Step:</B> If the guided tour dialog displays, close it</li>
	*<li><B>Step:</B> Click on the 'Trash' folder</li>
	*<li><B>Verify:</B> The Filter icon is disabled/grayed out.</li>
	*<li><a href="Notes://cooper.domino.cwp.pnp-hcl.com/852584250052A03C/A3B1F5A7FAF7FB158525703C006F870C/74A2CBE3CFBD6C14852582CC0015ADEF">TTT - REFINE SIMPLIFIED - PANEL DEDICATED TO FILTERING (D78)</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void noFilterPanelIconDisabledForTrash() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		//Load the component
		logger.strongStep("Login ans Load File Component");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		log.info("Check for the guided tour pop-up dialog.");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			
			log.info("INFO: If the guided tour pop-up dialog appears, close it");
			driver.changeImplicitWaits(5);
			Cui.closeGuidedTourPopup();
			driver.turnOnImplicitWaits();
		}
		
		logger.strongStep("Click on the 'Trash' link");
		log.info("INFO: Click on the 'Trash' link");
		ui.clickLinkWait(FilesUIConstants.fileTrashLink);
					
		logger.strongStep("Verify the filter icon is not selectabled/disabled");
		log.info("INFO: Verify the filter icon is not selectabled/disabled");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileFilterIconDisabled),
				"The filter view icon is not selectabled/disabled");
		
		
		ui.endTest();
	}
	
	public List<Element> collectListOfFolders() {

		// collect list of folders
		List<Element> folderList = driver
				.getElements(FilesUIConstants.fileFolderList);
		log.info("INFO: Folders = " + folderList.size());
						
		// Log each folder for debug purposes
		for (Element folder : folderList) {
			String title = folder.getText();
			log.info("INFO: Folder " + title + " is listed");
		}
		
		return folderList;

	}
	
	
	/**
	 * This method will collect a list of folders in the Folders view.  NOTE: folders display twice because 
	 * they also appear in the My Drive view.
	 * If the 'doclick' param is set to true the subcommunity will be selected.
	 * 
	 * 
	* @param folder - list of folders in the Folder view
	 * @param folderName - folder to select
	 * @param doclick - input true if you want to click on the folder; otherwise, just return the element
	 * @return - selected folder or null if folder is not found
	 */
	public Element selectFolder(List<Element> folder, String folderName, boolean doclick) {
		Element returnedfolder = null;
		for (int i = 0; i < folder.size(); i++) {
			if (folder.get(i).getText().equals(folderName)) {
				returnedfolder = folder.get(i);
				if (doclick) returnedfolder.click();
				break;
			}

		}

		return returnedfolder;
	}
}

	

