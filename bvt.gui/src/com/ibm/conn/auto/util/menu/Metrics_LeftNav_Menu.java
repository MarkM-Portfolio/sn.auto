package com.ibm.conn.auto.util.menu;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.MetricsUI;

public enum Metrics_LeftNav_Menu implements Menu {

	PEOPLE("People", "css=div[widgetid='lconn_metrics_scenes__NavigationTreeNode_2'] span:contains(People)"),
	PARTICIPATION("Participation", "css=div[widgetid='lconn_metrics_scenes__NavigationTreeNode_3'] span:contains(Participation)"),
	CONTENT("Content", "css=div[widgetid='lconn_metrics_scenes__NavigationTreeNode_4'] span:contains(Content)"),
	
	PEOPLE_SC("People", "css=div[class='lotusMenuSubsection'] span:contains(People),div[class='leftTreeULDiv'] span:contains(People),div[widgetid='lconn_metricssc_scenes__NavigationTreeNode_2'] span:contains(People)"),
	PARTICIPATION_SC("Participation", "css=div[class='lotusMenuSubsection'] span:contains(Participation),div[class='leftTreeULDiv'] span:contains(Participation),div[widgetid='lconn_metricssc_scenes__NavigationTreeNode_3'] span:contains(Participation)"),
	CONTENT_SC("Content", "css=div[class='lotusMenuSubsection'] span:contains(Content),div[class='leftTreeULDiv'] span:contains(Content),div[widgetid='lconn_metricssc_scenes__NavigationTreeNode_4'] span:contains(Content)");
	
	
	String action = null;
	String link = null;
	
	Metrics_LeftNav_Menu(String action, String link){
		this.action=action;
		this.link = link;
	}

	public String getMenuItemText(){
		return this.action;
	}
	
	public String getMenuItemLink(){
		return this.link;
	}

	/**
	 * select - Select left navigation menu item
	 * @param ui
	 */
	public void select(MetricsUI ui){
		ui.waitForSameTime();

		ui.clickLinkWithJavascript(this.getMenuItemLink());
		
	}


}
