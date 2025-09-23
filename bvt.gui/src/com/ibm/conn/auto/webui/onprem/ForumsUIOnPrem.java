package com.ibm.conn.auto.webui.onprem;

import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.ForumsUI;


public class ForumsUIOnPrem extends ForumsUI {

	public ForumsUIOnPrem(RCLocationExecutor driver) {
		super(driver);
	}
	public void verifyBizCard(){
		Assert.assertTrue(driver.getSingleElement(ForumsUIConstants.bizCardLink).isVisible(), "the business card link doesn't show up");
		driver.getSingleElement(ForumsUIConstants.bizCardLink).hover();
		this.fluentWaitPresent(ForumsUIConstants.bizCard);
		Assert.assertTrue(driver.getSingleElement(ForumsUIConstants.bizCard).isVisible(), "the business card doesn't show up");
	}
	
}
