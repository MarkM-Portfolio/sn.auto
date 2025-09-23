package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ProfilesUI;

public enum Profile_Tags_Action implements Menu {


	MINIMIZE("Minimize", "css=table[id='socialTagsmoreActions'] td[id='socialTags_toggleAction_text']:contains(Minimize)"),
	MAXIMIZE("Maximize", "css=table[id='socialTagsmoreActions'] td[id='socialTags_toggleAction_text']:contains(Maximize)"),
	REFRESH("Refresh", "css=table[id='socialTagsmoreActions'] td[id='dijit_MenuItem_0_text']:contains(Refresh)"),
	HELP("Help", "css=table[id='socialTagsmoreActions'] td[id='dijit_MenuItem_1_text']:contains(Help)");

	private static Logger log = LoggerFactory.getLogger(Profile_Tags_Action.class);
	String action = null;
	String link = null;

	Profile_Tags_Action(String action, String link){
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

		log.info("INFO: Open Tags action menu");
		ui.clickLinkWithJavascript(ProfilesUIConstants.TagsActionMenu);

	}

	/**
	 * select - Select tags action item
	 * @param ui
	 */
	public void select(ProfilesUI ui){

		open(ui);
		ui.clickLinkWithJavascript(this.getMenuItemLink());
	}

}