package com.ibm.atmn.waffle.core;

import java.util.List;

/**
 * Extension to {@link com.ibm.atmn.waffle.core.Executor} that allows Selenium-RC style selectors to be provided directly to locator dependent methods
 * as well as instances of {@link com.ibm.atmn.waffle.core.selector.Selector}.
 * 
 * @see com.ibm.atmn.waffle.core.Executor
 * @author Ruairi Pidgeon/Ireland/IBM
 * 
 */
public interface RCLocationExecutor extends Executor {

	/**
	 * @see com.ibm.atmn.waffle.core.Executor#getElements(Selector)
	 */
	public List<Element> getElements(String selector);

	/**
	 * @see com.ibm.atmn.waffle.core.Executor#getSingleElement(Selector)
	 */
	public Element getSingleElement(String selector);

	/**
	 * @see com.ibm.atmn.waffle.core.Executor#getFirstElement(Selector)
	 */
	public Element getFirstElement(String selector);

	/**
	 * @see com.ibm.atmn.waffle.core.Executor#getVisibleElements(Selector)
	 */
	public List<Element> getVisibleElements(String selector);

	/**
	 * @see com.ibm.atmn.waffle.core.Executor#isElementPresent(Selector)
	 */
	public boolean isElementPresent(String selector);
}
