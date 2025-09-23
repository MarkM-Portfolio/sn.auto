package com.ibm.conn.auto.util.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;

public enum Events_View_Menu implements Menu{

	ONE_DAY("One Day", "One Day"),
	TWO_DAYS("Two Days", "Two Days"),
	FIVE_DAYS("Five Days", "Five Days"),
	WEEK("Week", "Week"),
	MONTH("Month", "Month");
	
	private static Logger log = LoggerFactory.getLogger(Events_View_Menu.class);
	String action = null;
	String actionLink = null;
	
	Events_View_Menu(String action, String actionLink){	
		this.action = action;
		this.actionLink = actionLink;
	}
	
	public String getMenuItemText(){
		return this.action;
	}
	
	public String getMenuItemLink(){
			return this.actionLink; 
	}
	
	public void select(CommunitiesUI ui) {

		//open menu
		log.info("INFO: Open Event View menu");
		open(ui);
		
		//Select Menu option
		log.info("INFO: Make selection " + this.getMenuItemText()+" from Menu ");
		ui.clickLinkWait("xpath=//td[contains(text(),'" + this.getMenuItemLink() + "')]");

	}

	public void open(CommunitiesUI ui){
		
	
		ui.fluentWaitElementVisible(CalendarUI.CalendarView);
		//Select Menu option
		ui.clickLinkWithJavascript(CalendarUI.CalendarView);

	}
	
	
}
