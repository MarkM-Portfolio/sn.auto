package com.ibm.conn.auto.tests.mentions.regression;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.HomepageUI;

public class Mentions_Regression extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Mentions_Regression.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;
	private User user, mentionUser1;
	private String testMsg = "  test message";
	private String partial;
	private String complete;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		
		user = cfg.getUserAllocator().getUser(this);
		mentionUser1 = cfg.getUserAllocator().getUser(this);
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		partial = "@" + mentionUser1.getFirstName();
		complete = mentionUser1.getDisplayName();
				
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of a mention, a text and a partial mention in the Status Update text area on the Homepage.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>In Status Update text area, enter a mention for another user.
	*<li><B>Step: </B>Add a text to the text area.
	*<li><B>Step: </B>Add a partial mention for the user to the text area using the first name only.
	*<li><B>Verify: </B>The text area contains the complete mention, the text and the partial mention.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeCompleteMentionTextPartialMention() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Selects the user from the typeahead
		logger.strongStep("Type the mention for the user: " + complete + " in the text area");
		log.info("INFO: Enter the mention for the user: " + complete + " in the text area");
		ui.selectAtMention(complete);
		
		//Type plain text
		logger.strongStep("Add the text '" + testMsg + "' to the text area");
		log.info("INFO: Add the text '" + testMsg + "' to the text area");
		appendStatus(testMsg);
		
		//Type partial mention
		logger.strongStep("Add a partial mention for the user: " + complete + " to the text area using the first name only");
		log.info("INFO: Add a partial mention for the user: " + complete + " to the text area using the first name only");
		appendStatus(partial);
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "@" + complete + testMsg + partial;
		logger.strongStep("Verify the Status Update text area contains: " + expected);
		log.info("INFO: Validate that the Status Update text area contains: " + expected);
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of a text followed by a partial mention in the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a text to the Status Update text area.
	*<li><B>Step: </B>Add a partial mention for another user to the text area using the first name only.
	*<li><B>Verify: </B>The text area contains the text and the partial mention.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeTextPartialMention() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type plain text
		logger.strongStep("Type the text '" + testMsg + "' in the text area");
		log.info("INFO: Enter the text '" + testMsg + "' in the text area");
		appendStatus(testMsg);
		
		//Type partial mention
		logger.strongStep("Add a partial mention for the user: " + complete + " to the text area using the first name only");
		log.info("INFO: Add a partial mention for the user: " + complete + " to the text area using the first name only");
		appendStatus(partial);
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = testMsg + partial;
		logger.strongStep("Verify the Status Update text area contains: " + expected);
		log.info("INFO: Validate that the Status Update text area contains: " + expected);
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of a partial mention followed by a text in the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a partial mention for another user in the Status Update text area using the first name only.
	*<li><B>Step: </B>Add a text to the Status Update text area.
	*<li><B>Verify: </B>The text area contains the partial mention and the text.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typePartialMentionText() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type partial mention
		logger.strongStep("Type a partial mention for the user: " + complete + " in the text area using the first name only");
		log.info("INFO: Type a partial mention for the user: " + complete + " in the text area using the first name only");
		appendStatus(partial);
		
		//Type plain text
		logger.strongStep("Add the text '" + testMsg + "' to the text area");
		log.info("INFO: Add the text '" + testMsg + "' to the text area");
		appendStatus(testMsg);

		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = partial + testMsg;
		logger.strongStep("Verify the Status Update text area contains: " + expected);
		log.info("INFO: Validate that the Status Update text area contains: " + expected);
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of a mention followed by a text in the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>In Status Update text area, type a mention for another user.
	*<li><B>Step: </B>Add a text to the Status Update text area.
	*<li><B>Verify: </B>The text area contains the mention and the text.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeCompleteMentionText() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Selects the user from the typeahead
		logger.strongStep("Type the mention for the user: " + complete + " in the text area");
		log.info("INFO: Enter the mention for the user: " + complete + " in the text area");
		ui.selectAtMention(complete);
		
		//Type plain text
		logger.strongStep("Add the text '" + testMsg + "' to the text area");
		log.info("INFO: Add the text '" + testMsg + "' to the text area");
		appendStatus(testMsg);
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "@" + complete + testMsg;
		logger.strongStep("Verify the Status Update text area contains: " + expected);
		log.info("INFO: Validate that the Status Update text area contains: " + expected);
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of a text followed by a mention in the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a text in the Status Update text area.
	*<li><B>Step: </B>Add a mention for another user to the text area.
	*<li><B>Verify: </B>The text area contains the text and the mention.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeTextCompleteMention() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type plain text
		logger.strongStep("Type the text '" + testMsg + "' in the text area");
		log.info("INFO: Enter the text '" + testMsg + "' in the text area");
		appendStatus(testMsg + " ");
		
		//Selects the user from the typeahead
		logger.strongStep("Add the mention for the user: " + complete + " to the text area");
		log.info("INFO: Add the mention for the user: " + complete + " to the text area");
		ui.selectAtMention(complete);

		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = testMsg + " @" + complete;
		logger.strongStep("Verify the Status Update text area contains: " + expected);
		log.info("INFO: Validate that the Status Update text area contains: " + expected);
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of a mention followed by a partial mention in the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a mention for another user in the text area followed by space.
	*<li><B>Step: </B>Add a partial mention for another user to the text area.
	*<li><B>Verify: </B>The text area contains the complete mention, the space and the partial mention.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeCompleteMentionPartialMention() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Types the user and selects from typeahead
		logger.strongStep("Type the mention for the user: " + complete + " in the text area");
		log.info("INFO: Enter the mention for the user: " + complete + " in the text area");
		ui.selectAtMention(complete);
		
		//Type space
		logger.strongStep("Add space in the text area");
		log.info("INFO: Add space in the text area");
		appendStatus(" ");
		
		//Type partial mention
		logger.strongStep("Add a partial mention for the user: " + complete + " to the text area using the first name only");
		log.info("INFO: Add a partial mention for the user: " + complete + " to the text area using the first name only");
		appendStatus(partial);
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "@" + complete + " " + partial;
		logger.strongStep("Verify the Status Update text area contains: " + expected);
		log.info("INFO: Validate that the Status Update text area contains: " + expected);
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Backspace key to delete a mention from the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a mention for another user in the Status Update text area.
	*<li><B>Step: </B>Hit the Backspace key as many times as the length of the mention to clear everything from the text area.
	*<li><B>Verify: </B>The text area does not contain the mention and is empty.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeCompleteMentionBackspace() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Types the user and selects from typeahead
		logger.strongStep("Type the mention for the user: " + complete + " in the text area");
		log.info("INFO: Enter the mention for the user: " + complete + " in the text area");
		ui.selectAtMention(complete);
			
		//Type backspace to delete the mention
		logger.strongStep("Hit the Backspace key as many times as the length of the mention to clear everything from the text area");
		log.info("INFO: Press the Backspace key as many times as the length of the mention to clear everything from the text area");
		for(int i = 0; i < 14; i++) {
			driver.typeNative(Keys.BACK_SPACE);
		}
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "";
		logger.strongStep("Verify the Status Update text area has no contents");
		log.info("INFO: Validate that the Status Update text area contains nothing");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Backspace key to delete a partial mention from the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a partial mention for another user in the Status Update text area.
	*<li><B>Step: </B>Hit the Backspace key as many times as the length of the partial mention to clear everything from the text area.
	*<li><B>Verify: </B>The text area does not contain the partial mention and is empty.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typePartialMentionBackspace() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type partial mention
		logger.strongStep("Type a partial mention for the user: " + complete + " in the text area using the first name only");
		log.info("INFO: Type a partial mention for the user: " + complete + " in the text area using the first name only");
		appendStatus(partial);
			
		//Type backspace 4 times to delete "@amy"
		logger.strongStep("Hit the Backspace key as many times as the length of the partial mention to clear everything from the text area");
		log.info("INFO: Press the Backspace key as many times as the length of the partial mention to clear everything from the text area");
		for(int i = 0; i < 5; i++) {
			driver.typeNative(Keys.BACK_SPACE);
		}
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "";
		logger.strongStep("Verify the Status Update text area has no contents");
		log.info("INFO: Validate that the Status Update text area contains nothing");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Backspace key to delete the '@' sign from the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Type the '@' sign in the text area.
	*<li><B>Step: </B>Hit the Backspace key once.
	*<li><B>Verify: </B>The text area does not contain the '@' sign and is empty.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeAtSignBackspace() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type @ sign
		logger.strongStep("Type the '@' sign in the text area");
		log.info("INFO: Input the '@' sign in the text area");
		appendStatus("@");
			
		//Type backspace to delete the mention
		logger.strongStep("Hit the Backspace key once");
		log.info("INFO: Press the Backspace key once");
		driver.typeNative(Keys.BACK_SPACE);
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "";
		logger.strongStep("Verify the Status Update text area has no contents");
		log.info("INFO: Validate that the Status Update text area contains nothing");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Backspace key to delete a text from the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a text in the Status Update text area.
	*<li><B>Step: </B>Hit the Backspace key as many times as the length of the text to clear everything from the text area.
	*<li><B>Verify: </B>The text area does not contain the text and is empty.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeTextBackspace() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type plain text
		logger.strongStep("Type the text '" + testMsg + "' in the text area");
		log.info("INFO: Enter the text '" + testMsg + "' in the text area");
		appendStatus(testMsg);
			
		//Type backspace 15 times to delete "  test message "
		logger.strongStep("Hit the Backspace key as many times as the length of the text to clear everything from the text area");
		log.info("INFO: Press the Backspace key as many times as the length of the text to clear everything from the text area");
		for(int i = 0; i < 16; i++) {
			driver.typeNative(Keys.BACK_SPACE);
		}
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "";
		logger.strongStep("Verify the Status Update text area has no contents");
		log.info("INFO: Validate that the Status Update text area contains nothing");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Enter key to add a new line in the Status Update text area and the usage of the Backspace key to delete a text from the text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a text in the Status Update text area.
	*<li><B>Step: </B>Hit the Enter key once.
	*<li><B>Step: </B>Hit the Backspace key one more time than the length of the text to clear everything from the text area.
	*<li><B>Verify: </B>The text area is empty and has no contents.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeTextEnterBackspace() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type plain text
		logger.strongStep("Type the text '" + testMsg + "' in the text area");
		log.info("INFO: Enter the text '" + testMsg + "' in the text area");
		appendStatus(testMsg);
		
		//Type Enter
		logger.strongStep("Hit the Enter key once");
		log.info("INFO: Press the Enter key once");
		driver.typeNative(Keys.ENTER);
	
		//Type backspace 16 times to delete "  test message " + \n
		logger.strongStep("Hit the Backspace key one more time than the length of the text to clear everything from the text area");
		log.info("INFO: Press the Backspace key one more time than the length of the text to clear everything from the text area");
		for(int i = 0; i < 17; i++) {
			driver.typeNative(Keys.BACK_SPACE);
		}
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "";
		logger.strongStep("Verify the Status Update text area has no contents");
		log.info("INFO: Validate that the Status Update text area contains nothing");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Enter key to add a new line in the Status Update text area and the usage of the Backspace key to delete a mention from the text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a mention for another user in the Status Update text area.
	*<li><B>Step: </B>Hit the Enter key once.
	*<li><B>Step: </B>Hit the Backspace key one more time than the length of the mention to clear everything from the text area.
	*<li><B>Verify: </B>The text area is empty and has no contents.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeCompleteMentionEnterBackspace() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Types the user and selects from typeahead
		logger.strongStep("Type the mention for the user: " + complete + " in the text area");
		log.info("INFO: Enter the mention for the user: " + complete + " in the text area");
		ui.selectAtMention(complete);
		
		//Type Enter
		logger.strongStep("Hit the Enter key once");
		log.info("INFO: Press the Enter key once");
		driver.typeNative(Keys.ENTER);
	
		//Type backspace 2 times to delete mention + \n
		logger.strongStep("Hit the Backspace key one more time than the length of the mention to clear everything from the text area");
		log.info("INFO: Press the Backspace key one more time than the length of the mention to clear everything from the text area");
		for(int i = 0; i < 15; i++) {
			driver.typeNative(Keys.BACK_SPACE);
		}
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "";
		logger.strongStep("Verify the Status Update text area has no contents");
		log.info("INFO: Validate that the Status Update text area contains nothing");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Enter key to add new lines in the Status Update text area and the usage of the Backspace key to delete a mention, a text and a partial mention from the text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a mention for another user in the Status Update text area.
	*<li><B>Step: </B>Hit the Enter key once.
	*<li><B>Step: </B>Add a text to the text area.
	*<li><B>Step: </B>Hit the Enter key once.
	*<li><B>Step: </B>Add a partial mention for the user to the text area using the first name only.
	*<li><B>Step: </B>Hit the Enter key once.
	*<li><B>Step: </B>Hit the Backspace key as many times as the total number of characters in the text area.
	*<li><B>Verify: </B>The text area is empty and has no contents.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeCompleteMentionEnterTextEnterPartialMentionEnterBackspace() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);

		//Types the user and selects from typeahead
		logger.strongStep("Type the mention for the user: " + complete + " in the text area");
		log.info("INFO: Enter the mention for the user: " + complete + " in the text area");
		ui.switchToTopFrame();
		ui.selectAtMention(complete);
		
		//Type Enter
		logger.strongStep("Hit the Enter key once");
		log.info("INFO: Press the Enter key once");
		driver.typeNative(Keys.ENTER);
		
		//Type plain text
		logger.strongStep("Add the text '" + testMsg + "' to the text area");
		log.info("INFO: Add the text '" + testMsg + "' to the text area");
		appendStatus(testMsg);
		
		//Type Enter
		logger.strongStep("Hit the Enter key once");
		log.info("INFO: Press the Enter key once");
		driver.typeNative(Keys.ENTER);
		
		//Type partial mention
		logger.strongStep("Add a partial mention for the user: " + complete + " to the text area using the first name only");
		log.info("INFO: Add a partial mention for the user: " + complete + " to the text area using the first name only");
		appendStatus(partial);
		
		//Type Enter
		logger.strongStep("Hit the Enter key once");
		log.info("INFO: Press the Enter key once");
		driver.typeNative(Keys.ENTER);
	
		//Type backspace 32 times to delete mention + \n + "  test message " + \n + "@amy"
		logger.strongStep("Hit the Backspace key as many times as the total number of characters in the text area");
		log.info("INFO: Press the Backspace as many times as the total number of characters in the text area");
		for(int i = 0; i < 32; i++) {
			ui.sleep(100);
			driver.typeNative(Keys.BACK_SPACE);
		}

		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "";
		logger.strongStep("Verify the Status Update text area has no contents");
		log.info("INFO: Validate that the Status Update text area contains nothing");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the input of '@' sign followed by a press of the Tab key in the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Type the '@' sign in the text area.
	*<li><B>Step: </B>Hit the Tab key once.
	*<li><B>Verify: </B>The Status Update text area contains only the '@' sign.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeAtSignTab() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type @ sign
		logger.strongStep("Type the '@' sign in the text area");
		log.info("INFO: Enter the '@' sign in the text area");
		appendStatus("@");
		
		//Type Tab
		logger.strongStep("Hit the Tab key once");
		log.info("INFO: Press the Tab key once");
		driver.typeNative(Keys.TAB);

		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "@";
		logger.strongStep("Verify the Status Update text area contains only the '@' sign");
		log.info("INFO: Validate that the Status Update text area contains only the '@' sign");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Home key followed by a single press of the Delete key to delete a mention from the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a mention for another user in the Status Update text area.
	*<li><B>Step: </B>Hit the Home key once.
	*<li><B>Step: </B>Hit the Delete key once.
	*<li><B>Verify: </B>The text area does not contain the mention and is empty.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeCompleteMentionHomeDelete() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		CalendarUI calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Types the user and selects from typeahead
		logger.strongStep("Type the mention for the user: " + complete + " in the text area");
		log.info("INFO: Enter the mention for the user: " + complete + " in the text area");
		ui.selectAtMention(complete);
		
		//Type Home
		logger.strongStep("Hit the Home key once");
		log.info("INFO: Press the Home key once");
		driver.typeNative(Keys.HOME);
		
		//Type Delete
		logger.strongStep("Hit the Delete key once");
		log.info("INFO: Press the Delete key once");
		driver.typeNative(Keys.DELETE);
		
		//Verify mention link is not present after deleting @
		driver.turnOffImplicitWaits();
		logger.strongStep("Verify the Status Update text area does not contain the mention for " + complete);
		log.info("INFO: Validate that the Status Update text area does not contain the mention for " + complete);
		Assert.assertFalse(driver.isElementPresent(calUI.getMentionPersonLink(complete)),
				"ERROR: mention shows " + complete + " as a link");
		driver.turnOnImplicitWaits();
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Home key followed by multiple presses of the Delete key to delete a text from the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a text in the Status Update text area.
	*<li><B>Step: </B>Hit the Home key once.
	*<li><B>Step: </B>Hit the Delete key as many times as the length of the text to clear everything from the text area.
	*<li><B>Verify: </B>The text area does not contain the text and is empty.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeTextDelete() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type plain text
		logger.strongStep("Type the text '" + testMsg + "' in the text area");
		log.info("INFO: Enter the text '" + testMsg + "' in the text area");
		appendStatus(testMsg);
			
		//Type Home
		logger.strongStep("Hit the Home key once");
		log.info("INFO: Press the Home key once");
		driver.typeNative(Keys.HOME);

		//Type backspace 15 times to delete "  test message "
		logger.strongStep("Hit the Delete key as many times as the length of the text to clear everything from the text area");
		log.info("INFO: Press the Delete key as many times as the length of the text to clear everything from the text area");
		for(int i = 0; i < 16; i++) {
			driver.typeNative(Keys.DELETE);
		}
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "";
		logger.strongStep("Verify the Status Update text area has no contents");
		log.info("INFO: Validate that the Status Update text area contains nothing");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");

		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of Enter key to add new lines and Left Arrow key to go to the beginning of a line in the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a text in the Status Update text area.
	*<li><B>Step: </B>Hit the Enter key once.
	*<li><B>Step: </B>Enter another text in the text area.
	*<li><B>Step: </B>Go to the beginning of the second line by pressing the Left Arrow key multiple times.
	*<li><B>Step: </B>Hit the Enter key again.
	*<li><B>Verify: </B>The text area contains the first text followed by two new lines and then the second text.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeTextEnterMoreTextMoveUpEnterAgain() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type test message
		logger.strongStep("Type the text 'aaa' in the text area");
		log.info("INFO: Enter the text 'aaa' in the text area");
		appendStatus("aaa");
		
		//Type Enter
		logger.strongStep("Hit the Enter key once");
		log.info("INFO: Press the Enter key once");
		driver.typeNative(Keys.ENTER);
	
		//Type test message again
		logger.strongStep("Add the text 'bbb' to the text area");
		log.info("INFO: Add the text 'bbb' to the text area");
		appendStatus("bbb");
		
		//Type left arrow 3 times to get back to end of first line
		logger.strongStep("Hit the Left Arrow key as many times as the length of the second text");
		log.info("INFO: Press the Left Arrow key as many times as the length of the second text");
		for(int i = 0; i < 4; i++) {
			driver.typeNative(Keys.ARROW_LEFT);
		}
		
		//Type Enter
		logger.strongStep("Hit the Enter key again");
		log.info("INFO: Press the Enter key again");
		driver.typeNative(Keys.ENTER);
		
		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "aaa\n\nbbb";
		logger.strongStep("Verify the Status Update text area contains: " + expected);
		log.info("INFO: Validate that the Status Update text area contains: " + expected);
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests if the Comments text area in the EE frame for one post can be used to add mentions and other text.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a text in the Status Update text area.
	*<li><B>Step: </B>Click on the Post link.
	*<li><B>Step: </B>Click on the News Item Container to open the EE frame.
	*<li><B>Step: </B>Switch to the EE frame.
	*<li><B>Verify: </B>The Comments text area appears inside the EE frame.
	*<li><B>Step: </B>In Comments text area, enter a mention for another user.
	*<li><B>Step: </B>Add a text to the text area.
	*<li><B>Verify: </B>The Comments text area inside the EE frame contains the mention followed by the text.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void postStatusTypeMentionInEE() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type plain text
		logger.strongStep("Type the text '" + testMsg + "' in the text area");
		log.info("INFO: Enter the text '" + testMsg + "' in the text area");
		appendStatus(testMsg);
		
		//Post status
		logger.strongStep("Click on the Post link");
		log.info("INFO: Click on the Post link");
		ui.clickLink(HomepageUIConstants.PostStatusUpdate);
		
		//Open EE
		logger.strongStep("Open the EE frame by clicking on the News Item Container");
		log.info("INFO: Click on the News Item Container to open the EE frame");
		driver.getVisibleElements(HomepageUIConstants.Post).get(0).click();;

		//Wait for EE to open
		logger.strongStep("Switch to the EE frame using the inner element: " + HomepageUIConstants.EECommentsTab);
		log.info("INFO: Switch to the EE frame using the inner element: " + HomepageUIConstants.EECommentsTab);
		ui.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab,
						Integer.valueOf(cfg.getFluentwaittime()));
					
		//Wait for EE to load comment field
		logger.strongStep("Verify the Comments text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Comments text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisibleOnce(BaseUIConstants.StatusUpdate_iFrame);
				
		//Select name from typeahead
		logger.strongStep("Type the mention for the user: " + complete + " in the text area");
		log.info("INFO: Enter the mention for the user: " + complete + " in the text area");
		ui.selectAtMention(complete);
						
		//Type more text to make sure box isn't disabled
		logger.strongStep("Verify the Comments text area appears inside the EE frame within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Comments text area appears inside the EE frame within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);
		
		logger.strongStep("Switch to the frame: " + BaseUIConstants.StatusUpdate_iFrame + " to start using the Comments text area");
		log.info("INFO: Switch to the frame containing the Comments text area");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		
		// Click on the text area
		Element updateBody = driver.getSingleElement(BaseUIConstants.StatusUpdate_Body);
		logger.strongStep("Click on the Comments text area, so that the cursor is at the end of any existing text");
		log.info("INFO: Click on the Comments text area, so that the cursor is at the end of any existing text");
		updateBody.click();
		
		logger.strongStep("Add the text '" +testMsg + "' to the Comments text area");
		log.info("INFO: Add status update message '" +testMsg + "' to the Comments text area");
		updateBody.typeWithDelay(testMsg);
		
		//Verify displayed message matches intended message
		String actual = updateBody.getText();
		String expected = "@" + complete + testMsg;
		logger.strongStep("Verify the Comments text area contains: " + expected);
		log.info("INFO: Validate that the Comments text area contains: " + expected);
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests that pressing the Caps Lock key after the '@' sign does not change anything in the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Type the '@' sign in the text area.
	*<li><B>Step: </B>Hit the Caps Lock key once.
	*<li><B>Verify: </B>The Status Update text area contains only the '@' sign.
	*</ul>
	*@throws Exception
	*/
	@Deprecated //The Unicode for Caps Lock does not work with the getKeyFromUnicode() method and there are no other alternatives of simulating the Caps Lock key press.
	@Test(groups = {"regression"}, enabled=false)
	public void typeAtSignCapsLock() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type @ sign
		logger.strongStep("Type the '@' sign in the text area");
		log.info("INFO: Input the '@' sign in the text area");
		appendStatus("@");
		
		//Type Caps Lock
		logger.strongStep("Hit the Caps Lock key once");
		log.info("INFO: Press the Caps Lock key once");
		driver.typeNative(Keys.getKeyFromUnicode('\u21ea'));

		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "@";
		logger.strongStep("Verify the Status Update text area contains only the '@' sign");
		log.info("INFO: Validate that the Status Update text area only contains: " + expected);
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of Ctrl + A followed by the Delete key to delete everything from the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a text in the Status Update text area.
	*<li><B>Step: </B>Select all text from the text area by using the Ctrl + A shortcut.
	*<li><B>Step: </B>Hit the Delete key once.
	*<li><B>Verify: </B>The Status Update text area has no contents.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeTextHighlightDelete() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type character sequence
		logger.strongStep("Type the text 'asdf' in the text area");
		log.info("INFO: Enter the text 'asdf' in the text area");
		appendStatus("asdf");
		
		//Type Ctrl + A
		WebDriver wd = (WebDriver) driver.getBackingObject();
		Actions action = new Actions(wd);
		logger.strongStep("Select all text from the text area by using the Ctrl + A shortcut");
		log.info("INFO: Use the Ctrl + A shortcut to select everything in the text area");
		action.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).build().perform();
		
		//Type Backspace
		logger.strongStep("Hit the Delete key once");
		log.info("INFO: Press the Delete key once");
		driver.typeNative(Keys.DELETE);

		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "";
		logger.strongStep("Verify the Status Update text area has no contents");
		log.info("INFO: Validate that the Status Update text area contains nothing");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of Ctrl + A followed by the Backspace key to delete everything from the Status Update text area.
	*<li><B>Step: </B>Open Homepage and login.
	*<li><B>Verify: </B>The Updates link is apparent in the left navigation menu.
	*<li><B>Step: </B>Click on the Updates link in the left navigation menu of the Homepage.
	*<li><B>Verify: </B>The Status Update text area appears.
	*<li><B>Step: </B>Enter a text in the Status Update text area.
	*<li><B>Step: </B>Select all text from the text area by using the Ctrl + A shortcut.
	*<li><B>Step: </B>Hit the Backspace key once.
	*<li><B>Verify: </B>The Status Update text area has no contents.
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression"})
	public void typeTextHighlightBackspace() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Open Homepage and login: " +user.getDisplayName());
		log.info("INFO: Log into Homepage as " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.strongStep("Verify the Updates link is apparent in the left navigation menu");
		log.info("INFO: Validate that the Updates link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the Updates link in the left navigation menu of the Homepage");
		log.info("INFO: Select the Updates link from the left navigation menu of the Homepage");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for the Status Update text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame);
		
		//Type character sequence
		logger.strongStep("Type the text 'asdf' in the text area");
		log.info("INFO: Enter the text 'asdf' in the text area");
		appendStatus("asdf");
		
		//Type Ctrl + A
		WebDriver wd = (WebDriver) driver.getBackingObject();
		Actions action = new Actions(wd);
		logger.strongStep("Select all text from the text area by using the Ctrl + A shortcut");
		log.info("INFO: Use the Ctrl + A shortcut to select everything in the text area");
		action.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).build().perform();

		//Type Backspace
		logger.strongStep("Hit the Backspace key once");
		log.info("INFO: Press the Backspace key once");
		driver.typeNative(Keys.BACK_SPACE);

		//Verify displayed message matches intended message
		String actual = getCKEditorContents();
		String expected = "";
		logger.strongStep("Verify the Status Update text area has no contents");
		log.info("INFO: Validate that the Status Update text area contains nothing");
		Assert.assertEquals(actual, expected, "ERROR: status update content is incorrect");
		
		ui.endTest();		
	}
	
	private String getCKEditorContents() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		logger.strongStep("Switch to the frame: " + BaseUIConstants.CKEditor_iFrame + " using the inner element " + BaseUIConstants.StatusUpdate_Body);
		ui.switchToFrame(BaseUIConstants.CKEditor_iFrame, BaseUIConstants.StatusUpdate_Body);
		String actualText = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_Body).getText();
		logger.strongStep("Switch back to the parent frame");
		ui.switchToTopFrame();
		return actualText;
	}
	
	private void appendStatus(CharSequence status) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Enter the text
		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		ui.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);
		
		logger.strongStep("Switch to the frame: " + BaseUIConstants.StatusUpdate_iFrame + " to start using the Status Update text area");
		log.info("INFO: Switch to the frame containing the Status Update text area");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		// Click on the lower right of the element, so that the cursor is at
		// the end of any existing text
		Element updateBody = driver.getSingleElement(BaseUIConstants.StatusUpdate_Body);
		int updateBodyWidth = updateBody.getSize().width;
		int updateBodyHeight = updateBody.getSize().height;
		logger.strongStep("Click on the lower right part of the text area, so that the cursor is at the end of any existing text");
		log.info("INFO: Click on the lower right part of the text area, so that the cursor is at the end of any existing text");
		updateBody.clickAt(updateBodyWidth - 440, updateBodyHeight - 20);
		
		logger.strongStep("Enter the status update message: " + status);
		log.info("INFO: Type status update message: " + status);
		updateBody.typeWithDelay(status);
		driver.switchToFrame().returnToTopFrame();
	}
}
