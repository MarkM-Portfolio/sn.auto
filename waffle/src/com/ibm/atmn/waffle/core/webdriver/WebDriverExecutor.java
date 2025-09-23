package com.ibm.atmn.waffle.core.webdriver;

import static com.ibm.atmn.waffle.utils.Utils.genStamp;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.remote.Augmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.Executor;
import com.ibm.atmn.waffle.core.ExecutorActionListener;
import com.ibm.atmn.waffle.core.JavaScriptLoader;
import com.ibm.atmn.waffle.core.JavaScriptLoader.Script;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.atmn.waffle.core.TestManager;
import com.ibm.atmn.waffle.core.selector.RCSelector;
import com.ibm.atmn.waffle.core.selector.Selector;
import com.ibm.atmn.waffle.core.selector.Selector.Strategy;
import com.ibm.atmn.waffle.core.selector.SizzleSelector;
import com.ibm.atmn.waffle.utils.FileIOHandler;
import com.ibm.atmn.waffle.utils.Utils;

/**
 * This is primarily a wrapper on Selenium's {@link WebDriver} interface. It adds additional functionality and helper methods for common cases. As WebDriver, there is a concept of
 * context related to {@link WebElement}, where this class represents the page, window or document root (in some cases this is restricted to the {@literal <body>} element).
 * <p />
 * There is a one-to-one relationship between a {@link TestManager} and an executor, so all test methods in a Test should use the same executor. It is possible to retrieve the
 * manager and {@link TestConfiguration} for a Test from an instance of this class.
 * <p />
 * If Test Methods in a Test are running in parallel, this class diverges very much from WebDriver in that it is thread-safe. There is a thread-local instance of WebDriver (created
 * on {@link #load(String)} that is used to handle each request. Consequently, if you want to re-use an instance of WebDriver across Test Methods, you need to ensure that the test
 * dependency is specified correctly (so TestNG will run them sequentially and single-threaded) and that the session is not {@link #quit()} until after the last test. It is still
 * possible for convenience to use {@link #load(String, boolean)} at the start of every test with preserveInstance as true. This will mean that the existing instance will be used
 * if it is present. A {@link WebDriver} can also be created separately and set using {@link #setContextDriver(WebDriver)} (if the {@link Executor} has been cast to a
 * {@link WebDriverExecutor}. It is not possible to for one thread to work with two instances of WebDriver through the Executor at the same time. Instead, parallel threads should
 * be used or one of the instances can be used directly in the test.
 * <p />
 * Note that this class implements {@link RCLocationExecutor} which extends the {@link Executor} interface to allow Selenium RC style selectors to be used directly without the need
 * to create {@link Selector}s, which is a more flexible implementation.
 * <p />
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 * 
 */
public class WebDriverExecutor implements RCLocationExecutor {
	

	private static class ThreadDriverHolder extends ThreadLocal<WebDriver> {

		private void setWebDriver(WebDriver driver) {

			super.set(driver);
		}

		private WebDriver getWebDriver() {

			return super.get();
		}		
	}

	private class ThreadActionListenerListHolder extends ThreadLocal<List<ExecutorActionListener>> {

		@Override
		protected List<ExecutorActionListener> initialValue() {
			return getTestActionListeners();
		}

		private List<ExecutorActionListener> getLocalActionListeners() {

			return get();
		}
	}

	/*
	 * Multiple executors belonging to the same thread is not permitted. This could be made non-static but doing so would cause other problems elsewhere anyway. The supported
	 * strategy to coordinate multiple browsers is parallel testing, which is a primary aim of this framework anyway and is a more flexible and powerful approach. Alternatively,
	 * multiple WebDriver instances could be created and used directly.
	 */
	private static ThreadDriverHolder driverHolder = new ThreadDriverHolder();

	private ThreadActionListenerListHolder actionListenerHolder;

	protected static Logger log = LoggerFactory.getLogger(WebDriverExecutor.class);

	private static final String[] SIZZLE_SELECTOR_DEFINITIONS = { ":contains(", ":containsEscaped(", ":not(", ":nth(", ":eq(", ":lt(", ":gt(", ":first", ":last", ":even", ":odd" };

	private TestManager testManager;

	public WebDriverExecutor(TestManager testManager) {

		setTestManager(testManager);
		actionListenerHolder = this.new ThreadActionListenerListHolder();
	}

	private void setTestManager(TestManager testManager) {

		this.testManager = testManager;
	}

	@Override
	public TestManager getTestManager() {

		return this.testManager;
	}

	protected TestConfiguration getTestConfiguration() {

		return getTestManager().getTestConfig();
	}

	@Override
	public List<ExecutorActionListener> getAllActionListeners() {

		//log.debug("get all action listeners returns: " + actionListenerHolder.getLocalActionListeners().size());
		return actionListenerHolder.getLocalActionListeners();
	}

	@Override
	public void addBrowserLifeActionListener(ExecutorActionListener listener) {

		getAllActionListeners().add(listener);
	}

	private List<ExecutorActionListener> getTestActionListeners() {

		//log.debug("get test action listeners returns: " + getTestManager().getTestActionListeners().size());
		return getTestManager().getTestActionListeners();
	}

	private void removeAllActionListeners() {

		//log.info("Removing all Browser-life action listeners.");
		actionListenerHolder.remove();
	}

	/**
	 * Gets a driver if it has been set. A {@link RuntimeException} is thrown if the driver has not been set.
	 * 
	 * @return A thread-local {@link WebDriver} instance
	 */
	public WebDriver wd() {

		if (driverIsSet()) {
			return driverHolder.getWebDriver();
		} else {
			log.error("A WebDriver Instance has not been set for the calling thread.");
			Utils.setThreadLocalUniqueTestName("No Artifacts");
			throw new RuntimeException(
					"A WebDriver instance has not been set for this thread. \nMake sure #load(String) has been called in your @Test or @BeforeMethod before you attempt to use executor.");
		}
	}

	@Override
	public Object getBackingObject() {

		return wd();
	}

	protected boolean driverIsSet() {

		return driverHolder.getWebDriver() != null;
	}

	private void removeContextDriver() {
		log.info("Destroying WebDriver instance for this thread: " + Thread.currentThread().getName() + "-" + Thread.currentThread().getId());
		driverHolder.getWebDriver().quit();
		driverHolder.remove();
	}

	protected void loadContextDriver() {

		setContextDriver(WebDriverSetup.createDriver(getTestConfiguration()));
	}

	/**
	 * Allows for the backing WebDriver instance to be set programmatically in the client suite. For this to be successful, it must be used along with (and in advance of)
	 * {@link #load(String, boolean)} where preserveInstance is true. If the instance is not preserved it will be overwritten with a new instance created from the configuration.
	 * Note that if you set an instance here that does not reflect your test configuration, you will unsurprisingly encounter problems. Waffle does not attempt to derive
	 * configuration from the driver set here. This is intended only to facilitate programmatic tailoring of driver configuration.
	 * 
	 * @param driver
	 */
	public void setContextDriver(WebDriver driver) {

		log.info("Setting WebDriver instance for this thread: " + Thread.currentThread().getName() + "-" + Thread.currentThread().getId());
		driverHolder.setWebDriver(driver);
	}

	Mouse getMouse() {

		return ((HasInputDevices) wd()).getMouse();
	}

	Keyboard getKeyboard() {

		return ((HasInputDevices) wd()).getKeyboard();
	}

	@Override
	public void load(String url) {

		load(url, false);
	}

	@Override
	public void load(String url, boolean preserveInstance) {

		if (!preserveInstance || (preserveInstance && !driverIsSet())) {
			loadContextDriver();
			// Configure implicit wait timeout (Note: This can be set only once).
			wd().manage().timeouts().implicitlyWait(getTestConfiguration().getImplicitWait(), TimeUnit.MILLISECONDS);
			wd().manage().timeouts().setScriptTimeout(getTestConfiguration().getImplicitWait(), TimeUnit.MILLISECONDS);
			if (!getTestConfiguration().browserIs(BrowserType.CHROME)) { //this timeout doesn't seem to work on gc as of selenium 2.24
				wd().manage().timeouts().pageLoadTimeout(getTestConfiguration().getImplicitWait() * 2, TimeUnit.MILLISECONDS);
			}
		} else {
			log.warn("A WebDriver instance is being reused.");
		}
		wd().get(url);
	}
	
	
	/*
	 * load the context driver and option to load the url
	 */
	
	@Override
	public void load(String url, boolean preserveInstance, boolean loadUrl) {

		if (!preserveInstance || (preserveInstance && !driverIsSet())) {
			loadContextDriver();
			// Configure implicit wait timeout (Note: This can be set only once).
			wd().manage().timeouts().implicitlyWait(getTestConfiguration().getImplicitWait(), TimeUnit.MILLISECONDS);
			wd().manage().timeouts().setScriptTimeout(getTestConfiguration().getImplicitWait(), TimeUnit.MILLISECONDS);
			if (!getTestConfiguration().browserIs(BrowserType.CHROME)) { //this timeout doesn't seem to work on gc as of selenium 2.24
				wd().manage().timeouts().pageLoadTimeout(getTestConfiguration().getImplicitWait() * 2, TimeUnit.MILLISECONDS);
			}
		} else {
			log.warn("A WebDriver instance is being reused.");
		}
		if(loadUrl == true)
		{
			wd().get(url);
		}
		
	}

	@Override
	public void quit() {

		if (driverIsSet()) {
			//log.debug("Quitting WebDriver");
			//IE hangs if onclose popup on the page
			if(getTestConfiguration().browserIs(BrowserType.IE)) {
				try{
					this.executeScript("window.onbeforeunload = function(e){};");
				}catch(Exception e){}
			}
			wd().quit();
			removeContextDriver();
		}
		removeAllActionListeners();
	}

	@Override
	public File saveScreenshot(String message) {

		String screenshot;

		if (!(wd() instanceof TakesScreenshot)) {
			screenshot = ((TakesScreenshot) new Augmenter().augment(wd())).getScreenshotAs(OutputType.BASE64);
		} else {
			screenshot = ((TakesScreenshot) wd()).getScreenshotAs(OutputType.BASE64);
		}

		// decode

//		Base64Encoder decoder = new Base64Encoder();
		byte[] decodedScreenshot = null;
		decodedScreenshot = Base64.getDecoder().decode(screenshot);
//		decodedScreenshot = decoder.decode(screenshot);

		// log.warn("IOException decoding screenshot: ");

		String fileName = genStamp() + ".png";
		File targetFile = new File(getTestConfiguration().getScreenshotsDir() + File.separator + fileName);
		// write file
		FileIOHandler.writeRawDataToFile(targetFile, decodedScreenshot);

		// Get relative URL for file
		URL imageURL = null;
		try {
			imageURL = targetFile.toURI().toURL();
		} catch (MalformedURLException e) {
			log.warn("MalformedURLException: " + e.getMessage());
			e.printStackTrace();
		}

		// Report
		String location = "";
		location = getCurrentUrl();
		String linkToImage = "<a href=" + imageURL + ">" + message + "</a>";
		String linkToAUT = "<a href=" + location + ">AUT Address</a>";
		Reporter.log("<br />" + linkToImage + " @ " + linkToAUT + "<br />");

		return targetFile;
	}
	
	@Override
	public File saveScreenshotWithFilename(String fileName) {

		String screenshot;

		try {
			if (!(wd() instanceof TakesScreenshot)) {
				screenshot = ((TakesScreenshot) new Augmenter().augment(wd())).getScreenshotAs(OutputType.BASE64);
			} else {
				screenshot = ((TakesScreenshot) wd()).getScreenshotAs(OutputType.BASE64);
			} 
		} catch(Exception e)  {
			log.warn("Error saving screenshot: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		// decode
		byte[] decodedScreenshot = null;
		decodedScreenshot = Base64.getDecoder().decode(screenshot);

		fileName = fileName + ".png";
		//fileName = null;
		File targetFile = new File(getTestConfiguration().getScreenshotsDir() + File.separator + fileName);
		// write file
		FileIOHandler.writeRawDataToFile(targetFile, decodedScreenshot);

		// Get relative URL for file
		URL imageURL = null;
		try {
			imageURL = targetFile.toURI().toURL();
		} catch (MalformedURLException e) {
			log.warn("MalformedURLException: " + e.getMessage());
			e.printStackTrace();
		}

		// Report
		String location = "";
		try {
			// it has been observed that when test failed due to an unexpected alert/prompt
			// eg. basic auth login, getCurrentUrl() would throw UnhandledAlertException.
			location = getCurrentUrl();
		} catch (Exception e) {
			log.warn("Exception in getCurrentUrl to get URL of failing point: " + e.getMessage());
			e.printStackTrace();
		}		
		String linkToImage = "<a href=" + imageURL + ">" + "Screenshot for method: " + fileName + "</a>";
		String linkToAUT = "<a href=" + location + ">AUT Address</a>";
		Reporter.log("<br />" + linkToImage + " @ " + linkToAUT + "<br />");

		return targetFile;
	}

	@Override
	public void close() {

		int windowCount = getWindowHandles().size();
		if (windowCount > 1) {
			wd().close();
		} else {
			quit();
		}
	}

	@Override
	public String getCurrentUrl() {

		return wd().getCurrentUrl();
	}

	@Override
	public String getPageSource() {

		// TODO: There seem to be disadvantages to the implementation of this in WebDriver. Try to use script here.
		return wd().getPageSource();
	}

	@Override
	public List<Element> getElements(Selector selector) {

		WebDriverElementLocator locator = new WebDriverElementLocator(this, selector);
		List<WebElement> wdElements = locator.locateAll();

		return WebDriverElement.wrapWebElements(this, wdElements, selector, null);
	}

	@Override
	public Element getSingleElement(Selector selector) {

		List<Element> elements = getElements(selector);
		if (elements.size() > 1) {
			throw new AssertionError("Too many found for: " + selector + "." + elements.size() + " elements found. getSingleElement expects only 1 matching element.");
		} else if (elements.isEmpty()) {
			throw new AssertionError("No elements found for: " + selector + ".");
		}
		return elements.get(0);
	}

	@Override
	public Element getFirstElement(Selector selector) {

		List<Element> elements = getElements(selector);
		if (elements.isEmpty()) {
			throw new AssertionError("No elements found for: " + selector + ".");
		}
		return elements.get(0);
	}

	@Override
	public List<Element> getVisibleElements(Selector selector) {

		List<Element> elements = getElements(selector);
		Iterator<Element> iter = elements.iterator();
		@SuppressWarnings("unused")
		int before = elements.size();
		while (iter.hasNext()) {
			Element element = iter.next();
			if (!element.isVisible()) {
				iter.remove();
			}
		}
		//log.debug("Returning " + elements.size() + " visible elements out of a total of " + before + " located");
		return elements;
	}

	@Override
	public boolean isElementPresent(Selector selector) {

		int elementCount = getElements(selector).size();
		if (elementCount > 1) {
			throw new AssertionError("Too many found for: " + selector + ". " + elementCount + " elements found. isElementPresent expects only 1 matching element.");
		}
		return elementCount == 1;
	}

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
	public List<Element> getElements(String selector) {

		return getElements(parseSelectorString(selector));
	}

	@Override
	public Element getFirstElement(String selector) {

		return getFirstElement(parseSelectorString(selector));
	}

	@Override
	public Element getSingleElement(String selector) {

		return getSingleElement(parseSelectorString(selector));
	}

	@Override
	public List<Element> getVisibleElements(String selector) {

		return getVisibleElements(parseSelectorString(selector));
	}

	@Override
	public boolean isElementPresent(String selector) {

		return isElementPresent(parseSelectorString(selector));
	}

	@Override
	public String getTitle() {

		return wd().getTitle();
	}

	@Override
	public void typeNative(CharSequence... text) {

		//String plain = text.toString();
		//log.debug("Native Typing text '" + plain + "' into active element.");
		Actions writer = new Actions(wd());
		writer.sendKeys(text).perform();
	}

	@Override
	public String getBodyText() {

		return this.getSingleElement("css=body").getText();
	}

	@Override
	public boolean isTextPresent(String text) {

		// TODO: Make this dig frame text
		return getSingleElement("css=body").isTextPresent(text);
	}
	
	@Override
	public boolean isTextNotPresent(String text) {//Changed from a boolean
		turnOffImplicitWaits();
		boolean NoText = !getSingleElement("css=body").getText().contains(text);
		turnOnImplicitWaits();
		return NoText;
	}
	
	public boolean turnOnImplicitWaits() {
		log.info("Setting implicitWait to " + TimeUnit.MILLISECONDS.toSeconds(getTestConfiguration().getImplicitWait()) + " seconds.");
		wd().manage().timeouts().implicitlyWait(getTestConfiguration().getImplicitWait(), TimeUnit.MILLISECONDS);
		Utils.setThreadLocalImplictWait(getTestConfiguration().getImplicitWait());
		return true;
	}
	
	public boolean turnOffImplicitWaits() {
		log.info("Setting implicitWait to 0 seconds.");
		wd().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		Utils.setThreadLocalImplictWait(0);
		return true;
	}
	
	/**
	 * Override implicit wait setting. Remember to call turnOnImplicitWaits() to reset when not needed.
	 * @param newTimeOut in SECONDS
	 */
	public boolean changeImplicitWaits(int newTimeOut) {
		log.info("Setting implicitWait to " + newTimeOut + " seconds.");
		wd().manage().timeouts().implicitlyWait(newTimeOut, TimeUnit.SECONDS);
		Utils.setThreadLocalImplictWait(TimeUnit.SECONDS.toMillis(newTimeOut));
		return true;
	}
	
	/**
	 * Get current implicit wait setting. It is in millisecond because calls such
	 * as isElementPresent (which uses WebDriverElementLocator.locateAll) expects it.
	 * @return current setting in MILLISECONDS
	 */
	public long getImplicitWaits() {
		return Utils.getThreadLocalImplictWait();
	}

	@Override
	public void clickAt(int xOffset, int yOffset) {

		Actions acts = new Actions(wd());
		acts.moveToElement((WebElement) getSingleElement(new RCSelector("css=body")).getBackingObject(), xOffset, yOffset).click().perform();
	}

	@Override
	public boolean isLoaded() {

		return driverIsSet();
	}

	@Override
	public Element switchToActiveElement() {

		return new WebDriverElement(this, wd().switchTo().activeElement());
	}

	@Override
	public String getWindowHandle() {
		return wd().getWindowHandle();
	}

	@Override
	public Set<String> getWindowHandles() {

		return wd().getWindowHandles();
	}

	@Override
	public Executor switchToWindowByHandle(String handle) {

		wd().switchTo().window(handle);
		acceptExplorerSSLWarning();
		return this;
	}

	@Override
	public Executor switchToWindowByName(String name) {

		return switchToWindowByHandle(name);
	}

	@Override
	public Executor switchToFirstMatchingWindowByPageTitle(String title) {

		return switchToWindowByHandle(getFirstHandleByMatchingPageTitle(title));
	}

	@Override
	public void maximiseWindow() {

		wd().manage().window().maximize();
	}

	private String getFirstHandleByMatchingPageTitle(String pageTitle) {

		long startTime = System.currentTimeMillis();
		String targetHandle = null;

		for (int i = 0; i == 0 || (targetHandle == null && System.currentTimeMillis() < (startTime + getTestConfiguration().getImplicitWait())); i++) {

			Utils.milliSleep(i * 200);
			Set<String> windowHandles = getWindowHandles();
			Iterator<String> it = windowHandles.iterator();

			while (targetHandle == null && it.hasNext()) {
				String thisHandle = it.next();
				switchToWindowByHandle(thisHandle);

				if (getTitle().contains(pageTitle)) {
					targetHandle = thisHandle;
				}
			}
		}

		if (targetHandle == null) {
			throw new AssertionError("A window starting with title '" + pageTitle + "' could not be found.");
		} else {
			return targetHandle;
		}
	}

	@Override
	public FrameSwitcher switchToFrame() {

		return new WebDriverFrameSwitcher();
	}

	private class WebDriverFrameSwitcher implements FrameSwitcher {

		@Override
		public Executor returnToTopFrame() {

			wd().switchTo().defaultContent();
			return WebDriverExecutor.this;
		}

		@Override
		public Executor selectFrameByElement(Element frameElement) {

			wd().switchTo().frame(((WebElement) frameElement.getBackingObject()));
			return WebDriverExecutor.this;
		}

		@Override
		public Executor selectFrameByIndex(int index) {

			wd().switchTo().frame(index);
			return WebDriverExecutor.this;
		}

		@Override
		public Executor selectSingleFrameBySelector(String selector) {

			return selectFrameByElement(getSingleElement(selector));
		}

	}

	@Override
	public Navigation navigate() {

		return new WebDriverNavigation();
	}

	private class WebDriverNavigation implements Navigation {
		
		@Override
		public void back() {

			wd().navigate().back();
		}

		@Override
		public void forward() {

			wd().navigate().forward();
		}

		@Override
		public void refresh() {

			wd().navigate().refresh();
		}

		@Override
		public void to(String url) {

			wd().navigate().to(url);
		}

	}

	@Override
	public Alert switchToAlert() {

		return new WebDriverAlert();
	}

	private class WebDriverAlert implements Alert {

		@Override
		public void accept() {

			wd().switchTo().alert().accept();
		}

		@Override
		public void dismiss() {

			wd().switchTo().alert().dismiss();
		}

		@Override
		public String getText() {

			return wd().switchTo().alert().getText();
		}

		@Override
		public void type(String text) {

			wd().switchTo().alert().sendKeys(text);
		}
	}

	@Override
	public void acceptExplorerSSLWarning() {

		if (getTestConfiguration().browserIs(BrowserType.IE) && getTitle().equalsIgnoreCase("Certificate Error: Navigation Blocked")) {
			navigate().to("javascript:document.getElementById('overridelink').click()");
		}
	}

	@Override
	public Calendar getBrowserDatetime() {

		//log.debug("Attempting to get browser datetime.");

		String pattern = "yyyy-MM-dd'T'HH:mm:ss";// the pattern returned by JS (ISO8601 compliant)
		SimpleDateFormat ISO8601withoutZone = new SimpleDateFormat(pattern);

		// Long currentTime;
		// try{
		// Double doubleTime = (Double) javascriptContext.executeScript(jsTime);
		// currentTime = doubleTime.longValue();
		// }catch(ClassCastException e){
		// currentTime = (Long) javascriptContext.executeScript(jsTime);
		// }

		long startTime = System.currentTimeMillis();
		Date browserDate = null;

		for (int i = 0; i == 0 || (browserDate == null && System.currentTimeMillis() < (startTime + getTestConfiguration().getImplicitWait())); i++) {

			JavaScriptLoader.loadScript(this, Script.WAFFLE_MISC);
			String browserLocalDatetime = "";
			browserLocalDatetime = (String) executeScript("return " + Script.WAFFLE_MISC.getHandle() + ".getISOLocalStringCurrentTime();");

			try {
				browserDate = ISO8601withoutZone.parse(browserLocalDatetime);
			} catch (ParseException e) {
				log.warn("ParseException[" + e.getMessage() + "] caught parsing browser time from: " + browserLocalDatetime);
			}
		}
		assert browserDate != null : "Unable to obtain local time from browser.";

		Calendar browserCalendar = Calendar.getInstance();
		browserCalendar.setTime(browserDate);
		//log.debug("Identified " + ISO8601withoutZone.format(browserCalendar.getTime()) + " as browser current local time.");

		return browserCalendar;
	}

	@Override
	public Object executeScript(String script, Object... args) {

		return runScript(false, script, args);
	}

	@Override
	public Object executeAsyncScript(String script, Object... args) {

		return runScript(true, script, args);
	}

	private Object runScript(boolean async, String script, Object... args) {

		String descriptor = "";
		if (async) descriptor = "Asyncronous ";

		//log.debug("Attempting " + descriptor + "JavaScript execution.");
		log.trace("Attempting " + descriptor + "JavaScript execution with script: " + script);
		Object result = null;
		long startTime = System.currentTimeMillis();
		boolean success = false;

		JavascriptExecutor scriptExecutor;
		if (wd() instanceof JavascriptExecutor) {
			scriptExecutor = (JavascriptExecutor) wd();
		} else {
			log.error("WebDriver instance is not JavaScript capable");
			throw new RuntimeException("WebDriver instance is not JavaScript capable");
		}

		for (int i = 0; !success && System.currentTimeMillis() < (startTime + getTestConfiguration().getImplicitWait()); i++) {
			Utils.milliSleep(200 * i);
			try {
				if (async) {
					result = scriptExecutor.executeAsyncScript(script, args);
				} else {
					result = scriptExecutor.executeScript(script, args);
				}
				if (result == null) {
					log.warn("" + descriptor + "JavaScript execution returned null");
				}
				success = true;
			} catch (WebDriverException e) {
				if (e.getMessage().contains("window.ICCAWaffleSizzle is not a function")) {
					log.warn("window.ICCAWaffleSizzle not found, load Extra Sizzle script again.");
					JavaScriptLoader.loadScript(this, Script.EXTRA_SIZZLE);
				}
				log.warn(descriptor + "JavaScript execution encountered WebDriver JavaScript execution exception: " + e.getMessage());
				log.trace(descriptor + "JavaScript execution encountered WebDriver JavaScript execution exception: " + e.getMessage() + " ...While attempting script: " + script);
			}
		}
		if (!success) {
			log.error(descriptor + "JavaScript execution failed to return without exception. Check log for warnings.");
			throw new RuntimeException(descriptor + "JavaScript execution failed to return without exception. Check log for warnings.");
		} else {
			return result;// may be null
		}
	}
}