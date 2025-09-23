package com.ibm.conn.auto.util.eventBuilder.login;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.OrientMeUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.OrientMeUI;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * @author 	Anthony Cox
 * Date:	8th March 2016
 */

public class LoginEvents {

	private static Logger log = LoggerFactory.getLogger(LoginEvents.class);
	
	/**
	 * Logs the specified user into Connections -> Homepage
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent and login methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginToHomepage(HomepageUI ui, User userToLogin, boolean preserveInstance) {
		
		// Log into Homepage
		loginToComponent(ui, userToLogin, "Homepage", Data.getData().HomepageImFollowing, preserveInstance);
	}
	
	/**
	 * Login to Homepage, if default is OrientMe then switch to classic Homepage.
	 * @param ui
	 * @param omUI
	 * @param driver
	 * @param userToLogin
	 * @param preserveInstance
	 */
	public static void loginToClassicHomepage(HomepageUI ui, OrientMeUI omUI, RCLocationExecutor driver, 
			User userToLogin, boolean preserveInstance)  {
		loginToHomepage(ui, userToLogin, preserveInstance);
		ui.waitForPageLoaded(driver);
		driver.turnOffImplicitWaits();
		if (driver.isElementPresent(OrientMeUIConstants.topUpdates))  {
			log.info("Default Homepage is OrientMe, switch to classic Homepage");
			ui.clickLinkWait(ProfilesUIConstants.UserMenu);
			ui.clickLinkWait(OrientMeUIConstants.switchHomepage);
			ui.waitForPageLoaded(driver);
		}
		driver.turnOnImplicitWaits();
		ui.getCloseTourScript();
	}
	
	/**
	 * Logs the specified user into Connections -> Activities
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent and login methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginToActivities(HomepageUI ui, User userToLogin, boolean preserveInstance) {
		
		// Log into Activities UI
		loginToComponent(ui, userToLogin, "Activities", Data.getData().ComponentActivities, preserveInstance);
	}
	
	/**
	 * Logs the specified user into Connections -> Blogs
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent and login methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginToBlogs(HomepageUI ui, User userToLogin, boolean preserveInstance) {
		
		// Log into Blogs UI
		loginToComponent(ui, userToLogin, "Blogs", Data.getData().ComponentBlogs, preserveInstance);
	}
	
	/**
	 * Logs the specified user into Connections -> Communities
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent and login methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginToCommunities(HomepageUI ui, User userToLogin, boolean preserveInstance) {
		
		// Log into Communities UI
		loginToComponent(ui, userToLogin, "Communities", Data.getData().ComponentCommunities, preserveInstance);
	}
	
	/**
	 * PLEASE NOTE: Private access for now since there should never be a requirement to call this method externally
	 * 				Please reference the "loginToHomepage()" or "loginToBlogs()" methods in test cases instead
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent and login methods
	 * @param userTologin - The User instance of the user to be logged in
	 * @param componentName - The name of the component to log in to - eg. Homepage, Communities, Blogs
	 * @param dataComponent - The link to the Data class component for the login - eg. Data.getData().ComponentHomepage
	 * @param preserveInstance - A boolean value.  This will be false if this is the first login in the test case, but
	 * true for subsequent logins
	 */
	private static void loginToComponent(HomepageUI ui, User userToLogin, String componentName, String dataComponent, boolean preserveInstance){

		log.info("INFO: Logging into " + componentName + " with " + userToLogin.getDisplayName());
		log.info("INFO: Logging into " + componentName + " with " + userToLogin.getDisplayName());
		if(componentName.equals("Orient Me"))
		{
			ui.loadComponent(Data.getData().HomepageImFollowing);
			ui.login(userToLogin);
			HCBaseUI hc = new HCBaseUI(ui.getDriver());
			if(hc.isElementVisibleWd(By.id("top-navigation"), 5))
			{
				log.info("INFO: Intentionally toggle to CNX7 UI after login");		
				hc.waitForElementVisibleWd(By.id("theme-switcher-wrapper"), 4);
				hc.clickLinkWd(By.id("theme-switcher-wrapper"), "new UI toggle switch");
				hc.clickLinkWithJavaScriptWd(hc.findElement(By.cssSelector("#theme_switcher_options_modal_switch input")));
				hc.findElement(By.id("options_modal_save_button")).click();	
			}
			ui.loadComponent(dataComponent,true);

		}
		else
		{
			if(preserveInstance) {
				ui.loadComponent(dataComponent, true);
			} else {
				ui.loadComponent(dataComponent);
			}
			ui.loginAndToggleUI(userToLogin, 
					TestConfigCustom.getInstance().getUseNewUI());
		}
		
	} 
	
	/**
	 * Logs the specified user into Connections -> Homepage and then navigates to the I'm Following view
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent, login and goto methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginAndGotoImFollowing(HomepageUI ui, User userToLogin, boolean preserveInstance) {
		
		// Log the user into Connections -> Homepage
		loginToHomepage(ui, userToLogin, preserveInstance);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
	}
	
	/**
	 * Logs the specified user into Connections -> Homepage and then navigates to the Discover view
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent, login and goto methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginAndGotoDiscover(HomepageUI ui, User userToLogin, boolean preserveInstance) {

		// Log the user into Connections -> Homepage
		loginToHomepage(ui, userToLogin, preserveInstance);

		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
	}

	/**
	 * Logs the specified user into Connections -> Homepage and then navigates to the Status Updates view
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent, login and goto methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginAndGotoStatusUpdates(HomepageUI ui, User userToLogin, boolean preserveInstance) {

		// Log the user into Connections -> Homepage
		loginToHomepage(ui, userToLogin, preserveInstance);

		// Navigate to the Status Updates view
		UIEvents.gotoStatusUpdates(ui);
	}

	/**
	 * Logs the specified user into Connections -> Homepage and then navigates to the My Notifications view
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent, login and goto methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginAndGotoMyNotifications(HomepageUI ui, User userToLogin, boolean preserveInstance) {

		// Log the user into Connections -> Homepage
		loginToHomepage(ui, userToLogin, preserveInstance);

		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
	}

	/**
	 * Logs the specified user into Connections -> Homepage and then navigates to the Mentions view
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent, login and goto methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginAndGotoMentions(HomepageUI ui, User userToLogin, boolean preserveInstance) {

		// Log the user into Connections -> Homepage
		loginToHomepage(ui, userToLogin, preserveInstance);

		// Navigate to the Mentions view
		UIEvents.gotoMentions(ui);
	}
	
	/**
	 * Logs the specified user into Connections -> Homepage and then navigates to the Saved view
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent, login and goto methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginAndGotoSaved(HomepageUI ui, User userToLogin, boolean preserveInstance) {
		
		// Log the user into Connections -> Homepage
		loginToHomepage(ui, userToLogin, preserveInstance);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
	}
	
	/**
	 * Logs the specified user into Connections -> Homepage and then navigates to the Action Required view
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent, login and goto methods
	 * @param userToLogin - The User instance of the user to be logged in
	 */
	public static void loginAndGotoActionRequired(HomepageUI ui, User userToLogin, boolean preserveInstance) {
		
		// Log the user into Connections -> Homepage
		loginToHomepage(ui, userToLogin, preserveInstance);
		
		// Navigate to the Saved view
		UIEvents.gotoActionRequired(ui);
	}
	
	/**
	 * Logs the current user out of Connections
	 * 
	 * @param ui - The HomepageUI instance to invoke the logout() method
	 */
	public static void logout(HomepageUI ui) {
		
		log.info("INFO: Ensure that the users 'Account Settings' icon is displayed before attempting to log out");
		ui.fluentWaitPresent(HomepageUIConstants.User_Navbar_AccountSettingsMenu);
		
		log.info("INFO: Now logging out of Connections");
		ui.logout();
	}
	
	/**
	 * Returns to the Home screen in Connections and then logs out
	 * 
	 * @param ui - The HomepageUI instance to invoke the goto and logout methods
	 */
	public static void gotoHomeAndLogout(HomepageUI ui) {
		
		// Return to the Home screen in the UI
		UIEvents.gotoHome(ui);
		
		// Log out from Connections
		logout(ui);
	}
	
	/**
	 * Logs the specified user into Connections -> Orient me
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent and login methods
	 * @param userToLogin - The User instance of the user to be logged in 
	 * @param driver - The RCLocationExecutor instance
	 * @param responseIcon - css selector for 'Responses to my content' element
	 * @param preserveInstance - A boolean value.  This will be false if this is the first login in the test case, but
	 * 				true for subsequent logins
	 */
	public static void goToOrientMe(HomepageUI ui, User userToLogin, RCLocationExecutor driver, String responseIcon, boolean preserveInstance) {
		
		// Log the user into Connections -> Orient Me
		loginToComponent(ui, userToLogin, "Orient Me", Data.getData().ComponentOrientMe, preserveInstance);
		confirmOrientMeLogin(ui, driver, responseIcon);
	}
	
	/**
	 * Logs the specified user into Connections -> Touchpoint view
	 * 
	 * @param ui - The HomepageUI instance to invoke the loadComponent and login methods
	 * @param userToLogin - The User instance of the user to be logged in 
	 * @param driver - The RCLocationExecutor instance
	 * @param welcomeScreen - css selector for Welcome message element
	 * @param preserveInstance - A boolean value.  This will be false if this is the first login in the test case, but
	 * 				true for subsequent logins
	 */
	
	public static void goToTouchpoint(HomepageUI ui, User userToLogin, RCLocationExecutor driver, String welcomeScreen, boolean preserveInstance) {
		
		// Log the user into Connections -> Touchpoint
		loginToComponent(ui, userToLogin, "Touchpoint", Data.getData().ComponentTouchpoint, preserveInstance);
		confirmTouchpointLogin(ui, driver, welcomeScreen);
	}

	/**
	 * This method calls goToOrientMe method and then click om go to latest update tab.
	 * It closes guided tour popup.
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userToLogin - user to login as
	 * @param driver - The RCLocationExecutor instance to invoke the isElementPresent(), navigate(),turnOnImplicitWaits(), turnOffImplicitWaits() methods to close the browser instance
	 * @param responseIcon - css selector for 'Responses to my content' element
	 * @param latestUpdate - css selector for 'Latest Update Tab' element
	 * @param preserveInstance - A boolean value.  This will be false if this is the first login in the test case, but
	 * 			true for subsequent logins
	 */
	public static void loginAndGoToOMLatestUpdatesTab(HomepageUI ui, User userToLogin, RCLocationExecutor driver, String responseIcon, String latestUpdate, boolean preserveInstance) {
		
		// Log the user into Connections -> Orient Me -> Latest Update Tab
		goToOrientMe(ui, userToLogin, driver, responseIcon, preserveInstance);
		ui.clickLinkWait(latestUpdate);
		ui.getCloseTourScript();
	}
	
	/**
	 * This method calls goToOrientMe method and then goes to the Top Updates tab if not already there.
	 * It closes guided tour popup.
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param omUI - The OrientMeUI instance to invoke all relevant methods
	 * @param userToLogin - user to login as
	 * @param driver - The RCLocationExecutor instance to invoke the isElementPresent(), navigate(),turnOnImplicitWaits(), turnOffImplicitWaits() methods to close the browser instance
	 * @param preserveInstance - A boolean value.  This will be false if this is the first login in the test case, but
	 * 			true for subsequent logins
	 */
	public static void loginAndGoToOMTopUpdatesTab(HomepageUI ui, OrientMeUI omUI, 
			User userToLogin, RCLocationExecutor driver, boolean preserveInstance) {
		
		// Log the user into Connections -> Orient Me -> Top Updates Tab
		goToOrientMe(ui, userToLogin, driver, OrientMeUIConstants.responseIcon, preserveInstance);
		Element topUpdateTab = ui.getFirstVisibleElement(OrientMeUIConstants.topUpdates);
		
		if (omUI.isTabSelected(topUpdateTab)) {
			topUpdateTab.click();
		}
		ui.getCloseTourScript();
	}

	/**
	 * For server which OM is not the default homepage, it goes to the classic Homepage upon login.
	 * This method hits the OM url again after login.
	 * TODO: go to OM by using the option under the user avatar.
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke the isElementPresent(), navigate(),turnOnImplicitWaits(), turnOffImplicitWaits() methods to close the browser instance
	 * @param responseIcon - css selector for 'Responses to my content' element
	 */
	public static void confirmOrientMeLogin(HomepageUI ui, RCLocationExecutor driver, String responseIcon) {
		TestConfigCustom cfg = TestConfigCustom.getInstance();
		log.info("INFO: Navigating to Orient Me view");
		driver.turnOffImplicitWaits();
		if (!driver.isElementPresent(responseIcon))  {
			log.info("Switch to OM: " + cfg.getTestConfig().getBrowserURL()+Data.getData().ComponentOrientMe);
			driver.navigate().to(cfg.getTestConfig().getBrowserURL()+Data.getData().ComponentOrientMe);
			ui.waitForPageLoaded(driver);
		}
		driver.turnOnImplicitWaits();
		ui.getCloseTourScript();
	}
	
	/**
	 * For the existing user who do not get touchpoint(on-boarding) screens by default, it goes to the homepage upon login.
	 * This method hits the Touchpoint url again after login.
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke the isElementPresent(), navigate(),turnOnImplicitWaits(), turnOffImplicitWaits() methods to close the browser instance
	 * @param welcomescreen - css selector for welcome message element
	 */
	public static void confirmTouchpointLogin(HomepageUI ui, RCLocationExecutor driver, String welcomescreen) {
		TestConfigCustom cfg = TestConfigCustom.getInstance();
		log.info("INFO: Navigating to On-boarding wizard");
		driver.turnOffImplicitWaits();
		if (!driver.isElementPresent(welcomescreen))  {
			log.info("Switch to Touchpoint: " + cfg.getTestConfig().getBrowserURL()+Data.getData().ComponentTouchpoint);
			driver.navigate().to(cfg.getTestConfig().getBrowserURL()+Data.getData().ComponentTouchpoint);
			ui.waitForPageLoaded(driver);
		}
		driver.turnOnImplicitWaits();
	}
}
