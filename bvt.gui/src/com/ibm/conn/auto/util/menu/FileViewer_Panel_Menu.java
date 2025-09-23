package com.ibm.conn.auto.util.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.FileViewerUI;


public enum FileViewer_Panel_Menu implements Menu {
	
		COMMENTS("Comments", "css=li.comments"),
		SHARING("Sharing", "css=li.share"),
		VERSIONS("Versions", "css=li.version"),
		ABOUT("About", "css=li.about");
		
		private String name;
		private String selector;
		private static Logger log = LoggerFactory.getLogger(FileViewer_Panel_Menu.class);
		
		FileViewer_Panel_Menu(String name, String selector) {
			this.name = name;
			this.selector = selector;
		}
		
		public String getName() {
			return name;
		}
		
		public String getSelector() {
			return selector;
		}
		
		
		public void select(FileViewerUI ui) {
			log.info("INFO: Selecting " + getName() + " panel");
			ui.clickLinkWait(getSelector());
		}
	
	

}
