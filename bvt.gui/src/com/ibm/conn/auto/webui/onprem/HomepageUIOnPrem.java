package com.ibm.conn.auto.webui.onprem;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.HomepageUI;

public class HomepageUIOnPrem extends HomepageUI {

	public HomepageUIOnPrem(RCLocationExecutor driver) {
		super(driver);
	}

	
	@Override
	protected void changeAccess() {
		clickLink(HomepageUIConstants.ChangeAccess);
	}

	@Override
	public void verifyMyPageLink() {
		Assert.assertTrue(fluentWaitPresent(HomepageUIConstants.MyPage));
	}
	
	@Override
	public void switchToHomepageTab(){
		//switch back to main window
		if(cfg.getUseNewUI()) {
			switchToNewTabByName("HCL Connections Home Page - Latest Updates");
		}
		else {
			switchToNewTabByName("HCL Connections Home Page - Updates");
		}
		
	}

	@Override
	public void verifyMeetingsWidget(){
		//do nothing as this is not supported in OP

	}

}
