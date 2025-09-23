package com.ibm.conn.auto.tests.fileviewer.regression;

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

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.role.FilesRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.FileViewer_Panel_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FileViewerUI.DocType_DocsViewerSupported;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.FilesUI.Container;
import com.ibm.conn.auto.webui.FilesUI.FileType;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;



public class FileViewer_Regression extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FileViewer_Regression.class);
	private FileViewerUI ui;
	private CommunitiesUI cUI;
	private FilesUI filesUI;
	private TestConfigCustom cfg;	
	private User testUser;
	private User testLookAheadUser1;
	private APICommunitiesHandler apiCommunityOwner; 
	private APIFileHandler apiFileOwner;
	private Community community;
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		log.info("Info: username  "+testUser.getEmail());
		testLookAheadUser1 = cfg.getUserAllocator().getUser();

		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiCommunityOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	
	}
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui=FileViewerUI.getGui(cfg.getProductName(), driver);
		filesUI=FilesUI.getGui(cfg.getProductName(), driver);
	}	

	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test Case : testBasicElementsPresent</li>
	 * <li><B>Step:</B> Upload a file to Files App by API </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open FiDO from file list view </li>
	 * <li><B>Verify:</B>  basic elements in FiDO : </li>
	 * <li><B>Verify:</B>1)  file name in top banner</li>
	 * <li><B>Verify:</B>2)  the four panels exist </li>
	 * <li><B>Verify:</B>3)  file current version , file name, file size and creator match the expected results </li>   
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testBasicElementsPresent(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file2)
				                             .extension(".jpg")
				                             .shareLevel(ShareLevel.EVERYONE)
				                             .rename(ui.changeName(Data.getData().file2))
				                             .build();
		
		log.info("INFO: Upload the file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		//Put additional expected VPs into hashmap because BaseFile does not include all attributes we need to verify , overwrite BaseFile will take much effort, so just use hashmap here 
		Map<String,String> expectedResults= new HashMap<String, String>();
		expectedResults.put(Data.FILEVIEWER_CURRENTVERSION, "1");
		expectedResults.put(Data.FILEVIEWER_SIZE, "587 KB");
		expectedResults.put(Data.FILEVIEWER_CREATED, testUser.getDisplayName());
		checkBasicElementsInFiDO(FileType.IMAGE, baseFileImage, expectedResults);
		
		log.info("INFO: Close FiDO");
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test Case: testBasicElementsPresent_CommunityFiles</li>
	 * <li><B>Step:</B></li>
	 * <li><B>Step:</B>1. Create a community by community API </li>
	 * <li><B>Step:</B>2. Upload a file to this community by Files API </li>
	 * <li><B>Step:</B>3. Open FiDO from file list view </li>
	 * <li><B>Verify:</B>4.  basic elements in FiDO : </li>
	 * <li><B>Verify:</B>1)  file name in top banner</li>
	 * <li><B>Verify:</B>2)  the four panels exist </li>
	 * <li><B>Verify:</B>3)  file current version , file name, file size and creator match the expected results </li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testBasicElementsPresent_CommunityFiles(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file3)
				.extension(".jpg")
				.shareLevel(ShareLevel.EVERYONE)
				.rename(ui.changeName(Data.getData().file3))
				.build();
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.COMMUNITYFILES, baseFileImage);
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		//Put additional expected VPs into hashmap because BaseFile does not include all attributes we need to verify , overwrite BaseFile will take much effort, so just use hashmap here 
		Map<String,String> expectedResults= new HashMap<String, String>();
		expectedResults.put(Data.FILEVIEWER_CURRENTVERSION, "1");
		expectedResults.put(Data.FILEVIEWER_SIZE, "823 KB");
		expectedResults.put(Data.FILEVIEWER_CREATED, testUser.getDisplayName());
		checkBasicElementsInFiDO(FileType.IMAGE, baseFileImage, expectedResults);
		ui.close();
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test Case : testCommentsPanel_AddComment </li>
	 * <li><B>Step:</B> Upload a file to Files App by API </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open FiDO from file list view </li>
	 * <li><B>Step:</B> Add comment in FiDO </li>
	 * <li><B>Verify:</B> Comment is added successfully.</li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testCommentsPanel_AddComment(){
		ui.startTest();
		BaseFile baseFileImage= new BaseFile.Builder(Data.getData().file6)
		                                    .extension(".jpg")            
		                                    .shareLevel(ShareLevel.EVERYONE)
		                                    .rename(ui.changeName(Data.getData().file6)).build();
		
		log.info("INFO: Upload the file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}	    
		
		log.info("INFO: Add comment to in FiDO");
		Assert.assertTrue(ui.addComments(Data.getData().commonComment),"ERROR : Add comment in FiDO failed ");
		
		log.info("INFO: Close FiDO");
		ui.close();
		
		log.info("Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test Case : testAboutPanel_EditDescription_AddTags </li>
	 * <li><B>Step:</B> Upload a file to Files App by API </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open FiDO from file list view </li>
	 * <li><B>Step:</B> Edit Description of the file </li>
	 * <li><B>Verify:</B> Edit Description works correctly </li>
	 * <li><B>Step:</B> Add Tags of the file </li>
	 * <li><B>Verify:</B> Add Tags of the file works correctly</li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testAboutPanel_EditDescription_AddTags(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData()
				                             .file6).extension(".jpg")
				                             .shareLevel(ShareLevel.EVERYONE)
				                             .rename(ui.changeName(Data.getData().file6)).build();
		
		log.info("INFO: Upload file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		log.info("INFO: Open About Panel of FiDO");
		FileViewer_Panel_Menu.ABOUT.select(ui);
		
		log.info("INFO: Verify Edit Description on About Panel");		
		String descriptionText = Data.getData().commonDescription + "_" + Helper.genDateBasedRand();
		Assert.assertTrue(ui.editDescription(descriptionText),"ERROR : Edit Description failed ");
		
		log.info("INFO: Verify Add tags on About Panel");		
		String tags = "tag1 tag2 tag3";
		Assert.assertTrue(ui.addTags(tags),"ERROR : add tag failed ");
		
		log.info("INFO: Close FiDO");
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();		
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario: FiDO Test Case : testAboutPanel_Description_SaveCancelButtonsExist </B></li>
	 * <li><B>Info:</B> Test to make sure the Save & Cancel buttons exist, functionality added in iteration D54 </li>
	 * <li><B>Step:</B> Upload a file to Files App by API </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open FiDO from file list view </li>
	 * <li><B>Verify:</B> Verify the Add a description link exists </li>
	 * <li><B>Step:</B> Click on the Add a description link </li>
	 * <li><B>Verify:</B> Verify both the Save and Cancel links display </li>
	 * <li><B>Step:</B> Close the fileviewer/FiDO </li>
	 * <li><B>Step:</B>> Cleanup: delete the uploaded file via API </li>
	 * <li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD176ACDBC9B767985257FD5004A81AC">TTT- File Viewer - FILE VIEWER - MY FILES - SIDEBAR - ABOUT THIS FILE - DESCRIPTION(D54)</a></li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testAboutPanel_Description_SaveCancelButtonsExist(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file8)
		                                     .extension(".jpg")
		                                     .shareLevel(ShareLevel.EVERYONE)
		                                     .rename(ui.changeName(Data.getData().file8)).build();
		
		log.info("INFO: Upload file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		log.info("INFO: Open About Panel of FiDO");
		FileViewer_Panel_Menu.ABOUT.select(ui);
		
		log.info("INFO: Verify the Add a description link displays");
		Assert.assertTrue(driver.isElementPresent(FileViewerUI.AddADescriptionLink),
				"Error: The Add a description link does not display");
		
		log.info("INFO: Click on the Add a description link");
		ui.clickLinkWait(FileViewerUI.AddADescriptionLink);
		
		log.info("INFO: Verify the Save button displays");
		Assert.assertTrue(driver.isElementPresent(FileViewerUI.SaveDescriptionButton), 
				"Error: Save button does not display");
		
		log.info("INFO: Verify the Cancel button displays");
		Assert.assertTrue(driver.isElementPresent(FileViewerUI.CancelDescriptionButton), 
				"Error: Cancel button does not display");

		log.info("INFO: Close FiDO");	
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();		
	}
	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario: FiDO Test Case : testAboutPanel_Description_CancelDescription </B></li>
	 * <li><B>Info:</B> Test to make sure Cancel button works correctly </li>
	 * <li><B>Step:</B> Upload a file to Files App by API </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open FiDO from file list view </li>
	 * <li><B>Step:</B> Click on the Add a description link </li>
	 * <li><B>Step:</B> Add some text to the description field & Cancel </li>
	 * <li><B>Verify:</B> Verify the text is NOT Saved to the description field </li>
	 * <li><B>Step:</B> Close the fileviewer/FiDO </li>
	 * <li><B>Step:</B>> Cleanup: delete the uploaded file via API </li>
	 * <li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD176ACDBC9B767985257FD5004A81AC">TTT- FILE VIEWER - MY FILES - SIDEBAR - ABOUT THIS FILE - DESCRIPTION(D54)</a></li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testAboutPanel_AddADescription_CancelDescription(){
		ui.startTest();
		
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file8)
		                                     .extension(".jpg")
		                                     .shareLevel(ShareLevel.EVERYONE)
		                                     .rename(ui.changeName(Data.getData().file8)).build();
		
		log.info("INFO: Upload file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		log.info("INFO: Open About Panel of FiDO");
		FileViewer_Panel_Menu.ABOUT.select(ui);
		
		log.info("INFO: Add content to the Description field");
		String descriptionText = Data.getData().commonDescription + "_" + Helper.genDateBasedRand();
		
		log.info("INFO: Verify the Add a description link displays");
		Assert.assertTrue(driver.isElementPresent(FileViewerUI.AddADescriptionLink),
				"Error: The Add a description link does not display");
		
		log.info("INFO: Click on the Add a description link");
		ui.clickLinkWait(FileViewerUI.AddADescriptionLink);
		
		log.info("INFO: Click on on the description input field");
		ui.fluentWaitElementVisible(FileViewerUI.DescriptionInputBox);
		Element descriptionInput = ui.getFirstVisibleElement(FileViewerUI.DescriptionInputBox);
		descriptionInput.click();
		
		log.info("INFO: Input the Description");
		descriptionInput.type(descriptionText);
		
		log.info("INFO: Click on the Cancel button");
		ui.clickLinkWait(FileViewerUI.CancelDescriptionButton);
		
		log.info("INFO: Verify the text entered into the description field was not save, should not appear");
		Assert.assertFalse(driver.isTextPresent(descriptionText),
				"ERROR: Description text appears, but should not");
		
		log.info("INFO: Close FiDO");	
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();		
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario: FiDO Test Case : testAboutPanel_Description_SaveDescription </B></li>
	 * <li><B>Info:</B> Test to make sure Save button works correctly </li>
	 * <li><B>Step:</B> Upload a file to Files App by API </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open FiDO from file list view </li>
	 * <li><B>Step:</B> Click on the Add a description link </li>
	 * <li><B>Step:</B> Add some text to the description field & Save </li>
	 * <li><B>Verify:</B> Verify the text is Saved to the description field </li>
	 * <li><B>Step:</B> Close the fileviewer/FiDO </li>
	 * <li><B>Step:</B>> Cleanup: delete the uploaded file via API </li>
	 * <li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD176ACDBC9B767985257FD5004A81AC">TTT- FILE VIEWER - MY FILES - SIDEBAR - ABOUT THIS FILE - DESCRIPTION(D54)</a></li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testAboutPanel_AddADescription_SaveDescription(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file8)
		                                     .extension(".jpg")
		                                     .shareLevel(ShareLevel.EVERYONE)
		                                     .rename(ui.changeName(Data.getData().file8)).build();
				
		log.info("INFO: Upload file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		log.info("INFO: Open About Panel of FiDO");
		FileViewer_Panel_Menu.ABOUT.select(ui);
		
		log.info("INFO: Add content to the description field");
		String descriptionText = Data.getData().commonDescription + "_" + Helper.genDateBasedRand();
		
		log.info("INFO: add content to the description field & Save.  Verify content is Saved");
		Assert.assertTrue(ui.editDescription(descriptionText),
				"ERROR : Edit Description failed ");

		log.info("INFO: Close FiDO");	
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();		
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario: FiDO Test Case : testAboutPanel_DescriptionTooLongMsg </B></li>
	 * <li><B>Info:</B> Test to verify the description is too long message displays </li>
	 * <li><B>Step:</B> Upload a file to Files App by API </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open FiDO from file list view </li>
	 * <li><B>Step:</B> Click on the Add a description link </li>
	 * <li><B>Step:</B> Add enough text to generate the description is too long message & Save </li>
	 * <li><B>Verify:</B> Verify the message 'The description is too long' displays </li>
	 * <li><B>Verify:</B> Verify the 'Shorten description?' link displays </li>
	 * <li><B>Step:</B> Close the fileviewer/FiDO </li>
	 * <li><B>Step:</B>> Cleanup: delete the uploaded file via API </li>
	 * <li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD176ACDBC9B767985257FD5004A81AC">TTT- FILE VIEWER - MY FILES - SIDEBAR - ABOUT THIS FILE - DESCRIPTION(D54)</a></li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testAboutPanel_DescriptionTooLongMsg(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file8)
		                                     .extension(".jpg")
		                                     .shareLevel(ShareLevel.EVERYONE)
		                                     .rename(ui.changeName(Data.getData().file8)).build();
		
		log.info("INFO: Upload file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		log.info("INFO: Open About Panel of FiDO");
		FileViewer_Panel_Menu.ABOUT.select(ui);
		
		log.info("INFO: Add content to the description field");
		String descriptionText = Data.getData().Chars1000 + Data.getData().Chars1001 + Data.getData().Chars1000 + "_" + Helper.genDateBasedRand();
		
		log.info("INFO: Click on the Add a description link");
		ui.clickLinkWait(FileViewerUI.AddADescriptionLink);

		log.info("INFO: Click on on the description input field");
		ui.fluentWaitElementVisible(FileViewerUI.DescriptionInputBox);
		Element descriptionInput = ui.getFirstVisibleElement(FileViewerUI.DescriptionInputBox);
		descriptionInput.click();

		log.info("INFO: Input the Description");
		descriptionInput.type(descriptionText);

		log.info("INFO: Click on the Save button");
		ui.clickLinkWait(FileViewerUI.SaveDescriptionButton);
		
		log.info("INFO: Verify the message 'The description is too long' displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData() .DescriptionTooLong),
				"ERROR: The description is too long message does not appear");
		
		log.info("INFO: Verify the link Shorten description? displays");
		Assert.assertTrue(driver.isElementPresent(FileViewerUI.ShortenDescriptionLink),
				"ERROR: The Shorten description? link does not appear");

		log.info("INFO: Close FiDO");	
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();		
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario: FiDO Test Case : testAboutPanel_DescriptionReadMoreReadLessLinks </B></li>
	 * <li><B>Info:</B> Test to verify the the Read More & Read Less links display </li>
	 * <li><B>Step:</B> Upload a file to Files App by API </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open FiDO from file list view </li>
	 * <li><B>Step:</B> Click on the Add a description link </li>
	 * <li><B>Step:</B> Add enough text to generate the Read More link displays & Save </li>
	 * <li><B>Verify:</B> Verify the message 'Read More...' link displays </li>
	 * <li><B>Step:</B> Click on the Read More... link </li>
	 * <li><B>Verify:</B> Verify the 'Read Less...' link displays </li>
	 * <li><B>Step:</B> Close the fileviewer/FiDO </li>
	 * <li><B>Step:</B>> Cleanup: delete the uploaded file via API </li>
	 * <li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD176ACDBC9B767985257FD5004A81AC">TTT- FILE VIEWER - MY FILES - SIDEBAR - ABOUT THIS FILE - DESCRIPTION(D54)</a></li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testAboutPanel_DescriptionReadMoreReadLessLinks(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file8)
		                                     .extension(".jpg")
		                                     .shareLevel(ShareLevel.EVERYONE)
		                                     .rename(ui.changeName(Data.getData().file8)).build();
		
		log.info("INFO: Upload file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		log.info("INFO: Open About Panel of FiDO");
		FileViewer_Panel_Menu.ABOUT.select(ui);
		
		log.info("INFO: Add content to the description field");
		String descriptionText = Data.getData().Chars1000 + "_" + Helper.genDateBasedRand();
		
		log.info("INFO: Click on the Add a description link");
		ui.clickLinkWait(FileViewerUI.AddADescriptionLink);

		log.info("INFO: Click on the description input field");
		ui.fluentWaitElementVisible(FileViewerUI.DescriptionInputBox);
		Element descriptionInput = ui.getFirstVisibleElement(FileViewerUI.DescriptionInputBox);
		descriptionInput.click();

		log.info("INFO: Input the Description");
		descriptionInput.type(descriptionText);

		log.info("INFO: Click on the Save button");
		ui.clickLinkWait(FileViewerUI.SaveDescriptionButton);
		
		log.info("INFO: Verify the 'Read More...' link displays");
		Assert.assertTrue(driver.isElementPresent(FileViewerUI.ReadMoreLink),
				"ERROR: The Read More link does not appear");
		
		log.info("INFO: Click on the Read More link");
		ui.clickLinkWait(FileViewerUI.ReadMoreLink);
		
		log.info("INFO: Verify the 'Read Less...' link displays");
		Assert.assertTrue(driver.isElementPresent(FileViewerUI.ReadLessLink),
				"ERROR: The Read Less link does not appear");
		
		log.info("INFO: Click on the Read Less link");
		ui.clickLinkWait(FileViewerUI.ReadLessLink);
		
		log.info("INFO: Verify the 'Read More...' link displays");
		Assert.assertTrue(driver.isElementPresent(FileViewerUI.ReadMoreLink),
				"ERROR: The Read More link does not appear");

		log.info("INFO: Close FiDO");	
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();		
	}
	
	
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test Case : testVersionPanel_UploadNewVersion </li>
	 * <li><B>Step:</B> Upload a file to Files App by API </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open FiDO from file list view </li>
	 * <li><B>Step:</B> Upload new version of the file </li>
	 * <li><B>Verify:</B> New version was uploaded.</li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testVersionPanel_UploadNewVersion(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file6)
		                                     .extension(".jpg")
		                                     .shareLevel(ShareLevel.EVERYONE)
		                                     .rename(ui.changeName(Data.getData().file6)).build();
		
		log.info("INFO: Upload a file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		log.info("INFO: Click on the Versions tab");
		FileViewer_Panel_Menu.VERSIONS.select(ui);
		
		log.info("INFO: Upload new version");
		String changeSummaryText = Data.getData().commonComment + "_" + Helper.genDateBasedRand();
		Assert.assertTrue(ui.uploadNewVersion(changeSummaryText),"ERROR : Upload new version failed ");

		log.info("INFO: Close FiDO");
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();		
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test testActions_Rename </li>
	 * <li><B>Step:</B> Upload a file by API , launch FiDO </li>	 * 
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Verify:</B> Rename works correctly. </li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testActions_Rename(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file10)
		                                     .extension(".jpg")
		                                     .rename(ui.changeName(Data.getData().file10)).build();
		
		log.info("INFO: Upload the file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		log.info("INFO: File Rename");
		String newName = "newName_" + baseFileImage.getName();
		Assert.assertTrue(ui.rename(newName),"ERROR : Rename the file Failed");
		
		log.info("INFO: Close FiDO");
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test Case : testSharePanel_QuickShareFileWithCommunityAsEditorPeopleAsReader </li>
	 * <li><B>Step:</B> Upload a public file by files api </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open file viewer in files apps  </li>
	 * <li><B>Step:</B> Go to 'Sharing' panel </li>
	 * <li><B>Step:</B> Click on Editor Quick share button </li>
	 * <li><B>Verify:</B> Editor as default role in role selector </li>
	 * <li><B>Step:</B> Add share for a community as Editor and click on save button </li>
	 * <li><B>Step:</B> Click on reader quick share button </li>
	 * <li><B>Verify:</B> reader as default role in role selector </li>
	 * <li><B>Step:</B> Add share for a people as reader and click on save button</li>
	 * <li><B>Verify:</B> 1) verify the community shows in the editor area ; 2)verify the people shows in Readers area </li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testSharePanel_QuickShareofCommunityAsEditorPeopleAsReader(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file7)
		                                     .extension(".jpg")
		                                     .rename(ui.changeName(Data.getData().file7)).build();
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
		                                               .access(Access.PUBLIC)
		                                               .description("test file viewer" )
		                                               .build();
		
		log.info("Create Community " + baseCommunity.getName() + " by Community API");
		community = baseCommunity.createAPI(apiCommunityOwner);
		if(community==null){
			log.info("ERROR: Fail to create community ");
		}	
		
		log.info("INFO: Upload a file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open file viewer failed");
			return ;
		}
		FileViewer_Panel_Menu.SHARING.select(ui);
		//Share to community as editor from quick share
		log.info("INFO: Click on editor quick share button");
		ui.clickLinkWait(FileViewerUI.QuickShareEditorLink);
		
		log.info("INFO: Verify default member type select is editor.");
		Assert.assertTrue(ui.isRoleSelected(FilesRole.EDITOR), "ERROR: The Role Type selector is not as editor which expected! ");
		
		log.info("INFO: Add one community as editor into the share queue");
		Assert.assertTrue(ui.addShare(FilesRole.EDITOR, Data.TypeAheadSelectorValueCommunity, community.getTitle(), community.getTitle()), "ERROR: Fail to add editor for : " + community.getTitle()+" to share queue");
	
		log.info("INFO: Click on multiple share save button");
		ui.clickLinkWait(FileViewerUI.MultiShareSaveButton);
		
		log.info("INFO: Verify the community is show up in the reader area in share list ! ");
		Assert.assertTrue(ui.isUserSharedWithRole(FilesRole.EDITOR, community.getTitle()), "ERROR: Fail to find : " + community.getTitle()+ "in the readers share list");
	   
		//Share to people as reader from quick share
		log.info("INFO: Click on quick share reader button");
		ui.clickLinkWait(FileViewerUI.QuickShareReaderLink);
		
		log.info("INFO: Verify default member type select is editor.");
		Assert.assertTrue(ui.isRoleSelected(FilesRole.READER), "ERROR: The Role Type selector is not as reader which expected! ");
		
		log.info("INFO: Add one people as reader into the share queue");
		Assert.assertTrue(ui.addShare(FilesRole.READER, Data.TypeAheadSelectorValueUser, testLookAheadUser1.getEmail(), testLookAheadUser1.getDisplayName()), "ERROR: Fail to add editor for : " + testLookAheadUser1.getDisplayName()+" to share queue");
		
		log.info("INFO: Click on multiple share save button");
		ui.clickLinkWait(FileViewerUI.MultiShareSaveButton);
		
		log.info("INFO: Verify the people is show up in the Readers area in share list! ");
		Assert.assertTrue(ui.isUserSharedWithRole(FilesRole.READER, testLookAheadUser1.getDisplayName()), "ERROR: Fail to find : " + testLookAheadUser1.getDisplayName()+ "in the editors share list");
		
		log.info("INFO: Close FiDO");
		ui.close();
		
		log.info("INFO: Cleanup - delete the file uploaded via the API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();	
	}

	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test Case : testSharePanel_MultipleSharesOfPeopleAsEditorAndCommunityAsReader </li>
	 * <li><B>Step:</B> Upload a public file by files api </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open file viewer in files apps  </li>
	 * <li><B>Step:</B> Go to 'Sharing' panel </li>
	 * <li><B>Step:</B> Click on Multiple Share Button </li>
	 * <li><B>Step:</B> Add share for a user as editor </li>
	 * <li><B>Step:</B> Add share for a community as reader </li>
	 * <li><B>Step:</B> Go to 'Shareing' panel </li>
	 * <li><B>Verify:</B> 1) verify the user shows in the editor area ; 2)verify the community shows in Readers area </li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testSharePanel_MultipleSharesOfPeopleAsEditorAndCommunityAsReader(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file7).extension(".jpg").rename(ui.changeName(Data.getData().file7)).build();
		BaseCommunity baseCommunity = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
		                                               .access(Access.PUBLIC)
		                                               .description("test file viewer" )
		                                               .build();
		
		log.info("Create Community " + baseCommunity.getName() + " by Community API");
		community = baseCommunity.createAPI(apiCommunityOwner);
		if(community==null){
			log.info("ERROR: Fail to create community ");
		}
		
		log.info("INFO: Upload a file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
        this.navigateToMyFilesView();
		
        log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open file viewer failed");
			return ;
		}
		FileViewer_Panel_Menu.SHARING.select(ui);
		log.info("INFO: Click on multiple share button");
		ui.clickLinkWait(FileViewerUI.MultiShareButton);
		
		log.info("INFO: Add one people as editor into the share queue");
		Assert.assertTrue(ui.addShare(FilesRole.EDITOR, Data.TypeAheadSelectorValueUser, testLookAheadUser1.getEmail(), testLookAheadUser1.getDisplayName()), "ERROR: Fail to add editor for : " + testLookAheadUser1.getDisplayName()+" to share queue");
		log.info("INFO: Add one community as reader into the share queue");
		Assert.assertTrue(ui.addShare(FilesRole.READER, Data.TypeAheadSelectorValueCommunity, community.getTitle(), community.getTitle()), "ERROR: Fail to add reader for : " + community.getTitle()+" to share queue");
	
		log.info("INFO: Click on multiple share save button");
		ui.clickLinkWait(FileViewerUI.MultiShareSaveButton);
		
		log.info("INFO: Verify the people show up in the editors area in share list! ");
		Assert.assertTrue(ui.isUserSharedWithRole(FilesRole.EDITOR, testLookAheadUser1.getDisplayName()), "ERROR: Fail to find : " + testLookAheadUser1.getDisplayName()+ "in the editors share list");
		log.info("Verify the community is show up in the reader area in share list ! ");
		Assert.assertTrue(ui.isUserSharedWithRole(FilesRole.READER, community.getTitle()), "ERROR: Fail to find : " + community.getTitle()+ "in the readers share list");
		
		log.info("INFO: Close FiDO");
		ui.close();
		
		log.info("INFO: Cleanup - delete the file uploaded via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();	
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test Case : testSharePanel_MultipleShareFileWithEveryone </li>
	 * <li><B>Step:</B> Upload a private file by files api </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Step:</B> Open file viewer in files apps  </li>
	 * <li><B>Step:</B> Go to 'Sharing' panel </li>
	 * <li><B>Step:</B> Click on Multiple Share Button </li>
	 * <li><B>Step:</B> Select everyone in the role selector and share </li>
	 * <li><B>Step:</B> Go to 'Sharing' panel </li>
	 * <li><B>Verify:</B> Everyone in your organization shows in readers area ; </li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testSharePanel_MultipleShareofEveryone(){
		ui.startTest();
		String product = cfg.getProductName();
		String org = "Everyone in";
		
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file7)
		                                     .extension(".jpg")
		                                     .rename(ui.changeName(Data.getData().file7)).build();
		
		log.info("INFO: Upload a file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open file viewer failed");
			return ;
		}
		FileViewer_Panel_Menu.SHARING.select(ui);
		log.info("INFO: Click on multiple share button");
		ui.clickLinkWait(FileViewerUI.MultiShareButton);
		
		log.info("INFO: Select Everyone in organization from member type selector");
		Assert.assertTrue(ui.addShare(FilesRole.READER, Data.TypeAheadSelectorValueEveryone, null, Data.DisplayTextEveryoneInYourOrganization), 
				"ERROR: Fail to select everyone in Orgnization");
		
		log.info("INFO: Click on multiple share save button");
		ui.clickLinkWait(FileViewerUI.MultiShareSaveButton);
		
		//determine of SC or OP				
		if(product.equalsIgnoreCase("cloud")){
			log.info("INFO: Verify the file is shared with everyone in the organization successfully");
			Assert.assertTrue(ui.isUserSharedWithRole(FilesRole.READER,org ), 
					"ERROR: Fail to share file to everyone in organization");
		}	else{
				
		
		log.info("INFO: Verify the file is shared to everyone in organization successfully");
		Assert.assertTrue(ui.isUserSharedWithRole(FilesRole.READER, Data.DisplayTextEveryoneInYourOrganization), 
				"ERROR: Fail to share file to everyone in organization");
		}
		
		log.info("INFO: Close FiDO");
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();	
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test testPreview_Image </li>
	 * <li><B>Step:</B> Upload a file by API , launch FiDO </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Verify:</B> Image could be previewed successfully. </li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testPreview_Image(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file10)
		                                     .extension(".jpg")
		                                     .rename(ui.changeName(Data.getData().file10)).build();
		
		log.info("INFO: Upload a file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		log.info("INFO: Verify the image appears in FiDO");
		Assert.assertTrue(ui.isFiDOPreviewed(FileType.IMAGE,null),"ERROR : Preview Image Failed");
		
		log.info("INFO: Close FiDO");
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test testPreview_Video </li>
	 * <li><B>Step:</B> Upload a file by API , launch FiDO </li>
	 * <li><B>Step:</B> Check for Guided Tour GK setting, if enabled close the guided tour pop-up dialog </li>
	 * <li><B>Step:</B> Click on All Files view </li>
	 * <li><B>Step:</B> Click on My Files view </li>
	 * <li><B>Verify:</B> Video could be previewed successfully. </li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void testPreview_Video(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file4)
		                                     .extension(".mp4")
		                                     .rename(ui.changeName(Data.getData().file4)).build();
		
		log.info("INFO: Upload a file");
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		
		log.info("INFO: Navigate to the My Files view");
		this.navigateToMyFilesView();
		
		log.info("INFO: Open the file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		log.info("INFO: Verify the video appears in FiDO");
		Assert.assertTrue(ui.isFiDOPreviewed(FileType.VIDEO,null),"ERROR : Preview Video Failed");
		
		log.info("INFO: Close FiDO");
		ui.close();
		
		log.info("INFO: Cleanup - delete the uploaded file via API");
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test testPreview_DOCX Note: This cases only for Docs Viewer Integrated Envs
	 * <li><B>Step:</B></li>
	 * <li><B>Step:</B>1. Upload a file by API , launch FiDO </li>
	 * <li><B>Verify:</B> Docx could be previed successfully. </li>
	 * </ul>
	 */
	@Test(groups = {"regression_docxsviewer"})
	public void testPreview_Docx(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder("WebEditorsTestFile.docx").extension(".docx").rename(ui.changeName("WebEditorsTestFile.docx")).build();
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.OFFICE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		Assert.assertTrue(ui.isFiDOPreviewed(FileType.OFFICE,DocType_DocsViewerSupported.DOCX),"ERROR : Preview docs Failed");
		ui.close();
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test testPreview_PDF Note: This cases only for Docs Viewer Integrated Envs
	 * <li><B>Step:</B></li>
	 * <li><B>Step:</B>1. Upload a file by API , launch FiDO </li>
	 * <li><B>Verify:</B> PDF could be previed successfully. </li>
	 * </ul>
	 */
	@Test(groups = {"regression_docsviewer"})
	public void testPreview_PDF(){
		ui.startTest();
		BaseFile baseFileImage = new BaseFile.Builder("WebEditorsTestFile.pdf").extension(".pdf").rename(ui.changeName("WebEditorsTestFile.pdf")).build();
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileImage);
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.OFFICE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		Assert.assertTrue(ui.isFiDOPreviewed(FileType.OFFICE,DocType_DocsViewerSupported.PDF),"ERROR : Preview PDF Failed");
		ui.close();
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test testPreview_XSLM
	 * <li><B>Step:</B></li>
	 * <li><B>Step:</B>1. Upload a file by API , launch FiDO </li>
	 * <li><B>Verify:</B> Rename works correctly. </li>
	 * </ul>
	 */
	@Test(groups = {"regression_docsviewer"})
	public void testPreview_XLSM(){
		ui.startTest();
		BaseFile baseFileXSLM = new BaseFile.Builder("WebEditorsTestFile.xlsm").extension(".xlsm").rename(ui.changeName("WebEditorsTestFile.xlsm")).build();
		List<FileEntry> fileEntries = uploadFilesToContainerAndLoadToMainPage(Container.FILES, baseFileXSLM);
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileXSLM, FileType.OFFICE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}
		
		Assert.assertTrue(ui.isFiDOPreviewed(FileType.OFFICE,DocType_DocsViewerSupported.XLSM),"ERROR : Preview XLSM Failed");
		ui.close();
		ui.deleteFilesByAPI(apiFileOwner,fileEntries);
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>FiDO Test 1 - log into Communities & then navigate to Files via mega menu</li>
	 * <li><B>Note: this is a modification of test method: testCommentsPanel_AddComment</li>
	 * <li><B>Note: 10/30/17 - updated test to log into Files, navigate to My Files view and then upload the file</li>
	 * </ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void fileviewerTest1(){
		ui.startTest();
		
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file6)
		                                    .extension(".jpg")            
		                                    .shareLevel(ShareLevel.EVERYONE)
		                                    .rename(ui.changeName(Data.getData().file6))
		                                    .build();
		
		log.info("INFO: Log into Files as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		log.info("INFO: Close the guided tour pop-up");
		cUI.closeGuidedTourPopup();

		log.info("INFO: Click on the My Files view");
		filesUI.clickMyFilesView();
		
		log.info("INFO: Wait for My Files view to be available");
		ui.fluentWaitPresent(FilesUIConstants.MyFilesTitleLink);
				
		log.info("INFO: Upload file");	
		baseFileImage.upload(filesUI);
				
		log.info("INFO: Open file in FiDO");
		if(!ui.openFiDOByView(Files_Display_Menu.DETAILS, baseFileImage, FileType.IMAGE)){
			log.info("ERROR : Open FiDO failed");
			return ;
		}	    

		log.info("INFO: Add comment to in FiDO");
		Assert.assertTrue(ui.addComments(Data.getData().commonComment),
				"ERROR : Add comment in FiDO failed ");

		log.info("INFO: Close FiDO");
		ui.close();

		log.info("Cleanup - delete the uploaded file");
		baseFileImage.trash(filesUI);
		baseFileImage.delete(filesUI);
		
		ui.endTest();		
	}
		
	/**
	 * FiDO invoked method : uploadFilesToContainerAndLoadToMainPage
	 * Upload files to Files App and Community Files Widget by API , launch browser and load to Files app main page or community file widget app main page
	 * @param container Files APP or Community Files Widget 
	 * @param baseFiles Upload any number of files 
	 * @return return created FileEntry list 
	 */
	private List<FileEntry> uploadFilesToContainerAndLoadToMainPage(Container container , BaseFile... baseFiles) {
		List<FileEntry> fileEntries = new ArrayList<FileEntry> ();
		FileEntry fileEntry ;
		if(container == Container.FILES){
			//Upload files by Files API in Files APP 
			for(BaseFile baseFile : baseFiles){
				fileEntry = ui.upload(baseFile, testConfig, testUser);
				//Notice: if no this changePermission , the file is still private status even if set baseFile sharelevel as everyone .
				apiFileOwner.changePermissions(baseFile, fileEntry);   
				fileEntries.add(fileEntry);
			} 
			log.info("INFO: Login and navigate to Files APP main page ");
			ui.loadComponent(Data.getData().ComponentFiles);
			ui.replaceProductionCookies();
			ui.login(testUser);
		}else if(container == Container.COMMUNITYFILES){
			//Create one community by Community API 
			BaseCommunity baseCommunity = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
			   										   .access(Access.PUBLIC)
			   										   .description("test FiDO" )
			   										   .build();
			log.info("Create Community " + baseCommunity.getName() + " by Community API");
			community = baseCommunity.createAPI(apiCommunityOwner);
			if(community==null){
				log.info("ERROR: Fail to create community ");
				return null ;
			}
			log.info("INFO : Upload files to community " + baseCommunity.getName());
			for(BaseFile baseFile : baseFiles){
				fileEntry = ui.upload(baseFile, testConfig, testUser,community);
				apiFileOwner.changePermissions(baseFile, fileEntry);
				fileEntries.add(fileEntry);				
			}
			
			log.info("INFO: Generate community UUID ");
			baseCommunity.getCommunityUUID_API(apiCommunityOwner, community);
			log.info("INFO: Login and navigate to the community by UUID ");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			baseCommunity.navViaUUID(cUI);
			//Load to the Community File Widget 
			log.info("INFO : Navigate to community files widget");
			Community_LeftNav_Menu.FILES.select(ui);
		}
		return fileEntries;
		
	}

	/**
	 * FiDO invoked method : checkBasicElementsInFiDO : Check Basic elements present and match the expected results 
	 * @param fileType
	 * @param baseFile
	 * @param expectedResults
	 */
	private void checkBasicElementsInFiDO(FileType fileType, BaseFile baseFile, Map<String, String> expectedResults){

		log.info("INFO : Start to the verification of basic elements in FiDO");
		String fileName = baseFile.getRename() + baseFile.getExtension();
		log.info("INFO : Verify top banner file title and panels title ");
		Assert.assertEquals(driver.getSingleElement(FileViewerUI.PreviewLinkTitle).getText(), fileName, "ERROR: FiDO banner title should match the file title");
		Assert.assertTrue(driver.isElementPresent(FileViewer_Panel_Menu.ABOUT.getSelector()), "ERROR: FiDO panel title should include " + Data.FILEVIEWER_ABOUTTHISFILE);
		Assert.assertTrue(driver.isElementPresent(FileViewer_Panel_Menu.COMMENTS.getSelector()),  "ERROR: FiDO panel title should include " + Data.FILEVIEWER_COMMENTS);
		Assert.assertTrue(driver.isElementPresent(FileViewer_Panel_Menu.SHARING.getSelector()), "ERROR: FiDO panel title should include " + Data.FILEVIEWER_SHARED_WITH);
		Assert.assertTrue(driver.isElementPresent(FileViewer_Panel_Menu.VERSIONS.getSelector()),  "ERROR: FiDO panel title should include " + Data.FILEVIEWER_VERSIONS);
		log.info("INFO : Go to About this File panel to verify current version , file name , size , creator ");
		FileViewer_Panel_Menu.ABOUT.select(ui);
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.PanelCurrentVersion).getText().contains(expectedResults.get(Data.FILEVIEWER_CURRENTVERSION)),"ERROR: FiDO panel current version should match the expected version");
		
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
					String fileSizeElementContents = driver.getSingleElement(FileViewerUI.FileSize_CurrentVersion).getText();
					String expectedSize1 = (fileSize - 1) + " KB";
					String expectedSize2 = fileSize + " KB";
					String expectedSize3 = (fileSize + 1) + " KB";
					Assert.assertTrue(fileSizeElementContents.contains(expectedSize1)
								      || fileSizeElementContents.contains(expectedSize2)
								      || fileSizeElementContents.contains(expectedSize3),
								      "ERROR: FiDO panel file size is incorrect. Actual size is " + fileSizeElementContents +
								      ", expected size is " + expectedSize1 + " to " + expectedSize3);
				}
			} else {
				Assert.assertTrue(driver.getSingleElement(FileViewerUI.FileSize_CurrentVersion).getText().contains(expectedResults.get(Data.FILEVIEWER_SIZE)),"ERROR: ERROR: FiDO panel file size should match the created size");				
			}
		}
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.CreatedBy).getText().contains(expectedResults.get(Data.FILEVIEWER_CREATED)),"ERROR: FiDO panel creator should match the created person");
		log.info("INFO : End verification of basic elements in FiDO");
	}
	
	
	/**
	 * This method does the following: 
	 *  1. checks for the Guided Tour gatekeeper setting.  If the guided tour pop-up box appears, it will get closed
	 *  2. clicks on the All Files view
	 *  3. clicks on the My Files view
	 */
	public void navigateToMyFilesView() {
				
		log.info("INFO: If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window");
		if(cUI.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){

			log.info("INFO: Check to see if the Guided Tour popup window appears, if yes close it");
			cUI.closeGuidedTourPopup();
		}
        
		log.info("INFO: Click on the All Files view");
		ui.clickLinkWait(FilesUIConstants.AllFilesView);

		log.info("INFO: Click on the My Files view");
		ui.clickLinkWait(FilesUIConstants.openMyFilesView);
		

	}
	
	
}
