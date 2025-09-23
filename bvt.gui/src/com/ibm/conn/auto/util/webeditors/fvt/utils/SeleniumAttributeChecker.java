package com.ibm.conn.auto.util.webeditors.fvt.utils;

import com.ibm.atmn.waffle.core.Element;

class SeleniumAttributeChecker extends AttributeChecker {

	public boolean isEnabled(Element element) {	return element.isEnabled();	}
	public boolean isVisible(Element element) {	return element.isVisible();	}
	public boolean isDisplayed(Element element) {	return element.isDisplayed();	}
}	
