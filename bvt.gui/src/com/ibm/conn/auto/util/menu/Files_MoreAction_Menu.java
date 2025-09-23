package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ICBaseUI;

public enum Files_MoreAction_Menu implements Menu {

	STOP_FOLLOWING("Stop Following", FilesUIConstants.StopFollowingOption),
	FOLLOWING("Following", FilesUIConstants.FollowingOption),
	ADDTOFOLDER("Add to Folder", FilesUIConstants.AddToFolderOption),
	EDIT_PROPERTIES("Edit Properties", FilesUIConstants.EditPropertiesOption),
	ADD_COMMENT("Add Comment", FilesUIConstants.AddCommentOption),
	LOCK_FILE("Lock File", FilesUIConstants.LockFileOption),
	COPY_COMMUNITY("Give Copy to Community", FilesUIConstants.CopyToCommunity),
	MOVE_TO_TRASH("Move to Trash", FilesUIConstants.MoveToTrashOption);
	
	String action = null;
	String menuID = null;
	
	private static Logger log = LoggerFactory.getLogger(Files_MoreAction_Menu.class);
	
	
	Files_MoreAction_Menu(String action, String menuID){	
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
		
		log.info("INFO: Select more actions menu");
		ui.clickLinkWait(FilesUIConstants.moreActions);
		
		log.info("INFO: Make menu selection ");
		ui.clickLinkWait(this.getMenuItemLink());
	}
	
	
}
