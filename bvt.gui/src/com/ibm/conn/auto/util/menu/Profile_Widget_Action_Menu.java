package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ProfilesUI;

public enum Profile_Widget_Action_Menu implements Menu {
	
	MINIMIZE("Minimize", "css=table[id$='moreActions'] td[id$='_toggleAction_text']:contains(Minimize)"),
	MAXIMIZE("Maximize", "css=table[id$='moreActions'] td[id$='_toggleAction_text']:contains(Maximize)"),
	REFRESH("Refresh", "css=table[id$='moreActions'] td[id^='dijit_MenuItem_'][id$='_text']:contains(Refresh)"),
	HELP("Help", "css=table[id$='moreActions'] td[id^='dijit_MenuItem_'][id$='_text']:contains(Help)");

	private static Logger log = LoggerFactory.getLogger(Profile_Widget_Action_Menu .class);
	String action = null;
	String link = null;

	Profile_Widget_Action_Menu (String action, String link){
		this.action=action;
		this.link=link;
	}

	public String getMenuItemText(){
		return this.action;
	}
	
	public String getMenuItemLink(){
		return this.link;
	}
	
	public void openMyLinksActionMenu(ProfilesUI ui){

		log.info("INFO: Open My Links action menu");
		ui.clickLinkWithJavascript(ProfilesUIConstants.ActionsForMyLinksMenu);

	}
	
	public void openNetworkActionMenu(ProfilesUI ui){

		log.info("INFO: Open My Links action menu");
		ui.clickLinkWithJavascript(ProfilesUIConstants.actionsForNetworkMenu);

	}
	
	public void actionsforRecentupdates(ProfilesUI ui){

		log.info("INFO: Open My Links action menu");
		ui.clickLinkWithJavascript(ProfilesUIConstants.ActionsforRecentupdates);
		
	}
	
	public void actionsforDoyouknow(ProfilesUI ui){

		log.info("INFO: Open My Links action menu");
		ui.clickLinkWithJavascript(ProfilesUIConstants.ActionsforDoyouknow);
		
	}
	
	public void actionsforOrgTags(ProfilesUI ui){

		log.info("INFO: Open My Links action menu");
		ui.clickLinkWithJavascript(ProfilesUIConstants.ActionsforOrgTags);
		
	}
	/**
	 * Action menu for report to chain widget
	 * @param ui
	 */
	public void actionsforReportToChain(ProfilesUI ui){
		log.info("INFO: Open Report to chain action menu");
		ui.clickLinkWithJavaScriptWd(ui.findElement(By.id(ProfilesUIConstants.reportToChainActionMenu)));	
	}
	

	/**
	 * select - Select My Links action item
	 * @param ui
	 */
	public void select(ProfilesUI ui){
		
		ui.clickLinkWithJavascript(this.getMenuItemLink());
	}
	
}



