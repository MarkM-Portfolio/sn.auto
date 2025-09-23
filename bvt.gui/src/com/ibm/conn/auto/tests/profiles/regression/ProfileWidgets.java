	package com.ibm.conn.auto.tests.profiles.regression;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Profile_View_Menu;
import com.ibm.conn.auto.util.menu.Profile_Widget_Action_Menu;
import com.ibm.conn.auto.webui.ProfilesUI;

	public class ProfileWidgets extends SetUpMethods2{
			
			private static Logger log = LoggerFactory.getLogger(ProfileWidgets.class);
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
			*<li><B>Test Scenario:</B> Verifying My Links Widget exists at the right of the page </li>
		    *<li><B>Step:</B> Click on Minimize Action Menu Item</li>
			*<li><B>Verify:</B> Verifying the widget is Minimized</li>
			*<li><B>Step:</B> Click on Maximize Action Menu Item</li>
			*<li><B>Verify:</B> Verify the Widget is Maximized </li>
			*<li><B>Step:</B> Click on Help Action Menu Item</li> 
			*<li><B>Verify:</B> Verifying the Add Links to Your Profile page is opened in new Help Window </li>
			*<li><B>Step:</B> Click on Add Links Action Button</li>
			*<li><B>Verify:</B> Verifying the Name and Link fields are displayed</li>
			*<li><B>Verify:</B> Verifying the Save and Cancel buttons are exist and are active</li>
			*<li><B>Step:</B> Enter Name and Link Click on Save Button</li>
			*<li><B>Verify:</B> Verifying new link is created</li>
			*<li><B>Step:</B> Click on the Created link</li>
			*<li><B>Verify:</B> Verifying a new window tap is opened with the IBM web page URl</li>
			*<li><B>Verify:</B> Verifying Link is Removed</li>
			<li><a HREF= "Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8F46A48B0A9BC6AB85257E13005D810D">- SC- IC Profiles Regression 15: My Profile Page - My Links Widget</a></li>
			*</ul>
			*/
			
			@Test(groups={"ptcsc", "ptc"})
			public void myProfilesLinksWidget() throws Exception {
				
				//Get User
				User testUser1 = cfg.getUserAllocator().getUser();
				
				//Start Test
				ui.startTest();

				//Load the component and login as below user
				ui.loadComponent(Data.getData().ComponentProfiles);
				ui.login(testUser1);
				
				
				//Open My profiles page
				log.info("INFO : Open My Profiles Page");
				Profile_View_Menu.MY_PROFILE.select(ui);
				
				//Verify MyLinks text presence in the page
				log.info("INFO:  My Links Widget  present");
				Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().MyLinksWidget),
						"ERROR:My Links Widget  present");
				
				//verify the text There are no links yet for this profile.
				log.info("INFO:the text There are no links yet for this profile");
				if (!ui.fluentWaitTextPresent(Data.getData().MessageUnderMyLinks)) {
					//Delete Link
					log.info("INFO:Delete Link");
					ui.profilesDeleteLink();
				}
				
				//Verify the text There are no links yet for this profile
				log.info("INFO:Verify the text There are no links yet for this profile");
				Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().MessageUnderMyLinks),
						"ERROR:There are no links yet for this profile text is present");
				
				//verify Add Link action Button is not presence or not
				log.info("Add Link action Button is  present");
				Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.ProfilesAddLink),
						 "ERROR: Add Link action Button is not present");
				
				// verify Minimize , Refresh , Help
				log.info("INFO:verify Minimize , Refresh , Help");
				driver.getSingleElement(ProfilesUIConstants.ActionsForMyLinksMenu).click();
				Assert.assertTrue(ui.fluentWaitTextPresent("Minimize"),
						"ERROR:Minimize dropdown is not present");
				Assert.assertTrue(ui.fluentWaitTextPresent("Refresh"),
						"ERROR:Refresh dropdown is not present");
				Assert.assertTrue(ui.fluentWaitTextPresent("Help"),
						"ERROR:Help dropdown is not present");
				
				//Click on Minimize item in My links Actions Menu
				log.info("INFO:Click on Minimize item in My links Actions Menu");
				Profile_Widget_Action_Menu.MINIMIZE.openMyLinksActionMenu(ui);
				Profile_Widget_Action_Menu.MINIMIZE.select(ui);
				
				//Verify when Minimize There are no links yet for this profile text is not present
				log.info("INFO:when Minimize There are no links yet for this profile text is not present");
				Assert.assertTrue(ui.fluentWaitTextNotPresent(Data.getData().MessageUnderMyLinks),
						"ERROR:There are no links yet for this profile text is present ");	
					
				//Click on Maximize item in My links Actions Menu
				log.info("INFO:Click on Maximize item in My links Actions Menu");
				Profile_Widget_Action_Menu.MAXIMIZE.openMyLinksActionMenu(ui);
				Profile_Widget_Action_Menu.MAXIMIZE.select(ui);
				
				//Verify when maximize there are no links yet for this profile text should  present
			   log.info("INFO:There are no links yet for this profile text is  present");	
			   Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().MessageUnderMyLinks),
					"ERROR:There are no links yet for this profile text is not present");
				
			   //Click on Help widget and navigate to frame
			   log.info("INFO:Click on Help widget and navigate to frame");
				String Handle = driver.getWindowHandle();
		        Profile_Widget_Action_Menu.HELP.openMyLinksActionMenu(ui);
		        Profile_Widget_Action_Menu.HELP.select(ui);
				driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMConnectionsCloud);
				driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.HelpTagFrame);
				driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.ContentFrame);
				driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.ContentViewFrame);
				
				//Add links to your profile text Should Appear
				log.info("INFO:Add links to your profile text Should Appear");
				Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().AddLinksToYourProfile),
						"ERROR: Add links to your profile not Present");
				ui.close(cfg);
				driver.switchToWindowByHandle(Handle);
			
				//Click on Add Links Action button
				log.info("INFO:Click on Add Links Action button");
				driver.getSingleElement(ProfilesUIConstants.ProfilesAddLink).click();
				
				//Name and Link fields should display
				log.info("INFO: Name and Link fields should display");
				Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.ProfilesAddLinkName),
						 "ERROR:Name is not displaying");
				Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.ProfilesAddLinkLinkname),
						"ERROR:Link is not displaying");
				
				//Save button should display
				log.info("INFO:Save button should display");
				Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.AddLinkSaveButton),
						"ERROR: Save button is not displaying");
				
				//Cancel Button Should display
				log.info("INFO:Cancel button Should display");
				Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.AddLinkCancelButton),
						"ERROR:Cancel button is not displaying");
				
				//Provide Name in Add Link name Field
				log.info("INFO:Provide Name in Add Link name Field");
				driver.getSingleElement(ProfilesUIConstants.ProfilesAddLinkName).type(Data.getData().BookmarkName);
				
				//Provide Link in Add Link Field
				log.info("INFO: Provide Link in Add Link Field");
				driver.getSingleElement(ProfilesUIConstants.ProfilesAddLinkLinkname).type(Data.getData().privacytabURL);
				
				//Click on Add Link save Button
				log.info("INFO: Click on Add Link save Button");
				driver.getSingleElement(ProfilesUIConstants.AddLinkSaveButton).click();
				
				//Created link should appear
				log.info("INFO: Created link should appear");
				Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.MyLinksIBM),
						"ERROR: Link Should not appear");
				String MyLinkswindow = driver.getWindowHandle();
				
				//Click on Created link
				log.info("Click on IBM link");
				driver.getSingleElement(ProfilesUIConstants.MyLinksIBM).click();
				driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMWindowURL);
						
				// A new browser should open with  http://www.ibm.com/in/en/ URL
				log.info("INFO: A new browser should open with  http://www.ibm.com/in/en/ URL");
				Assert.assertTrue(driver.getCurrentUrl().contains(Data.getData().privacytabURL));
				driver.switchToWindowByHandle(MyLinkswindow);
				
				//Delete created link
				log.info("INFO Delete IBM link");
				ui.profilesDeleteLink();
				
				//Link Should not appear
				log.info("INFO:link should not appear");
				Assert.assertTrue(ui.fluentWaitTextNotPresent(ProfilesUIConstants.MyLinksIBM),
						"ERROR: link is Visible");
				
				ui.endTest();
				
	      }
			
			
			/**
			*<ul>
			*<li><B>Test Scenario:</B> Verifying the Another User's Profile page's Things In Common widget </li>
			*<li><B>Step:</B> Open My Profile </li>
			*<li><B>Verify:</B> Verifying Things in Common widget is displayed on the page </li>
			*<li><B>Verify:</B> Verifying  the widget menu contains: Minimize, Refresh, Help menu items </li>
			<li><a HREF= "Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/4AF2172B2224A8D585257E1F005462D0">- SC - IC Profiles Regression 25: Another User's Profile Page - Things In Common widget</a></li>
			*</ul>
			*/
			@Test(groups={"ptcsc","ptc"})
			public void thingsInCommonWidget() throws Exception {
				
				
			//Start Test
			ui.startTest();
			
			User testUser = cfg.getUserAllocator().getUser();
			User testUser1 = cfg.getUserAllocator().getUser();

			//Load the component and login as below user
			ui.loadComponent(Data.getData().ComponentProfiles);
			ui.login(testUser1);

			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
			Profile_View_Menu.MY_PROFILE.select(ui);
			
			//provide user name in search tab
			log.info("INFO:provide user name in search tab");
			ui.searchForUser(testUser);
			
			//Click on user name
			log.info("INFO:Click on username");
			ui.clickLink("link="+testUser.getDisplayName());
			
			//Verify Things in Common widget is displayed on the page.
			log.info("INFO:Verify Things in Common widget is displayed on the page.");
			Assert.assertTrue(ui.fluentWaitTextPresent("Things in Common"),
					"ERROR:Things in Common test should not dislay");
			
			//The widget menu contains Minimize, Refresh, Help menu items
			log.info("INFO:Verify the widget menu contains Minimize, Refresh, Help menu items");
			driver.getSingleElement(ProfilesUIConstants.thingsInCommonWidgetMenu).click();
			Assert.assertTrue(ui.fluentWaitTextPresent("Minimize"),
					"ERROR:Minimize drop down is not present");
			Assert.assertTrue(ui.fluentWaitTextPresent("Refresh"),
					"ERROR:Refresh drop down is not present");
			Assert.assertTrue(ui.fluentWaitTextPresent("Help"),
					"ERROR:Help drop down is not present");
					
				ui.endTest();
			
		  }
			
			/**
			*<ul>
			*<li><B>Test Scenario:</B> Verifying  Another User's Profile Page - Who Connects Us widget </li>
			*<li><B>Step:</B> Open My Profile </li>
			*<li><B>Step:</B> open Another user's profile
			*<li><B>Verify:</B> Verifying Who Connects Us? widget is displayed </li>
			<li><a HREF= "Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/2967169DCDD3A88385257E1F005E4A09">- SC - IC Profiles Regression 26: Another User's Profile Page - Who Connects Us widget</a></li>
			*</ul>
			*/
			
			@Test(groups={"ptcsc", "ptc"})
			public void whoConnectsUswidget() throws Exception {
				
				
			//Start Test
			ui.startTest();
			
			User testUser = cfg.getUserAllocator().getUser();
			User testUser1 = cfg.getUserAllocator().getUser();


			//Load the component and login as below user
			ui.loadComponent(Data.getData().ComponentProfiles);
			ui.login(testUser1);

			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
			Profile_View_Menu.MY_PROFILE.select(ui);
			
			//provide user name in search tab
			log.info("INFO: provide user name in search tab");
			ui.searchForUser(testUser);
			
			//Click on user name
			log.info("INFO: Click on user name");
			ui.clickLink("link="+testUser.getDisplayName());
			
			//verify the test Who Connects Us? is presence
			log.info("INFO: verify the test Who Connects Us? is presence ");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().ProfileWhoConnectsUs),
					"ERROR: the text Who Connects Us widget should not present");
					
					ui.endTest();
			
			}
			
			/**
			*<ul>
			*<li><B>Test Scenario:</B> Verifying Another User's Profile Page - Network - View All link </li>
			*<li><B>Step:</B> Open My Profile Tab</li>
			*<li><B>Verify:</B> Verifying the Network widget is on the page </li>
			*<li><B>Verify:</B> Verifying View All link exists </li>
			**<li><B>Step:</B> Click on viewAll link</li>
			*<li><B>Verify:</B> Verifying the page opens with correct URL  </li>
			<li><a HREF= "Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/A410D88B9693482285257E1F005FB176 ">-SC - IC Profiles Regression 27: Another User's Profile Page - Network - View All link</a></li>
			*</ul>
			*/
			
			@Test(groups={"ptcsc","ptc"})
			public void networkViewAlllink() throws Exception {
				
				
			//Start Test
			ui.startTest();
			
			User testUser = cfg.getUserAllocator().getUser();
			User testUser1 = cfg.getUserAllocator().getUser();

			//Load the component and login as below user
			ui.loadComponent(Data.getData().ComponentProfiles);
			ui.login(testUser1);

			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			//ui.fluentWaitPresentWithRefresh(ProfilesUI.People);
			Profile_View_Menu.MY_PROFILE.select(ui);
			
			// provide user name in search tab
			log.info("INFO: provide user name in search tab");
			ui.searchForUser(testUser);
			
			//Click on user name
			log.info("INFO:Click on user name");
			ui.clickLink("link="+testUser.getDisplayName());
			
			// the Network widget is on the page.
			log.info("INFO: the Network widget is on the page.");
			Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.networkSection).getText().contains("Network"),
					"ERROR: the Network widget is not present on the page");
			
			// View All link exists
			log.info("INFO: View All link exists");
			Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.networkViewAllLink),
					"ERROR: View All link is not present");
			
			//Click on ViewAllLink link
			log.info("INFO:Click on ViewAllLink link");
			driver.getSingleElement(ProfilesUIConstants.networkViewAllLink).click();
			
			//ViewAllLink page is opened correct url
			log.info("INFO:ViewAllLink page is opened correct url");
			Assert.assertTrue(driver.getCurrentUrl().contains(ui.openViewLinkUrl()),
					"ERROR: ViewAllLink opened with incorrect url");

					
			ui.endTest();  
			
			
		  }
			
			/**
			*<ul>
			*<li><B>Test Scenario:</B> SC - IC Profiles Regression 16: My Profile Page - Network Widget</li>
			*<li><B>Step:</B> Open People -> My Profile page</li>
			*<li><B>Verify:</B>The Network widget exists at the right of the page.</li>
			*<li><B>Verify:</B> The Minimize, Refresh, Help actions are available in networkAction menu</li>
			*<li><B>Verify:</B> The user photo action button exists for each user in your network.</li>
			*<li><B>Verify:</B> The user's business card control exists</li>
			*<li><B>Verify:</B> The View All link is displayed.</li>
			*<li><B>Step:</B>  Mouse over user photo in the Network widget</li> 
			*<li><B>Verify:</B> The user's name is displayed</li>
			*<li><B>Step:</B> Click the User photo in the Network widget</li>
			*<li><B>Verify:</B> The user's profile opens with correct URL</li>
			*<li><B>Step:</B> Mouse over the Business card control in the Network widget </li>
			*<li><B>Verify:</B> The message Business card for < your test user > is displayed.</li>
			*<li><B>Step:</B> Click the User Business card control in the Network widget</li>
			*<li><B>Verify:</B> The user's Business card is opened</li>
			*<li><B>Step:</B>Click the View All link, in the Network widget </li>
			*<li><B>Verify:</B> The user's My Network view is opened with correct URL</li>
			*<li><B>Verify:</B>The user's that were displayed in the Network widget are also displayed in this view.
			*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/DD5F390ED9EA76D385257E13006719E9 ">SC - IC Profiles Regression 16: My Profile Page - Network Widget</a></li>
			*</ul>
			*/
			
			@Test(groups={"ptcsc", "ptc"})
			public void myProfilesNetworkWidget() throws Exception {
				
				
			//Start Test
			ui.startTest();
			
			User testUser1 = cfg.getUserAllocator().getUser();
			User testUser2 = cfg.getUserAllocator().getUser();

			//Load the component and login as below user
			ui.loadComponent(Data.getData().ComponentProfiles);
			ui.login(testUser1);

			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
			Profile_View_Menu.MY_PROFILE.select(ui);
			
			// the Network widget exists at the right of the page.
			log.info("INFO: the Network widget exists at the right of the page");
			Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.networkRightSide).getSingleElement(ProfilesUIConstants.networkSection).isDisplayed(),
					"ERROR:the Network widget should not present at the right of the page");
			
			// verify Minimize , Refresh , Help
			log.info("INFO:verify Minimize , Refresh , Help");
			ui.clickLink(ProfilesUIConstants.actionsForNetworkMenu);
			Assert.assertTrue(ui.fluentWaitTextPresent("Minimize"),
					"ERROR:Minimize dropdown is not present");
			Assert.assertTrue(ui.fluentWaitTextPresent("Refresh"),
					"ERROR:Refresh dropdown is not present");
			Assert.assertTrue(ui.fluentWaitTextPresent("Help"),
					"ERROR:Help dropdown is not present");
			
            //verify either the message No network contacts are associated with this profile 
			log.info("INFO:The message No network contacts are associated with this profile");
			if (!ui.fluentWaitTextPresent(Data.getData().networkUnderText)) {
				//Delete all users
				log.info("INFO:Delete all users");
			    driver.getSingleElement(ProfilesUIConstants.networkViewAllLink).click();
				List<Element> deleteImg = driver.getElements(ProfilesUIConstants.userDeleteIcon);
			    for (Element element : deleteImg)
			    {
					driver.getFirstElement(ProfilesUIConstants.userDeleteIcon).click();
					ui.clickLinkWait(ProfilesUIConstants.networkDeleteOkButton);
					ui.fluentWaitTextPresent("Successfully removed the following contact:");
					}
			}
			
			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
			Profile_View_Menu.MY_PROFILE.select(ui);
			
			//The message No network contacts are associated with this profile should present
			log.info("INFO:The message No network contacts are associated with this profile should present");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().networkUnderText),
						"ERROR:The message No network contacts are associated with this profile is not present");
				
			
			//ViewAll link should  present
			log.info("INFO: View All link is displayed");
			Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.networkViewAllLink),
					"ERROR: ViewAll link is not displayed");
			
			//Search for the testUser2
			log.info("INFO: Search for the " + testUser2.getDisplayName());
			ui.openAnotherUserProfile(testUser2);
					
			//Remove an existing network relationship if already exists
			log.info("INFO: Remove an existing network relationship if already exists");
			ui.removeExistingNetworkRelation(testUser1, testUser2);
					
			//Invite User to My Network
			ui.inviteUserToMyNetwork(testUser2);
			
			//logout User
			ui.logout();
			
			//Load the component and login as below user
			ui.loadComponent(Data.getData().ComponentProfiles, true);
			ui.login(testUser2);
			
			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
			Profile_View_Menu.MY_PROFILE.select(ui);
			
			//Accept Network invite of testUser1
			log.info("INFO: Accept Network invite of " + testUser1.getDisplayName());
			ui.acceptUserInvite(testUser1);
			
			//logout User
			ui.logout();
			
			//Load the component and login as below user
			ui.loadComponent(Data.getData().ComponentProfiles, true);
			ui.login(testUser1);
					
			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			Profile_View_Menu.MY_PROFILE.select(ui);
			
			//User photo action button should exist
			log.info("INFO:User photo action button should exist");
			driver.getSingleElement(ui.getContactImage(testUser2)).hover();
			Assert.assertTrue(driver.isElementPresent(ui.getContactImage(testUser2)),
					"ERROR:User photo action button should not present");
			
			//User name should present
			log.info("INFO: user's name is present");
			Assert.assertTrue(driver.isElementPresent(ui.getContactImage(testUser2)),
					"ERROR:user name is not present");

			
			//Click on testUser2 image in Network widget
			log.info("INFO: Click on image " + testUser2.getDisplayName());
			ui.clickLinkWait(ui.getContactImage(testUser2));
				
			//user's Profile page is opened with correct URL
			log.info("INFO:user's Profile page is opened with correct URL");
			Assert.assertTrue(driver.getCurrentUrl().contains(ui.getNetworkUserUrl()),
					"ERROR:user's Profile page is opened with  incorrect URL");
			
			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			Profile_View_Menu.MY_PROFILE.select(ui);

			//Click on Business card
			log.info("INFO:Click on Business card");
			ui.openBusinessCardOfUser(testUser2);
			
			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			Profile_View_Menu.MY_PROFILE.select(ui);
			
			//Click on view all link
			log.info("INFO:Click on View All link");
			ui.clickLinkWait(ProfilesUIConstants.networkViewAllLink);
			System.out.println(driver.getTitle());
			System.out.println(driver.getCurrentUrl());
			
			//View All link page is opened with correct URL
			log.info("INFO:ViewAllLink page is opened correct url");
			Assert.assertTrue(driver.getCurrentUrl().contains(ui.getMyNetworkUrl()),
					"ERROR: ViewAllLink opened with incorrect url");
			
			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			Profile_View_Menu.MY_PROFILE.select(ui);
			
			//get all images present in the network container
			log.info("INFO:get all images present in the network container");
			List<Element> images = driver.getElements(ProfilesUIConstants.networkfriendscontainer);
			List<String> users = new ArrayList<String>();
			for (Element element : images) {
				users.add(element.getAttribute("title"));
			}
			
			//verify  the user's that were displayed in the Network widget are also displayed in this view.
			log.info("INFO: the user's that were displayed in the Network widget are also displayed in this view");
			driver.getSingleElement(ProfilesUIConstants.networkViewAllLink).click();
			for(String user : users) {
			Assert.assertTrue(ui.getFirstVisibleElement("link="+ user).isDisplayed(),
						"ERROR: the user's that were displayed in the Network widget are not displayed in this view");
			}
		}
	}			
