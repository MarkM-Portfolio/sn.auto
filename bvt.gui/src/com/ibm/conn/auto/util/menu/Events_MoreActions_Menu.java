package com.ibm.conn.auto.util.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.CalendarUI;

public enum Events_MoreActions_Menu implements Menu {

	NOTIFY_OTHER_PEOPLE("Notify Other People", "calendar_event_viewer-miNotify_text"),
			     DELETE("Delete", "calendar_event_viewer-miDelete_text");
	
	private static Logger log = LoggerFactory.getLogger(Events_MoreActions_Menu.class);
	String action = null;
	String actionLink = null;
	
	Events_MoreActions_Menu(String action, String actionLink){	
		this.action = action;
		this.actionLink = actionLink;
	}
	
	public String getMenuItemText(){
		return this.action;
	}
	
	public String getMenuItemLink(){
			return this.actionLink; 
	}
	/**
	 * 	Selects specified menu option from the More Actions menu on an event details page
	 *  @param CalendarUI - ui
	 */
	public void select(CalendarUI ui) {
		//Open More Actions menu
		log.info("INFO: Open More Actions menu");
		open(ui);
		
		//Select option specified from the menu
		log.info("INFO: Select " + this.getMenuItemText()+" from menu ");
		ui.navigateMenuByID(this.getMenuItemLink());
	}
	
	/**
	 * Open More Actions menu from events details page
	 * @param CalendarUI - ui
	 */
	public void open(CalendarUI ui){		
		ui.fluentWaitElementVisible(CalendarUI.MoreActions);
		//Select Menu option
		ui.clickLinkWithJavascript(CalendarUI.MoreActions);
	}
	

}

