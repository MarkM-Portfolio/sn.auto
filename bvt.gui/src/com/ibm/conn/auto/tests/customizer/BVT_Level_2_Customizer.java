package com.ibm.conn.auto.tests.customizer;

import java.io.File;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CustomizerUI;


public class BVT_Level_2_Customizer extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Customizer.class);
	private TestConfigCustom cfg;
	
	private User connAdmin, testUserA;
	private CustomizerUI ui;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		ui = new CustomizerUI(driver);
		
		connAdmin = cfg.getUserAllocator().getAdminUser();
		testUserA = cfg.getUserAllocator().getUser();	
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Create a Customizer app, verify customization is shown on Homepage then Delete app.</li>
	*<li><B>Pre-req:</B>Customization js file already exists on the server.</li>
	*<li><B>Step:</B>As admin, go to Customizer.</li>
	*<li><B>Step:</B>Create a new app that uses the HelloWorld js file on the server. Save.</li>
	*<li><B>Verify:</B>Confirmation message is shown and the app is shown in the view.</li>
	*<li><B>Verify:</B>The app is enabled by default.</li>
	*<li><B>Step:</B>Login as a normal user, go to Homepage.</li>
	*<li><B>Verify:</B>The Hello World! customization is displayed in the Activity Stream title.</li>
	*<li><B>Step:</B>Log out and log back in to the Customizer as admin.</li>
	*<li><B>Step:</B>Delete the app</li>
	*<li><B>Verify:</B>Confirmation message is shown and the app is no longer shown in the view.</li>
	*<li>Reference doc: https://help.hcltechsw.com/connections/v65/admin/customize/custom_customizer_create_app.html?hl=customizer</li>
	*/
	@Test(groups = {"level2", "cplevel2"})
	public void testCreateAppAndDelete() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		logger.strongStep("Login as Connections Admin and go to the Customizer: " + connAdmin.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCustomizer);
		ui.login(connAdmin);
		
		logger.strongStep("Click New App button");
		ui.clickLinkWait(ui.newAppBtn);
		
		logger.strongStep("Go to the Code Editor");
		ui.clickLinkWait(ui.codeEditorInNav);
		
		log.info("Create the json to define the new app");
		String jsonString = ui.createHelloWorldAppJson();
		File jsonFile = ui.createJsonFile(jsonString);
		
		logger.strongStep("Import the json file and save app");
		ui.importJsonFile(jsonFile, cfg.getTestConfig());
		log.info("Click Save button");
		ui.clickLinkWait(ui.saveAppBtn);

		logger.strongStep("Verify success message and the app card are shown in the main view");
		JSONObject jsonObject = new JSONObject(jsonString);
		String appName = jsonObject.getString("name");
		String appTitle = jsonObject.getString("title");
		
		log.info("App name = " + appName);
		String successMsg = "New application '" + appName + "' successfully created";
		ui.fluentWaitTextPresent(successMsg);
		log.info("App title = " + appTitle);
		Element app = ui.getAppByTitle(appTitle);
		Assert.assertNotNull(app, "Card for app " + appName + " not found.");
		
		logger.strongStep("Verify the app is enabled by default");
		Assert.assertTrue(ui.isAppEnabled(appTitle), "App is not enabled: " + appTitle);
		
		log.info("There is no logout link so just clear cookies");
		WebDriver wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		
		logger.strongStep("Login as another test user: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing, true);
		ui.login(testUserA);
		ui.getCloseTourScript();
		
		logger.strongStep("Verify the customized Hello World! title is shown then logout.");
		log.info("Verify the customized Hello World! title is shown then logout.");
		Assert.assertEquals("Hello World!", ui.getElementText(HomepageUIConstants.HomepageActivityStreamTitle));
		ui.logout();
		
		logger.strongStep("Log back in as Connections Admin and go to the Customizer: " + 
				connAdmin.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCustomizer, true);
		ui.login(connAdmin);
		
		logger.strongStep("Delete the app: " + appName);
		ui.deleteAppFromView(appName, appTitle);
		
		logger.strongStep("Verify success message is shown and deleted app is no longer in the view");
		log.info("Verify success message is shown and deleted app is no longer in the view");
		successMsg = "Application '" + appName + "' has been deleted";
		ui.fluentWaitTextPresent(successMsg);
		Assert.assertNull(ui.getAppByTitle(appTitle), "Card for app " + appName + " is found.");		

		ui.endTest();
	}
	
}
