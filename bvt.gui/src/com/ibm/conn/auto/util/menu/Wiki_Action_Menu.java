package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.WikisUI;

public enum Wiki_Action_Menu implements Menu{

	EDIT("EditWiki", "css=tr[id^='dijit_MenuItem_']:contains(Edit Wiki)"),
	DELETE("DeleteWiki", "css=tr[id^='dijit_MenuItem_']:contains(Delete Wiki)");
	
	private static Logger log = LoggerFactory.getLogger(Com_Action_Menu.class);
	String action = null;
	String link = null;
	
	
	Wiki_Action_Menu(String action, String link){	
		this.action = action;
		this.link = link;
	}
	
	public String getMenuItemText(){
		return this.action;
	}
	
	public String getMenuItemLink(){
		return this.link; 
	}
	
	public void select(WikisUI ui) {

		//open menu
		log.info("INFO: Open Wiki Action menu");
		open(ui);
		
		//Select Menu option
		log.info("INFO: Make selection " + this.getMenuItemText()+" from Menu ");
		ui.navigateMenuByID(ui.getWikiActionMenuID(this.getMenuItemLink()));

	}

	public void open(WikisUI ui){
	
		ui.fluentWaitPresent(WikisUIConstants.WikisActionMenu);
		
		//Select Menu option
		log.info("INFO: Open Wiki Action menu");
		ui.navigateMenuByID("wikiActionMenuLink");

	}
	
}
