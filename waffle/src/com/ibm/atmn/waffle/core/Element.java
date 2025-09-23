package com.ibm.atmn.waffle.core;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.ibm.atmn.waffle.core.selector.Selector;
import com.ibm.atmn.waffle.core.webdriver.WebDriverExecutor;

/**
 * Instances represent a particular HTML element on the current page when a selector was used to locate it. Do not attempt to reuse an instance of this object after a page has
 * changed (i.e after refresh, redirect, or following a link to new resource. JavaScript related page modifications do not count).
 * <p/>
 * Operations to do with a particular element will be performed through this interface, although it may be possible to influence other elements on the page due to resulting
 * JavaScript events. Results may vary depending on how the underlying automation tool simulates user interaction in the current browser. The most authentic interaction available
 * will be used in the implementation.
 * <p/>
 * WebDriver: uses {@link org.openqa.selenium.WebElement}
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 */
public interface Element {

	/**
	 * 
	 * @return The primary test tool instance that represents this element if applicable.
	 * <p/>
	 * WebDriver: returns {@link org.openqa.selenium.WebElement}
	 */
	Object getBackingObject();

	// Mouse
	/**
	 * Click this element.
	 * <p/>
	 * 
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#click()}
	 */
	void click();

	//TODO: Javadocs
	void clickAt(int x, int y);

	void doubleClick();

	void hover();

	void leftMouseDown();

	void leftMouseUp();

	void rightMouseClick();
     
	 WebDriverExecutor getWebDriverExecutor();
	 
	 WebElement getWebElement();
	/**
	 * Will attempt to find and use the submit button for the form. Do not attempt to use this if a form has multiple submit options or does not have a visible submit button of
	 * type <input type="submit" value="Sample"> ({@literal <input type="submit">} or form is not in standard format. This will automatically wait for the page to change after the
	 * submit. If form is submitted with AJAX page waiting may not be reliable.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#submit()}
	 */
	// void submit(); //TODO: is this too specific to web driver? Note: Does not seem to work cross-browser @ selenium 2.17

	// Keyboard
	/**
	 * Valid only for use on an {@literal <input>} or {@literal <textarea>} element. Will attempt to enter the text provided in to the input field, and fire any related javascript
	 * events.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#sendKeys(CharSequence...)}
	 * 
	 * @param text
	 */
	void type(CharSequence... text);
	
	/**
	 * Valid only for use on an {@literal <input>} or {@literal <textarea>} element. Will attempt to enter the text provided in to the input field, and fire any related javascript
	 * events. Pauses for a fixed amount of time between entering characters
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#sendKeys(CharSequence...)}
	 * 
	 * @param text
	 */
	void typeWithDelay(CharSequence text);

	/**
	 * Valid only for use on a file upload element (<input type="file">). Will attempt to enter the path provided in to the input field. The strategy used for this may vary wildly
	 * depending on the tool and the browser.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#sendKeys(CharSequence...)}
	 * 
	 * @param path
	 */
	void typeFilePath(CharSequence path);

	/**
	 * Valid only for use on an {@literal <input>} or {@literal <textarea>} elements. Will attempt to clear the current text and set the value to "".
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#clear()}
	 * 
	 */
	void clear();

	// Inspection
	/**
	 * Gets the type (tag name) of this element. If the element has a 'type' attribute, this will not be returned.
	 * <p/>
	 * Example: Will return <code>"input"</code> for the element <code>&lt;input type="button" /&gt;</code>.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#getTagName()}
	 * 
	 * @return The type (TagName) of this element.
	 */
	String getTagName();

	/**
	 * See WebDriver JavaDoc for behaviour. Other tools will attempt to match this.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#getAttribute(String)}
	 * 
	 * @param attributeName
	 *            The name of the attribute
	 * @return The value of the attribute or null if attribute not found.
	 */
	String getAttribute(String attributeName);

	/**
	 * Determine whether or not this element is selected or not. Valid only for input elements such as checkboxes, options in a select and radio buttons.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#isSelected()}
	 * 
	 * @return True if the element is currently selected or checked, false otherwise.
	 */
	boolean isSelected();

	/**
	 * Get the innerText of this element (e.g. {@literal <h1>innerText</h1>} returns "innerText"), including sub-elements, without any leading or trailing whitespace.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#getText()}
	 * 
	 * @return The innerText of this element.
	 */
	String getText();

	/**
	 * Using the body to check if text is present in the UI.
	 * @param text
	 * @return
	 */
	boolean isTextPresent(String text);
	
	/**
	 * Using the body to check if text is not present in the UI.
	 * @param text
	 * @return
	 */
	boolean isTextNotPresent(String text);

	/**
	 * Checks whether the element is displayed or hidden (by CSS property) This may not be very reliable cross-browser.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#isDisplayed()}
	 * 
	 * @return Whether or not the element is visible
	 */
	boolean isVisible();

	/**
	 * Calculates the coordinates of the top left-hand corner of the element relative to the top left hand corner of the page.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#getLocation()}
	 * 
	 * @return location AWT Point object with x,y coordinates of the element on the screen
	 */
	Point getLocation();

	/**
	 * Get this element's size
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#getSize()}
	 * 
	 * @return size AWT Dimension object with width and height values of the element
	 */
	Dimension getSize();

	// Relative context navigation
	// These are not to be offered in an RC selector friendly interface as done for the driver, since RC never offered context location.
	/**
	 * Find all elements within the current element that match the given selector.
	 * 
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#findElements(By)}
	 * 
	 * @param selector
	 * @return A list of all {@link Element}s matching this selector. Empty list means no elements found.
	 */
	List<Element> getElements(Selector selector);
	
	// TODO: javadoc
	List<Element> getElements(String selector);

	/**
	 * Find the only {@link Element} within the current element that matches the given selector.
	 * 
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#findElements(By)}
	 * 
	 * This should not be used unless selector is expected to return single element. This should not be used to look for non-present elements, use {@link #getElements(String)} and
	 * assert that list is empty. This should not be used to get first matching element, use {@link #getElements(String)} and get first match instead.
	 * 
	 * @param selector
	 * @return The matching sub{@link Element} of the current element.
	 * @throws ElementNotFoundException
	 *             If no matching elements are found
	 * @throws LooseSelectorException
	 *             If more than one matching element is found
	 */
	Element getSingleElement(Selector selector);

	// TODO: javadoc
	Element getSingleElement(String selector);
	
	// TODO: javadoc
	boolean isElementPresent(Selector selector);
	
	// TODO: javadoc
   boolean isElementPresent(String selector);
	
	/**
	 * Valid only if this current element is a {@literal <select>}. e.g. <select><option value="value1">visible text1</option><option value="value2">visible text2</option></select>
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.support.ui.Select}
	 * 
	 * @return Instance of class for using dropdown menus.
	 */
	SelectDropdown useAsDropdown();

	interface SelectDropdown {

		/**
		 * Selects an option by its 'value' attribute. e.g. {@literal <option value="byThis">}
		 * 
		 * <p/>
		 * WebDriver: uses {@link org.openqa.selenium.support.ui.Select#selectByValue(String)}
		 * 
		 * @param value
		 *            The 'value' attribute of the {@literal <option>} you wish to select.
		 */
		void selectOptionByValue(String value);

		/**
		 * Selects an option in a select dropdown if the current element is a {@literal <select>}.
		 * 
		 * <p/>
		 * WebDriver: uses {@link org.openqa.selenium.support.ui.Select#selectByVisibleText(String)}
		 * 
		 * @param value
		 *            The 'value' attribute of the {@literal <option>} you wish to select.
		 */
		void selectOptionByVisibleText(String text);

		/**
		 * @return Whether multiple options may be selected or not.
		 */
		boolean isMultiple();

		/**
		 * 
		 * @return the List of all {@literal <Option>}s.
		 */
		List<Element> getOptions();

		/**
		 * 
		 * @return the List of all {@literal <Option>}s that are currently selected.
		 */
		List<Element> getAllSelectedOptions();

		/**
		 * Marks the option at the provided index as selected.
		 */
		void selectByIndex(int index);

		/**
		 *Deselects all options.
		 */
		void deselectAll();

		/**
		 * Deselect an option by its value.
		 * @param value
		 */
		void deselectByValue(String value);

		/**
		 * Deselect an option by its index.
		 * @param index
		 */
		void deselectByIndex(int index);

		/**
		 * Deselect an option by its inner text.
		 * @param text
		 */
		void deselectByVisibleText(String text);
	}
	
	/**
	 * Determine whether or not this element is displayed or not
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#isDisplayed()}
	 * 
	 * @return true if the element is displayed or false if not
	 */
	boolean isDisplayed();

	/**
	 * Determine whether or not this element is enabled or not
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebElement#isEnabled()}
	 * 
	 * @return true if the element is enabled or false if not
	 */
	boolean isEnabled();
	
	/**
	 * Valid only for use on an {@literal <input>} or {@literal <textarea>} elements. Will attempt to clear the current text using send keys [ctr+A and delete] and set the value to "".
	 * <p/>
	 * 
	 */
	void clearWithSendKeys();
}
