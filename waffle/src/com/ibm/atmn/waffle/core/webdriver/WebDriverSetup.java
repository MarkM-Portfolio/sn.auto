package com.ibm.atmn.waffle.core.webdriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.base.BaseSetup;
import com.ibm.atmn.waffle.core.Environment;
import com.ibm.atmn.waffle.core.RunConfiguration;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.atmn.waffle.utils.FileIOHandler;
import com.ibm.atmn.waffle.utils.Utils;

import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;

/**
 * Handles creation and destruction of {@link WebDriver} instances.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 * 
 */
public class WebDriverSetup {

	private static final Logger log = LoggerFactory.getLogger(WebDriverSetup.class);

	private static Properties browserProps = FileIOHandler
			.loadExternalProperties("test_config/core/webdriver/browser.properties");

	private static FirefoxProfile createFirefoxProfile(TestConfiguration testConfig) {

		Properties firefoxProfileProps = FileIOHandler
				.loadExternalProperties("test_config/core/webdriver/firefox_profile.properties");
		FirefoxProfile firefoxProfile = new FirefoxProfile();

		// setEnableNativeEvents no longer exists in Selenium 3
		if (Boolean.parseBoolean(browserProps.getProperty("firefox_enable_native_events", "true"))) {
			firefoxProfile.setAlwaysLoadNoFocusLib(true);
		}

		Set<String> set = firefoxProfileProps.stringPropertyNames();
		for (String name : set) {
			String value = firefoxProfileProps.getProperty(name);
			if (Boolean.parseBoolean(value) || value.equals("false")) {
				firefoxProfile.setPreference(name, Boolean.parseBoolean(value));
			} else {
				boolean isInt = false;
				try {
					firefoxProfile.setPreference(name, Integer.parseInt(value));
					isInt = true;
				} catch (NumberFormatException nfe) {
				}
				if (!isInt) {
					firefoxProfile.setPreference(name, value);
				}
			}
		}

		firefoxProfile.setAssumeUntrustedCertificateIssuer(false);
		firefoxProfile.setAcceptUntrustedCertificates(true);
		firefoxProfile.setPreference("browser.download.folderList", 2);
		firefoxProfile.setPreference("browser.download.manager.showWhenStarting", false);
		if (testConfig.serverIsLegacyGrid())  {
			firefoxProfile.setPreference("browser.download.dir", testConfig.getBrowserEnvironment()
				.constructAbsolutePathToDirectoryFromRoot("SeleniumServer", "downloads"));
		} else {
			// ensure uniqueness for clean downloads folder
			firefoxProfile.setPreference("browser.download.dir",
					"/home/selenium/Downloads"+"/"+Utils.getThreadLocalUniqueTestName().replace(".", "_"));
		}
		firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"image/jpeg, image/png, text/csv, application/zip, application/force-download, application/pdf, text/plain, text/x-vcard");
		// notifications can steal focus while typing which causes mistyping and
		// false positives - this disables notifications
		firefoxProfile.setPreference("dom.webnotifications.enabled", false);
		firefoxProfile.setPreference("permissions.default.desktop-notification", 1);

		try {
			JavaScriptError.addExtension(firefoxProfile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return firefoxProfile;
	}
	
	private static ChromeOptions createChromeProfile(TestConfiguration testConfig) {

		ChromeOptions ChromeOptions = new ChromeOptions();
		ChromeOptions.setAcceptInsecureCerts(true);

		String downloadFilepath = testConfig.getBrowserEnvironment()
				.constructAbsolutePathToDirectoryFromRoot("SeleniumServer", "downloads");

		HashMap<String, Object> chromePreferences = new HashMap<String, Object>();
		if (testConfig.serverIsLegacyGrid())  {
			chromePreferences.put("download.default_directory", downloadFilepath);
		} else {
			// ensure uniqueness for clean downloads folder
			chromePreferences.put("download.default_directory", 
					"/home/selenium/Downloads"+"/"+Utils.getThreadLocalUniqueTestName().replace(".", "_"));
		}
		chromePreferences.put("profile.default_content_setting_values.notifications", 2);
		ChromeOptions.setExperimentalOption("prefs", chromePreferences);
		return ChromeOptions;
	}

	private static String[] getChromeSwitches() {

		ArrayList<String> switches = new ArrayList<String>();
		String[] frags = browserProps.getProperty("chrome_switches", "").split(";");
		for (String frag : frags) {
			if (frag != null && frag.length() > 0) {
				switches.add(frag);
			}
		}
		return switches.toArray(new String[0]);
	}
	
	private static EdgeOptions createEdgeProfile(TestConfiguration testConfig) {

		EdgeOptions edgeOptions = new EdgeOptions();
		edgeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		edgeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

		// Uncomment this if we need to use the legacy Selenium grid again
//		if (!testConfig.serverIsLegacyGrid())  {
			// ensure uniqueness for clean downloads folder
			HashMap<String, Object> edgePreferences = new HashMap<String, Object>();
			edgePreferences.put("download.default_directory",  
					"/home/selenium/Downloads"+"/"+Utils.getThreadLocalUniqueTestName().replace(".", "_"));

			edgePreferences.put("profile.default_content_settings.popups", 0);
			edgePreferences.put("profile.default_content_setting_values.notifications", 2);	
			HashMap<String, Object> prefs = new HashMap<String, Object>();
			prefs.put("prefs", edgePreferences);
			edgeOptions.setCapability("ms:edgeOptions", prefs);
			edgeOptions.setCapability("ms:edgeChromium", true);						
//		} else {
//			log.error("Edge is not supported in legacy grid!");
//			throw new RuntimeException("Edge is not supported in legacy grid!");
//		}

		return edgeOptions;
	}
	
	protected static WebDriver createDriver(TestConfiguration testConfig) {

		WebDriver driver;

		FirefoxProfile firefoxProfile = createFirefoxProfile(testConfig);

		if (testConfig.serverIsBrowserStack() || testConfig.serverIsGridHub()) {
			String nodeName = System.getProperty("nodeName", "").trim();
			//IE
			if (testConfig.browserIs(BrowserType.IE)) {
				log.info("Starting Remote Internet Explorer");
				DesiredCapabilities caps = setStandardCapabilities(testConfig, DesiredCapabilities.internetExplorer());
				caps.setCapability("nativeEvents", false);
				caps.acceptInsecureCerts();
				caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				if (!nodeName.isEmpty()) {
					caps.setCapability("applicationName", nodeName);
				}
				driver = new RemoteWebDriver(getRemoteURL(testConfig), caps);
			//Firefox
			} else if (testConfig.browserIs(BrowserType.FIREFOX)) {
				log.info("Starting Remote Firefox");
				DesiredCapabilities caps = setStandardCapabilities(testConfig, DesiredCapabilities.firefox());
				FirefoxOptions options = new FirefoxOptions();
				// For Debug:
				// options.setLogLevel(FirefoxDriverLogLevel.TRACE);
				options.setCapability("moz:webdriverClick", false);
				if (!testConfig.serverIsLegacyGrid() && !testConfig.serverIsBrowserStack() )  {
					String uniqueTestName = Utils.getThreadLocalUniqueTestName();
					options.setCapability("enableVNC", true);
					options.setCapability("enableVideo", true);
					options.setCapability("screenResolution", "1400x1024");
					options.setCapability("name", uniqueTestName.substring(0, uniqueTestName.length()-5));
					options.setCapability("videoName", uniqueTestName.replace(".", "_") + ".mp4");
					options.setCapability("enableLog",true);
					options.setCapability("logName", uniqueTestName.replace(".", "_") + ".log");
					options.setAcceptInsecureCerts(true);
				}
				options.merge(caps);
				if (!nodeName.isEmpty()) {
					options.setCapability("applicationName", nodeName);
				}
				options.setProfile(firefoxProfile);
				driver = new RemoteWebDriver(getRemoteURL(testConfig), options);
			//Chrome
			} else if (testConfig.browserIs(BrowserType.CHROME)) {
				log.info("Starting Remote GoogleChrome");
				DesiredCapabilities caps = setStandardCapabilities(testConfig, DesiredCapabilities.chrome());
				caps.setCapability("chrome.switches", Arrays.asList(getChromeSwitches()));
				if (!nodeName.isEmpty()) {
					caps.setCapability("applicationName", nodeName);
				}
				ChromeOptions ops=createChromeProfile(testConfig);
				caps.setCapability(ChromeOptions.CAPABILITY, ops);
				if (!testConfig.serverIsLegacyGrid() && !testConfig.serverIsBrowserStack() )  {
					String uniqueTestName = Utils.getThreadLocalUniqueTestName();
					caps.setCapability("enableVNC", true);
					caps.setCapability("enableVideo", true);
					caps.setCapability("screenResolution", "1400x1024");
					caps.setCapability("name", uniqueTestName.substring(0, uniqueTestName.length()-5));
					caps.setCapability("videoName", uniqueTestName.replace(".", "_") + ".mp4");
					caps.setCapability("enableLog",true);
					caps.setCapability("logName", uniqueTestName.replace(".", "_") + ".log");
					caps.setAcceptInsecureCerts(true);
				}
				driver = new RemoteWebDriver(getRemoteURL(testConfig), caps);
			//Safari
			} else if (testConfig.browserIs(BrowserType.SAFARI)) {
				log.info("Starting Remote Safari");
				DesiredCapabilities caps = setStandardCapabilities(testConfig, DesiredCapabilities.safari());
				if (!nodeName.isEmpty()) {
					caps.setCapability("applicationName", nodeName);
				}
				driver = new RemoteWebDriver(getRemoteURL(testConfig), caps);
			// Chromium Edge
			} else if (testConfig.browserIs(BrowserType.EDGE)) {
				log.info("Starting Remote Chromium Microsoft Edge");
				setStandardCapabilities(testConfig, DesiredCapabilities.edge());

				EdgeOptions ops=createEdgeProfile(testConfig);

				if (!testConfig.serverIsLegacyGrid() && !testConfig.serverIsBrowserStack() )  {
					String uniqueTestName = Utils.getThreadLocalUniqueTestName();
					
					if (!testConfig.getBrowserVersion().equals("0"))  {
						// specified version in capability required by Edge
						ops.setCapability("browserVersion", testConfig.getBrowserVersion());
					}
					ops.setCapability("enableVNC", true);
					ops.setCapability("enableVideo", true);
					ops.setCapability("screenResolution", "1280x1024");
					ops.setCapability("name", uniqueTestName.substring(0, uniqueTestName.length()-5));
					ops.setCapability("videoName", uniqueTestName.replace(".", "_") + ".mp4");
					ops.setCapability("enableLog",true);
					ops.setCapability("logName", uniqueTestName.replace(".", "_") + ".log");
				}
				driver = new RemoteWebDriver(getRemoteURL(testConfig), ops);
			//Other
			} else {
				log.error("RemoteWebDriver initialisation is not defined for this BrowserType: "
						+ testConfig.getBrowser());
				throw new RuntimeException("RemoteWebDriver initialisation is not defined for this BrowserType: "
						+ testConfig.getBrowser());
			}
		}else {//to run locally

			if (testConfig.browserIs(BrowserType.IE)) {
				log.info("Starting Internet Explorer");
				setIEDriverPath();
				driver = new InternetExplorerDriver();
			} else if (testConfig.browserIs(BrowserType.FIREFOX)) {
				log.info("Starting Firefox");
				setGeckoDriverPath();
				FirefoxOptions options = new FirefoxOptions();
				// For Debug:
				// options.setLogLevel(FirefoxDriverLogLevel.TRACE);

				// need to disable FF interactability check otherwise seeing
				// random button click failed
				// see
				// https://firefox-source-docs.mozilla.org/testing/geckodriver/Capabilities.html#moz-webdriverclick
				options.setCapability("moz:webdriverClick", false);
				options.setProfile(firefoxProfile);
				driver = new FirefoxDriver(options);
			} else if (testConfig.browserIs(BrowserType.CHROME)) {
				log.info("Starting GoogleChrome");
				setChromeDriverPath();
				ChromeOptions options=createChromeProfile(testConfig);
				options.addArguments(getChromeSwitches());
				driver = new ChromeDriver(options);
			} else if (testConfig.browserIs(BrowserType.OPERA)) {
				log.info("Starting Opera");
				DesiredCapabilities capabilities = DesiredCapabilities.opera();
				capabilities.setCapability("opera.port", -1);
				capabilities.setCapability("opera.profile", "");
				driver = new OperaDriver(capabilities);
			} else if (testConfig.browserIs(BrowserType.SAFARI)) {
				log.info("Starting Safari");
				DesiredCapabilities capabilities = DesiredCapabilities.safari();
				// capabilities.setCapability("safari.port", -1);
				// capabilities.setCapability("safari.profile", "");
				driver = new SafariDriver(capabilities);
			} else if (testConfig.browserIs(BrowserType.EDGE)) {
						log.info("Starting Edge");
						setEdgeDriverPath();
						DesiredCapabilities capabilities = DesiredCapabilities.edge();
						capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
						capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS,true);
						driver = new EdgeDriver(capabilities);
			} else {
				throw new RuntimeException(
						"WebDriver initialisation is not defined for this BrowserType: " + testConfig.getBrowser());
			}
		}
		return driver;
	}

	public static DesiredCapabilities setStandardCapabilities(TestConfiguration testConfig, DesiredCapabilities caps) {

		// Set Version
		// a version of 0 or less means wildcard version
		if (Float.parseFloat(testConfig.getBrowserVersion()) > 0) {
			caps.setVersion(testConfig.getBrowserVersion());
		}

		// Set OS if server is GridHub
		if(testConfig.serverIsGridHub()){
			if (testConfig.getBrowserEnvironment().isWindows()) {
				caps.setPlatform(Platform.WINDOWS);
			} else if (testConfig.getBrowserEnvironment().isLinux()) {
				caps.setPlatform(Platform.LINUX);
			} else if (testConfig.getBrowserEnvironment().isMac()) {
				caps.setPlatform(Platform.MAC);
			} else {
				log.error("WebDriver capabliliteis not defined for OS or no OS set.");
				throw new RuntimeException("WebDriver capabliliteis not defined for OS or no OS set.");
			}
		}		
		
		//Set Common Capabilities
		if(testConfig.serverIsBrowserStack()){
			String buildName = testConfig.getBuildName()+"_"+BaseSetup.getTimestamp();
			BaseSetup.browserStackProps.setProperty("buildName", buildName);
			
			if (testConfig.getBrowserEnvironment().isWindows()) {
				caps.setCapability("os", "Windows");
			}else if (testConfig.getBrowserEnvironment().isMac()) {
				caps.setCapability("os", "OS X");
			} else {
				log.error("WebDriver capabliliteis not defined for OS or no OS set.");
				throw new RuntimeException("WebDriver capabliliteis not defined for OS or no OS set.");
			}
			caps.setCapability("os_version", testConfig.getOsVersion());
			caps.setCapability("browserstack.maskCommands", "setValues");
			caps.setCapability("browserstack.local", "true");
			caps.setCapability("browserstack.localIdentifier", BaseSetup.getTimestamp());
			caps.setCapability("browserstack.selenium_version", "3.141.59");
			caps.setCapability("resolution", "1280x1024");
			caps.setCapability("project", "connections-sn.auto");
			caps.setCapability("browserstack.idleTimeout", testConfig.getBrowserstacktimeout());
			caps.setCapability("build",buildName);
			caps.setCapability("name",Utils.getThreadLocalUniqueTestName());
			caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			if(testConfig.browserStackDebug()){
				caps.setCapability("browserstack.debug", "true");				
			}
		}
		return caps;
	}

	/**
	 * 
	 * @return Server_host appropriately formatted (e.g.
	 *         http://[server_host]/wd/hub)
	 */
	public static URL getRemoteURL(TestConfiguration testConfig) {

		String address;
		String maskedAddress="";

		if(testConfig.serverIsBrowserStack()){
			address = "https://" + testConfig.getBrowserstackUsername() + ":" + testConfig.getBrowserstackKey() + "@hub-cloud.browserstack.com/wd/hub";	
		}
		else if(!testConfig.serverIsLegacyGrid() && !testConfig.serverIsBrowserStack() ) {
			if (!testConfig.getSelenoidUsername().isEmpty() && !testConfig.getSelenoidKey().isEmpty()) {
				// Selenoid GGR with security
				address = "http://" + testConfig.getSelenoidUsername() + ":" + testConfig.getSelenoidKey() + "@" + testConfig.getServerHost() + ":" + testConfig.getServerPort() + "/wd/hub";
			} else {
				address = "http://" + testConfig.getServerHost() + ":" + testConfig.getServerPort() + "/wd/hub";
			}
		} else if (testConfig.serverIsGridHub()) {
			address = "http://" + testConfig.getServerHost() + ":" + testConfig.getServerPort() + "/wd/hub";
		}else { // This seems to be the same as for grid hub now, but I will
					// leave this seemingly pointless if/else in case they
					// change the route again
			address = "http://" + testConfig.getServerHost() + ":" + testConfig.getServerPort() + "/wd/hub";
		}
		
		if(testConfig.serverIsBrowserStack()){
	         maskedAddress=address.replace((CharSequence)address.substring(address.indexOf("//")+2, address.indexOf(":",address.indexOf("//"))),"***************");
	         maskedAddress=maskedAddress.replace(maskedAddress.substring(maskedAddress.indexOf(":",maskedAddress.indexOf("//"))+1, maskedAddress.indexOf("@")),"***************");
	         log.info("Remote selenium server: " + maskedAddress);
		}else {
			log.info("Remote selenium server: " + address);
		}		
		
		try {
			URL targetUrl = new URL(address);
			return targetUrl;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException("A valid URL for remote selenium could not be formed from: " + address);
		}
	}

	/**
	 * Not in use , for future reference
	 * 
	 * Determines whether a local driver or instance of RemoteWebDriver should
	 * be used.
	 * 
	 * @param testConfig
	 * @return true if all configuration points to using local browser, false
	 *         otherwise.
	 */
	/*private static boolean useLocalDriver(TestConfiguration testConfig) {
		// This could really be done simply with a getServerPort.equals(""), but
		// by testing all conditions for localhost before using it, the
		// consequences of a misconfiguration are
		// less severe.
		return ((testConfig.getServerHost().equalsIgnoreCase("localhost")
				|| testConfig.getServerHost().equals("127.0.0.1")) && !testConfig.serverIsGridHub()
				&& testConfig.getServerPort().equals(""));
	}*/

	/**
	 * Sets the system property pointing to the chrome driver based on
	 * environment. Only applies when running locally of course.
	 */
	private static void setChromeDriverPath() {

		String chromeDriverFileName;
		File chromeDriverFolder = new File("resources/chromedriver");
		File chromeDriverFile = null;

		Environment localEnv = RunConfiguration.getInstance().getLocalEnvironment();

		if (localEnv.isWindows()) {
			chromeDriverFileName = "chromedriver_win.exe";
		} else if (localEnv.isLinux()) {
			if (localEnv.is64Bit()) {
				chromeDriverFileName = "chromedriver_linux_64";
			} else {
				chromeDriverFileName = "chromedriver_linux_32";
			}
		} else {
			chromeDriverFileName = "chromedriver_mac";
		}

		chromeDriverFile = new File(chromeDriverFolder, chromeDriverFileName);
		if (chromeDriverFile == null || !chromeDriverFile.exists() || !chromeDriverFile.canExecute()) {
			log.error("Error executing chrome driver at: " + chromeDriverFile.getAbsolutePath()
					+ ". The file may not exist or may not be executable.");
			throw new RuntimeException("Error executing chrome driver at: " + chromeDriverFile.getAbsolutePath());
		}

		// log.debug("Setting system property webdriver.chrome.driver to
		// chromedriver path " + chromeDriverFile.getAbsolutePath());
		System.setProperty("webdriver.chrome.driver", chromeDriverFile.getAbsolutePath());
	}

	private static void setIEDriverPath() {

		String ieDriverFileName;
		File ieDriverFolder = new File("resources/iedriver");
		File ieDriverFile = null;

		Environment localEnv = RunConfiguration.getInstance().getLocalEnvironment();

		assert localEnv
				.isWindows() : "Invalid configuration: The browser environment must be windows to use the IE driver.";

		if (localEnv.is64Bit()) {
			ieDriverFileName = "IEDriverServer_64.exe";
		} else {
			ieDriverFileName = "IEDriverServer_32.exe";
		}

		ieDriverFile = new File(ieDriverFolder, ieDriverFileName);
		if (ieDriverFile == null || !ieDriverFile.exists() || !ieDriverFile.canExecute()) {
			log.error("Error executing ie driver at: " + ieDriverFile.getAbsolutePath()
					+ ". The file may not exist or may not be executable.");
			throw new RuntimeException("Error executing ie driver at: " + ieDriverFile.getAbsolutePath());
		}

		// log.debug("Setting system property webdriver.ie.driver to ie-driver
		// path " + ieDriverFile.getAbsolutePath());
		System.setProperty("webdriver.ie.driver", ieDriverFile.getAbsolutePath());
	}

	/**
	 * Sets the system property pointing to the Gecko driver for Firefox.
	 */
	private static void setGeckoDriverPath() {

		String geckoDriverFileName;
		File geckoDriverFolder = new File("resources/geckodriver");
		File geckoDriverFile = null;

		Environment localEnv = RunConfiguration.getInstance().getLocalEnvironment();

		if (localEnv.isWindows()) {
			if (localEnv.is64Bit()) {
				geckoDriverFileName = "geckodriver_win_64.exe";
			} else {
				geckoDriverFileName = "geckodriver_win_32.exe";
			}
		} else if (localEnv.isLinux()) {
			if (localEnv.is64Bit()) {
				geckoDriverFileName = "geckodriver_linux_64";
			} else {
				geckoDriverFileName = "geckodriver_linux_32";
			}
		} else {
			geckoDriverFileName = "geckodriver_mac";
		}

		geckoDriverFile = new File(geckoDriverFolder, geckoDriverFileName);
		if (geckoDriverFile == null || !geckoDriverFile.exists() || !geckoDriverFile.canExecute()) {
			log.error("Error executing Gecko driver at: " + geckoDriverFile.getAbsolutePath()
					+ ". The file may not exist or may not be executable.");
			throw new RuntimeException("Error executing Gecko driver at: " + geckoDriverFile.getAbsolutePath());
		}

		System.setProperty("webdriver.gecko.driver", geckoDriverFile.getAbsolutePath());
	}
	
	private static void setEdgeDriverPath() {

		String edgeDriverFileName;
		File edgeDriverFolder = new File("resources/edgedriver");
		File edgeDriverFile = null;

		Environment localEnv = RunConfiguration.getInstance().getLocalEnvironment();

		if (localEnv.isWindows()) {
			if (localEnv.is64Bit()) {
				edgeDriverFileName = "msedgedriver_win_64.exe";
			} else {
				edgeDriverFileName = "msedgedriver_win_32.exe";
			}
		} else if (localEnv.isLinux()) {
			if (localEnv.is64Bit()) {
				edgeDriverFileName = "msedgedriver_linux_64";
			} else {
				edgeDriverFileName = "msedgedriver_linux_32";
			}
		} else {
			edgeDriverFileName = "msedgedriver_mac";
		}

		edgeDriverFile = new File(edgeDriverFolder, edgeDriverFileName);
		if (edgeDriverFile == null || !edgeDriverFile.exists() || !edgeDriverFile.canExecute()) {
			log.error("Error executing MS Edge driver at: " + edgeDriverFile.getAbsolutePath()
					+ ". The file may not exist or may not be executable.");
			throw new RuntimeException("Error executing MS Edge driver at: " + edgeDriverFile.getAbsolutePath());
		}

		System.setProperty("webdriver.edge.driver", edgeDriverFile.getAbsolutePath());
	}
}
