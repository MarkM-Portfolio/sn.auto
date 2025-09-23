package com.ibm.conn.auto.tests.mentions;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.webui.HomepageUI;

public class BVT_Homepage_Mentions extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Homepage_Mentions.class);
	
	private HomepageUI ui;
	private TestConfigCustom cfg;
	private User user, mentionUser1, mentionUser2;
	private String partial;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		user = cfg.getUserAllocator().getUser();
		mentionUser1 = cfg.getUserAllocator().getUser();
		mentionUser2 = cfg.getUserAllocator().getUser();
		
		partial = "@" + mentionUser1.getFirstName();
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
			
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Status Update box exists</li>
	*<li><B>Step:</B>Load Homepage</li>
	*<li><B>Verify:</B>The Updates link exists in the left nav</li>
	*<li><B>Step:</B>Click the Updates link</li>
	*<li><B>Verify:</B>The existence of Status Update box</li>
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level2", "regressioncloud", "bvt"})
	public void verifyStatusUpdateExists() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		//Load component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load Homepage and Login");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.weakStep("Validate that 'Updates' Link is displayed in the left navigation menu");
		log.info("INFO: Validate Updates link is displayed in the left nav");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the 'Updates' Link");
		ui.clickLink(HomepageUIConstants.Updates);

		//Verify status update box exists
		logger.weakStep("Verify that the 'Status Update' box displays");
		log.info("INFO: Verify status update box displays");
		Assert.assertTrue(driver.getVisibleElements(BaseUIConstants.StatusUpdate_iFrame).size() > 0,
				"Status update box is not displayed.");
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Typing a test message</li>
	*<li><B>Step:</B>Load Homepage</li>
	*<li><B>Verify:</B>The Updates link exists in the left nav</li>
	*<li><B>Step:</B>Click the Updates link</li>
	*<li><B>Step:</B>In Status Update box, enter message</li>
	*<li><B>Verify:</B>The message displays exactly as typed</li>
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"cplevel2", "level2", "regressioncloud", "bvt"})
	public void typeTestMessage() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testMsg = Data.getData().commonStatusUpdate;
		ui.startTest();
		
		//Load component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load Homepage and login");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.weakStep("Validate that the 'Update' link is displayed in the left navigation menu");
		log.info("INFO: Validate Update link is displayed in the left nav");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Click on the 'Updates' Link");
		log.info("INFO: Select Updates in the left navigation");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Test plain text message
		logger.strongStep("Enter plain text message");
		log.info("INFO: Enter plain text message");
	
		ui.enterStatus(testMsg);
		
		//Verify displayed message matches intended message
        logger.weakStep("Check that displayed message matches the intended message");
        Element statusUpdate = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_iFrame);
		driver.switchToFrame().selectFrameByElement(statusUpdate);
		Element statusUpdateBody = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_Body);
		String actual = statusUpdateBody.getText();
		String expected = testMsg;
		log.info("INFO: Verify the displayed text message matches intended text message");
		Assert.assertEquals(actual, expected);
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Typing a partial mention message</li>
	*<li><B>Step: </B>Load Homepage</li>
	*<li><B>Verify: </B>The Updates link exists in the left nav</li>
	*<li><B>Step: </B>Click the Updates link</li>
	*<li><B>Step: </B>In Status Update box, type "@amy"</li>
	*<li><B>Verify: </B>The message was displayed exactly as typed</li>
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level2", "regressioncloud", "bvt"})
	public void typePartialMention() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		//Load component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load homepage and Login");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		log.info("INFO: Validate 'Update' link is displayed in the left navigation menu");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Select 'Updates' in the left navigation menu");
		log.info("INFO: Select Updates in the left navigation");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Test partial @mention
		logger.strongStep("Enter a partial @mention");
		log.info("INFO: Enter a partial at mention");
	
		ui.enterStatus(partial);

		//Verify displayed message matches intended message
		logger.weakStep("Verify the displayed partial @mention matches intended @mention");
        Element statusUpdate = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_iFrame);
		driver.switchToFrame().selectFrameByElement(statusUpdate);
		Element statusUpdateBody = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_Body);
		String actual = statusUpdateBody.getText();
		String expected = partial;
		log.info("INFO: Verify the displayed partial mention matches intended mention");
		Assert.assertEquals(actual, expected);
		
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Typing a complete mention message</li>
	*<li><B>Step: </B>Load Homepage</li>
	*<li><B>Verify: </B>The Updates link exists in the left nav</li>
	*<li><B>Step: </B>Click the Updates link</li>
	*<li><B>Step: </B>In Status Update box, type "@" followed by the complete user name</li>
	*<li><B>Step: </B>Select name from typeahead</li>
	*<li><B>Verify: </B>The at mention matches intended user</li>
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"cplevel2", "level2", "regressioncloud", "bvt"})
	public void typeCompleteMention() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		//Load component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load Homepage and login");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(user);
		ui.waitForPageLoaded(driver);
		
		//Check that the Updates link is displayed in the left nav
		logger.weakStep("Validate that the 'Update' Link is displayed in the left navigation menu");
		log.info("INFO: Validate Update link is displayed in the left nav");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates), "ERROR: Update link not found");
		
		//Click on the Updates link
		logger.strongStep("Click on the 'Updates' Link");
		log.info("INFO: Select Updates in the left navigation");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for mentions text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Wait for Mentions text area to Load");
		ui.waitForPageLoaded(driver);
		
		//Types the user and selects from typeahead
	    logger.strongStep("Enter a complete @mention");
		log.info("INFO: Enter a complete at mention");
		ui.selectAtMention(mentionUser1.getDisplayName());
		
		//Verify displayed mention matches intended mention
		logger.strongStep("If it does not work, enter a complete @mention again");
		Element statusUpdate = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_iFrame);
		driver.switchToFrame().selectFrameByElement(statusUpdate);
		Element statusUpdateBody = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_Body);
		String actual = statusUpdateBody.getText();
		String expected = "@" + mentionUser1.getDisplayName();
		logger.weakStep("Verify the displayed mention matches intended mention");
		log.info("INFO: Verify the displayed mention match intended mention");		
		Assert.assertEquals(actual, expected);
		
		ui.endTest();		
	}
	

	
	/**
	*<ul>
	*<li><B>Info: </B>Typing Two complete mention messages</li>
	*<li><B>Step: </B>Load Homepage</li>
	*<li><B>Verify: </B>The Updates link exists in the left nav</li>
	*<li><B>Step: </B>Click the Updates link</li>
	*<li><B>Step: </B>In Status Update box, type "@" followed by the complete user name</li>
	*<li><B>Step: </B>Select name from typeahead</li>
	*<li><B>Step: </B>Hit space bar</li>
	*<li><B>Step: </B>Type another complete user name</li>
	*<li><B>Step: </B>Select name from typeahead</li>
	*<li><B>Verify: </B>The mentions match intended users</li>
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression", "regressioncloud", "bvt"})
	public void typeTwoCompleteMention() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		//Load component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Load Homepage and Login");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(user);
		
		//Check that the Updates link is displayed in the left nav
		logger.weakStep("Validate 'Update' link is displayed in the left navigation menu");
		log.info("INFO: Validate Update link is displayed in the left nav");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.Updates));
		
		//Click on the Updates link
		logger.strongStep("Select 'Updates' in the left navigation menu");
		log.info("INFO: Select Updates in the left navigation");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Wait for mentions text area to load
		//Reason: It loads later on the page and gives the typeahead time
		//to load in and be ready for @mentions being typed
		logger.strongStep("Wait for mentions text area to load");
		ui.waitForPageLoaded(driver);
		
		//Types the user and selects from typeahead
		logger.strongStep("Enter a complete @mention");
		log.info("INFO: Enter a complete at mention");
		ui.selectAtMention(mentionUser1.getDisplayName());
		
		//Type space
		ui.enterStatus(" ");

		//Types another user and selects from typeahead
		logger.strongStep("Else enter another complete @mention");
		log.info("INFO: Enter another complete at mention");
		ui.selectAtMention(mentionUser2.getDisplayName());

		//Verify displayed mention matches intended mention
        Element statusUpdate = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_iFrame);
		driver.switchToFrame().selectFrameByElement(statusUpdate);	
		Element statusUpdateBody = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_Body);
		String actual = statusUpdateBody.getText();
		String expected = "@" + mentionUser1.getDisplayName() + " " + "@" + mentionUser2.getDisplayName();
		String expectedWithOutSpace = "@" + mentionUser1.getDisplayName() + "@" + mentionUser2.getDisplayName();
		logger.weakStep("Verify the displayed mentions match intended mentions");
		log.info("INFO: Verify the displayed mentions match intended mentions");
		
		if(actual.compareTo(expected) == 0  || actual.compareTo(expectedWithOutSpace) == 0)
			Assert.assertTrue(true, "The displayed mentions match the intended mentions");
		else
			Assert.assertTrue(false, "The displayed mentions do not match the intended mentions");

		ui.endTest();		
	}
	

}
