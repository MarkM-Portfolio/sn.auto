package com.ibm.conn.auto.webui;

import static org.testng.Assert.assertTrue;

import java.awt.Robot;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.google.common.base.Function;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.RobotTypeClass;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.Data.Side;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.JavascriptEvent;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;

public abstract class ICBaseUI {
	
	private static Logger log = LoggerFactory.getLogger(ICBaseUI.class);
	
	protected RCLocationExecutor driver;
	protected TestConfigCustom cfg;
	
	public ICBaseUI(RCLocationExecutor driver) {
		this.driver = driver;
		this.cfg = TestConfigCustom.getInstance();
	}
	
	private List<String> onLoginScripts = new ArrayList<String>();
	
	public void addOnLoginScript(String script) {
		onLoginScripts.add(script);
	}

	public String currentComponent;
	
	public String sRemoveTagLinkinTagCloud(String sTag){
		return "css=a[title='Remove the tag " + sTag + " from the selected filter tags'].lotusDelete";
	}
	
	public RCLocationExecutor getDriver() {
		return driver;
	}

	public void loadComponent(String componentName) {
		loadComponent(componentName, false);
	}
	
	public void loadComponent(String url, String componentName) {
		loadComponent(url, componentName, false);
	}
	
	public void loadComponent(String componentName, boolean preserveInstance) {
		loadComponent(componentName, preserveInstance, null);
	}
	
	/**
	 * Load component and toggle to desire UX based on toggle flag value, except for Files and Communities and those support anonymous access
	 * @param compoenentName - component to load
	 * @param preserveInstance - if user wants to preserve the same session
	 * @param toggle - if user want to toggle to new UI
	 */
	public void loadComponentAndToggleUI(String componentName, boolean preserveInstance,boolean toggle) {		
		loadComponent(componentName, preserveInstance, null);
		CommonUICnx8 commonUiCnx8 = new CommonUICnx8(driver);
		commonUiCnx8.toggleNewUI(toggle);
		//TODO:cnx8ui - need to remove this refresh once toggle UI functionality is finalized and implemented. 
        ((WebDriver)driver.getBackingObject()).navigate().refresh();
	}
	
	public void loadComponent(String url, String componentName, boolean preserveInstance) {
		loadComponent(url, componentName, preserveInstance, null);
	}
	
	public void loadComponent(String componentName, User user) {
		loadComponent(componentName, false, user);
	}
	
	public void loadComponent(String url, String componentName, User user) {
		loadComponent(url, componentName, false, user);
	}
	
	public void loadComponent(String componentName, boolean preserveInstance, User user) {
		loadComponent(cfg.getTestConfig().getBrowserURL(), componentName, preserveInstance, user);
	}
	
	public void loadComponent(String url, String componentName, boolean preserveInstance, User user) {
		testInfo();
		if (cfg.getTestConfig().serverIsMT())  {
			log.info("INFO: Loading org: " + url);			
		}
		log.info("INFO: Loading the component: " + componentName);
		url = url + componentName;
		String securityType = cfg.getSecurityType();
		if(securityType.equalsIgnoreCase("false"))
		{	
			driver.load(url, preserveInstance);
		}
		else
		{
			// security type redirection
			// load the context but do not load the url in the browser
			// set the component name so it can be used within ivtLogin
			driver.load(url, preserveInstance,false);
			currentComponent = componentName;
		}
		
		//send fake url for performance team
		//String methodName = new Throwable().fillInStackTrace().getStackTrace()[2].getMethodName();
		//String className = new Throwable().fillInStackTrace().getStackTrace()[2].getClassName();
		//recordNodeIp("start", methodName, className);
		
		if(user != null) {
			log.info("INFO: Login using cookies with: " + user.getDisplayName());
			addLoginCookies(url, user);
			// login with redirection url fix - darren rabbitt
			if (!securityType.equalsIgnoreCase("false")) 
			{
				loadSecurityDeployment(securityType,cfg.getTestConfig().getBrowserURL(),user.getFirstName(),user.getPassword()); 
			}
			else
				driver.navigate().to(url);
		}
		
		int port;
		try {
			port = Integer.parseInt(cfg.getTestConfig().getServerPort());
		} catch(Exception e){
			port = 0;
		}
		//log.info("INFO: Running " + className + "_" + methodName + " on node: " + returnGridNodeName((WebDriver) driver.getBackingObject(), cfg.getTestConfig().getServerHost(), port));
		browserSetup();
		log.info("INFO: Browser and URL are loaded successfully");		
	}
	
	public void login(User loginUser) {
		HCBaseUI hc = new HCBaseUI(driver);
		String userName = loginUser.getAttribute(cfg.getLoginPreference());
		login(userName, loginUser.getPassword());
		if(hc.isElementVisibleWd(By.id("top-navigation"), 5))
		{
			log.info("INFO: Intentionally toggle after login");		
			hc.waitForElementVisibleWd(By.id("theme-switcher-wrapper"), 7);
			waitForJQueryToLoad(driver);
			
			hc.clickLinkWd(By.id("theme-switcher-wrapper"), "new UI toggle switch");
			hc.clickLinkWithJavaScriptWd(hc.findElement(By.cssSelector("#theme_switcher_options_modal_switch input")));
			hc.findElement(By.id("options_modal_save_button")).click();	
		}
	}
	
	/**
	 * Log in and toggle to desire UX based on use_new_ui switch in testTemplate xml
	 * @param loginUser
	 * @param switchToNewUI
	 */
	public void loginAndToggleUI(User loginUser, boolean switchToNewUI) {
		login(loginUser);
		CommonUICnx8 commonUI = new CommonUICnx8(driver);
		commonUI.toggleNewUI(switchToNewUI);
		//TODO:cnx8ui - need to remove this refresh once toggle UI functionality is finalized and implemented. 
    //    ((WebDriver)driver.getBackingObject()).navigate().refresh();
        waitForPageLoaded(driver);

	}

	/**
	 * Login to the product
	 * @param userName - the user for this test
	 * @param password - password for this user
	 * @author Conor Pelly 
	 */
	public void login(String userName, String password) {
		String loginType = cfg.getLoginType();
		log.info("INFO: Logging into "+loginType+" as user: "+userName);
	
		if (loginType.equalsIgnoreCase("onprem")){
			if (driver.getCurrentUrl().contains(Data.getData().ComponentMobile)){
				mobileLogin(userName, password);
			}else{
				commonLogin(userName, password);
			}
		}else if (loginType.equalsIgnoreCase("cloud")){
			scLogin(userName, password);
			waitForSameTime();
		}else if (loginType.equalsIgnoreCase("cloud_SBS")){
		     sbsLogin(userName,password);
		     waitForSameTime();
		}else if(loginType.equalsIgnoreCase("icsaml")){
			icsamlLogin(userName, password);	
		}	else if(loginType.equalsIgnoreCase("onprem_ivt")){
			ivtLogin(userName, password);
		}
		// Log BigIp
		WebDriver wd = (WebDriver) driver.getBackingObject();
		Set<Cookie> cookies = wd.manage().getCookies();
		for(Cookie c: cookies) {
			if(c.getName().contains("BIGipServer") && c.getName().contains("9081")) {
				log.info("INFO: BigIp cookie: " + c.getName() + " - " + c.getValue());
				break;
			}
		}
		waitForPageLoaded(driver);
		
		log.info("INFO: successfully logged into "+loginType+" ");
		
		for(String script: onLoginScripts) {
			driver.executeScript(script);
		}
	}
	
	private boolean addLoginCookies(String url, User user) {
		boolean success = false;
		try {
			boolean gotLtpaToken2 = false;
			String userName = user.getAttribute(cfg.getLoginPreference());
			for(Cookie c: Helper.executeJLogin(url, userName, user.getPassword())) {
				WebDriver wd = (WebDriver) driver.getBackingObject();
				wd.manage().addCookie(c);
				if(c.getName().equalsIgnoreCase("LtpaToken2"))
					gotLtpaToken2 = true;
			}
			if(!gotLtpaToken2) {
				log.info("LtpaToken2 was not returned trying to authenticate using API");
				return false;
			} else {
				success = true;
			}
		} catch (HttpException e) {
			log.info("HttpException thrown, authentication using cookies failed.");
			return false;
		} catch (IOException e) {
			log.info("IOException thrown, authentication using cookies failed.");
			return false;
		}
		
		return success;
	}
	
	
	private void commonLogin(String userName, String password) {
		//enter the username/password
		fluentWaitPresent(BaseUIConstants.USERNAME_FIELD);
		typeText(BaseUIConstants.USERNAME_FIELD, userName);
		typeText(BaseUIConstants.Password_FIELD, password);
		//Click the login button
		clickLinkWait(BaseUIConstants.Login_Button);
		
		// wait for the login page to disappear
		driver.turnOffImplicitWaits();
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 10);
		ExpectedCondition<Boolean> expected = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				RCLocationExecutor rcExec = getDriver();
				List<Element> usernameField = rcExec.getVisibleElements(BaseUIConstants.USERNAME_FIELD);
				return usernameField.size() == 0;
			}
		};
		wait.withMessage("Expect the login page is no longer shown").until(expected);
		driver.turnOnImplicitWaits();
	}
	
	private void scLogin(String userName, String password){

		//enter the username
		fluentWaitPresent(BaseUIConstants.USERNAME_FIELD);
		typeText(BaseUIConstants.USERNAME_FIELD, userName);
		//check to see if password field hidden if it is look for a continue button
		if (!driver.getSingleElement(BaseUIConstants.Password_FIELD).isVisible()){
			log.info("INFO: Cloud UI contains continue button.");
			clickLinkWait(BaseUIConstants.cloudLoginContinueButton);
		}
		
		//enter the password
		typeText(BaseUIConstants.Password_FIELD, password);
		//Click the login button
		clickLinkWait(BaseUIConstants.Login_Button);
		
	 }
	
	private void  sbsLogin(String userName, String password)
	{
		//enter the username
				fluentWaitPresent(BaseUIConstants.USERNAME_FIELD);
				typeText(BaseUIConstants.USERNAME_FIELD, userName);
				//check to see if password field hidden if it is look for a continue button
				if (!driver.getSingleElement(BaseUIConstants.Password_FIELD).isVisible()){
					log.info("INFO: Cloud UI contains continue button.");
					clickLinkWait(BaseUIConstants.cloudLoginContinueButton);
				}
				
				if (driver.isElementPresent(BaseUIConstants.SBS_Create_IBM_ID)){
					log.info("INFO: SBS cloud UI conatins Create IBM ID.");
					typeText(BaseUIConstants.USERNAME_FIELD, userName);
					clickLinkWait(BaseUIConstants.cloudLoginContinueButton);
				}	
				
			//enter the password
				typeText(BaseUIConstants.Password_FIELD, password);
				//Click the login button
				clickLinkWait(BaseUIConstants.Login_Button);
				
				if (driver.isElementPresent(BaseUIConstants.SBS_remember_Me)){
					log.info("INFO: Cloud UI contains Remember me radio button.");
					clickLinkWait(BaseUIConstants.cloudLoginContinueButton);
				}	
	}
	
	private void mobileLogin(String userName, String password) {
		//enter the username/password
		driver.getSingleElement(BaseUIConstants.Mobile_USERNAME_FIELD).type(userName);
		driver.getSingleElement(BaseUIConstants.Mobile_Password_FIELD).type(password);
		driver.getSingleElement(BaseUIConstants.Mobile_Login_Button).click();
		fluentWaitTextPresent("Status Updates");
	}
	
	private void icsamlLogin(String userName, String password) {
		if(driver.isElementPresent(BaseUIConstants.Login_Link))
			driver.getSingleElement(BaseUIConstants.Login_Link).click();
		fluentWaitTextPresent("User Login Page");
		driver.getSingleElement(BaseUIConstants.USERNAME_FIELD).type(userName);
		driver.getSingleElement(BaseUIConstants.Password_FIELD).type(password);
		driver.getSingleElement(BaseUIConstants.Login_Button).click();
		fluentWaitTextPresent("IBM Connections");
		fluentWaitTextPresent("Submit Feedback");
	}
	
	
	
	/* IVT login - for security deployments
	 * author: darren rabbitt
	 *  */
	
	private void ivtLogin(String userName, String password) {

		String securityType = cfg.getSecurityType();
		String config = cfg.getTestConfig().getBrowserURL();		
		if (!securityType.equalsIgnoreCase("false")) 
		{
			loadSecurityDeployment(securityType,config,userName,password); 
	    }
		else
			commonLogin(userName, password);
		
		
	}

	public void logout() {
		log.info("INFO: Click the Log Out link to logout");
		String loginType = cfg.getLoginType();
		WebDriver wd = (WebDriver) driver.getBackingObject();
		// Method to log out of Connections
		if (loginType.equalsIgnoreCase("icsaml")) {
			wd.manage().deleteAllCookies();
			driver.navigate().refresh();
		} else {
			JavascriptExecutor jse = (JavascriptExecutor) driver.getBackingObject();
			jse.executeScript("javascript:document.getElementById('logoutLink').click()");
			wd.manage().deleteAllCookies();
			// It has been observed that json is loaded upon relogin so adding artificial wait before refreshing
			sleep(500);
			driver.navigate().refresh();
		}
		log.info("INFO: Logging out");

	}
	
	public void logoutMobile() {
		log.info("INFO: Click the Log Out link to logout");
		//Method to log out of Connections
		driver.getFirstElement(BaseUIConstants.Logout_Link).click();
		log.info("INFO: Logged out");
		
	}
	
	/**Logout of the current user session & log back in to another user session*/
	@Deprecated
	public void logoutCurrentUserAndLoginDifferentUser(String UserDisplayName, User UserName)throws Exception{
		log.info("INFO: Logging in as a different user");
		//Logout the current user
		logout();
				
		//click the login link
		fluentWaitPresent(BaseUIConstants.Login_Link);
		clickLink(BaseUIConstants.Login_Link);
				
		login(UserName);
		log.info("INFO: the next user has being logged in successfully");
	}

	/** Prints out information about the browser, OS, computer and server that is running the test */
	public void testInfo() {
		String browserVersion = cfg.getTestConfig().getBrowserVersion();
		BrowserType browserName = cfg.getTestConfig().getBrowser();
		String OSName = System.getProperty("os.name");
		String computername;
		try {
			computername = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.info("WARNING: Getting local host name threw exception");
			computername = "Unknown Host";
		}
		String ServerName = cfg.getTestConfig().getBrowserURL();
		log.info("INFO: Test Server: "+ServerName);
		log.info("INFO: Test Client: "+computername);
		log.info("INFO: Test Client OS: "+OSName);
		log.info("INFO: Test Browser: "+browserName +" " +browserVersion);
		
	}
	
	/**
	 * Perform a click on the browser specify action during setup
	 *
	 */
	private void browserSetup() {
		//Perform some browser specify actions
		driver.maximiseWindow();
		if (cfg.getTestConfig().browserIs(BrowserType.IE) && driver.getTitle().equalsIgnoreCase("Certificate Error: Navigation Blocked")) {
			//click on the override link 
			driver.navigate().to("javascript:document.getElementById('overridelink').click()");
		}
	}
	
	private void registerEvent(JavascriptEvent event) {
		log.info("INFO: Register event: " + event.getEvent());
		driver.executeScript(event.getSetupScript());
	}
	
	private boolean waitForEvent(JavascriptEvent event) {
		log.info("INFO: Wait for event: " + event.getEvent());
		for(int i = 0; i < event.getWaitSeconds() * 2; i++) {
			try {
				if ((Boolean) driver.executeScript(event.hasFiredScript())) {
					log.info("INFO: Event '" + event.getEvent() + "' has fired.");
					driver.executeScript(event.getTearDownStript());
					return true;
				}
				sleep(500);
			} catch(ClassCastException e) {
				log.warn("WARN: Unable to cast output from event fired script.");
				return false;
			}
		}
		log.info("WARN: Event '" + event.getEvent() + "' did not fire in " + event.getWaitSeconds() + " seconds.");
		return false;
	}
	
	/**
	 * click on the selector that is passed into the method using getFirstVisibleElement()
	 * @param selector - the object to click
	 * @author Conor Pelly 
	 * @return 
	 * @throws Exception 
	 */
	public void clickLink(String selector){
		log.info("INFO: click action will be performed");
		this.getFirstVisibleElement(selector).click();
		log.info("INFO: clickLink was performed on: " +selector);
	}
	
	public void clickLinkWait(String selector){
		log.info("INFO: click action will be performed on selector: " + selector);
		this.fluentWaitPresent(selector);
		this.getFirstVisibleElement(selector).click();
		log.info("INFO: clickLink was performed on: " +selector);
	}
	
	public void clickWaitForEvent(Element element, JavascriptEvent event) {
		registerEvent(event);
		element.click();
		waitForEvent(event);
	}
	
	public void clickLinkWithJavascript(String selector) {
		log.info("INFO: javascript click action will be performed");
		WebElement element = null;
		try {
			element = (WebElement) this.getFirstVisibleElement(selector).getBackingObject();
			JavascriptExecutor jse = (JavascriptExecutor) driver.getBackingObject();
			jse.executeScript("arguments[0].click();", element);
		}catch(Exception e){
			log.info("Exception thrown: " + e);
			element = (WebElement) this.getFirstVisibleElement(selector).getBackingObject();
			JavascriptExecutor jse = (JavascriptExecutor) driver.getBackingObject();
			jse.executeScript("arguments[0].click();", element);
		}
		
		log.info("INFO: clickLinkWithJavascript was performed on: " +selector);
	}
	
	public void clickLinkWithAsyncJavascript(String selector) {
		log.info("INFO: asynchronous javascript click action will be performed");
		WebElement element = null;
		try {
			element = (WebElement) this.getFirstVisibleElement(selector).getBackingObject();
			driver.executeAsyncScript("arguments[0].click();", element);
		}catch(Exception e){
			log.info("Exception thrown: " + e);
			element = (WebElement) this.getFirstVisibleElement(selector).getBackingObject();
			driver.executeAsyncScript("arguments[0].click();", element);
		}
		
		log.info("INFO: clickLinkWithAsyncJavascript was performed on: " +selector);
	}
	
	public void blurWithJavascript(String selector) {
		log.info("INFO: javascript blur action will be performed");
		WebElement element = null;
		try {
			element = (WebElement) this.getFirstVisibleElement(selector).getBackingObject();
			driver.executeScript("arguments[0].blur();", element);
		}catch(Exception e){
			log.info("Exception thrown: " + e);
			element = (WebElement) this.getFirstVisibleElement(selector).getBackingObject();
			driver.executeScript("arguments[0].blur();", element);
		}
		
		log.info("INFO: blurWithJavascript was performed on: " +selector);
	}
	
	/**
	 * getElementText -
	 * @param selector
	 * @return String containing text from element
	 */
	public String getElementText(String selector){
		log.info("INFO: Get text using selector: " + selector);
		this.fluentWaitPresent(selector);
		return this.getFirstVisibleElement(selector).getText();
	}
	
	/**
	 * clear the selector that is passed into the method using getFirstVisibleElement()
	 * @param selector - the object to clear
	 * @author Conor Pelly 
	 */
	public void clearText(String selector) {
		// .clear doesn't clear input box in geckodriver so changed to use script instead
		driver.executeScript("arguments[0].value=''", (WebElement) this.getFirstVisibleElement(selector).getBackingObject());
		log.info("INFO: textfield or area has been cleared");
	}
	
	/**
	 * type text into the selector that is passed into the method using getFirstVisibleElement()
	 * @param selector - the object to type text
	 * @param text - text to be typed
	 * @author Conor Pelly 
	 */
	public void typeText(String selector, String text){
		this.getFirstVisibleElement(selector).type(text);
	}
	
	public void typeTextWithDelay(String selector, String text){
		this.getFirstVisibleElement(selector).typeWithDelay(text);
	}

	
	public void typeNativeInCkEditor(String text){
		log.info("INFO: enter text into the ckeditor");
		//typeNativeInCkEditor(text,"0");
		typeInCkEditor(text);
		log.info("INFO: entering text into the ckeditor");
	}
	
	/**
	 * Type text into the body for the CKEditor
	 * @param text - text that will be entered into the editor
	 * @param Index - index of the body of the editor - generally 0
	 * @author Conor Pelly 
	 */
	public void typeNativeInCkEditor(String text,String Index){
		/*
		 * Adding code to handle typing text into the CKEditor depending on the OS
		 * Windows - works
		 * Linux - should work now
		 * MAC - should work now
		 */
		if(cfg.getTestConfig().getBrowserEnvironment().isWindows()){
			//Click into the CK Editor and then type
			driver.getSingleElement(BaseUIConstants.CKEditor_iFrame+":nth("+Index+")").click();
			driver.getSingleElement(BaseUIConstants.CKEditor_iFrame+":nth("+Index+")").type(text);
		}else if(cfg.getTestConfig().getBrowserEnvironment().isLinux()){
			//Click into the CK Editor and then type
			driver.getSingleElement(BaseUIConstants.CKEditor_iFrame+":nth("+Index+")").click();
			try {
				typingTextUsingRobotClass(text);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}else if(cfg.getTestConfig().getBrowserEnvironment().isMac()){
			//Click into the CK Editor and then type
			driver.getSingleElement(BaseUIConstants.CKEditor_iFrame+":nth("+Index+")").click();
			try {
				typingTextUsingRobotClass(text);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Typing in CKEditor by clicking on the on html and natively typing stopped working on the grid
	 * after CKEditor was updated from 3.6 to 4.2.
	 * This method utilizes CKEditor javascript API to type
	 * @param text
	 * @author Ilya
	 */
	public void typeInCkEditor(String text) {
		clickInCkEditor();
		String jsQuotedText = StringEscapeUtils.escapeJavaScript(text);
		driver.executeScript("for(var i in CKEDITOR.instances) { var x = CKEDITOR.instances[i]; " + " x.setData('" + jsQuotedText + "'); }");
	}
	
	public void clearCkEditor() {
		waitForCkEditorReady();
		WebDriver wd = (WebDriver) driver.getBackingObject();
		switchToFrameBySelector(BaseUIConstants.ckEditorFrame);
		wd.findElement(By.xpath("//p")).clear();
		switchToTopFrame();
	}
	
	public void typeMentionInCkEditor(String text) {
		waitForCkEditorReady();
		WebDriver wd = (WebDriver) driver.getBackingObject();
		switchToFrameBySelector(BaseUIConstants.ckEditorFrame);
		fluentWaitPresent("//body");
		wd.findElement(By.xpath("//body")).sendKeys(text);
		switchToTopFrame();
		clickLinkWithJavascript(BaseUIConstants.searchlinkDropdown);
		
		try {
			selectUserFromTypeAheadSearchResult(text);
		} catch (StaleElementReferenceException e) {
			// type ahead list refreshed so let's try to select user again
			log.info("Type ahead list refreshed, will select user again again.");
			selectUserFromTypeAheadSearchResult(text);
		}
	}

	public void selectUserFromTypeAheadSearchResult(String text) {

		List<Element> surnames = driver.getElements(ActivitiesUIConstants.namesInList + ">b:last-child");
		for (Element surname : surnames) {
			if (text.endsWith(surname.getText())) {
				surname.doubleClick();
				break;
			}
		}

	}

	/**
	 * This method will click in ckEditor
	 */
	public void clickInCkEditor() {
		waitForCkEditorReady();
		WebDriver wd = (WebDriver) driver.getBackingObject();
		Actions action = new Actions(wd);
		String xpath= null;
		//if(driver.isElementPresent("xpath=//*[@class='cke_wysiwyg_frame cke_reset']")){
		if(driver.getFirstElement("xpath=//*[@class='cke_wysiwyg_frame cke_reset']").isVisible()){
			xpath = "//*[@class='cke_wysiwyg_frame cke_reset']";
		}
		else if(driver.isElementPresent("xpath=//*[@class='cke_inner cke_reset']")){
			xpath = "//*[@class='cke_inner cke_reset']";
		}
		
		scrollIntoViewElement(xpath);
		action.doubleClick(wd.findElement(By.xpath(xpath))).click().build().perform();
	}
	
	public void typingTextUsingRobotClass(String text)throws Exception{
		Robot r = new Robot();
		RobotTypeClass type = new RobotTypeClass(r);
		type.typeMessage(text);
		log.info("INFO: Used the Java robot class to type into the CK Editor");
	}

	public boolean isTextPresent(String text) {

		return driver.isTextPresent(text);
	}
	
	public boolean isElementPresent(String selector) {

		return driver.isElementPresent(selector);
	}
	
	public boolean isElementVisible(String selector){
		List<Element> elements = driver.getVisibleElements(selector);
		return !elements.isEmpty();
	}

	//finds all frames that match given selector, then switches to each until reaching
	//a frame that contains an element with the specified selector
	public void switchToFrame(String frameSelector, String selectorForElementWithinFrame){
		List<Element> frames = driver.getElements(frameSelector);
		
		for(Element e : frames){			
			driver.switchToFrame().selectFrameByElement(e);
			if(driver.isElementPresent(selectorForElementWithinFrame))
				return;
			else
				switchToTopFrame();
		}
		
		Assert.fail("unable to find frame with selector [" + frameSelector + "] that contains element [" + selectorForElementWithinFrame + "] ... try increasing sleep time before this method is called..." +
				"btw there were [" + frames.size() + "] matching frames");
		log.info("INFO: switched to frame: "+frameSelector+" ");
	}

	public void switchToTopFrame() {
		driver.switchToFrame().returnToTopFrame();
		log.info("INFO: switched back to the main frame");
	}
	
	public void switchToFrameByName(String FrameName) {
		log.info("INFO: switching to frame: "+FrameName+" ");
		driver.switchToFrame().selectSingleFrameBySelector("css=*[name='"+FrameName+"']");
		log.info("INFO: switched to frame: "+FrameName+" ");
	}
	
	public void switchToFrameBySelector(String Selector) {
		driver.switchToFrame().selectSingleFrameBySelector(Selector);
		log.info("INFO: switched to frame with selector: "+Selector+" ");
	}
	
	public void switchToFrameByTitle(String FrameTitle) {
		log.info("INFO: switching to frame: "+FrameTitle+" ");
		driver.switchToFrame().selectSingleFrameBySelector("css=*[title='"+FrameTitle+"']");
		log.info("INFO: switched to frame: "+FrameTitle+" ");
	}
	
	public void switchToNewTabByName(String Tabname){
		driver.switchToFirstMatchingWindowByPageTitle(Tabname);
	}
	
	public boolean switchToNextTab() {
		String currentWindow = driver.getWindowHandle();
		Set<String> allWindows = driver.getWindowHandles();
		allWindows.remove(currentWindow);
		if(allWindows.isEmpty()){
			log.info("Only one window found");
			return false;
		}
		driver.switchToWindowByHandle(allWindows.iterator().next());
		return true;
	}
	
	public void closeNewTabAndMoveToParentTab() {
		Set<String> test = driver.getWindowHandles();
		Iterator<String> itr = test.iterator();
		String parentWindow = driver.getWindowHandle();

		while (itr.hasNext()) {
			String secWindow = itr.next();

			if (!parentWindow.equalsIgnoreCase(secWindow)) {
				driver.switchToWindowByHandle(secWindow);
				driver.close();

			}
			driver.switchToWindowByHandle(parentWindow);
		}
	}
	
	public boolean dropdownContains(String dropdownSelector, String text) {
		List<Element> dropdownList = driver.getSingleElement(dropdownSelector).useAsDropdown().getOptions();
		for(Element e: dropdownList) {
			if(e.getText().equals(text)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method will select an element from dropdown by its value
	 * @param String - locatorForDropdown
	 * @param String - value
	 */
	public void selectFromDropdownWithValue(String locatorForDropdown, String value) {
		getFirstVisibleElement(locatorForDropdown).useAsDropdown().selectOptionByValue(value);
	}
	
	
	public void sleep(int duration) {

		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			return;
		}
	}

	public boolean fluentWaitNumberOfWindowsEqual(final int numberOfWindows){
		log.info("INFO: Entering fluentWait: Expected window count equal " + numberOfWindows);
		String fluentWaitTimeout = cfg.getFluentwaittime();
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(Long.valueOf(fluentWaitTimeout), TimeUnit.SECONDS)
			.pollingEvery(1000, TimeUnit.MILLISECONDS)
			.ignoring(NoSuchElementException.class);

        boolean foo = wait.until(new Function<RCLocationExecutor, Boolean>() 
        {
                public Boolean apply(RCLocationExecutor driver)
                {
                	return (driver.getWindowHandles().size() == numberOfWindows);
                }

        });
        driver.getTitle();
        return foo;              
    };
	
	public boolean fluentWaitPresent(final String locator){
		log.info("INFO: Entering fluentWait.locator: "+locator);
		String fluentWaitTimeout = cfg.getFluentwaittime();
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(Long.valueOf(fluentWaitTimeout), TimeUnit.SECONDS)
			.pollingEvery(1000, TimeUnit.MILLISECONDS)
			.ignoring(NoSuchElementException.class)
			.ignoring(TimeoutException.class)
			.ignoring(AssertionError.class);

        boolean foo = wait.until(new Function<RCLocationExecutor, Boolean>() 
        {
                public Boolean apply(RCLocationExecutor driver)
                {
                	return driver.isElementPresent(locator);
                }
                @Override
                public String toString(){
                	return String.format("\"%s\" locator ", locator);
                }
        });
        driver.getTitle();
        return foo;              
    }; 
    
    public boolean fluentWaitElementVisible(final String locator){
		log.info("Entering fluentWait.locator: "+locator);
		String fluentWaitTimeout = cfg.getFluentwaittime();
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(Long.valueOf(fluentWaitTimeout), TimeUnit.SECONDS)
			.pollingEvery(1000, TimeUnit.MILLISECONDS)
			.ignoring(NoSuchElementException.class);

        boolean foo = wait.until(new Function<RCLocationExecutor, Boolean>() 
        {
                public Boolean apply(RCLocationExecutor driver)
                {
                	boolean elementPresent = false;
                	
                    // FIXME this is confusing. This method is repeatedly called to check if wait should stop. 
                	// However driver.getSingleElement will throw exception after it checks existence of the element,
                	// which means it will always fail if the element is not present in the first poll. 
                	if(!elementPresent){elementPresent = driver.getSingleElement(locator).isVisible();}
                	return elementPresent;
                }
                @Override
                public String toString(){
                	return String.format("\"%s\" locator ", locator);
                }
        });
        driver.getTitle();
        return foo;              
    }; 
    
    /**
     * Wait for title to change
     * 
     * @param title
     * @return
     */
    public boolean fluentWaitTitleChange(final String title){
		log.info("Entering fluentWait title change: "+title);
		String fluentWaitTimeout = cfg.getFluentwaittime();
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(Long.valueOf(fluentWaitTimeout), TimeUnit.SECONDS)
			.pollingEvery(1000, TimeUnit.MILLISECONDS);

        boolean foo = wait.until(new Function<RCLocationExecutor, Boolean>() 
        {
                public Boolean apply(RCLocationExecutor driver)
                {
                	return !driver.getTitle().equalsIgnoreCase(title);
                }
        });
        driver.getTitle();
        return foo;              
    }; 
    
    /**
     * Wait for a popup/alert to be displayed on the screen
     * 
     * @param title
     * @return
     * @throws InterruptedException 
     */
    public boolean fluentWaitAlertDisplayed() throws InterruptedException{
    	boolean foo=false;
		log.info("Entering fluentWait for alert");
		int fluentWaitTimeout = Integer.parseInt(cfg.getFluentwaittime());
		 int i=0;
		   while(i++<fluentWaitTimeout)
		   {
		        try
		        {
		            String alert = driver.switchToAlert().getText();
		            log.info("Alert with text " + alert + " is displayed on the screen");
		            foo=true;
		            break;
		        }
		        catch(NoAlertPresentException e)
		        {
		          Thread.sleep(1000);
		          continue;
		        }
		   }
        return foo;              
    }; 
    
    /**
     * Version of fluentWaitElementVisible that will not throw an exception if the
     * locator matches more than one element on the page, but only one element is
     * visible.
     * 
     * @see #fluentWaitElementVisible(String)
     * @param locator
     * @return
     */
    public boolean fluentWaitElementVisibleOnce(final String locator){
		log.info("Entering fluentWait.locator: "+locator);
		String fluentWaitTimeout = cfg.getFluentwaittime();
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(Long.valueOf(fluentWaitTimeout), TimeUnit.SECONDS)
			.pollingEvery(1000, TimeUnit.MILLISECONDS)
			.ignoring(NoSuchElementException.class);

        boolean foo = wait.until(new Function<RCLocationExecutor, Boolean>() 
        {
                public Boolean apply(RCLocationExecutor driver)
                {
                	boolean elementPresent = false;
                	if(!elementPresent){
                		int nElements = driver.getVisibleElements(locator).size();
                		Assert.assertFalse(nElements > 1, "Too many elements visible for: {" +
                				locator + "}. " + nElements + " elements found, 1 element expected.");
                		elementPresent = (nElements > 0);
                	}
                	return elementPresent;
                }
                @Override
                public String toString(){
                	return String.format("\"%s\" locator ", locator);
                }
        });
        driver.getTitle();
        return foo;              
    };     
    
    public boolean fluentWaitTextPresent(final String... text){
    	log.info("Entering fluentWait.text: "+Arrays.toString(text));
    	String fluentWaitTimeout = cfg.getFluentwaittime();
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(Long.valueOf(fluentWaitTimeout), TimeUnit.SECONDS)
			.pollingEvery(1000, TimeUnit.MILLISECONDS)
			.ignoring(NoSuchElementException.class);

        boolean foo = wait.until(new Function<RCLocationExecutor, Boolean>() 
        {
                public Boolean apply(RCLocationExecutor driver)
                {
                	for(String t: text) if(driver.isTextPresent(t)) return true;
                	return false;
                }
                @Override
                public String toString(){
                	return String.format("\"%s\" text ", Arrays.toString(text));
                }
        });
        driver.getTitle();
        return foo;              
    }; 

    public boolean fluentWaitTextPresentRefresh(final String text){

    	String fluentWaitTimeout = cfg.getFluentwaittime();
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(Long.valueOf(fluentWaitTimeout), TimeUnit.SECONDS)
			.pollingEvery(1000, TimeUnit.MILLISECONDS)
			.ignoring(NoSuchElementException.class);

		boolean foo = false;
		//retry for resilience
		for(int x = 0; x < 5; x = x+1){
          	driver.navigate().refresh();  
          	driver.getTitle();        	
			try{
				foo = wait.until(new Function<RCLocationExecutor, Boolean>() 
		    	{
		    	   public Boolean apply(RCLocationExecutor driver)
		                {
		                	return driver.isTextPresent(text);
		                }
		               
		        });
			}catch(Exception e){
					log.warn("WARNING: Text "+ text +" not found");
			}			
			//if text is no longer present break out
			if (foo){
		    	 log.info("INFO: Text "+ text +" was found");
		    	 break;
		     }
		}
        driver.getTitle();		
        return foo;              
    };
    
    
    /**
     * fluentWaitTextNotPresent - added a driver navigate refresh to the wait to help improve the resilience of script wait
     * @param locator
     * @return boolean
     */
	public boolean fluentWaitTextNotPresent(final String text){
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(5, TimeUnit.SECONDS)
			.pollingEvery(1000, TimeUnit.MILLISECONDS)
			.ignoring(NoSuchElementException.class);
			
		boolean foo = false;
		//retry for resilience
		for(int x = 0; x < 5; x = x+1){
          	driver.navigate().refresh(); 
   			driver.getTitle();        	
			try{
				foo = wait.until(new Function<RCLocationExecutor, Boolean>() 
		    	{
		    	   public Boolean apply(RCLocationExecutor driver)
		                {
		                	return driver.isTextNotPresent(text);
		                }
		               
		        });
			}catch(Exception e){
					log.warn("WARNING: Text "+ text +" found");
			}			
			//if text is no longer present break out
			if (foo){
		    	 log.info("INFO: Text "+ text +" was not found");
		    	 break;
		     }
		}
        driver.getTitle();		
        return foo;              
    };
    
    /**
     * fluentWaitTextNotPresent - added a driver navigate to help improve the resilience of script wait
     * @param locator
     * @return boolean
     */
	public boolean fluentWaitTextNotPresentWithoutRefresh(final String text){
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(5, TimeUnit.SECONDS)
			.pollingEvery(1000, TimeUnit.MILLISECONDS)
			.ignoring(NoSuchElementException.class);
			
		boolean foo = false;
		//retry for resilience
		for(int x = 0; x < 5; x = x+1){ 
	   		driver.getTitle();     	
			try{
				foo = wait.until(new Function<RCLocationExecutor, Boolean>() 
		    	{
		    	   public Boolean apply(RCLocationExecutor driver)
		                {
		                	return driver.isTextNotPresent(text);
		                }
		               
		        });
			}catch(Exception e){
					log.warn("WARNING: Text "+ text +" found");
			}			
			//if text is no longer present break out
			if (foo){
		    	 log.info("INFO: Text "+ text +" was not found");
		    	 break;
		     }
		}
        driver.getTitle();		
        return foo;              
    };
    
    
    
    /**
     * fluentWaitPresentWithRefresh - added a driver navigate refresh to the wait to help improve the resilience of script wait
     * @param locator
     * @return boolean
     */
	public boolean fluentWaitPresentWithRefresh(final String locator){
		log.info("Entering fluentWait.locator: "+locator);
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(5, TimeUnit.SECONDS)
			.pollingEvery(1000, TimeUnit.MILLISECONDS)
			.ignoring(NoSuchElementException.class);
			
		boolean foo = false;
		//retry for resilience
		for(int x = 0; x < 5; x = x+1){
          	driver.navigate().refresh();
	   		driver.getTitle();         	
			try{
				foo = wait.until(new Function<RCLocationExecutor, Boolean>() 
		    	{
		    	   public Boolean apply(RCLocationExecutor driver)
		                {	
		                	return driver.isElementPresent(locator);
		                }
		                @Override
		                public String toString(){
		                	return String.format("\"%s\" locator ", locator);
		                }
		        });
			}catch(Exception e){
					log.warn("WARNING: Element "+ locator +" NOT found");
			}			
			//if we found what we are looking for break out
			if (foo){
		    	 log.info("INFO: Element "+ locator +" found");
		    	 break;
		     }
		}
        driver.getTitle();		
        return foo;              
    }; 
	 
    /**
     * Method to wait for document.readyState to report complete   
     * @param driver
     */
    public boolean waitForPageLoaded(RCLocationExecutor driver) {
    	int secToWait = 10;
    	driver.turnOffImplicitWaits();
    	try {
    		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), secToWait);
    		ExpectedCondition<Boolean> expected = new ExpectedCondition<Boolean>() {
    			@Override
    			public Boolean apply(WebDriver driver) {
    				driver.getTitle();	  
    				JavascriptExecutor js = (JavascriptExecutor) driver;  
    				return js.executeScript("return document.readyState").equals("complete");					 
    			}
    		};
    		wait.until(expected);
    	} catch (TimeoutException te)  {
    		log.error("Page is still loading after " + secToWait + " secs");
    		Assert.assertTrue(false, "Expect Page has finished loading after " + secToWait + " sec.");
    		return false;
    	}
    	driver.turnOnImplicitWaits();
    	return true;
    }
	 
	 public boolean waitForJQueryToLoad(RCLocationExecutor driver) {

		 final WebDriver wd = (WebDriver)driver.getBackingObject();
		 WebDriverWait wait = new WebDriverWait(wd, 30);
		 // wait for jQuery to load
		 ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			 @Override
			 public Boolean apply(WebDriver driver) {
				 try {
					 return ((Long)((JavascriptExecutor)wd).executeScript("return jQuery.active") == 0);
				 }
				 catch (Exception e) {
					 // no jQuery present
					 return true;
				 }
			 }
		 };
		 
		 return wait.until(jQueryLoad);
	 }

	public boolean waitForCkEditorReady() {
		String fluentWaitTimeout = cfg.getFluentwaittime();
		log.info("INFO: setting the fluent wait timeout at: "+fluentWaitTimeout);
			Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
					.withTimeout(Long.valueOf(fluentWaitTimeout), TimeUnit.SECONDS)
					.pollingEvery(1, TimeUnit.SECONDS)
					.ignoring(NoSuchElementException.class);

		   return wait.until(new Function<RCLocationExecutor, Boolean>(){

	    	        public Boolean apply(RCLocationExecutor driver){
	    	            driver.getTitle();
	    	        	return  driver.executeScript("return CKEDITOR.status").equals("loaded");
	    }});				
	}
		
	/** Simple Method for checking for errors on a page */
	public void checkForErrorsOnPage() {
		try{
			assertTrue(driver.isTextNotPresent("An error has occurred, contact your system Administrator"), "FAIL: Error message is displaying");
			assertTrue(driver.isTextNotPresent("Page not found"), "FAIL: Error message is displaying");
		}catch (Exception e){
			log.warn("WARNING: An error appeared on the UI - check the screen shot");
			driver.saveScreenshot("Unexpected Error in the UI");
		}
		
	}
	
	/**
     * 
     * Method to return the node that a test is running against.
     * 
     * @param dr - instance of WebDriver
     * @return - representing the string used to register the node
     * @author Liam Walsh
     */
    public String returnGridNodeName(WebDriver dr,String gridHost,Integer port){
    	
    	if (!cfg.getTestConfig().serverIsGridHub() || !cfg.getTestConfig().serverIsLegacyGrid()){
    		return cfg.getTestConfig().getServerHost();
    	}

    	return Helper.getGridNodeName(dr, gridHost, port);
    }

	public String returnIPAddress(String hostname)throws Exception{
		try {
			InetAddress inetAddr = InetAddress.getByName(hostname);
			byte[] addr = inetAddr.getAddress();

			// Convert to dot representation
			String ipAddr = "";
			for (int i = 0; i < addr.length; i++) {
				if (i > 0) {
					ipAddr += ".";
				}
				ipAddr += addr[i] & 0xFF;
				hostname = ipAddr.toString();
			}
			log.info("INFO: IP Address for the test node is: " + ipAddr);
		}
		catch (UnknownHostException e) {
			log.info("Host not found: " + e.getMessage());
		}
		return hostname;
	}

	/**
	 * replaceCookie -
	 * @param cookieName
	 * @param value
	 */
	public void replaceCookie(String cookieName, String value) {
		WebDriver wd = (WebDriver) driver.getBackingObject();
		Cookie cookie = wd.manage().getCookieNamed(cookieName);
		
		if(cookie == null) {
			log.info("WARN: Cookie " + cookieName + " not found");
		}
		else {
			log.info("INFO: Deleting cookie " + cookieName);
			wd.manage().deleteCookie(cookie);
		}
		
		log.info("INFO: Adding new cookie " + cookieName + " with value " + value);
		wd.manage().addCookie(new Cookie(cookieName, value));
		log.info("INFO: Cookie added");
	}
	
	public Side replaceProductionCookies() {
		if(Data.getData().testNode == null) {
			log.warn("WARN: node not specified in nodes properties, load balancer will be used");
			return null;
		} else if(Data.getData().testNode.equals("0")) {
			log.info("INFO: node 0 is specified in nodes properties, load balancer will be used");
			return null;
		}
		//Select side by finding last number in the ip address
		Side side = null;
		try {
			String subIp = Helper.getRequestString(cfg.getTestConfig().getBrowserURL() + "/validate/hostname.php");
			subIp = subIp.substring(subIp.indexOf(":") + 2);
			log.info("Last nomber of IP in /validate/hostname.php is : " + subIp);
			if(Data.getData().nodeIps.get(Side.A).contains(subIp)) {
				log.info("Side A selected");
				side = Side.A;
			} else if(Data.getData().nodeIps.get(Side.B).contains(subIp)) {
				log.info("Side B selected");
				side = Side.B;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("Failed to select side.");
		}
		
		if(side == null) {
			log.warn("WARN: Didn't find Side out of available IPs - cookies are not replaced.");
			return null;
		}
		
		String nodeIp = Data.getData().getNodeIp(side);
		if(nodeIp == null) {
			log.warn("WARN: node ip not specified in nodes properties for node "+Data.getData().testNode+", load balancer will be used");
			return null;
		}
		String cookie = "nodeSelect-ac";
		replaceCookie(cookie, nodeIp);
		return side;
	}
	
	public void validateSelectedNode(String url, Side side) {
		try {
			if(side == null) {
				log.info("Side was not selected, don't validate.");
				return;
			}
			String ip = Data.getData().getNodeIp(side);
			String script = "var xmlHttp = new XMLHttpRequest();" +
							"xmlHttp.open(\"GET\", \"" + url + "/validate/hostname.php\", false);" +
							"xmlHttp.send(null);" + 
							"return xmlHttp.responseText;";
			String response = driver.executeScript(script).toString();
			log.info("Node selected: " + response + ". IP attempted " + ip);
			String[] ipSplit = ip.split("\\.");
			if(ipSplit[ipSplit.length-1].equals(response.substring(response.indexOf(":") + 2))) {
				log.info("Server IP matches attempted IP.");
			} else {
				log.warn("Server IP does not match attempted IP - incorrect node is hit.");
			}
		} catch(Exception e) {
			e.printStackTrace();
			log.warn("Failed to validate selected node.");
		}
	}
	
	public void checkContinueAnnouncement(String continueSelector) {
		if(driver.isElementPresent(continueSelector))
			clickLink(continueSelector);
	}
	
	/**
	 * Get the name of the calling method (the "test" name) and print a trace message for
	 * the "Start of <test>".  
	 * 
	 * The test name is also returned as the value of this method for convenience.
	 * 
	 * @return							- the name of the current test (the method name of the caller)
	 * 
	 * @see #endTest()
	 * 
	 * @author	Ralph LeBlanc
	 */
	public String startTest() {
		String testName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
		log.info("INFO: ********** Beginning of test " + testName + " at " + new Date() + " **********");
		return startTestAux(testName);
	}
		
	/**
	 * Second variant of the startTest method. This version does not rely on stack trace information; instead it uses
	 * TestNG's parameter data. As such, it can be called from any method other than the test method.
	 * 
	 * @return							- the name of the current test (the method name of the caller)
	 * 
	 * @see #endTest()
	 */
	// https://examples.javacodegeeks.com/enterprise-java/testng/testng-beforemethod-example/
	public String startTest(Method testMethod, String tngTestName) {
		log.info("INFO: ********** Beginning of test method '" + testMethod.getDeclaringClass().getCanonicalName() + "." 
																+ testMethod.getName() + "' at '" + new Date() 
																+ "'; running within the context of test '" + tngTestName + "' **********");
		String testName = testMethod.getName();
		return startTestAux(testName);
	}
	
	public String startTest(String tngTestName) {
		log.info("INFO: ********** Beginning of test " + tngTestName + " at " + new Date() + " **********");
		return tngTestName;
	}

	public String startTestAux(String testName) {
		//Send data for performance team
		String url = cfg.getTestConfig().getBrowserURL().replace("https", "http") + "fakeurl/" + testName + "/start";
		if (cfg.isRecordPerformance())
			recordTestStartEnd(url);

		return testName;
	}
	
	/**
	 * Trace the normal "End of <test>".    
	 *  
	 * @see #startTest()
	 * 
	 * @author	Ralph LeBlanc
	 */	
	public void endTest() {
		String testName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
		
		String className = new Throwable().fillInStackTrace().getStackTrace()[1].getClassName();
		
		log.info("INFO: ********** End of test " + testName + " at " + new Date() + " **********");
		
		//Send data for performance team
//		recordNodeIp("end", testName, className);

	}
	
	/**
	 * Gathers video information and closes the browser for tests with multiple sessions
	 * 
	 * @param testConfig Configuration for current test
	 */
	public void close(TestConfigCustom cfg){
		Helper.endSession(driver, cfg);
		driver.close();
	}

	/**
	 * Second variant of the endTest method. This version does not rely on stack trace information; instead it uses
	 * TestNG's parameter data. As such, it can be called from any method other than the test method.
	 * 
	 * @see #startTest()
	 */
	// https://examples.javacodegeeks.com/enterprise-java/testng/testng-beforemethod-example/
	public void endTest(Method testMethod, String tngTestName) {
		log.info("INFO: ********** Ending of test method '" + testMethod.getDeclaringClass().getCanonicalName() + "." 
																+ testMethod.getName() + "' at '" + new Date() 
																+ "'; running within the context of test '" + tngTestName + "' **********");
		//Send data for performance team
		//recordNodeIp("end", testMethod.getName(), testMethod.getDeclaringClass().getCanonicalName() );
	}

	private void recordNodeIp(String startEnd, String testName, String className) {
		String pUrl;
		try {
			WebDriver wd = (WebDriver) driver.getBackingObject();
			String host = cfg.getTestConfig().getServerHost();
			int port = Integer.parseInt(cfg.getTestConfig().getServerPort());
			String nodeName = returnGridNodeName(wd,host,port);
			String ip = returnIPAddress(nodeName);
			String[] classNameSplit = className.split("\\.");
			pUrl = cfg.getTestConfig().getBrowserURL().replace("https", "http") + "fakeurl/" + classNameSplit[classNameSplit.length - 1] + "/" + testName + "/" + startEnd + "?ip=" + ip;
			if (cfg.isRecordPerformance())
				recordTestStartEnd(pUrl);
		} catch (Exception e1) {
			log.info("WARN: Failed to get grid node ip.");
		}
	}
	
	private void recordTestStartEnd(String url) {
		boolean success = Helper.sendGetRequest(url);
		String message;
		if(success)
			message = "Request to fakeurl successful";
		else
			message = "Request to fakeurl fail";
		log.info("INFO: " + message);
	}
	
	public void waitForSameTime() {
		//check if Sametime is enabled		
		if(cfg.isSametimeEnabled()){
			log.info("INFO: SameTime is enabled on this deployment");
			try {
				fluentWaitPresent(BaseUIConstants.STAvailability);
			} catch (Exception e) {
				log.warn("WARNING: Sametime message did not appear as expected.",e);
			}
			
		}else{
			log.info("INFO: Sametime not enabled on this deployment.");
		}
	}

	/**
	 * Select Menu option from top Mega Menu
	 * @param menuItem String Menu Option
	 */
	public void selectMegaMenu(String menuItem){
		log.info("INFO: Selecting Mega Menu " + menuItem);
		Element el = getFirstVisibleElement(menuItem);
		el.click();
		
	}
	
	/**
	 * Select Global Search menu option
	 * @param searchItem The title of the option in the drop down list to select
	 */
	public void selectGlobalSearch(String searchItem){
		log.info("INFO: Selecting Global Search Dropdown " + searchItem);
		driver.getFirstElement(BaseUIConstants.GlobalSearchBarDropdown).click();
		log.info("INFO: Selecting Global Search Option " + searchItem);
		Element el = getFirstVisibleElement(BaseUIConstants.GlobalSearchBarContainer + ":contains('" + searchItem + "')");
		el.click();
	}
	
	/**
	 * Selects a date from a subset of dates in the next or previous 18 months
	 * @param locator
	 */
	public void pickRandomDojoDate(String locator, boolean useCalPick) {

		//pick forward or backward 12 months at random
		int offsetMonths = ((int) (Math.floor(Math.random() * 2))) == 1 ? 12 : -12;
		
		//Generate random future month and date of month
		Calendar target = getBrowserLocalCurrentDate();
		long cts = target.getTimeInMillis();
		target.add(Calendar.MONTH, offsetMonths);
		long ots = target.getTimeInMillis();
		long randomTime = ots > cts ? ((long)(Math.random()*((ots - cts) + 1)+cts)) : ((long)(Math.random()*((cts - ots) + 1)+ots));	
		target.setTime(new Date(randomTime));
		
		log.info("INFO: Random Date: " + new SimpleDateFormat("d MMM yyyy").format(target.getTime()));
		pickDojoDate(locator, target, useCalPick);
	}
	
	
	
	/**
	 * Pick date either via text or calendar
	 * @param locator
	 * @param targetDate
	 */
	public void pickDojoDate(String locator, Calendar targetDate, boolean useCalPick) {

		log.info("INFO: Entering DojoDate picker");	
		log.info("INFO: Selecting date: " + new SimpleDateFormat("MM/dd/yyyy").format(targetDate.getTime()));

		log.info("INFO: Checking to see if we are using calendar picker "+ useCalPick);
		
		if(!useCalPick){
			log.info("INFO: Type Calendar Date");
			driver.getSingleElement(locator).clear();
			driver.getSingleElement(locator).type(new SimpleDateFormat("MM/dd/yyyy").format(targetDate.getTime()));
		}else{
			try{
				driver.getSingleElement(locator).click();
				
				//Select the Desired Month
				dojoDateSelectMonth(targetDate);
				
				//Select the Desired Year
				dojoDateSelectYear(targetDate, locator);
		
				//Select the Desired Day
				dojoDateSelectDay(targetDate);
			}
			catch (NoSuchElementException e){
				try{
					log.info("WARN: Calendar not found. Searching again.");
					driver.getSingleElement(locator).click();
					
					//Select the Desired Month
					dojoDateSelectMonth(targetDate);
					
					//Select the Desired Year
					dojoDateSelectYear(targetDate, locator);
			
					//Select the Desired Day
					dojoDateSelectDay(targetDate);
				}
				catch (NoSuchElementException el){
					throw el;
				}
			}
		}
		log.info("INFO: Date selection complete");
	}
	
	/**
	 * Select year for pickDojoDate
	 * @param targetDate
	 * @see pickDojoDate()
	 */
	private void dojoDateSelectYear(Calendar targetDate, String locator){
		
		int popUpYear, attempt = 0;
		boolean full = false;
		List<Element> year;
		Calendar currentDate = getBrowserLocalCurrentDate();
		String sPopUpYear;

		log.info("INFO: Target Calendar year: " + targetDate.get(Calendar.YEAR));
		log.info("INFO: Today Calendar year: " + currentDate.get(Calendar.YEAR));

		do {
			//Attempt to get the year from the calendar if it is visible. 
			year = driver.getVisibleElements(BaseUIConstants.DatePicker_CurrentSelected_Year);
			sPopUpYear = getsPopUpYearFromYear(year);
			
			//If the calendar is not visible, click on the calendar icon again and use visible elements
			//to get the sPopUpYear.
			if(sPopUpYear.equals("0")) driver.getSingleElement(locator).click();
			//If we have a valid year, we can exit from the for loop. 
			else full = true;
			
			attempt++;
		} while (!full && attempt < 3); 
		
		//Use calendar to get current year if the lists are never populated.		
		if (sPopUpYear.equals("0")) popUpYear = currentDate.get(Calendar.YEAR); 
		else{
			popUpYear = Integer.parseInt(sPopUpYear);
			
			//Select the correct year if the target year and the popUpYear are different through incrementally 
			//increasing or decreasing the popUpYear.
			while (targetDate.get(Calendar.YEAR) - popUpYear != 0) {
				if (targetDate.get(Calendar.YEAR) > popUpYear) {
					log.info("INFO: Selecting Next Year ");
					List<Element> yearPost = driver.getVisibleElements(BaseUIConstants.DatePicker_FollowingSelected_Year);
					if (yearPost.size() > 0) yearPost.get(yearPost.size()-1).click();		
					popUpYear+=1;
				}
				else {
					log.info("INFO: Selecting Previous Year ");
					List<Element> yearPrev = driver.getVisibleElements(BaseUIConstants.DatePicker_PreviousSelected_Year);
					if (yearPrev.size() > 0) yearPrev.get(yearPrev.size()-1).click();
					popUpYear-=1;
				}
			}
		}
	}
		
	/**
	 * Helper function for correctly assigning sPopUpYear.
	 * @param year the generated List from getVisibleElements.
	 * @return sPopUpYear
	 */	
	private String getsPopUpYearFromYear(List<Element> year){			
		if (!year.isEmpty()) return year.get(year.size()-1).getText();			
		else return "0";
	}
	
	/**
	 * Select month for pickDojoDate
	 * @param targetDate
	 * @see pickDojoDate()
	 */
	private void dojoDateSelectMonth(Calendar targetDate){

		log.info("INFO: Selecting month: " + new SimpleDateFormat("MMMM").format(targetDate.getTime()));		
		log.info("INFO: Collect all date fields and choose last date field");
		
		List<Element> dateFields = driver.getElements(BaseUIConstants.DatePicker_MonthLabel);
		dateFields.get(dateFields.size()-1).click();

		List<Element> Month = driver.getVisibleElements(BaseUIConstants.DatePicker_MonthOptions);
		Iterator<Element> nameList = Month.iterator();
		while(nameList.hasNext())
		{
			Element nameInList = nameList.next();
			log.info("INFO: Name " + nameInList.getText());
			if(nameInList.getText().contains(new SimpleDateFormat("MMMM").format(targetDate.getTime()))){
				log.info("Month selected " + nameInList.getText());
				nameInList.click();
				break;
			}
		}
	}
	
	/**
	 * Select day for pickDojoDate
	 * @param targetDate
	 * @see pickDojoDate()
	 */
	private void dojoDateSelectDay(Calendar targetDate){
		int dayOfMonth = targetDate.get(Calendar.DAY_OF_MONTH);
		log.info("INFO: Select day of month: " + dayOfMonth);
		
		List<Element> temp = driver.getVisibleElements(BaseUIConstants.DatePicker_CurrentMonth_Dates);
		
		if (temp.isEmpty()) throw new NullPointerException("Cannot find days of month.");
		else temp.get(Math.min(temp.size(), dayOfMonth) - 1).click(); 
		//Either chooses the correct day of month, or a smaller date to index correctly if temp does not register enough days. 
	}
	
	
	public Calendar getBrowserLocalCurrentDate(){
		return driver.getBrowserDatetime();
	}
	
	/*
	 * select value from select combo box
	 */
	public void selectComboValue(final String elementId, final String value) {
	    final Select selectBox = new Select((WebElement) driver.getSingleElement(elementId).getBackingObject());
	    selectBox.selectByVisibleText(value);
	}

	/**
	 * navigateMenuByID
	 * @param menuID
	 */
	public void navigateMenuByID(String menuID){
		try{
			JavascriptExecutor jse = (JavascriptExecutor) driver.getBackingObject();
			jse.executeScript("javascript:document.getElementById('" + menuID + "').click()");
		}catch (Exception e){
			log.error("ERROR: Unable to select Action menu");
		}
	}
	
    
	public Element getFirstVisibleElement(String selector){
		
		List<Element> elements = driver.getVisibleElements(selector);
		if(elements.isEmpty()){
			elements = driver.getVisibleElements(selector);
		}
		if(elements.isEmpty()){
			log.error("There are no visible elements matching selector: " + selector);
			throw new AssertionError("There are no visible elements matching selector: " + selector);
		}else{
			return elements.get(0);
		}
	}
	
    /** Click on the Create button in forms - try to have a single approach to this from now on */
    public void clickCreateButton(){
        String selector1 = BaseUIConstants.CreateButton;
        this.getFirstVisibleElement(selector1).click();
    }

    /** Click on the Save button in forms - try to have a single approach to this from now on */
	public void clickSaveButton(){
		String selector1 = BaseUIConstants.SaveButton;
		this.getFirstVisibleElement(selector1).click();
	}
	
	/** Click on the OK button in forms - try to have a single approach to this from now on */
	public void clickOKButton(){
		String selector1 = BaseUIConstants.OKButton;
		this.getFirstVisibleElement(selector1).click();
	}
	
	/** Click on the Save button in forms - try to have a single approach to this from now on */
	public void clickCancelButton(){
		String selector1 = BaseUIConstants.CancelButton;
		this.getFirstVisibleElement(selector1).click();
	}
	
	public void clickButton(String nameOfButton){
		String selector = "css=* input[value='"+nameOfButton+"']";
		this.getFirstVisibleElement(selector).click();
	}
	
	public String getMegaMenuApps(){
		return BaseUIConstants.MegaMenuApps;
	}
	
	
	/*
	 * Uses http client to retrieve location header for siteminder spenego redirection. (The location header and hostname are required)
	 * param url - the deployment url
	 * return ArrayList - with location header and hostname
	 * Darren Rabbitt
	 */
	
	public ArrayList<String> getLocationHeaders(String url)
	{
	url = url.replaceFirst("https:", "http:");
	HttpClient client = new HttpClient();
	ArrayList<String> headers = new ArrayList<String>();
	String location = "",hostname = "";

	HttpMethod get = new GetMethod(url);
	get.setDoAuthentication(true);

	try {
	    int status = client.executeMethod(get);
	    log.info("status is " + status);
	    
	    location = get.getQueryString();
	    hostname = get.getHostConfiguration().getHost();
	    log.info("Query String is " + location);
	    
	    
	    location = java.net.URLDecoder.decode(location, "UTF-8");
	    log.info("result is " + location);
	    log.info("hostname is " + hostname );
	    headers.add(location);
	    headers.add(hostname);

	} catch(HttpException ex) {
		log.error("ERROR: HttpException occurred connecting " + url);
	}
	catch (IOException ex)
	{
		log.error("ERROR: IOException occurred connecting to " + url);
	}
	finally {
		get.releaseConnection();
		
	}
	return headers;
	
	}
	
	
	/**
	 * Load an IVT Security Deployment    
	 * param securityType - type of security Deployment
	 * param url - deployment url
	 * param component - current component
	 * param username - username 
	 * param password - password
	 * 
	 * @author	Darren Rabbitt
	 */	
	public void loadSecurityDeployment(String securityType,String url,String username,String password)
	{
		if (securityType.equalsIgnoreCase("SITEMINDER_SPNEGO"))
		{
			ArrayList<String> headers = getLocationHeaders(url);
			String location = headers.get(0);
			String hostname = headers.get(1);
			if(location.length() > 0 && hostname.length() > 0)
			{
				String cred = "/siteminderagent/ntlm/creds.ntc?";
				url = "http://" + username + ":" + password + "@" + hostname + cred + location + "/" + currentComponent;
				log.info("Redirect URL for Siteminder spenego is " + url);
				driver.navigate().to(url);
			}
			else
				log.error("Error: Issue building redirect for URL" + url);
		}
		else if (securityType.equalsIgnoreCase("TAM_SPNEGO") || securityType.equalsIgnoreCase("SITEMINDER") || securityType.equalsIgnoreCase("TAM")|| securityType.equalsIgnoreCase("SAML")) 
		{
			url = url + currentComponent;
			WebDriver wd = (WebDriver) driver.getBackingObject();
			wd.navigate().to(url);
			commonLogin(username, password);
		}
	
	}
	
	/**
	 * 
	 * @param user - The user/community whose name will be selected from the typeahead
	 * @param typeahead - The typeahead from which the selection will be made
	 */
	public void typeaheadSelection(String user, String typeahead){

		//Collect all the options
		List<Element> options = driver.getVisibleElements(typeahead);
		
		//Iterate through the list and select the user/community from drop down
		Iterator<Element> iterator = options.iterator();
		while (iterator.hasNext()) {
			Element option = iterator.next();
			if (option.getText().contains(user + " ") || option.getText().endsWith(user) || option.getText().contentEquals(user)){
				log.info("INFO: Found user " + user);
				option.hover();
				option.click();
				break;
			}
		}
	}
	
	public void selectVisitingOrg(String orgName) {
		driver.getSingleElement(BaseUIConstants.VisitorMenu).click();
		
		List<Element> orgs = driver.getVisibleElements(BaseUIConstants.VisitorOrgsList);
		log.info(String.format("Found %d visiting orgs", orgs.size()));
		String name = null;
		for(Element org: orgs) {
			name = org.getText();
			log.info("Org found: " + name);
			if(orgName.equals(name)){
				log.info("Selecting Org: " + name);
				org.click();
				break;
			}
		}
	}
	
	public String getCloseTourScript() {
		return "var button = dojo.byId('close-tooltip'); if(button) { button.click(); }";
	}
	
	/**
	 * This method will check that gate keeper flag is on or off 
	 * returns 'true' if gate keeper flag is on; otherwise, false
	*/
	public boolean checkGKSetting(String gkFlag){
		
		log.info("INFO: Check to see if the Gatekeeper " + gkFlag + " setting is enabled");
		if (driver.getBackingObject() == null) {
			log.error("webdriver has not been created, start the browser first.");
			throw new IllegalStateException("webdriver not exists");
		}
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(driver);
		boolean value = gkc.getSetting(gkFlag);
		log.info("INFO: Gatekeeper flag " + gkFlag + " is " + value );
		return value;
	}
	
	/**
	 * Note: Need to load at least the login page (or actually logged in for MT) before calling this
	 */
	public Boolean isComponentPackInstalled() {
		return (Boolean) driver.executeScript("return lconn.core.config.services['extensionRegistry'] !== undefined");
	}	
	
	public void switchToOrgBURL(String serverBURL) {
		String url = driver.getCurrentUrl();
		String org1 = url.substring(url.indexOf("/") + 2, url.indexOf("."));
		String org2 = serverBURL.substring(serverBURL.indexOf("/") + 2, serverBURL.indexOf("."));
		log.info("Org1 is: " + org1);
		log.info("Org2 is: " + org2);
		url = url.replace(org1, org2);
		driver.navigate().to(url);
	}

	public void validateAccessDenied(String accessDeniedErrMsg, String noPermissionErrMsg) {

		log.info("Verify access denied error message should be displayed");
		fluentWaitElementVisible(BaseUIConstants.errorBox);
		Element ele1 = driver.getFirstElement(BaseUIConstants.AccessDenied);
		Element ele2 = driver.getFirstElement(BaseUIConstants.NoPermissionToAccess);
		String msg1 = ele1.getText();
		String msg2 = ele2.getText();
		log.info("1) Message is:" + msg1);
		log.info("2) Message is:" + msg2);

		Assert.assertEquals(msg1, accessDeniedErrMsg);
		Assert.assertEquals(msg2, noPermissionErrMsg);

	}

    public void scrolltoViewElement(WebElement element, WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView();", element);
    }
    
    public void scrollIntoViewElement(String selector) {
    	WebElement element = (WebElement) driver.getFirstElement(selector).getBackingObject();
    	WebDriver wd = (WebDriver) driver.getBackingObject();
        scrolltoViewElement(element, wd);
    }
    
    public void verifyTextColor(Element ele, String[] colorCode) {

		RemoteWebElement tagNameRWE = (RemoteWebElement) ele.getBackingObject();
		String color = tagNameRWE.getCssValue("color");
		String hexCode = Color.fromString(color).asHex();

		log.info("INFO: Hex value is  " + hexCode);
		List<String> expectedColorCode = new ArrayList<>();
		for (int i = 0; i < colorCode.length; i++) {
			expectedColorCode.add(colorCode[i]);
		}
		Assert.assertTrue(expectedColorCode.contains(hexCode));
	}
    
    public void dragAndDrop(Element source, Element target) throws Exception {
		Actions acts = new Actions(source.getWebDriverExecutor().wd());
		try {
			Action action;
			WebElement dragFrom = source.getWebElement();
			WebElement dragTo = target.getWebElement();

			// acts.dragAndDrop(dragFrom, dragTo).perform();
			acts.clickAndHold(dragFrom).moveByOffset(30, 30);
			action = acts.build();
			action.perform();

			acts.moveToElement(dragTo).build().perform();
			acts.release().perform();
		} catch (StaleElementReferenceException se) {
			throw se;
		}
	}
    
	/**
	 * This function will return component text before     
	 * a separator /
	 * @param String component name url
	 * @return component name without any text after forward slash
	 */	
    public String extractComponent(String component) {
      	return component.split("/")[0].trim();
    }
	
}
