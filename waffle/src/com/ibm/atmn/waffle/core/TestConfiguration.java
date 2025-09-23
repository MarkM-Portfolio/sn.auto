package com.ibm.atmn.waffle.core;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.xml.XmlSuite;

import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.atmn.waffle.utils.PropertyNum;
import com.ibm.atmn.waffle.utils.Utils;

/**
 * For gathering and holding test and suite configuration. Should be refreshed
 * at least at start of every Test to ensure config from testng test structure
 * perspective. Is held by {@link TestManager}
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 * 
 */
public class TestConfiguration {

	// private static final Logger log =
	// LoggerFactory.getLogger(TestConfiguration.class);
	
	private static final Logger log = LoggerFactory.getLogger(TestConfiguration.class);

	public static enum BrowserType {

		IE,
		FIREFOX,
		CHROME,
		SAFARI,
		OPERA,
		HTML_UNIT,
		ANDROID,
		EDGE,
		IPHONE;
	}

	// TODO: How should/can test log level be implemented with new logger?
	private enum ParameterNames implements PropertyNum {

		SERVER_HOST("localhost"),
		SERVER_PORT(""),
		SERVER_IS_GRID_HUB("false"),
		SERVER_IS_BROWSERSTACK("false"),
		SERVER_IS_LEGACY_GRID("true"),
		BROWSERSTACK_USERNAME(""),
		BROWSERSTACK_KEY(""),
		BROWSERSTACK_DEBUG(""),
		BROWSERSTACK_TIMEOUT("90"),
		SELENOID_USERNAME(""),
		SELENOID_KEY(""),
		BROWSER_START_COMMAND,
		BROWSER_START_COMMAND_DELIMITER("_"),
		BROWSER_URL,
		BROWSER_URL_MT_ORGA("_"),
		BROWSER_URL_MT_ORGB("_"),
		TEST_LOG_LEVEL("info"),
		TEST_TOOL("web_driver"),
		TIMEOUT_IMPLICIT_WAIT("20000"),
		TIMEOUT_ASYNC_SCRIPT("30000"),
		TIMEOUT_PAGE_NAVIGATE_TIMEOUT("60000"),
		PCH_ENABLE_DOJO_PAGELOAD("false"),
		PCH_ENABLE_STANDARD_PAGELOAD("true"),
		PCH_POST_EVENT_PAUSE("0"),
		LOCATION_FORCE_SCRIPT("false"),
		LOCATION_USE_FALLBACK_STRATEGY("true"),
		TYPING_DELAY("300"),
		IS_MT("false"),
		AUTH_TYPE("basic");	


		private final String defaultValue;

		ParameterNames() {

			this.defaultValue = null;
		}

		ParameterNames(String defaultValue) {

			this.defaultValue = defaultValue;
		}

		public String getDefaultValue() {

			return this.defaultValue;
		}

		@Override
		public String toString() {

			return name();
		}
	}

	public enum TestTool {

		WEB_DRIVER;

		@Override
		public String toString() {

			return name();
		}
	}
	private String osVersion;
	
	private XmlSuite suite;

	private String browserName;

	private String browserVersion;

	private BrowserType browser;

	private TestTool testTool;

	private File screenshotsDir;

	private Environment browserEnvironment;

	private Map<ParameterNames, String> configParameters = new EnumMap<ParameterNames, String>(ParameterNames.class);

	private static volatile Map<ITestContext, TestConfiguration> tConfigs = Collections.synchronizedMap(new HashMap<ITestContext, TestConfiguration>());

	private TestConfiguration(ITestContext context) {
		suite = context.getSuite().getXmlSuite();
		//log.debug("Creating TestConfiguration for test: " +context.getName());
		loadConfigProperties(context);
	}

	/**
	 * Returns a test local context configuration.
	 * 
	 * @return TestConfiguration
	 */
	static TestConfiguration getTestConfiguration(ITestContext context) {

		TestConfiguration tConfig;
		if (tConfigs.containsKey(context)) {
			tConfig = tConfigs.get(context);
		} else {
			tConfig = new TestConfiguration(context);
			tConfigs.put(context, tConfig);
		}
		return tConfig;
	}

	private void loadConfigProperties(ITestContext context) {

		// Cycles through expected Parameters and adds values from testng.xml (or default) to parameter map.
		this.configParameters = Utils.loadPropertyMapToEnum(context.getCurrentXmlTest().getAllParameters(), ParameterNames.class);

		// Identify test tool from .xml parameter
		for (TestTool testTool : TestTool.values()) {
			if (testTool.toString().equalsIgnoreCase(this.configParameters.get(ParameterNames.TEST_TOOL))) {
				this.testTool = testTool;
			}
		}
		if (this.testTool == null) throw new InvalidParameterException("TestTool not defined for: " + this.configParameters.get(ParameterNames.TEST_TOOL));

		// TODO: Validate parameters appropriately. Allow wildcards and setup web driver appropriately.
		String browserStartCommandDelimiter = configParameters.get(ParameterNames.BROWSER_START_COMMAND_DELIMITER);
		String[] browserStartCommand = configParameters.get(ParameterNames.BROWSER_START_COMMAND).split(browserStartCommandDelimiter);
		browserName = browserStartCommand[0];
		browserVersion = browserStartCommand[1];
		String browserEnvironmentOS = browserStartCommand[2];
		// OS version is needed when running on BrowserStack
		if (serverIsBrowserStack())  {
			if (browserStartCommand.length > 3) {
				setOsVersion(browserStartCommand[3]);			
			} else {
				log.error("OS version not provided in " + ParameterNames.BROWSER_START_COMMAND);
				throw new InvalidParameterException("Missing OS version");
			}
		}

		// Create browser environment.
		if (usingLocalBrowser()) {
			setBrowserEnvironment(new Environment(true, browserEnvironmentOS));
		} else {
			setBrowserEnvironment(new Environment(browserEnvironmentOS));
		}

		if (browserName.equalsIgnoreCase("Firefox") || browserName.equalsIgnoreCase("FF")) {
			setBrowser(BrowserType.FIREFOX);
		} else if (browserName.equalsIgnoreCase("InternetExplorer") || browserName.equalsIgnoreCase("IE")) {
			setBrowser(BrowserType.IE);
		} else if (browserName.equalsIgnoreCase("GoogleChrome") || browserName.equalsIgnoreCase("Chrome") || browserName.equalsIgnoreCase("GC")) {
			setBrowser(BrowserType.CHROME);
		} else if (browserName.equalsIgnoreCase("Opera") || browserName.equalsIgnoreCase("O")) {
			setBrowser(BrowserType.OPERA);
		} else if (browserName.equalsIgnoreCase("Safari") || browserName.equalsIgnoreCase("S")) {
			setBrowser(BrowserType.SAFARI);
		} else if (browserName.equalsIgnoreCase("HTMLUnit") || browserName.equalsIgnoreCase("html")) {
			setBrowser(BrowserType.HTML_UNIT);
		} else if (browserName.equalsIgnoreCase("Edge") || browserName.equalsIgnoreCase("mse")) {
				setBrowser(BrowserType.EDGE);
		} else {
			throw new InvalidParameterException("Browser could not be identified from: " + browserName);
		}

		setBrowserVersion(browserVersion);

		// File objects for IO to local machine
		this.setScreenshotsDir(new File(context.getOutputDirectory() + File.separator + "screenshots"));

		printProps();
	}

	/**
	 * Prints out all of the environment properties
	 */
	private static void printProps(Map<ParameterNames, String> configParameters) {

		LogManager.printPropertyMap(configParameters, "test configuration");
	}

	public void printProps() {
		printProps(this.configParameters);
	}

	public Environment getBrowserEnvironment() {

		return this.browserEnvironment;
	}

	private void setBrowserEnvironment(Environment env) {

		this.browserEnvironment = env;
	}

	public String getBrowserVersion() {

		return this.browserVersion;
	}

	private void setBrowserVersion(String browserVersion) {

		this.browserVersion = browserVersion;
	}
	
	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	/**
	 * 
	 * @return The server_host parameter stripped of any trailing "/", port number or 'http://'.
	 */
	public String getServerHost() {

		// TODO: this and browserHOst should be validated before prop print
		String serverHost = configParameters.get(ParameterNames.SERVER_HOST);
		if (serverHost.contains("//")) serverHost = serverHost.substring(serverHost.indexOf("//") + 2);
		if (serverHost.contains(":")) serverHost = serverHost.substring(0, serverHost.indexOf(":"));
		if (serverHost.endsWith("/")) serverHost = serverHost.substring(0, serverHost.length() - 1);
		return serverHost;
	}
	public String getBrowserstackUsername() {

		// TODO: this and browserHOst should be validated before prop print
		String browserstackUsername = configParameters.get(ParameterNames.BROWSERSTACK_USERNAME);
		if (browserstackUsername.contains("//")) browserstackUsername = browserstackUsername.substring(browserstackUsername.indexOf("//") + 2);
		if (browserstackUsername.contains(":")) browserstackUsername = browserstackUsername.substring(0, browserstackUsername.indexOf(":"));
		if (browserstackUsername.endsWith("/")) browserstackUsername = browserstackUsername.substring(0, browserstackUsername.length() - 1);
		return browserstackUsername;
	}

	public String getServerPort() {

		return this.configParameters.get(ParameterNames.SERVER_PORT);
	}
	public String getBrowserstackKey() {

		return this.configParameters.get(ParameterNames.BROWSERSTACK_KEY);
	}
	public String getSelenoidUsername() {

		return this.configParameters.get(ParameterNames.SELENOID_USERNAME);
	}
	public String getSelenoidKey() {

		return this.configParameters.get(ParameterNames.SELENOID_KEY);
	}

	public boolean browserIs(BrowserType browser) {

		return this.browser == browser ? true : false;
	}

	public BrowserType getBrowser() {

		return this.browser;
	}

	private void setBrowser(BrowserType browserType) {

		this.browser = browserType;
	}

	public boolean serverIsGridHub() { // Get if server is grid hub or not. Affects browser start command.

		return Boolean.parseBoolean(this.configParameters.get(ParameterNames.SERVER_IS_GRID_HUB));
	}
	
	public boolean serverIsBrowserStack() { // Get if server is grid hub or not. Affects browser start command.

		return Boolean.parseBoolean(this.configParameters.get(ParameterNames.SERVER_IS_BROWSERSTACK));
	}

	public boolean browserStackDebug() {

		return Boolean.parseBoolean(this.configParameters.get(ParameterNames.BROWSERSTACK_DEBUG));
	}
	
	public String getBrowserstacktimeout() {
		return (this.configParameters.get(ParameterNames.BROWSERSTACK_TIMEOUT));
	}
	
	// Get if the Selenium Grid is the old one
	public boolean serverIsLegacyGrid() {    

		return Boolean.parseBoolean(this.configParameters.get(ParameterNames.SERVER_IS_LEGACY_GRID));
	}

	public void updateBrowserURL(String url) {
		this.configParameters.put(ParameterNames.BROWSER_URL, url);
	}

	public String getBrowserURL() {

		String browserHost = this.configParameters.get(ParameterNames.BROWSER_URL);
		if (!browserHost.contains("//")) browserHost = "http://" + browserHost;
		if (!browserHost.endsWith("/")) browserHost = browserHost + "/";
		return browserHost;
	}
	
	public String getBrowserURL(String MT_URL) {
		String browserHost = "";
		if (MT_URL.equalsIgnoreCase("orga")) {
			browserHost = this.configParameters.get(ParameterNames.BROWSER_URL_MT_ORGA);
		} else if (MT_URL.equalsIgnoreCase("orgb")) {
			browserHost = this.configParameters.get(ParameterNames.BROWSER_URL_MT_ORGB);
		}
		
		if (!browserHost.contains("//"))browserHost = "http://" + browserHost;
		if (!browserHost.endsWith("/"))	browserHost = browserHost + "/";

		return browserHost;	
	}

	/**
	 * Determine if the browser used will be running locally. This may be running through a standalone server or directly. Do not use this when configuring web driver to determine 
	 * whether remote driver is required...There's nothing *wrong* with running against a command serverhosted locally.
	 * 
	 * @return true if the browser to be used will run on the local machine.
	 */
	private boolean usingLocalBrowser() {

		return ((getServerHost().equalsIgnoreCase("localhost") || getServerHost().equals("127.0.0.1"))
				&& !serverIsGridHub()) ? true : false;
	}

	public long getImplicitWait() {

		return Long.parseLong(this.configParameters.get(ParameterNames.TIMEOUT_IMPLICIT_WAIT));
	}

	public long getAsyncScriptTimeout() {

		return Long.parseLong(this.configParameters.get(ParameterNames.TIMEOUT_ASYNC_SCRIPT));
	}

	public long getPageNavigateTimeout() {

		return Long.parseLong(this.configParameters.get(ParameterNames.TIMEOUT_PAGE_NAVIGATE_TIMEOUT));
	}

	public boolean dojoPageLoadEnabled() {

		return Boolean.parseBoolean(this.configParameters.get(ParameterNames.PCH_ENABLE_DOJO_PAGELOAD));
	}

	public boolean standardPageLoadEnabled() {

		return Boolean.parseBoolean(this.configParameters.get(ParameterNames.PCH_ENABLE_STANDARD_PAGELOAD));
	}

	public long getPostEventPause() {

		return Long.parseLong(this.configParameters.get(ParameterNames.PCH_POST_EVENT_PAUSE));
	}

	public boolean forceScriptLocation() {

		return Boolean.parseBoolean(this.configParameters.get(ParameterNames.LOCATION_FORCE_SCRIPT));
	}

	public boolean useLocationFallback() {

		return Boolean.parseBoolean(this.configParameters.get(ParameterNames.LOCATION_USE_FALLBACK_STRATEGY));
	}

	private void setScreenshotsDir(File screenshotsDir) {

		this.screenshotsDir = screenshotsDir;
	}

	public File getScreenshotsDir() {

		return screenshotsDir;
	}

	public boolean testToolIs(TestTool testTool) {

		return this.testTool == testTool ? true : false;
	}

	public TestTool getTestTool() {
		return this.testTool;
	}

	public long getTypingDelay() {
		return Long.parseLong(this.configParameters.get(ParameterNames.TYPING_DELAY));
	}

	public String getBuildName() {
		String buildName = suite.getName() + "_" + browserName + browserVersion ;
		return buildName;
	}
		
	public boolean serverIsMT() { 

		return Boolean.parseBoolean(this.configParameters.get(ParameterNames.IS_MT));
	}
	
	public String useBrowserUrl_Mt_OrgA() {

		return (this.configParameters.get(ParameterNames.BROWSER_URL_MT_ORGA));
	}
	
	// returns true if browser_url is either the same as browser_url_mt_orga or browser_url_mt_orgb
	// typically happens when running MT BVT suite against a particular org using standalone deployment BVT.
	public boolean serverIsMTAsStandalone() {
		return getBrowserURL().equalsIgnoreCase(useBrowserUrl_Mt_OrgA()) ||
				getBrowserURL().equalsIgnoreCase(useBrowserUrl_Mt_OrgB());
	}
	
	public String useBrowserUrl_Mt_OrgB() {

		return (this.configParameters.get(ParameterNames.BROWSER_URL_MT_ORGB));
	}
	
	public String AuthenticationType() { 

		return this.configParameters.get(ParameterNames.AUTH_TYPE);
	}
}
