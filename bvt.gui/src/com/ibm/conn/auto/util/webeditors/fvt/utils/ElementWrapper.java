package com.ibm.conn.auto.util.webeditors.fvt.utils;

import java.util.List;
import com.ibm.atmn.waffle.core.Element;

class ElementWrapper implements ElementContainerWrapper {

	private Element element;
	public ElementWrapper(Element element) { super(); this.element = element; }

	@Override public List<Element> getElements(String selector) { return element.getElements(selector);}
}
