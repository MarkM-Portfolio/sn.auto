package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.WikisUI;

public enum WikiPage_LeftNav_Menu implements Menu{


	INDEX("Index", "css=a[id='lconn_wikis_scenes_SiteTools_index']"),
	MEMBERS("Members", "css=a[id='lconn_wikis_scenes_SiteTools_members']"),
	TRASH("Trash", "css=a[id='lconn_wikis_scenes_SiteTools_trash']");

	String action = null;
	String link = null;

	WikiPage_LeftNav_Menu(String action, String link){
		this.action=action;
		this.link=link;
	}

	public String getMenuItemText(){
		return this.action;
	}

	/**
	 * select - Select left navigation menu item
	 * @param ui
	 */
	public void select(WikisUI ui){
		ui.clickLinkWait(link);
	}
	
}
