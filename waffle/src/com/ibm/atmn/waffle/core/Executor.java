package com.ibm.atmn.waffle.core;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.ibm.atmn.waffle.core.selector.Selector;
import com.ibm.atmn.waffle.core.webdriver.WebDriverExecutor;

/**
 * Primary Interface for interacting with your chosen web automation tool for controlling the browser. The following list identifies the key terms found in this interface.
 * <p/>
 * <ul>
 * <li>{@link Executor} The browser itself, its windows, and the current page document.</li>
 * <li>{@link Element} An element of the current page.</li>
 * </ul>
 * <p/>
 * First method in a session that should be invoked is {@link #load(String)}, which is used to open a browser and load a new web page. Last method that should be invoked in a
 * session is {@link #quit} to close the browser. {@link #getSingleElement(String)} is used to find an {@link Element} to examine or act on.
 * <p/>
 * Implementation will vary by test tool and browser, depending on capabilities.
 * <p/>
 * WebDriver: uses {@link org.openqa.selenium.WebDriver}
 * 
 * @see WebDriverExecutor
 * @author Ruairi Pidgeon/Ireland/IBM
 */
public interface Executor {
	
	/**
	 * 
	 * @return The {@link TestManager} that created this Executor.
	 */
	TestManager getTestManager();
	
	/**
	 * 
	 * @return The list of all active listeners (test listeners and browser-life listeners).
	 */
	List<ExecutorActionListener> getAllActionListeners();
	
	/**
	 * Adds a listener that is tied to the life of the browser (thread-local). All listeners added this way will be destroyed
	 * along with the representative browser instance on {@link #quit()}. To add @literal <test>} scoped listeners, add them using
	 * {@link TestManager#addTestActionListener(ExecutorActionListener)}
	 */
	void addBrowserLifeActionListener(ExecutorActionListener listener);
	
	/**
	 * 
	 * @return The primary test tool instance driving the browser interaction.
	 * <p/>
	 * WebDriver: returns {@link org.openqa.selenium.WebDriver}
	 */
	Object getBackingObject();

	// Navigation

	/**
	 * Opens new browser window and navigates to URL. Returns once load is complete. A new backing instance will be used.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#get(String)}
	 * 
	 * @param url
	 *            The URL to load. e.g. http://w3.ibm.com/connections/activities/login.jsp
	 */
	void load(String url);

	/**
	 * Same as {@link #load(String)}load but may be used to automatically reuse an existing backing instance if available (i.e. if browser never quit)
	 * 
	 * @param url
	 * @param preserveInstance
	 *            Will attempt to use existing backing object if true.
	 */
	void load(String url, boolean preserveInstance);
	

	/**
	 * Same as {@link #load(String)}load but may be used to automatically reuse an existing backing instance if available (i.e. if browser never quit)
	 * 
	 * @param url
	 * @param preserveInstance
	 * @param getUrl
	 *            Will attempt to use existing backing object if true.
	 */
	void load(String url, boolean preserveInstance,boolean loadUrl);

	/**
	 * This can be used to execute JavaScript directly on the page. Behaviour will likely vary significantly depending on the tool. Return type will vary in particular. Expect that
	 * escaped characters such as "\"" will be automatically double escaped (for JavaScript also, i.e. "\\\""). Execution exceptions will be automatically caught and execution will
	 * be attempted again until the implicit_wait timeout has been reached. RuntimeException will be thrown if script can not be executed without internal tool exception.
	 * <p/>
	 * For WebDriver, include a 'return' statement in script or nothing will be returned. Objects returned are specified in WebDriver doc but be aware that return type might vary
	 * across browsers.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.JavascriptExecutor#executeScript(String, Object...)}
	 * 
	 * @param script
	 *            The JavaScript to execute
	 * @param args
	 *            An Object[] of arguments matching "arguments[0...n]" in the script
	 * @return The object returned by the script execution function of the test tool. May be null.
	 */
	Object executeScript(String script, Object... args);

	/**
	 * Get a string representing the current URL that the browser is looking at.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#getCurrentUrl()}
	 * 
	 * @return The URL of the page currently loaded in the browser
	 */
	String getCurrentUrl();

	// General properties

	/**
	 * The inner text of the title element of the current page.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#getTitle()}
	 * 
	 * @return The title with leading and trailing whitespace stripped, or an empty string if title empty/not found
	 */
	String getTitle();

	/**
	 * Find all elements within the current page that match the given selector.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#findElements(By)}
	 * 
	 * @param selector
	 * @return A list of all {@link Element}s matching this selector. Empty list means no elements found.
	 */
	List<Element> getElements(Selector selector);

	/**
	 * Find the {@link Element} within the current page that matches the given selector.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#findElements(By)}
	 * <p/>
	 * This should not be used unless you want to strictly enforce that a selector return exactly one element. for better performance, use use {@link #getFirstElement(String)}
	 * instead. This should not be used to look for non-present elements, use {@link #getElements(String)} and assert that list is empty. This should not be used to get first
	 * matching element, use {@link #getFirstElement(String)} instead.
	 * 
	 * @param selector
	 * @return The matching {@link Element} on the current page.
	 * @throws ElementNotFoundException
	 *             If no matching elements are found
	 * @throws LooseSelectorException
	 *             If more than one matching element is found
	 */
	Element getSingleElement(Selector Selector);

	/**
	 * Find the first {@link Element} within the current page that matches the given selector.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#findElements(By)}
	 * <p/>
	 * This should not be used unless only the first matching element found is required. This should not be used to look for non-present elements, use {@link #getElements(String)}
	 * and assert that list is empty.
	 * 
	 * @param selector
	 * @return The matching {@link Element} on the current page.
	 * @throws ElementNotFoundException
	 *             If no matching elements are found
	 */
	Element getFirstElement(Selector selector);

	/**
	 * Find all visible {@link Element}s within the current page that matches the given selector.
	 * <p/>
	 * 
	 * @see #getElements(String)
	 * @see Element#isVisible()
	 * 
	 * @param selector
	 * @return A list of all {@link Element}s that match this selector and answer true to {@link Element#isVisible()}. Empty list means no visible elements found.
	 */
	List<Element> getVisibleElements(Selector selector);

	/**
	 * Determine if there is a single matching selector on the current page. If you wish to test for more than one matching Element, use {@link #getElements(Selector)}.
	 * 
	 * @see #getSingleElement(Selector)
	 * @param selector
	 * @return true if there is single matching element, false if there are none, will terminate with AssertionError otherwise.
	 */
	boolean isElementPresent(Selector selector);

	// Misc

	/**
	 * Get the HTML source of the page.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#getPageSource()} NOTE: See delegate JavaDoc for important warnings on possible browser variation of returned string.
	 * <p/>
	 * 
	 * @return The source of the current page (not necessarily the current page source!)
	 */
	String getPageSource();

	/**
	 * 
	 * @return The inner text of the {@literal <body>} element of the current page and all descendants (included under the rules of {@link Element#getText()}.
	 */
	String getBodyText();

	/**
	 * Checks for text using {@link #getBodyText()}. Case insensitive.
	 * 
	 * @param text
	 * @return true if the body text contains provided text, false otherwise.
	 */
	boolean isTextPresent(String text);

	/**
	 * Close the current window.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#close()}
	 * <p/>
	 * Do not use to close the last window, use {@link #quit()} instead.
	 */
	void close();

	/**
	 * Exits browser, closing all open windows. Next invocation on Executor, if any, should be {@link #load(String)}.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#quit()}
	 */
	void quit();

	/**
	 * Attempts to save a screenshot of the current page to the report output folder for this suite. Success will depend on tool and browser. A link to the screenshot will be added
	 * to TestNG Reporter for the current suite.
	 * 
	 * @param message
	 */
	File saveScreenshot(String message);
	
	/**
	 * Attempts to save a screenshot of the current page to the report output folder for this suite. Success will depend on tool and browser. A link to the screenshot will be added
	 * to TestNG Reporter for the current suite.
	 * 
	 * @param message
	 */
	File saveScreenshotWithFilename(String fileName);

	/**
	 * Use this if you are attempting to type without a standard {@literal <input>} element. This method will attempt simulate key presses that may have the desired effect. The
	 * correct element focus must be achieved before calling this method. Window focus may also be required.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.interactions.Actions#sendKeys(CharSequence...)}
	 * 
	 * @param text
	 */
	void typeNative(CharSequence... text);

	/**
	 * Click coordinates on the page. Coordinates are taken as an offset from the top left corner of the {@literal <body>} element.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.interactions.Actions#moveToElement(WebElement, int, int)}
	 * @see Element#clickAt(int, int)
	 * @param xOffset
	 * @param yOffset
	 */
	void clickAt(int xOffset, int yOffset);

	/**
	 * Determines if the backing tool instance is currently set for this thread and return s true if so, false otherwise.
	 * 
	 * @return true if the backing object is currently set.
	 */
	boolean isLoaded();

	/**
	 * Gets the browser-local current time using JavaScipt Date() on the current page.
	 * 
	 * @return A Calendar instance set to the browser's current date and time.
	 */
	Calendar getBrowserDatetime();

	/**
	 * Return a set of window handles which can be used to iterate over all open windows of this browser instance by passing them to {@link #switchToWindowByHandle()}
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#getWindowHandles()}
	 * 
	 * @return A set of window handles which can be used to iterate over all open windows.
	 */
	Set<String> getWindowHandles();

	/**
	 * Returns the handle for the current window. Unique for this browser session only.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver#getWindowHandles()}
	 * 
	 * @return The handle of the current target window.
	 */
	String getWindowHandle();

	/**
	 * Switches to a window with the given window name. This is the name given when the window was created.
	 * 
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver.TargetLocator#window(String)}
	 * 
	 * @param name
	 * @return this Executor
	 */
	Executor switchToWindowByName(String name);

	/**
	 * Switches to a window with the given js/internal handle.
	 * 
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver.TargetLocator#window(String)}
	 * 
	 * @param handle
	 * @return this Executor
	 */
	Executor switchToWindowByHandle(String handle);

	/**
	 * Switches to the window with a root page title that contains ({@link java.lang.String#contains(CharSequence)}) the provided String.
	 * 
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver.TargetLocator#window(String)}
	 * 
	 * @param title
	 *            The title of the page loaded in the desired window.
	 * @return this Executor
	 */
	Executor switchToFirstMatchingWindowByPageTitle(String title);

	/**
	 * Maximises the current window.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver.Window#maximize()}
	 */
	void maximiseWindow();

	/**
	 * Switches to the element that currently has focus within the document. i.e. the element that would recieve keyboard input if the user was to type.
	 * <p/>
	 * WebDriver: uses {@link org.openqa.selenium.WebDriver.TargetLocator#activeElement()}
	 * 
	 * @return The Element with focus, or the body element if no element with focus can be detected.
	 */
	Element switchToActiveElement();

	/**
	 * Use to select a frame. When a frame is selected, all future executor actions will be restricted to that frame. Use {@link FrameSwitcher#returnToTopFrame()} to move to main
	 * document (if {@literal <iframe>}) or top frame (if page of {@literal <iframe>}s.
	 * 
	 * @return A FrameSwitcher which can be used to switch between {@literal <frame>}s or {@literal <iframe>}s on the page.
	 */
	FrameSwitcher switchToFrame();

	interface FrameSwitcher {

		/**
		 * Select a frame by its position among frames on on the page. The first frame has index '0'.
		 * <p/>
		 * WebDriver: uses {@link org.openqa.selenium.WebDriver.TargetLocator#frame(int)}
		 * 
		 * @param index
		 */
		Executor selectFrameByIndex(int index);

		/**
		 * Select a frame using standard element selector.
		 * <p/>
		 * WebDriver: uses {@link org.openqa.selenium.WebDriver.TargetLocator#frame(WebElement)}
		 * 
		 * @param Selector
		 *            is standard form.
		 */
		Executor selectSingleFrameBySelector(String selector);

		/**
		 * Select a frame that has already been located.
		 * <p/>
		 * WebDriver: uses {@link org.openqa.selenium.WebDriver.TargetLocator#frame(WebElement)}
		 * 
		 * @param frameElement
		 *            This element must be a {@literal <frame> or <iframe>} to prevent exception.
		 */
		Executor selectFrameByElement(Element frameElement);

		/**
		 * Selects either the first {@literal <frame>} on the page, or the root document when a page contains {@literal <iframe>}.
		 * <p/>
		 * WebDriver: uses {@link org.openqa.selenium.WebDriver.TargetLocator#defaultContent()}
		 */
		Executor returnToTopFrame();

	}

	/**
	 * Switches to an expected JavaScript alert. Throws an exception if not found.
	 * 
	 * @return {@link Alert} with options to act on alert.
	 */
	Alert switchToAlert();

	interface Alert {

		/**
		 * Equivalent to using 'cancel' option on alert.
		 * <p />
		 * WebDriver: uses {@link org.openqa.selenium.Alert#dismiss()}
		 */
		void dismiss();

		/**
		 * Equivalent to using 'ok' option on alert.
		 * <p />
		 * WebDriver: uses {@link org.openqa.selenium.Alert#accept()}
		 */
		void accept();

		/**
		 * Gets the text of the alert message.
		 * <p />
		 * WebDriver: uses {@link org.openqa.selenium.Alert#getText()}
		 */
		String getText();

		/**
		 * For use with JavaScript input alerts.
		 * <p />
		 * WebDriver: uses {@link org.openqa.selenium.Alert#sendKeys(String)}
		 */
		void type(String text);
	}

	/**
	 * Returns {@link Navigation} which provides methods that simulate the use of the browser's navigation bar. Includes {@link Navigation#back()}, {link Navigation#forward()), go
	 * to url: {@link Navigation#to(String)}, and {@link Navigation#refresh()}.
	 * <p/>
	 * WebDriver uses {@link org.openqa.selenium.WebDriver.Navigation}
	 * 
	 * @return A {@link Navigation}
	 */
	Navigation navigate();

	interface Navigation {
		/**
		 * Simulates click of browser 'back' button or does nothing if no pages in history.
		 * <p/>
		 * WebDriver uses {@link org.openqa.selenium.WebDriver.Navigation#back()}
		 */
		void back();

		/**
		 * Simulates click of browser 'forward' button or does nothing if current page is latest in history.
		 * <p/>
		 * WebDriver uses {@link org.openqa.selenium.WebDriver.Navigation#forward()}
		 */
		void forward();

		/**
		 * Simulates entering url in browser address bar and sending request.
		 * <p/>
		 * WebDriver uses {@link org.openqa.selenium.WebDriver.Navigation#to()}
		 * 
		 * @param a
		 *            fully qualified url
		 */
		void to(String url);

		/**
		 * Simulates click of browser 'refresh' button.
		 * <p/>
		 * WebDriver uses {@link org.openqa.selenium.WebDriver.Navigation#refresh()}
		 */
		void refresh();
	}

	/**
	 * Accept the SSL warning page in Internet Explorer by clicking on the 'override' link.
	 * Performs a check for the page, and if not found then no further action will be taken.
	 */
	void acceptExplorerSSLWarning();

	//TODO: doc
	Object executeAsyncScript(String script, Object... args);

	/**
	 * This is a check using the html body to ensure that certain text is not 
	 * present in the UI. 
	 * @param text
	 * @return
	 */
	boolean isTextNotPresent(String text);
	
	/** 
	 * Turn on the implicitWait again using the default timeout as per xml file 
	 * @return
	 */
	boolean turnOnImplicitWaits();
	
	/**
	 * Turn off the implicitWait timeout so an action is performed quickly, ensure
	 * that you then use turnOnImplicitWaits() to turn the implicitWait on again
	 * otherwise there is the risk of timeouts
	 * @return
	 */
	boolean turnOffImplicitWaits();
	
	/**
	 * Adjust implicitWait timeout in case the default is too long/short for a given action.
	 * Ensure that you then use turnOnImplicitWaits() to restore to default timeout
	 * otherwise there is the risk of premature timeouts
	 * @return
	 */
	boolean changeImplicitWaits(int newTimeOut);
	
	/**
	 * Get the current implicit wait setting
	 * @return
	 */
	long getImplicitWaits();
}
