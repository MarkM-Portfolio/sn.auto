package com.ibm.conn.auto.util.webeditors.fvt.utils;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;

public class DriverUtils {

	private static final int POLLING_INTERVAL_ON_WAIT_MS = 250;
	private static final boolean DEBUGGING = false;

	private static Logger log = LoggerFactory.getLogger(DriverUtils.class);

	private RCLocationExecutor driver;
	private AttributeChecker ac; 

	public DriverUtils(RCLocationExecutor driver) {
		Assert.assertNotNull(driver, "Supplied driver is null...");
		this.driver = driver;
		this.ac = new CSSAttributeChecker();
	}

	private static final int TIMEOUT = 3;
	
	// this is ugly, but for now it will do
	public Element click(final String selector) 										{ return performOperation(new DriverWrapper(driver), 			new ClickElement(), selector, TIMEOUT); }
	public Element click(final String selector, int timeout)							{ return performOperation(new DriverWrapper(driver), 			new ClickElement(), selector, timeout); }
	public Element click(Element containingElement, final String selector)				{ return performOperation(new ElementWrapper(containingElement), new ClickElement(), selector, TIMEOUT); }
	public Element click(Element containingElement, final String selector, int timeout)	{ return performOperation(new ElementWrapper(containingElement), new ClickElement(), selector, timeout); }
	
	public Element type(final String selector, CharSequence text)											{ return performOperation(new DriverWrapper(driver), 			new TypeInElement(text), selector, TIMEOUT); }
	public Element type(final String selector, CharSequence text, int timeout)								{ return performOperation(new DriverWrapper(driver), 			new TypeInElement(text), selector, timeout); }
	public Element type(Element containingElement, final String selector, CharSequence text)				{ return performOperation(new ElementWrapper(containingElement), new TypeInElement(text), selector, TIMEOUT); }
	public Element type(Element containingElement, final String selector, CharSequence text, int timeout)	{ return performOperation(new ElementWrapper(containingElement), new TypeInElement(text), selector, timeout); }
	
	private Element performOperation(final ElementContainerWrapper elementContainer, final BrowserAction performAction, final String selector, int timeout) {
		
		Assert.assertFalse(Strings.isNullOrEmpty(selector), "The selector can't be null or empty!");

		Element element = null;
		
		element = waitUntilElementIsAvailable(elementContainer, performAction, selector, timeout);
		
		waitUntilElementIsOperable(element, performAction, selector, timeout);
		

		performAction.performOn(element);
		
		return element;
	}

	public void waitUntilElementIsOperable(final Element element) { waitUntilElementIsOperable(element, null, null, TIMEOUT); }
	
	private void waitUntilElementIsOperable(final Element element, final BrowserAction performAction, final String elementSelector, final int timeout) {
		if( ac.isOperable(element) ) {
			if(DEBUGGING)
				log.info("Element is operable; we can carry out the intended operation on the element *now* - returning immediately!");
			return;
		}
		else {
			if(DEBUGGING)
				log.info("Element is not yet operable; running fluent wait operation...");

			Wait<Element> waitUntilOperable = new FluentWait<Element>(element)
								.withTimeout(timeout, TimeUnit.SECONDS)
								.pollingEvery(POLLING_INTERVAL_ON_WAIT_MS, TimeUnit.MILLISECONDS)
								.ignoring(ElementNotFoundException.class);
			
			try {
				waitUntilOperable.until(new Function<Element, Element>() {
					public Element apply(Element elem) {
						
						Element returnElem = null;
						
						if (ac.isOperable(elem))
							returnElem = elem;
	
						if(DEBUGGING) {
							if (performAction != null && elementSelector != null)
								log.warn( "WARNING: To perform '" + performAction.getClass().getSimpleName() + "', the element '" + elementSelector 
										+ "' should be enabled(" + ac.isEnabled(elem) + "), " + "displayed(" + ac.isDisplayed(elem) 
										+ ") and visible(" + ac.isVisible(elem) + ")." );
							else 
								log.warn( "WARNING: To be operable, the element should be enabled(" + ac.isEnabled(elem) + "), " 
										+ "displayed(" + ac.isDisplayed(elem) + ") and visible(" + ac.isVisible(elem) + ")." );
						}
						
						return returnElem;
					}
				});
			}
			catch (TimeoutException ex) {
				final String errorMessage;
				if(performAction != null && elementSelector != null)
					errorMessage = "It's not possible to perform '"+performAction.getClass().getSimpleName()+"' on the element found with '" + elementSelector + "'.";
				else
					errorMessage = "Timeout occurred while waiting for the element to become operable!";
				log.error("ERROR: " + errorMessage);
				throw new IllegalStateException(errorMessage, ex);
			}
		}
	}

	private Element waitUntilElementIsAvailable(final ElementContainerWrapper elementContainer, final BrowserAction performAction, final String selector, int timeout) {

		Element returnElement = null;
		
		List<Element> elementsFound = elementContainer.getElements(selector);
		if(elementsFound.size() == 1) {
			returnElement = elementsFound.get(0);
		}
		else {
			Wait<Object> waitForElement = new FluentWait<Object>(new Object())
								.withTimeout(timeout, TimeUnit.SECONDS)
								.pollingEvery(POLLING_INTERVAL_ON_WAIT_MS, TimeUnit.MILLISECONDS)
								.ignoring(ElementNotFoundException.class);
			
			try {
				returnElement = waitForElement.until(new Function<Object, Element>() {
					public Element apply(Object dummy) {
						
						List<Element> elementsFound = elementContainer.getElements(selector);
						
						if(1 < elementsFound.size())
							throw new UnsupportedOperationException("Too many elements found for '" + selector + "'. " + elementsFound.size() + " elements found. "
									+ "Won't '" + performAction.getClass().getSimpleName() + "' more than one element.");
						if(elementsFound.isEmpty())
							throw new NoSuchElementException("No elements found for '" + selector + "'.");
	
						return elementsFound.get(0);
					}
				});
			}
			catch (TimeoutException ex) {
				final String errorMessage = "Timeout has occurred while waiting for an '" + selector + "' element, after " + timeout + " seconds.";
				log.error("ERROR: " + errorMessage);
				throw new TimeoutException(errorMessage, ex);
			}
		}
		
		return returnElement;
	}
	
	
	public boolean waitUntilNumberOfWindowsEquals(final int numberOfWindows) {
		return waitUntilNumberOfWindowsEquals(numberOfWindows, TIMEOUT);
	}
	
	public boolean waitUntilNumberOfWindowsEquals(final int numberOfWindows, final int timeout){
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
											.withTimeout(timeout, TimeUnit.SECONDS)
											.pollingEvery(POLLING_INTERVAL_ON_WAIT_MS, TimeUnit.MILLISECONDS);
		
        boolean numberOfWindowsEqualsSpecifiedValue = wait.until(new Function<RCLocationExecutor, Boolean>() {
								        							public Boolean apply(RCLocationExecutor driver) {
								        								int currNumberOfWindows = driver.getWindowHandles().size();
								        								if(DEBUGGING) {
								        									log.info("I have " + currNumberOfWindows + " open window(s); I'm waiting for " + numberOfWindows + " open window(s)...");
								        								}
							        									return (currNumberOfWindows == numberOfWindows);
								        							}
						        						});
        return numberOfWindowsEqualsSpecifiedValue;
    };

	
	public static String xpathIndexerForCssClass(String cssClass) {
		return "contains(concat(\" \", normalize-space(@class), \" \"), \" "+cssClass+" \")";
	}

	public String getChildWindowHandle(String parentWindowHandle) {
		String[] windowHandles = driver.getWindowHandles().toArray(new String[driver.getWindowHandles().size()]);
		
		String childWindowHandle = null;
		for (int i = 0; i < windowHandles.length; ++i) {
			if (windowHandles[i].equals(parentWindowHandle)) {
				try {
					childWindowHandle = windowHandles[i+1];
				}
				catch (ArrayIndexOutOfBoundsException ex) {
					if(	0 < i && driver.getTestManager().getTestConfig().getBrowser().equals(BrowserType.IE) )
						childWindowHandle = windowHandles[i-1];
					else
						throw new RuntimeException("The parent window '" + parentWindowHandle
								+ "' was found, but there are no windows following it! Perhaps the child window was closed? List of window handles:'"
								+ Arrays.toString( windowHandles ) + "'.", ex);
				}
				break;
			}
		}

		Assert.assertNotNull(childWindowHandle, "The parent window '" + parentWindowHandle 
				+ "' was not found. Perhaps the parent window was closed? List of window handles:'"
				+ Arrays.toString( windowHandles ) + "'.");

		return childWindowHandle;
	}

	/**
	 * Waits for an alert for {@code timeout} seconds and accepts it if {@code acceptAlert} is true or dismisses it otherwise. 
	 * 
	 * @param acceptAlert true to accept, false to dismiss the alert
	 * @param timeout specifies how many seconds to wait before returning false   
	 * @return true if an alert box was processed, false if no alert box was found 
	 */
	public boolean handleAlertIfPresent(boolean acceptAlert, int timeout) {

		WebDriver wd = (WebDriver) driver.getBackingObject();
		
		WebDriverWait  wait = new WebDriverWait(wd, timeout);

		Alert alert = null;
		try {
			alert = wait.until(ExpectedConditions.alertIsPresent());
		}
		catch(TimeoutException ex) {
			log.info("INFO: an alert popup was not found; nothing to do");
		}
		
		if( alert != null ) {
			try {
				alert = wd.switchTo().alert();
			}
			catch(NoAlertPresentException ex) {
				throw new RuntimeException("An alert popup was detected, but it was dismissed before anything could be done!", ex);
			}
			
			if(acceptAlert)
				alert.accept();
			else
				alert.dismiss();
			
			return true;
		}
		else {
			return false;
		}
	}

	
	RCLocationExecutor wd() {
		return driver;
	}

}
