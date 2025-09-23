package com.ibm.conn.auto.util.webeditors.fvt.utils;

import java.util.List;
import com.ibm.atmn.waffle.core.Element;

/**
 * The reason of being for {@code ElementContainerWrapper} is quite simply the fact that {@code Element} and {@code RCLocationExecutor} don't share a common 
 * interface with a {@code getElements} method.
 * @author David Coelho
 */
interface ElementContainerWrapper {
	List<Element> getElements(String selector);
}
