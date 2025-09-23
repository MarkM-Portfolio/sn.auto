package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ICBaseUI;

public enum Activity_LeftNav_Menu implements Menu{

	ACTIVITYOUTLINE("Activity Outline"),
	RECENTUPDATES("Recent Updates"),
	TODOITEMS("To Do Items"),
	TRASH("Trash"),
	MEMBERS("Members");

	String action = null;
	
	Activity_LeftNav_Menu(String action){
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
