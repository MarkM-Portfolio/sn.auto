package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.WikisUI;

public enum Wiki_LeftNav_Menu implements Menu{


	OWNER("I'm an Owner"),
	EDITOR("I'm an Editor"),
	READER("I'm a Reader"),
	FOLLOWING("I'm Following"),
	PUBLICWIKIS("Public Wikis");

	String action = null;

	Wiki_LeftNav_Menu(String action){
		this.action=action;
	}

	public String getMenuItemText(){
		return this.action;
	}

	/**
	 * select - Select left navigation menu item
	 * @param ui
	 */
	public void select(WikisUI ui){
		ui.clickLinkWithJavascript("link=" + this.getMenuItemText());
	}
	
	
}
