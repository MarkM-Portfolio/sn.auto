package com.ibm.conn.auto.webui.onprem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.NotificationCenterUI;

public class NotificationCenterOnPrem extends NotificationCenterUI {

	private static Logger log = LoggerFactory.getLogger(NotificationCenterUI.class);
	
	public NotificationCenterOnPrem(RCLocationExecutor driver) {
		super(driver);
	}
}
