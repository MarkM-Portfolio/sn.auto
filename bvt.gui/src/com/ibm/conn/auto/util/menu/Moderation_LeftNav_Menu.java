package com.ibm.conn.auto.util.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ICBaseUI;

public enum Moderation_LeftNav_Menu implements Menu{
	
	//"CA" stands for "Content Approval", "FC" stands for "Flagged Content"
	CABLOGSENTRIES("Approval->Blogs->Entries", "css=div[widgetid='lconn_moderation_scenes__NavigationTreeNode_3'] span:contains(Entries)"),
	CABLOGSCOMMENTS("Approval->Blogs->Comments", "css=div[widgetid='lconn_moderation_scenes__NavigationTreeNode_4'] span:contains(Comments)"),
	CAFILESCONTENT("Approval->Files->Content", "css=div[widgetid='lconn_moderation_scenes__NavigationTreeNode_6'] span:contains(Content)"),
	CAFILESCOMMENTS("Approval->Files->Comments", "css=div[widgetid='lconn_moderation_scenes__NavigationTreeNode_7'] span:contains(Comments)"),
	CAFORUMSPOSTS("Approval->Forums->Posts", "css=div[widgetid='lconn_moderation_scenes__NavigationTreeNode_9'] span:contains(Posts)"),
	FCBLOGSENTRIES("Flagged->Blogs->Entries","css=div[widgetid='lconn_moderation_scenes__NavigationTreeNode_12'] span:contains(Entries)"),
	FCBLOGSCOMMENTS("Flagged->Blogs->Comments", "css=div[widgetid='lconn_moderation_scenes__NavigationTreeNode_13'] span:contains(Comments)"),
	FCFILESCONTENT("Flagged->Files->Content", "css=div[widgetid='lconn_moderation_scenes__NavigationTreeNode_15'] span:contains(Content)"),
	FCFILESCOMMENTS("Flagged->Files->Comments","css=div[widgetid='lconn_moderation_scenes__NavigationTreeNode_16'] span:contains(Comments)"),
	FCFORUMSPOSTS("Flagged->Forums->Posts", "css=div[widgetid='lconn_moderation_scenes__NavigationTreeNode_18'] span:contains(Posts)");
	
	String action = null;
	String link = null;
	private static Logger log = LoggerFactory.getLogger(Community_LeftNav_Menu.class);

	Moderation_LeftNav_Menu(String action, String link){
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
	 * select - Select left navigation menu item
	 * @param ui
	 */
	public void select(ICBaseUI ui){
		ui.waitForSameTime();

		//Select Menu option
		log.info("INFO: Make selection " + this.getMenuItemText()+" from Menu ");
		ui.clickLinkWithJavascript(this.getMenuItemLink());
		
	}

}
