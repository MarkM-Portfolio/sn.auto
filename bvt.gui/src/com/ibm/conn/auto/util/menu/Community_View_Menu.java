package com.ibm.conn.auto.util.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;

public enum Community_View_Menu implements Menu {

	
	IM_AN_OWNER("I'm an Owner", "css=a[id='toolbar_catalog_menu_ownedcommunities']"),
	IM_A_MEMBER("I'm a Member", "css=a[href='/communities/service/html/mycommunities']"),
	MY_COMMUNITIES("My Communities", "css=a[id='toolbar_catalog_menu_allmycommunities']"),
	IM_FOLLOWING("I'm Following", "css=span[data-value='/followedcommunities']"),
	IM_INVITED("I'm Invited", "css=a[id='toolbar_catalog_menu_communityinvites']"),
	IVE_CREATED("I've Created", "css=a[id='toolbar_catalog_menu_createdcommunities']"),
	PUBLIC_COMMUNITIES("My Organization Communities", "css=a[id='toolbar_catalog_menu_allcommunities']"),
	TRASH("Trash", "css=a[id='toolbar_catalog_menu_trashedcommunities']");
	

	String action = null;
	String link = null;
	private static Logger log = LoggerFactory.getLogger(Community_LeftNav_Menu.class);

	Community_View_Menu(String action, String link){
		this.action=action;
		this.link=link;
	}

	public String getMenuItemText(){
		return this.action;
	}
	
	public String getMenuItemLink(){
		return this.link;
	}

	/**
	 * select - Select view menu item
	 * @param ui
	 */
	public void select(CommunitiesUI ui){
		log.info("INFO: Select '" +action +"' from view menu ");
		ui.clickLink(link);
	}
	
}
