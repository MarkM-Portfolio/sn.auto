package com.ibm.conn.auto.webui;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.onprem.NotificationCenterOnPrem;

public class NotificationCenterUI extends ICBaseUI {
	
	private static Logger log = LoggerFactory.getLogger(NotificationCenterUI.class);
	
	public static String notificationIcon = "css=li#lotusBannerNotifications a";
	public static String notificationNewCount = "css=div[id*='NotificationCenterInlineBadge'] div.icBanner-badge[role=alert]";
	public static String notificationDownArrow = "css=div#acNTUTabId span#ac-toggle-dropdown";
	public static String notificationMarkAllRead = "css=div#acNTUTabId a#firstDDLOption";
	
	public NotificationCenterUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	
	/**
	 * Returns the new notification count in the Notification icon.
	 * @return count in the icon, 0 if no count is found.
	 */
	public int getNewNotificationCountInIcon() {
		Element newCountElm = driver.getSingleElement(notificationNewCount);
		String count = newCountElm.getText();
		if (count.isEmpty())  {
			return 0;
		} else {
			return Integer.parseInt(count);
		}
	}
	
	/**
	 * There is a lag time until the new notification count appears
	 * in the Notification bell icon. This method waits until it appears.
	 * @param expectedCount
	 * @param exactMatch true will verify count is an exact match, otherwise just verify if it's greater than.
	 */
	public void verifyNewNotificationCount(int expectedCount, boolean exactMatch) {
		WebDriverWait wait = new WebDriverWait((WebDriver) driver.getBackingObject(), 10);
		ExpectedCondition<Boolean> expected = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				int count = getNewNotificationCountInIcon();
				if (exactMatch)  {
					if (count == expectedCount)  {
						log.info("Expected # of new notification found: " + expectedCount);
						return true;
					} else {
						return false;
					}
				} else if (count >= expectedCount)  {
					log.info(String.format("Actual count (%s) >= Expected # of new notification (%s)", count, expectedCount));
					return true;					
				} else {
					return false;
				}
			}
		};
		wait.withMessage("Expected new notification count not found: " + expectedCount).until(expected);
	}

	
	/**
	 * Loop through the messages in the Notification Center to find the target message.
	 * @param message target message
	 * @return Element of the message, null if not found.
	 */
	public WebElement getNotification(String message) {
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 10);
		List<WebElement> notifications = wait.until(
				ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div#action-center-tab-panel div[role='listitem']")));
		
		for (WebElement entry : notifications)  {
			WebElement summary = entry.findElement(By.cssSelector("div.ic-notification-summary"));
			if (summary.getText().contains(message))  {
				return entry;
			}
		}
		return null;
	}
	
	/**
	 * Toggle the given notification read status
	 * @param message
	 * @return notification status label after toggle
	 */
	public String toggleNotificationReadStatus(WebElement message) {
		WebElement readStatus = message.findElement(By.cssSelector("div.ic-notification-mark[role=button]"));
		log.info("Original message read status: " + readStatus.getAttribute("aria-label"));
		readStatus.click();
		return readStatus.getAttribute("aria-label");
	}
	
	public static NotificationCenterUI getGui(String product, RCLocationExecutor driver) {
		// add class for other offerings if needed
		if (product.toLowerCase().equals("onprem")) {
			return new NotificationCenterOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}

}
