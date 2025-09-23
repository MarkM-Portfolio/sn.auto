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
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ProfilesUI;

public class RegressionProfiles extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(RegressionProfiles.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;		
	private User testUser1, testUser2;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
	}
	
	/**
	 * Log in as user 1. Search for the profile of user 2 and view it. Attempt
	 * to invite user 2 to user 1's network. Log out and log in as user 2.
	 * View user 2's invites and attempt to accept user 1's invitation. Verify
	 * that user 1 appears in user 2's network contacts.
	 * @throws Exception
	 */
	@Test(groups = {"regression"} , enabled=false )
	public void inviteUserToMyNetwork() throws Exception {
		
		ui.startTest();

		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);

		//Type user 2 in the searchbox
		ui.searchForUser(testUser2);
		
		//wait for results page
		ui.fluentWaitTextPresent(Data.PROFILES_SEARCH_RESULTS);
		
		//Verify the person appeared in the search results
		String inviteeLink = "link=" + testUser2.getDisplayName();
		Assert.assertTrue(ui.isElementPresent(inviteeLink),
				"The person " + testUser2.getDisplayName() + " was not found when searching.");
		ui.clickLink(inviteeLink);
		
		//Click the invite link
		ui.clickLinkWait(ui.getInviteToMyNetwork());
		
		//Wait for the popup to appear
		ui.fluentWaitTextPresent("Invite " + testUser2.getDisplayName() +
				" to be your network contact");
		
		//click "send invitation" in the popup that appears
		ui.clickLinkWait(ProfilesUIConstants.SendInvite);
		ui.fluentWaitTextPresent(testUser2.getDisplayName() + ProfilesUIConstants.InvitedMessage);

		//Logout
		ui.logout();
		
		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		ui.login(testUser2);

		//Load logged in user's network invitations
		ui.clickLink(ProfilesUIConstants.MyNetwork);
		ui.clickLinkWait(ProfilesUIConstants.LeftNavInvitations);
		
		//Accept the invitation
		log.info("INFO: Click on accept invitation link");
		ui.clickLinkWait("css=a[id^='accept_link'][title='Accept " + testUser1.getDisplayName() + "']");
		
		//Go to my network contacts
		ui.clickLinkWait(ProfilesUIConstants.LeftNavContacts);
		
		//verify the inviter appears
		Assert.assertTrue(ui.isElementPresent("link=" + testUser1.getDisplayName()),
				"The person " + testUser1.getDisplayName() + " did not appear in " +
				testUser1.getDisplayName() + "'s network contacts after attemping to " +
				"accept the network invitation.");
		
		ui.endTest();
	}
	
	/**
	 * Log in as user 1. Search for the profile of user 2 and view it. Click
	 * the "invite to my network" button and type in an invite message longer
	 * than 500 characters in the popup that appears. Verify that an error
	 * message appears stating that the invite message is too long.
	 * @throws Exception
	 */
	@Test(groups = {"regression"} , enabled=false )
	public void inviteUserMsgLimit() throws Exception {
		ui.startTest();

		//Load the component and login
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);

		//Type user 2 in the searchbox
		ui.searchForUser(testUser2);
		
		//wait for results page
		ui.fluentWaitTextPresent(Data.PROFILES_SEARCH_RESULTS);
		
		//Verify the person appeared in the search results
		String inviteeLink = "link=" + testUser2.getDisplayName();
		Assert.assertTrue(ui.isElementPresent(inviteeLink),
				"The person " + testUser2.getDisplayName() + " was not found when searching.");
		ui.clickLink(inviteeLink);
		
		//Click the invite link
		ui.clickLinkWait(ui.getInviteToMyNetwork());
		
		//Wait for the popup to appear
		ui.fluentWaitTextPresent("Invite " + testUser2.getDisplayName() +
				" to be your network contact");
		
		//Write an invite message over 500 characters long
		log.info("INFO: Entering invite message");
		ui.typeText(ProfilesUIConstants.InvitationMessage, Data.LONG_INVITE_MESSAGE);
		
		//click "send invitation"
		ui.clickLinkWait(ProfilesUIConstants.SendInvite);
		
		//Verify an error message appears
		String noErrorMessage = "No error message appeared when trying to invite someone using an " +
				"invite message over 500 characters long.";
		
		//If there is no JavaScript alert, check for the error message in the page's content
		WebDriver wd = (WebDriver) driver.getBackingObject();
		try {
			Alert alert = wd.switchTo().alert();
			Assert.assertTrue(alert.getText().contains(Data.INVITE_MESSAGE_TOO_LONG), noErrorMessage);
			alert.accept();
		} catch (NoAlertPresentException Ex) {
			Assert.assertTrue(ui.isTextPresent(Data.INVITE_MESSAGE_TOO_LONG), noErrorMessage);
		}
		
		ui.endTest();
	}
	
	// test tagUserProfile omitted as it duplicates the profilesAddTagThenSearch
	// test in BVT_Level_2_Profiles
}