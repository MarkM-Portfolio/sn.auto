package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ProfilesUI;

public enum Profile_View_Menu implements Menu {

	
	MY_PROFILE("My Profile", "css=b>a[role='menuitem']:contains(My Profile), a:contains(My Profile)"),
	MY_CONTACTS("My Contacts", "css=a[role='menuitem']:contains(My Contacts)"),
	MY_NETWORK("My Network", "css=a[role='menuitem']:contains(My Network), a:contains(My Network)"), 
	ORG_Directory("Org Directory", "css=a[role='menuitem']:contains(Directory), a:contains(Directory)"),
	Edit_My_Profile("Edit My Profile", "css=a:contains(Edit My Profile)"),
	Status_Updates("Status Updates", "css=a:contains(Status Updates)");

	private static Logger log = LoggerFactory.getLogger(Profile_View_Menu.class);
	String action = null;
	String link = null;

	Profile_View_Menu(String action, String link){
		this.action=action;
		this.link=link;
	}

	public String getMenuItemText(){
		return this.action;
	}
	
	public String getMenuItemLink(){
		return this.link;
	}
	
	public void open(ProfilesUI ui){

		log.info("INFO: Open Overview menu");
		ui.clickLinkWithJavascript(ProfilesUIConstants.People);

	}

	/**
	 * select - Select view menu item
	 * @param ui
	 */
	public void select(ProfilesUI ui){
		
		open(ui);
		ui.clickLinkWithJavascript(this.getMenuItemLink());
	}
	
}
