package com.ibm.atmn.waffle.core.webdriver;

import java.awt.Dimension;
import java.awt.Point;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.ExecutorActionListener;
import com.ibm.atmn.waffle.core.ExecutorActionListener.ElementEvent;
import com.ibm.atmn.waffle.core.selector.RCSelector;
import com.ibm.atmn.waffle.core.selector.Selector;
import com.ibm.atmn.waffle.core.selector.Selector.Strategy;
import com.ibm.atmn.waffle.core.selector.SizzleSelector;
import com.ibm.atmn.waffle.utils.Utils;

/**
 * This is primarily a wrapper on Seleniums's {@link WebElement}, but it adds extra functionality, such as an ability to recover from {@link StaleElementReferenceException}s in
 * some scenarios. It also makes the calls that allow for the {@link ExecutorActionListener} to be possible. Adding an {@link ExecutorActionListener} to the Test or Executor (which
 * acts as a Test Method listener) is the means by which any non-core functionality should be added to element events. The primary aim of this class is to provide cross-browser
 * compatible automation compatibility where Selenium may fail to do so, due to variations between the browser drivers and native vs synthetic events.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 * 
 */
public class WebDriverElement implements Element {

	private static final Logger log = LoggerFactory.getLogger(WebDriverElement.class);

	private List<WebElement> locationSet;
	private WebElement element;
	private WebDriverElement context;
	private WebDriverExecutor exec;
	Selector selector;

	WebDriverElement(WebDriverExecutor executor, WebElement element) {
		this(executor, element, null, null);
	}

	WebDriverElement(WebDriverExecutor executor, WebElement element, List<WebElement> locationSet, Selector selector) {
		this(executor, element, locationSet, selector, null);
	}

	WebDriverElement(WebDriverExecutor executor, WebElement element, List<WebElement> locationSet, Selector selector, WebDriverElement context) {

		this.exec = executor;
		this.element = element;
		if (locationSet != null) {
			this.locationSet = locationSet;
		} else {
			this.locationSet = Arrays.asList(element);
		}
		this.selector = selector;
		this.context = context;

		if (this.exec == null || this.element == null || this.locationSet == null) {
			log.error("WebDriverElement required parameters are null.");
			throw new InvalidParameterException("WebDriverElement required parameters are null.");
		}
	}
    
	@Override
	public WebDriverExecutor getWebDriverExecutor()
	{
		return exec;
	}
	
	@Override
	public Object getBackingObject() {

		return element;
	}

	@Override
	public WebElement getWebElement() {

		return element;
	}

	@Override
	public String getTagName() {

		//log.debug("Getting tag name of element: " + selector);
		eventNotifyBefore(ElementEvent.GET_ELEMENT_ATTRIBUTE);

		String tagName;
		try {
			tagName = element.getTagName();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				return getTagName();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.GET_ELEMENT_ATTRIBUTE);
		log.trace("Returning tag name '" + tagName + "' from element:" + selector);
		return tagName;
	}

	@Override
	public String getText() {

		//log.debug("Getting inner text of element: " + selector);
		eventNotifyBefore(ElementEvent.GET_INNER_TEXT);

		String text;
		try {
			text = element.getText();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				return getText();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.GET_INNER_TEXT);
		log.trace("Returning text '" + text + "' from element:" + selector);
		return text;
	}

	@Override
	public String getAttribute(String attributeName) {

		//log.debug("Getting attribute '" + attributeName + "' of element: " + selector);
		eventNotifyBefore(ElementEvent.GET_ELEMENT_ATTRIBUTE);

		String value;
		try {
			value = element.getAttribute(attributeName);
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				return getAttribute(attributeName);
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.GET_ELEMENT_ATTRIBUTE);
		log.trace("Returning value '" + value + "' for attribute '" + attributeName + "' of element: " + selector);
		return value;
	}

	@Override
	public Point getLocation() {

		//log.debug("Getting Location of element: " + selector);
		eventNotifyBefore(ElementEvent.GET_ELEMENT_LOCATION);

		Point point;
		try {
			point = new Point(element.getLocation().getX(), element.getLocation().getY());
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				return getLocation();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.GET_ELEMENT_LOCATION);
		log.trace("Returning Location [x,y]'" + point.getX() + "," + point.getY() + "' for element:" + selector);
		return point;
	}

	@Override
	public Dimension getSize() {

		//log.debug("Getting size of element: " + selector);
		eventNotifyBefore(ElementEvent.GET_ELEMENT_SIZE);

		Dimension dimension;
		try {
			dimension = new Dimension(element.getSize().getWidth(), element.getSize().getHeight());
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				return getSize();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.GET_ELEMENT_SIZE);
		log.trace("Returning Size [width,height]: '" + dimension.getWidth() + "," + dimension.getHeight() + "' for element:" + selector);
		return dimension;
	}

	@Override
	public void click() {

		//log.debug("Clicking element: " + selector);
		eventNotifyBefore(ElementEvent.CLICK);

		try {
			element.click();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				click();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.CLICK);
	}

	@Override
	public void hover() {

		//log.debug("Hovering on element: " + selector);
		eventNotifyBefore(ElementEvent.HOVER);

		// ((JavascriptExecutor) exec.wd()).executeScript("arguments[0].onmouseover()", element);
		//		
		// String script =
		// "" +
		// " function eventFire(element, eventName)" +
		// " {" +
		// "  if (element.fireEvent)" +
		// "  { element.fireEvent('on' + eventName); }" +
		// "  else" +
		// "  {" +
		// "    var eventObject = document.createEvent('Events');" +
		// // parameters: type, bubbles, cancelable
		// "    eventObject.initEvent(eventName, true, false);" +
		// "    element.dispatchEvent(eventObject);" +
		// "  }" +
		// " };";
		//
		// String eventCall = String.format( "eventFire(%s, '%s');", elementRef, "mouseover" );
		// String exec = script + eventCall;
		Actions acts = new Actions(exec.wd());
		try {
			acts.moveToElement(element).perform();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				hover();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.HOVER);
	}
	
	@Override
	public void typeWithDelay(CharSequence text) {
		String plain = "";
		String myText = text.toString();
		char[] chars = myText.toCharArray();
		for (char c : chars){
			plain = plain + c;
			log.info("Typing text '" + plain + "' into element: " + selector);
			eventNotifyBefore(ElementEvent.TYPE);

			try {
				element.sendKeys(String.valueOf(c));
				Thread.sleep(exec.getTestConfiguration().getTypingDelay());
			} catch (StaleElementReferenceException se) {
				if (recoverFromSERE(se)) {
					typeWithDelay(String.valueOf(c));
				} else throw se;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		eventNotifyAfter(ElementEvent.TYPE);
	}
	
	@Override
	public void type(CharSequence... text) {

		String plain = "";
		for (CharSequence chars : text)
			plain = plain + chars.toString();
		//log.debug("Typing text '" + plain + "' into element: " + selector);
		eventNotifyBefore(ElementEvent.TYPE);

		try {
			element.sendKeys(text);
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				type(text);
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.TYPE);
	}

	@Override
	public void typeFilePath(CharSequence path) {

		//log.debug("Typing file path in (file input) element: " + selector);
		eventNotifyBefore(ElementEvent.TYPE_FILE_PATH);

		type(path);

		eventNotifyAfter(ElementEvent.TYPE_FILE_PATH);
	}

	@Override
	public void clear() {

		//log.debug("Clearing text of element: " + selector);
		eventNotifyBefore(ElementEvent.CLEAR);

		try {
			element.clear();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				clear();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.CLEAR);
	}
	
	@Override
	public void clearWithSendKeys() {
		eventNotifyBefore(ElementEvent.CLEAR);
		
		try {
			element.sendKeys(Keys.CONTROL + "a");
			element.sendKeys(Keys.DELETE);
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				clear();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.CLEAR);
	}

	@Override
	public boolean isVisible() {

		//log.debug("Testing if element " + selector + " is visible.");
		eventNotifyBefore(ElementEvent.IS_VISIBLE_TEST);

		boolean isVisible = false;
		try {
			isVisible = element.isDisplayed();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				return isVisible();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.IS_VISIBLE_TEST);
		log.trace("Returning " + isVisible + " for isVisible test on element: " + selector);
		return isVisible;
	}

	@Override
	public void doubleClick() {

		//log.debug("Double-clicking on element: " + selector);
		eventNotifyBefore(ElementEvent.CLICK);

		//TODO: doesn't seem to work in Selenium 3 with geckodriver
		Actions acts = new Actions(exec.wd());
		try {
			acts.moveToElement(element).doubleClick().build().perform();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				doubleClick();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.CLICK);
	}

	@Override
	public void leftMouseDown() {

		//log.debug("Clicking and holding left-mouse-button down on element: " + selector);
		Actions acts = new Actions(exec.wd());
		try {
			acts.clickAndHold(element).perform();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				leftMouseDown();
			} else throw se;
		}
	}

	@Override
	public void leftMouseUp() {

		//log.debug("Releasing left-mouse-button on element: " + selector);
		Actions acts = new Actions(exec.wd());
		try {
			acts.release(element).perform();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				leftMouseUp();
			} else throw se;
		}
	}

	@Override
	public void rightMouseClick() {

		//log.debug("Right-clicking on element: " + selector);
		eventNotifyBefore(ElementEvent.CLICK);

		Actions acts = new Actions(exec.wd());
		try {
			acts.contextClick(element).perform();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				rightMouseClick();
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.CLICK);
	}

	@Override
	public void clickAt(int xOffset, int yOffset) {

		//log.debug("Clicking at offset [x,y] '" + xOffset + "," + yOffset + "' of element: " + selector);
		eventNotifyBefore(ElementEvent.CLICK);

		Actions acts = new Actions(exec.wd());
		try {
			acts.moveToElement(element, xOffset, yOffset).click().perform();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				clickAt(xOffset, yOffset);
			} else throw se;
		}

		eventNotifyAfter(ElementEvent.CLICK);
	}

	@Override
	public List<Element> getElements(Selector selector) {

		eventNotifyBefore(ElementEvent.LOCATION);
		WebDriverElementLocator locator = new WebDriverElementLocator(exec, selector, this);
		List<WebElement> wdElements = locator.locateAll();

		eventNotifyAfter(ElementEvent.LOCATION);
		return WebDriverElement.wrapWebElements(exec, wdElements, selector, this);
	}

	@Override
	public Element getSingleElement(Selector selector) {

		List<Element> elements = getElements(selector);
		if (elements.size() > 1) {
			throw new AssertionError("Too many found for: " + selector + " in context: " + this.selector + "." + elements.size()
					+ " elements found. getSingleElement expects only 1 matching element.");
		} else if (elements.isEmpty()) {
			throw new AssertionError("No elements found for: " + selector + " in context: " + this.selector + ".");
		}
		return elements.get(0);
	}

	@Override
	public boolean isElementPresent(Selector selector) {

		int elementCount = getElements(selector).size();
		if (elementCount > 1) {
			throw new AssertionError("Too many found for: " + selector + " in context: " + this.selector + "." + elementCount
					+ " elements found. isElementPresent expects only 1 matching element.");
		}
		return elementCount == 1;
	}

	@Override
	public boolean isSelected() {

		//log.debug("Testing if selected on element: " + selector);
		boolean isSelected = false;
		try {
			isSelected = element.isSelected();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				return isSelected();
			} else throw se;
		}
		log.trace("Returning '" + isSelected + "' for isSelected test on element: " + selector);
		return isSelected;
	}

	@Override
	public SelectDropdown useAsDropdown() {

		return new WebDriverSelectDropdown();
	}

	private class WebDriverSelectDropdown implements SelectDropdown {

		/*
		 * No attempt to recover from StaleElementReferenceExceptions here. It is too unpredictable as they could bubble up from subelements, which most of these methods work with.
		 */

		private Select select;

		public WebDriverSelectDropdown() {

			select = new Select(element);
		}

		@Override
		public void deselectAll() {

			select.deselectAll();
		}

		@Override
		public void deselectByIndex(int index) {

			select.deselectByIndex(index);
		}

		@Override
		public void deselectByValue(String value) {

			select.deselectByValue(value);
		}

		@Override
		public void deselectByVisibleText(String text) {

			select.deselectByVisibleText(text);
		}

		@Override
		public List<Element> getAllSelectedOptions() {

			List<WebElement> wdElements = select.getAllSelectedOptions();
			return wrapWebElements(exec, wdElements, null, null);
		}

		@Override
		public List<Element> getOptions() {

			List<WebElement> wdElements = select.getOptions();
			return wrapWebElements(exec, wdElements, null, null);
		}

		@Override
		public boolean isMultiple() {

			return select.isMultiple();
		}

		@Override
		public void selectOptionByValue(String value) {

			select.selectByValue(value);
		}

		@Override
		public void selectOptionByVisibleText(String text) {

			select.selectByVisibleText(text);
		}

		@Override
		public void selectByIndex(int index) {

			select.selectByIndex(index);
		}
	}

	@Override
	public boolean isTextPresent(String text) {

		long startTime = System.currentTimeMillis();
		boolean present = false;
		for (int i = 0; i == 0 || (!present && System.currentTimeMillis() < (startTime + exec.getTestConfiguration().getImplicitWait())); i++) {

			Utils.milliSleep(100 * i); // pause
			present = getText().toLowerCase().contains(text.toLowerCase());
		}

		return present;
	}

	static List<Element> wrapWebElements(WebDriverExecutor exec, List<WebElement> wdElements, Selector selector, WebDriverElement context) {

		List<Element> elements = new ArrayList<Element>();
		for (WebElement wdElement : wdElements) {
			elements.add(new WebDriverElement(exec, wdElement, wdElements, selector, context));
		}

		return elements;
	}

	private boolean recoverFromSERE(StaleElementReferenceException se) {

		if (isRelocatable()) {
			return this.relocate();
		} else {
			log.error("Element not relocatable. Rethrowing StaleElementRefereceException.");
			return false;
		}
	}

	private boolean relocate() {

		if (!isRelocatable()) {
			log.error("Attempt to relocate Element that is not relocatable.");
			throw new RuntimeException("Attempt to relocate Element that is not relocatable.");
		}

		int setSize = locationSet.size();
		int index = locationSet.indexOf(element);
		assert setSize > 0 && index >= 0 : "Something's gone very wrong.";

		List<Element> relocated;
		if (hasContextElement()) {
			relocated = context.getElements(selector);
		} else {
			relocated = exec.getElements(selector);
		}
		if (setSize != relocated.size()) {
			log.error("Element relocation failure. Result set size has changed, index can no longer be reasonably expected to be valid.");
			return false;
		} else {
			this.element = (WebElement) relocated.get(index).getBackingObject();
			log
					.warn("Element has been relocated. Relocation makes assumptions that may not always hold true, and therefore the resulting element may not correspond to the original one in every case.");
			return true;
		}
	}

	private boolean hasContextElement() {

		return context != null;
	}

	private boolean isRelocatable() {

		return selector != null;
	}

	private void eventNotifyAfter(ElementEvent event) {

		for (ExecutorActionListener listener : exec.getAllActionListeners()) {
			listener.afterElementEvent(exec, this, event);
		}
	}

	private void eventNotifyBefore(ElementEvent event) {

		for (ExecutorActionListener listener : exec.getAllActionListeners()) {
			listener.beforeElementEvent(exec, this, event);
		}
	}
	
	@Override
	public boolean isDisplayed() {
		eventNotifyBefore(ElementEvent.IS_DISPLAYED_TEST);
		
		boolean isDisplayed = false;
		try {
			isDisplayed = element.isDisplayed();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				return isDisplayed();
			} else throw se;
		}
		log.trace("Returning '" + isDisplayed + "' for isDisplayed test on element: " + selector);
		eventNotifyAfter(ElementEvent.IS_DISPLAYED_TEST);
		return isDisplayed;
	}

	@Override
	public boolean isEnabled() {
		eventNotifyBefore(ElementEvent.IS_ENABLED_TEST);
		
		boolean isEnabled = false;
		try {
			isEnabled = element.isEnabled();
		} catch (StaleElementReferenceException se) {
			if (recoverFromSERE(se)) {
				return isEnabled();
			} else throw se;
		}
		log.trace("Returning '" + isEnabled + "' for isEnabled test on element: " + selector);
		eventNotifyAfter(ElementEvent.IS_ENABLED_TEST);
		return isEnabled;
	}

	@Override
	public boolean isTextNotPresent(String text) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void turnOnImplicitWaits() {
		
	}
	
	protected void turnOffImplicitWaits() {
		
	}

	@Override
	public List<Element> getElements(String selector) {

		return getElements(parseSelectorString(selector));
	}

	@Override
	public Element getSingleElement(String selector) {

		return getSingleElement(parseSelectorString(selector));
	}

	private static final String[] SIZZLE_SELECTOR_DEFINITIONS = { ":contains(", ":containsEscaped(", ":not(", ":nth(", ":eq(", ":lt(", ":gt(", ":first", ":last", ":even", ":odd" };

	private boolean sizzleDefinitionsPresent(Selector selector) {

		boolean present = false;
		for (String def : SIZZLE_SELECTOR_DEFINITIONS) {
			if (selector.getQuery().contains(def))
				present = true;
		}
		return present;
	}

	private Selector parseSelectorString(String selector) {

		Selector rcSelector = new RCSelector(selector);
		if (rcSelector.getStrategy() == Strategy.CSS && sizzleDefinitionsPresent(rcSelector)) {
			return new SizzleSelector(rcSelector.getQuery());
		} else {
			return rcSelector;
		}
	}

   @Override
   public boolean isElementPresent(String selector) {

      return isElementPresent(parseSelectorString(selector));
   }
	
}
