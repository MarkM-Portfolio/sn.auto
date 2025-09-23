package com.ibm.conn.auto.util.webeditors.fvt.utils;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When the browser pops open a new window, the Selenium client has to 'chase' the newly opened window. Otherwise the Selenium client will continue to operate on the
 * old window. This class performs all the Selenium housekeeping that goes with the operation of 'focusing' Selenium on the context of the new browser window.<br/>
 * 
 * @author David Coelho
 */
public class WindowContextHandler {

	private static Logger log = LoggerFactory.getLogger(WindowContextHandler.class);

	private final DriverUtils driverUtl;

	private String initialWindowHandle;
	private int inititalWindowCount;
	
	/**
	 * The {@code WindowContextHandler} constructor memorizes the current window's handle and the number of currently open windows.
	 * @param driverUtils The object containing the WebDriver;  
	 */
	public WindowContextHandler(DriverUtils driverUtils) {
		driverUtl = driverUtils;

		log.info("Getting currently open window handle (preparing for new window to popup)");
		initialWindowHandle = driverUtl.wd().getWindowHandle();
		log.info("Currently open window handle is '" + initialWindowHandle + "'.");
		
		log.info("Finding the number of currently open windows");
		Set<String> windowHandles = driverUtl.wd().getWindowHandles();
		inititalWindowCount = windowHandles.size();
		log.info("Found '" + inititalWindowCount + "' open windows. Window handles are '" + windowHandles.toString() + "'.");
	}
	
	/**
	 * The {@code resyncSeleniumWithNewWindow} method waits until a new browser window opens, switches Selenium to that new window and returns the new window's handle.
	 * @return The new window's handle.
	 */
	public String resyncSeleniumWithNewWindow() {
		log.info("Waiting until a new window has opened...");
		driverUtl.waitUntilNumberOfWindowsEquals(inititalWindowCount + 1, 10); // 10 seconds timeout

		log.info("Find out new window's ID...");
		String newWindowHandle = driverUtl.getChildWindowHandle(initialWindowHandle);
		
		log.info("Switch to new Office Web App window... (ID:" + newWindowHandle + ")");
		driverUtl.wd().switchToWindowByHandle(newWindowHandle);
		
		return newWindowHandle;
	}

	/**
	 * The {@code resyncSeleniumWithOriginalWindow} method waits until a single browser window has closed and switches Selenium to the original window.
	 */
	public void resyncSeleniumWithOriginalWindow() {
		log.info("Waiting until a window has closed...");
		driverUtl.waitUntilNumberOfWindowsEquals(inititalWindowCount, 10); // 10 seconds timeout
		
		log.info("Switching back to the original window ("+initialWindowHandle+")");
		driverUtl.wd().switchToWindowByHandle(initialWindowHandle);
	}

}
