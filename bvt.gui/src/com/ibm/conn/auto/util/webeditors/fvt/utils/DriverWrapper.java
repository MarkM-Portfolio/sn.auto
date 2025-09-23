package com.ibm.conn.auto.util.webeditors.fvt.utils;

import java.util.List;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;

class DriverWrapper implements ElementContainerWrapper {
	
	private RCLocationExecutor driver;
	
	public DriverWrapper(RCLocationExecutor driver) { super(); this.driver = driver; }
	@Override public List<Element> getElements(String selector) { return driver.getElements(selector);}
}
