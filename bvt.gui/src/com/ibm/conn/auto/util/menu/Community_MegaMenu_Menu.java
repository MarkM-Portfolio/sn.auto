package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;

public enum Community_MegaMenu_Menu implements Menu {
	
	IM_AN_OWNER("I'm an Owner", "css=a[class='lotusBold']:contains(I'm an Owner)","css=li[role='menuItem']>a:contains(I'm an Owner)"),
	IM_A_MEMBER("I'm a Member", "css=a[class='lotusBold']:contains(I'm a Member)","css=li[role='menuItem']>a:contains(I'm a Member)"),
	IM_FOLLOWING("I'm Following", "css=a[class='lotusBold']:contains(I'm Following)","css=li[role='menuItem']>a:contains(I'm Following)"),
	IM_INVITED("I'm Invited", "css=a[class='lotusBold']:contains(I'm Invited)","css=li[role='menuItem']>a:contains(I'm Invited)"),
	PUBLIC_COMMUNITIES("My Organization Communities", "css=a[class='lotusBold']:contains(My Organization Communities)","css=li[role='menuItem']>a:contains(My Organization Communities)"),
	MY_COMMUNITIES("My Communities", "css=a[class='lotusBold']:contains(My Communities)","css=li[role='menuItem']>a:contains(My Communities)"),
	DISCOVER("Discover", "css=a[class='lotusBold']:contains(Discover)","css=li[role='menuItem']>a:contains(Discover)");
	
	String action = null;
	String link = null;
	String newLink = null;
	private static Logger log = LoggerFactory.getLogger(Community_MegaMenu_Menu.class);
	
	Community_MegaMenu_Menu(String action, String link, String newLink){
		this.action=action;
		this.link=link;
		this.newLink = newLink;
	}
	
	public String getMenuItemText(){
		return this.action;
	}

	public String getMenuItemLink(){
		return this.link;
	}
	public String getMenuItemNewLink() {
		return this.newLink;
	}
			
	/**
	 * select - Select view menu item
	 * @param ui
	 */
	public void select(CommunitiesUI ui){
		log.info("INFO: Click Communities dropdown menu");
		ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
		
		log.info("INFO: Select '" +action +"' from Communities mega-menu drop-down");
		ui.clickLinkWait(link);
	}
	
	public void open(CommunitiesUI ui){
		
		log.info("INFO: Click Communities dropdown menu");
		ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);

	}

}
