package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.util.Menu;

public enum Widget_Action_Menu implements Menu {

	MINIMIZE("Minimize"),
	MAXIMIZE("Maximize"),
	REFRESH("Refresh"),
	EDIT("Edit"),
	CHANGETITLE("Change Title"),
	HELP("Help"),
	MOVEUP("Move Up"),
	MOVEDOWN("Move Down"),
	MOVETOPREVIOUSCOLUMN("Move To Previous Column"),
	MOVETONEXTCOLUMN("Move To Next Column"),
	HIDE("Hide"),
	REMOVE("Remove"),
	DELETE("Delete");


	String action = null;


	Widget_Action_Menu(String action){
		this.action = action;
	}

	public String getMenuItemText(){
		return this.action;
	}

}
