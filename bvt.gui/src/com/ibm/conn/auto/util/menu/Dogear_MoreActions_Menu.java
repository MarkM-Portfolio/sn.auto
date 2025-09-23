package com.ibm.conn.auto.util.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.DogearUI;

public enum Dogear_MoreActions_Menu implements Menu {
	
	ADD_TO_ACTIVITY("Add to Activity", "css=tbody[class='dijitReset'] tr td:contains(Add to Activity)"),
	ADD_TO_COMMUNITY("Add to Community", "css=tbody[class='dijitReset'] tr td:contains(Add to Community)"),
	ADD_TO_BLOG("Add to Blog", "css=tbody[class='dijitReset'] tr td:contains(Add to Blog)"),
	FLAG_AS_BROKEN_URL("Flag as Broken URL", "css=tbody[class='dijitReset'] tr td:contains(Flag as Broken URL)");

	
	private static Logger log = LoggerFactory.getLogger(Events_MoreActions_Menu.class);
	String action = null;
	String actionLink = null;
	
	Dogear_MoreActions_Menu(String action, String actionLink){	
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
	 * 	Selects specified menu option from the More Actions menu on the Bookmarks details view
	 *  @param DogearUI - ui
	 *  @param sUUID - UUID of the bookmark target
	 */
	public void select(DogearUI ui, String sUUID) {
		//Open More Actions menu
		log.info("INFO: Open More Actions menu");
		open(ui, sUUID);

		//Select option specified from the menu
		log.info("INFO: Select " + this.getMenuItemText()+" from menu ");
		ui.clickLinkWait(this.getMenuItemLink());
		//navigateMenuByID(this.getMenuItemLink());
	}

	/**
	 * Open More Actions menu from Bookmarks details view
	 * @param DogearUI - ui
	 * @param sUUID - UUID of the bookmark target
	 */
	public void open(DogearUI ui, String sUUID){	
		
		ui.fluentWaitElementVisible("css=#MoreActions_" + sUUID);
		//Select Menu option
		ui.clickLinkWithJavascript("css=#MoreActions_" + sUUID);
	}


}
