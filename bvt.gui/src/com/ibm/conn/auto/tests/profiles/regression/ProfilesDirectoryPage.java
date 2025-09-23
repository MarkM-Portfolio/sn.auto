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

package com.ibm.conn.auto.tests.profiles.regression;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Profile_View_Menu;
import com.ibm.conn.auto.util.menu.Profile_Widget_Action_Menu;
import com.ibm.conn.auto.webui.ProfilesUI;
public class ProfilesDirectoryPage extends SetUpMethods2 {
	

	private static Logger log = LoggerFactory.getLogger(ProfilesDirectoryPage.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		           
	}
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> Verifying Directory Page's Default Layout</li>
	*<li><B>Step:</B>Open the user's My Profile page</li>
	*<li><B>Step:</B>Click on My organization directory link people tab</li>
	*<li><B>Verify:</B> My organization directory page is opened with correct URL
	*<li><B>Step:</B>In directory search option search for a user by typing two letters of the users</li>
	*<li><B>Verify:</B>As we type in the search control, matching names are displayed.</li>
	*<li><B>Verify:</B>Footer displays the number of results returned</li>
	*<li><B>Verify:</B>Help banner text is present</li>
	*<li><B>Step:</B>Clicking on use tags link</li>
	*<li><B>Verify:</B>The Understanding Profile Tags help topic is opened in a new browser window</li>
	*<li><B>Step:</B>Click x button to close help banner</li>
	*<li><B>Verify:</B>the help banner is no longer displayed</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/81AF2E46D40CF29585257BAB006E5F73/C0AA3CCBAC8660DE85257E1F006B86A2">SC - IC Profiles Regression 28: Directory Page - Default Layout
	*</ul>
	*/
	@Test(groups = {"ptcsc", "ptc"})
	public void directoryPageDefaultLayout() throws Exception {
		
		//Get User
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start test
		ui.startTest();

		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//Click on organization directory menu item on people tab
	    log.info("INFO:Clicking on My Org Directory menu item");
	    ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.ORG_Directory.select(ui);
		
		//Verify My organization directory page is opened with correct URL:
		log.info("INFO:My organization directory page is opened with correct URL");
		Assert.assertTrue(driver.getCurrentUrl().contains(Data.getData().OrgDirectoryUrl.replaceAll("SERVER", cfg.getServerURL())),
		    	"ERROR:My organization directory is opened with incorrect URL");
		
		//Click on Directory search field and type first few letters of the user and validate correct users are dispalyed
		log.info("INFO:Clicking and typing first few letters of the user in Search directory and validate correct users are dispalyed");
		Assert.assertTrue(ui.isDirectorySearchResultsMatching(testUser), 
				"ERROR:Searched user with first few letters not displayed");
		
		//Verify footer message box shows correct number of users listed
		log.info("INFO:verifying footer message box shows correct number of users listed");
		Assert.assertEquals(ui.getFooterMessage().replaceAll("[^0-9]", "").trim(),Integer.toString(driver.getVisibleElements(ProfilesUIConstants.DirectorySearchResultWidgets).size()),
				"ERROR:footer message box shows incorrect number of users");

		//Validate the help banner text
		log.info("INFO:Validate the help banner text");
		boolean banner;
		banner = ui.fluentWaitTextPresent(Data.getData().OrgBannerHelpMsg) && ui.fluentWaitTextPresent(Data.getData().OrgBannerHelpMsg1)
		&& ui.fluentWaitTextPresent(Data.getData().OrgBannerHelpMsg2);
		Assert.assertTrue(banner,		
			     "ERROR:Help banner text is not proper");
		
		//Click on use tag link in help banner
		log.info("INFO:Clicking on use tag link in help banner");
		String Handle = driver.getWindowHandle();
		driver.getSingleElement(ProfilesUIConstants.Usetagslink).click();
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMConnectionsCloud);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.HelpTagFrame);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.ContentFrame);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.ContentViewFrame);

		//Understanding profile tags page should appear
		log.info("INFO:Add links to your profile text should appear");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().Usetagslinkpage),
				"ERROR: Understanding profile tags  page did not open");
		
		//Close usertag browser window
		log.info("INFO:Close usertag browser window");
		ui.close(cfg);
		driver.switchToWindowByHandle(Handle);
		
		//Close Helpbanner from UI
		log.info ("closing help banner from UI");
		driver.getSingleElement(ProfilesUIConstants.HelpbannerClose).click();
		
		// Verify help banner is no longer displayed.
		log.info("INFO:Validate help banner is no longer displayed");		
		Assert.assertTrue(ui.fluentWaitTextNotPresent(Data.getData().OrgBannerHelpMsg),		
				 "ERROR:Help banner text is still appearing");
		
		ui.endTest();	
				 
	}	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 30: Directory Page - Do You Know(1 of 2)</li>
	*<li><B>Step:</B>Open the Orgs directory page - People - <current org> Directory</li>
	*<li><B>Verify:</B>the Do You Know widget is displayed</li>
	*<li><B>Step:</B>Open the Do You Know menu</li>
	*<li><B>Verify:</B>menu items Minimize Refresh and Help exist.</li>
	*<li><B>Step:</B>the tags are hidden</li>
	*<li><B>Step:</B>Select Maximize from the menu.</li>
	*<li><B>Verify:</B>the tags are displayed</li>
	*<li><B>Step:</B>Select Help from the menu</li>
	*<li><B>Verify:</B>the "See who IBM Connections Cloud recommends to you" page of the help is opened in a new window</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/D7871900C8CFF9C585257E200054032C">SC - IC Profiles Regression 30: Directory Page - Do You Know
	*</ul>
	*/
	@Test(groups = {"ptcsc", "ptc"})
	public void directoryPageDoyouknowWidget() throws Exception {
		
		//Get User
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start test
		ui.startTest();

		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//Click on organization directory menu item on people tab
	    log.info("INFO:Clicking on My Org Directory menu item");
	    ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.ORG_Directory.select(ui);
		
		//Verify My organization directory page is opened with correct URL:
		log.info("INFO:My organization directory page is opened with correct URL");
		Assert.assertTrue(driver.getCurrentUrl().contains(Data.getData().OrgDirectoryUrl.replaceAll("SERVER", cfg.getServerURL())),
		    	"ERROR:My organization directory is opened with incorrect URL");
		
		//Verify Do you know widget is displayed
		log.info("Verifying Do you know widget is displayed");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.DYKwidget),
				"ERROR:Do you know widget is not displayed");
		 
		// verify Minimize , Refresh , Help
		log.info("INFO:verify Minimize , Refresh , Help");
		driver.getSingleElement(ProfilesUIConstants.ActionsforDoyouknow).click();
		Assert.assertTrue(ui.fluentWaitTextPresent("Minimize"),
				"ERROR:Minimize dropdown is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("Refresh"),
				"ERROR:Refresh dropdown is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("Help"),
				"ERROR:Help dropdown is not present");
		
		//Click on Minimize item in Do you know Actions Menu
		log.info("INFO:Click on Minimize item in Do you know Actions Menu");
		Profile_Widget_Action_Menu.MINIMIZE.actionsforDoyouknow(ui);
		Profile_Widget_Action_Menu.MINIMIZE.select(ui);
		
		//Verify when Minimize Invite to connect button is hidden
		log.info("INFO:Verifying when Minimize Invite to connect button is hidden");
		Assert.assertFalse(driver.getSingleElement(ProfilesUIConstants.Invitetoconnect).isVisible(),
				 "ERROR:Invite to connect button is not hidden ");	
			
		//Click on Maximize item in My links Actions Menu
		log.info("INFO:Click on Maximize item in Do you know Actions Menu");
		Profile_Widget_Action_Menu.MAXIMIZE.actionsforDoyouknow(ui);
		Profile_Widget_Action_Menu.MAXIMIZE.select(ui);
		
		//Verify when Maximize Invite to connect button is displayed
		log.info("INFO:Verify when Maximize Invite to connect button is displayed");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.Invitetoconnect),
				 "ERROR:Invite to connect button is hidden ");
		
	    //Click on Help widget and navigate to frame
	    log.info("INFO:Click on Help widget and navigate to frame");
		String Handle = driver.getWindowHandle();
        Profile_Widget_Action_Menu.HELP.actionsforDoyouknow(ui);
        Profile_Widget_Action_Menu.HELP.select(ui);
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMConnectionsCloud);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.HelpTagFrame);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.ContentFrame);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.ContentViewFrame);
		
		//Verify the help topic
		log.info("INFO:Verifying the help topic");
		ui.helptopicDYKwidget();
		ui.close(cfg);
		driver.switchToWindowByHandle(Handle);
		
		ui.endTest();
			
	}
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 30: Directory Page - Do You Know(2 of 2)</li>
	*<li><B>Step:</B>Open the Orgs directory page - People - <current org> Directory</li>
	*<li><B>Step:</B>Click the Invite to Connect button</li>
	*<li><B>Verify:</B> the "Invite to My Network" dialog is presented</li>
	*<li><B>Step:</B>Click Send Invitation button.</li>
	*<li><B>Verify:</B>Send Invitation dialog is closed.</li>
	*<li><B>Verify:</B>the user who the invite was sent to is removed from the "Do You Know" widget</li>
	*<li><B>Step:</B> Click on the Show Previous & Show Next buttons in the widget.</li>
	*<li><B>Verify:</B>the previous or next user in the list is moved into the center focus in the widget.</li>
	*<li><B>Step:</B>Click on a user's name link in the widget</li>
	*<li><B>Verify:</B>the user's profile page is opened.</li>
	*<li><B>Step:</B>Click on a user's business card link in the widget</li>
	*<li><B>Verify:</B>the user's business card is opened</li>
	*<li><B>Step:</B>Click the Remove action</li>
	*<li><B>Verify:</B> the selected user is removed from the "Do You Know" widget</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/D7871900C8CFF9C585257E200054032C">SC - IC Profiles Regression 30: Directory Page - Do You Know
	*</ul>
	*/
	@Test(groups = {"ptcsc", "ptc"})
	public void directoryPageInviteToConnect() throws Exception {
		
		//Get User
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start test
		ui.startTest();

		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//Click on organization directory menu item on people tab
	    log.info("INFO:Clicking on My Org Directory menu item");
		Profile_View_Menu.ORG_Directory.select(ui);
		
		//Identify the current user to whom invitation to be sent
		log.info("INFO: Identifying the current user to whom invitation to be sent");
		String doYouknowuser = driver.getFirstElement(ProfilesUIConstants.DYKuser).getText();
		
		//click on invite to connect button
		log.info("INFO:clicking on invite to connect button");
		ui.clickLinkWait(ProfilesUIConstants.Invitetoconnect);
		
		//Verify Invite to My Network dialog is presented.
		log.info("INFO:Verifying Invite to My Network dialog is presented");
		if(driver.isElementPresent(ProfilesUIConstants.InviteToMyNetworkDailog)){
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.InviteToMyNetworkDailog),
				  "ERROR:Invite to My Network dialog did not appear");
		
		//Click on send invitation button
		log.info("INFO:clicking on send invitation button");
		ui.clickLinkWait(ProfilesUIConstants.SendInvite);
		
		//Verify Invite to My Network dialog is closed
		log.info("INFO:Verifying Invite to My Network dialog is closed");
		ui.fluentWaitTextNotPresent(Data.getData().inviteDialogMsg);
		Assert.assertFalse(driver.isElementPresent(ProfilesUIConstants.InviteToMyNetworkDailog),
			       "ERROR:Invite to My Network dialog did not close");
				
		//Validate that once invitation is sent the user is not visible in center focus in the widget
		log.info("INFO: Validating once invitation is sent the user is not visible in center focus in the widget");
		String doYouknowuser2 = driver.getFirstElement(ProfilesUIConstants.DYKuser).getText();
		Assert.assertNotEquals(doYouknowuser, doYouknowuser2, 
				"ERROR:invitation sent user is still visible in the view");
		}
		
		//Click on show previous person image
		String doYouknowuser2 = driver.getFirstElement(ProfilesUIConstants.DYKuser).getText();
		log.info("INFO:clicking on show previous person image");
		driver.getSingleElement(ProfilesUIConstants.StreamButtonPrev).click();
		
		//Verify previous user in the list is moved into the center focus in the widget
		log.info("INFO:Verifying previous user in the list is moved into the center focus in the widget");
		String doYouknowuser3 = driver.getFirstElement(ProfilesUIConstants.DYKuser).getText();
		Assert.assertNotEquals(doYouknowuser2, doYouknowuser3, 
				"ERROR:previous user in the list is not moved into the center focus in the widget");
		
		//Click on show next person image
		log.info("INFO:clicking on show next person image");
		driver.getSingleElement(ProfilesUIConstants.StreamButtonNext).click();
				
		//Verify next user in the list is moved into the center focus in the widget
		log.info("INFO:Verifying previous user in the list is moved into the center focus in the widget");
		String doYouknowuser4 = driver.getFirstElement(ProfilesUIConstants.DYKuser).getText();
		Assert.assertNotEquals(doYouknowuser3, doYouknowuser4, 
				 "ERROR:Next user in the list is not moved into the center focus in the widget");
		
		//Click on a user's name link in the widget
		log.info("INFO:Click on a user's name link");
		ui.clickLinkWait(ProfilesUIConstants.DYKuser);
		
		//Verify the user's profile page is opened
		log.info("INFO:verify the user's profile page is opened");
		Assert.assertTrue(driver.getCurrentUrl().contains(ui.userProfilePageUrl()),
				 "ERROR:User profile page is not opened");
		
		//Click on organization directory menu item on people tab
	    log.info("INFO:Clicking on My Org Directory menu item");
		Profile_View_Menu.ORG_Directory.select(ui);
		
		//Click on DYK Business card and verify DYK business card is opened
		log.info("INFO: Click on DYK Business card and verify DYK business card is opened");
		ui.dykPersoncardpopup();
		
		//Hover on ActionsforDoyouknow menu
		log.info("Hover on ActionsforDoyouknow menu");
		driver.getSingleElement(ProfilesUIConstants.ActionsforDoyouknow).hover();
				
		//Click on remove user link in the widget
		log.info("INFO: Click on remove user link in the widget");
		String doYouknowuser5 = driver.getFirstElement(ProfilesUIConstants.DYKuser).getText();
		ui.clickLinkWait(ProfilesUIConstants.DoyouknowUserremove);

		//Verify user is removed from the widget
		log.info("INFO: Verify user is removed from the widget");
		String doYouknowuser6 = driver.getFirstElement(ProfilesUIConstants.DYKuser).getText();
		Assert.assertNotEquals(doYouknowuser5, doYouknowuser6, 
				 "ERROR:User is not removed from the widget");
		
		ui.endTest();
  }
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 29: Directory Page - Org Tags</li>
	*<li><B>Step:</B>Open the Orgs directory page - "People - <current org> Directory"</li>
	*<li><B>Verify:</B> the Organization Tags widget is displayed</li>
	*<li><B>Step:</B>Open the Org Tags menu</li>
	*<li><B>Verify:</B>menu items Minimize Refresh and Help exist</li>
	*<li><B>Step:</B>Select Minimize from the menu.</li>
	*<li><B>Verify:</B>the tags are hidden or no tags yet info hidden</li>
	*<li><B>Step:</B>Select Maximize from the menu</li>
	*<li><B>Verify:</B>the tags are displayed</li>
	*<li><B>Step:</B>Select Help from the menu</li>
	*<li><B>Verify:</B>the "Using the Organization Tags collection" page of the help is opened in a new window</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/F86AC542C9D1378B85257E200048BCA2">SC - IC Profiles Regression 29: Directory Page - Org Tags
	*</ul>
	*/
	@Test(groups = {"ptcsc", "ptc"})
	public void directoryPageOrgTags() throws Exception {
		
		//Get User
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start test
		ui.startTest();

		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);

		//Click on organization directory menu item on people tab
	    log.info("INFO:Clicking on My Org Directory menu item");
	    ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.ORG_Directory.select(ui);
		
		//Verify organizational tags widget is displayed
		log.info("INFO:Clicking on organizational tag");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.Orgtags),
				"ERROR:organizational tags widget is not displayed");
			
		// verify Minimize , Refresh , Help organizational tags widget
		log.info("INFO:verify Minimize , Refresh , Help");
		driver.getSingleElement(ProfilesUIConstants.ActionsforOrgTags).click();
		Assert.assertTrue(ui.fluentWaitTextPresent("Minimize"),
				"ERROR:Minimize dropdown is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("Refresh"),
				"ERROR:Refresh dropdown is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent("Help"),
				"ERROR:Help dropdown is not present");
		
		//Click on Minimize item in organizational tags widget
		log.info("INFO:Click on Minimize item in organizational tags widget");
		Profile_Widget_Action_Menu.MINIMIZE.actionsforOrgTags(ui);
		Profile_Widget_Action_Menu.MINIMIZE.select(ui);
		
		//Verify when Minimize, Find a tag link is hidden
		log.info("Verifying when Minimize Find a tag link is hidden.");
		Assert.assertFalse(driver.isElementPresent(ProfilesUIConstants.Findtaglink) && ui.isTextPresent("No tags yet"),
		                  "ERROR:org content is dispalyed");
		
		//Click on Maximize item in organizational tags widget
		log.info("INFO:Click on Maximize item in organizational tags widget");
		Profile_Widget_Action_Menu.MAXIMIZE.actionsforOrgTags(ui);
		Profile_Widget_Action_Menu.MAXIMIZE.select(ui);
		
		//Verify when Maximize Find a tag link is displayed
		log.info("INFO:Verify when Maximize Find a tag link is displayed");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.Findtaglink) || ui.isTextPresent("No tags yet"),
                  "ERROR:org content is not dispalyed");
		
	    //Click on Help widget and navigate to frame
	    log.info("INFO:Click on Help widget and navigate to frame");
		String Handle = driver.getWindowHandle();
        Profile_Widget_Action_Menu.HELP.actionsforOrgTags(ui);
        Profile_Widget_Action_Menu.HELP.select(ui);
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMConnectionsCloud);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.HelpTagFrame);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.ContentFrame);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.ContentViewFrame);
		
		//Verify the help topic
		log.info("INFO:Verifying the help topic");
		Assert.assertTrue(ui.fluentWaitTextPresent("Using the Organization Tags collection"),
				  "ERROR: help topic is not present");
		ui.close(cfg);
		driver.switchToWindowByHandle(Handle);
		
		ui.endTest();
			
  }	
			 		
}