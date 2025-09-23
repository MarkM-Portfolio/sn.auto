package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;

public enum Com_Action_Menu implements Menu {

	CUSTOMIZE("Customize", "communityMenu_CUSTOMIZEURL_text", "communityMenu_CUSTOMIZE_text"),
	GOTOCOMMUNITY("Goto Community", "communityMenu_GOTOCOMMURL_text", "communityMenu_GOTOCOMM_text"),
	CREATESUB("Create Subcommunity", "communityMenu_CREATESUBURL_text", "communityMenu_CREATESUB"),
	EDIT("Edit Community", "communityMenu_EDITURL_text", "communityMenu_EDITURL_text"),
	EDITSUB("Edit Community", "communityMenu_EDITSUBURL_text", "communityMenu_EDITSUBURL_text"),
	ADDAPP("Add Apps", "communityMenu_CUSTOMIZEURL_text", "communityMenu_CUSTOMIZE_text"),
	MODERATE("Moderate Community", "communityMenu_MODERATE_text", "communityMenu_MODERATE_text"),
	LEAVE("Leave Community", "communityMenu_LEAVE_text", "communityMenu_LEAVE"),
	DELETE("Delete Community", "communityMenu_DELETE_text", "communityMenu_DELETE"),
	MOVECOMMUNITY("Move Community", "communityMenu_MOVE_text", "communityMenu_MOVE"),
	MAILCOMMUNITY("Mail Community", "communityMenu_EMAIL_text", "communityMenu_EMAIL"),
	UNSUBSCRIBEFROMMAIL("Unsubscribe from Mail", "communityMenu_UNSUBSCRIBE_text", "communityMenu_UNSUBSCRIBE"),
	CHANGELAYOUT("Change Layout", "communityMenu_CHANGELAYOUT_text", "communityMenu_CHANGELAYOUT"),
	COPYCOMMUNITYSTRUCTURE("Copy Community", "communityMenu_CREATECOPY_text", "communityMenu_CREATECOPY");
	
	private static Logger log = LoggerFactory.getLogger(Com_Action_Menu.class);
	String action = null;
	String actionLink = null;
	String actionLinkFromOverview = null;
	
	Com_Action_Menu(String action, String actionLink, String actionLinkFromOverview){	
		this.action = action;
		this.actionLink = actionLink;
		this.actionLinkFromOverview = actionLinkFromOverview;
	}
	
	public String getMenuItemText(){
		return this.action;
	}
	
	/**
	 * Get the locator when selected tab is not Overview
	 * @return locator
	 */
	public String getMenuItemLink(){
			return this.actionLink; 
	}
	
	/**
	 * Get the locator when Overview is the current tab
	 * it has been observed that the ID could be different.
	 * @return locator
	 */
	public String getMenuItemLinkFromOverview(){
		return this.actionLinkFromOverview; 
}
	
	public void select(CommunitiesUI ui) {

		//open menu
		//TODO: observed browser hang when clicking Community Actions while the page
		//Add ui.waitForCommunityLoaded() to caller ad-hoc.
		log.info("INFO: Open Community Action menu");
		open(ui);
		
		//Select Menu option
		log.info("INFO: Make selection " + this.getMenuItemText()+" from Menu ");
		ui.getDriver().changeImplicitWaits(3);
		if (ui.isElementPresent("css=#"+this.getMenuItemLink())) {
			ui.navigateMenuByID(this.getMenuItemLink());			
		} else {
			ui.navigateMenuByID(this.getMenuItemLinkFromOverview());
		}
		ui.getDriver().turnOnImplicitWaits();
	}

	public void open(CommunitiesUI ui){
		
		ui.fluentWaitPresent(BaseUIConstants.Community_Actions_Button);
		
		//Select Menu option
		ui.navigateMenuByID("displayActionsBtn");

	}
	
}
 