package com.ibm.conn.auto.tests.profiles.regression;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.MyContacts_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Profile_View_Menu;
import com.ibm.conn.auto.webui.ProfilesUI;

public class ProfileNetwork extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(ProfileNetwork.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
	}
	
	@BeforeMethod(alwaysRun=true )
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	/**
	*<ul>
	*<li><B>Profile_Invite_To_My_Network</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 20: Another User's Profile Page - Invite - Follow - Accept (1 of 2)</li>
	*<li><B>Test Scenario:</B> OP - IC Profiles Regression 20: Another User's Profile Page - Invite - Follow - Accept (1 of 2)</li>
	*<li><B>Info:</B> Invite user to my network by sending invitation and follow him</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Search User and click on User name in the popup</li>
	*<li><B>Step:</B> Click on Invite to my Network</li>
	*<li><B>Verify:</B> The "Invite to My Network" dialog is displayed</li>
	*<li><B>Verify:</B> The static text in the dialog and "Also Follow" check box is checked.</li>
	*<li><B>Step:</B> Add a message to the message field</li> 
	*<li><B>Step:</B> Add a Tag to the tag field and Click Send Invitation button</li>
	*<li><B>Verify:</B> The message " <user's name> has been invited to your network is displayed</li>
	*<li><B>Verify:</B> The static text message "Pending Invitation ..." is displayed next to the user's name.</li>
	*<li><B>Verify:</B> The "Stop following" button is now displayed, the "Invite to My Network"  & "Follow" buttons are now hidden</li>
	*<li><B>Step:</B> Click People - My Network - Following</li> 
	*<li><B>Verify:</B> The user who was just invited with Following checked appears in the view</li>
	*<li><B>Verify:</B> The message text for that users, shows "Network Invitation Sent"</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/E019D86204CED1D485257E18006C2F17">SC - IC Profiles Regression 20: Another User's Profile Page - Invite - Follow - Accept</a></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/C622AB62F30FB90F85257EE40044C0AB">OP - IC Profiles Regression 20: Another User's Profile Page - Invite - Follow - Accept</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc", "ptc"})
	public void inviteToMyNetwork() throws Exception {
		
		//Start Test
		ui.startTest();
		User testUser1, testUser2; //Declare test users
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Search for the testUser2
		log.info("INFO: Search for the " + testUser2.getDisplayName());
		ui.openAnotherUserProfile(testUser2);
		
		//Remove an existing network relationship if already exists
		log.info("INFO: Remove an existing network relationship if already exists");
		ui.removeExistingNetworkRelation(testUser1, testUser2);
		
		//Click the Invite to My Network button
		log.info("INFO: Click on Invite to My Network button");
		ui.clickLinkWithJavascript(ProfilesUIConstants.Invite_OnPrem);
		
		//The "Invite to My Network" dialog is displayed
		log.info("INFO: Verify Invite to My Network dailog is displayed");
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.InviteToMyNetworkDailog).isVisible(),
				"ERROR: Invite to My Network dailog is not displayed");
				
		//The static text in the dialog
		log.info("INFO: Verify the static text in the dialog");
		Assert.assertTrue(ui.fluentWaitTextPresent("Invite " + testUser2.getDisplayName() +
				" to be your network contact"), "ERROR:- Static text in the dialog does not match");
		
		//Also Follow checkbox is checked
		log.info("INFO: Verify Also Follow checkbox is checked");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.AlsoFollowCheckBox).isSelected(),
				"ERROR:- Also Follow checkbox is not checked ");
		
		//Add message in text field
		log.info("INFO: Add message in the text field");
		ui.getFirstVisibleElement(ProfilesUIConstants.InvitationMsgField).clear();
		ui.typeText(ProfilesUIConstants.InvitationMsgField,
				Data.MY_NOTIFICATIONS_NETWORK_INVITE_FOR_ME.replaceAll("USER", testUser1.getDisplayName()));
		
		//Add a tag
		log.info("INFO: Add a tag to the tag field");
		ui.typeTextWithDelay(ProfilesUIConstants.InvitationTagsField, Data.TAG_NAME);
		
		//click "send invitation"
		log.info("INFO: Click on Send invitation");
		ui.clickLinkWithJavascript(ProfilesUIConstants.SendInvite);
		
		//The message " <user's name> has been invited to your network
        log.info("INFO: Verify the message " + testUser2.getDisplayName() + ProfilesUIConstants.InvitedMessage);
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser2.getDisplayName() + ProfilesUIConstants.InvitedMessage),
				"ERROR:- " + testUser2.getDisplayName() + ProfilesUIConstants.InvitedMessage + " is not displayed");
		
		//The text message "Pending Invitation ..." is displayed
		log.info("INFO: Verify static text message " + Data.getData().networkInvitationStatus + " is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().networkInvitationStatus),
				"ERROR:- Text message " + Data.getData().networkInvitationStatus + " is not displayed");
		
		//The "Stop following" button is now displayed.
		log.info("INFO: Verify Stop following button is now displayed");
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.Unfollow).isDisplayed(),
				"ERROR: Stop following button is not displayed");
		
		//The "Invite to My Network" button is now hidden
        log.info("INFO: Verify 'Invite to My Network' button is hidden");
        driver.turnOffImplicitWaits(); //As below elements are not present, Increase test execution speed.
        Assert.assertFalse(ui.isElementPresent(ProfilesUIConstants.Invite_OnPrem),
        		"ERROR: 'Invite to My Network' button is not hidden");

        //The "Follow" button is now hidden
        log.info("INFO: Verify 'Follow' button is hidden");
        Assert.assertFalse(ui.isElementPresent(ProfilesUIConstants.FollowPerson),
        		"ERROR: 'Follow' button is not hidden");
        driver.turnOnImplicitWaits();
        
        //Click People -> My Network -> Following
        log.info("INFO: Click on People -> My Network -> Following");
        ui.gotoMyNetwork();
        MyContacts_LeftNav_Menu.FOLLOWING.open(ui);
        
        //The testUser2 appears in the view.
        log.info("INFO: Verify " + testUser2.getDisplayName() + " appears in Following view");
        Assert.assertTrue(ui.fluentWaitTextPresent(testUser2.getDisplayName()),
        		"ERROR:- " + testUser2.getDisplayName() + " does not appear in Following view" );
        
        //The message text for testUser2, shows "Network Invitation Sent"
        log.info("INFO: Verify 'Network Invitation Sent' message is shown for user " + testUser2.getDisplayName());
        if(cfg.getProductName().toLowerCase().equals("cloud"))
	        Assert.assertTrue(ui.getContactInFollowingView(testUser2.getDisplayName()).getSingleElement(ui.getUserInviteMessageInFollowingView()).getText().contains(Data.getData().networkInvitationSent),
	        		"ERROR:- 'Network Invitation Sent' message is not shown for user " + testUser2.getDisplayName());
        
        ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Profile_Accept_Invitation</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 20: Another User's Profile Page - Invite - Follow - Accept (2 of 2)</li>
	*<li><B>Info:</B> Invited User Accepts Invitation and verifies new network contact is displayed</li>
	*<li><B>Step:</B> Login as Invited User -> Open People -> My Profile page</li>
	**<li><B>Verify:</B> The message link "1 new invitation" appears in the Network widget.</li>
	*<li><B>Step:</B> Click on message "1 new invitation"</li>
	*<li><B>Verify:</B> The "Invitations" page is opened and invitation is displayed in the view</li>
	*<li><B>Step:</B> Click the (Inviter's) User's name link in the invitation, to open their Profile page</li>
	*<li><B>Verify:</B> Inviters Profile page is opened and message "Pending invitation" is displayed next to user's name.</li>
	*<li><B>Verify:</B> "Accept Invitation" and "Follow" button is active</li> 
	*<li><B>Step:</B> Click the "Accept Invitation" button</li>
	*<li><B>Verify:</B> The message "<user name> has been added to your network contact list.</li>
	*<li><B>Verify:</B> The static text message "Network Contact" is displayed next to the user's name.</li>
	*<li><B>Verify:</B> The "Remove From Network" button is now Active.</li>
	*<li><B>Step:</B> Click People - My Network page</li> 
	*<li><B>Verify:</B> The New network contact is displayed.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/E019D86204CED1D485257E18006C2F17">SC - IC Profiles Regression 20: Another User's Profile Page - Invite - Follow - Accept</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc"})
	public void acceptNetworkInvite() throws Exception {
		
		//Start Test
		ui.startTest();
		User testUser1, testUser2; //Declare test users
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
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
		
		//1 new invitation appears in Network widget
		log.info("INFO: Verify '1 new invitation' link appears in Network widget.");
		ui.fluentWaitPresent(ProfilesUIConstants.ProfilesTagTypeAhead);
		Assert.assertTrue(ui.fluentWaitElementVisible(ProfilesUIConstants.NewNetworkInvitation),
				"ERROR:- '1 new invitation' link is not displayed in Network widget");
		
		//Click on 'new invitation'
		log.info("INFO: Click on 'new invitation'");
		ui.clickLinkWithJavascript(ProfilesUIConstants.NewNetworkInvitation);
		
		//Verify Invitations page is opened
		log.info("INFO: Verify Invitations page is opened");
		Assert.assertTrue(ui.getFirstVisibleElement(MyContacts_LeftNav_Menu.INVITATIONS.getMenuItemLink()).getAttribute("aria-pressed").contains("true"),
				"ERROR: Invitations page is not opened");
		
		//testUser1 is displayed in Invitations page
		log.info("INFO: Verify invitation is displayed in the view");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser1.getDisplayName()),
				"ERROR: " + testUser1.getDisplayName() +" invitation is not displayed in the view");
		
		//Click the (Inviter's) User's name link in the invitation, to open their Profile page
		log.info("INFO: Click on " + testUser1.getDisplayName() + " to open their profile page");
		ui.clickLink("link="+ testUser1.getDisplayName());
		
		//Inviters profile page is opened
		log.info("INFO: Verify Inviters profile page is opened");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.ProfileVcard).getSingleElement("link="+ testUser1.getDisplayName()).isDisplayed(),
				"ERROR: Inviters profile page is not opened");
		
		//The text message "Pending Invitation ..." is displayed
		log.info("INFO: Verify text message " + Data.getData().networkInvitationStatus + " is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().networkInvitationStatus),
				"ERROR:- Text message " + Data.getData().networkInvitationStatus + " is not displayed");
		
		//The "Accept Invitation" button is active.
		log.info("INFO: Verify Accept Invitation button is active");
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.AcceptInvite_OnPrem).isEnabled(),
				"ERROR: Accept Invitation button is not active");
		
		//The "Follow" button is active
		log.info("INFO: Verify Follow button is active");
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.FollowPerson).isEnabled(),
				"ERROR: Follow button is not active");
		
		//Click the "Accept Invitation" button
		log.info("INFO: Click on Accept Invitation button");
		ui.clickLinkWithJavascript(ProfilesUIConstants.AcceptInvite_OnPrem);
		
		//The message " <user's name> has been added to your network contact list.
        log.info("INFO: Verify the message " + testUser1.getDisplayName() + ProfilesUIConstants.AcceptedMessage);
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser1.getDisplayName() + ProfilesUIConstants.AcceptedMessage),
				"ERROR:- " + testUser1.getDisplayName() + ProfilesUIConstants.AcceptedMessage + " is not displayed");
		
		//The text message "Network Contact" is displayed
		log.info("INFO: Verify text message " + Data.getData().networkContactStatus + " is displayed");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.ConnectionIndicator).getText().contentEquals(Data.getData().networkContactStatus),
				"ERROR:- Text message " + Data.getData().networkContactStatus + " is not displayed");
		
		//The "Remove From Network" button is active
		log.info("INFO: Verify Remove From Network button is active");
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.NetworkRemove).isEnabled(),
				"ERROR: Remove From Network button is not active");
		
		//Invitee opens their People - My Network page.
		log.info("INFO: Invitee opens their People - My Network page");
		Profile_View_Menu.MY_NETWORK.select(ui);
		
		//testUser1 is displayed in My Network page
		log.info("INFO: Verify " + testUser1.getDisplayName() +" is displayed in the My Network view");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser1.getDisplayName()),
				"ERROR: " + testUser1.getDisplayName() +" is not displayed in the My Network view");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Profile_Remove_From_Network</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 21: Another User's Profile Page - Remove from Network - Stop </li>
	*<li><B>Test Scenario:</B> OP - IC Profiles Regression 21: Another User's Profile Page - Remove from Network - Stop </li>
	*<li><B>Info:</B>  Remove User who is already in your Network</li>
	*<li><B>Step:</B> Login as Invited User -> Open People -> My Profile page</li>
	**<li><B>Step:</B> Click on image action button of someone who is already in your Network.</li>
	*<li><B>Step:</B> Click the "Remove from Network" button.</li>
	*<li><B>Verify:</B> The message "<user name> has been removed from your network contact list."</li>
	*<li><B>Verify:</B> The "Network Contact" text is removed from the page.</li>
	*<li><B>Verify:</B> The "Invite to My Network" button is displayed.</li>
	*<li><B>Step:</B> Click the Stop Following button.</li> 
	*<li><B>Verify:</B> The message "<user name> has been removed from your following list."</li>
	*<li><B>Verify:</B> The "Follow" button is displayed</li>
	*<li><B>Verify:</B> Open the People - My Network page, Verify The user removed from your network in step#1 above is not listed in the My Network view.</li>
	*<li><B>Verify:</B> Open the "Following" view, Verify the user stopped following in step#2 above is not listed in the Following view.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/9ABF586802A4623485257E1A00494983">SC - IC Profiles Regression 21: Another User's Profile Page - Remove from Network - Stop</a></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/BAFCDEA68F22E98985257EE4004CD4FA">OP - IC Profiles Regression 21: Another User's Profile Page - Remove from Network - Stop</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc","ptc"})
	public void removeFromNetworkAndStopFollowing() throws Exception {
		
		//Start Test
		ui.startTest();
		User testUser1, testUser2; //Declare test users
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
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
		
		//Click on testUser2 image in Network widget
		log.info("INFO: Click on image " + testUser2.getDisplayName());
		ui.clickLinkWait(ui.getContactImage(testUser2));
		
		//Click the "Remove from Network" button
		log.info("INFO: Click the 'Remove from Network' button");
		ui.clickLink(ProfilesUIConstants.NetworkRemove);
		
		//The message " <user's name> has been removed from your network contact list.
        log.info("INFO: Verify the message " + testUser2.getDisplayName() + Data.getData().removedNetworkMessage);
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser2.getDisplayName() + Data.getData().removedNetworkMessage),
				"ERROR:- " + testUser2.getDisplayName() + Data.getData().removedNetworkMessage + " is not displayed");
		
		//The text message "Network Contact" is removed from the page
		log.info("INFO: Verify text message " + Data.getData().networkContactStatus + " is removed from the page");
		driver.turnOffImplicitWaits(); //As below element is not present, Increase test execution speed.
		Assert.assertFalse(driver.getFirstElement(ProfilesUIConstants.ConnectionIndicator).getText().contentEquals(Data.getData().networkContactStatus),
				"ERROR:- Text message " + Data.getData().networkContactStatus + " is not removed from the page");
		driver.turnOnImplicitWaits();
		
		//The "Invite to My Network" button is displayed
        log.info("INFO: Verify 'Invite to My Network' button is displayed");
        Assert.assertTrue(ui.isElementPresent(ProfilesUIConstants.Invite_OnPrem),
        		"ERROR: 'Invite to My Network' button is not displayed");
        
        //Click the Stop Following button.
        log.info("INFO: Click the Stop Following button.");
        ui.clickLink(ProfilesUIConstants.Unfollow);
        
        //The message "<user name> has been removed from your following list."
        log.info("INFO: Verify the message " + testUser2.getDisplayName() + Data.getData().stopNetworkFollowingMessage);
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser2.getDisplayName() + Data.getData().stopNetworkFollowingMessage),
				"ERROR:- " + testUser2.getDisplayName() + Data.getData().stopNetworkFollowingMessage + " is not displayed");
		
		//Follow button is displayed
		log.info("INFO: Follow button is displayed");
		Assert.assertTrue(ui.isElementPresent(ProfilesUIConstants.FollowPerson),
				"ERROR: Follow button is not displayed");
		
		//Open the People -> My Network page
		log.info("INFO: Open the People -> My Network page");
		ui.gotoMyNetwork();
		
		//testUser2 is removed from My Network view
		log.info("INFO: " + testUser2.getDisplayName() + " is removed from My Network view");
		driver.turnOffImplicitWaits(); //As below element is not present, Increase test execution speed.
		Assert.assertFalse(ui.isElementPresent("link="+ testUser2.getDisplayName()),
				"ERROR: " + testUser2.getDisplayName() + " is not removed from My Network view");
		driver.turnOnImplicitWaits();
		
		//Open the "Following" view
		log.info("INFO: Open the 'Following' view");
		MyContacts_LeftNav_Menu.FOLLOWING.open(ui);
		
		//testUser2 is removed from Following view
		log.info("INFO: " + testUser2.getDisplayName() + " is removed from Following view");
		driver.turnOffImplicitWaits(); //As below element is not present, Increase test execution speed.
		Assert.assertFalse(ui.isElementPresent("link="+ testUser2.getDisplayName()),
				"ERROR: " + testUser2.getDisplayName() + " is not removed from Following view");
		driver.turnOnImplicitWaits();
		
	}
	
	/**
	*<ul>
	*<li><B>Profile_Accept_Invitation</B></li>
	*<li><B>Test Scenario:</B> OP - IC Profiles Regression 20: Another User's Profile Page - Invite - Follow - Accept (2 of 2)</li>
	*<li><B>Info:</B> Invited User Accepts Invitation and verifies new network contact is displayed</li>
	*<li><B>Step:</B> Login as the Invited User, Open their Profiles - My Network page</li>
	**<li><B>Verify:</B> the invitation is displayed in the view.</li>
	*<li><B>Step:</B> Click the Accept button.</li>
	*<li><B>Verify:</B> The Invite is removed from the view.</li>
	*<li><B>Step:</B> Open the My network - My Network Contacts view</li>
	*<li><B>Verify:</B> The Invited user in now in the view</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/C622AB62F30FB90F85257EE40044C0AB">OP - IC Profiles Regression 20: Another User's Profile Page - Invite - Follow - Accept</a></li>
	*</ul>
	*/
	@Test(groups={"ptc"})
	public void acceptNetworkInviteOnPrem() throws Exception {
		
		//Start Test
		ui.startTest();
		User testUser1, testUser2; //Declare test users
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
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
		
		//Click the "Accept" button
		log.info("INFO: Click on Accept button");
		ui.acceptUserInvite(testUser1);
		
		//The testUser1 is removed from the view.
		log.info("INFO: Verify the "+testUser1.getDisplayName()+" is removed from the view.");
		driver.turnOffImplicitWaits(); //As below element is not present, Increase test execution speed.
		Assert.assertFalse(driver.isElementPresent("link="+ testUser1.getDisplayName()),
				"ERROR: " + testUser1.getDisplayName() + " invite is not removed from the view");
		driver.turnOnImplicitWaits();
		
		//Invitee opens their People - My Network page.
		log.info("INFO: Invitee opens their People - My Network page");
		MyContacts_LeftNav_Menu.MY_NETWORK.open(ui);
		
		//testUser1 is displayed in My Network page
		log.info("INFO: Verify " + testUser1.getDisplayName() +" is displayed in the My Network view");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser1.getDisplayName()),
				"ERROR: " + testUser1.getDisplayName() +" is not displayed in the My Network view");
		
		ui.endTest();
		
	}
	
}
