package com.ibm.conn.auto.util.webeditors.fvt.utils;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.WebElement;
import com.ibm.atmn.waffle.core.Element;

class CSSAttributeChecker extends AttributeChecker {

	// http://www.w3.org/TR/CSS21/propidx.html
	private static final List<String>
		// display
		displayOperable = 		Arrays.asList(new String[]{ "inline", "block" }),
		displayNotOperable =	Arrays.asList(new String[]{ "none" }),
		displayUnknown = 		Arrays.asList(new String[]{ "inherit", "list-item", "inline-block", "table", "inline-table", "table-row-group", "table-header-group", "table-footer-group", "table-row", "table-column-group", "table-column", "table-cell", "table-caption" }),
		// visibility
		visibleOperable =		Arrays.asList(new String[]{ "visible" }),
		visibleNotOperable =	Arrays.asList(new String[]{ "hidden" }),
		visibleUnknown =		Arrays.asList(new String[]{ "inherit", "collapse" });

	public boolean isEnabled(Element element) { // This will generally return true for everything but disabled input elements.
		// https://w3c.github.io/webdriver/webdriver-spec.html#get-element-attribute
		// If <name> is a boolean attribute; "true" (string) if the element has the attribute, otherwise null
		String attributeDisabled = element.getAttribute("disabled");
		if( attributeDisabled != null && attributeDisabled.equals("true") )
			return false;
		return true;
	}
	
	public boolean isVisible(Element element) {	
		WebElement webElement = (WebElement) element.getBackingObject();
		String webElementVisibility = webElement.getCssValue("visibility");

		if( visibleOperable.contains(webElementVisibility) )
			return true;
		else if( visibleNotOperable.contains(webElementVisibility)  )
			return false;
		else if( visibleUnknown.contains(webElementVisibility) ) {
			if(!element.isVisible())
				return false;
		}
		else
			throw new RuntimeException("Unknown CSS visibility value: '"+ webElementVisibility +"'");
		
		return true; // to avoid java warnings
	}
	
	public boolean isDisplayed(Element element) {
		WebElement webElement = (WebElement) element.getBackingObject();
		String webElementDisplay = webElement.getCssValue("display");

		if( displayOperable.contains(webElementDisplay) )
			return true;
		else if( displayNotOperable.contains(webElementDisplay)  )
			return false;
		else if( displayUnknown.contains(webElementDisplay) ) {
			if(!element.isDisplayed())
				return false;
		}
		else
			throw new RuntimeException("Unknown CSS display value: '"+ webElementDisplay +"'");
		
		return true; // to avoid java warnings
	}
}	
