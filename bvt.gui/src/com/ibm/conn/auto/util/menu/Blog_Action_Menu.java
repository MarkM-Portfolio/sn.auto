package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.util.Menu;

public enum Blog_Action_Menu implements Menu{

	VIEWBLOG("View Blog"),
	NEWENTRY("New Entry"),
	MANAGEBLOG("Manage Blog");
	
	String action = null;
	
	Blog_Action_Menu(String action){	
		this.action = action;
	}
	
	public String getMenuItemText(){
		return this.action;
	}
	
}
