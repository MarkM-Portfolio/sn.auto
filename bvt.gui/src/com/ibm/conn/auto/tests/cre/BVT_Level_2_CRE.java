/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.cre;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.CREData;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;

public class BVT_Level_2_CRE extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_CRE.class);
	

	private CommunitiesUI ui;
	private HomepageUI hUI;
	private TestConfigCustom cfg;	
	private User testUser;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(),driver);
		hUI = HomepageUI.getGui(cfg.getProductName(),driver);
		
	}
	
	// Gadget
	public static String TestURLTextBox = "name=gadgetUrl";
	public static String LoadGadgetButton = "name=loadGadgetButton";
	public static String OutputFrame = "//span[contains(@id,'gadget')]//iframe[contains(@id, '__gadget_gadget')]";
	public static String ContentTextBox = "id=com_ibm_lconn_gadget_test_lib_BootstrapEE_0eeDataModel";
	public static String testPassedEEHelloWorldDiv = "id=eeContext";
	public static String testPassedHelloWorldGadgetDiv = "id=testData";
	public static String testPassedOS_PeopleDiv = "id=output";

	// iWidget
	public static String iWidgetpreFillStockButton = "//button[@onclick='preFillStockIwidget();']";
	public static String iWidgetloadButton = "//button[@onclick='load_iwidget();']";
	public static String iWidgettitleInputTextBox = "id=titleInput";
	public static String iWidgetchangeTitleButton = "id=changeTitleButton";
	public static String iWidgetTitleText = "id=title";
	public static String iWidgetStockChart = "id=chartImage";

	// Sharebox
	public static String sbStatusFrame = "//iframe[contains(@id, '__gadget_gadget')]";
	public static String sbGlobalLink = "id=sbOpenLinkg2";
	public static String sbCommunityLink = "id=sbOpenLinkc1";
	public static String sbStatusText = "css=textarea.lotusText";
	public static String sbMentionsStatusText = "css=div[id^='mentionstextAreaNode_']";
	public static String sbPostButton2 = "//input[@value='Post']";
	public static String sbSuccessfulxpath = "css=div.lotusMessage.lotusConfirm";
	public static String sbSuccessfulPost = "message was successfully";
	public static String sbFailPost = "error occurred while posting the status";

	// Proxy access radio
	public static String ProxyAccessRadioIntranetAccess = "id=proxyAccessIntranet";
	public static String ProxyAccessRadioExternalOnly = "id=proxyAccessExternalOnly";

	// Feature access radio
	public static String FeatureAccessRadioRestricted = "id=featureAccessRestricted";
	public static String FeatureAccessRadioTrusted = "id=featureAccessTrusted";
	public static String FeatureAccessRadioTrustedSSO = "id=featureAccessSSO";
	public static String FeatureAccessRadioEverything = "id=featureAccessKitchenSink";

	// Render Mode radio
	public static String RenderModeRadiorenderModeEE = "id=renderModeEE";
	public static String RenderModeRadiorenderModeSharedialog = "id=renderModeSharedialog";
	public static String RenderModeRadiorenderModeHomepage = "id=renderModeHomepage";
	

	/**
	*<ul>
	*<li><B>Info: </B>Authenticated User loading of Gadget(s): EE Hello World and OS People</li>
	*<li><B>Step: </B>Log into Homepage</li>
	*<li><B>Step: </B>Load the CRE test page</li> 
	*<li><B>Step: </B>Enter URL for EE Hello World Gadget</li>
	*<li><B>Step: </B>Enter text into EE Data Context field</li>
	*<li><B>Step: </B>Go through variations of radio options for Proxy Access, Feature Access and Render Mode</li>
	*<li><B>Step: </B>For each variation, click Load Gadget Button</li>
	*<li><B>Verify: </B>The Render Preview area displays expected content</li>
	*<li><B>Step: </B>Repeat last 2 steps and verify until all variations are exhausted</li>
	*<li><B>Step: </B>Enter OS People Gadget URL into Gadget URL field</li>
	*<li><B>Step: </B>Go through variations of radio options for Proxy Access, Feature Access and Render Mode</li>
	*<li><B>Step: </B>For each variation, click Load Gadget Button</li>
	*<li><B>Verify: </B>The Render Preview area displays User ID, Display Name or Email</li>
	*</ul>
	*/ 
	
	@Test(groups = { "level1", "level2", "mt-exclude" })
	public void testGadgetLoads() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();

		// Load the homepage component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load homepage and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser);
	
		// Add another wait for element since this test has started to fail
		// due to premature load of CRE test page when Homepage is still loading
		hUI.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);
		log.info("Homepage is loaded");
		
		// Load CRE test page and start testing
		logger.strongStep("Load CRE test page");
		log.info("Load the CRE test page");
		driver.load(testConfig.getBrowserURL() + Data.getData().ComponentCRE, true);
		
		//Test loading of EE Hello World Gadget
		logger.strongStep("Test how 'EE Hello World' gadget loads");
		EEHelloWorldGadget();
		
		//Test loading of People Gadget
		logger.strongStep("Test how 'People' gadget loads");
		OS_peopleGadget();
		
		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Anonymous loading of Gadget(s): OS People</li>
	*<li><B>Step: </B>Log into Homepage</li>
	*<li><B>Step: </B>Click Log out link</li> 
	*<li><B>Step: </B>Load the CRE test page</li>
	*<li><B>Step: </B>Enter OS People Gadget URL into Gadget URL field</li>
	*<li><B>Step: </B>Set first radio option for each: Proxy Access, Feature Access and Render Mode</li>
	*<li><B>Step: </B>Click Load Gadget Button</li>
	*<li><B>Verify: </B>The Render Preview area doesn't displays User ID, Display Name or Email</li>
	*</ul>
	*/ 
	@Test(groups = { "level2", "bvt", "mt-exclude", "bvtcloud" })
	public void testGadgetLoggedOut() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		// Load the homepage component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load homepage and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		logger.strongStep("Login and logout to maintain anonymity");
		ui.login(testUser);
		// log out as this is an anonymous test
		ui.logout();
		if(cfg.getSecurityType().equalsIgnoreCase("false"))
			ui.fluentWaitPresent(BaseUIConstants.Login_Button);

		// Load CRE test page and start testing
		logger.strongStep("Load CRE test page");
		log.info("Load the CRE test page");
		driver.load(testConfig.getBrowserURL() + Data.getData().ComponentCRE, true);
		
		logger.strongStep("Enter 'OS People Gadget' url into 'Gadget URL' Field");
		log.info("INFO: Enter OS People Gadget URL into Gadget URL field");
		driver.getSingleElement(TestURLTextBox).clear();
		driver.getSingleElement(TestURLTextBox).type(testConfig.getBrowserURL() + CREData.OS_peopleGadget);

		logger.strongStep("Set state to: Proxy State, Feature Access and Render Mode");
		log.info("INFO: Set State as follows:");
		setProxyState(0);
		setFeatureAccess(0);
		setRenderMode(0);

		logger.strongStep("Click 'Load Gadget' Button");
		log.info("INFO: Click Load Gadget Button");
		ui.getFirstVisibleElement(LoadGadgetButton).click();
		
		// Validate contents within the Render Preview area
		logger.strongStep("Verify that Render Preview area displays the expected content");
		log.info("INFO: Verify Render Preview area displays expected content");
		ui.fluentWaitPresent(OutputFrame);
		driver.switchToFrame().selectFrameByElement(ui.getFirstVisibleElement(OutputFrame));

		logger.weakStep("Verify that the user's ID doesn't display");
		String output = driver.getSingleElement(testPassedOS_PeopleDiv).getText();
		log.info("INFO: Verify User's ID doesn't display");
		Assert.assertFalse(output.contains(testUser.getUid()),"ERROR: User's ID " + testUser.getUid() + " was found");
		logger.weakStep("Verify that the User's Display Name doesn't display");
		log.info("INFO: Verify User's Display Name doesn't display");
		Assert.assertFalse(output.contains(testUser.getDisplayName()),"ERROR: User's Display Name" + testUser.getDisplayName() + " was found");
		logger.weakStep("Verify that the User's Email does not display");
		log.info("INFO: Verify User's Email doesn't display");
		Assert.assertFalse(output.contains(testUser.getEmail()),"ERROR: User's Email " + testUser.getEmail() + " was found " );

		driver.switchToFrame().returnToTopFrame();
		
		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>IWidget: loads stock chart and edits widget title</li>
	*<li><B>Step: </B>Load CRE test page</li>
	*<li><B>Step: </B>Go to IWidget test area, click Pre-Fill Stocks button</li> 
	*<li><B>Step: </B>Click Load Widget button</li>
	*<li><B>Step: </B>Click OK on the alert dialog 'Got Widget Info'</li>
	*<li><B>Verify: </B>The stock chart image displays</li>
	*<li><B>Step: </B>Change widget title from Stock to Title Was Edited</li>
	*<li><B>Step: </B>Click Change Title button</li>
	*<li><B>Verify: </B>The widget title is updated</li>
	*</ul>
	*/ 
	@Test(groups = { "level2", "bvt", "bvtcloud" })
	public void testIwidgets() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		// Load CRE test page
		logger.strongStep("Load the CRE test page");
		log.info("Load the CRE test page");
		driver.load(testConfig.getBrowserURL() + CREData.testIwidgetUrl, true);
		
		// Within IWidget Test area
		logger.strongStep("Click 'prefill stocks button' in the iwidget test region");
		log.info("Click Pre-fill Stocks button in IWidget test region");
		ui.clickLinkWait(iWidgetpreFillStockButton);
		log.info("Click Load Iwidget button");
		ui.clickLinkWait(iWidgetloadButton);
		
		// An alert dialog will display and needs dismissal
		logger.strongStep("Dismiss Alert dialog");
		log.info("Dismiss Alert dialog");
		try {
			Assert.assertTrue(ui.fluentWaitAlertDisplayed(), "ERROR: Alert is not displayed on the screen");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		driver.switchToAlert().accept();
		
		// Validate stock chart image displays
		logger.weakStep("Verify that the stock chart image displays");
		log.info("INFO: Verify the stock chart image displays");
		Assert.assertTrue(driver.isElementPresent(iWidgetStockChart), "ERROR: Stock Chart Image not found");
		
		// Change widget title from Stock to Title Was Edited
		logger.strongStep("Enter text into widget title field");
		log.info("Enter text into Widget Title field");	
		driver.getSingleElement(iWidgettitleInputTextBox).type("Title Was Edited");
		logger.strongStep("Click 'click title' button");
		log.info("Click Change Title button");
		ui.clickLinkWait(iWidgetchangeTitleButton);
		
		// Validate Widget Title is updated
		logger.weakStep("Verify that the Widget title is updated");
		log.info("INFO: Verify the Widget title is updated");
		Assert.assertTrue(driver.getSingleElement(iWidgetTitleText).getText().contains("Title Was Edited"), "ERROR: Title text not found");
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Posting a message from Sharebox</li>
	*<li><B>Step: </B>Log into Homepage</li>
	*<li><B>Step: </B>Load the CRE test page</li> 
	*<li><B>Step: </B>Click link Open Sharebox Dialog - Global</li>
	*<li><B>Step: </B>Enter text into status field</li>
	*<li><B>Step: </B>Click Post button</li>
	*<li><B>Verify: </B>A Success alert message displays</li>
	*</ul>
	*/ 
	@Test(groups = { "level2", "mt-exclude", "bvt", "bvtcloud" })
	public void testShareboxStatus() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		// Load the homepage component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load the homepage and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser);
		
		// Add another wait for element since this test has started to fail
		// due to premature load of CRE test page when Homepage is still loading
		hUI.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);
		log.info("Homepage is loaded");
		
		// Load ShareBox test page and start testing
		logger.strongStep("Load the sharebox test page");
		log.info("Load the CRE test page");
		
		// http results in a CORS issue
		String sbUri = (testConfig.getBrowserURL() + CREData.testShareBox).replace("http://", "https://");
		driver.load(sbUri, true);
		
		// Add a wait for element here since this test has started to fail
		// due to premature click of sbGlobalLink before the test page is loaded
		hUI.waitForPageLoaded(driver);
		log.info("Sharebox test page is loaded");

		logger.strongStep("Click the 'Open Sharebox' Dialog(Global)");
		log.info("Click Open Sharebox Dialog - Global");
		// commenting out as it fails intermittently
		// driver.getSingleElement(sbGlobalLink).click();
		hUI.clickLinkWait(sbGlobalLink);

		logger.strongStep("Enter text into status field");
		log.info("Enter text into status field");
		driver.switchToFrame().selectFrameByElement(ui.getFirstVisibleElement(sbStatusFrame));
		if(driver.isElementPresent(sbMentionsStatusText))
			driver.getSingleElement(sbMentionsStatusText).type("BVT CRE Test status update" + Helper.genDateBasedRandVal());
		else {
			hUI.enterStatus("BVT CRE Test status update" + Helper.genDateBasedRandVal());
			driver.switchToFrame().selectFrameByElement(ui.getFirstVisibleElement(sbStatusFrame));
		}
		logger.strongStep("Click 'Post' button");
		log.info("Click Post button");
		driver.getSingleElement(sbPostButton2).click();
		driver.switchToFrame().returnToTopFrame();
		
		logger.weakStep("Verify that a success alert message displays");
		log.info("INFO: Verify A Success alert message displays");
		Assert.assertTrue(driver.getSingleElement(sbSuccessfulxpath).getText().contains(sbSuccessfulPost),
					"ERROR: Success alert message not found" );

	}


	private void EEHelloWorldGadget() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Enter 'EE HelloWorld' Gadget URL into Gadget URL field");
		log.info("INFO: Enter EE HelloWord Gadget URL into Gadget URL field");
		driver.getSingleElement(TestURLTextBox).clear();
		driver.getSingleElement(TestURLTextBox).type(testConfig.getBrowserURL() + CREData.EEhelloWorldGadget);
		
		logger.strongStep("Enter CRE Test Contect in the 'EE Data Context' Field");
		log.info("INFO: Enter " + CREData.TestContent + " into the EE Data Context field");
		driver.getSingleElement(ContentTextBox).clear();
		driver.getSingleElement(ContentTextBox).type(CREData.TestContent);

		for (int i = 0; i <= 1; i++)
			for (int j = 0; j <= 3; j++)
				for (int k = 0; k <= 0; k++) {// only runs on EE
					
					logger.strongStep("Set the State as follow: Proxy State, Feature Access and Render Mode");
					log.info("INFO: Set State: " + i + j + k +" as follows");
					setProxyState(i);
					setFeatureAccess(j);
					setRenderMode(k);
					
					logger.strongStep("Click 'Load Gadget' Button");
					log.info("INFO: Click Load Gadget Button");
					ui.getFirstVisibleElement(LoadGadgetButton).click();
					
					logger.weakStep("Verify that the 'Render Preview' area displays expected content");
					log.info("INFO: Verify Render Preview area displays expected content");
					ui.fluentWaitPresent(OutputFrame);
					driver.switchToFrame().selectFrameByElement(ui.getFirstVisibleElement(OutputFrame));
					
					logger.weakStep("Verify that the CRE Test Content displays");
					String output = driver.getSingleElement(testPassedEEHelloWorldDiv).getText();
					log.info("INFO: Verify " + CREData.TestContent + " displays");
					Assert.assertTrue(output.contains(CREData.TestContent), "ERROR: With State " + i + j + k + " - Was looking for: " + CREData.TestContent + " but got: " + output);
				
					driver.switchToFrame().returnToTopFrame();
					
				}
	}

	private void OS_peopleGadget() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Enter 'OS People' gadget URL into gadget URL field");
		log.info("INFO: Enter OS People Gadget URL into Gadget URL field");
		driver.getSingleElement(TestURLTextBox).clear();
		driver.getSingleElement(TestURLTextBox).type(testConfig.getBrowserURL() + CREData.OS_peopleGadget);
		
		for (int i = 0; i <= 1; i++)
			for (int j = 0; j <= 3; j++)
				for (int k = 0; k <= 2; k = k + 2) {// 2 skipping share dialog
						             				// runs
					logger.strongStep("Set state: Proxy State, Feature Access, and Render Mode");
					log.info("INFO: Set State: " + i + j + k +" as follows");
					setProxyState(i);
					setFeatureAccess(j);
					setRenderMode(k);
					
					logger.strongStep("Click 'Load Gadget' Button");
					log.info("INFO: Click Load Gadget Button");
					ui.getFirstVisibleElement(LoadGadgetButton).click();

					logger.strongStep("Veridy that 'Render Preview' Area displays expected content");
					log.info("INFO: Verify Render Preview area displays expected content");
					ui.fluentWaitPresent(OutputFrame); 
					driver.switchToFrame().selectFrameByElement(ui.getFirstVisibleElement(OutputFrame));
					
					logger.strongStep("Wait for text to display- 'Result of people request'");
					ui.fluentWaitTextPresent("result of people request");
					String output = driver.getSingleElement(testPassedOS_PeopleDiv).getText();
					logger.weakStep("Verify that User's ID Displays");
					log.info("INFO: Verify User's ID displays");
					Assert.assertTrue(output.contains(testUser.getUid()),"ERROR: State " + i + j + k + " - Was looking for: " + testUser.getUid() + " but got: " + output);
					logger.weakStep("Verify that User's Display Name displays");
					log.info("INFO: Verify User's Display Name displays");
					Assert.assertTrue(output.contains(testUser.getDisplayName()),"ERROR: With State " + i + j + k + " - Was looking for: " + testUser.getDisplayName() + " but got: " + output);
					logger.weakStep("Verify that User's Email displays");
					log.info("INFO: Verify User's Email displays");
					Assert.assertTrue(output.contains(testUser.getEmail()),"ERROR: State " + i + j + k + " - Was looking for: " + testUser.getEmail() + " but got: " + output);

					driver.switchToFrame().returnToTopFrame();
				}
	}


	private void setProxyState(int i) {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		switch (i) {
		case 0:
			logger.strongStep("Set 'Proxy Access' radio button to 'Internet Access'");
			log.info("INFO: Set Proxy Access radio button to Internet Access");
			driver.getSingleElement(ProxyAccessRadioIntranetAccess).click();
			break;
		default:
			logger.strongStep("Set 'Proxy Access' radio button to 'External' Only");
			log.info("INFO: Set Proxy Access radio button to External Only");
			driver.getSingleElement(ProxyAccessRadioExternalOnly).click();
			break;
		}
	}

	private void setFeatureAccess(int i) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		switch (i) {
		case 0:
			logger.strongStep("Set 'Feature Access' radio button to 'Restricted'");
			log.info("INFO: Set Feature Access radio button to Restricted");
			driver.getSingleElement(FeatureAccessRadioRestricted).click();
			break;
		case 1:
			logger.strongStep("Set 'Feature Access' radio button to 'Trusted'");
			log.info("INFO: Set Feature Access radio button to Trusted");
			driver.getSingleElement(FeatureAccessRadioTrusted).click();
			break;
		case 2:
			logger.strongStep("Set 'Feature Access' radio button to 'Trusted + SSO'");
			log.info("INFO: Set Feature Access radio button to Trusted + SSO");
			driver.getSingleElement(FeatureAccessRadioTrustedSSO).click();
			break;
		default:
			logger.strongStep("Set 'Feature Access' radio button to 'Everything'");
			log.info("INFO: Set Feature Access radio button to Everything");
			driver.getSingleElement(FeatureAccessRadioEverything).click();
			break;
		}
	}

	private void setRenderMode(int i) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		switch (i) {
		case 0:
			logger.strongStep("Set 'Render Mode' radio button to 'Embedded Experience'");
			log.info("INFO: Set Render Mode radio button to Embedded Experience");
			driver.getSingleElement(RenderModeRadiorenderModeEE).click();
			break;
		case 1:
			logger.strongStep("Set 'Render Mode' radio button to 'Share Dialog'");
			log.info("INFO: Set Render Mode radio button to Share Dialog");
			driver.getSingleElement(RenderModeRadiorenderModeSharedialog).click();
			break;
		default:
			logger.strongStep("Set 'Render Mode' radio button to 'Homepage'");
			log.info("INFO: Set Render Mode radio button to Homepage");
			driver.getSingleElement(RenderModeRadiorenderModeHomepage).click();
			break;
		}
	}

}