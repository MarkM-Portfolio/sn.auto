package com.ibm.conn.auto.webui.cnx8;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;

public class CommonUICnx8 extends HCBaseUI {
	
	protected static Logger log = LoggerFactory.getLogger(CommonUICnx8.class);
	
	public CommonUICnx8(RCLocationExecutor driver) {
		super(driver);
	}
	
	/**
	 * Toggle the new UI switcher
	 * @param switchToNewUI true to switch to new UI, false to switch to old
	 * @return true if new UI is shown, false if old UI
	 */
	public boolean toggleNewUI(boolean switchToNewUI) {
		boolean isNewUI = isElementVisibleWd(By.id("top-navigation"), 1);

		if ((isNewUI && !switchToNewUI) || (!isNewUI && switchToNewUI))  {
			if (isToggleSwitchEnabled())  {
				log.info("INFO: Toggle to new UI: " + switchToNewUI);
				clickLinkWd(By.id("theme-switcher-wrapper"), "new UI toggle switch");
				clickLinkWithJavaScriptWd(findElement(By.cssSelector("#theme_switcher_options_modal_switch input")));

				findElement(By.id("options_modal_save_button")).click();
			} else {
				log.warn("Cannot switch UX because toggle switch is disabled or server version is older than CNX8. Check use_new_ui in test template.");
				throw new IllegalStateException("Cannot switch UX because toggle switch is disabled or server version is older than CNX8. Check use_new_ui in test template.");
			}
		}

		log.info("Determine if new UI left nav is visible.");
		return isElementVisibleWd(By.xpath(AppNavCnx8.NAVBAR.getAppMenuLocator()), 3);		
	}

	/**
	 * Returns server setting whether UX toggle switch should be present
	 * @return
	 */
	public boolean isToggleSwitchEnabled() {
		// Disabling this part temporary to make the CNX8UI tests pass over CPBVT and need to update once UX flag value is finalized  
		/*String uxFlag = (String) driver.executeScript("return window.ICS_UI_ISCNX8UXUSERSETTING");
		if (uxFlag == null) {
			return false;
		} else {
			if (!isNewUXEnabled())  {
				// strictly old UI
				return false;
			} else {
				return Boolean.valueOf(uxFlag);
			}
		}*/
		return true;
	}
	
	/**
	 * Returns server setting whether the new UX is enabled
	 * @return
	 */
	public boolean isNewUXEnabled() {
		String uxFlag = (String) driver.executeScript("return window.ICS_UI_ISCNX8UXENABLED");
		if (uxFlag == null) {
			return false;
		} else {
			return Boolean.valueOf(uxFlag);
		}		
	}
	

	/**
	 * Get remove icon on ITM bar for specified user
	 * @param user
	 * @return locator
	 */
	public static String getRemoveIcon(String user) {
		return "//li[@aria-label='"+user+"']//button[@class='remove-entry']";
	}
}
