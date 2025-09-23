package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ICBaseUI;

public enum Homepage_LeftNav_Menu implements Menu{


	UPDATES("Updates"),
	MENTIONS("Mentions"),
	MYNOTIFICATIONS("My Notifications"),
	ACTIONREQUIRED("Action Required"),
	SAVED("Saved"),
	MYPAGE("My Page"),
	GETTINGSTARTED("Getting Started");

	String action = null;

	Homepage_LeftNav_Menu(String action){
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
		ui.clickLinkWithJavascript("linkpartial=" + this.getMenuItemText());
	}
	
	
}
