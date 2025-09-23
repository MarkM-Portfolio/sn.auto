package com.ibm.conn.auto.webui.cnx8;

import org.openqa.selenium.By;

import com.ibm.atmn.waffle.core.RCLocationExecutor;

public abstract class SecondaryNav extends HCBaseUI {
	
	public String secondaryNavBar = "#tertiary_level_nav ";
	
	public SecondaryNav(RCLocationExecutor driver) {
		super(driver);
	}
	
	/**
	 * Click on the given item in the secondary nav
	 * @param locator
	 */
	public void clickSecNavItem(String locator)  {
		String elemLocator = secondaryNavBar + locator;
		clickLinkWaitWd(By.cssSelector(elemLocator),7, elemLocator);
	}
	
	public String getSecNavLocator(String locator) {
		return secondaryNavBar + locator;
	}
}
