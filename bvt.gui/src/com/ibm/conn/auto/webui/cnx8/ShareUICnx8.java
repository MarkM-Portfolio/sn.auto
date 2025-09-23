package com.ibm.conn.auto.webui.cnx8;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.constants.ShareUIContants;

public class ShareUICnx8 extends HCBaseUI {
	
	private static Logger log = LoggerFactory.getLogger(ShareUICnx8.class);

	public ShareUICnx8(RCLocationExecutor driver) {
		super(driver);
	}
	
	/**
	 * This method opens the Share In Connections dialog.
	 * It takes care of scenario where a dropdown is shown when Teams share is enabled.
	 */
	public void openShareInConnectionsDialog() {
		log.info("INFO: Click on Share Button");
		clickLinkWaitWd(By.cssSelector(ShareUIContants.share), 2);
		if (isElementDisplayedWd(By.xpath(ShareUIContants.shareInConnectionsDropDownOption))) {
			// dropdown is there, click and select the option
			clickLinkWd(By.xpath(ShareUIContants.shareInConnectionsDropDownOption));
		}	
	}
 

}
