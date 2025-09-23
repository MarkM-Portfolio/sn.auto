package com.ibm.conn.auto.webui.cloud;

import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.ForumsUI;

public class ForumsUICloud extends ForumsUI {

	public ForumsUICloud(RCLocationExecutor driver) {
		super(driver);
	}
	
	public void verifyBizCard(){
		String vCardDropIcon = "css=div.vcard.lotusLikeAvatarLink a.menu_drop_icon";
		String bizCardIframe = "css=div.personMenu";

		Assert.assertEquals(driver.getElements(vCardDropIcon).size(),1, "the business card drop icon doesn't show up");
		driver.getFirstElement(vCardDropIcon).click();
		Assert.assertTrue(driver.getSingleElement(bizCardIframe).isVisible(), "the business card doesn't show up");		
	}
}
