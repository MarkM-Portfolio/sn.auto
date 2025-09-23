package com.ibm.conn.auto.util.display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ICBaseUI;

public enum Files_Display_Menu implements Menu {

	TILE("Tile", "css=a[class^='lotusSprite lotusView lotusTile']"),
	DETAILS("Details", "css=a[class^='lotusSprite lotusView lotusDetails']"),
	SUMMARY("Summary", "css=a[class^='lotusSprite lotusView lotusSummary']");
	
	String action = null;
	String menuID = null;
	
	private static Logger log = LoggerFactory.getLogger(Files_Display_Menu.class);
	
	
	Files_Display_Menu(String action, String menuID){	
		this.action = action;
		this.menuID = menuID;
	}
	
	public String getMenuItemText(){
		return this.action;
	}
	
	public String getMenuItemLink(){
		return this.menuID;
	}
	
	/**
	 * 	Selects specified display button
	 *  @param ICBaseUI - ui
	 */
	public void select(ICBaseUI ui) {
			
		log.info("INFO: Make " +action +" display selection ");
		ui.clickLinkWait(this.getMenuItemLink());
	}
	
	
}
