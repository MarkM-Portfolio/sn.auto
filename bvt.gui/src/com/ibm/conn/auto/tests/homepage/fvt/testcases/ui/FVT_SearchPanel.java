package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.webui.HomepageUI;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016				                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */


/**
 * Automation Backlog - New Search on Homepage
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/171584
 * @author Patrick Doherty
 */
public class FVT_SearchPanel extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FVT_SearchPanel.class);
	
	private HomepageUI ui;
	private TestConfigCustom cfg;
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		//initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}

	/**
	* searchIcon_Present() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Homepage</B></li>
	*<li><B>Verify: Check for Search Icon at the top of the right hand column</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/8E2A76E4396D3B9D85257F6900456514">TTT - Search Panel in Homepage - 00010 - Search Text box replaced by Icon</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void searchIcon_Present() {
		
		ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to Homepage / Updates / I'm Following / All");
		ui.gotoImFollowing();
		
		log.info("INFO: Verify that the Search Icon is visible in the I'm Following view");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelOpenIcon),
				"ERROR: Search icon is NOT visible in I'm Following view");
		
		log.info("INFO: Navigate to Homepage / Updates / Status Updates / All");
		ui.gotoStatusUpdates();
		
		log.info("INFO: Verify that the Search Icon is visible in the Status Updates view");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelOpenIcon),
				"ERROR: Search icon is NOT visible in Status Updates view");
		
		log.info("INFO: Navigate to Homepage / Updates / Discover / All");
		ui.gotoDiscover();
		
		log.info("INFO: Verify that the Search Icon is visible in the Discover view");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelOpenIcon),
				"ERROR: Search icon is NOT visible in Discover view");
		
		log.info("INFO: Navigate to Homepage / Mentions");
		ui.gotoMentions();
		
		log.info("INFO: Verify that the Search Icon is visible in the Mentions view");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelOpenIcon),
				"ERROR: Search icon is NOT visible in Mentions view");
		
		log.info("INFO: Navigate to Homepage / My Notifications / All");
		ui.gotoMyNotifications();
		
		log.info("INFO: Verify that the Search Icon is visible in the My Notifications view");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelOpenIcon),
				"ERROR: Search icon is NOT visible in My Notifications view");
		
		log.info("INFO: Navigate to Homepage / Action Required / All");
		ui.gotoActionRequired();
		
		log.info("INFO: Verify that the Search Icon is visible in the Action Required view");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelOpenIcon),
				"ERROR: Search icon is NOT visible in Action Required view");
		
		log.info("INFO: Navigate to Homepage / Saved / All");
		ui.gotoSaved();
		
		log.info("INFO: Verify that the Search Icon is visible in the Saved view");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelOpenIcon),
				"ERROR: Search icon is NOT visible in Saved view");
		
		//Getting Started should only be tested On Premise (CustomParameterNames.PRODUCT_NAME.getDefaultValue()) = "onprem"*/
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())){

			log.info("INFO: Navigate to Homepage / Getting Started / All");
			ui.gotoGettingStarted();
			
			log.info("INFO: Verify that the Search Icon is visible in the Getting Started view");
			Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelOpenIcon),
					"ERROR: Search icon is NOT visible in Getting Started view");
			
		}
				
		ui.endTest();
	}

	/**
	* searchIcon_HoverText() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Homepage</B></li>
	*<li><B>Step: Check for Search Icon at the top of the right hand column</B></li>
	*<li><B>Step: Mouse over the magnifying glass Search icon</B></li>
	*<li><B>Verify: The Hover text "Search" appears and pointer changes to hand icon</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/A56847F7A31BB74885257F69004C872E">TTT - SEARCH PANEL IN HOMEPAGE - 00011 - HOVER TEXT APPEARS WHEN MOUSING OVER ICON</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void searchIcon_HoverText() {
		
		ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to Homepage / Updates / I'm Following / All");
		ui.gotoImFollowing();
		
		log.info("INFO: Wait for the Search Icon to appear");
		ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelOpenIcon);

		//Tool tip text for the Search Panel icon
		String toolTipText = driver.getFirstElement(HomepageUIConstants.SearchPanelSearchIcon_2).getAttribute("title");
		log.info("INFO: toolTipText = " + toolTipText);
		Assert.assertTrue(toolTipText.equals("Search"), "ERROR: Tool tip text does not match expected value of 'Search'");
			
		ui.endTest();
	}

	/**
	* searchPanelFlyout_Opens() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Homepage</B></li>
	*<li><B>Step: Check for Search Icon at the top of the right hand column</B></li>
	*<li><B>Step: Click on the magnifying glass Search icon</B></li>
	*<li><B>Verify: The Search Panel Flyout opens over the right hand column</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/47942676DAACD19C85257F69004EBB15">TTT - SEARCH PANEL IN HOMEPAGE - 00012 - CLICKING ICON OPENS SEARCH PANEL FLYOUT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void searchPanelFlyout_Opens() {
		
		ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to Homepage / Updates / I'm Following / All");
		ui.gotoImFollowing();
		
		log.info("INFO: Wait for the Search Icon to appear and then open it");
		ui.clickLinkWait(HomepageUIConstants.SearchPanelOpenIcon);

		log.info("INFO: Verify that the text box is visible when the Search Panel flyout opens");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelTextBox),
				"ERROR: Text box is NOT visible");

		log.info("INFO: Verify that the search icon is visible when the Search Panel flyout opens");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelSearchIcon),
				"ERROR: Search icon is NOT visible");
		
		//Default text in Search panel when opened
		ui.fluentWaitTextPresent(Data.getData().SearchPanelFlyoutDefaultText_1);
		ui.fluentWaitTextPresent(Data.getData().SearchPanelFlyoutDefaultText_2);
			
		ui.endTest();
	}

	/**
	* searchPanelFlyout_PerformSearch() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Homepage</B></li>
	*<li><B>Step: Check for Search Icon at the top of the right hand column</B></li>
	*<li><B>Step: Click on the magnifying glass Search icon</B></li>
	*<li><B>Step: Type some text in the text box</B></li>
	*<li><B>Step: Click the Search Panel's search icon</B></li>
	*<li><B>Verify: The Search Panel Flyout opens over the right hand column</B></li>
	*<li><B>No TTT Scenario - Fulfilling the expectation that a search can be performed (see https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/171584)</B></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void searchPanelFlyout_PerformSearch() {
		
		ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to Homepage / Updates / I'm Following / All");
		ui.gotoImFollowing();
		
		log.info("INFO: Wait for the Search Icon to appear and then open it");
		ui.clickLinkWait(HomepageUIConstants.SearchPanelOpenIcon);

		log.info("INFO: Wait for the text box to appear in the Search Panel flyout");
		ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelTextBox);
		
		String searchText = Helper.genStrongRand();
		
		log.info("INFO: Type some text in the text box in the Search Panel flyout");
		driver.getSingleElement(HomepageUIConstants.SearchPanelTextBox).typeWithDelay(searchText);
		
		log.info("INFO: Verify that the Search Panel's no results text is visible in the Search Panel flyout");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().SearchPanelFlyout_NoResultsText),
				"ERROR: Search Panel's no results text is NOT visible in the Search Panel flyout");
		
		log.info("INFO: Verify that the 'All Content' link is visible in the Search Panel flyout");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.SearchPanelAllContentLink),
				"ERROR: 'All Content' link is NOT visible in the Search Panel flyout");

		String allContentText = driver.getSingleElement(HomepageUIConstants.SearchPanelAllContentLink).getText();
		log.info("INFO: All Content text = " + allContentText);
		log.info("INFO: Verify that the 'All Content' link hover text reads 'All Content'");
		Assert.assertTrue(allContentText.equals("All Content"),
				"ERROR: All Content hover text does not match expected value");
		
		log.info("INFO: Click on the Search Panel's search icon");
		ui.clickLinkWait(HomepageUIConstants.SearchPanelSearchIcon);
		
		log.info("INFO: Wait for the Search Results page to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseSearchResults);
		
		log.info("INFO: Get the current page title");
		String pageTitle = driver.getTitle();
		log.info("INFO: Current page title = " + pageTitle);

		log.info("INFO: Verify that the browser has navigated to the Search Results page");
		Assert.assertTrue(pageTitle.equals(Data.getData().SearchResultsPageTitle),
				"ERROR: Browser has NOT navigated to the Search Results page");
		
		ui.endTest();
		
	}
	
}
