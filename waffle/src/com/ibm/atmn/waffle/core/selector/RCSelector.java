package com.ibm.atmn.waffle.core.selector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Executor;

/**
 * For handling selectors (appobjects) written for Selenium RC (e.g. css=#myelementid). Be warned that RC selectors may be converted to {@link SizzleSelector} as RC used sizzle for
 * all location and some non-standard selectors can not be handled otherwise.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 * 
 */
public class RCSelector implements Selector {

	private static final Logger log = LoggerFactory.getLogger(RCSelector.class);

	private String rawSelector;
	private Strategy strategy;
	private String query;

	public RCSelector(String selector) {

		this.rawSelector = selector;
		parseSelector(this.rawSelector);
	}

	@Override
	public Strategy getStrategy() {

		return this.strategy;
	}

	@Override
	public String getQuery() {

		return this.query;
	}

	@Override
	public Selector setStrategy(Strategy strategy) {

		this.strategy = strategy;
		return this;
	}

	@Override
	public Selector setQuery(String query) {

		this.query = query;
		return this;
	}

	private void parseSelector(String rawSelector) {

		if (rawSelector.startsWith("xpath=") || rawSelector.startsWith("//") || rawSelector.startsWith("./")) {
			setStrategy(Strategy.XPATH);
			setQuery(rawSelector.replaceFirst("^\\s*xpath=\\s*", ""));
		} else if (rawSelector.startsWith("css=")) {
			setStrategy(Strategy.CSS);
			setQuery(rawSelector.replaceFirst("^\\s*css=\\s*", ""));
		} else if (rawSelector.startsWith("id=")) {
			setStrategy(Strategy.ID);
			setQuery(rawSelector.replaceFirst("^\\s*id=\\s*", ""));
		} else if (rawSelector.startsWith("link=")) {
			setStrategy(Strategy.LINK_EQUALS_TEXT);
			setQuery(rawSelector.replaceFirst("^\\s*link=\\s*", ""));
		} else if (rawSelector.startsWith("linkpartial=")) {
			setStrategy(Strategy.LINK_PARTIAL_TEXT);
			setQuery(rawSelector.replaceFirst("^\\s*linkpartial=\\s*", ""));
		} else if (rawSelector.startsWith("name=")) {
			setStrategy(Strategy.NAME);
			setQuery(rawSelector.replaceFirst("^\\s*name=\\s*", ""));
		} else if (rawSelector.startsWith("class=")) {
			setStrategy(Strategy.CLASS);
			setQuery(rawSelector.replaceFirst("^\\s*class=\\s*", ""));
		} else {
			log.error("Selector strategy could not be identified by RCSelector: " + rawSelector);
			throw new RuntimeException("Selector strategy could not be identified by RCSelector: " + rawSelector);
		}
		//log.debug("Selector: '" + rawSelector + "' parsed as : " + this.toString());
	}

	public String toString() {

		return "{(" + getStrategy() + ")," + getQuery() + "}";
	}

	@Override
	public void afterLocation(Executor exec) {
		// Nothing to do here
	}

	@Override
	public String beforeLocation(Executor exec) {

		return getQuery();// Nothing to do here
	}

}
