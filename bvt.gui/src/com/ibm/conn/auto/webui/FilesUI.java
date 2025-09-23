package com.ibm.conn.auto.webui;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.base.BaseSetup;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Utils;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.appobjects.base.BaseFolder.Access;
import com.ibm.conn.auto.config.RobotTypeClass;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.FTPhelper;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.webui.cloud.FilesUICloud;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.multi.FilesUIMulti;
import com.ibm.conn.auto.webui.onprem.FilesUIOnPrem;
import com.ibm.conn.auto.webui.production.FilesUIProduction;

public abstract class FilesUI extends HCBaseUI {

	public enum Container{
		FILES,COMMUNITYFILES
	};

	public enum FileType {
		IMAGE, VIDEO, OFFICE, UNSUPPORTED
	};

	public FilesUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	protected static Logger log = LoggerFactory.getLogger(FilesUI.class);


	/**
	 * This method will return Locator for recent file title
	 * @param int - position
	 */
	public static String recentHistoryFileTitle(int position) {
		return "css=#list div[class='recentItems']:nth-child(" + position + ") div[class*='recentItemTitle'] a[href*='files']";

	}
	
	/**
	 * This method will return locator for type of action
	 * @param int - position
	 */
	public static String typeOfAction(int position) {
		return "css=#list div[class='recentItems']:nth-child(" + position + ") div[class*='recentItemTitle'] div[class*='metaInfo'] li[class='timeInfo']";

	}
	
	/**
	 * This method will return locator for File name
	 * @param String - fileName
	 */
	public static String fileNameLinks(String fileName) {
		return "css=div[class='recentItems'] div[class*='recentItemTitle'] a[title='" + fileName + "']";

	}

	private CommunitiesUI ui= CommunitiesUI.getGui(cfg.getProductName(), driver);

	/**
	 * 
	 * @param file name
	 * */
	public abstract void searchFile(String name);
	
	/**
	 * 
	 * @param fileDate
	 * */
	public abstract long getFileCreatedTime(String fileDate);
	
	public enum FilesListView {
		GRID(FilesUIConstants.selectViewGrid, FilesUIConstants.viewGridIsActive),
		LIST(FilesUIConstants.selectViewList, FilesUIConstants.viewListIsActive),
		DETAILS(FilesUIConstants.selectViewDetail, FilesUIConstants.viewDetailIsActive);
		
		String activateSelector = null;
		String isActiveSelector = null;
		
		FilesListView(String activate, String isActive) {
			activateSelector = activate;
			isActiveSelector = isActive;
		}
		
		public String getActivateSelector() {
			return this.activateSelector;
		}
		
		public String getIsActiveSelector() {
			return this.isActiveSelector;
		}
	}
	
	/**
	 * 
	 * @param folder
	 * @return
	 */
	public static String selectMyFolder(BaseFolder folder){
		return "css=.lotusContent a[title='" + folder.getName() + "']";
	}
	
	public static String selectOneFolder(BaseFolder folder){
		return "css=.lotusContent tr[style='cursor: default;'] a[title='" + folder.getName() + "']";
	}
	
	/**
	 * getFilesIsUploaded - 
	 * @param file
	 * @return
	 */
	public static String getFileIsUploaded(BaseFile file){
		return "css=a[class^='entry-title'][title='"+file.getRename() + file.getExtension()+ "']";
	}
		
	/**
	 * getUploadFileName - 
	 * @param file
	 * @return fileName
	 */
	public String getUploadFileName(BaseFile file){
		return "css=a[title='"+file.getRename() + file.getExtension()+ "']";
	}
	
	/**
	 * selectFile -
	 * @param file
	 */
	public static String selectFile(BaseFile file){
		return "xpath=//h4//a[@title='" + file.getName() + "']";
	
	}
	/**
	 * For file viewer : select file in Grid View
	 * @param file
	 * @return preview link in Grid View  for file viewer
	 */
	public static String selectFileInGridView(BaseFile file){
		return "css=div[class^='ic-thumb-widget-flip-card']:contains(" + file.getRename()+ file.getExtension() + ")" ;
	}

	/**
	 * For file viewer : Preview in Grid View to launch file viewer file viewer test
	 * @param file
	 * @return preview link in Grid View  for file viewer
	 */
	public static String previewLinkInGridView(BaseFile file){
		return "css=div[class='card-back']:contains(" + file.getRename() + file.getExtension() + ")";
	}


	/**
	 * 
	 * @param folder
	 * @return
	 */
	public static String selectFolder(BaseFolder folder){
		return "css=td label[title='" + folder.getName() + "']";
	}
	
	/**
	 * 
	 * @param file_name
	 * @return selector for rename file in File Upload Dialog box
	 */
	public String renameFileSelector(String file_name) {
		return "css=a[title^='Rename " + file_name + "']";
	}
	
	/**
	 * fileSpecificMore -
	 * @param postion
	 * @return
	 */
	public String fileSpecificMore(int postion) {
		return "css=a[aria-controls='detailsRow_" + postion + "']:contains(More)";
	}
	
	public String fileSpecificMore(BaseFile file) {
		return "//tr[descendant::a[@title='" + file.getName() + "']]//a[text()='More']";
	}
	
	public String fileSpecificActionMenu(BaseFile file) {
		return "//tr[descendant::a[@title='" + file.getName() + "']]//a[@title='Actions']";
	}
	
	/**
	 * fileSpecificShareStatus - Return the String value for a file in the Sharing column
	 * @param file - BaseFile instance for the file to be checked
	 * @return String - The value for a file in the Sharing column
	 */
	public String fileSpecificShareStatus(BaseFile file) {
		return "xpath=//tr[descendant::a[@title='" + file.getName() + "']]//a[@class='lotusNowrap']";
	}
	
	public String fileSpecificCheckmark(int position) {
		if (driver.getCurrentUrl().contains("files")){
			return "css=input[id='list_" + position + "']";
		} else if (driver.getCurrentUrl().contains("communities")){
			return "css=input[id='list_" + position + "']";
		} else {
			Assert.fail("File is neither a regular file nor a community file. This should never happen.");
			return null;
		}
	}
	
	/**
	 * isFileLocked - Return a boolean value depending on whether a file is locked (by verifying the presence of the Lock icon and the 'Unlock File' link') 
	 * @param file - BaseFile instance for a file that needs to be verified
	 * @return boolean - true if the file is locked and False if it is not
	 */
	public boolean isFileLocked(BaseFile file) {
		return isElementVisible("xpath=//a[contains(@title,'" + file.getName() + "')]/ancestor::tr[@class='lotusDetails']/descendant::img[@alt='Locked by you']")
				&& isElementVisible("xpath=//a[contains(@title,'" + file.getName() + "')]/ancestor::tr[@class='lotusDetails']/descendant::a[contains(text(),'Unlock File')]");
	}
	
	/**
	 * unlockFile - Unlock a file by clicking on the 'Unlock File' link 
	 * @param file - BaseFile instance for a file that has been locked
	 */
	public void unlockFile(BaseFile file) {
		clickLinkWithJavascript("xpath=//a[contains(@title,'" + file.getName() + "')]/ancestor::tr[@class='lotusDetails']/descendant::a[contains(text(),'Unlock File')]");
	}
	
	/**
	 * getSelectorForCheckbox - Given the folder position, get selector for the check box 
	 * @param position - the position of the folder in the folder list 
	 * @return the selector of the check box 
	 */
	public static String getSelectorForCheckbox(int position){
		return "css=input[id='list_" + position + "']";
    }
	
	/**
	 * getFileCheckbox - Given the file name, get selector for filename check box. 
	 * @param FileName - Name of File
	 * @return the selector of the check box 
	 */
	public static String getFileCheckbox(String FileName)
	{
		return "//div[@title='"+FileName+"']/input[not(contains(@style,'display:none;'))]";
	}
	
	/**
	 * getGateKeeperValue - Get GateKeeper value based on the gk flag 
	 * @param gk_flag_onprem - GateKeeper String, such as "FILES_NESTED_FOLDER" 
	 * @param gk_flag_cloud - GateKeeper String, such as "files-nested-folder"
	 * @return the value of GateKeeper 
	 */
	public boolean getGateKeeperValue(String gk_flag_onprem, String gk_flag_cloud) {
		String product = cfg.getProductName();
		String serverURL = APIUtils.formatBrowserURLForAPI(cfg.getTestConfig().getBrowserURL());
		User adminUser = cfg.getUserAllocator().getAdminUser();
		GatekeeperConfig gkc_onprem = GatekeeperConfig.getInstance(serverURL, adminUser);
		GatekeeperConfig gkc_cloud = GatekeeperConfig.getInstance(driver);
		boolean value;
			
		
		if(product.equalsIgnoreCase("onprem")){
			log.info("INFO: Check to see if the Gatekeeper " +gk_flag_onprem + " setting is enabled");
			if(gkc_onprem == null)
				gkc_onprem = GatekeeperConfig.getInstance(serverURL, adminUser);
			 value = gkc_onprem.getSetting(gk_flag_onprem);
			log.info("INFO: Gatekeeper flag " + gk_flag_onprem + " is " + value);

		}
		else {
			log.info("INFO: Check to see if the Gatekeeper " +gk_flag_cloud + " setting is enabled");
			if(gkc_cloud == null)
				gkc_cloud = GatekeeperConfig.getInstance(driver);
			 value = gkc_cloud.getSetting(gk_flag_cloud);
			log.info("INFO: Gatekeeper flag " + gk_flag_cloud + " is " + value);
		}
		
	   return value;	
	}
	
	/**
	 * getSelectorForMoreLink - Given the folderName, get selector for the more link 
	 * @param folderName - the name of the folder
	 * @return the selector of the more link 
	 */
	public String getSelectorForMoreLink(String folderName) {
		return "//tr[descendant::a[@title='" + folderName + "']]//a[text()='More']";
	}
	
	public String getFilesOption(){
		return FilesUIConstants.filesOption;
	}
	
	/**
	 * For file viewer : add comment in file viewer 
	 * @return if comment can be searched out in comment container , return true ; or else , false
	 */
	public boolean fileViewer_addComment() {
		log.info("INFO: Add a comment in file viewer");
		typeNativeInCkEditor(Data.getData().commonComment, "0");
		clickLinkWait(FilesUIConstants.fileviewer_postCommentButton);
		String getComment = "";
		try {
			getComment = driver.getSingleElement(FilesUIConstants.fileviewer_commentContainer).getText();
			
		} catch (Exception e) {
			log.error("Not able get text from comment box due to "+e);
		}

		if (getComment.contains(Data.getData().commonComment)) {
			return true;
		} else {
			return false;
		}

	}
	/**
	 * For File Viewer : openFileViewerByView
	 * In main page of file app or community file widget main page , Open file viewer against Launcher (grid view , list , customize view , file detail page) 
	 * and verify the file viewer is opened or not 
	 * @param launcher  views in files main page top right corner
	 * @param baseFile  
	 * @param fileType
	 * @return True : open file viewer successfully ; False : open file viewer failed . 
	 */
	public boolean openFileViewerByView(FilesListView launcher, BaseFile baseFile, FileType fileType ){
		log.info("INFO : testFileViewer Start");
		String fileName ="";
		
		fileName = baseFile.getRename() + baseFile.getExtension();
		log.info("INFO: Open file Viewer for file :  " + fileName + " in " + launcher);
		if(launcher == FilesListView.GRID){
			Files_Display_Menu.TILE.select(this); 
			if(!driver.isElementPresent(selectFileInGridView(baseFile))){
				log.info("ERROR: Fail to find the uploaded file " + fileName + " in " + launcher);
				return false;
			}
			log.info("INFO: Click Preview icon in GridView for file " + fileName);
			//DEV will make front side clickable to open viewer by defect 149585: Grid view thumbnail click handler causes problems in Selenium , hover won't needed when 149585 is fixed
			driver.getSingleElement(selectFileInGridView(baseFile)).hover();
			clickLink(previewLinkInGridView(baseFile));

		}else if(launcher == FilesListView.DETAILS){  
			//This DETAILPAGE will be removed when this task is done in D41 159465: Replace file details page with file viewer
			log.info("INFO: Click file name link in File Details view for file :  " + fileName);
			Files_Display_Menu.DETAILS.select(this);
			clickLinkWait(FilesUI.getFileIsUploaded(baseFile)); 
		}
		
		if(!isFileViewerDisplayed(fileType)){
			log.error("ERROR: Fail to open file viewer of " + fileName + "in " + launcher);
			return false;
		} 
		return true ;
	}
	
	/**
	 * For File Viewer : isFileViewerDisplayed
	 * @param fileType
	 * @return True : file viewer is displayed ; False : file viewer is not opened 
	 */
	public boolean isFileViewerDisplayed(FileType fileType){
		switch(fileType){
		    case IMAGE:
			      return driver.isElementPresent(FilesUIConstants.fileviewer_previewImage);
			    case VIDEO:
			      return driver.isElementPresent(FilesUIConstants.fileviewer_previewVideo);
			    case OFFICE:
			      return driver.isElementPresent(FilesUIConstants.fileviewer_previewOffice);
			    case UNSUPPORTED:
			      return driver.isElementPresent(FilesUIConstants.fileviewer_previewUnsupported);
			    default:
			      return false;
		}
	}
	/**
	 * For File Viewer : closeFileViewer
	 */
	public void closeFileViewer(){
		if(driver.isElementPresent(FilesUIConstants.fileviewer_close))
			clickLink(FilesUIConstants.fileviewer_close);
	}
	/**
	 * For File Viewer : reName 
	 * Add Data before file name (without extension name) 
	 * @param fileName
	 * @return The new name 
	 */
	public String reName(String fileName){
		if(fileName.indexOf(".")<0)
			throw new AssertionError("ERROR: please input the file name with extension name");
		return Helper.genDateBasedRand() + "_" + fileName.substring(0, fileName.indexOf('.'));
	}
	
	public void clickShareFiles() throws Exception {
		//Click on the Share Files link
		clickLink(BaseUIConstants.CommunityShareFiles);
		fluentWaitPresent(BaseUIConstants.BrowseFilesOnMyComputer);
	}
	

	public void clickBrowseFilesOnMyComputer() throws Exception {
		//Click on the "Browse files on my computer..." link
		clickLink(BaseUIConstants.BrowseFilesOnMyComputer);
		fluentWaitTextPresent("Upload Files to This Community");
	}
	
	public void clickFilesSidebar() throws Exception {
		//Click on the Files sidebar link
		clickLink(BaseUIConstants.CommunityFilesSidebar);
		fluentWaitTextPresent("Files for This Community");
	}

	/**
	 * FileToUploadUsingDismissClass() Method to input the target file path to File Upload dialog.
	 * Note: BrowserStack doesn't support running AutoIt script so the Browse window will be left on screen.
	 * @param String fileUploadName - Name of file to attach
	 * @param String inputFieldLoc - file input field selector
	 * @throws Exception
	 */
	public void fileToUploadUsingDismissClass(String fileUploadName, String inputFieldLoc) throws Exception {	
		// For some components, the file path input field is not present until the Browse button is clicked.
		// Check for the presence of the input field and click Browse if not there.
        fluentWaitElementVisible(FilesUIConstants.getBrowseButton);
        driver.turnOffImplicitWaits();
        if(!this.isElementPresent(inputFieldLoc)) {
        	log.info("INFO: Input field not found, click the Browse button.");
        	clickLinkWithJavascript(FilesUIConstants.getBrowseButton);
        }
        driver.turnOnImplicitWaits();
    	sleep(2000);
    	
		// determine the upload file location and type it into the input field
		setLocalFileDetector();
		String uploadFilePath = getFileUploadPath(fileUploadName, cfg);
		log.info("INFO: File awaiting upload: " + uploadFilePath);
		
		Element inputField = driver.getSingleElement(inputFieldLoc);
		//remove class attribute so input field becomes visible
		driver.executeScript("arguments[0].setAttribute('class', '');arguments[0].setAttribute('style', 'position: absolute; top: 0px;');", (WebElement) inputField.getBackingObject());
		
		inputField.typeFilePath(uploadFilePath);
	}
	
	/**
	 * Set file detector to local if running on remote grid
	 * so that local files can be used.
	 * Note: call this BEFORE the web element of the file path input field is
	 * defined otherwise it will not work.
	 * @return true if the setFileDetector is called
	 */
	public boolean setLocalFileDetector() {
		
		// Uncomment this if we need to use the legacy Selenium grid again
//		TestConfiguration testConfig = cfg.getTestConfig();
//		if (!testConfig.serverIsLegacyGrid())  {
			// use LocalFileDetector to upload local file in remote grid node.
			log.info("Call setFileDetector to use a local file");
			RemoteWebDriver wd = (RemoteWebDriver) driver.getBackingObject();
			wd.setFileDetector(new LocalFileDetector());
			return true;
//		}
//		return false;
	}
	
	
	public void fileToUploadUsingRobotClass(String FileUploadName)throws Exception{
		String FileNameAndLoc = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), FileUploadName);
		sleep(3000);
		Robot r = new Robot();
		RobotTypeClass type = new RobotTypeClass(r);
		type.typeMessage(FileNameAndLoc);
		sleep(500);
		type.tabToButtonAndEnter(2);
		log.info("INFO: Used the Java Robot Class to type");
	}
	
	/**
	 * Enters the absolute path for a file name into an input field of a 'Browse' dialog box and presses 'Enter' to confirm the file name to be uploaded
	 * 
	 * @param fileNameToBeUploaded - The file name of the file to be uploaded
	 * @return - True if all actions are completed successfully, false otherwise
	 */
	public boolean uploadFileUsingRobotClass(String fileNameToBeUploaded) {
		
		log.info("INFO: Retrieve the absolute path for the file to be uploaded with filename: " + fileNameToBeUploaded);
		String absolutePathForFile = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), fileNameToBeUploaded);
		sleep(3000);
		log.info("INFO: The absolute path for the file has been retrieved: " + absolutePathForFile);
		
		// Initialise an instance of the Robot class
		Robot robot;
		try {
			robot = new Robot();
		} catch(AWTException awte) {
			log.info("ERROR: An AWTException was thrown while initialising an instance of the Robot class");
			awte.printStackTrace();
			robot = null;
		}
		if(robot == null) {
			return false;
		}
		
		// Initialise an instance of the RobotTypeClass class
		RobotTypeClass robotTypeClass = new RobotTypeClass(robot);
		
		log.info("INFO: Now typing the absolute path for the file into the input field using the RobotTypeClass");
		robotTypeClass.typeMessage(absolutePathForFile);
		sleep(500);
		
		log.info("INFO: Now pressing the 'Enter' key to confirm the file entry");
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		
		log.info("INFO: Successfully used the Java Robot and RobotTypeClass classes to enter the absolute path of the file and upload the file");
		return true;
	}
	
	/**
	 * Upload a file
	 * @param fileUploadName - file to upload
	 * @param inputField - locator of the file path field
	 * @throws Exception
	 */
	public void fileToUpload(String fileUploadName, String inputField) throws Exception {
		TestConfiguration testConfig = cfg.getTestConfig();
		
		// // Uncomment this if we need to use the legacy Selenium grid again
//		if (!testConfig.serverIsBrowserStack() && !testConfig.serverIsGridHub()) {
			// When running locally on Windows, start the AutoIt script FileUploadCancel.exe to dismiss the OS Browse dialog.
			// It has been observed that even we don't miss the Browser dialog the test can still run behind it.
			// It is already running on Windows grid nodes.
//			if (testConfig.getBrowserEnvironment().isLocal() && testConfig.getBrowserEnvironment().isWindows()) {
//				Runtime.getRuntime().exec(cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), "FileUploadCancel.exe"));
//			}
//		}
		
		if (testConfig.browserIs(BrowserType.FIREFOX) || testConfig.browserIs(BrowserType.CHROME) || testConfig.browserIs(BrowserType.EDGE)) {
			log.info("INFO: Will perform File Upload for:" + fileUploadName);
			fileToUploadUsingDismissClass(fileUploadName, inputField);
		} 
		else {
			Assert.fail("Framework does not support file upload for this browser type.");
		}
	}

	
	/**
	 * Locate target test file in the local machine.
	 * Folder Search Order: 
	 * 1) "root_folder_name"/"upload_files_folder_name" in testTemplate.xml
	 * 2) <current working dir>/resources    (when running from extracted zip  eg icautomation but testTemplate still has old values for grid node)
	 * 4) <current working dir>/../resources (in case current working dir is test_config)
	 * @param fileUploadName
	 * @param cfg
	 * @return absolute path for test file
	 */
	public static String getFileUploadPath(String fileUploadName, TestConfigCustom cfg)  {
		// Search #1 - file path according to "root_folder_name"/"upload_files_folder_name" in testTemplate.xml
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), fileUploadName);
		
		if (cfg.getTestConfig().serverIsBrowserStack()) {
			log.info("INFO: File upload to be performed on Browserstack");
			return BaseSetup.browserStackProps.getProperty("fileUploadPath") + fileUploadName;
		} 
		
		// Uncomment this if we need to use the legacy Selenium grid again
//		if (cfg.getTestConfig().serverIsLegacyGrid())  {
//				// Search #1 is the only supported path for the legacy Selenium Grid
//				log.info("INFO: File upload to be performed on internal Selenium");
//				return filePath;
//		}
		
		// Search #1:
		// This will be the exact path to type into the file path field in the UI.
		// Make sure the name separator is for the local OS since getBrowserEnvironment().getAbsoluteFilePath
		// uses the Grid node OS which may be different from the local machine. 
		if (File.separator.equals("\\")) {
			filePath = filePath.replace("/", "\\");
		} else {
			filePath = filePath.replace("\\", "/");
		}
		
		if (!Files.isRegularFile(Paths.get(filePath))) {
			// Search #2:
			Path path = Paths.get(System.getProperty("user.dir"), "resources", fileUploadName);
			if (Files.isRegularFile(path)) {
				filePath = path.toString();
			} else {
				// Search #3:
				path = Paths.get(System.getProperty("user.dir"), "..", "resources", fileUploadName);
				if (Files.isRegularFile(path)) {
					filePath = path.toString();
				} else {
					log.error("Cannot find test file: " + fileUploadName);
				}
			}
		}
        
		log.info("INFO: Test files folder path: " + filePath);
		return filePath;
	}
	
	/**
	 * uploadFileToCommunity
	 * @param file
	 * @throws Exception
	 */
	public void uploadFileToCommunity(BaseFile file) throws Exception {

		//Upload file to the community
		log.info("INFO: perform a file upload in a community");
		
		//click share files link
		log.info("INFO: Select share files link");		
		fluentWaitTextPresent(FilesUIConstants.fileForThisCommunityMsg);
		clickLink(BaseUIConstants.CommunityShareFiles);
		
		//select to upload file from my computer
		log.info("INFO: Checking to see where to load file from");
		if(file.getFileLocal()){
			log.info("INFO: Select to upload the file from local");			
			fluentWaitPresent(BaseUIConstants.CommunityMyComputer);
			clickLink(BaseUIConstants.CommunityMyComputer);
		}
		
		//Enter the path/filename
		log.info("INFO: Enter the file name and path");
		fileToUpload(file.getName(), BaseUIConstants.FileInputField2);
		
		//check to see if you want to encrypt the file
		log.info("INFO: Check to see if file needs to be encrypted ");
		if(file.getEncrypt()){
			log.info("INFO: Selecting check box to encrypt file");
			getFirstVisibleElement("css=input[id$='setEncrypt']").click();		
		}
		
		//Now upload the file
		log.info("INFO: Select share button to upload the file");
		clickLink(BaseUIConstants.Share_Files_Button);
		
		
		//Validate file upload
		log.info("INFO: Check to see community is moderated ");
		if(file.getModerated()){
			log.info("INFO: Community is moderated unable to validate upload");
		}else {
			log.info("INFO: Looking for file upload success message");
			fluentWaitTextPresent("Successfully uploaded " + file.getName());
		}
		
		log.info("INFO: File has being uploaded successfully");

	}
	
	
	/**
	 * Upload a file
	 * 
	 */
	public void upload(BaseFile file) {
		upload(file, null);
	}

	public void upload(BaseFile file, GatekeeperConfig gkc) {

		String gk_flag_onprem = "FILES_NESTED_FOLDER";
		String gk_flag_cloud = "files-nested-folder";
		String product = cfg.getProductName();
		String serverURL = APIUtils.formatBrowserURLForAPI(cfg.getTestConfig().getBrowserURL());
		User adminUser = cfg.getUserAllocator().getAdminUser();

		waitForSameTime();
		
		//Open "File Upload" dialog
		if(!file.getComFile()){
	        //goto My Files View
	        clickMyFilesView();
	        waitForPageLoaded(driver);
	        
			log.info("INFO: Select Upload Files Button");
			clickUploadButtonInGlobalNewButton(false);
			clickAdditionalOptions();
			
			//Smart sleep allow others to share these files
			fluentWaitTextPresent("Allow others to share these files");
			
			//In File Upload dialog enter the name and path to the file to upload
			try {
				fileToUpload(file.getName(), BaseUIConstants.FileInputField);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
			
			//Rename the file
			if(file.getRename() != null){
				clickLinkWait(renameFileSelector(file.getName()));
				//Wait so that the webpage has time to respond to the click.
				try {
					fluentWaitElementVisible(FilesUIConstants.UploadFiles_Name);
				} catch (Exception e){
					log.info("INFO: Clicking the file name didn't perform well. Rename textbox is not present. Will click the file name again");
					clickLinkWait(renameFileSelector(file.getName()));
					fluentWaitElementVisible(FilesUIConstants.UploadFiles_Name);
				}
				clearText(FilesUIConstants.UploadFiles_Name);
				typeText(FilesUIConstants.UploadFiles_Name, file.getRename());
				blurWithJavascript(FilesUIConstants.UploadFiles_Name);
				//change the file name inside the object
				log.info("INFO: Change the name of the file");
				file.setName(file.getRename()+ file.getExtension());
				
				fluentWaitElementVisible(FilesUIConstants.fileRenameMsg);
			}
						
			//Check to see if adding Tag
			if(file.getTags() != null){
				clickLinkWait(FilesUIConstants.UploadFiles_Tag);
				typeText(FilesUIConstants.UploadFiles_Tag, file.getTags());
			}

			//Set Share Level
			log.info("INFO: Select share level");
			clickLinkWait(file.getShareLevel().getShareLink());
			
			//Upload the file
			fluentWaitPresent(FilesUIConstants.fileUpload);
			clickLinkWithJavascript(FilesUIConstants.fileUpload);
			
		}else{
			log.info("INFO: Select Upload Files Button");
			boolean value;
			
			if(product.equalsIgnoreCase("onprem")){
				log.info("INFO: Check to see if the Gatekeeper " +gk_flag_onprem + " setting is enabled");
				if(gkc == null)
					gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
				value = gkc.getSetting(gk_flag_onprem);
				log.info("INFO: Gatekeeper flag " + gk_flag_onprem + " is " + value);

			}
			else {
				log.info("INFO: Check to see if the Gatekeeper " +gk_flag_cloud + " setting is enabled");
				if(gkc == null)
					gkc = GatekeeperConfig.getInstance(driver);
				value = gkc.getSetting(gk_flag_cloud);
				log.info("INFO: Gatekeeper flag " + gk_flag_cloud + " is " + value);
			}	
			
			if(value){
				clickLinkWait(FilesUIConstants.ComFilesAdd_Button);
				clickLinkWithJavascript(FilesUIConstants.ComFilesNewUpload);
			
			}else{

				//TODO: Implement fluent wait that collects only visible elements
				List <Element> addFilebtn = driver.getVisibleElements(FilesUIConstants.ComFilesAddFiles_Button);
				if (addFilebtn.size() > 0) {
					addFilebtn.get(0).click();
				} else {
					if (driver.isElementPresent(FilesUIConstants.ComFilesOverviewAddFirstFile_Button)) {
						clickLinkWait(FilesUIConstants.ComFilesOverviewAddFirstFile_Button);
					} else {
						clickLinkWait(FilesUIConstants.ComFilesOverviewAddFiles_Button);
					}
				}
				fluentWaitTextPresent("Add files to this Community");
				switchToMyComputerInFilePicker();
			}
			
			//In File Upload dialog enter the name and path to the file to upload
			try {
				fileToUpload(file.getName(), BaseUIConstants.FileInputField2);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
			
			//Rename the file
			if(file.getRename() != null){
				clickLinkWait("link=" + file.getName());
				clearText(FilesUIConstants.UploadFiles_Name);
				typeText(FilesUIConstants.UploadFiles_Name, file.getRename());
				
				//change the file name inside the object
				log.info("INFO: Change the name of the file");
				file.setName(file.getRename()+ file.getExtension());
				blurWithJavascript(FilesUIConstants.UploadFiles_Name);
			}
			
			//Check to see if adding Tag
			if(file.getTags() != null){
				clickLinkWait(FilesUIConstants.UploadFiles_Tag);
				typeText(FilesUIConstants.UploadFiles_Tag, file.getTags());
			}
			
			//Share file
			log.info("INFO: Share file");
			if(value){
				fluentWaitPresent(FilesUIConstants.Upload_Button);
				clickLinkWait(FilesUIConstants.Upload_Button);
			}else{
				fluentWaitPresent(BaseUIConstants.Share_Files_Button);
				clickLinkWait(BaseUIConstants.Share_Files_Button);
			}
			

		}
	}
	
	/**
	 * Use when trying to click the upload link a second time after selecting a file to upload
	 * @param file
	 * @param gkc
	 */
	public void reClickUploadLink(BaseFile file, GatekeeperConfig gkc){
		
		// Choosing which link to click on determined by same logic as in 
		// this.upload(BaseFile file, GatekeeperConfig gkc);
		
		if (!file.getComFile()){
			fluentWaitPresent(FilesUIConstants.fileUpload);
			clickLinkWithJavascript(FilesUIConstants.fileUpload);
		}else{
			String gk_flag = "FILES_NESTED_FOLDER";
			String serverURL = APIUtils.formatBrowserURLForAPI(cfg.getTestConfig().getBrowserURL());
			User adminUser = cfg.getUserAllocator().getAdminUser();
			
			if(gkc == null){
				gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
			}
			boolean value = gkc.getSetting(gk_flag);
			
			if(value){
				fluentWaitPresent(FilesUIConstants.Upload_Button);
				clickLinkWait(FilesUIConstants.Upload_Button);
			}else{
				fluentWaitPresent(BaseUIConstants.Share_Files_Button);
				clickLinkWait(BaseUIConstants.Share_Files_Button);
			}
		}
		
	}

	public void multiUpload(BaseFile file) throws Exception {
		

		//Click on Upload button and wait for dialog
		if (driver.getCurrentUrl().contains("files")){
			clickUploadButtonInGlobalNewButton(true);
		}else if (driver.getCurrentUrl().contains("communities")){
			clickShareFiles();
			clickBrowseFilesOnMyComputer();
		}

				
		//Upload the file
		clickLink(FilesUIConstants.Upload_Button);

		//Verify that the success message in the UI
		fluentWaitTextPresent(Data.getData().UploadMessage);
		
	}
	
	/**
	 * add a file to a folder
	 * 
	 */
	public void addToFolder(BaseFile file, BaseFolder folder) {
		String gk_flag_onprem = "FILES_NESTED_FOLDER";
		String gk_flag_cloud = "files-nested-folder";
		boolean value = getGateKeeperValue(gk_flag_onprem, gk_flag_cloud);
		
		//Open the My Files view
		clickMyFilesView();
		
		log.info("INFO: Switch to Details View");
		clickLink(FilesUIConstants.selectViewList);

		log.info("INFO: Select More button");
		clickLink(fileSpecificMore(file));
		
		log.info("INFO: Select add file to folder");
		clickLinkWait(FilesUIConstants.addFileToFolder);
		
		//select My Folders menu in picker
		selectComboValue(FilesUIConstants.pickerMenu, "My Folders");
		
		//Click on the folder to add the file too
		log.info("INFO: Select the folder to add the file too");
		clickFolderItemInFilePicker(folder.getName(), value);
		
		//Click on Add to Folders button
		log.info("INFO: Select the Add to Folder Button");
		clickLinkWait(FilesUIConstants.addToFolder);
	
	}

	/**
	 * add a community file to a community folder
	 * 
	 */
	public void addToComFolder(BaseFile file, BaseFolder folder) {
		String gk_flag_onprem = "FILES_NESTED_FOLDER";
		String gk_flag_cloud = "files-nested-folder";
		boolean value = getGateKeeperValue(gk_flag_onprem, gk_flag_cloud);
		
		//Switch the display from default Tile to Details
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(this);
				
		log.info("INFO: Select More button");
		clickLink(fileSpecificMore(file));
		
		this.fluentWaitPresent(FilesUIConstants.genericMore);
		
		if(isElementPresent(FilesUIConstants.addFileToFolder)){
		  log.info("INFO: Select add file to folder");
		  clickLinkWait(FilesUIConstants.addFileToFolder);
		} else{
		  log.info("INFO: Select more actions menu");
		  List<Element> moreActions = driver.getVisibleElements(FilesUIConstants.genericMore);
		  moreActions.get(0).click();
			
		  log.info("INFO: Select add file to folder");
		  clickLinkWait(FilesUIConstants.MenuAddToFolder);
		}
				
		//Click on the folder to add the file too
		log.info("INFO: Select the folder to add the file too");
		clickFolderItemInFilePicker(folder.getName(), value);
		
		//Click on Add to Folders button
		log.info("INFO: Select the Add to Folder Button");
		clickLinkWait(FilesUIConstants.addToFolder);
	}
	
	/** 
	 * MoveFolder - Move folder to a tagetFolder 
	 * @param BaseFolder targetFolder
	
	 */
	public void moveFolder(BaseFolder targetFolder){

		//Click on the folder to add the file too
		log.info("INFO: Select the folder A to move folder B");
		clickFolderItemInFilePicker(targetFolder.getName(),true);
				
		//Click on Move to Folder button
		log.info("INFO: Click on Move Here button");
		clickLinkWait(FilesUIConstants.DialogmovetoButton);
		
	}
	/** 
	 * trash - select a file and move this to trash
	 * @param Filename - name of file to move to trash
	 */
	public void trash(BaseFile file){

		if (!file.getComFile()){
			//Select the file to move to trash
			clickLinkWait(FilesUIConstants.FileListCheckbox+"[title='"+file.getRename() + file.getExtension()+"']");
	
			//Click on the Move to Trash button
			clickLink(FilesUIConstants.MoveToTrashButton);
					
			//Click OK to move file to trash
			fluentWaitTextPresent("Are you sure you want");
			clickButton(Data.getData().buttonOK);
	
			//Verify that the message appears stating that the file was moved to trash
			log.info("INFO: Validate move to file message");
			fluentWaitTextPresent(file.getName() + Data.getData().DeleteFileMessage);
	
		}else{
			
			log.info("INFO: Select More link");
			selectMoreLinkByFile(file);
			
			//select more actions menu
			log.info("INFO: Select more actions menu");
			List<Element> moreActions = driver.getVisibleElements(FilesUIConstants.genericMore);
			moreActions.get(0).click();

			//select file action
			log.info("INFO: Select the file action move to trash");
			List<Element> actions = driver.getVisibleElements(FilesUIConstants.genericMoveToTrash);
			actions.get(0).click();
			
			//Click OK to move file to trash
			fluentWaitTextPresent("Are you sure you want to move this file to the trash?");
			List<Element> dialogBox = driver.getVisibleElements(FilesUIConstants.okButton);
			dialogBox.get(0).click();
			
			//validate message 
			log.info("INFO: Validate was moved to the trash message");
			fluentWaitTextPresent(file.getName() + Data.getData().DeleteFileMessage);
		}


	}
	

	
	public void share(BaseFolder folder){
		
		//now share this file
		clickLinkWait(FilesUIConstants.shareFolderButton);
		//now chose to share with everyone in the community
		clickLink(FilesUIConstants.shareWithEveryone);
		//Share
		clickButton("Share");

		
	}
	
	public void delete(BaseFolder folder){
		
		clickLink(FilesUIConstants.moreActionsFolderButton);
		clickLink(FilesUIConstants.deleteFolder);
		clickLink(BaseUIConstants.OKButton);
		
	}
	

	/** 
	 * share - share this file
	 * @param BaseFile file - files to share
	 */
	public void share(BaseFile file) {
		
		if (!file.getComFile()) {
			log.info("INFO: Select More button");
			clickLink(fileSpecificMore(file));
			
			if (isElementPresent(FilesUIConstants.shareDropDown)) {
				clickLinkWait(FilesUIConstants.shareDropDown);
				clickLinkWait(FilesUIConstants.shareInDropDown);
			}
			else {
				//click share button
				clickLink(FilesUIConstants.shareLink);
			}
			//now chose to share with everyone in the community
			clickLink(FilesUIConstants.shareWithEveryone);
			//Share
			clickButton("Share");
			//verify the UI message
			isTextPresent("The file was shared successfully.");
		} else {
			log.info("INFO: Select link to add a file to a community");
			clickAddComFileButton();
			
			//select My Files menu in picker
	        selectComboValue(FilesUIConstants.pickerMenu, "My Files");

			String checkboxSelector = String.format(FilesUIConstants.FileCheckbox, file.getName(), file.getName());

			log.info("INFO: Select checkbox next to the file");
			clickLinkWait(checkboxSelector);
			//Note: It was not working to simply click the button because the dialog would reposition as
			//soon as the button was scrolled to and the click would fail. So, first hover over the
			//button so that the dialog is repositioned, and then click it.
			log.info("INFO: Share file");
			Element shareButton = getFirstVisibleElement(FilesUIConstants.ComShareButton);
			shareButton.hover();
			shareButton.click();

			//validate upload message
			fluentWaitTextPresent(Data.getData().ShareMessage);
		}
	}

	public void restore(BaseFile file){
		
		if(!file.getComFile()){
			
		}else{
			//go to view trash link
			clickLink(FilesUIConstants.CommunityViewTrashLink);
			clickLink(FilesUIConstants.ActionTwist);
			clickLink(FilesUIConstants.ClickForActionsOptionRestore);
			fluentWaitTextPresent("The file " + file.getName() + " was restored.");
			log.info("INFO: File " + file.getName() + " has being successfully restored");
			
		}
		
	}

	public void delete(BaseFile file){
		
		if(!file.getComFile()){
			
			//Click on the Trash link in the nav to open the trash view
			if(cfg.getUseNewUI())
				clickLinkWd(By.xpath(FilesUIConstants.trashSecTopNav));
			else
				clickLink(FilesUIConstants.TrashLinkinNav);
			ui.closeGuidedTourPopup();
			
			//validate it is in the trash view
			log.info("INFO: Validate file is in trash.");
			driver.isTextPresent(file.getName());

			//Empty Trash
			log.info("INFO: Empty trash");
			clickLink(FilesUIConstants.EmptyTrash);
			
			//Confirm to Empty the Trash
			log.info("INFO: Confirm empty the trash");
			clickLink(BaseUIConstants.OKButton);

		}else{
			//go to view trash link
			clickLink(FilesUIConstants.CommunityViewTrashLink);
			clickLink(FilesUIConstants.ActionTwist);
			clickLink(FilesUIConstants.ClickForActionsOptionDelete);
			clickButton(Data.getData().buttonDelete);
			fluentWaitTextPresent(file.getName() + " was deleted.");
			log.info("INFO: File " + file.getName() + " has being successfully deleted");

		}
		
	}
	
	
	/**
	 * pin -
	 * @param file
	 */
	public void pin(BaseFile file) {
		String gk_flag_onprem = "FILES_ENABLE_NEW_PIN_ICON";
		String gk_flag_cloud = "files-enable-new-pin-icon";
		boolean value = getGateKeeperValue(gk_flag_onprem, gk_flag_cloud);
		
		//Pin the file
		log.info("INFO: Pin the file");
		if (value) {
			 getFirstVisibleElement(FilesUIConstants.PinFileNewPinIcon).click();
		} else {
			 getFirstVisibleElement(FilesUIConstants.PinFile).click();
		}
	}
	
	/**
	 * like -
	 * @param file
	 */
	public void like(BaseFile file) {

		//Like the file
		log.info("INFO: Like the file");
		getFirstVisibleElement(FilesUIConstants.LikeFile).click();
		clickLinkWait(FilesUIConstants.PopupLikeFile);
	}
	
	/**
	 * comment -
	 * @param file
	 */
	public void comment(String comment) {
		
		//Add a comment
		log.info("INFO: Select add a comment link");
		clickLink(FilesUIConstants.AddACommentLink);
		typeNativeInCkEditor(Data.getData().commonComment);
		
		//Save comment
		clickSaveButton();
		fluentWaitTextPresent(Data.getData().commonComment);
	}
	
	/**
	 * This method will go to the "Comments" tab of the File Overlay and post a comment
	 * @param sComment - The comment to be posted
	 */
	public void addFileOverlayComment(String sComment){
		
		log.info("INFO: Now posting a comment to the file using the file details overlay 'Comments' tab");
		
		// Navigate to the iFrame for posting comments in the file details overlay
		switchToFileOverlayCommentFrame();
		
		log.info("INFO: Now entering (with no delay) the text content '" + sComment + "' into the input field");
		driver.switchToActiveElement().type(sComment);
		
		log.info("INFO: Switching back to main frame");
		driver.switchToFrame().returnToTopFrame();	
			
        log.info("INFO: Select post");
		clickLinkWait(FilesUIConstants.FileOverlayCommentPost);
	}
	
	/**
	 * Switches the focus of Selenium to the Comments iframe in the file details overlay.
	 */
	public void switchToFileOverlayCommentFrame() {
		
		log.info("INFO: Clicking on the 'Comments' tab in the file details overlay");
		clickLinkWait(FilesUIConstants.FileOverlayCommentsTab);
		
		log.info("INFO: Now switching to the comment frame in the file details overlay");
		Element commentframe = driver.getSingleElement(FilesUIConstants.FileOverlayCommentInputBox);
		driver.switchToFrame().selectFrameByElement(commentframe);
		
		log.info("INFO: Ensure that the comment input field is visible before proceeding");
		fluentWaitElementVisible(FilesUIConstants.FilesCommentTextField);
		
		log.info("INFO: Click into the comment input field");
		driver.getSingleElement(FilesUIConstants.FilesCommentTextField).click();
	}
	
	public void following(BaseFile file) {
		
		boolean filesView = driver.isElementPresent(FilesUIConstants.MyFilesTitleLink);
		
		if(filesView){
			clickLink(fileSpecificMore(file));
			
			//select more actions menu
			log.info("INFO: Select more actions menu");
			List<Element> moreActions = driver.getVisibleElements(FilesUIConstants.genericMore);
			moreActions.get(0).click();
		}else{
			
			//select more actions menu
			log.info("INFO: Select more actions menu");
			clickLinkWait("css=button[title='More Actions']");
		}
		
		//select file action
		log.info("INFO: Select the file action");
		getFirstVisibleElement("css=tbody[class='dijitReset'] td:contains(Follow)").click();		
		
		//close more
		if(filesView){
			clickLinkWait(FilesUIConstants.hideLink);
		}
	}
	
	/**
	 * Uses the UI to follow a file in any file view (eg My Files, Public Files etc)
	 * 
	 * @param fileToFollow - A BaseFile instance of the file which is to be followed
	 * @param leftNavSelection - The view to be selected from the Files screen left nav
	 * @return - True if the file has been successfully followed, false otherwise
	 */
	public boolean followFileAnyView(BaseFile fileToFollow, String leftNavSelection) {
		log.info("INFO: Processing following the file with filename: " + fileToFollow.getRename() + fileToFollow.getExtension());
		
		log.info("INFO: Selecting the following view from the left nav: " + leftNavSelection);
		clickLinkWait(leftNavSelection);
		Files_Display_Menu.DETAILS.select(this);
		
		boolean isFilePresent = driver.isTextPresent(fileToFollow.getRename() + fileToFollow.getExtension());
		
		if(isFilePresent) {
			log.info("INFO: Opening the file overlay and following the file");
			
			// Click on the filename to open the file overlay
			clickLinkWait(getUploadFileName(fileToFollow));
			
			log.info("INFO: Waiting for the file overlay to load");
			fluentWaitPresent(FilesUIConstants.PinFile_FileOverlay);
			
			// Click on the "..." drop down menu and select "Follow"
			clickLinkWait(FilesUIConstants.FileOverlayMoreActions);
			clickLinkWait(FilesUIConstants.FileOverlayMoreActionsFollow);
			fluentWaitTextPresent(FilesUIConstants.FOLLOWING_FILE_YOU);
			
			// Close the file overlay again
			clickLinkWait(FilesUIConstants.FileOverlayClose);
			
			log.info("INFO: File successfully followed");
			return true;
		} else {
			log.info("INFO: Could not locate the filename - cannot follow the file.");
			return false;
		}
	}
	
	/** 
	 * CreateFolder - create folder with folder name, description and access level
	 * @param FolderName
	 * @param waitForPageLoaded true to wait for page to load. Otherwise do not wait for page to complete loaded. 
	 * @param ShareWithOption
	 * @throws Exception
	 */
	public void create(BaseFolder folder, boolean waitForPageLoaded){

		//Click on the New Folder button
		clickNewFolderInGlobalNewButton(false);

		//Fill in the form
		typeText(FilesUIConstants.CreateFolderName, folder.getName());
		if(folder.getDescription() != null){
		    typeText(FilesUIConstants.CreateFolderDescription, folder.getDescription());
		}
				
		if(folder.getAccess() != null && Access.PUBLIC.equals(folder.getAccess())){
			clickLink(FilesUIConstants.shareWithEveryone);
		}
		
		//Save the form
		clickCreateButton();
		
		if (waitForPageLoaded)
			waitForPageLoaded(driver);
		
		fluentWaitTextPresent("Successfully created "+folder.getName()+".");
	}
	
	public void create(BaseFolder folder) {
		this.create(folder, true);
	}
	
	
	/** 
	 * createSubFolder - create subfolder with folder name, description 
	 * @param FolderName
	 * @param FolderDescription
	 * @throws Exception
	 */
	public void createSubFolder(BaseFolder subfolder){
		
		//Fill in the form
		typeText(FilesUIConstants.CreateFolderName, subfolder.getName());
		if(subfolder.getDescription() != null){
			typeText(FilesUIConstants.CreateFolderDescription, subfolder.getDescription());
		}
		//Add check to make sure 'Sharing options' are not visible in the dialog to create sub-folder
		Boolean isExist = isElementPresent(FilesUIConstants.FolderAccessNoOne);
		if(isExist){
			//'FolderAccessNoOne' is exist but the status is disabled
			Assert.assertTrue(isElementPresent(FilesUIConstants.FolderAccessNoOne+"[disabled]"),"sub-folder can set its ACL");
		}else{
			Assert.assertFalse(isExist,"sub-folder can set its ACL");
		}
		
		//Save the form
		clickCreateButton();
		waitForPageLoaded(driver);
		fluentWaitTextPresent("Successfully created "+subfolder.getName()+".");
	}
	
	/** 
	 * AddFolder - create folder with folder name, description and access level
	 * @param FolderName
	 * @param FolderDescription
	 * @param ShareWithOption
	 * @throws Exception
	 */
	public void add(BaseFolder folder) {

		// Click on the New Folder button
		if (!folder.getPerformFromOverview()) {
			if(clickAddButtonInCommunityGlobal())
				clickLinkWithJavascript(FilesUIConstants.NEW_FOLDER_IN_GLOBAL_ADD);
			else
				clickLink(FilesUIConstants.AddFolders_Button);
		} else {
			clickLink(FilesUIConstants.AddFoldersOverview_Button);
		}
		
		fluentWaitPresent(FilesUIConstants.CreateFolderName);

		// Fill in the form
		typeText(FilesUIConstants.CreateFolderName, folder.getName());
		if(folder.getDescription()!= null){
			typeText(FilesUIConstants.CreateFolderDescription, folder.getDescription());
		}
		// Save the form
		clickLink(FilesUIConstants.AddButton);
		waitForPageLoaded(driver);
		fluentWaitTextPresent("Successfully created " + folder.getName() + ".");
	}
	
	/**
	 * MultipleFileUpload - upload multiple files at the same time renaming to ensure all have different names
	 * @param numToUpload - number of files to upload
	 * @param fileUploadName - file name to upload
	 * @param fileRenamed - rename of file - incremented by 1 
	 * @throws Exception
	 */
	public void multipleFileUpload(int numToUpload, String fileUploadName, String fileRenamed) throws Exception {

		//Click on Upload button and wait for dialog
		if (driver.getCurrentUrl().contains("files")){
			clickUploadButtonInGlobalNewButton(true);
		}else if (driver.getCurrentUrl().contains("communities")){
			clickShareFiles();
			clickBrowseFilesOnMyComputer();
		}
		clickAdditionalOptions();	
		/*
		 * Loop now to add multiple files for upload
		 */
		try {
			int i = 0;
			while (i < numToUpload) {
				//In File Upload dialog enter the name and path to the file to upload
				fileToUpload(fileUploadName, BaseUIConstants.FileInputField);
				
				//Rename the file
				clickLink("link="+fileUploadName);
				clearText(FilesUIConstants.nameLinkInUploadDialog);
				typeText(FilesUIConstants.nameLinkInUploadDialog, fileRenamed+i);
								
				//Click into tags field in order to get the focus out of the rename field
				driver.getSingleElement(FilesUIConstants.UploadFiles_Tag).click();
				i++;		
			}
		} catch (Exception e) {
			log.error("ERROR: problems uploading multiple files: "+e);
		}	
				
		//Upload the file
		driver.getSingleElement(FilesUIConstants.Upload_Button).click();

		
	}
	
	/**
	 * MultipleFileUpload - upload multiple files at the same time ensuring they all have different names
	 * @param files - BaseFile to upload
	 * @throws Exception
	 */
	public void multipleFileUpload(BaseFile... files) {

		//Click on Upload button and wait for dialog
		if (driver.getCurrentUrl().contains("files")){
			clickUploadButtonInGlobalNewButton(true);
		}else if (driver.getCurrentUrl().contains("communities")){
			//clickAddComFileButton();
			//fluentWaitTextPresent("Add Files to this Community");
			clickLinkWait("css=button[aria-label='Add']");
			clickLinkWait("css=tr[aria-label='New Upload... ']");
			//switchToMyComputerInFilePicker();
		}
		
		/*
		 * Loop now to add multiple files for upload
		 */
		for (BaseFile file : files) {
			String fileUploadName = file.getName();
			//In File Upload dialog enter the name and path to the file to upload
			try {
				if (file.getComFile()) {
					fileToUpload(fileUploadName, BaseUIConstants.FileInputField2);
				} else {
					fileToUpload(fileUploadName, BaseUIConstants.FileInputField);
				}
			} catch (Exception e) {
				Assert.fail("ERROR: The file " + fileUploadName + " could not be uploaded. "
						+ "Exception thrown: " + e.getMessage());
				return;
			}

			if (file.getRename() != null) {
				//Rename the file
				clickLink("link="+fileUploadName);
				clearText(FilesUIConstants.nameLinkInUploadDialog);
				typeText(FilesUIConstants.nameLinkInUploadDialog, file.getRename());
			}

			//Click on an image to get focus off the text field
			//I use the *last* visible icon for uploaded files to avoid trying
			//to click on something that has scrolled offscreen
			List<Element> uploadIcons = driver.getVisibleElements(FilesUIConstants.uploadedFileIcon);
			int nIcons;
			if (uploadIcons == null || (nIcons = uploadIcons.size()) < 1) {
				Assert.fail("ERROR: No visible elements found for selector: " + FilesUIConstants.uploadedFileIcon);
			} else {
				log.info("INFO: Clicking on element " + (nIcons - 1) + " of all elements matching selector: " +
						 FilesUIConstants.uploadedFileIcon);
				uploadIcons.get(nIcons - 1).click();
			}
			
			fluentWaitTextPresent("The original file name was " + fileUploadName);
		}
				
		//Upload the file
		
		clickLink(FilesUIConstants.Upload_Button);
		

		//Verify that the success message in the UI
		fluentWaitTextPresent(Data.getData().UploadMessage);
		
	}

	public void changeViewAndSelectFile(String viewName)throws Exception{
		if (viewName=="Detail"){
			clickLink(FilesUIConstants.selectViewDetail);
		}else if (viewName=="List"){
			clickLink(FilesUIConstants.selectViewList);
		}
		//select the file checkbox
		if (driver.getCurrentUrl().contains("files")){
			clickLink("css=input[id='list_0']");
		}else if (driver.getCurrentUrl().contains("communities")){
			clickLink("css=input[id='list_0']");
		}
	}
	
	public void changeViewAndSelectAllFiles(FilesListView view)throws Exception{
		clickLink(view.getActivateSelector());
		this.fluentWaitPresent(view.getIsActiveSelector());
		//select the file checkbox
		clickLink("css=input[title='Select all items']");
	}
	
	public boolean deleteAllFilesInDirectory(File directory){
		if(directory == null)
			return false;
		if(!directory.exists())
			return false;
		if(!directory.isDirectory())
			return false;
		
		String[] list = directory.list();
		for(String entry : list){
			File file = new File(directory, entry);
			if(!file.delete())
				return false;
		}
		
		return true;
	}
	
	public boolean deleteOldFilesInDirectory(String directoryPath) {
		Date now = new Date();
		Calendar twoHoursAgo = Calendar.getInstance();
		twoHoursAgo.add(Calendar.HOUR, -2);
		
		if (!cfg.getTestConfig().serverIsGridHub()) {
			File directory = new File(directoryPath);
			
			if(!directory.exists())
				return false;
			if(!directory.isDirectory())
				return false;
			
			String[] list = directory.list();
			for(String entry : list){
				File file = new File(directory, entry);
				Calendar fileLastModified = Calendar.getInstance();
				fileLastModified.setTimeInMillis(file.lastModified());
				if(fileLastModified.before(twoHoursAgo)) {
					if(!file.delete())
						return false;
				}
			}
			return true;
		} else {
			FTPClient ftp = new FTPClient();
			WebDriver wd = (WebDriver) driver.getBackingObject();		
			String remoteNode = returnGridNodeName(wd,cfg.getTestConfig().getServerHost(),Integer.parseInt(cfg.getTestConfig().getServerPort()));
			int port =  cfg.getFtpPort();
			String ftpUser = cfg.getFtpUsername();
			String ftpPass = cfg.getFtpPassword();
			
			if (!FTPhelper.connectAndLogin(ftp, remoteNode, port, ftpUser, ftpPass)) {
				return false;
			}
			
			if (!FTPhelper.setBinaryType(ftp)) {
				return false;
			}
			
			if (!FTPhelper.changeDirectory(ftp, directoryPath)) {
				return false;
			}
			
			ArrayList<String> oldFileNames = new ArrayList<String>();
			
			List<FTPFile> dir = FTPhelper.listFiles(ftp);
			if (dir == null) {
				return false;
			}
			
			for (FTPFile file : dir) {
				if (file.getType() != FTPFile.FILE_TYPE)
					continue;
				Calendar fileLastModified = file.getTimestamp();
				//According to the javadoc "getTimestamp() Returns the file timestamp.
				//This is usually the last modification time." 
				if (fileLastModified != null && fileLastModified.before(twoHoursAgo)) {
					log.info("INFO: File " + file.getName() + " is old: last modified " +
							fileLastModified.getTime() + ", current time is :" + now);
					oldFileNames.add(file.getName());
				}
			}
			
			for (String oldFileName : oldFileNames) {
				if (!FTPhelper.deleteFile(ftp, oldFileName)) {
					break;
				}
			}
			
			// Close the server connection
			try {
				ftp.disconnect();
			} catch (IOException e2) {}
			
			return true;
		}
	}
	
	/**
	 * Given a filename, attempt to create a copy of the file in the uploads
	 * directory with a randomly generated name. The copy of the file will
	 * be deleted when the Java virtual machine exits. That is, it is not
	 * necessary to manually clean up the copies. 
	 * @param fromFilename - the name of the file in the uploads dir to copy
	 * @return The name of the copy of the file on success, null on failure
	 */
	public String createTempFileForUpload(String fromFilename) {
		
		int lastDot = fromFilename.lastIndexOf(".");
		if (lastDot < 0) {
			log.info("INFO: Could not determine extension of filename: " + fromFilename);
			return null;
		}
		String extension = fromFilename.substring(lastDot);
		if (extension.contains(File.separator)) {
			log.info("INFO: Could not determine extension of filename: " + fromFilename);
			return null;
		}
		
		String newName = "TEMP_" + Helper.genStrongRand() + extension;

		String originalPath = FilesUI.getFileUploadPath(fromFilename, cfg);
		String copyPath = FilesUI.getFileUploadPath(newName, cfg);
		File originalFile = null;
		File copyFile = null;
		boolean copySuccess = false;

		try {
			originalFile = new File(originalPath);
			copyFile = new File(copyPath);
			FileUtils.copyFile(originalFile, copyFile);
			copyFile.deleteOnExit();
			log.info("INFO: Successfully copied file " + originalPath + " to " + copyPath);
			copySuccess = true;
		} catch (IOException ioe) {
			log.info("INFO: Could not copy file " + originalPath + " to " +
					copyPath + " Exception was: " + ioe.getMessage());
			return null;
		}
		
		if (copySuccess && cfg.getTestConfig().serverIsGridHub()) {
			FTPClient ftp = new FTPClient();
			WebDriver wd = (WebDriver) driver.getBackingObject();		
			String remoteNode = returnGridNodeName(wd,cfg.getTestConfig().getServerHost(),Integer.parseInt(cfg.getTestConfig().getServerPort()));
			int port =  cfg.getFtpPort();
			String ftpUser = cfg.getFtpUsername();
			String ftpPass = cfg.getFtpPassword();
			
			if (!FTPhelper.connectAndLogin(ftp, remoteNode, port, ftpUser, ftpPass)) {
				return null;
			}
			
			if (!FTPhelper.setBinaryType(ftp)) {
				return null;
			}
			
			if (!FTPhelper.changeDirectory(ftp, cfg.getFtpUploadsDir())) {
				return null;
			}
			
			if (!FTPhelper.putFile(ftp, copyFile, newName)) {
				return null;
			}
			
			// Retrieve the current directory, delete all temporary files
			// older than one hour, and verify the uploaded file is present
			Date now = new Date();
			Calendar twoHoursAgo = Calendar.getInstance();
			twoHoursAgo.setTime(now);
			twoHoursAgo.add(Calendar.HOUR, -2);
			
			ArrayList<String> oldFileNames = new ArrayList<String>();
			boolean hasFoundUpload = false;
			
			List<FTPFile> dir = FTPhelper.listFiles(ftp);
			if (dir == null) {
				return null;
			}

			for (FTPFile file : dir) {
				if (file.getType() != FTPFile.FILE_TYPE)
					continue;
				String dirEntry = file.getName();
				if (dirEntry.equals(newName)) {
					log.info("INFO: Verified uploaded file " + newName + 
							" is present on remote server.");
					hasFoundUpload = true;
				} else if (dirEntry.startsWith("TEMP_")) {
					if (file.getTimestamp().before(twoHoursAgo)) {
						oldFileNames.add(file.getName());
					}
				}
			}
			
			for (String oldFileName : oldFileNames) {
				if (!FTPhelper.deleteFile(ftp, oldFileName)) {
					break;
				}
			}
			
			// Close the server connection
			try {
				ftp.disconnect();
			} catch (IOException e2) {}
			
			if (!hasFoundUpload) {
				return null;
			}
		} 
		
		return newName;
	}

	
	public void setupDirectory()throws Exception{
		if(cfg.getTestConfig().getServerHost().contains("localhost")){
			String downloadDir = (cfg.getTestConfig().getBrowserEnvironment().constructAbsolutePathToDirectoryFromRoot(cfg.getBrowserEnvironmentBaseDir(), "downloads"));
			File downloadFolder = new File(downloadDir);
			if(!downloadFolder.exists()){
				downloadFolder.mkdir();
			}else{
				deleteOldFilesInDirectory(downloadDir);
			}
			log.info("INFO: download directory is: "+downloadDir);
		}else{
			String downloadDir = cfg.getFtpDownloadsDir();
			if (!deleteOldFilesInDirectory(downloadDir)) {
				log.info("INFO: Could not setup directory by FTP, throwing a Hail Mary pass to Windows shares.");
				WebDriver wd = (WebDriver) driver.getBackingObject();		
				String remoteNode = returnGridNodeName(wd,cfg.getTestConfig().getServerHost(),Integer.parseInt(cfg.getTestConfig().getServerPort()));
				String IPAddress = returnIPAddress(remoteNode);
				String ipAddressForNode = "\\\\"+IPAddress+"";
				String connectToNode = ipAddressForNode+ FilesUIConstants.pathToDownloads;
				log.info("INFO: Node and download folder location is: "+connectToNode);
				File downloadFolder = new File(connectToNode);
				if(!downloadFolder.exists()){
					downloadFolder.mkdir();
				}else{
					deleteAllFilesInDirectory(downloadFolder);
				}
			}
		}
	}
	
	/**
	 * Perform a file download
	 * This is done by setting some ff preferences in the waffle to repress the 
	 * file download dialog from appear and setting the download directory in 
	 * advance
	 */
	public void download(BaseFile file){
		
		boolean isDetailsView;
		driver.turnOffImplicitWaits();
		isDetailsView = driver.isElementPresent(FilesListView.DETAILS.isActiveSelector);
		driver.turnOnImplicitWaits();
		
		if (isDetailsView) {
			log.info("INFO: Select Action menu");
			clickLinkWait(fileSpecificActionMenu(file));
			
			log.info("INFO: Perform a file download");		
			//click on the download button
			List<Element> visibleDownloadMenuItems = driver.getVisibleElements(FilesUIConstants.DownloadActionMenuOption);
			Assert.assertEquals(visibleDownloadMenuItems.size(), 1,
					"ERROR: Wrong number of elements visible for selector {" + FilesUIConstants.DownloadActionMenuOption + "}");
			visibleDownloadMenuItems.get(0).click();
			log.info("INFO: File Download has been performed");
		} else {		
			log.info("INFO: Select More button");
			clickLinkWait(fileSpecificMore(file));
			log.info("INFO: Perform a file download");		
			//click on the download button
			clickLinkWait(FilesUIConstants.DownloadFileLink);
			log.info("INFO: File Download has been performed");
		}
		//This sleep is unavoidable as there is no element to wait for, and 
		//no callcack that I know of when the download is finished.
		sleep(FilesUIConstants.waitAfterDownloadInMilliSeconds);

		Set<String> handles = driver.getWindowHandles();
	    // check if pop a new window
		if(handles.size() > 1) {
			String currentWindow = driver.getWindowHandle();
			for (String winHandle : driver.getWindowHandles()) {
				if(winHandle != currentWindow) {
					driver.switchToWindowByHandle(winHandle);
				    if(driver.isElementPresent(FilesUIConstants.DownloadAnyWayLink)) {
						clickLink(FilesUIConstants.DownloadAnyWayLink);
						sleep(FilesUIConstants.waitAfterDownloadInMilliSeconds);
					}
				}
			}
			// switch back to original window
			driver.switchToWindowByHandle(currentWindow);
		}
	}
	
	public void downloadAsCompressedFile()throws Exception{
		clickLinkWait(FilesUIConstants.DownloadButton);
		fluentWaitTextPresent(Data.getData().DownloadAsCompressed);
		clickButton("Download");
		sleep(FilesUIConstants.waitAfterDownloadInMilliSeconds);
	}
	
	public void downloadAllAsCompressedFile()throws Exception{
		fluentWaitTextPresent(Data.getData().DownloadAllFiles);
		clickLink(FilesUIConstants.DownloadAllLink);
		fluentWaitTextPresent(Data.getData().DownloadAsCompressed);
		clickButton("Download");
		sleep(FilesUIConstants.waitAfterDownloadInMilliSeconds);
	}
	
	public void download(String downloadLinkSelector){
		log.info("INFO: Perform a file download");		
		//click on the download button
		clickLink(downloadLinkSelector);
		log.info("INFO: File Download has been performed");
		
		//This sleep is unavoidable as there is no element to wait for, and 
		//no callcack that I know of when the download is finished.
		sleep(FilesUIConstants.waitAfterDownloadInMilliSeconds);
	}
	
	public void download(Element downloadLinkElement){
		log.info("INFO: Perform a file download");		
		//click on the download button
		downloadLinkElement.click();
		log.info("INFO: File Download has been performed");
		
		//This sleep is unavoidable as there is no element to wait for, and 
		//no callcack that I know of when the download is finished.
		sleep(FilesUIConstants.waitAfterDownloadInMilliSeconds);
	}
	
	public void verifyFileDownloaded(String downloadedFileName)throws Exception{
	
		String downloadDir="";
		if(cfg.getTestConfig().serverIsBrowserStack()){
			WebDriver wd = (WebDriver) driver.getBackingObject();
			JavascriptExecutor jse = (JavascriptExecutor)wd;
			String isFilePresent =jse.executeScript("browserstack_executor: {\"action\": \"fileExists\", \"arguments\": "
					+ "{\"fileName\": \""+downloadedFileName+"\"}}").toString();
			Assert.assertTrue(Boolean.parseBoolean(isFilePresent),"   File not found ");	
		}
		else if(cfg.getTestConfig().getServerHost().contains("localhost")){

			downloadDir= (cfg.getTestConfig().getBrowserEnvironment().constructAbsolutePathToDirectoryFromRoot(cfg.getBrowserEnvironmentBaseDir(), "downloads"));	
			String dirAndFile = downloadDir+"\\"+downloadedFileName;
			log.info("INFO: directory and file name of downloaded file is: "+dirAndFile);
			LocalFileDetector detector = new LocalFileDetector();
			String pathToFile = dirAndFile;
			File f = detector.getLocalFile(pathToFile);
				
			if(f == null || !f.exists()){
				log.info("INFO: Check file for @ "+ f);
				Assert.fail("unable to locate file on filesystem at path [" + pathToFile + "]");
			}else{
				log.info("INFO: file is present @ "+pathToFile);
			} 
	
		}else{
			boolean ftpSucceeded = false;
FTP:		{
				FTPClient ftp = new FTPClient();
				WebDriver wd = (WebDriver) driver.getBackingObject();		
				String remoteNode = returnGridNodeName(wd,cfg.getTestConfig().getServerHost(),Integer.parseInt(cfg.getTestConfig().getServerPort()));
				int port =  cfg.getFtpPort();
				String ftpUser = cfg.getFtpUsername();
				String ftpPass = cfg.getFtpPassword();
				
				if (!FTPhelper.connectAndLogin(ftp, remoteNode, port, ftpUser, ftpPass)) {
					break FTP;
				}
				
				if (!FTPhelper.changeDirectory(ftp, cfg.getFtpDownloadsDir())) {
					break FTP;
				}

				List<FTPFile> dir = FTPhelper.listFiles(ftp);
				if (dir == null) {
					break FTP;
				}

				boolean downloadSucceeded = false;
				
				for (FTPFile file : dir) {
					if (file.getType() != FTPFile.FILE_TYPE)
						continue;
					String dirEntry = file.getName();
					if (dirEntry.equals(downloadedFileName)) {
						log.info("INFO: Verified downloaded file " + downloadedFileName + 
								" is present on remote server.");
						downloadSucceeded = true;
					}
				}

				// Close the server connection
				try {
					ftp.disconnect();
				} catch (IOException e2) {}

				ftpSucceeded = true;
				Assert.assertTrue(downloadSucceeded, "ERROR: The list of files " +
						"on the grid node " + remoteNode + " was successfully retrieved " +
						"by FTP, but the file to download '" + downloadedFileName +
						"' was not present.");
			} // End of FTP: block, "break FTP" will resume control here.
			if (!ftpSucceeded) {
				log.info("INFO: Could not get file list by FTP, attempting web service.");	
				String downloadsUrl;
				if (cfg.getTestConfig().serverIsLegacyGrid())  {
					WebDriver wd = (WebDriver) driver.getBackingObject();	
					String remoteNode = returnGridNodeName(wd,cfg.getTestConfig().getServerHost(),Integer.parseInt(cfg.getTestConfig().getServerPort()));
					String IPAddress = returnIPAddress(remoteNode);
					downloadsUrl = "http://" + IPAddress + ":8000" + FilesUIConstants.pathToDownloads;
				} else {
					// old file_server property in xml contains /videos, strip it in case
					// it's accidentally provided for new grid as well
					String fileServer = cfg.getFileServer();
					fileServer = fileServer.contains("/videos") ? fileServer.replace("/videos", "") : fileServer; 
					downloadsUrl = "http://" + fileServer + ":" + cfg.getFileServerPort() + FilesUIConstants.pathToDownloads + "/" + Utils.getThreadLocalUniqueTestName().replace(".", "_");
				}	

				log.info("INFO: Node and download folder location is: "+ downloadsUrl);
				String response = Helper.getRequestString(downloadsUrl);
				if(response == null || !response.contains(downloadedFileName)){	
					Assert.fail("unable to locate file '" + downloadedFileName +"' on filesystem out of '" + response + "' at path [" + downloadsUrl + "].\n" + 
							"Webserver might not be running on the node. Check resources folder for webserver.jar \n" + 
							"Run java -jar webserver.jar on all grid nodes");
				}else{
					log.info("INFO: file is present @ "+downloadsUrl);
				}
			}
		}
	}
	
	public void unzipFileAndVerify(String zipFile, String outputFolder)throws Exception{
		String downloadDir = (cfg.getTestConfig().getBrowserEnvironment().constructAbsolutePathToDirectoryFromRoot(cfg.getBrowserEnvironmentBaseDir(), "downloads"));
		byte[] buffer = new byte[1024];

		if(cfg.getTestConfig().getServerHost().contains("localhost")){
			try{
				//create output directory is not exists
				File folder = new File(outputFolder);
				if(!folder.exists()){
					folder.mkdir();
				}
				//get the zip file content
				ZipInputStream zis = new ZipInputStream(new FileInputStream(downloadDir+"\\"+zipFile));
				//get the zipped file list entry
				ZipEntry ze = zis.getNextEntry();
				while(ze!=null){	 
					String fileName = ze.getName();
					File newFile = new File(outputFolder + File.separator + fileName);
					log.info("file unzip: "+newFile.getAbsolutePath());
					//create all non exists folders
					//else you will hit FileNotFoundException for compressed folder
					new File(newFile.getParent()).mkdirs();	 
					FileOutputStream fos = new FileOutputStream(newFile);             	 
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();   
					ze = zis.getNextEntry();
				}
				zis.closeEntry();
				zis.close();
				log.info("INFO: unzipping the zipped file is done");
			}
			catch(IOException ex){
				ex.printStackTrace(); 
				Assert.fail("unable to uncompress file[" + zipFile + "] to [" + outputFolder + "]");
			}
		}else{
			boolean ftpSucceeded = false;
FTP:		{
				FTPClient ftp = new FTPClient();
				WebDriver wd = (WebDriver) driver.getBackingObject();		
				String remoteNode = returnGridNodeName(wd,cfg.getTestConfig().getServerHost(),Integer.parseInt(cfg.getTestConfig().getServerPort()));
				int port =  cfg.getFtpPort();
				String ftpUser = cfg.getFtpUsername();
				String ftpPass = cfg.getFtpPassword();
				
				if (!FTPhelper.connectAndLogin(ftp, remoteNode, port, ftpUser, ftpPass)) {
					break FTP;
				}
				
				if (!FTPhelper.changeDirectory(ftp, cfg.getFtpDownloadsDir())) {
					break FTP;
				}
				
				if (!FTPhelper.setBinaryType(ftp)) {
					break FTP;
				}
				
				int nEntries = FTPhelper.verifyZipFile(ftp, zipFile);
				if (nEntries == -1) {
					break FTP;
				}

				try {
					ftp.disconnect();
				} catch (IOException e2) {}

				ftpSucceeded = true;
				Assert.assertTrue(nEntries > 0, "ERROR: Zip file " + zipFile + " was successfully retrieved " +
						"but had no valid entries!");
				log.info("INFO: Found at least one valid entry in zip file " + zipFile);
				return;
			}  // End of FTP: block, "break FTP" will resume control here.
			if (!ftpSucceeded) {
				String connectToNode;
				String zipFileLocation;
				
				if (cfg.getTestConfig().serverIsLegacyGrid())  {
					log.info("INFO: Could not check zip file by FTP, making a futile attempt to use Windows shares.");
					WebDriver wd = (WebDriver) driver.getBackingObject();		
					String remoteNode = returnGridNodeName(wd,cfg.getTestConfig().getServerHost(),Integer.parseInt(cfg.getTestConfig().getServerPort()));
					String IPAddress = returnIPAddress(remoteNode);
					String ipAddressForNode = "\\\\"+IPAddress+"";
					connectToNode = ipAddressForNode+ FilesUIConstants.pathToDownloads;
					zipFileLocation = connectToNode+"\\"+zipFile;
				} else {
					log.info("INFO: Could not check zip file by FTP. Attempt to download the zip file.");
					String fileServer = cfg.getFileServer();
					fileServer = fileServer.contains("/videos") ? fileServer.replace("/videos", "") : fileServer; 
					connectToNode = "http://" + fileServer + ":" + cfg.getFileServerPort() + FilesUIConstants.pathToDownloads + "/" + Utils.getThreadLocalUniqueTestName().replace(".", "_");

					String zipFilePath = connectToNode+"/"+zipFile;
					InputStream inputStream = new URL(zipFilePath).openStream();
					File tmpDownloadFile = File.createTempFile(zipFile, ".zip");
					tmpDownloadFile.deleteOnExit();
					Files.copy(inputStream, Paths.get(tmpDownloadFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
					zipFileLocation = tmpDownloadFile.getAbsolutePath();
				}
					
				try {
					//create a temp directory to unzip the test file
					Path tmpUnzipFolder = Files.createTempDirectory(outputFolder);
					
					//get the zip file content
					ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFileLocation));
					//get the zipped file list entry
					ZipEntry ze = zis.getNextEntry();
					while(ze!=null){	 
						String fileName = ze.getName();
						File newFile = new File(tmpUnzipFolder.toString() + File.separator + fileName);
						log.info("file unzipped: "+newFile.getAbsolutePath());
						//create all non exists folders
						//else you will hit FileNotFoundException for compressed folder
						new File(newFile.getParent()).mkdirs();	 
						FileOutputStream fos = new FileOutputStream(newFile);             	 
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						fos.close();   
						ze = zis.getNextEntry();
					}
					zis.closeEntry();
					zis.close();
					log.info("INFO: all files have being uncompressed successfully");
				}
				catch(IOException ex){
					ex.printStackTrace(); 
					log.error(ex.getMessage());
					Assert.fail("unable to uncompress file[" + zipFile + "] to [" + outputFolder + "]");
				}
			}
		}
	}
	
	/**
	 * selectFolderCheckmark - Given a folder, select the folder by check box
	 * @param folder - the folder
	 */
	public void selectFolderCheckmark(BaseFolder folder){
		int folderPosition = 0;
		log.info("INFO: Locate the More link associated with our folder");
		String folderName = folder.getName();
		List<Element> folders = driver.getVisibleElements(FilesUIConstants.folderElements);
		boolean foundFolder = false;
		if(folders.size() > 0){
			for(Element folderElement : folders){
				if(folderElement.getText().contains(folderName)){
					log.info("INFO: Select check box");
					folderPosition = Integer.parseInt(folderElement.getAttribute("dnddata"));
					Element checkmark = getFirstVisibleElement(getSelectorForCheckbox(folderPosition));
					if(!checkmark.isSelected()){
						checkmark.click();
					}
					foundFolder = true;
					break;
				}
				
			}
		}else{
			Assert.fail("No folder elements were found on the page");
		}
		Assert.assertTrue(foundFolder, "The checkbox for the folder " + folderName + " was not found!");
	}
		
	/**
	 * selectFolderMoreLink - Given a folder, select the folder by more link
	 * @param folder - the folder
	 */
	public void selectFolderMoreLink(BaseFolder folder) {
		String folderName = folder.getName();
		log.info("INFO: Locate the More link associated with folder");
        List<Element> folders; 
        folders = driver.getVisibleElements(FilesUIConstants.folderElements);
        boolean foundFolder = false;
        
        if(folders.size()>0) {
        	for(Element folderElement: folders){
        		if(folderElement.getText().contains(folderName)){
        			log.info("INFO: Select more link");
        			clickLinkWait(getSelectorForMoreLink(folderName));
        			foundFolder = true;
        		    break;			
        		}
        	}
        }else{
			Assert.fail("No folder elements were found on the page");
		}
		Assert.assertTrue(foundFolder, "The more link for the folder " + folderName + " was not found!");
	}
	
	public void selectFileCheckmark(BaseFile file) {
		int filePostion = 0;

		log.info("INFO: Locate the More link associated with our file");
		List<Element> files = driver.getVisibleElements(FilesUIConstants.fileElements);
		String fileName;
		boolean foundFile = false;
		if (file.getRename() == null) {
			fileName = file.getName() + file.getExtension();
		} else {
			fileName = file.getRename() + file.getExtension();
		}

		if(files.size() > 0) {
			for(Element fileElement : files){
				if(fileElement.getText().contains(fileName)){
					log.info("INFO: Select more link");
					filePostion = Integer.parseInt(fileElement.getAttribute("dnddata"));
					Element checkmark = getFirstVisibleElement(fileSpecificCheckmark(filePostion));
					if (!checkmark.isSelected()) {
						checkmark.click();
					}
					foundFile = true;
					break;
				}
			}
		} else {
			Assert.fail("No file elements were found on the page");
		}
		
		Assert.assertTrue(foundFile, "The checkbox for the file " + fileName + " was not found!");
	}
	
    public String getCopiedLink() {

        log.info("INFO: Get copied link");
        
        String copiedLink=driver.getSingleElement(FilesUIConstants.copyLinkText).getAttribute("value").toString();
        
		return copiedLink;
        
    }
    
    public String getShareLink() {

        log.info("INFO: Get copied share link");
        
        String shareLink=driver.getSingleElement(FilesUIConstants.copyShareLink).getAttribute("value").toString();
        
		return shareLink;
        
    }
    
	public void selectMoreLinkByFile(BaseFile file) {
		
		int filePostion = 0;

		log.info("INFO: Locate the More link associated with our file");
		List<Element> files = driver.getVisibleElements(FilesUIConstants.fileElements);

		if(files.size()>1){
			for(Element fileElement: files){
				if(fileElement.getText().contains(file.getName())){
					log.info("INFO: Select more link");
					filePostion = Integer.parseInt(fileElement.getAttribute("dnddata"));
					clickLinkWait(fileSpecificMore(filePostion));
				}
			}
		}else{
			log.info("INFO: Select more link");	
			clickLinkWait(FilesUIConstants.moreLink);
			filePostion = 0;
		}
	}
	
	public void clickAdditionalOptions() {
		log.info("INFO: Clicking Additional Options Button to open addtional Section");
		List <Element> additionalOptionsbtn = driver.getVisibleElements(FilesUIConstants.AddtionalSection_Button);
		if (additionalOptionsbtn.size() > 0) {
			additionalOptionsbtn.get(additionalOptionsbtn.size() - 1).click();
		}
	}
	
	/**
	 * clickAddButtonInCommunityGlobal(): This method will find New button and Click.
	 * 
 	 * @return boolean - true: New button is visible element, false: New button is invisible element. 
	 */
	public boolean clickAddButtonInCommunityGlobal() {
		log.info("INFO: click Add Button in community global");
		List <Element> el = driver.getVisibleElements(FilesUIConstants.GLOBAL_ADD_BUTTON);
		if (null == el || 0 == el.size()) {
			return false;
		} else {
			el.get(0).click();
			return true;
		}
	}
	
	/**
	 * clickNewButtonInGlobal(): This method will find New button and Click.
	 * 
	 * @param boolean - call fluentWaitPresent();
 	 * @return boolean - true: New button is visible element, false: New button is invisible element. 
	 */
	public boolean clickNewButtonInGlobal(Boolean waitForPresent) {
		log.info("INFO: click New Button in global");
		List <Element> el = driver.getVisibleElements(FilesUIConstants.GLOBAL_NEW_BUTTON);
		if (null == el || 0 == el.size()) {
			return false;
		} else {
			el.get(0).click();
			return true;
		}
	}	
	
	/**
	 * clickUploadButtonInGlobalNewButton(): This method will find Upload button and Click.
	 * 
	 * @param boolean - call fluentWaitPresent();
 	 * @return
	 */
	public void clickUploadButtonInGlobalNewButton(Boolean waitForPresent) {
		log.info("INFO: click upload button in global new button");
		boolean isVisibleElement = clickNewButtonInGlobal(waitForPresent);
		if(cfg.getUseNewUI())
		{
			Assert.assertEquals(findElement(createByFromSizzle(FilesUIConstants.NEW_FOLDER_IN_GLOBAL_NEW)).getText(),"Folder");
			Assert.assertEquals(findElement(createByFromSizzle(FilesUIConstants.UPLOAD_FILE_IN_GLOBAL_NEW)).getText(),"Upload");	
		}
		if (isVisibleElement) {
			List <Element> el = driver.getVisibleElements(FilesUIConstants.UPLOAD_FILE_IN_GLOBAL_NEW);
			if (null != el) {
				el.get(0).click();
			}
		} else {
			//This was needed by the old test code which doesn't wait for page load complete event. 
			//The waiting may not be necessary now.
			if(waitForPresent)
				fluentWaitPresent(FilesUIConstants.UploadFiles_Button);
			clickLink(FilesUIConstants.UploadFiles_Button);
		 }
	}
	
	/**
	 * clickNewFolderInGlobalNewButton(): This method will find CreateFolder button and Click.
	 * 
	 * @param boolean - call fluentWaitPresent();
 	 * @return
	 */
	public void clickNewFolderInGlobalNewButton(Boolean waitForPresent) {
		log.info("INFO: click new folder in global new button");
		boolean isVisibleElement = clickNewButtonInGlobal(waitForPresent);
		if (isVisibleElement) {
			fluentWaitElementVisible(FilesUIConstants.NEW_FOLDER_IN_GLOBAL_NEW);
			clickLinkWait(FilesUIConstants.NEW_FOLDER_IN_GLOBAL_NEW);
		} else {
			clickLinkWait(FilesUIConstants.NewFolder_Button);
			//This was needed by the old test code which doesn't wait for page load complete event. 
			//The waiting may not be necessary now.
			if(waitForPresent)
				fluentWaitPresent(FilesUIConstants.CreateFolderName);
		}
	}
	
	/**
	 * clickFolderItemInFilePicker(): This method will find targatFolder and Click.
	 * 
	 * @param String - target folder name
 	 * @return
	 */
	public void clickFolderItemInFilePicker(String folderName, boolean isEnabled) {
		log.info("INFO: click folder item in file picker");
		String folderItemInFilePicker = null;
		if(!isEnabled){
			folderItemInFilePicker = "css=div[dojoattachpoint^='checkboxDiv'][title='" + folderName + "'] input";
			List <Element> el = driver.getVisibleElements(folderItemInFilePicker);
			if (null == el || 0 == el.size()){
				folderItemInFilePicker = "css=td label[title='" + folderName + "']";
				clickLink(folderItemInFilePicker);
			}
			else {
				el.get(0).click();
			}
		}
		else{
			folderItemInFilePicker = "css=div[class='lconnPickerSourceArea'] div[name='title'][title='"+ folderName +"']";
			List <Element> el = driver.getVisibleElements(folderItemInFilePicker);
			if (null != el){
				el.get(0).click();				
			}
			else {
				Assert.fail("No target folder elements were found on the page");
			}
		}		
	}
	
	/**
	 * switchToMyComputerInFilePicker(): This method will find switch button in file picker and click
	 * 
 	 * @return
	 */
	public void switchToMyComputerInFilePicker() {
		log.info("INFO: switch to my computer in file Picker");
		List <Element> el = driver.getVisibleElements(FilesUIConstants.PICKER_SWITCHER);
		if (null == el || 0 == el.size()) {
			clickLink(FilesUIConstants.uploadMyComputer);
		} else {
			el.get(0).click();
		}
	}
	
	public void clickAddComFileButton() {
		log.info("INFO: Clicking link to upload a file to the community");
		//TODO: Implement fluent wait that collects only visible elements
		List <Element> addFilebtn = driver.getVisibleElements(FilesUIConstants.ComFilesAddFiles_Button);
		if (addFilebtn.size() > 0) {
			addFilebtn.get(0).click();
		} else {
			if (driver.isElementPresent(FilesUIConstants.ComFilesOverviewAddFirstFile_Button)) {
				clickLinkWait(FilesUIConstants.ComFilesOverviewAddFirstFile_Button);
			} else {
				clickLinkWait(FilesUIConstants.ComFilesOverviewAddFiles_Button);
			}
		}
		
	}
	
	public void openFolderAndUploadFile(String Newfoldername, String FileUploadName)throws Exception{
		log.info("INFO: Open the folder created and perform a file upload");
		clickLink("css=h4 a[title='"+Newfoldername+"']");
		libraryFileUpload (FileUploadName);
		log.info("INFO: File was uploaded in the folder");
		
	}
	
	/** Start of Library methods */
	public void libraryFileUpload (String FileUploadName)throws Exception{
		log.info("INFO: Perform a file upload");
		//click on the upload a file button 
		clickLinkWait(FilesUIConstants.UploadAFileButton);
		//Perform a file upload
		fluentWaitPresent(FilesUIConstants.CheckInButton);
		fileToUpload(FileUploadName, FilesUIConstants.LibraryFileInput);
		clickLink(FilesUIConstants.CheckInButton);
		fluentWaitTextPresent("Successfully uploaded " + FileUploadName);
		log.info("INFO: File has being uploaded successfully");
	}
	
	public String getPublicOrgNavLink() {
		return getOrganizationName(driver);
	}
	
	/**
	 * getOrganizationName - Will return either organization name for cloud or Public for onPrem.
	 * 	This helps with files/app/public view name displayed in navigation panel.
	 * @return String
	 * @author - Ralph LeBlanc
	 */
	public static String getOrganizationName(RCLocationExecutor driver){
		String orgName="";
		
		//NOTE: As of 6/11/15 onPrem changed to div#lotusLogo for IBM Connections but cloud still uses selector below for company
		if(driver.getElements("css=div.lotusRightCorner div.lotusInner ul.lotusInlinelist li a").size()!=0){
			orgName = driver.getFirstElement("css=div.lotusRightCorner div.lotusInner ul.lotusInlinelist li a").getText();
		} else {
			//NOTE: Adding logic to deal with new Nav bar
			orgName = driver.getSingleElement("css=a[class='org _myorg']").getText();
		}
		
		if (orgName.contentEquals(""))
			orgName = "Public";
		return orgName;
	}
	
	public static FilesUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  FilesUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  FilesUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new  FilesUIProduction(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  FilesUIOnPrem(driver);
		} else if(product.toLowerCase().equals("multi")) {
			return new  FilesUIMulti(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
	public boolean checkNestedFolderGK(){
		String gk_nestedfolder_flag = "files-nested-folder";
		
		//GateKeeper check for files nested gate keeper setting 
		log.info("INFO: Check to see if the Gatekeeper " + gk_nestedfolder_flag + " setting is enabled");
		GatekeeperConfig gkc_fnf = GatekeeperConfig.getInstance(driver);
		boolean value = gkc_fnf.getSetting(gk_nestedfolder_flag);
		log.info("INFO: Gatekeeper flag " + gk_nestedfolder_flag + " is " + value );
		return value;
	}
	
	public void clickMyFilesView(){
		if(cfg.getUseNewUI())
		{
			clickLinkWaitWd(By.xpath(FilesUIConstants.filesAndFoldersSecTopNav), 5, "click on Files and Folders from top nav");
		}
		else
			try {
				//Open the My Files view
				log.info("INFO: Open the My Files view");
				clickLinkWithJavascript(FilesUIConstants.openMyFilesView);
			} catch (TimeoutException te) {
				// Old UI: All Files exists, click on it first to expand
				if(isElementPresent(FilesUIConstants.AllFilesView)) {
					clickLinkWithJavascript(FilesUIConstants.AllFilesView);
				}
				log.info("INFO: Open the My Files view");
				clickLinkWithJavascript(FilesUIConstants.openMyFilesView);
			}
	}
	
	public void clickMyFoldersView(){
		try {
			//Click on My Folder to see your folder
			log.info("INFO: Click on My Folder");
			clickLinkWithJavascript(FilesUIConstants.MyFoldersLeftMenu);
		} catch (TimeoutException te) {
			// Old UI:  If All Folders exists, click on it first to expand
			if(isElementPresent(FilesUIConstants.AllFoldersLeftMenu)) {
				clickLinkWithJavascript(FilesUIConstants.AllFoldersLeftMenu);
			}
			log.info("INFO: Click on My Folder");
			clickLinkWithJavascript(FilesUIConstants.MyFoldersLeftMenu);
		}
	 }
	
	public void clickPinnedFoldersInFoldersView(){
	// If Folders exists, click on it first to expand
      if(isElementPresent(FilesUIConstants.FoldersLeftMenu)) {
          clickLinkWait(FilesUIConstants.FoldersLeftMenu);
      }
      //Click on Pinned Folders to see Pinned Folders
      log.info("INFO: Click on Pinned Folder");
      clickLinkWait(FilesUIConstants.RefinePinnedFoldersLeftMenu);
     }

	public boolean trashNotPresent(BaseFile file) {

		log.info("INFO: Select More link");
		selectMoreLinkByFile(file);

		// select more actions menu
		log.info("INFO: Select more actions menu");
		List<Element> moreActions = driver.getVisibleElements(FilesUIConstants.genericMore);
		moreActions.get(0).click();

		// select file action
		log.info("INFO: Select the file action move to trash");
		List<Element> actions = driver.getVisibleElements(FilesUIConstants.genericMoveToTrash);
		if (actions.size() > 0) {
			return false;
		} else {
			return true;
		}

	}	
		

	/**
	 * getFolderActionMenu(): This method will return locator for folder action menu
	 * 
	 * @param String - Folder name
 	 * @return String - Locator for folder action menu
	 */
	public String getFolderActionMenu(String folderName) {
		//return locator for folder action menu
		return "css=div[title='"+folderName+" folder action menu'] img.lotusArrow.lotusDropDownSprite";
	}
	
	/**
	 * This method will return locator for user's different info
	 * @param String - userInfo
	 */
	public String getUserDetail(String userInfo) {
		return "xpath=//div[@class='recentItems']//a[contains(text(),'" + userInfo + "')]";
	}
	
	/**
	 * This method will return Top header locator
	 * @param String - displayName
	 */
	public String getHeaderName(String displayName) {
		return "xpath=//h1[@id='scene-title']//a[contains(text(),'" + displayName + "')]";
	}
	
	/**
	 * getDownloadedFileName(): This method will return path of downloaded template .zip file
	 */
	public String getDownloadedFileName() {
		TestConfiguration testConfig = cfg.getTestConfig();
		String fileName = "";
		WebDriver webDriver = (WebDriver) driver.getBackingObject();
		
		if(testConfig.browserIs(BrowserType.FIREFOX)){
			webDriver.get("about:downloads");		
			fileName = (String) driver.executeScript("return document.querySelector('#contentAreaDownloadsView .downloadMainArea .downloadContainer description:nth-of-type(1)').value");		
			webDriver.navigate().back();
		}else{
			webDriver.get("chrome://downloads");
			fileName = (String) driver.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div#content #file-link').text");
			webDriver.navigate().back();
		}		
		return fileName;
	}

	/**
	 * createDoc(): This method will select different type of file 
	 * @param String - docType
	 * @param String - fileNameField
 	 * @param BaseFile - file
	 */
	public void createDoc(String docType, String fileNameField,  BaseFile file) {
		
		if(!file.getComFile()) {
			clickLinkWait(FilesUIConstants.GLOBAL_NEW_BUTTON);
		}else {
			clickLinkWait(FilesUIConstants.ComFilesAdd_Button);
		}
		
		if (isElementPresent(docType)) {
			clickLinkWait(docType);
			getFirstVisibleElement(fileNameField).clear();
			getFirstVisibleElement(fileNameField).type(file.getRename());
			clickLinkWait(FilesUIConstants.submitButton);
		}else {
			Assert.assertTrue(false, "Option to create document/spreadsheet/presentation is present");
		}
	}

	/**
	 * editDoc(): This method will edit the created document 
	 * @param String - documentBody
	 * @param String - fileName
 	 * @param String - fileDescription
	 */
	public void editDoc(String documentBody, String fileName, String fileDescription) {

		String originalWindow = driver.getWindowHandle();

		driver.switchToFirstMatchingWindowByPageTitle(fileName);
	    dismissDocsGuidedTour();
		switchToFrameByTitle(FilesUIConstants.docEditor_iFrame);
		getFirstVisibleElement(documentBody).click();
		driver.typeNative(fileDescription);
		publishNowDocs();

		driver.switchToWindowByHandle(originalWindow);
	}
	
	/**
	 * editPresentation(): This method will edit the presentation file
	 * @param String - documentBody
	 * @param String - fileName
 	 * @param String - fileDescription
	 */
	public void editPresentation(String documentBody, String fileName, String fileDescription) {
		Actions action = new Actions((WebDriver) driver.getBackingObject()); 
		String originalWindow = driver.getWindowHandle();

		driver.switchToFirstMatchingWindowByPageTitle(fileName);
		driver.changeImplicitWaits(5);
		Boolean isPresent = driver.getElements(FilesUIConstants.notNowButtonDGT).size()>0;
		driver.turnOnImplicitWaits();
		if (isPresent) {
			getFirstVisibleElement(FilesUIConstants.notNowButtonDGT).click();
			getFirstVisibleElement(FilesUIConstants.closeButtonDGT).click();
		}		
		getFirstVisibleElement(documentBody).click();
		action.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).build().perform();
		getFirstVisibleElement(documentBody).clear();
		getFirstVisibleElement(documentBody).typeWithDelay(fileDescription);		
		switchToFrameByName(FilesUIConstants.pptEditor_iFrame);
		clickLinkWait(FilesUIConstants.docEditor_FileMenu);
		clickLinkWait(FilesUIConstants.selectPublishNow);
		switchToTopFrame();
		clickLinkWait(FilesUIConstants.publishButton);
		driver.close();

		driver.switchToWindowByHandle(originalWindow);
	}

	/**
	 * dismissDocsGuidedTour(): This method will close Docs tour guide popup
	 */
	public void dismissDocsGuidedTour() {
		log.info("Looking for Docs guided tour");
		driver.changeImplicitWaits(5);
		Boolean isPresent = driver.getElements(FilesUIConstants.notNowButtonDGT).size()>0;
		driver.turnOnImplicitWaits();
		if (isPresent) {
			getFirstVisibleElement(FilesUIConstants.notNowButtonDGT).click();
			getFirstVisibleElement(FilesUIConstants.closeButtonDGT).click();
			if(driver.isElementPresent(FilesUIConstants.docEditor_HelpMenu)) {
				clickLinkWait(FilesUIConstants.docEditor_HelpMenu);
			}
		}
		
	}
	
	/**
	 * publishNowDocs(): This method will publish the document after edit
	 */
	public void publishNowDocs(){
		switchToTopFrame();
		clickLinkWait(FilesUIConstants.docEditor_FileMenu);
		clickLinkWait(FilesUIConstants.selectPublishNow);
		clickLinkWait(FilesUIConstants.publishButton);
		driver.close();
	}
	
	/**
	 * createSpreadsheet(): This method will edit the spreadsheet
	 * @param String - DocumentBody
 	 * @param String - fileName
 	 * @param String - fileDescription
	 */
	public void editSpreadsheet(String documentBody, String fileName, String fileDescription) {

		String originalWindow = driver.getWindowHandle();

		driver.switchToFirstMatchingWindowByPageTitle(fileName);
	    dismissDocsGuidedTour();		
		getFirstVisibleElement(documentBody).click();
		driver.typeNative(fileDescription);
		publishNowSpreadSheet();

		driver.switchToWindowByHandle(originalWindow);
	}
	
	/**
	 * publishNowSpreadSheet(): This method will Publish the spreadsheet after edit
	 */
	public void publishNowSpreadSheet(){		
		clickLinkWait(FilesUIConstants.spreadsheetEditor_FileMenu);
		clickLinkWait(FilesUIConstants.ssPublishNow);
		clickLinkWait(FilesUIConstants.ssPublishNowBtn);
		driver.close();
	}
	
	/**
	 * docSpecificEdit(): This method will return locator for Docs Edit
	 * @param BaseFile - File
 	 * @return String - Locator for Edit in Docs
	 */
	public String docSpecificEdit(BaseFile file) {
		return "//tr[@dndelementtitle='" + file.getName() + "']//following-sibling::tr//a[@title='Edit in Docs']";
		
	}
	
	/**
     * getFolderName(): This method will return locator for Folder Name
     * @param String - Folder Name
      * @return String - Locator for FolderName
     */
    public String getFolderName(String folderName) {
        return "css=div[aria-label='folder selection'] [name='title'][title='" + folderName + "']";
    }
    
    /**
     * getSharedFolderCount(): This method will return count for number of folders shared
     * @param String - sharedFolder text
      * @return int - returns count for number of folders file shared with
     */
    public int getSharedFolderCount(String locator) {
    	String sharedFolder = ui.getFirstVisibleElement(locator).getText();
        return Integer.parseInt(sharedFolder.substring(sharedFolder.length() - 1));
    }
    
    /**
     * shareFileWithFolder(): This method will share the file with folder
     * @param BaseFile - baseFolder
     */
    public void shareFileWithFolder(BaseFile baseFolder) {
        
        log.info("INFO: Get the count of number of folder before you share file");
        int beforeCount = getSharedFolderCount(FilesUIConstants.ShareFolderTitle);
        
        log.info("INFO: Click on plus button to share file with folder");
        if(!baseFolder.getComFile()) {
        	ui.clickLinkWithJavascript(FilesUIConstants.SharingAddToFolderStandalone);
        	driver.getSingleElement("css=select[aria-label='Choose folders from:']").useAsDropdown().selectOptionByVisibleText("My Folders");
        }else {
        	ui.clickLinkWithJavascript(FilesUIConstants.SharingAddToFolder);
        	
        }
        log.info("INFO: Select available folder from community and click on Add her button");
        ui.getFirstVisibleElement(getFolderName(baseFolder.getName())).click();
        ui.clickLinkWithJavascript(FilesUIConstants.submitButton);
        
        log.info("INFO: Get the count of number of folder after you share file");
        int afterCount = getSharedFolderCount(FilesUIConstants.ShareFolderTitle);
        
        log.info("INFO: Verify number of folder increases after you share file with folder ");
        Assert.assertEquals(beforeCount + 1, afterCount);
    }
    
    /**
     * getFileThumbnail(): This method will return a locator of file's Thumbnail
     * @param BaseFile - file
     */
	public String getFileThumbnail(BaseFile file) {
		return "css=div[dndelementtitle='" + file.getName() + "']";
	}

	/**
     * createFileFromUploadDropdown(): This method will create a file from template file by selecting -
     * 'Create File' option from upload dropdown 
     * @param BaseFile - file
     */
	public void createFileFromUploadDropdown(BaseFile file) {
		log.info("INFO: Click on Upload and select 'Create File' ");
		ui.getFirstVisibleElement(FilesUIConstants.moreEditOption).click();
		ui.clickLinkWait(FilesUIConstants.createFileLink);
		
		log.info("INFO: Enter file name and click on create button");
		ui.getFirstVisibleElement(FilesUIConstants.fileNameField).clear();
		ui.getFirstVisibleElement(FilesUIConstants.fileNameField).type(file.getRename());
		ui.clickLinkWait(FilesUIConstants.fileCreateBtn);
	}
	
	/**
     * shareFileWithCommunityOrPeople(): This Method will be sharing a file with community or person
     * @param String - community name or user name
     * @param String - role
     * @param String - title
     * @param String - community or user
     */
	public void shareFileWithCommunityOrPeople(String searchText, String role, String title,String searchFor) {

		log.info("INFO: Get the count of "+role+" before you share file with "+searchFor);
		int beforeCount = getSharedFolderCount(title);
		
		shareFileWithCommOrPerson(searchText,role,searchFor);
	
		log.info("INFO: Get the count of "+role+" after you share file");
		int afterCount = getSharedFolderCount(title);

		log.info("INFO: Verify number of "+role+" increases after you share file with "+searchFor);
		if (role.equalsIgnoreCase("editor")) {
			Assert.assertEquals(beforeCount + 1, afterCount);
			getFirstVisibleElement("css=span[title='" + searchText + "'] a").hover();
			clickLinkWithJavascript(FilesUIConstants.removeEditorButton);
			clickLinkWithJavascript(FilesUIConstants.okButtonRemoveAction);
		} else {
			if (searchFor.equalsIgnoreCase("community")) {
				Assert.assertEquals(beforeCount + 2, afterCount);
			} else {
				Assert.assertEquals(beforeCount + 1, afterCount);
			}
			getFirstVisibleElement("css=span[title='" + searchText + "']").hover();
			clickLinkWithJavascript(FilesUIConstants.removeReaderButton);
			clickLinkWithJavascript(FilesUIConstants.okButtonRemoveAction);
		}
		
		if (searchFor.equalsIgnoreCase("community")) {
			getFirstVisibleElement(FilesUIConstants.shareEveryonetitle).hover();
			clickLinkWithJavascript(FilesUIConstants.removeShareEveryoneButton);
			clickLinkWithJavascript(FilesUIConstants.okButtonRemoveAction);
		}

	}

	/**
     * shareFileWithCommOrPerson(): This Method will be sharing a file with community or person
     * @param String - community name or user name
     * @param String - role
     * @param String - community or user
     */
	public void shareFileWithCommOrPerson(String searchText, String role, String searchFor) {
		clickLinkWait(FilesUIConstants.addPeopleOrCommunities);
		selectFromDropdownWithValue(FilesUIConstants.memberTypeSelector, searchFor);
		selectFromDropdownWithValue(FilesUIConstants.roleTypeSelector, role);
		getFirstVisibleElement(FilesUIConstants.commSearchTextBox).click();
		typeTextWithDelay(FilesUIConstants.commSearchTextBox, searchText);
		clickLinkWait(FilesUIConstants.searchButton);
		clickLinkWithJavascript(FilesUIConstants.firstCommFromSearch);
		clickLinkWait(FilesUIConstants.shareButtonForComm);
		driver.navigate().refresh();
		ui.clickLinkWithJavascript(FilesUIConstants.sharingTabInFiDO);

	}
	
	/**
     * getFileNameFromUploadMessage(): This method will return a locator of file name link displayed in file upload message
     * @param BaseFile - file
     */
	public String getFileNameFromUploadMessage(BaseFile file) {
		
		return "link="+file.getName();
	}
	/**
     * folderAddFiles(): This Method will be Add the File to a Folder via 'Add Files' Dialog.
     * @param String - folderName
     * @param String - fileName
     */
	public void folderAddFiles(String folderName , String fileName)
	{
		log.info("INFO: Click the Folder Tab in the Left Navigation Panel");
		  ui.fluentWaitElementVisible(FilesUIConstants.FoldersLeftMenu);
		  ui.getFirstVisibleElement(FilesUIConstants.FoldersLeftMenu).click();
		  if(!cfg.getUseNewUI())
		  {
			  log.info("INFO: Open the Folder - " + folderName);
			  ui.fluentWaitPresent(FilesUIConstants.FolderLeftList.replace("PLACEHOLDER", folderName));
			  ui.getFirstVisibleElement(FilesUIConstants.FolderLeftList.replace("PLACEHOLDER",folderName)).doubleClick();
			  log.info("INFO: Click on Dropdown for Folder - "+folderName);
			  ui.clickLinkWait(this.getFolderActionMenu(folderName));
			  log.info("INFO: Click on 'Add File...' option for Folder - "+folderName);
			  ui.clickLinkWait(FilesUIConstants.FolderBreadcrumbAddFilesOption);
		  }
		  else
		  {
			  if(ui.getFirstVisibleElement(FilesUIConstants.MoreButton).isDisplayed())
				  ui.getFirstVisibleElement(FilesUIConstants.MoreButton).click();
		  }
		      
		  ui.clickLink(getFileCheckbox(fileName));
		  ui.clickLinkWait(FilesUIConstants.shareFolderDialogShareButton);
	}
	
	/**
	 *This method edits the file name in Edit Properties
	 * @param file 
	 */

	public void editFileProperties(BaseFile file) {
	
		log.info("INFO: Click on the More Actions link");
		clickLinkWait(getFileMoreActionLink(file.getName()));

		log.info("INFO: Click on Edit Properties");
		clickLinkWait(FilesUIConstants.EditPropertiesOption);

		log.info("INFO: Edit the file name");
		fluentWaitTextPresent(Data.getData().editPropertiesDialogBoxTitle);
		clearText(FilesUIConstants.editPropertiesName);
		typeText(FilesUIConstants.editPropertiesName, Data.getData().editedFileName);
		clickButton(Data.getData().buttonSave);

		log.info("INFO: Verify the updated file name appears");
		Assert.assertTrue(driver.isTextPresent(Data.getData().editedFileName),
				"The updated file name is present");
	}
	
	
	/**
	 *This method takes file name as input and return locator for moreaction link
	 * @param String filename 
	 */
	 public String getFileMoreActionLink(String filename) {
		return "css=tr[dndelementtitle='"+filename+"'] ~ tr a[title='More Actions']";
	 }

	/**
     * getLikeCount(): This Method return a link of Like count.
     * @param BaseFile - file
     */
	public static String getLikeCount(BaseFile file) {
		return "css=tr[dndelementtitle='" + file.getName() + "'] a.lotusLikeCount";
	}
}
