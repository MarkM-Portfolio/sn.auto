package com.ibm.atmn.waffle.core.selector;

import com.ibm.atmn.waffle.core.Executor;

/**
 * Interface for a selector to be located by JavaScript. 
 * This can be messy, see {@link SizzleSelector} for example. 
 * A {@link Selector} with strategy JavaScript will be automatically cast to use this interface on location, since the
 * {@link Executor} does not expect it as a parameter. A Selector really only needs to be declared as a JavaScriptSelector if
 * modifiers such as {@link #addToParent()} need to be used. Use of these is experimental though, it will cause problems under certain scenarios.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 * 
 */
public interface JavaScriptSelector extends Selector {

	Object[] getArguments();

	/**
	 * Navigate to the parent element of the element returned by the base query.
	 * 
	 * @return The new Selector
	 */
	Selector addToParent();

	/**
	 * Navigate upwards through all the parent elements of the element returned by the base query until one with the specified tag type is reached.
	 * 
	 * @param tagName
	 *            The type of the element to ascend to, such as 'table' for {@literal <table>}.
	 * @return The new Selector
	 */
	Selector addToParentByTagName(String tagName);

	/**
	 * Adds an element as context for the base query. Note that relative navigation such as toParent is applied after the contextual search, so the element ultimately returned as a
	 * result of applying the selector may not be a descendant of the context element.
	 * 
	 * This can not be used to apply context prior to location of the context element. It is not intended for use other than in
	 * {@link com.ibm.atmn.waffle.core.Element#getElements()}
	 * 
	 * @param An
	 *            object that will be evaluated to an element on the page to use as context for the query.
	 */
	void addContext(Object context);

}
