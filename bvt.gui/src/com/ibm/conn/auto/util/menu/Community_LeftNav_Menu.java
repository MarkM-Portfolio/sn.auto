package com.ibm.conn.auto.util.menu;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.Executor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ICBaseUI;

public enum Community_LeftNav_Menu implements Menu {

	    HIGHLIGHTS("Highlights", "css=li[role='button']>a:contains(Highlights)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Highlights)"),
		OVERVIEW("Overview", "css=li[role='button']>a:contains(Overview), li[role='button']>a:contains( Overview )", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Overview), div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains( Overview )"),
		RECENT_UPDATES("RecentUpdates", "css=li[role='button']>a:contains(Recent Updates)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Recent Updates)"),
		STATUSUPDATES("StatusUpdates", "css=li[role='button']>a:contains(Status Updates)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Status Updates)"),
		MEMBERS("Members", "css=li[role='button']>a:contains(Members)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Members)"),
		FORUMS("Forums", "css=li[role='button']>a:contains(Forums)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Forums)"),
		BOOKMARK("Bookmarks", "css=li[role='button']>a:contains(Bookmarks)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Bookmarks)"),
		FILES("Files", "css=li[role='button']>a:contains(Files)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Files)"),
		WIKI("Wiki", "css=li[role='button']>a:contains(Wiki)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Wiki)"),
		BLOG("Blog", "css=li[role='button']>a:contains(Blog)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Blog)"),
		ACTIVITIES("Activities", "css=li[role='button']>a:contains(Activities)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Activities)"),
		MEDIA("Media", "css=li[role='button']>a:contains(Media)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Media)"),
		METRICS("Metrics", "css=li[role='button']>a:contains(Metrics)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Metrics)"),
		IDEATIONBLOG("Ideation Blog", "css=li[role='button']>a:contains(Ideation Blog)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Ideation Blog)"),
		RELATEDCOMMUNITIES("Related Communities", "css=li[role='button']>a:contains(Related Communities)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Related Communities)"),
		LIBRARY("Library", "css=li[role='button']>a:contains(Library)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Library)"),
		EVENTS("Events", "css=li[role='button']>a:contains(Events)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Events)"),
		FEEDS("Feeds", "css=li[role='button']>a:contains(Feeds)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Feeds)"),
		MODERATION("Moderation", "css=li[role='button']>a:contains(Moderation)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Moderation)"),
		SURVEYS("Surveys", "css=li[role='button']>a:contains(Surveys)", "css=div.lotusMenu[role='navigation'] div#dropdownNavMenu a:contains(Surveys)");
		

		String action = null;
		String link = null;
		String newLink = null;
		private static Logger log = LoggerFactory.getLogger(Community_LeftNav_Menu.class);

		Community_LeftNav_Menu(String action, String link, String newLink){
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
		 * select - Select left navigation menu item
		 * @param ui
		 */
		public void select(ICBaseUI ui) {
			if (isTabbed(ui.getDriver())) {
				getMatchingMenu().select(ui);
			} else {
				ui.waitForSameTime();
				
				//check if new left nav is present
				if(ui.isElementPresent("css=#dropdownNavMenuContainer")){
					//open Overview menu
					open(ui);
					//Select Menu option
					log.info("INFO: Make selection " + this.getMenuItemText()+" from Menu ");
					ui.clickLinkWithJavascript(this.getMenuItemNewLink());
				} else {
					//Select Menu option
					log.info("INFO: Make selection " + this.getMenuItemText()+" from Menu ");
					ui.clickLinkWithJavascript(this.getMenuItemLink());
				}
			}
		}

		public void open(ICBaseUI ui){
			
			log.info("INFO: Open Overview menu");
			ui.clickLinkWithJavascript("css=#dropdownNavMenuTitleLink");

		}
		
		private boolean isTabbed(Executor driver) {
			GatekeeperConfig gkc;
			String gk_flag = "communities-tabbed-nav";
			TestConfigCustom cfg = TestConfigCustom.getInstance();
			log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
			if(cfg.getProductName().equalsIgnoreCase("onprem")){
				String adminUserToken = getClass().getSimpleName() + Helper.genStrongRand();
				User adminUser = cfg.getUserAllocator().getAdminUser(adminUserToken);
				String serverURL = APIUtils.formatBrowserURLForAPI(cfg.getTestConfig().getBrowserURL());
				gk_flag = "COMMUNITIES_TABBED_NAV";
				gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
				cfg.getUserAllocator().checkInAllAdminUsersWithToken(adminUserToken);
			} else{
				gkc = GatekeeperConfig.getInstance(driver);
			}
			return gkc.getSetting(gk_flag);
		}
		
		private Community_TabbedNav_Menu getMatchingMenu() {
			for(Community_TabbedNav_Menu menu: Community_TabbedNav_Menu.values()) {
				if(menu.toString().equals(this.toString())) {
					return menu;
				}
			}
			
			return null;
		}

}
