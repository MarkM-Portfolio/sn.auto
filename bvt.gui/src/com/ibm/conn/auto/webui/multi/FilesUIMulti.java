package com.ibm.conn.auto.webui.multi;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;


public class FilesUIMulti extends FilesUI {
	public static String UploadFiles_Rename = "css=a[class='lconnFilenameContainer bidiSTT_URL']"; 
	public static String New_Button = "css=button[id='lconn_files_action_createitem_0']";
	public static String New_Folder_Option = "css=td[id='lconn_files_action_createcollection_0_text']";
	
	public FilesUIMulti(RCLocationExecutor driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param file name
	 * */
	public void searchFile(String name) {
		log.info("INFO: searchFile by file name is Cloud only variable skipping for Multi-Tenant");
	}
	
	/**
	 * 
	 * @param fileDate
	 * */
	public long getFileCreatedTime(String fileDate) {
	
		log.info("INFO: getFileCreatedTime is Cloud only variable skipping for Multi-Tenant");
		return 0;
	}
	
	public void addToComFolder(BaseFile file, BaseFolder folder) {
		
		//Switch the display from default Tile to Details
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(this);
		
		log.info("INFO: Select the file by name");
		clickLinkWait(selectFile(file));
				
		log.info("INFO: Select add file to folder");
		clickLinkWait(FilesUIConstants.addFileToFolder);
				
		//Click on the folder to add the file too
		log.info("INFO: Select the folder to add the file too");
		clickLink(selectFolder(folder));
		
		//Click on Add to Folders button
		log.info("INFO: Select the Add to Folder Button");
		clickLinkWait(FilesUIConstants.addToFolder);
	}
	
	
	/** 
	 * CreateFolder - create folder with folder name, description and access level
	 * @param FolderName
	 * @param FolderDescription
	 * @param ShareWithOption
	 * @throws Exception
	 */
	public void create(BaseFolder folder){

		//Click on the 'New Folder' button / 'New' drop-down button
		try{			
			clickLinkWait(FilesUIConstants.NewFolder_Button);
		} catch (Exception e){
			clickLinkWait(New_Button);
			clickLinkWait(New_Folder_Option);
		}
		
		fluentWaitPresent(FilesUIConstants.CreateFolderName);

		//Fill in the form
		typeText(FilesUIConstants.CreateFolderName, folder.getName());
		typeText(FilesUIConstants.CreateFolderDescription, folder.getDescription());
		
		//Save the form
		clickSaveButton();
		waitForPageLoaded(driver);
		fluentWaitTextPresent("Successfully created "+folder.getName()+".");
	}
	
}
