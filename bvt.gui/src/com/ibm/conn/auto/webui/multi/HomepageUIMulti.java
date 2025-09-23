package com.ibm.conn.auto.webui.multi;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.HomepageUI;

public class HomepageUIMulti extends HomepageUI {

	public HomepageUIMulti(RCLocationExecutor driver) {
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
		switchToNewTabByName("IBM Connections Home Page - Updates");
	}

	@Override
	public void verifyMeetingsWidget() {
		//do nothing as this is not supported
		
	}

}
