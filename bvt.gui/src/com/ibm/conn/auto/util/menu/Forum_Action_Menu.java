package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.util.Menu;

public enum Forum_Action_Menu implements Menu {

	EDIT("Edit Forum"),
	DELETE("Delete Forum"),
	LOCK("Lock Forum"),
	UNLOCK("Unlock Forum");
	

	String action = null;
	
	Forum_Action_Menu(String action){	
		this.action = action;
	}
	
	public String getMenuItemText(){
		return this.action;
	}
		
}