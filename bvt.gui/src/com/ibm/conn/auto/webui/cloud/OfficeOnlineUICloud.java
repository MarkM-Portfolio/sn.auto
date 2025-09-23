package com.ibm.conn.auto.webui.cloud;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.OfficeOnlineUI;

public class OfficeOnlineUICloud extends OfficeOnlineUI {

	private static final String expectedSCPageTitle = "An error has occurred"; // "We are unable to process your request"

	public OfficeOnlineUICloud(RCLocationExecutor driver) {
		super(driver);
	}

	@Override
	public String getExpectedPageTitle() {
		return expectedSCPageTitle;
	}
}
