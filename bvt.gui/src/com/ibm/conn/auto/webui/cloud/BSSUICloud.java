package com.ibm.conn.auto.webui.cloud;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.google.common.base.Function;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.webui.ICBaseUI;

public class BSSUICloud extends ICBaseUI {
	
	public BSSUICloud(RCLocationExecutor driver) {
		super(driver);
	}
	
	protected static Logger log = LoggerFactory.getLogger(BSSUICloud.class);
	
	public static final String APPS_CISCO_JABBER = "Cisco Jabber";
	public static final String APPS_CISCO_WEBEX = "Cisco WebEx";
	public static final String APPS_CISCO_SPARK = "Cisco Spark";
	
	/** Start of selectors section*/
	public static String BSSNavbarAdminMenuBtn = "css=#bss-adminMenu";
	public static String BSSNavbarAdminManageOrganizationLink = "//a[contains(text(),'Manage Organization')]";
	
	public static String MyAccountSettings = "css=li[id='account_info'] a";
	
	public static String IntegratedApps = "css=li[id='cust_applications'] a";
	public static String IntegratedAppsEnableLink_noMobile = "css=tr:contains('${apps}'):not(:contains('Mobile')) a[id*=atnSubscribe]";
	public static String IntegratedAppsDisableLink_noMobile = "css=tr:contains('${apps}':not(:contains('Mobile'))) a[id*=atnUnsubscribe]";
	public static String IntegratedAppsEnableAllLink_noMobile = "css=tr:contains('${apps}'):not(:contains('Mobile')) a[id*=atnEnable]";
	public static String IntegratedAppsEnabled_noMobile = "css=tr:contains('${apps}'):not(:contains('Mobile')) a:contains(Enabled)";
	public static String IntegratedAppsEnableLink = "css=tr:contains('${apps}') a[id*=atnSubscribe]";
	public static String IntegratedAppsDisableLink = "css=tr:contains('${apps}') a[id*=atnUnsubscribe]";
	public static String IntegratedAppsEnableAllLink = "css=tr:contains('${apps}') a[id*=atnEnable]";
	public static String IntegratedAppsEnabled = "css=tr:contains('${apps}') a:contains(Enabled)";
	public static String EnableForAllRadioBtn = "css=input[value='all'][id*=-enableAll]";
	public static String EnableForIndividualRadioBtn = "css=input[value='individual'][id*=-enable]";
	public static String EnableApplicationSaveBtn = "css=input[id*='dlgSubscribe'][value='OK']";
	public static String EnableApplicationCancelBtn = "css=input[value='Cancel']";
	public static String DisableApplicationSaveBtn = "css=input[id*='dlgSubscribe'][value='OK']";
	
	public static String ChatAndMeetings = "css=li[id='webchat_config'] a";
	public static String ChatAndMeetingsDropDown = "css=select[class='st-config-dropdown']";
	public static String DisableConnectionsMeetingsCheckBox = "css=input[id*=_disableMeetings]";
	public static String WebExSiteName = "css=input[id*='_sitename']";
	public static String WebExSaveChangesBtn = "css=div[class*='webex-settings'] input[value='Save Changes']";
	public static String JabberSaveChangesBtn = "css=div[class*='jabber-settings'] input[value='Save Changes']";
	public static String SparkSaveChangesBtn = "css=div[class*='spark-settings'] input[value='Save Changes']";
	public static String CancelChangesBtn = "css=input[value='Cancel']";
	public static String ChatAndMeetingsAckBox = "css=div[id='lotusMessageNotificationText']";

	public static String JabberOptionText = Data.getData().JabberOptionText;
	public static String WebExOptionText = Data.getData().WebExOptionText;
	public static String SparkOptionText = Data.getData().SparkOptionText;
	public static String ChatAndMeetingsSaveSuccessAckText = Data.getData().ChatAndMeetingsSaveSuccessAckText;
	public static String CiscoJabberEnabledAck = Data.getData().CiscoJabberEnabledAck;
	public static String CiscoJabberSaveToDisableMsg = Data.getData().CiscoJabberSaveToDisableMsg;
	public static String CiscoJabberDisabledAck = Data.getData().CiscoJabberDisabledAck;
	public static String CiscoSparkEnabledAck = Data.getData().CiscoSparkEnabledAck;
	
	public static String IntegratedThirdPartyAppsDescription = Data.getData().IntegratedThirdPartyAppsDescription;
	public static String IntegratedThirdPartyAppsEnabledAck = Data.getData().IntegratedThirdPartyAppsEnabledAck;
	public static String IntegratedThirdPartyAppsDisabledAck = Data.getData().IntegratedThirdPartyAppsDisabledAck;	
	
	/**
	 * @param product - Only support cloud product
	 * @param driver
	 * @return new BSSUICloud
	 */
	public static BSSUICloud getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  BSSUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			throw new RuntimeException("Do not support product name: " + product);
		} else if(product.toLowerCase().equals("production")) {
			throw new RuntimeException("Do not support product name: " + product);
		} else if(product.toLowerCase().equals("vmodel")) {
			throw new RuntimeException("Do not support product name: " + product);
		} else if(product.toLowerCase().equals("multi")) {
			throw new RuntimeException("Do not support product name: " + product);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}	
	
	/**
	 * check if given application is enabled on Integrated Third-Party Apps page
	 * @param sApp - name of the application showing on Integrated Third-Party Apps page
	 * @return true if sApp is enabled, else false
	 */
	public boolean isIntegratedAppsEnabled(String sApp)	{
		String enabled = null;
		if(sApp.contains("Mobile")) {
			enabled = IntegratedAppsEnabled.replace("${apps}", sApp);
		}
		else {
			enabled = IntegratedAppsEnabled_noMobile.replace("${apps}", sApp);
		}
		return isElementPresent(enabled);
	}
	
	/**
	 * enable app on Integrated Third-Party Apps page, expected the app is disabled before call this method
	 * @param sApp - name of the application showing on Integrated Third-Party Apps page
	 * @param all - enable for all current users if true 
	 */
	public void enableIntegratedApps(String sApp, boolean all){
		String integratedAppsEnableLink = null;
		if(sApp.contains("Mobile")) {
			integratedAppsEnableLink = IntegratedAppsEnableLink.replace("${apps}", sApp);
		}
		else {
			integratedAppsEnableLink = IntegratedAppsEnableLink_noMobile.replace("${apps}", sApp);
		}
		clickLinkWait(integratedAppsEnableLink);
		if(all) {
			clickLinkWait(EnableForAllRadioBtn);
		} else {
			clickLinkWait(EnableForIndividualRadioBtn);
		}
		clickLinkWait(EnableApplicationSaveBtn);
		waitForPageLoaded(driver);
		Assert.assertTrue(isTextPresent(IntegratedThirdPartyAppsEnabledAck), "enable successfully");
	}
	
	/**
	 * disable app on Integrated Third-Party Apps page, expected the app is enabled before call this method
	 * @param sApp - name of the application showing on Integrated Third-Party Apps page
	 */
	public void disableIntegratedApps(String sApp) {
		String integratedAppsDisableLink = null;
		if(sApp.contains("Mobile")) {
			integratedAppsDisableLink = IntegratedAppsDisableLink.replace("${apps}", sApp);
		}
		else {
			integratedAppsDisableLink = IntegratedAppsDisableLink_noMobile.replace("${apps}", sApp);
		}
		clickLinkWait(integratedAppsDisableLink);
		clickLinkWait(DisableApplicationSaveBtn);
		waitForPageLoaded(driver);
		Assert.assertTrue(isTextPresent(IntegratedThirdPartyAppsDisabledAck), "diable successfully");
	}
		
	/**
	 * wait for chat and meetings page to be fully loaded
	 * @param driver
	 * @return true if chat and meetings page fully loaded
	 */
	public boolean waitForSTConfigDropdownAppear(RCLocationExecutor driver) {
		String fluentWaitTimeout = cfg.getFluentwaittime();
		log.info("INFO: setting the fluent wait timeout at: "+fluentWaitTimeout);
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
			.withTimeout(Long.valueOf(fluentWaitTimeout), TimeUnit.SECONDS)
			.pollingEvery(1, TimeUnit.SECONDS)
			.ignoring(ElementNotFoundException.class);

		return wait.until(new Function<RCLocationExecutor, Boolean>(){
		    public Boolean apply(RCLocationExecutor driver){       	    	                
	        	return  driver.executeScript("return document.readyState").equals("complete");
	    }});	
	}
	
	/**
	 * wait for specified option to be appeared on chat and meetings dropdown menu
	 * @param visibleText - text of the option that you want to check
	 * @param shouldVisible - should the option be visible
	 * @return true if shouldVisible is true and the option is visible, or shouldVisible is false and the option is not visible, else false
	 */
	public boolean waitForSTConfigDropdownMenu(String visibleText, boolean shouldVisible) {
		String timeout = cfg.getFluentwaittime();

		if(shouldVisible) {
			long startTime = System.currentTimeMillis();  
			long elapsedTime = 0;
			while (elapsedTime < Long.parseLong(timeout))
			{
				if(isElementPresent(BSSUICloud.ChatAndMeetingsDropDown)) {
					Select selectBox = new Select((WebElement) driver.getSingleElement(ChatAndMeetingsDropDown).getBackingObject());
					List<WebElement> options = selectBox.getOptions();
					for (WebElement option : options) {
						if(visibleText.equals(option.getAttribute("value")))
							return true;
					}
				} 
				
				try {
					driver.wait(3000);
				} catch (Exception e) {
					log.warn("ERROR: driver wait interrupted.");
				}
				elapsedTime = System.currentTimeMillis() - startTime;
			}
		} else {
			long startTime = System.currentTimeMillis();  
			long elapsedTime = 0;
			while (elapsedTime < Long.parseLong(timeout))
			{
				if(isElementPresent(BSSUICloud.ChatAndMeetingsDropDown)) {
					Select selectBox = new Select((WebElement) driver.getSingleElement(ChatAndMeetingsDropDown).getBackingObject());
					List<WebElement> options = selectBox.getOptions();
					for (WebElement option : options) {
						if(visibleText.equals(option.getAttribute("value"))) {
							try {
								driver.wait(3000);
							} catch (Exception e) {
								log.warn("ERROR: driver wait interrupted.");
							}
							elapsedTime = System.currentTimeMillis() - startTime;
						}
					}
				}
				return true;
			}
			
		}
		return false;

	}
	
	/**
	 * enable Cisco Jabber
	 * 1. click Admin>Manage Organization on Navbar
	 * 2. click Integrated Third-Party Apps
	 * 3. enable Cisco Jabber if it is not enabled
	 * 4. go to chat and meetings page
	 * 5. select Jabber Chat on dropdown
	 * 6. click Save on Jabber settings page
	 */
	public void enableJabberChat() {
		// open BSS ui
		clickLinkWithJavascript(BSSUICloud.BSSNavbarAdminMenuBtn);
		clickLinkWait(BSSUICloud.BSSNavbarAdminManageOrganizationLink);
		
		//open third party integrated Apps page
		clickLinkWait(BSSUICloud.IntegratedApps);
		fluentWaitTextPresent(IntegratedThirdPartyAppsDescription);
		if(!isIntegratedAppsEnabled(APPS_CISCO_JABBER)) 
			enableIntegratedApps(APPS_CISCO_JABBER, true);
		
		clickLinkWait(BSSUICloud.ChatAndMeetings);
		waitForSTConfigDropdownMenu(JabberOptionText, true);
		selectComboValue(BSSUICloud.ChatAndMeetingsDropDown, JabberOptionText);
		if(isTextPresent(CiscoJabberEnabledAck)) {
			return;
		}
		if(isElementPresent(JabberSaveChangesBtn)) {
			clickLinkWait(JabberSaveChangesBtn);
			Assert.assertTrue(getElementText(ChatAndMeetingsAckBox).equals(ChatAndMeetingsSaveSuccessAckText), 
					"INFO: Verify chat and meetings changes saved.");
		}
		Assert.assertTrue(isTextPresent(CiscoJabberEnabledAck), 
				"ERROR: Jabber Chat is not enabled.");	
	}
	
	/**
	 * disable Cisco Jabber
	 * 1. click Admin>Manage Organization on Navbar
	 * 2. click Integrated Third-Party Apps
	 * 3. disable Cisco Jabber if it is not disabled
	 * 4. go to chat and meetings page
	 * 5. select Jabber Chat on dropdown
	 * 6. click Save on Jabber settings page
	 */
	public void disableJabberChat() {
		// open BSS ui
		clickLinkWithJavascript(BSSUICloud.BSSNavbarAdminMenuBtn);
		clickLinkWait(BSSUICloud.BSSNavbarAdminManageOrganizationLink);
		
		//open third party integrated Apps page
		clickLinkWait(BSSUICloud.IntegratedApps);
		fluentWaitTextPresent(IntegratedThirdPartyAppsDescription);
		if(isIntegratedAppsEnabled(APPS_CISCO_JABBER)) 
			disableIntegratedApps(APPS_CISCO_JABBER);
		
		clickLinkWait(BSSUICloud.ChatAndMeetings);
		waitForSTConfigDropdownMenu(JabberOptionText, false);
		selectComboValue(BSSUICloud.ChatAndMeetingsDropDown, JabberOptionText);
		
		if(isElementPresent(JabberSaveChangesBtn)) {
			Assert.assertTrue(isTextPresent(CiscoJabberSaveToDisableMsg), 
					"INFO: Verify text is present '"+CiscoJabberSaveToDisableMsg+"'.");
			clickLinkWait(JabberSaveChangesBtn);
			Assert.assertTrue(getElementText(ChatAndMeetingsAckBox).equals(ChatAndMeetingsSaveSuccessAckText), 
					"INFO: Verify chat and meetings changes saved.");
		}
		Assert.assertTrue(isTextPresent(CiscoJabberDisabledAck), 
				"ERROR: Jabber Chat is not disabled.");			
	}
	
	/**
	 * enable Cisco Spark
	 * 1. click Admin>Manage Organization on Navbar
	 * 2. click Integrated Third-Party Apps
	 * 3. enable Cisco Jabber if it is not enabled
	 * 4. go to chat and meetings page
	 * 5. select Jabber Chat on dropdown
	 * 6. click Save on Jabber settings page
	 */
	public void enableSparkChat() {
		// open BSS ui
		clickLinkWithJavascript(BSSUICloud.BSSNavbarAdminMenuBtn);
		clickLinkWait(BSSUICloud.BSSNavbarAdminManageOrganizationLink);
		
		//open third party integrated Apps page
		clickLinkWait(BSSUICloud.IntegratedApps);
		fluentWaitTextPresent(IntegratedThirdPartyAppsDescription);
		if(!isIntegratedAppsEnabled(APPS_CISCO_SPARK)) 
			enableIntegratedApps(APPS_CISCO_SPARK, true);
		
		clickLinkWait(BSSUICloud.ChatAndMeetings);
		waitForSTConfigDropdownMenu(SparkOptionText, true);
		selectComboValue(BSSUICloud.ChatAndMeetingsDropDown, SparkOptionText);
		if(isTextPresent(CiscoSparkEnabledAck)) {
			return;
		}
		if(isElementPresent(SparkSaveChangesBtn)) {
			clickLinkWait(SparkSaveChangesBtn);
			Assert.assertTrue(getElementText(ChatAndMeetingsAckBox).equals(ChatAndMeetingsSaveSuccessAckText), 
					"INFO: Verify chat and meetings changes saved.");
		}
		Assert.assertTrue(isTextPresent(CiscoSparkEnabledAck), 
				"ERROR: Spark Chat is not enabled.");	
	}
	
	/**
	 * enable Cisco WebEx
	 * 1. click Admin>Manage Organization on Navbar
	 * 2. click Integrated Third-Party Apps
	 * 3. enable Cisco WebEx if it is not enabled
	 * 4. go to chat and meetings page
	 * 5. select WebEx Meetings on dropdown
	 * 6. click Save on WebEx settings page. if save failed, enter some text in site name field then click Save again
	 */
	public void enableWebExMeetings() {
		// open BSS ui
		clickLinkWithJavascript(BSSUICloud.BSSNavbarAdminMenuBtn);
		clickLinkWait(BSSUICloud.BSSNavbarAdminManageOrganizationLink);
				
		//open third party integrated Apps page
		clickLinkWait(BSSUICloud.IntegratedApps);
		fluentWaitTextPresent(IntegratedThirdPartyAppsDescription);
		if(!isIntegratedAppsEnabled(APPS_CISCO_WEBEX)) 
			enableIntegratedApps(APPS_CISCO_WEBEX, true);
				
		clickLinkWait(BSSUICloud.ChatAndMeetings);
		waitForSTConfigDropdownMenu(WebExOptionText, true);
		selectComboValue(BSSUICloud.ChatAndMeetingsDropDown, WebExOptionText);
				
		clickLinkWait(WebExSaveChangesBtn);
		if(getElementText(ChatAndMeetingsAckBox).equals(ChatAndMeetingsSaveSuccessAckText)) {
			Assert.assertTrue(getElementText(ChatAndMeetingsAckBox).equals(ChatAndMeetingsSaveSuccessAckText), 
				"INFO: Verify chat and meetings changes saved.");
		} else {
			typeText(WebExSiteName, "test.webex.com");
			clickLinkWait(WebExSaveChangesBtn);
			Assert.assertTrue(getElementText(ChatAndMeetingsAckBox).equals(ChatAndMeetingsSaveSuccessAckText), 
					"INFO: Verify chat and meetings changes saved.");
		}
	}
		
	/**
	 * To update "Disable Connections Meetings" setting in Chat and Meetings page
	 * requirement:
	 * logged in as admin user which org has WebEx enabled
	 * steps:
	 * 1. click Admin>Manage Organization on Navbar
	 * 2. click Chat and Meetings
	 * 3. click WebEx Meetings in dropdown
	 * 4. check/uncheck "Disable Connections Meetings" setting
	 * 5. click Save
	 * 6. verify "Disable Connections Meetings" setting has been updated
	 * @param check - true: check "Disable Connections Meetings" checkbox; false: uncheck "Disable Connections Meetings" checkbox
	 */
	public void checkWebExDisableConnectionsMeetings(boolean check){
		clickLinkWait(BSSNavbarAdminMenuBtn);
		clickLinkWait(BSSNavbarAdminManageOrganizationLink);
		clickLinkWait(ChatAndMeetings);
		waitForSTConfigDropdownAppear(driver);
		driver.getSingleElement(ChatAndMeetingsDropDown).useAsDropdown().selectOptionByVisibleText(WebExOptionText);
		log.info("INFO: selected WebEx Meetings in Chat and Meetings dropdown.");
		
		// get the current setting of "Disable Connections Meetings" checkbox.
		boolean checkboxIsSelected = driver.getFirstElement(DisableConnectionsMeetingsCheckBox).isSelected();
		// if the expected setting(check) is differ from current setting(checkboxIsSelected), click the checkbox to update it.
		if ((checkboxIsSelected && !check) || (!checkboxIsSelected && check)) {
			clickLinkWait(DisableConnectionsMeetingsCheckBox);		
			// save the setting
			clickLinkWait(WebExSaveChangesBtn);
			Assert.assertTrue(getElementText(ChatAndMeetingsAckBox).equals(ChatAndMeetingsSaveSuccessAckText), 
					"INFO: Verify chat and meetings changes saved.");
		}
		
		// verify the current setting is the same as expected setting(check)
		if (check)                                   
			Assert.assertTrue(driver.getFirstElement(DisableConnectionsMeetingsCheckBox).isSelected(), 
					"INFO: Connections Meetings is disabled.");
		else 
			Assert.assertFalse(driver.getFirstElement(DisableConnectionsMeetingsCheckBox).isSelected(), 
					"INFO: Connections Meetings is enabled.");
	}
}