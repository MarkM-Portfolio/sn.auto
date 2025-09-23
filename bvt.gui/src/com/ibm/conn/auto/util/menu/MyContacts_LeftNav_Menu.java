package com.ibm.conn.auto.util.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ProfilesUI;

public enum MyContacts_LeftNav_Menu implements Menu {

	
	ALL_CONTACTS("All Contacts", "css=div#lotusSidenav div.lotusMenuSection > ul > li > a[title='All contacts']"),
	MY_NETWORK("My Network", "css=div#lotusSidenav div.lotusMenuSection > ul > li > a[title='My network'], a#aColleagues"),
	MY_CONTACTS("My Contacts", "css=div#lotusSidenav div.lotusMenuSection > ul > li > a[title='My contacts']"),
	INVITATIONS("Invitations", "css=div#lotusSidenav div.lotusMenuSection > ul > li > a[title='Invitations'], a#inivtesMenuA"),
	BY_ORGANIZATION("By Organization", "css=div#lotusSidenav div.lotusMenuSection > ul > li > a[title='By organization']"),
	FOLLOWING("Following", "css=div#lotusSidenav div.lotusMenuSection > ul > li > a[title$='m following'], a#aFollowing"),
	FOLLOWERS("Followers", "css=div#lotusSidenav div.lotusMenuSection > ul > li > a[title='People following me'],a#aFollowers"),
	MY_GROUPS("My Groups", "css=div#lotusSidenav div#scGroupTree a#scGroupTree_expandLink"),
	NEW_GROUP("New Group", "css=div#lotusSidenav div#scGroupTree a[class='scActionFirst'][title='New Group']");

	private static Logger log = LoggerFactory.getLogger(MyContacts_LeftNav_Menu.class);
	String action = null;
	String link = null;

	MyContacts_LeftNav_Menu(String action, String link){
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
	public void open(ProfilesUI ui){

		log.info("INFO: Open Left Nav menu");
		ui.clickLinkWithJavascript(this.getMenuItemLink());
	}
	
}
