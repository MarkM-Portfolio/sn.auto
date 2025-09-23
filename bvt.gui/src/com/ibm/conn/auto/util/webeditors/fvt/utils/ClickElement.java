package com.ibm.conn.auto.util.webeditors.fvt.utils;

import com.ibm.atmn.waffle.core.Element;

class ClickElement implements BrowserAction { 
	@Override public void performOn(Element element) { 
		element.click(); 
	}
} 
