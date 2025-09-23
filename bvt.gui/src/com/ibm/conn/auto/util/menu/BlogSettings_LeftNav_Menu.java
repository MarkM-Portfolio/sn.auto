package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.BlogsUI;

public enum BlogSettings_LeftNav_Menu implements Menu {

	CREATEEDIT("Create & Edit"),
	ENTRIES("Entries"),
	COMMENTS("Comments"),
	LINKS("Links"),
	FILEUPLOADS("File Uploads"),
	REFERRERS("Referrers"),
	GENERAL("General"),
	AUTHORS("Authors"),
	THEME("Theme");

	String action = null;
	
	BlogSettings_LeftNav_Menu(String action){
		this.action=action;
	}

	public String getMenuItemText(){
		return this.action;
	}

	/**
	 * select - Select left navigation menu item
	 * @param ui
	 */
	public void select(BlogsUI ui){
		ui.clickLinkWithJavascript("link=" + this.getMenuItemText());
	}
	
	
}
