package com.ibm.conn.auto.util.webeditors.fvt.utils;

import com.ibm.atmn.waffle.core.Element;

abstract class AttributeChecker {

	/**
	 * Checks parameter {@code element} to find out if it is Enabled, Displayed & Visible. 
	 * It is used to find out if {@code element} is operable, i.e. is it clickable? is it typable?
	 * 
	 * @param element the element to be checked
	 * @return a boolean indicating if the element is Enabled, Displayed & Visible
	 */
	public boolean isOperable(Element element) {
		return isDisplayed(element) && isVisible(element) && isEnabled(element);
	}
	
	public abstract boolean isEnabled(Element element);
	public abstract boolean isVisible(Element element);
	public abstract boolean isDisplayed(Element element);
}


