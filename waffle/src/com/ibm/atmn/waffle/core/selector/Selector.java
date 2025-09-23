package com.ibm.atmn.waffle.core.selector;

import com.ibm.atmn.waffle.core.Executor;

public interface Selector {

	public enum Strategy {

		JAVASCRIPT, CSS, XPATH, ID, CLASS, NAME, LINK_PARTIAL_TEXT, LINK_EQUALS_TEXT;
	}

	/**
	 * 
	 * @return The location strategy of this Selector.
	 */
	public abstract Strategy getStrategy();

	/**
	 * The selector query will vary depending on the strategy. It is the responsibility of the locator to know the correct way to handle a query.
	 * 
	 * @return The query for location of the base element.
	 */
	public abstract String getQuery();

	public abstract Selector setStrategy(Strategy method);

	public abstract Selector setQuery(String query);

	/**
	 * Should be called by a locator immediately after the selector has been set and before the Selector query is executed to locate an element. The string returned from this
	 * should be used as the query.
	 * 
	 * @param exec
	 */
	public abstract String beforeLocation(Executor exec);

	/**
	 * Should be called by a locator immediately after the Selector query is executed to locate an element, regardless of result.
	 * 
	 * @param exec
	 */
	public abstract void afterLocation(Executor exec);

}
