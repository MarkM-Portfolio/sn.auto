package com.ibm.conn.auto.util.menu;

import java.util.List;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.util.Menu;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;

public enum Community_TabbedNav_Menu implements Menu {

	OVERVIEW("Overview", "//li[@role='tab']//a[contains(text(),'Overview') and not(contains(@class,'lotusHidden'))]", "//td[starts-with(@id, 'moreIdx')][contains(text(),'Overview')]"),
	RECENT_UPDATES("RecentUpdates", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Recent Updates)", "css=td[id^='moreIdx']:contains(Recent Updates)"),
	STATUSUPDATES("StatusUpdates", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Status Updates)", "css=td[id^='moreIdx']:contains(Status Updates)"),
	MEMBERS("Members", "css=li[role='tab']:not(.lotusHidden)>a:contains(Members)", "css=td[id^='moreIdx']:contains(Members)"),
	METRICS("Metrics", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Metrics)", "css=td[id^='moreIdx']:contains(Metrics)"),
	FORUMS("Forums", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Forums)", "css=td[id^='moreIdx']:contains(Forums)"),
	BOOKMARK("Bookmarks", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Bookmarks)", "css=td[id^='moreIdx']:contains(Bookmarks)"),
	BLOG("Blog", "css=li[role='tab']:not(.lotusHidden)>a:contains(Blog)", "css=td[id^='moreIdx']:contains(Blog)"),
	WIKI("Wiki", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Wiki)", "css=td[id^='moreIdx']:contains(Wiki)"),
	ACTIVITIES("Activities", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Activities)", "css=td[id^='moreIdx']:contains(Activities)"),
	EVENTS("Events", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Events)", "css=td[id^='moreIdx']:contains(Events)"),
	IDEATIONBLOG("Ideation Blog", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Ideation Blog)", "css=td[id^='moreIdx']:contains(Ideation Blog)"),
	FEEDS("Feeds", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Feeds)", "css=td[id^='moreIdx']:contains(Feeds)"),
	MEDIA("Media", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Media)", "css=td[id^='moreIdx']:contains(Media)"),
	RELATEDCOMMUNITIES("Related Communities", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Related Communities)", "css=td[id^='moreIdx']:contains(Related Communities)"),
	LIBRARY("Library", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Library)", "css=td[id^='moreIdx']:contains(Library)"),
	MODERATION("Moderation", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Moderation)", "css=td[id^='moreIdx']:contains(Moderation)"),
	SURVEYS("Surveys", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Surveys)", "css=td[id^='moreIdx']:contains(Surveys)"),
	FILES("Files", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Files)", "css=td[id^='moreIdx']:contains(Files)"),
	HIGHLIGHTS("Highlights", "css=ul#lotusNavBar>li[role='tab']:not(.lotusHidden)>a:contains(Highlights)", "css=td[id^='moreIdx']:contains(Highlights)");
	
	String action = null;
	String link = null;
	String moreMenuLink = null;
	private static Logger log = LoggerFactory.getLogger(Community_TabbedNav_Menu.class);
	
	Community_TabbedNav_Menu(String action, String link, String more){
		this.action=action;
		this.link=link;
		this.moreMenuLink = more;
	}
	
	public String getMenuItemText(){
		return this.action;
	}

	public String getMenuItemLink(){
		return this.link;
	}
	
	public String getMoreMenuItemLink(){
		return this.moreMenuLink;
	}
	
	/**
	 * select - Select tabbed navigation menu item
	 * @param ui
	 */
	public void select(ICBaseUI ui) {
		ui.waitForSameTime();
		ui.waitForJQueryToLoad(ui.getDriver());

		ui.getDriver().changeImplicitWaits(5);
		if (ui.isElementPresent(this.getMenuItemLink())) {
			// Select Menu option
			log.info("INFO: Make selection " + this.getMenuItemText() + " from Menu ");
			// Don't use clickLinkWithJavascript otherwise the browser may get frozen
			ui.clickLinkWait(this.getMenuItemLink());
		} else {
			log.info("INFO: " + this.getMenuItemText() + " not visible, checking the 'More' dropdown list");
			HCBaseUI hc = new HCBaseUI(ui.getDriver());
			hc.scrollToElementWithJavaScriptWd(By.cssSelector("div[id=\"theme-switcher-wrapper\"]"));
			hc.clickLinkWaitWd(By.cssSelector("li[id='tabNavMoreBtn'] div"), 4, "Click on more link");
			ui.fluentWaitElementVisible("css=table#CommunitiesMoreMenu");
			// Select option from dropdown
			log.info("INFO: Make selection " + this.getMenuItemText() + " from the dropdown");
			ui.clickLinkWithJavascript(this.getMoreMenuItemLink());
		}
		ui.getDriver().turnOnImplicitWaits();
	}
	
	/**
	 * Select the item in the navigation menu with retry
	 * Note: the menu items may change after clicking so the whole menu 
	 * needs to be loop through in every retry.
	 * @param ui
	 * @param retry # of retry
	 */
	public void select(ICBaseUI ui, int retry) {
		int retried = -1;
		
		while (retried < retry)  {
			boolean isSelected = false;
			
			try {
				select(ui);
			} catch (AssertionError | org.openqa.selenium.TimeoutException ae) {
				if (retry - retried <= 1) {
					// last try so throw the exception
					throw ae;
				} else {
					log.info("Cannot find " + this.getMenuItemLink() + " to click anymore, will check again.");
					retried++;
					continue;
				}
			}
			
			retried++;
			ui.waitForPageLoaded(ui.getDriver());
			ui.waitForJQueryToLoad(ui.getDriver());

			try {
				isSelected = isTabSelected(ui);
			} catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException se)  {
				// menu is refreshed during check so let's check again
				log.info("Menu is refreshed, will check again.");
				isSelected = isTabSelected(ui);
			}
			
			if (isSelected) {
				log.info("Target item selected in the menu.");
				return;				
			}
		}
		
		log.warn("WARN: " + this.getMenuItemText() + " is not selected after retrying " + retry + " times.");
	}
	
	/**
	 * Returns whether the tab is selected. 
	 * If the item is in the More dropdown, returns whether 'More' is selected.
	 * @param ui
	 */
	public boolean isTabSelected(ICBaseUI ui)  {
		// use getElements so the items in the 'More' will be included.
		// when Blog or Wiki is selected a diff class (bizCardNavMenu) is used.
		List<Element> tabs = ui.getDriver().getElements("css=#lotusNavBar li[role='tab'], #bizCardNavMenu li[role='tab']");
		boolean checkMore = false;
		boolean isMoreSelected = false;
		
		for (Element tab : tabs)  {
			if (tab.getAttribute("innerText").equalsIgnoreCase(this.getMenuItemText()))  {
				// found the target item in the menu
				String tabClasses = tab.getAttribute("class");
				if (!tabClasses.contains("lotusHidden"))  {
					if (tabClasses.contains("lotusSelected")) {
						return true;
					}
				} else {
					// it's an item in the More dropdown, 'More" should be selected instead.
					checkMore = true;
				}
			} else if (tab.getAttribute("id").equals("tabNavMoreBtn"))  {
				// it's the More dropdown, check if it's selected
				isMoreSelected = tab.getAttribute("class").contains("lotusSelected");
			}
		}
		
		return checkMore && isMoreSelected;
	}

}
