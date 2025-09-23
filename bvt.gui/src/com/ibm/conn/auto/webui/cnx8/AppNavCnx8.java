package com.ibm.conn.auto.webui.cnx8;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum AppNavCnx8 {

	NAVBAR("Nav Bar", "//*[@id='side-navigation']"),
	HOMEPAGE("Home", NAVBAR.getAppMenuLocator()+"//span[contains(text(), 'Home')]"),
	COMMUNITIES("Communities", NAVBAR.getAppMenuLocator()+"//span[contains(text(), 'Communities')]"),
	PEOPLE("People", NAVBAR.getAppMenuLocator()+"//span[contains(text(), 'People')]"),
	FILES("Files", NAVBAR.getAppMenuLocator()+"//span[contains(text(), 'Files')]"),
	SETTINGS("Settings", NAVBAR.getAppMenuLocator()+"//span[contains(text(), 'Settings')]"),
	MORE("More", NAVBAR.getAppMenuLocator()+"//span[contains(text(), 'More')]"),
	
	NOTIFICATIONS("Notifications", NAVBAR.getAppMenuLocator()+"//span[contains(text(), 'Notifications')]"),
	PROFILE("Profile", NAVBAR.getAppMenuLocator()+"//span[contains(text(), 'Profile')]"),
	HELP("Help", NAVBAR.getAppMenuLocator()+"//span[contains(text(), 'Help')]"),
	
	SUBNAV("Sub Nav Bar", "//*[@x-placement='right-start']"),
	ACTIVITIES("Activities", SUBNAV.getAppMenuLocator()+"//span[contains(text(), 'Activities')]"),
	BLOGS("Blogs", SUBNAV.getAppMenuLocator()+"//span[contains(text(), 'Blogs')]"),
	BOOKMARKS("Bookmarks", SUBNAV.getAppMenuLocator()+"//span[contains(text(), 'Bookmarks')]"),
	FORUMS("Forums", SUBNAV.getAppMenuLocator()+"//span[contains(text(), 'Forums')]"),
	WIKIS("Wikis", SUBNAV.getAppMenuLocator()+"//span[contains(text(), 'Wikis')]"),
	BOARDS("Boards", SUBNAV.getAppMenuLocator()+"//span[contains(text(), 'Boards')]");


	private static Logger log = LoggerFactory.getLogger(AppNavCnx8.class);
	
	String app = null;
	String locator = null;
	
	AppNavCnx8(String app, String locator){
		this.app = app;
		this.locator =  locator;
	}
	
	public String getAppMenuText(){
		return this.app;
	}
	
	public String getAppMenuLocator(){
		return this.locator;
	}
	
	public void select(HCBaseUI ui) {
		String appLocator = this.getAppMenuLocator();
		
		if (this.getAppMenuLocator().contains(SUBNAV.getAppMenuLocator())) {
			if (ui.isElementVisibleWd(By.xpath(this.getAppMenuLocator()), 5))  {
				// app by default is in submenu but it's now at the root level
				appLocator = this.getAppMenuLocator()
						.replace(SUBNAV.getAppMenuLocator(), NAVBAR.getAppMenuLocator());			
			} else {
				// app is still in the More submenu
				log.info("Click " + MORE.getAppMenuText());
				ui.clickLinkWaitWd(By.xpath(MORE.getAppMenuLocator()), 5);
			}
		}
		log.info("Click " + this.getAppMenuText());
		ui.clickLinkWaitWd(By.xpath(appLocator), 5);
	}
	
	
	public boolean isAppSelected(HCBaseUI ui) {
		boolean isSelected = false;
		
		// app must be at the root level in order to be shown as  
		// selected so updated the locator to check if needed
		String appLocator = this.getAppMenuLocator()
				.replace(SUBNAV.getAppMenuLocator(), NAVBAR.getAppMenuLocator());
		
		try {
			WebElement app = ui.findElement(By.xpath(appLocator+"/parent::li"));
			isSelected = app.getAttribute("class").toLowerCase().contains("mui-selected");
		} catch (NoSuchElementException ne)  {
			// cannot even find the app at root level
			return false;
		}
		
		return isSelected;
	}

}
