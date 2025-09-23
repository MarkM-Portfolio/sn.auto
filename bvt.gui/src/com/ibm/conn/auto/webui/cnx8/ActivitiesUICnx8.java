package com.ibm.conn.auto.webui.cnx8;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseActivity;

public class ActivitiesUICnx8 extends HCBaseUI{
protected static Logger log = LoggerFactory.getLogger(BlogsUICnx8.class);
	
	public ActivitiesUICnx8(RCLocationExecutor driver) {
		super(driver);
	}
	/**
	 * Locates More Link for specified activity name
	 * @param actName
	 */
	public static String getMore(String actName) {
		return "//td//a[text()='"+actName+"']/ancestor::td/following-sibling::td[contains(@class,'lotusTiny')]//a[text()='More']";
	}
	/**
	 * Locates Priority dropdown for specified activity name
	 * @param actName
	 */	
	public static String getPriority(String actName) {
		return "//a[text()='"+actName+"']/ancestor::tr/following-sibling::tr//li//a[text()='Prioritize']";
	}
	
	/**
	 * Select specified priority option for the activity
	 * @param activity
	 * @param priorityLocator
	 */
	public void setPriority(BaseActivity activity, String priorityLocator) {
		
		log.info("INFO : Select More button");
		clickLinkWaitWd(By.xpath(getMore(activity.getName())), 10, "Select More");
		
		log.info("INFO : Select Priority dropdown");
		waitForElementVisibleWd(By.xpath(getPriority(activity.getName())), 5);
		clickLinkWaitWd(By.xpath(getPriority(activity.getName())), 6, "Select Prioritize");
		
		log.info("INFO : Select Priority menu from drodown");
		waitForElementVisibleWd(By.xpath(priorityLocator), 5);
		clickLinkWithJavaScriptWd(findElement(By.xpath(priorityLocator)));
	}
}
