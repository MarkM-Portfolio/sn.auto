package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.WikisUI;

public enum Wiki_Page_Menu implements Menu{

	CREATECHILD("CreateChild", "xpath=//td[contains(text(), 'Create Child')]"),
	CREATEPEER("CreatePeer", "xpath=//td[contains(text(), 'Create Peer')]"),
	PRINTPAGE("PrintPage", "xpath=//td[contains(text(), 'Print Page')]"),
	MOVEPAGE("MovePage", "xpath=//td[contains(text(), 'Move Page')]"),
	DOWNLOADPAGE("DownloadPage", "xpath=//td[contains(text(), 'Download Page')]"),
	MOVETOTRASH("MoveToTrash", "xpath=//td[contains(text(), 'Move to Trash')]");

	private static Logger log = LoggerFactory.getLogger(Wiki_Page_Menu.class);
	String action = null;
	String actionlink = null;
	
	Wiki_Page_Menu(String action, String actionlink){	
		this.action = action;
		this.actionlink = actionlink;
	}
	
	public String getMenuItemText(){
		return this.action;
	}
	
	public String getMenuItemLink(){
		return this.actionlink;
	}
	
	/**
	 * 	Selects specified menu option from the Page Actions menu
	 *  @param WikisUI - ui
	 */
	public void select(WikisUI ui) {
		//Open Page Actions menu
		log.info("INFO: Open Page Actions menu");
		open(ui);
		
		//Select option specified from the menu
		log.info("INFO: Select " + this.getMenuItemText()+" from menu ");
		ui.clickLinkWait(this.getMenuItemLink());
	}
	
	/**
	 * Open Page Actions menu
	 * @param WikisUI - ui
	 */
	public void open(WikisUI ui){		
		ui.fluentWaitElementVisible(WikisUIConstants.Page_Actions_Button);
		//Select Menu option
		ui.clickLinkWithJavascript(WikisUIConstants.Page_Actions_Button);
	}
	
}
