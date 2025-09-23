package com.ibm.conn.auto.webui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.http.HttpStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.hcl.lconn.automation.framework.payload.AdminBannerResponse;
import com.hcl.lconn.automation.framework.services.AdminBannerService;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;

import io.restassured.response.Response;

public class CustomizerUI extends HCBaseUI {
	
	private static Logger log = LoggerFactory.getLogger(CustomizerUI.class);
	CommonUICnx8 commonUI = new CommonUICnx8(driver);
			
	// selectors
	public String newAppBtn = "css=#newAppBtn";
	public String saveAppBtn = "css=#saveAppBtn";
	public String codeEditorInNav = "xpath=//a[contains(text(), 'Code Editor')]";
	public String codeEditor = "css=#jsonEditor .textviewContent";
	public String importFile = "appUploadInput";
	public String selectedImportFile = "css=.ic-file-upload-label";
	public String appList = "css=#appList div.ic-app";
	public String appNameInCard = "css=.ic-app-name";
	public String appCardActionDropdown = "css=button[id^=appActionsDropdown]";
	public String deleteAppOptionInDropdown = "css=#deleteAppBtn";
	public String deleteNameField = "css=#deleteName";
	public String deleteBtn = "css=#confirmDeleteBtn";
	public String appState = "css=.ic-state";
	public String textViewCodeSnippet = "//div[@class='textview editorTheme']";
	public String appActionDropdown = "//h3[text()='PLACEHOLDER']/../../..//button[@class=\"dropdown-toggle btn btn-default\"]";
	public String disableAppOptionInDropdown =  "//h3[text()='PLACEHOLDER']/../../..//a[@id='disableAppBtn']";
	public String enableAppOptionInDropdown = "//h3[text()='PLACEHOLDER']/../../..//a[@id='enableAppBtn']";
	public String cardLayout = "//h3[text()='PLACEHOLDER']";	
	
	public CustomizerUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	/**
	 * Find the target app card in the main view
	 * @param appTitle
	 * @return Element of the target app card, null if not found
	 */
	public Element getAppByTitle(String appTitle) {
		List<Element> apps = driver.getVisibleElements(appList);
		for (Element app : apps)  {
			String cardName = app.getSingleElement(appNameInCard).getText();
			log.info("Card listed: " + cardName);
			if (appTitle.trim().equals(cardName))  {
				return app;
			}
		}
		return null;
	}
	
	/**
	 * Return whether the app is enabled based on the status shown in the card.
	 * @param appTitle
	 * @return
	 */
	public boolean isAppEnabled(String appTitle) {
		Element app = getAppByTitle(appTitle.trim());
		if (app == null)  {
			throw new IllegalArgumentException("Cannot find app in the view: " + appTitle);
		}		
		
		Element appStateElm = app.getSingleElement(appState);
		if (appStateElm.getText().equalsIgnoreCase("enabled")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Delete the target app from view
	 * @param appName
	 * @param appTitle
	 */
	public void deleteAppFromView(String appName, String appTitle) {
		log.info("Delete app: " + appName);
		appName = appName.trim();
		appTitle = appTitle.trim();
		Element app = getAppByTitle(appTitle);
		if (app == null)  {
			throw new IllegalArgumentException("Cannot find app in the view: " + appTitle);
		}
		
		log.info("Click the option dropdown and select Delete");
		app.getSingleElement(appCardActionDropdown).click();
		app.getSingleElement(deleteAppOptionInDropdown).click();
		
		log.info("Type the app name then click Delete");
		Element nameField = driver.getFirstElement(deleteNameField);
		nameField.typeWithDelay(appName);
		driver.getFirstElement(deleteBtn).click();
	}
	
	/**
	 * Import the given json file to App Editor.
	 * Note: the file is expected to reside in the machine where the test is being executed
	 * even when it's running on Grid. This is to avoid overhead to make sure the file is copied
	 * to each grid node.
	 * @param jsonFile
	 */
	public void importJsonFile(File jsonFile, TestConfiguration testConfig) {
		WebElement inputField;
		
		if (testConfig.serverIsBrowserStack() || testConfig.serverIsGridHub()) {
			log.info("Call setFileDetector to use a local file");
			RemoteWebDriver wd = (RemoteWebDriver) driver.getBackingObject();
			wd.setFileDetector(new LocalFileDetector());
			
			inputField = wd.findElementById(importFile);
		} else {
			WebDriver wd = (WebDriver) driver.getBackingObject();
			inputField = wd.findElement(By.id(importFile));
		}
		
		log.info("Set import field to use file: " + jsonFile.getAbsolutePath());
		inputField.sendKeys(jsonFile.getAbsolutePath());
		Element selectedFile = driver.getSingleElement(selectedImportFile);
		Assert.assertEquals(selectedFile.getText(), jsonFile.getName());
	}
	
	/**
	 * Create app json file in system temp dir of the machine where the test is being executed.
	 * @param jsonString
	 * @return File path of the app json, it will be deleted automatically after test.
	 * @throws IOException 
	 */
	public File createJsonFile(String jsonString) throws IOException {
		try {
			File tmpJsonFile = File.createTempFile("helloWorld-"+Helper.genDateBasedRandVal3(), ".json");
			tmpJsonFile.deleteOnExit();
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmpJsonFile));
			bw.write(jsonString);
			bw.close();
			
			return tmpJsonFile;
		} catch (IOException e) {
			log.error("Cannot create json file for Customizer app");
			throw e;
		}		
	}
	
	/**
	 * Return the HelloWorld app json with unique name and title.
	 * It is based on https://github.com/hclcnx/customizer/blob/master/samples/helloWorld/helloWorld.json
	 * @return string format of json
	 */
	public String createHelloWorldAppJson() {
		String rand = Helper.genDateBasedRandVal3();
		
		String jsonContent = "{" +
				"\"name\": \"BVT " + rand + " app\"," +
				"\"title\": \" BVT title " + rand + "\"," +
				"\"description\": \"BVT " + rand + " - Inserts text into HCL Connections Homepage\"," +
				"\"services\": [" +
				"  \"Customizer\"" +
				"]," +
				"\"extensions\": [" +
				"  {" +
				"    \"payload\": {" +
				"      \"include-files\": [" +
				"        \"helloWorld/helloWorld.user.js\"" +
				"      ]," +
				"      \"include-repo\":{" +
				"        \"name\": \"global-samples\"" +
				"      }," +
				"      \"cache-headers\": {" +
				"        \"cache-control\": \"max-age=0\"" +
				"      }" +
				"    }," +
				"    \"name\": \"BVT " + rand + " - Hello World Extension\"," +
				"    \"type\": \"com.ibm.customizer.ui\"," +
				"    \"path\": \"homepage\"" +
				"  }" +
				" ]" +
				"}";
		
		return jsonContent;
	}
	
	/**
	 * Creating MS Team Share app and importing the json file.
	 * @param logger
	 * @param appName - Name of App
	 * @throws IOException 
	 */
	public void createMSTeamShareApp(DefectLogger logger, String appName) throws IOException {
		String successMsg = "New application '" + appName + "' successfully created";

		logger.strongStep("Click on New App button");
		clickLinkWd(createByFromSizzle(newAppBtn), "Click on new app button");
		
		logger.strongStep("Get MS Team Share app json with unique name and title");
		String jsonString = createMSTeamShareAppJson();
		
		logger.strongStep("Import json file.");
		importJsonFileAndSaveApp(jsonString, successMsg);	
	}

	
	/**
	 * Importing JSON file and Save the app
	 * @param jsonString
	 * @param successMsg
	 */
	public void importJsonFileAndSaveApp(String jsonString, String successMsg) throws IOException {
		log.info("Go to the Code Editor");
		clickLinkWd(createByFromSizzle(codeEditorInNav), "Click on code editor");
		
		log.info("Create the json to define the new app");	
		log.info("Create the json to define the new app");
		File jsonFile = createJsonFile(jsonString);
		
		log.info("Waiting for the textview code editor");
		waitForElementsVisibleWd(By.xpath(textViewCodeSnippet), 3);
		
		log.info("Import the json file");
		importJsonFile(jsonFile, cfg.getTestConfig());		
		
		log.info("Click on Save button");
		clickLinkWd(createByFromSizzle(saveAppBtn), "Click on save button");
		
		log.info("Verify success message and the app card are shown in the main view");
		isTextPresentWd(successMsg);
	}
	
	/**
	 * Calling PUT API to update Admin banner
	 * @param logger
	 * @param testUser
	 * @param message
	 * @param severity
	 * @param isEnabled
	 * @throws IOException 
	 */
	public AdminBannerResponse updateAdminBannerViaApi(DefectLogger logger, User testUser, String message, String severity, boolean isEnabled) {
		
		logger.strongStep("Invoke PUT API to update Admin banner");
		log.info("Info: Invoke PUT API to update Admin banner");
		AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = adminBannerService.updateAdminBanner(message, severity, isEnabled);
		
		logger.strongStep("Verify status code after got response of Admin banner");
		log.info("Info: Verify status code after got response of Admin banner");	
		adminBannerService.assertStatusCode(resp, HttpStatus.SC_OK, "Admin Banner is updated");
		
		logger.strongStep("converting response to POJO class");
		log.info("Info: converting response to POJO class");	
		AdminBannerResponse adminBannerResponse=resp.as(AdminBannerResponse.class);
		
		return adminBannerResponse;
	}

	/**
	 * Disable the target app from view
	 * @param appName
	 * @param appTitle
	 */
	public void disableAppFromAppReg(String appTitle) {
		log.info("Click on the option dropdown and select Disable");
		clickLinkWaitWd(By.xpath(appActionDropdown.replace("PLACEHOLDER", appTitle)), 3, "Click on app dropdown");
		clickLinkWaitWd(By.xpath(disableAppOptionInDropdown.replace("PLACEHOLDER", appTitle)), 2, "Click on disable button");
	}
	
	/**
	 * Enable the target app from view
	 * @param appName
	 * @param appTitle
	 */
	public void enableAppFromAppReg(String appTitle) {
		log.info("Click on the option dropdown and select Enable");
		clickLinkWaitWd(By.xpath(appActionDropdown.replace("PLACEHOLDER", appTitle)), 3, "Click on app dropdown");
		clickLinkWaitWd(By.xpath(enableAppOptionInDropdown.replace("PLACEHOLDER", appTitle)), 2, "Click on enable button");		
	}
	
	/**
	 * Return the Admin Banner app json with unique name and title.
	 * @return string format of json
	 */
	public String createAdminBannerAppJson() {		
		String jsonContent = "{\r\n" + 
				"    \"name\": \"Connections Banner\",\r\n" + 
				"    \"title\": \"Connections Banner\",\r\n" + 
				"    \"description\": \"Configuration for the banner that will be displayed at the top of Connections pages.\",\r\n" + 
				"    \"services\": [\r\n" + 
				"        \"Connections\"\r\n" + 
				"    ],\r\n" + 
				"    \"state\": \"enabled\",\r\n" + 
				"    \"extensions\": [\r\n" + 
				"        {\r\n" + 
				"            \"name\": \"connections-banner\",\r\n" + 
				"            \"type\": \"com.hcl.connections.banner\",\r\n" + 
				"            \"payload\": {\r\n" + 
				"                \"open\": true,\r\n" + 
				"                \"message\": [\r\n" + 
				"                    \"This is the HCL Connections Banner\"\r\n" + 
				"                ],\r\n" + 
				"                \"severity\": \"success\"\r\n" + 
				"            },\r\n" + 
				"            \"path\": \"global\",\r\n" + 
				"            \"state\": \"enabled\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		
		return jsonContent;
	}
	
	/**
	 * Return the Admin Banner app json with unique name,title and cacheExpiration.
	 * @return string format of json
	 */
	public String createAdminBannerAppJsonWithCache() {		
		String jsonContent = "{\r\n" + 
				"    \"name\": \"Connections Banner\",\r\n" + 
				"    \"title\": \"Connections Banner\",\r\n" + 
				"    \"description\": \"Configuration for the banner that will be displayed at the top of Connections pages.\",\r\n" + 
				"    \"services\": [\r\n" + 
				"        \"Connections\"\r\n" + 
				"    ],\r\n" + 
				"    \"state\": \"enabled\",\r\n" + 
				"    \"extensions\": [\r\n" + 
				"        {\r\n" + 
				"            \"name\": \"connections-banner\",\r\n" + 
				"            \"type\": \"com.hcl.connections.banner\",\r\n" + 
				"            \"payload\": {\r\n" + 
				"                \"open\": true,\r\n" + 
				"                \"message\": [\r\n" + 
				"                    \"This is the HCL Connections Banner\"\r\n" + 
				"                ],\r\n" + 
				"                \"severity\": \"success\",\r\n" + 
				"                 \"cacheExpiration\":2000 \r\n" +
				"            },\r\n" + 
				"            \"path\": \"global\",\r\n" + 
				"            \"state\": \"enabled\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		
		return jsonContent;
	}
	
	/**
	 * Return customized Searched Display json with unique name and title.
	 * @return string format of json
	 */
	public String createCustomizedSearchDisplay()
	{
		String jsonContent = "{\r\n"
				+ "\"name\": \"Customized Search Display\",\r\n"
				+ "\"title\": \"Customized Search Display\",\r\n"
				+ "\"description\": \"Properties to customize Search Display\",\r\n"
				+ "\"services\": [\r\n"
				+ "\"Connections\"\r\n"
				+ "],\r\n"
				+ "\"state\": \"enabled\",\r\n"
				+ "\"extensions\": [\r\n"
				+ "{\r\n"
				+ "\"name\": \"Customized Search Display\",\r\n"
				+ "\"title\": \"Customized Search Display\",\r\n"
				+ "\"description\": \"Properties to customize Search Display\",\r\n"
				+ "\"translations\": {\r\n"
				+ "\"\": {\r\n"
				+ "\"allconnection.label\": \"All Connection\",\r\n"
				+ "\"friends.label\": \"Friends\"\r\n"
				+ "},\r\n"
				+ "\"en\": {\r\n"
				+ "\"allconnection.label\": \"All Connection\",\r\n"
				+ "\"friends.label\": \"Friends\"\r\n"
				+ "},\r\n"
				+ "\"fr\": {\r\n"
				+ "\"allconnection.label\": \"Toute Connection\",\r\n"
				+ "\"friends.label\": \"Copines\"\r\n"
				+ "}\r\n"
				+ "},\r\n"
				+ "\"type\": \"com.hcl.search.customization\",\r\n"
				+ "\"payload\": {\r\n"
				+ "\"env\": \"NOconnections\",\r\n"
				+ "\"searchResultOptions\": [\r\n"
				+ "{\r\n"
				+ "\"isSuggestion\": true,\r\n"
				+ "\"searchURL\": \"http://www.google.com/search?q=${searchTerm}\",\r\n"
				+ "\"options\": [\r\n"
				+ "{\r\n"
				+ "\"label\": \"%nls:allconnection.label\",\r\n"
				+ "\"url\": \"http://www.google.com/search?q=${searchTerm}\"\r\n"
				+ "},\r\n"
				+ "{\r\n"
				+ "\"label\": \"%nls:friends.label\",\r\n"
				+ "\"url\": \"http://www.yahoo.com/search?q=${searchTerm}\"\r\n"
				+ "}\r\n"
				+ "]\r\n"
				+ "}\r\n"
				+ "]\r\n"
				+ "},\r\n"
				+ "\"state\": \"enabled\"\r\n"
				+ "}\r\n"
				+ "]\r\n"
				+ "}";
		return jsonContent;
	}
	
	/**
	 * Return the MS Team Share app json with unique name and title.
	 * @return string format of json
	 */
	public String createMSTeamShareAppJson() {		
		String jsonContent ="{ \r\n" +
				"    \"name\": \"MS Teams Share Extension\",\r\n" +
				"    \"title\": \"MS Teams Share\",\r\n" +
				"    \"description\": \"Share functionality extension to share link to current content into Teams channel\",\r\n" +
				"    \"services\": [\r\n" +
				"        \"Connections\"\r\n" +
				"    ],\r\n" +
				"    \"state\": \"enabled\",\r\n" +
				"    \"extensions\": [\r\n" +
				"        {\r\n" +
				"            \"name\": \"MS Teams Share\",\r\n" +
				"            \"type\": \"com.hcl.share.extension\",\r\n" +
				"            \"payload\": {\r\n" +
				"                \"include-files\": [\r\n" +
				"                    \"/files/customizer/share-extensions/ms-teams/connections-teams-share-extension-8.0.js\"\r\n" +
				"                ],\r\n" +
				"                \"cache-headers\": {\r\n" +
				"                    \"cache-control\": \"max-age=43200\"\r\n" +
				"                },\r\n" +
				"                \"className\": \"teams-share-button\",\r\n" +
				"                \"icon\": {\r\n" +
				"                    \"type\": \"svg\",\r\n" +
				"                    \"data\": \"data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2aWV3Qm94PSIwIDAgMTAyNCAxMDI0Ij4KICAgICAgPGRlZnM+CiAgICAgICAgPGxpbmVhckdyYWRpZW50IGlkPSJwbGF0ZS1maWxsIiB4MT0iLS4yIiB5MT0iLS4yIiB4Mj0iLjgiIHkyPSIuOCI+CiAgICAgICAgICA8c3RvcCBvZmZzZXQ9IjAiIHN0b3AtY29sb3I9IiM1YTYyYzQiPjwvc3RvcD4KICAgICAgICAgIDxzdG9wIG9mZnNldD0iMSIgc3RvcC1jb2xvcj0iIzM5NDBhYiI+PC9zdG9wPgogICAgICAgIDwvbGluZWFyR3JhZGllbnQ+CiAgICAgICAgPHN0eWxlPgogICAgICAgICAgLmNscy0xe2ZpbGw6IzUwNTljOX0uY2xzLTJ7ZmlsbDojN2I4M2VifQogICAgICAgIDwvc3R5bGU+CiAgICAgICAgPGZpbHRlciBpZD0icGVyc29uLXNoYWRvdyIgeD0iLTUwJSIgeT0iLTUwJSIgd2lkdGg9IjMwMCUiIGhlaWdodD0iMzAwJSI+CiAgICAgICAgICA8ZmVHYXVzc2lhbkJsdXIgaW49IlNvdXJjZUFscGhhIiBzdGREZXZpYXRpb249IjI1Ij48L2ZlR2F1c3NpYW5CbHVyPgogICAgICAgICAgPGZlT2Zmc2V0IGR5PSIyNSI+PC9mZU9mZnNldD4KICAgICAgICAgIDxmZUNvbXBvbmVudFRyYW5zZmVyPgogICAgICAgICAgICA8ZmVGdW5jQSB0eXBlPSJsaW5lYXIiIHNsb3BlPSIuMjUiPjwvZmVGdW5jQT4KICAgICAgICAgIDwvZmVDb21wb25lbnRUcmFuc2Zlcj4KICAgICAgICAgIDxmZU1lcmdlPgogICAgICAgICAgICA8ZmVNZXJnZU5vZGU+PC9mZU1lcmdlTm9kZT4KICAgICAgICAgICAgPGZlTWVyZ2VOb2RlIGluPSJTb3VyY2VHcmFwaGljIj48L2ZlTWVyZ2VOb2RlPgogICAgICAgICAgPC9mZU1lcmdlPgogICAgICAgIDwvZmlsdGVyPgoKCiAgICAgICAgPGZpbHRlciBpZD0iYmFjay1wbGF0ZS1zaGFkb3ciIHg9Ii01MCUiIHk9Ii01MCUiIHdpZHRoPSIzMDAlIiBoZWlnaHQ9IjMwMCUiPgogICAgICAgICAgCgk8ZmVHYXVzc2lhbkJsdXIgaW49IlNvdXJjZUFscGhhIiBzdGREZXZpYXRpb249IjI0Ij48L2ZlR2F1c3NpYW5CbHVyPgoJICA8ZmVPZmZzZXQgZHg9IjIiIGR5PSIyNCI+PC9mZU9mZnNldD4KICAgICAgICAgIDxmZUNvbXBvbmVudFRyYW5zZmVyPgogICAgICAgICAgPGZlRnVuY0EgdHlwZT0ibGluZWFyIiBzbG9wZT0iLjYiPjwvZmVGdW5jQT4KCiAgICAgICAgICA8L2ZlQ29tcG9uZW50VHJhbnNmZXI+CiAgICAgICAgICA8ZmVNZXJnZT4KICAgICAgICAgICAgPGZlTWVyZ2VOb2RlPjwvZmVNZXJnZU5vZGU+CiAgICAgICAgICAgIDxmZU1lcmdlTm9kZSBpbj0iU291cmNlR3JhcGhpYyI+PC9mZU1lcmdlTm9kZT4KICAgICAgICAgIDwvZmVNZXJnZT4KICAgICAgICA8L2ZpbHRlcj4KICAgICAgICA8ZmlsdGVyIGlkPSJ0ZWUtc2hhZG93IiB4PSItNTAlIiB5PSItNTAlIiB3aWR0aD0iMjUwJSIgaGVpZ2h0PSIyNTAlIj4KICAgICAgICAgIDxmZUdhdXNzaWFuQmx1ciBpbj0iU291cmNlQWxwaGEiIHN0ZERldmlhdGlvbj0iMTIiPjwvZmVHYXVzc2lhbkJsdXI+CiAgICAgICAgICA8ZmVPZmZzZXQgZHg9IjEwIiBkeT0iMjAiPjwvZmVPZmZzZXQ+CiAgICAgICAgICA8ZmVDb21wb25lbnRUcmFuc2Zlcj4KICAgICAgICAgICAgPGZlRnVuY0EgdHlwZT0ibGluZWFyIiBzbG9wZT0iLjIiPjwvZmVGdW5jQT4KICAgICAgICAgIDwvZmVDb21wb25lbnRUcmFuc2Zlcj4KICAgICAgICAgIDxmZU1lcmdlPgogICAgICAgICAgICA8ZmVNZXJnZU5vZGU+PC9mZU1lcmdlTm9kZT4KICAgICAgICAgICAgPGZlTWVyZ2VOb2RlIGluPSJTb3VyY2VHcmFwaGljIj48L2ZlTWVyZ2VOb2RlPgogICAgICAgICAgPC9mZU1lcmdlPgogICAgICAgIDwvZmlsdGVyPgoKICAgICAgIAoKICAgICAgICA8Y2xpcFBhdGggaWQ9ImJhY2stcGxhdGUtY2xpcCI+CiAgICAgICAgICA8cGF0aCBkPSJNNjg0IDQzMkg1MTJ2LTQ5LjE0M0ExMTIgMTEyIDAgMSAwIDQxNiAyNzJhMTExLjU1NiAxMTEuNTU2IDAgMCAwIDEwLjc4NSA0OEgxNjBhMzIuMDk0IDMyLjA5NCAwIDAgMC0zMiAzMnYzMjBhMzIuMDk0IDMyLjA5NCAwIDAgMCAzMiAzMmgxNzguNjdjMTUuMjM2IDkwLjggOTQuMiAxNjAgMTg5LjMzIDE2MCAxMDYuMDM5IDAgMTkyLTg1Ljk2MSAxOTItMTkyVjQ2OGEzNiAzNiAwIDAgMC0zNi0zNnoiIGZpbGw9IiNmZmYiPjwvcGF0aD4KICAgICAgICA8L2NsaXBQYXRoPgogICAgICA8L2RlZnM+CiAgICAgIDxnIGlkPSJzbWFsbF9wZXJzb24iIGZpbHRlcj0idXJsKCNwZXJzb24tc2hhZG93KSI+CiAgICAgICAgPHBhdGggaWQ9IkJvZHkiIGNsYXNzPSJjbHMtMSIgZD0iTTY5MiA0MzJoMTY4YTM2IDM2IDAgMCAxIDM2IDM2djE2NGExMjAgMTIwIDAgMCAxLTEyMCAxMjAgMTIwIDEyMCAwIDAgMS0xMjAtMTIwVjQ2OGEzNiAzNiAwIDAgMSAzNi0zNnoiPjwvcGF0aD4KICAgICAgICA8Y2lyY2xlIGlkPSJIZWFkIiBjbGFzcz0iY2xzLTEiIGN4PSI3NzYiIGN5PSIzMDQiIHI9IjgwIj48L2NpcmNsZT4KICAgICAgPC9nPgogICAgICA8ZyBpZD0iTGFyZ2VfUGVyc29uIiBmaWx0ZXI9InVybCgjcGVyc29uLXNoYWRvdykiPgogICAgICAgIDxwYXRoIGlkPSJCb2R5LTIiIGRhdGEtbmFtZT0iQm9keSIgY2xhc3M9ImNscy0yIiBkPSJNMzcyIDQzMmgzMTJhMzYgMzYgMCAwIDEgMzYgMzZ2MjA0YTE5MiAxOTIgMCAwIDEtMTkyIDE5MiAxOTIgMTkyIDAgMCAxLTE5Mi0xOTJWNDY4YTM2IDM2IDAgMCAxIDM2LTM2eiI+PC9wYXRoPgogICAgICAgIDxjaXJjbGUgaWQ9IkhlYWQtMiIgZGF0YS1uYW1lPSJIZWFkIiBjbGFzcz0iY2xzLTIiIGN4PSI1MjgiIGN5PSIyNzIiIHI9IjExMiI+PC9jaXJjbGU+CiAgICAgIDwvZz4KICAgICAgPHJlY3QgaWQ9IkJhY2tfUGxhdGUiIHg9IjEyOCIgeT0iMzIwIiB3aWR0aD0iMzg0IiBoZWlnaHQ9IjM4NCIgcng9IjMyIiByeT0iMzIiIGZpbHRlcj0idXJsKCNiYWNrLXBsYXRlLXNoYWRvdykiIGNsaXAtcGF0aD0idXJsKCNiYWNrLXBsYXRlLWNsaXApIiBmaWxsPSJ1cmwoI3BsYXRlLWZpbGwpIj48L3JlY3Q+CiAgICAgIDxwYXRoIGlkPSJMZXR0ZXJfVCIgZD0iTTM5OS4zNjUgNDQ1Ljg1NWgtNjAuMjkzdjE2NC4yaC0zOC40MTh2LTE2NC4yaC02MC4wMlY0MTRoMTU4LjczeiIgZmlsdGVyPSJ1cmwoI3RlZS1zaGFkb3cpIiBmaWxsPSIjZmZmIj48L3BhdGg+CiAgICA8L3N2Zz4=\"\r\n" +
				"                }\r\n" +
				"            },\r\n" +
				"            \"path\": \"global\",\r\n" +
				"            \"state\": \"enabled\"\r\n" +
				"        }\r\n" +
				"    ]\r\n" +
				"}";
				
		return jsonContent;
	}
	
	/**
	 * Return the updated Admin Banner app json with unique name and title.
	 * @return string format of json
	 */
	public String updateAdminBannerAppJson() {		
		String jsonContent = "{\r\n" + 
				"    \"name\": \"Connections Banner\",\r\n" + 
				"    \"title\": \"Connections Banner\",\r\n" + 
				"    \"description\": \"Configuration for the banner that will be displayed at the top of Connections pages.\",\r\n" + 
				"    \"services\": [\r\n" + 
				"        \"Connections\"\r\n" + 
				"    ],\r\n" + 
				"    \"state\": \"enabled\",\r\n" + 
				"    \"extensions\": [\r\n" + 
				"        {\r\n" + 
				"            \"name\": \"connections-banner\",\r\n" + 
				"            \"type\": \"com.hcl.connections.banner\",\r\n" + 
				"            \"payload\": {\r\n" + 
				"                \"open\": true,\r\n" + 
				"                 \"message\": [\r\n" + 
				"                    \"Please make sure to update your profile soon.\"\r\n" + 
				"                 ], \r\n" + 
				"                 \"severity\": \"info\"\r\n" + 
				"            },\r\n" + 
				"            \"path\": \"global\",\r\n" + 
				"            \"state\": \"enabled\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		
		return jsonContent;
	}
	
	
	/**
	 * Return the updated Admin Banner app json with unique name,title and cacheExpiration.
	 * @return string format of json
	 */
	public String updateAdminBannerAppJsonWithCache() {		
		String jsonContent = "{\r\n" + 
				"    \"name\": \"Connections Banner\",\r\n" + 
				"    \"title\": \"Connections Banner\",\r\n" + 
				"    \"description\": \"Configuration for the banner that will be displayed at the top of Connections pages.\",\r\n" + 
				"    \"services\": [\r\n" + 
				"        \"Connections\"\r\n" + 
				"    ],\r\n" + 
				"    \"state\": \"enabled\",\r\n" + 
				"    \"extensions\": [\r\n" + 
				"        {\r\n" + 
				"            \"name\": \"connections-banner\",\r\n" + 
				"            \"type\": \"com.hcl.connections.banner\",\r\n" + 
				"            \"payload\": {\r\n" + 
				"                \"open\": true,\r\n" + 
				"                 \"message\": [\r\n" + 
				"                    \"Please make sure to update your profile soon.\"\r\n" + 
				"                 ], \r\n" + 
				"                 \"severity\": \"info\",\r\n" + 
				"                 \"cacheExpiration\":2000 \r\n" +
				"            },\r\n" + 
				"            \"path\": \"global\",\r\n" + 
				"            \"state\": \"enabled\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		
		return jsonContent;
	}
	
	/**
	 * Creating an app and importing the json file.
	 * @param appName
	 * @throws IOException 
	 */
	public void createAppViaAppReg(String appName) throws IOException {
		String successMsg = "New application '" + appName + "' successfully created";
		String jsonString = null;
		log.info("Click on New App button");
		clickLinkWd(createByFromSizzle(newAppBtn), "Click on new app button");
		
		//logger.strongStep("Get Admin Banner app json with unique name and title");
		switch(appName)
		{
		case "Touchpoint" : 
			 jsonString = createTouchpointAppJson();
			break;
		case "SharePoint Widget Library (ORG D)" : 
			 jsonString = createSharepointAppJson();
			break;
		case "Connections Navigation" :
			jsonString = createNavigationBarAppJson();
			break;
		case "Connections Navigation Cache" :
			jsonString= createNavigationBarAppJsonWithCache();
			break;
		case "Connections Banner" :
			jsonString = createAdminBannerAppJson();
			break;		
		case "Connections Banner Cache" :
			jsonString = createAdminBannerAppJsonWithCache();
			break;
		case "Connections Custom Style - Fiesta" :
			jsonString = createConnectionsCustomStyleFiestaAppJson();
			break;
		case "Connections Custom Style - Dark" :
			jsonString = createConnectionsCustomStyleDarkAppJson();
			break;
		case "Connections Custom Style - Fiesta Cache" :
			jsonString = createConnectionsCustomStyleFiestaAppJsonWithCache();
			break;
		case "Customized Search Display" :
			jsonString = createCustomizedSearchDisplay();
			break;
		}
		
		importJsonFileAndSaveApp(jsonString, successMsg);	
	}
	
	/**
	 * Return the Touch Point app json with unique name and title.
	 * @return string format of json
	 */
	public String createTouchpointAppJson() {
		String jsonContent = "{\r\n" + 
				"    \"name\": \"Touchpoint\",\r\n" + 
				"    \"title\": \"Touchpoint\",\r\n" + 
				"    \"description\": \"Configuration for the banner that will be displayed at the top of Connections pages.\",\r\n" + 
				"    \"services\": [\r\n" + 
				"        \"Connections\"\r\n" + 
				"    ],\r\n" + 
				"    \"state\": \"enabled\",\r\n" + 
				"    \"extensions\": [\r\n" + 
				"{"
				+ "\"name\":\"Touchpoint MT\","
				+ "\"type\":\"com.ibm.social.apps.touchpoint.config\","
				+ "\"payload\":{\"uiEnabled\":true,\"steps\":"
				+ "{\"paths\":"
				+ "{\"defaultPath\":\"welcome,editProfile,profileTags,findColleagues,followCommunities\",\"icExternalPath\":"
				+ "\"welcome,editProfile,profileTags\",\"pagStandalone\":\"pagStandalone\"},\"order\":"
				+ "\"welcome,profileTags,findColleagues,followCommunities,editProfile\"},"
				+ "\"privacyAndGuidelines\":"
				+ "{\"enabled\":true,"
				+ "\"version\":\"1.0\","
				+ "\"externalLink\":"
				+ "\"https:\\//www.google.com\\/\",\r\n"
				+ "\"internalLink\":\"https:\\//gollmick.de\\/tou\\/TOU-ORG-C.html\"},"
				+ "\"maxPromotedExperts\":3,"
				+ "\"_promotedExperts\":\"\","
				+ "\"promotedExperts\":\"1000000001,1000000002,1000000005,1000000009\","
				+ "\"maxPromotedCommunities\":3,"
				+ "\"_promotedCommunities\":\"\","
				+ "\"promotedCommunities\":\"aa29c489-45b2-4e9f-bb84-8a0e94879d29\","
				+ "\"welcomeVideoUrl\":\"\"},"
				+ "\"path\":\"touchpoint\","
				+ "\"state\":\"enabled\""
				+ "}"
				+"]\r\n" 
				+"}";
		
		return jsonContent;
	}

	
	/**
	 * Return the Share Point app json with unique name and title.
	 * @return string format of json
	 */
	//NOTE : In order for this to work the org needs to be registered to SP server.Currently only MT1 orgD is registered to SP server.
	public String createSharepointAppJson() {
		String jsonContent = "{\r\n"
				+ "    \"name\": \"SharePoint Widget Library (ORG D)\",\r\n"
				+ "    \"title\": \"SharePoint Widget Library (ORG D)\",\r\n"
				+ "    \"description\": \"SharePoint Widget Library\",\r\n"
				+ "    \"services\": [\r\n"
				+ "        \"Communities\"\r\n"
				+ "    ],\r\n"
				+ "    \"state\": \"enabled\",\r\n"
				+ "    \"extensions\": [\r\n"
				+ "        {\r\n"
				+ "            \"ext_id\": \"com.hcl.sharepoint.widget\",\r\n"
				+ "            \"name\": \"SharePoint Library\",\r\n"
				+ "            \"title\": \"SharePoint Library\",\r\n"
				+ "            \"description\": \"SharePoint Library\",\r\n"
				+ "            \"type\": \"community_widget\",\r\n"
				+ "            \"payload\": {\r\n"
				+ "                \"defId\": \"SharePoint Library\",\r\n"
				+ "                \"itemSet\": [\r\n"
				+ "                    {\r\n"
				+ "                        \"name\": \"clientId\",\r\n"
				+ "                        \"value\": \"dabe2132-5b82-4119-818b-ea0cdebc0d48\"\r\n"
				+ "                    },\r\n"
				+ "                    {\r\n"
				+ "                        \"name\": \"tenant\",\r\n"
				+ "                        \"value\": \"hclconnections.onmicrosoft.com\"\r\n"
				+ "                    }\r\n"
				+ "                ],\r\n"
				+ "                \"themes\": \"wpthemeThin wpthemeNarrow wpthemeWide wpthemeBanner\",\r\n"
				+ "                \"modes\": \"view fullpage edit\",\r\n"
				+ "                \"primaryWidget\": \"true\",\r\n"
				+ "                \"showInPalette\": \"true\",\r\n"
				+ "                \"iconUrl\": \"https://sampleconnectionswidget.mybluemix.net/icon.png\",\r\n"
				+ "                \"uniqueInstance\": \"true\",\r\n"
				+ "                \"url\": \"{webresourcesSvcRef}/../../spo/SharepointWidget.xml\"\r\n"
				+ "            }\r\n"
				+ "        }\r\n"
				+ "    ]\r\n"
				+ "}";
		
		return jsonContent;
	}
	
	/**
	 * Return the Navigation app json with unique name and title.
	 * @return string format of json
	 */
	public String createNavigationBarAppJson() {		
		String jsonContent = "{\r\n" + 
				"    \"name\": \"Connections Navigation\",\r\n" + 
				"    \"title\": \"Connections Navigation\",\r\n" + 
				"    \"description\": \"Configuration for the Connections 8 navigation bar.\",\r\n" + 
				"    \"services\": [\r\n" + 
				"        \"Connections\"\r\n" + 
				"    ],\r\n" + 
				"    \"state\": \"enabled\",\r\n" + 
				"    \"extensions\": [\r\n" + 
				"        {\r\n" + 
				"            \"name\": \"connections-nav\",\r\n" + 
				"            \"translations\": {\r\n" + 
				"                \"\": {\r\n" + 
				"                    \"nav.name.homepage\": \"Homepage\"\r\n" + 
				"                },\r\n" + 
				"                \"en\": {\r\n" + 
				"                    \"nav.name.homepage\": \"Homepage\"\r\n" + 
				"                },\r\n" + 
				"                \"de\": {\r\n" + 
				"                    \"nav.name.homepage\": \"Startseite\"\r\n" + 
				"                }\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"com.hcl.connections.nav\",\r\n" + 
				"            \"payload\": {\r\n" + 
				"                \"customEntries\": [\r\n" + 
				"                    {\r\n" + 
				"                        \"id\": \"intranet\",\r\n" + 
				"                        \"name\": \"Customer Intranet\",\r\n" + 
				"                        \"action\": \"add\",\r\n" + 
				"                        \"link\": \"https://intranet.customer.com\",\r\n" + 
				"                        \"icon\": \"https://intranet.customer.com/logo.png\",\r\n" + 
				"                        \"order\": 500,\r\n" + 
				"                        \"submenu\": [],\r\n" + 
				"                        \"location\": \"bottom\"\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"id\": \"homepage\",\r\n" + 
				"                        \"name\": \"%nls:nav.name.homepage\",\r\n" + 
				"                        \"action\": \"update\"\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"id\": \"more\",\r\n" + 
				"                        \"name\": \"More\",\r\n" + 
				"                        \"action\": \"update\",\r\n" + 
				"                        \"submenu\": [\r\n" + 
				"                            {\r\n" + 
				"                                \"id\": \"activities\",\r\n" + 
				"                                \"name\": \"Activities\",\r\n" + 
				"                                \"action\": \"remove\"\r\n" + 
				"                            }\r\n" + 
				"                        ]\r\n" + 
				"                    }\r\n" + 
				"                ]\r\n" + 
				"            },\r\n" + 
				"            \"path\": \"global\",\r\n" + 
				"            \"state\": \"enabled\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		return jsonContent;
				
	}
	
	/**
	 * Return the Navigation app json with cache Expiration time
	 * @return string format of json
	 */
	public String createNavigationBarAppJsonWithCache() {		
		String jsonContent = "{\r\n" + 
				"    \"name\": \"Connections Navigation\",\r\n" + 
				"    \"title\": \"Connections Navigation\",\r\n" + 
				"    \"description\": \"Configuration for the Connections 8 navigation bar.\",\r\n" + 
				"    \"services\": [\r\n" + 
				"        \"Connections\"\r\n" + 
				"    ],\r\n" + 
				"    \"state\": \"enabled\",\r\n" + 
				"    \"extensions\": [\r\n" + 
				"        {\r\n" + 
				"            \"name\": \"connections-nav\",\r\n" + 
				"            \"translations\": {\r\n" + 
				"                \"\": {\r\n" + 
				"                    \"nav.name.homepage\": \"Homepage\"\r\n" + 
				"                },\r\n" + 
				"                \"en\": {\r\n" + 
				"                    \"nav.name.homepage\": \"Homepage\"\r\n" + 
				"                },\r\n" + 
				"                \"de\": {\r\n" + 
				"                    \"nav.name.homepage\": \"Startseite\"\r\n" + 
				"                }\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"com.hcl.connections.nav\",\r\n" + 
				"            \"payload\": {\r\n" + 
				"                \"customEntries\": [\r\n" + 
				"                    {\r\n" + 
				"                        \"id\": \"intranet\",\r\n" + 
				"                        \"name\": \"Customer Intranet\",\r\n" + 
				"                        \"action\": \"add\",\r\n" + 
				"                        \"link\": \"https://intranet.customer.com\",\r\n" + 
				"                        \"icon\": \"https://intranet.customer.com/logo.png\",\r\n" + 
				"                        \"order\": 500,\r\n" + 
				"                        \"submenu\": [],\r\n" + 
				"                        \"location\": \"bottom\"\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"id\": \"homepage\",\r\n" + 
				"                        \"name\": \"%nls:nav.name.homepage\",\r\n" + 
				"                        \"action\": \"update\"\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"id\": \"more\",\r\n" + 
				"                        \"name\": \"More\",\r\n" + 
				"                        \"action\": \"update\",\r\n" + 
				"                        \"submenu\": [\r\n" + 
				"                            {\r\n" + 
				"                                \"id\": \"activities\",\r\n" + 
				"                                \"name\": \"Activities\",\r\n" + 
				"                                \"action\": \"remove\"\r\n" + 
				"                            }\r\n" + 
				"                        ]\r\n" + 
				"                    }\r\n" + 
				"                ],\r\n" +
				"               \"cacheExpiration\" :2000 \r\n" +  
				"            },\r\n" + 
				"            \"path\": \"global\",\r\n" + 
				"            \"state\": \"enabled\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		return jsonContent;
				
	}

	/**
	 * Return the Navigation app json with cache Expiration time
	 * @return string format of json
	 */
	public String updateNavigationBarAppJsonWithCache() {		
		String jsonContent = "{\r\n" + 
				"    \"name\": \"Connections Navigation\",\r\n" + 
				"    \"title\": \"Connections Navigation\",\r\n" + 
				"    \"description\": \"Configuration for the Connections 8 navigation bar.\",\r\n" + 
				"    \"services\": [\r\n" + 
				"        \"Connections\"\r\n" + 
				"    ],\r\n" + 
				"    \"state\": \"enabled\",\r\n" + 
				"    \"extensions\": [\r\n" + 
				"        {\r\n" + 
				"            \"name\": \"connections-nav\",\r\n" + 
				"            \"translations\": {\r\n" + 
				"                \"\": {\r\n" + 
				"                    \"nav.name.homepage\": \"Homepage\"\r\n" + 
				"                },\r\n" + 
				"                \"en\": {\r\n" + 
				"                    \"nav.name.homepage\": \"Homepage\"\r\n" + 
				"                },\r\n" + 
				"                \"de\": {\r\n" + 
				"                    \"nav.name.homepage\": \"Startseite\"\r\n" + 
				"                }\r\n" + 
				"            },\r\n" + 
				"            \"type\": \"com.hcl.connections.nav\",\r\n" + 
				"            \"payload\": {\r\n" + 
				"                \"customEntries\": [\r\n" + 
				"                    {\r\n" + 
				"                        \"id\": \"intranet\",\r\n" + 
				"                        \"name\": \"Customer Intranet1\",\r\n" + 
				"                        \"action\": \"add\",\r\n" + 
				"                        \"link\": \"https://intranet1.customer.com\",\r\n" + 
				"                        \"icon\": \"https://intranet1.customer.com/logo.png\",\r\n" + 
				"                        \"order\": 500,\r\n" + 
				"                        \"submenu\": [],\r\n" + 
				"                        \"location\": \"bottom\"\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"id\": \"homepage\",\r\n" + 
				"                        \"name\": \"%nls:nav.name.homepage\",\r\n" + 
				"                        \"action\": \"update\"\r\n" + 
				"                    },\r\n" + 
				"                    {\r\n" + 
				"                        \"id\": \"more\",\r\n" + 
				"                        \"name\": \"More\",\r\n" + 
				"                        \"action\": \"update\",\r\n" + 
				"                        \"submenu\": [\r\n" + 
				"                            {\r\n" + 
				"                                \"id\": \"activities\",\r\n" + 
				"                                \"name\": \"Activities\",\r\n" + 
				"                                \"action\": \"remove\"\r\n" + 
				"                            }\r\n" + 
				"                        ]\r\n" + 
				"                    }\r\n" + 
				"                ],\r\n" +
				"               \"cacheExpiration\" :3000 \r\n" +  
				"            },\r\n" + 
				"            \"path\": \"global\",\r\n" + 
				"            \"state\": \"enabled\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		return jsonContent;			
	}	
	
	/**
	 * Return the Connections Custom Style - Fiesta app json with unique name and title.
	 * @return string format of json
	 */
	public String createConnectionsCustomStyleFiestaAppJson() {		
		String jsonContent = "{\r\n" + 
				"   \"name\":\"Connections Custom Style - Fiesta\",\r\n" + 
				"   \"title\":\"Connections Custom Style - Fiesta\",\r\n" + 
				"   \"description\":\"Customized the Connections 8 navigation bar style.\",\r\n" + 
				"   \"services\":[\r\n" + 
				"      \"Connections\"\r\n" + 
				"   ],\r\n" + 
				"   \"state\":\"enabled\",\r\n" + 
				"   \"extensions\":[\r\n" + 
				"      {\r\n" + 
				"         \"name\":\"connections-custom-style-fiesta\",\r\n" + 
				"         \"type\":\"com.hcl.connections.custom.style\",\r\n" + 
				"         \"payload\":{\r\n" + 
				"            \"style-customization\":{\r\n" + 
				"               \"generic\":{\r\n" + 
				"                  \"--color-base-background\":\"#181818\",\r\n" + 
				"                  \"--color-main-background\":\"#262626\",\r\n" + 
				"                  \"--color-main-secondary-background\":\"#363636\",\r\n" + 
				"                  \"--color-main-hover-background\":\"#474747\",\r\n" + 
				"                  \"--color-main-border\":\"#474747\",\r\n" + 
				"                  \"--color-main-box-shadow\":\"255 255 255\",\r\n" + 
				"                  \"--color-header\":\"#0f1e2f\",\r\n" + 
				"                  \"--color-footer\":\"#0f1e2f\",\r\n" + 
				"                  \"--color-navigation\":\"#092B51\",\r\n" + 
				"                  \"--color-navigation-selected\":\"#0c3461\",\r\n" + 
				"                  \"--color-text-primary\":\"#FFFFFF\",\r\n" + 
				"                  \"--color-text-secondary\":\"#DAE1E6\",\r\n" + 
				"                  \"--color-tab\":\"#181818\",\r\n" + 
				"                  \"--color-tab-text\":\"#DAE1E6\",\r\n" + 
				"                  \"--color-tab-text-selected\":\"#0780C7\",\r\n" + 
				"                  \"--color-tab-border-selected\":\"#01539B\",\r\n" + 
				"                  \"--color-link\":\"#0780C7\",\r\n" + 
				"                  \"--color-link-hover\":\"#01539B\",\r\n" + 
				"                  \"--color-button\":\"#0780C7\",\r\n" + 
				"                  \"--color-button-hover\":\"#01539B\",\r\n" + 
				"                  \"--color-itmbar\":\"#262626\",\r\n" + 
				"                  \"--color-scrollbar\":\"#474747\",\r\n" + 
				"                  \"--color-scrollbar-thumb\":\"#737373\",\r\n" + 
				"                  \"--color-scrollbar-thumb-hover\":\"#8f8f8f\"\r\n" + 
				"               },\r\n" + 
				"               \"top-navigation\":{\r\n" + 
				"                  \"logo\":{\r\n" + 
				"                     \"position\":\"right\",\r\n" + 
				"                     \"order\":\"1\"\r\n" + 
				"                  },\r\n" + 
				"                  \"search\":{\r\n" + 
				"                     \"position\":\"left\",\r\n" + 
				"                     \"order\":\"2\"\r\n" + 
				"                  },\r\n" + 
				"                  \"text\":{\r\n" + 
				"                     \"position\":\"center\",\r\n" + 
				"                     \"content\":\"Test Environment\"\r\n" + 
				"                  },\r\n" + 
				"                  \"actions\":{\r\n" + 
				"                     \"position\":\"left\",\r\n" + 
				"                     \"order\":\"3\"\r\n" + 
				"                  }\r\n" + 
				"               }\r\n" + 
				"            }\r\n" + 
				"         },\r\n" + 
				"         \"path\":\"global\",\r\n" + 
				"         \"state\":\"enabled\"\r\n" + 
				"      }\r\n" + 
				"   ]\r\n" + 
				"}";
		return jsonContent;
				
	}
	
	/**
	 * Return the Connections Custom Style - Dark app json with unique name and title.
	 * @return string format of json
	 */
	public String createConnectionsCustomStyleDarkAppJson() {		
		String jsonContent = "{\r\n" + 
				"   \"name\":\"Connections Custom Style - Dark\",\r\n" + 
				"   \"title\":\"Connections Custom Style - Dark\",\r\n" + 
				"   \"description\":\"Customized the Connections 8 navigation bar style.\",\r\n" + 
				"   \"services\":[\r\n" + 
				"      \"Connections\"\r\n" + 
				"   ],\r\n" + 
				"   \"state\":\"enabled\",\r\n" + 
				"   \"extensions\":[\r\n" + 
				"      {\r\n" + 
				"         \"name\":\"connections-custom-style-dark\",\r\n" + 
				"         \"type\":\"com.hcl.connections.custom.style\",\r\n" + 
				"         \"payload\":{\r\n" + 
				"            \"style-customization\":{\r\n" + 
				"               \"generic\":{\r\n" + 
				"                  \"--color-base-background\":\"#181818\",\r\n" + 
				"                  \"--color-main-background\":\"#262626\",\r\n" + 
				"                  \"--color-main-secondary-background\":\"#363636\",\r\n" + 
				"                  \"--color-main-hover-background\":\"#474747\",\r\n" + 
				"                  \"--color-main-border\":\"#474747\",\r\n" + 
				"                  \"--color-main-box-shadow\":\"255 255 255\",\r\n" + 
				"                  \"--color-header\":\"#0f1e2f\",\r\n" + 
				"                  \"--color-footer\":\"#0f1e2f\",\r\n" + 
				"                  \"--color-navigation\":\"#092B51\",\r\n" + 
				"                  \"--color-navigation-selected\":\"#0c3461\",\r\n" + 
				"                  \"--color-text-primary\":\"#FFFFFF\",\r\n" + 
				"                  \"--color-text-secondary\":\"#DAE1E6\",\r\n" + 
				"                  \"--color-tab\":\"#181818\",\r\n" + 
				"                  \"--color-tab-text\":\"#DAE1E6\",\r\n" + 
				"                  \"--color-tab-text-selected\":\"#0780C7\",\r\n" + 
				"                  \"--color-tab-border-selected\":\"#01539B\",\r\n" + 
				"                  \"--color-link\":\"#0780C7\",\r\n" + 
				"                  \"--color-link-hover\":\"#01539B\",\r\n" + 
				"                  \"--color-button\":\"#0780C7\",\r\n" + 
				"                  \"--color-button-hover\":\"#01539B\",\r\n" + 
				"                  \"--color-itmbar\":\"#262626\",\r\n" + 
				"                  \"--color-scrollbar\":\"#474747\",\r\n" + 
				"                  \"--color-scrollbar-thumb\":\"#737373\",\r\n" + 
				"                  \"--color-scrollbar-thumb-hover\":\"#8f8f8f\"\r\n" + 
				"               }\r\n" + 
				"            }\r\n" + 
				"         },\r\n" + 
				"         \"path\":\"global\",\r\n" + 
				"         \"state\":\"enabled\"\r\n" + 
				"      }\r\n" + 
				"   ]\r\n" + 
				"}";
		return jsonContent;			
	}
	
	/**
	 * Return the Connections Custom Style - Fiesta app json with unique name ,title and cacheExpiration.
	 * @return string format of json
	 */
	public String createConnectionsCustomStyleFiestaAppJsonWithCache() {		
		String jsonContent = "{\r\n" + 
				"   \"name\":\"Connections Custom Style - Fiesta\",\r\n" + 
				"   \"title\":\"Connections Custom Style - Fiesta\",\r\n" + 
				"   \"description\":\"Customized the Connections 8 navigation bar style.\",\r\n" + 
				"   \"services\":[\r\n" + 
				"      \"Connections\"\r\n" + 
				"   ],\r\n" + 
				"   \"state\":\"enabled\",\r\n" + 
				"   \"extensions\":[\r\n" + 
				"      {\r\n" + 
				"         \"name\":\"connections-custom-style-fiesta\",\r\n" + 
				"         \"type\":\"com.hcl.connections.custom.style\",\r\n" + 
				"         \"payload\":{\r\n" + 
				"            \"style-customization\":{\r\n" + 
				"               \"generic\":{\r\n" + 
				"                  \"--color-base-background\":\"#181818\",\r\n" + 
				"                  \"--color-main-background\":\"#262626\",\r\n" + 
				"                  \"--color-main-secondary-background\":\"#363636\",\r\n" + 
				"                  \"--color-main-hover-background\":\"#474747\",\r\n" + 
				"                  \"--color-main-border\":\"#474747\",\r\n" + 
				"                  \"--color-main-box-shadow\":\"255 255 255\",\r\n" + 
				"                  \"--color-header\":\"#0f1e2f\",\r\n" + 
				"                  \"--color-footer\":\"#0f1e2f\",\r\n" + 
				"                  \"--color-navigation\":\"#092B51\",\r\n" + 
				"                  \"--color-navigation-selected\":\"#0c3461\",\r\n" + 
				"                  \"--color-text-primary\":\"#FFFFFF\",\r\n" + 
				"                  \"--color-text-secondary\":\"#DAE1E6\",\r\n" + 
				"                  \"--color-tab\":\"#181818\",\r\n" + 
				"                  \"--color-tab-text\":\"#DAE1E6\",\r\n" + 
				"                  \"--color-tab-text-selected\":\"#0780C7\",\r\n" + 
				"                  \"--color-tab-border-selected\":\"#01539B\",\r\n" + 
				"                  \"--color-link\":\"#0780C7\",\r\n" + 
				"                  \"--color-link-hover\":\"#01539B\",\r\n" + 
				"                  \"--color-button\":\"#0780C7\",\r\n" + 
				"                  \"--color-button-hover\":\"#01539B\",\r\n" + 
				"                  \"--color-itmbar\":\"#262626\",\r\n" + 
				"                  \"--color-scrollbar\":\"#474747\",\r\n" + 
				"                  \"--color-scrollbar-thumb\":\"#737373\",\r\n" + 
				"                  \"--color-scrollbar-thumb-hover\":\"#8f8f8f\"\r\n" + 
				"               },\r\n" + 
				"               \"top-navigation\":{\r\n" + 
				"                  \"logo\":{\r\n" + 
				"                     \"position\":\"right\",\r\n" + 
				"                     \"order\":\"1\"\r\n" + 
				"                  },\r\n" + 
				"                  \"search\":{\r\n" + 
				"                     \"position\":\"left\",\r\n" + 
				"                     \"order\":\"2\"\r\n" + 
				"                  },\r\n" + 
				"                  \"text\":{\r\n" + 
				"                     \"position\":\"center\",\r\n" + 
				"                     \"content\":\"Test Environment\"\r\n" + 
				"                  },\r\n" + 
				"                  \"actions\":{\r\n" + 
				"                     \"position\":\"left\",\r\n" + 
				"                     \"order\":\"3\"\r\n" + 
				"                  }\r\n" + 
				"               }\r\n" + 
				"            },\r\n" + 
				"            \"cacheExpiration\" :3000 \r\n" +  
				"         },\r\n" + 
				"         \"path\":\"global\",\r\n" + 
				"         \"state\":\"enabled\"\r\n" + 
				"      }\r\n" + 
				"   ]\r\n" + 
				"}";
		return jsonContent;				
	}
	
	/**
	 * Return the Connections Custom Style - Fiesta app json with unique name ,title and cacheExpiration.
	 * @return string format of json
	 */
	public String updateConnectionsCustomStyleFiestaAppJsonWithCache() {		
		String jsonContent = "{\r\n" + 
				"   \"name\":\"Connections Custom Style - Fiesta\",\r\n" + 
				"   \"title\":\"Connections Custom Style - Fiesta\",\r\n" + 
				"   \"description\":\"Customized the Connections 8 navigation bar style.\",\r\n" + 
				"   \"services\":[\r\n" + 
				"      \"Connections\"\r\n" + 
				"   ],\r\n" + 
				"   \"state\":\"enabled\",\r\n" + 
				"   \"extensions\":[\r\n" + 
				"      {\r\n" + 
				"         \"name\":\"connections-custom-style-fiesta\",\r\n" + 
				"         \"type\":\"com.hcl.connections.custom.style\",\r\n" + 
				"         \"payload\":{\r\n" + 
				"            \"style-customization\":{\r\n" + 
				"               \"generic\":{\r\n" + 
				"                  \"--color-base-background\":\"#181818\",\r\n" + 
				"                  \"--color-main-background\":\"#262626\",\r\n" + 
				"                  \"--color-main-secondary-background\":\"#363636\",\r\n" + 
				"                  \"--color-main-hover-background\":\"#474747\",\r\n" + 
				"                  \"--color-main-border\":\"#474747\",\r\n" + 
				"                  \"--color-main-box-shadow\":\"255 255 255\",\r\n" + 
				"                  \"--color-header\":\"#0f1e2f\",\r\n" + 
				"                  \"--color-footer\":\"#0f1e2f\",\r\n" + 
				"                  \"--color-navigation\":\"#092B51\",\r\n" + 
				"                  \"--color-navigation-selected\":\"#0c3461\",\r\n" + 
				"                  \"--color-text-primary\":\"#FFFFFF\",\r\n" + 
				"                  \"--color-text-secondary\":\"#DAE1E6\",\r\n" + 
				"                  \"--color-tab\":\"#181818\",\r\n" + 
				"                  \"--color-tab-text\":\"#DAE1E6\",\r\n" + 
				"                  \"--color-tab-text-selected\":\"#0780C7\",\r\n" + 
				"                  \"--color-tab-border-selected\":\"#01539B\",\r\n" + 
				"                  \"--color-link\":\"#0780C7\",\r\n" + 
				"                  \"--color-link-hover\":\"#01539B\",\r\n" + 
				"                  \"--color-button\":\"#0780C7\",\r\n" + 
				"                  \"--color-button-hover\":\"#01539B\",\r\n" + 
				"                  \"--color-itmbar\":\"#262626\",\r\n" + 
				"                  \"--color-scrollbar\":\"#474747\",\r\n" + 
				"                  \"--color-scrollbar-thumb\":\"#737373\",\r\n" + 
				"                  \"--color-scrollbar-thumb-hover\":\"#8f8f8f\"\r\n" + 
				"               },\r\n" + 
				"               \"top-navigation\":{\r\n" + 
				"                  \"logo\":{\r\n" + 
				"                     \"position\":\"left\",\r\n" + 
				"                     \"order\":\"1\"\r\n" + 
				"                  },\r\n" + 
				"                  \"search\":{\r\n" + 
				"                     \"position\":\"right\",\r\n" + 
				"                     \"order\":\"2\"\r\n" + 
				"                  },\r\n" + 
				"                  \"text\":{\r\n" + 
				"                     \"position\":\"left\",\r\n" + 
				"                     \"content\":\"Test Environment\"\r\n" + 
				"                  },\r\n" + 
				"                  \"actions\":{\r\n" + 
				"                     \"position\":\"center\",\r\n" + 
				"                     \"order\":\"3\"\r\n" + 
				"                  }\r\n" + 
				"               }\r\n" + 
				"            },\r\n" + 
				"            \"cacheExpiration\" :3000 \r\n" +  
				"         },\r\n" + 
				"         \"path\":\"global\",\r\n" + 
				"         \"state\":\"enabled\"\r\n" + 
				"      }\r\n" + 
				"   ]\r\n" + 
				"}";
		return jsonContent;				
	}
	
}
