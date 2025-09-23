package com.ibm.conn.auto.webui;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.webeditors.fvt.utils.DriverUtils;
import com.ibm.conn.auto.util.webeditors.fvt.utils.WindowContextHandler;
import com.ibm.conn.auto.webui.cloud.OfficeOnlineUICloud;
import com.ibm.conn.auto.webui.onprem.OfficeOnlineUIOnPrem;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public abstract class OfficeOnlineUI extends ICBaseUI{

	public OfficeOnlineUI(RCLocationExecutor driver) {
		super(driver);
		driverUtl = new DriverUtils(driver);
	}
	
	public static OfficeOnlineUI getGui(String product, RCLocationExecutor driver){
		
        String prd = product.toLowerCase();
  
        if(prd.equals("multi") || prd.equals("production") || prd.equals("vmodel") ){
            throw new NotImplementedException("'" + product + "' product is not yet supported.");
        } else if(prd.equals("onprem")) {
            return new OfficeOnlineUIOnPrem(driver);
        } else if(prd.equals("cloud")){
            return new OfficeOnlineUICloud(driver);
        } else {
            throw new NotImplementedException("'" + product + "' product is unknown.");
        }
    }
	
	protected static Logger log = LoggerFactory.getLogger(OfficeOnlineUI.class);
	
	public enum FileSet { BASIC, BVT, FULL, NOEDIT, NONE, ONEFILE }
	
	//FileEntry types to match the file creation standards in waffle
	private static final List<String> bvtTestFileSet, fullTestFileSet, smallTestFileSet, uneditableTestFileSet, oneFileTestFileSet;
	public final List<FileEntry> testFiles = new ArrayList<FileEntry>();
	
	//Integer to count how many times the OAuth screen is called
	private int oAuthCounter;
	
	private DriverUtils driverUtl;
	
	//String used to perform checkings when it comes to the file ID
	private String currentFileIDDownloadLink;
	private String currentFileIDViewerURL;
	
	public static final String testFileTitle = "WebEditorsTestFile";
	
	public abstract String getExpectedPageTitle();
	
    // Testing files - Strings to be inserted in Data
	static {
		final String fileODP, fileDOCX, filePDF, fileODS, fileTXT;
		
		fileDOCX = testFileTitle + ".docx";
		{
			List<String> basicTestFilesAux = new ArrayList<String>();
			basicTestFilesAux.add(fileDOCX);
			basicTestFilesAux.add(testFileTitle + ".pptx");
			basicTestFilesAux.add(testFileTitle + "Sheet.xlsx");
			bvtTestFileSet = Collections.unmodifiableList(basicTestFilesAux);
		}
		
		fileODP = testFileTitle + ".odp";
		fileODS = testFileTitle + ".ods";
		filePDF = testFileTitle + ".pdf";
		fileTXT = testFileTitle + ".txt";
		{
			List<String> aditionalTestFilesAux = new ArrayList<String>( bvtTestFileSet );
			aditionalTestFilesAux.addAll( Arrays.asList(new String[] {fileODP, fileODS, filePDF}) );
			aditionalTestFilesAux.add(testFileTitle + ".potx");
			aditionalTestFilesAux.add(testFileTitle + ".pps");
			aditionalTestFilesAux.add(testFileTitle + ".ppt");
			aditionalTestFilesAux.add(fileTXT);
			aditionalTestFilesAux.add(testFileTitle + ".xls");
			aditionalTestFilesAux.add(testFileTitle + ".xlsm");
			aditionalTestFilesAux.add(testFileTitle + "Fails.pot");
			aditionalTestFilesAux.add(testFileTitle + "Large.ppsx");
			aditionalTestFilesAux.add(testFileTitle + "Objects.xlsx");
			aditionalTestFilesAux.add(testFileTitle + "ObjectsEditable.xlsx");
			aditionalTestFilesAux.add(testFileTitle + "Picture.jpg");
			aditionalTestFilesAux.add(testFileTitle + "Picture.png");
			fullTestFileSet = Collections.unmodifiableList(aditionalTestFilesAux);
		}
		oneFileTestFileSet = Collections.unmodifiableList( Arrays.asList(new String[] {fileDOCX}));
		
		smallTestFileSet = Collections.unmodifiableList( Arrays.asList(new String[] {fileODP, fileDOCX, filePDF, fileODS}) );
		
		uneditableTestFileSet = Collections.unmodifiableList( Arrays.asList(new String[] {fileTXT, fileDOCX}) );
		
	}

	private static final String[] 
	    //Types of files which should be edited on Office Online;
	    FileTypesForEditionO365 = {"ods", "xlsb", "xlsm", "xlsx", "odp", "ppsx", "pptx", "docm", "docx", "odt" };
	
	public static final String 
		//Text constants
		EditInOfficeOnlineButtonLabel = "Edit in Microsoft Office Online� ",
		EditInPowerPointOnlineButtonLabel = "Edit in Microsoft PowerPoint Online",
		EditInWordOnlineButtonLabel = "Edit in Microsoft Word Online",
		EditInExcelOnlineButtonLabel = "Edit in Microsoft Excel Online",
		EditInPowerPointOnlineButtonTitle = "Use Microsoft PowerPoint Online to edit this file",
	    EditInExcelOnlineButtonTitle = "Use Microsoft Excel Online to edit this file",
		EditInWordOnlineButtonTitle= "Use Microsoft Word Online to edit this file",
		ConnectionsToOfficeOnlineBridgeText = "Connections to Office 365 bridge",
		OAuthPageTitle = "Authorize access to IBM Connections",
		AccessGrantedText = "Access Granted",
		ExtensionNotSupportedMessage = "The extension is not supported for this action (or is not supported at all in the case of editing a word document), if you are attempting to edit try to view. Sometimes view is possible and edit is not, will automate opening into view if edit is impossible",
		NoFilesOnFilesScreenText = "You have not uploaded any files.",
		certificateErrorInIEPageTitle = "Certificate Error: Navigation Blocked",
	
		//Components
		TrashComponent = "files/app#/trash";
	
	public static final String 
		//Elements from Office Online UI
		EditInO365Button = "css=li.ics-viewer-action.ics-viewer-action-edit.ics-viewer-splitbutton a.actionLink span.linkText",
		dropDownButtonNextToUpload = "css=span.dijitDropDownButton > span[title]";
	
	private static final String 
		DownloadButton = "css=a[class='actionLink'][title='Download file']",
		GrantAccessButton = "css=input[id='authBtn'][value='Grant Access']",
		overrideLinkIE = "javascript:document.getElementById('overridelink').click()", 
		FormTagInsidePageBody = "css=html body form#canvas_form",
		
		TAB_ICON_URL_LNK = "css=head > link[rel='shortcut icon']",
		JWT_TOKEN_TXTBX = "css=form#canvas_form > input[name='access_token']",
		EDIT_IN_OFFICEONLINE_TD = "css=tbody.dijitReset>tr>td.dijitMenuItemLabel:contains('Edit')"
		;


	/**
	 * Given a filename, returns the CSS selector of that specific file in a Detail View.
	 * @param fileName - String type, name of the file to be selected on the screen
	 * @return string related to the selector, using the file name as parameter
	 */
	public String getFileNameDetailsView (String fileName){
		return "css=a[title='"+fileName+"']";
	}
	
	public void printID(FileEntry fileEntry){
		log.info("FileEntryID: "+ fileEntry.getId().toString());
	}
	
	/**
	 * OAuth Variable Methods
	*/
	public void restartOAuthCounter() {
		this.oAuthCounter = 0;
	}
	
	public void updateOAuthCounter() {
		this.oAuthCounter++;
	}
	
	public int getOAuthCounter() {
		return this.oAuthCounter;
	}
	
	
	/**
	 * File ID methods
	*/
	private void updateCurrentFileID(String fileIDViewer, String fileIDDownload) {
		this.currentFileIDViewerURL = fileIDViewer;
		this.currentFileIDDownloadLink = fileIDDownload;
	}
	
	public String getCurrentFileIDFromViewerURL(){
		return currentFileIDViewerURL;
	}
	
	public String getCurrentFileIDFromDownloadLink(){
		return currentFileIDDownloadLink;
	}
	
	
	/** updateFileIDVariables
	 * 
	 * Method which updates the Id variables (one related to the URL, and another one which should get the ID displayed on the download link)
	 * by cropping the File Viewer URL into pieces and fetching the ID of the opened file. The same is done to the 'href' link inside the
	 * 'Download File' button. Then, method updateCurrentFileID(String,String) is called to update the IDs in order to make further file ID asserts.    
	 * @throws ArrayIndexOutOfBoundsException 
	 */			
	public void updateFileIDVariables() throws ArrayIndexOutOfBoundsException {
		//Getting the ID of the file from the URL
		String url = driver.getCurrentUrl();
		String[] stringListFromURL = url.split("file/");
	
		//Getting the ID of the file from the download button
		String urlFromHref = driver.getSingleElement(DownloadButton).getAttribute("href");
		//Split the URL containing in href attribute twice
		String[] stringListFromHref = urlFromHref.split("document/");
		stringListFromHref = stringListFromHref[1].split("/media");
		//	
		updateCurrentFileID(stringListFromHref[0].toString(),stringListFromURL[1].toString());
	}

	
	/** returnsIDFromOfficeWebAppFile
	 * 
	 * Method which breaks the URL from Office Online tab into pieces in order to retrieve the
	 * ID of the file opened by the user inside Connections. This ID will be used in an assertion, to check if the 
	 * same file ID is on the File Viewer, the Office Online URL and the File itself (in the download link)
	 * @params url - String type, the content of the Office Web App URL 
	 * @returns stringListFromURL[0] - String type, which is the fileID
	 */
	public String returnsIDFromOfficeWebAppFile(String url){
		
		//striping the url twice in order to achieve the pure file ID
		String[] stringListFromURL = url.split("&file_id=");
		stringListFromURL = stringListFromURL[1].split("&");
		
		//Returns just the fileId from the URL
		return stringListFromURL[0];
	}

	
	/**createMultipleTestFiles
	 * 
	 * Create multiple files to set the environment
	 * It sets FileEntry types, so they can be used on some methods already created by other test teams, passing the fileName as the second parameter to call createAFile method
	 * @param apiHandler - APIFileHandler type, which is the instance that will handle the Files API methods
	 */
	public void createMultipleTestFiles(APIFileHandler apiHandler, FileSet fileSet) {
	
		final List<String> targetList;

		switch(fileSet) {
			case BVT:
				targetList = bvtTestFileSet;
				break;
			case FULL:
				targetList = fullTestFileSet;
				break;
			case BASIC:
				targetList = smallTestFileSet;
				break;
			case NOEDIT:
				targetList = uneditableTestFileSet;
				break;
			case ONEFILE:
              targetList = oneFileTestFileSet;
              break;	
			case NONE:
				targetList = new ArrayList<String>();
				break;
			default:
				throw new RuntimeException("Unknown FileSet: '" + fileSet.name() + "'.");
		}
		
		for (String filename : targetList) {
			FileEntry fileEntry = createTestFile(apiHandler, filename);
			assertFileEntryIsConsistent(fileEntry);
			testFiles.add( fileEntry );
		}
	}

	private static final String ID_LOCATOR = "com:td:";
	
	private static void assertFileEntryIsConsistent(FileEntry fileEntry) {
		Assert.assertNotNull(fileEntry, "FileEntry parameter cannot be null.");
		Assert.assertNotNull(fileEntry.getId(), "Id property in FileEntry parameter cannot be null.");
		Assert.assertTrue(fileEntry.getId().toString().contains(ID_LOCATOR), "Id property in FileEntry parameter must contain '" + ID_LOCATOR + "'.");
	}
		
	/**createTestFile
	 * 
	 * This method creates a file using the name given as the parameter, calling the Files API to handle the process
	 * It uses the BaseFile class to build the file, as this is the way the API works, returning a File Entry type
	 * @param apiHandler - APIFileHandler type, which is the instance that will handle the Files API methods
	 * @param fileName - String type, the name of the file to-be created
	 * @return fileEntry - FileEntry type, which is used on FilesUI to work with the files elements on Connections
	 */
	private FileEntry createTestFile(APIFileHandler apiHandler, String fileName){
		
		File file = new File(FilesUI.getFileUploadPath(fileName, cfg));
		
		BaseFile baseFile = FileBaseBuilder.buildBaseFile( fileName, "." + FilenameUtils.getExtension(fileName), ShareLevel.NO_ONE );
		
		log.info("INFO: executing the create file API call for '" + fileName + "'...");
		FileEntry fileEntry = baseFile.createAPI(apiHandler, file);
		int statusCode = apiHandler.getService().getRespStatus();
		if( !FTPReply.isPositiveCompletion(statusCode) ) {
			final String errMsg = "An error has occurred while posting '" + fileName + "'. HTTP response status code is '" + statusCode + "'. Please review the server logs.";
			log.error(errMsg);
			throw new SkipException(errMsg);
		}
		log.info("INFO: create file API call was successful");
		
		return fileEntry;
	}
	
	
	/**isFileEditable
	 * 
	 * Check if the file is editable in Office Online according to the EditableTypes 
	 * It verifies according to the extension type, returning true if the file is editable on Office Online
	 * It uses FileTypesForEditionO365 array, which contains the set of editable file types, to perform the checking
	 * @param fileName - String type, name of the file to-be checked
	 * @return foundInList - boolean type, which is set as true if the file is editable
	 */
	public boolean isFileEditable(String fileName) {
		String fileExtension = FilenameUtils.getExtension( fileName ).toLowerCase();
		boolean foundInList = false;
		//Runs through all the elements on FileTypesForEditionO365 array, which are the supported types 
		for (int i=0; i < FileTypesForEditionO365.length; i++){
			if (fileExtension.equalsIgnoreCase(FileTypesForEditionO365[i])){
				log.info("MATCH: File "+fileName+" is editable in Office Online : $"+fileExtension+"$ = $"+FileTypesForEditionO365[i]+"$ ");
				foundInList = true;
				break;
			}	
		}
		return foundInList;
	}


	/**getEditButtonContent
	 * 
	 * Method which accesses the drop down menu inside the File Viewer screen, and fetches the content of the button
	 * @return String type, the content of the Edit in Office Online button
	 * @throws Exception
	 */			
	public String getEditButtonContent() throws Exception{
		
		clickLinkWait(dropDownButtonNextToUpload);
		Element O365EditButton = driver.getSingleElement(EditInO365Button);
		String O365ebAriaLabel = O365EditButton.getAttribute("aria-label");
		Assert.assertNotNull(O365ebAriaLabel, "Aria-label attribute was not defined in the 'Edit in O365 Button' ("+ dropDownButtonNextToUpload +")");
		return O365ebAriaLabel;
	}

	
	/** doesButtonExists
	 * 
	 * Method which verifies if the edit button element exists and is visible inside the drop down on the File View
	 * In this method, no verification is done on the content of the button or to check if it is working, but only its existence and presence on the screen 
	 * @returns buttonExists - boolean type, will be true if the button is present
	 */			
	public boolean doesButtonExist() {
		boolean buttonExists = false;
		if (isElementPresent(dropDownButtonNextToUpload)){
			clickLinkWait(dropDownButtonNextToUpload);
			if (isElementPresent(EditInO365Button)){
				buttonExists = true;
			}
		} 
		return buttonExists; 
	} 

	
	/**switchToChildTab
	 * 
	 * Method which changes the handle to the recently opened tab, returning the handle of the parent tab
	 * @return String type, the parent page handle
	 */	
	public String switchToChildTab(){
		String parentWindowHandle = driver.getWindowHandle();
		
		//Gets the handles of the opened screens
		for(String handle : driver.getWindowHandles()) {
		    //Perform some actions if the screen is not the parent one, so the following actions can take place in the new opened tab
			if (!handle.equals(parentWindowHandle)) {
				//Gets the new tab, which is the OAuth screen or the Office Web Apps
				driver.switchToWindowByHandle(handle);
			}
		}
		return parentWindowHandle;
	}
	
	
	/**switchBackToParentTab
	 * 
	 * Method which changes the handle to the parent tab using the handle passed as parameter. It also closes the child (and current) tab
	 * @param windowHandle - String type, the handle of the parent window
	 */	
	public void switchBackToParentTab(String windowHandle){
		log.info("INFO: closing the Office Online tab");
		this.close(cfg);
    
        log.info("INFO: switch back to the parent tab, Connections");
        driver.switchToWindowByHandle(windowHandle);
	}
	
	
	/**oAuthHandler
	 * 
	 * Method which performs the actions on OAuth page, handling elements of the screen to click the button and increase the OAuth variable 
	 */	
	public void oAuthHandler(){
		
		log.info("INFO: attempting to ignore certificate problems for IE browser...");
		ignoreCertificateInIE();
		
		log.info("INFO: check if the screen contains the OAuth UI");
        if (driver.getTitle().equalsIgnoreCase(OAuthPageTitle)){	
			
        	log.info("Going to click + "+driver.getTitle());
			fluentWaitPresent(GrantAccessButton);
        	clickLinkWait(GrantAccessButton);
			//Counter of oAuth variable is increased, showing how many times this screen was shown
        	updateOAuthCounter();
			log.info("OAuth was UPDATED = "+oAuthCounter+" ");
        }
        log.info("INFO: Calls method which ignores certificate problems for IE browser once again");
        ignoreCertificateInIE();
	}
	
	
	/** ignoreCertificateInIE
	 * 
	 * This method runs code to ignore the IE Certificate problems. It performs this step as many times as the certificate security issue page is shown,
	 * bypassing this screen for multiple certificate problems.  
	 * */
	public void ignoreCertificateInIE(){
		do{
			if (cfg.getTestConfig().browserIs(BrowserType.IE) && driver.getTitle().equalsIgnoreCase(certificateErrorInIEPageTitle)) {
				//click on the override link 
				driver.navigate().to(overrideLinkIE);
				//fluentWaitTextNotPresent("There is a problem with this website�s security certificate");
				log.info("INFO: IGNORING IE CERTIFICATE");
			} 
		}while (driver.getTitle().equalsIgnoreCase(certificateErrorInIEPageTitle));	
	}
		
	
	/** dismissFilesFromTrash
     * 
     * This method dismisses the files on Connections trash after all testing. This step is taken once, and uses elements existing in the UI's.
     * after all the tests are performed, and as this tests uses multiple files, cleaning the trash can will release space after every run of the test suite.
     * */
    public void dismissFilesFromTrash(){
        
        waitForPageLoaded(driver);
        
        //Empty Trash
        log.info("INFO: Empty trash");
        driverUtl.click(FilesUIConstants.EmptyTrash);
        
        //Confirm to Empty the Trash
        log.info("INFO: Confirm empty the trash");
        driverUtl.click(BaseUIConstants.OKButton);
        log.info("INFO: Trash is empty, test will be dismissed");
    }
	

	/**deleteAllTestFiles
	 * 
	 * Method which handles the deletion of the test files, printing a message all the files were dismissed
	 * It uses the method areFilesDismissed(APIFileHandler), which aggregates the file deletion for all test files
	 * @param apiHandler - APIFileHandler type, which is the instance that will handle the Files API methods
	 */
	public void deleteAllTestFiles(APIFileHandler apiHandler) {
		Assert.assertNotNull(apiHandler, "ERROR: APIFileHandler instance cannot be null!");

		//Calls a method which performs the deletion of the files, and returns true if it worked
		if (areFilesDismissed(apiHandler))
			log.info("INFO: All Files Deleted");
	}

	
	/**areFilesDismissed
	 * 
	 * Method which, using the deleteFile(fileName) method from the Files API, deletes and return true if all the files are dismissed
	 * The above mentioned method has to be called for each of the test file, therefore it should return true if and only if all the files are deleted  
	 * @param apiHandler - APIFileHandler type, which is the instance that will handle the Files API methods, in this case, the deletion of the files
	 * @return boolean true if all files are dismissed 
	 */
	private boolean areFilesDismissed(APIFileHandler apiHandler){
	  
	  boolean result = true;
	  for(FileEntry fileEntry : testFiles) {
		  Assert.assertNotNull(fileEntry.getEditLink(), "File entry's Edit Link is null!");
		  result = result && apiHandler.deleteFile(fileEntry);
	  }
	  return result;
	}

  /**clickEditInOfficeButton
   * 
   * Method which accesses the drop down menu inside the File Viewer screen, and click the correct button
   * depending on the type of file to be opened
   */ 
   public void clickEditInOfficeButton(String fileName){
	 
     driverUtl.click(getFileNameDetailsView(fileName));
            
     driverUtl.click(dropDownButtonNextToUpload);
     
     driverUtl.click(EDIT_IN_OFFICEONLINE_TD);
   }

  /**getWOPISrc
   * 
   * Method which accesses the <body/>, then the <form/> to get the the WOPISrc inside one of its attributes
   * Then it returns just the WOPI source parameter by splitting a string  
   * @return wopiSrc - the source of the WOPI server
   */
  public String getWOPISrc() {
    String wopiSrc[] = driver.getSingleElement(FormTagInsidePageBody).getAttribute("action").split("WOPISrc=");
    return wopiSrc[1];
  }
  
  /**getFileIDFromWopiUrl
   * 
   * Method which gets the file ID from the url and returns it after splitting the string 
   * @return file ID from Wopi URL
   */
  public String getFileIDFromWopiUrl() {
    String[] stringListFromURL = driver.getCurrentUrl().split("file_id=");
    return stringListFromURL[1];
  }
  
  /**getFileIDFromWopiSrc
   * 
   * Method which gets the file ID from the WopiSrc and returns it after splitting the string 
   * @return file ID from Wopi Src
   */
  public String getFileIDFromWopiSrc(String wopiSRC) {
    String[] stringListFromWopiSRC = wopiSRC.split("files/");
    return stringListFromWopiSRC[1];
  }
  
  /**getFileIDFromFileEntry
   * 
   * Method which gets the file ID from the FileEntry to get only the necessary part
   * @return file ID from FileEntry
   */
  public String getFileIDFromFileEntry(FileEntry fileEntry) {
	assertFileEntryIsConsistent(fileEntry);

    String[] stringListFileID = fileEntry.getId().toString().split(ID_LOCATOR);
    return stringListFileID[1];
  }
  
  /**getServerNameFromURL
   * 
   * Method which gets the server name form a given URL and returns it
   * @return server name
   */
  public String getServerNameFromURL(String url){
    String[] elementArray = url.split("/");
    return elementArray[3];
  }

  /**
   * Retrieves the icon in Office Online Server for the file indicated in fileElement.
 * @param fileElement An element for an html <a/> element. The element's 'title' attribute must contain a filename.
 * @return a Pair object with two strings. The 1st string contains the filename found in fileElement's 'title' attribute;
 * 			the 2nd string contains the icon's URL for the file indicated in fileElement, as indicated by OfficeOnline.
 * @throws URISyntaxException 
 */
  public URI getFileIconUrl(String filename) throws URISyntaxException {
	  
	  WindowContextHandler windowJump = editFileInOfficeOnline(filename);
	  
	  log.info("INFO: getting tab icon and reading it's href attribute...");
	  Element icon = driver.getSingleElement(TAB_ICON_URL_LNK);
	  URI iconUri = new URI( icon.getAttribute("href") );
	  
	  log.info("INFO: closing Office Online window");
	  this.close(cfg);
	  
	  log.info("INFO: resyncing Selenium with browser's previous status");
	  windowJump.resyncSeleniumWithOriginalWindow();
	  
	  return iconUri;
  }
  
	public String getJwtToken(String filename) {
	  WindowContextHandler windowJump = editFileInOfficeOnline(filename);
      
	  log.info("INFO: reading the serialized jwt token...");
      String jwtToken = driver.getSingleElement(JWT_TOKEN_TXTBX).getAttribute("value");
	  
	  log.info("INFO: closing Office Online window");
	  this.close(cfg);
      
	  log.info("INFO: resyncing Selenium with browser's previous status");
      windowJump.resyncSeleniumWithOriginalWindow();
      
	  return jwtToken;
	}
	
	private WindowContextHandler editFileInOfficeOnline(String filename) {
	  log.info("INFO: clicking file '" + filename + "'");
	  driverUtl.click( String.format(FilesUIConstants.FILELIST_GRIDMODE_FILENAME_LNK, filename) );
	  
	  log.info("INFO: waiting for page to load");
	  waitForPageLoaded(driver);
	  
	  String pageTitle = driver.getTitle();
	  log.info("INFO: for file '" + filename + "', the page title is '" + pageTitle + "'");
	  if(!pageTitle.startsWith(filename)) {
		  throw new RuntimeException("The pagetitle '" + pageTitle + "' does not begin with '" + filename + "'");
	  }
	  Assert.assertTrue(pageTitle.startsWith(filename), "The pagetitle '" + pageTitle + "' does not begin with '" + filename + "'");
	  
	  log.info("INFO: saving current windows context");
	  WindowContextHandler windowJump = new WindowContextHandler(driverUtl);
	  
	  log.info("INFO: editing file in Office Online");
	  driverUtl.click(OfficeOnlineUI.EditInO365Button);
	  
	  log.info("INFO: resyncing Selenium with browser's current status");
	  windowJump.resyncSeleniumWithNewWindow();
	  return windowJump;
	}
}
