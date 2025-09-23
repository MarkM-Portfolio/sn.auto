package com.ibm.atmn.waffle.core.webdriver;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.selector.JavaScriptSelector;
import com.ibm.atmn.waffle.core.selector.Selector;
import com.ibm.atmn.waffle.core.selector.SizzleSelector;
import com.ibm.atmn.waffle.core.selector.Selector.Strategy;
import com.ibm.atmn.waffle.utils.Utils;

/**
 * Responsible for all element location using {@link WebDriver} or {@link WebElement}. Uses Waffle {@link Selector}s.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 * 
 */
public class WebDriverElementLocator {

	private static final Logger log = LoggerFactory.getLogger(WebDriverElementLocator.class);

	private Selector givenSelector;
	private WebDriverExecutor exec;
	private WebDriverElement context;

	WebDriverElementLocator(WebDriverExecutor executor, Selector selector) {

		this(executor, selector, null);
	}

	WebDriverElementLocator(WebDriverExecutor executor, Selector selector, WebDriverElement context) {

		this.givenSelector = selector;
		this.exec = executor;
		this.context = context;
	}

	private By getSelectBy(Selector selector) {

		switch (selector.getStrategy()) {
		case CSS:
			return By.cssSelector(selector.getQuery());
		case XPATH:
			return By.xpath(selector.getQuery());
		case CLASS:
			return By.className(selector.getQuery());
		case ID:
			return By.id(selector.getQuery());
		case NAME:
			return By.name(selector.getQuery());
		case LINK_EQUALS_TEXT:
			return By.linkText(selector.getQuery());
		case LINK_PARTIAL_TEXT:
			return By.partialLinkText(selector.getQuery());
		}
		log.error("Select By not appropriate for strategy: " + selector.getStrategy());
		throw new InvalidParameterException("Select By not appropriate for strategy: " + selector.getStrategy());
	}

	@SuppressWarnings("unchecked")
	private List<WebElement> locateWithJavaScript(Selector selector, long implicitWaitRemaining) {

		//log.debug("Attempting to locate selector: '" + selector + "' by JavaScript");
		long startTime = System.currentTimeMillis();

		List<WebElement> elements = null;

		JavaScriptSelector jsSelector;

		// Selector must be JavaScriptSelector for this
		if (!(selector instanceof JavaScriptSelector)) {
			//log.debug("Selector: '" + selector + "' not JavaScriptSelector. Attempting change to SizzleSelector...");
			if (selector.getStrategy() == Strategy.CSS) {
				jsSelector = new SizzleSelector(selector.getQuery());
			} else {
				//log.debug("Selector: '" + selector + "' not CSS, and therefore not compatible with SizzleSelector.");
				return null;
			}
		} else {
			//log.debug("Upgrading Selector: '" + selector + "' to JavaScriptSelector.");
			jsSelector = (JavaScriptSelector) selector;
		}

		assert jsSelector.getStrategy() == Strategy.JAVASCRIPT : "Strategy must be JavaScript to continue with location by script execution.";

		if (hasContextElement()) {

			jsSelector.addContext(context);
		}

		for (int i = 0; i == 0 || ((elements == null || elements.isEmpty()) && (implicitWaitRemaining - (System.currentTimeMillis() - startTime)) > 0); i++) {

			Utils.milliSleep(200 * i);// breath - logger can stackoverflow on a long timeout.

			log.trace("Iteration " + i + " of JavaSCript execution loop to locate elements with script: " + jsSelector);

			jsSelector.beforeLocation(exec);
			Object result = exec.executeScript("return " + jsSelector.getQuery(), jsSelector.getArguments());
			jsSelector.afterLocation(exec);

			if (result instanceof List<?>) {
				try {
					elements = (List<WebElement>) result; // Unchecked Warning suppressed.
					for (@SuppressWarnings("unused")
					WebElement el : elements)
						; // do-nothing loop to trigger ClassCastException here if it's going to happen.
				} catch (ClassCastException e) {
					log.warn("ClassCastException caught attempting to cast script return to List<WebElement>: " + e.getMessage());
					elements = null;
				}
			}
		}

		return elements; // may be null.
	}

	private List<WebElement> locateWithDriver(Selector selector) {

		//log.debug("Attempting to locate selector: '" + selector + "' by native WebDriver location.");
		List<WebElement> elements = null;

		By selectBy = getSelectBy(selector);

		selector.beforeLocation(exec);
		if (hasContextElement()) {
			elements = context.getWebElement().findElements(selectBy);
		} else {
			elements = exec.wd().findElements(selectBy);
		}
		selector.afterLocation(exec);

		return elements; // may be null
	}

	List<WebElement> locateAll() {

		//log.debug("Attempting to locate all with selector: " + givenSelector);
		List<WebElement> elements = null;

		if (givenSelector.getStrategy() == Strategy.JAVASCRIPT || exec.getTestConfiguration().forceScriptLocation()) {

			elements = locateWithJavaScript(givenSelector, exec.getImplicitWaits());
		} else {

			elements = locateWithDriver(givenSelector);

			// Element not found after implicit_wait: try once with Sizzle as a fall-back (if configured to do so).
			if ((elements == null || elements.isEmpty()) && exec.getTestConfiguration().useLocationFallback()) {
				elements = locateWithJavaScript(givenSelector, 0);
			}
		}

		// If nothing found or location failed/returned something unexpected, return empty list.
		if (elements == null || !(elements instanceof List<?>)) {
			elements = new ArrayList<WebElement>();
		}

		return elements; // must not be null, empty list means no elements found.
	}

	private boolean hasContextElement() {

		return this.context != null;
	}

	// TODO: Will this work?-NO. Is this helpful? Does just selecting an element not do this effectively anyway?
	// WebElement waitForAppearance(WebDriverExecutor executor) {
	//
	// WebElement element = (new WebDriverWait(executor.wd(), 30)).until(new ExpectedCondition<WebElement>() {
	//
	// @Override
	// public WebElement apply(WebDriver wd) {
	//
	// return wd.findElement(selectBy);
	// }
	//
	// });
	// return element;
	// }

}
