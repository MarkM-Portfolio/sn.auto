package com.ibm.conn.auto.util;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.conn.auto.util.browsers.FFAction;
import com.ibm.conn.auto.util.browsers.IEAction;

public abstract class BrowserActions {
	
	protected RCLocationExecutor driver;
	
	protected BrowserActions(RCLocationExecutor driver) {
		this.driver = driver;
	}
	
	public static BrowserActions getBrowserAction(RCLocationExecutor driver, BrowserType browser) {
		
		switch(browser) {
		case FIREFOX: return new FFAction(driver);
		case IE: return new IEAction(driver);
		default: return new FFAction(driver);
		}
	}
}
