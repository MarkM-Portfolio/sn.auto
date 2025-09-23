package com.ibm.conn.auto.tests.files.regression;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.FilesUI.Container;
import com.ibm.conn.auto.webui.FilesUI.FileType;
import com.ibm.conn.auto.webui.FilesUI.FilesListView;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;


public class FileViewer_Regression extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FileViewer_Regression.class);
	private FilesUI ui;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;	
	private User testUser;
	private APICommunitiesHandler apiCommunityOwner;
	private APIFileHandler apiFileOwner;
	private String filePath ;
	private Community community;
	private File file ;
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiCommunityOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		

	}
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}	

	/**
	 * <ul>
	 * <li><B>Info:</B>File Viewer Test Case : checkFileViewerBasicElementsPresent_Files_DetailPage</li>
	 * <li><B>Step:</B></li>
	 * <li><B>Step:</B>1. Upload a file to Files App by API </li>
	 * <li><B>Step:</B>2. Open file viewer from file details page </li>
	 * <li><B>Verify:</B>  basic elements in file viewer : </li>
	 * <li><B>Verify:</B>1)  file name in top banner</li>
	 * <li><B>Verify:</B>2)  the four panels exist </li>
	 * <li><B>Verify:</B>3)  file current version , file name, file size and creator match the expected results </li>   
	 * </ul>
	 */
	@Test(groups = {"regression"})
	public void checkFileViewerBasicElementsPresent_Files_DetailPage(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file2)
				.extension(".jpg")
				.shareLevel(ShareLevel.EVERYONE)
				.rename(ui.reName(Data.getData().file2))
				.build();
		uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		logger.strongStep("Close guided tour popup if exists");
		log.info("Close guided tour popup if exists");
		cUI.closeGuidedTourPopup();
		
		logger.strongStep("Click on my files menu option from lefet side");
		log.info("Click on my files menu option from lefet side");
		ui.clickLinkWait(FilesUIConstants.myfiles);
		
		logger.strongStep("Info : Verify file viewer is opened or not");
		if(!ui.openFileViewerByView(FilesListView.DETAILS, baseFileImage, FileType.IMAGE)){
			log.error("ERROR : Open file viewer failed");
			return ;
		}
		//Put additional expected VPs into hashmap because BaseFile does not include all attributes we need to verify , overwrite BaseFile will take much effort, so just use hashmap here 
		Map<String,String> expectedResults= new HashMap<String, String>();
		expectedResults.put(Data.FILEVIEWER_CURRENTVERSION, "1");
		expectedResults.put(Data.FILEVIEWER_SIZE, "650 KB");
		expectedResults.put(Data.FILEVIEWER_CREATED, testUser.getDisplayName());
		checkBasicElementsInFileViewer(FileType.IMAGE, baseFileImage, expectedResults);
		
		ui.closeFileViewer();
		
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>File Viewer Test Case: checkFileViewerBasicElementsPresent_CommunityFiles_DetailPage</li>
	 * <li><B>Step:</B></li>
	 * <li><B>Step:</B>1. Create a community by community API </li>
	 * <li><B>Step:</B>2. Upload a file to this community by Files API </li>
	 * <li><B>Step:</B>3. Open file viewer from file details page </li>
	 * <li><B>Verify:</B>4.  basic elements in file viewer : </li>
	 * <li><B>Verify:</B>1)  file name in top banner</li>
	 * <li><B>Verify:</B>2)  the four panels exist </li>
	 * <li><B>Verify:</B>3)  file current version , file name, file size and creator match the expected results </li>
	 * </ul>
	 */
	@Test(groups = {"regression"})
	public void checkFileViewerBasicElementsPresent_CommunityFiles_DetailPage(){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file3)
				.extension(".jpg")
				.shareLevel(ShareLevel.EVERYONE)
				.rename(ui.reName(Data.getData().file3))
				.build();
		uploadFilesToContainerAndLoadToMainPage(Container.COMMUNITYFILES, baseFileImage);
		
		logger.strongStep("Info : Verify file viewer is opened or not");
		if(!ui.openFileViewerByView(FilesListView.DETAILS, baseFileImage, FileType.IMAGE)){
			log.error("ERROR : Open file viewer failed");
			return ;
		}
		
		//Put additional expected VPs into hashmap because BaseFile does not include all attributes we need to verify , overwrite BaseFile will take much effort, so just use hashmap here 
		Map<String,String> expectedResults= new HashMap<String, String>();
		expectedResults.put(Data.FILEVIEWER_CURRENTVERSION, "1");
		expectedResults.put(Data.FILEVIEWER_SIZE, "922 KB");
		expectedResults.put(Data.FILEVIEWER_CREATED, testUser.getDisplayName());
		checkBasicElementsInFileViewer(FileType.IMAGE, baseFileImage, expectedResults);
		
		ui.closeFileViewer();
		
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>File Viewer Test Case : addCommentInFileViewer
	 * <li><B>Step:</B></li>
	 * <li><B>Step:</B>1. Upload a file to Files App by API </li>
	 * <li><B>Step:</B>2. Open file viewer from file details page </li>
	 * <li><B>Step:</B>3. Add comment in file viewer </li>
	 * <li><B>Verify:</B> Comment is added successfully.</li>
	 * </ul>
	 */
	@Test(groups = {"regression"})
	public void addCommentInFileViewer(){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file6).extension(".jpg").shareLevel(ShareLevel.EVERYONE).rename(ui.reName(Data.getData().file6)).build();
		uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		logger.strongStep("Close guided tour popup if exists");
		log.info("Close guided tour popup if exists");
		cUI.closeGuidedTourPopup();
		
		logger.strongStep("Click on my files menu option from lefet side");
		log.info("Click on my files menu option from lefet side");
		ui.clickLinkWait(FilesUIConstants.myfiles);
		
		logger.strongStep("INFO : Verify if file viewer is opened or not");
		if(!ui.openFileViewerByView(FilesListView.DETAILS, baseFileImage, FileType.IMAGE)){
			log.error("ERROR : Open file viewer failed");
			return ;
		}
		Assert.assertTrue(ui.fileViewer_addComment(),"ERROR : Add comment in file viewer failed ");
		
		ui.closeFileViewer();
		
		ui.endTest();		
	}


	/**
	 * File Viewer invoked method : uploadFilesToContainerAndLoadToMainPage
	 * Upload files to Files App and Community Files Widget by API , launch browser and load to Files app main page or community file widget app main page
	 * @param container Files APP or Community Files Widget 
	 * @param baseFiles Upload any number of files 
	 * @return return created FileEntry list 
	 */
	public List<FileEntry> uploadFilesToContainerAndLoadToMainPage(Container container , BaseFile... baseFiles) {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		List<FileEntry> fileEntries = new ArrayList<FileEntry> ();
		FileEntry fileEntry ;
		if(container == Container.FILES){
			
			//Upload files by Files API in Files APP 
			for(BaseFile baseFile : baseFiles){
				filePath = "resources/" + baseFile.getName();
				
				logger.strongStep("INFO: Upload file " + baseFile.getName()+ " to Files APP by Files API and rename to: " + baseFile.getRename() + " filePath :" + filePath);
				log.info("INFO: Upload file " + baseFile.getName()+ " to Files APP by Files API and rename to: " + baseFile.getRename() + " filePath :" + filePath);
				file = new File(filePath);
				fileEntry = baseFile.createAPI(apiFileOwner, file);
				
				//Notice: if no this changePermission , the file is still private status even if set baseFile sharelevel as everyone .
				apiFileOwner.changePermissions(baseFile, fileEntry);   
				fileEntries.add(fileEntry);
			} 
			
			logger.strongStep("INFO: To load and navigate to Files APP main page ");
			log.info("INFO: To load and navigate to Files APP main page ");
			ui.loadComponent(Data.getData().ComponentFiles);
			ui.replaceProductionCookies();
			ui.login(testUser);
			
		}else if(container == Container.COMMUNITYFILES){
			//Create one community by Community API 
			BaseCommunity baseCommunity = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
			   										   .access(Access.PUBLIC)
			   										   .description("test file viewer" )
			   										   .build();
			
			
			logger.strongStep("Create Community " + baseCommunity.getName() + " by Community API");
			log.info("Create Community " + baseCommunity.getName() + " by Community API");
			community = baseCommunity.createAPI(apiCommunityOwner);
			
			if (community == null) {
				log.info("ERROR: Fail to create community ");
				return null;
			}
			
			logger.strongStep("INFO : Upload files to community " + baseCommunity.getName());
			log.info("INFO : Upload files to community " + baseCommunity.getName());
			
			for (BaseFile baseFile : baseFiles) {
				filePath = "resources/" + baseFile.getName();
				
				logger.strongStep("filePath :" + filePath);
				log.info("filePath :" + filePath);
				file = new File(filePath);
				fileEntry = apiFileOwner.CreateFile(baseFile, file, community);
				apiFileOwner.changePermissions(baseFile, fileEntry);
				fileEntries.add(fileEntry);
			}
			
			logger.strongStep("INFO: Generate community UUID ");
			log.info("INFO: Generate community UUID ");
			baseCommunity.getCommunityUUID_API(apiCommunityOwner, community);
			
			logger.strongStep("INFO: To load and navigate to the community by UUID ");
			log.info("INFO: To load and navigate to the community by UUID ");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			baseCommunity.navViaUUID(cUI);
			
			//Load to the Community File Widget 
			logger.strongStep("INFO : Navigate to community files widget");
			log.info("INFO : Navigate to community files widget");
			Community_LeftNav_Menu.FILES.select(ui);
		}
		return fileEntries;
		
	}

	/**
	 * File Viewer invoked method : checkBasicElementsInFileViewer : Check Basic elements present and match the expected results 
	 * @param fileType
	 * @param baseFile
	 * @param expectedResults
	 */
	public void checkBasicElementsInFileViewer(FileType fileType, BaseFile baseFile, Map<String, String> expectedResults){

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("INFO : Start to the verification of basic elements in file viewer");
		log.info("INFO : Start to the verification of basic elements in file viewer");
		String fileName = baseFile.getRename() + baseFile.getExtension();
		
		logger.strongStep("INFO : Verify top banner file title and panels title ");
		log.info("INFO : Verify top banner file title and panels title ");
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.fileviewer_previewLinkTitle).getText(), fileName, "ERROR: File Viewer banner title should match the file title");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileviewer_panelAboutThisFile), "ERROR: File viewer panel title should include " + Data.FILEVIEWER_ABOUTTHISFILE);
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileviewer_panelComment),  "ERROR: File viewer panel title should include " + Data.FILEVIEWER_COMMENTS);
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileviewer_panelShareWith), "ERROR: File viewer panel title should include " + Data.FILEVIEWER_SHARED_WITH);
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileviewer_panelVersion),  "ERROR: File viewer panel title should include " + Data.FILEVIEWER_VERSIONS);
		
		logger.strongStep("INFO : Go to About this File panel to verify current version , file name , size , creator ");
		log.info("INFO : Go to About this File panel to verify current version , file name , size , creator ");
		ui.clickLinkWait(FilesUIConstants.fileviewer_panelAboutThisFile);
		Assert.assertTrue(driver.getSingleElement(FilesUIConstants.fileviewer_panelCurrentVersion).getText().contains(expectedResults.get(Data.FILEVIEWER_CURRENTVERSION)),"ERROR: File viewer panel current version should match the expected version");
		
		// Permit file sizes to be off by 1 kB
		String expectedFileSize = expectedResults.get(Data.FILEVIEWER_SIZE);
		if (expectedFileSize != null && !expectedFileSize.isEmpty()) {
			Pattern fileSizePattern = Pattern.compile("([0-9]+) KB");
			Matcher fileSizeMatcher = fileSizePattern.matcher(expectedFileSize);
			if (fileSizeMatcher.matches() && fileSizeMatcher.groupCount() > 0) {
				int fileSize = -1;
				try {
					fileSize = Integer.parseInt(fileSizeMatcher.group(1));
				} catch (NumberFormatException e) {
				}
				if (fileSize > 0) {
					String fileSizeElementContents = driver.getSingleElement(FilesUIConstants.fileviewer_fileSize_CurrentVersion).getText();
					String expectedSize1 = (fileSize - 1) + " KB";
					String expectedSize2 = fileSize + " KB";
					String expectedSize3 = (fileSize + 1) + " KB";
					Assert.assertTrue(fileSizeElementContents.contains(expectedSize1)
								      || fileSizeElementContents.contains(expectedSize2)
								      || fileSizeElementContents.contains(expectedSize3),
								      "ERROR: File viewer panel file size is incorrect. Actual size is " + fileSizeElementContents +
								      ", expected size is " + expectedSize1 + " to " + expectedSize3);
				}
			} else {
				Assert.assertTrue(driver.getSingleElement(FilesUIConstants.fileviewer_fileSize_CurrentVersion).getText().contains(expectedResults.get(Data.FILEVIEWER_SIZE)),"ERROR: ERROR: File viewer panel file size should match the created size");
			}
		}
		Assert.assertTrue(driver.getSingleElement(FilesUIConstants.fileviewer_createdBy).getText().contains(expectedResults.get(Data.FILEVIEWER_CREATED)),"ERROR: File viewer panel creator should match the created person");
		
		logger.strongStep("INFO : End verification of basic elements in file viewer");
		log.info("INFO : End verification of basic elements in file viewer");
	}
	
}
