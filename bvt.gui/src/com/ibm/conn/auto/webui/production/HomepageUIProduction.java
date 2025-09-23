package com.ibm.conn.auto.webui.production;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.HomepageUI;

public class HomepageUIProduction extends HomepageUI {

	public HomepageUIProduction(RCLocationExecutor driver) {
		super(driver);
	}
	private static Logger log = LoggerFactory.getLogger(HomepageUIProduction.class);

	@Override
	protected void changeAccess() {

	}

	@Override
	public void verifyMyPageLink() {
		//do nothing as this is not supported in SC
		
	}
	
	@Override
	public void switchToHomepageTab(){
		//switch back to main window
		switchToNewTabByName("IBM Connections Home Page - Updates");
	}

	@Override
	public void verifyMeetingsWidget() {
		log.info("INFO: Validate Meetings Widget in right panel");
		fluentWaitPresent(HomepageUIConstants.meetingsWidget);
		switchToFrame(HomepageUIConstants.meetingsWidgetIframe, HomepageUIConstants.joinMeetingBtn);
		switchToTopFrame();		
	}
}
