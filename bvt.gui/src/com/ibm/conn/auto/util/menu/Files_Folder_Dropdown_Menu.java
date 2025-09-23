package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ICBaseUI;

public enum Files_Folder_Dropdown_Menu implements Menu {

	NEW_FOLDER("New Folder", FilesUIConstants.FolderBreadcrumbNewFolderOption),
	ADD_FILES("Add Files", FilesUIConstants.FolderBreadcrumbAddFilesOption),
	MOVE_TO("Move to", FilesUIConstants.FolderBreadcrumbMovetoOption),
	ADD_TO_MY_DRIVE("Add to My Drive", FilesUIConstants.FolderBreadcrumbAddToMyDriveOption),
	SHARE("Share", FilesUIConstants.FolderBreadcrumbShareOption),
	STOP_FOLLOWING("Stop Following", FilesUIConstants.FolderBreadcrumbStopingFollowingOption),
	FOLLOW("Follow", FilesUIConstants.FolderBreadcrumbFollowOption),
	EDIT_PROPERTIES("Edit Properties", FilesUIConstants.FolderBreadcrumbEditPropertiesOption),
	DELETE("Delete", FilesUIConstants.FolderBreadcrumbDeleteOption);
	
	final private String action;
	final private String menuID;
	
	private static Logger log = LoggerFactory.getLogger(Files_Folder_Dropdown_Menu.class);
	
	
	Files_Folder_Dropdown_Menu(String action, String menuID){	
		this.action = action;
		this.menuID = menuID;
	}
	
	public String getMenuItemText(){
		return this.action;
	}
	
	public String getMenuItemLink(){
		return this.menuID;
	}
	
	public void select(ICBaseUI ui) {
		
		log.info("INFO: Click folder's title dropdown menu");
		ui.clickLinkWait(FilesUIConstants.FolderTitleDropdownMenu);
		
		log.info("INFO: Click on menu item: "+this.action);
		ui.clickLinkWait(this.getMenuItemLink());
	}
}
