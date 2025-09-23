package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ICBaseUI;

public enum Files_LeftNav_Menu implements Menu{

	PINNEDFILES("Pinned Files"),
	MYFILES("My Files"),
	SHAREDWITHME("Shared With Me"),
	SHAREDBYME("Shared By Me"),
	COMMUNITYFILES("Community Files"),
	PUBLICFILES("Public Files"),
	TRASH("Trash"),
	MYFOLDERS("My Folders"),
	PUBLICFOLDERS("Public Folders");

	String action = null;
	
	Files_LeftNav_Menu(String action){
		this.action=action;
	}

	public String getMenuItemText(){
		return this.action;
	}

	/**
	 * select - Select left navigation menu item
	 * @param ui
	 */
	public void select(ICBaseUI ui){
		ui.clickLinkWithJavascript("link=" + this.getMenuItemText());
	}

}
