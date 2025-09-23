package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.urlpreview;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.cssBuilder.CSSBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.conn.auto.webui.ProfilesUI;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_URLPreview_EE_Microblogging extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser2;
	private ProfilesUI uiProfiles;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiProfiles = ProfilesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
	}
	
	/**
	* urlPreview_EE_statusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update in the embedded sharebox which contains a valid URL</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for that story</B></li>
	*<li><B>Verify: Verify that the URL preview appears correctly in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9691DD926C0BE43085257C350035497D">TTT - URL PREVIEW - EE - 00010 - STATUS UPDATE URL PREVIEW APPEARS IN EE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_EE_statusUpdate() {

		ui.startTest();
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now post a status update with URL
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		UIEvents.postStatusWithURL(ui, user1StatusUpdate, url, true);
		
		// User 1 will now open the EE for the status update posted
		String statusWithURL = user1StatusUpdate + " " + url;
		UIEvents.openEE(ui, statusWithURL);
		
		// Verify that the status update including URL is displayed in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURL}, null, true);
				
		// Verify that the URL preview widget and thumbnail image are displayed in the EE
		String urlPreviewEE = CSSBuilder.getURLPreviewWidgetSelector_EE(url);
		String thumbnailImageEE = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(url);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewEE, thumbnailImageEE}, null, true);
		
		ui.endTest();
	}

	/**
	* urlPreview_EE_boardMessage() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to testUser2's profile page</B></li>
	*<li><B>Step: Post a new board message in the embedded sharebox which contains a valid URL</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for that story</B></li>
	*<li><B>Verify: Verify that the URL preview appears correctly in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3FC08ABD0BDA5B5B85257C35003A055F">TTT - URL PREVIEW - EE - 00011 - BOARD MESSAGE URL PREVIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_EE_boardMessage() {

		ui.startTest();
		
		// Log in as User 1 and go to User 2's profile
		ProfileEvents.loginAndNavigateToUserProfile(ui, uiProfiles, testUser1, profilesAPIUser2, false);
		
		// User 1 will now post a board message with URL to User 2's profile
		String user1BoardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		ProfileEvents.postBoardMessageWithURLUsingUI(ui, user1BoardMessage, url, true);
		
		// Return to the Home screen and navigate to the I'm Following view
		UIEvents.gotoHomeAndGotoImFollowing(ui);
		
		// User 1 will now open the EE for the board message
		String messagePostedEvent = ProfileNewsStories.getPostedAMessageToUserNewsStory(ui, testUser2.getDisplayName(), testUser1.getDisplayName());
		UIEvents.openEE(ui, messagePostedEvent);
		
		// Verify that the board message including URL is displayed in the EE
		String boardMessageWithURL = user1BoardMessage + " " + url;
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{boardMessageWithURL}, null, true);
				
		// Verify that the URL preview widget and thumbnail image are displayed in the EE
		String urlPreviewEE = CSSBuilder.getURLPreviewWidgetSelector_EE(url);
		String thumbnailImageEE = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(url);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewEE, thumbnailImageEE}, null, true);
		
		ui.endTest();
	}

	/**
	* urlPreview_EE_Mention() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update in the embedded sharebox which contains a valid URL and an @mentions</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for that story</B></li>
	*<li><B>Verify: Verify that the URL preview appears correctly in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5F2BDA68FFDE123A85257C3500471DC3">TTT - URL PREVIEW - EE - 00013 - URL PREVIEW DISPLAY - @ MENTION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_EE_Mention() {

		ui.startTest();
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now enter a status update which contains a valid URL
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		UIEvents.typeStatusWithURL(ui, user1StatusUpdate, url, true);
		
		// User 1 will now append a mentions to User 2 on to the end of the status update
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		UIEvents.typeMentionsOrPartialMentions(ui, mentions, mentions.getUserToMention().getDisplayName().length());
		UIEvents.waitForTypeaheadMenuToLoad(ui);
		UIEvents.getTypeaheadMenuItemsListAndSelectUser(ui, driver, mentions);
		
		// User 1 will now post the status update with URL and mentions to User 2
		ProfileEvents.postStatusUpdateUsingUI(ui);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// User 1 will now open the EE for the status update posted
		String statusWithURLAndMentions = user1StatusUpdate + " " + url + " @" + mentions.getUserToMention().getDisplayName();
		UIEvents.openEE(ui, statusWithURLAndMentions);
		
		// Verify that the status update including URL and mentions is displayed in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURLAndMentions}, null, true);
				
		// Verify that the URL preview widget and thumbnail image are displayed in the EE
		String urlPreviewEE = CSSBuilder.getURLPreviewWidgetSelector_EE(url);
		String thumbnailImageEE = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(url);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewEE, thumbnailImageEE}, null, true);
		
		ui.endTest();
	}
	
	/**
	* urlPreview_EE_statusUpdate_Comment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update in the embedded sharebox which contains a valid URL</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for that story</B></li>
	*<li><B>Step: Post a comment containing a valid URL on the status update from within the EE</B></li>
	*<li><B>Verify: Verify that the URL preview does NOT appear in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/332673C53B0279FF85257C35005ABF0B">TTT - URL PREVIEW - EE - 00026 - URL PREVIEW DOES NOT APPEAR IN COMMENT IN EE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_EE_statusUpdate_Comment() {

		ui.startTest();
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now post a status update with URL
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateURL = Data.getData().skyNewsURL;
		UIEvents.postStatusWithURL(ui, user1StatusUpdate, statusUpdateURL, true);
		
		// User 1 will now open the EE for the status update posted and will post a comment with valid URL using the EE
		String statusUpdateWithURL = user1StatusUpdate + " " + statusUpdateURL;
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String commentURL = Data.getData().FacebookURL;
		UIEvents.addEECommentUsingUI(ui, testUser1, statusUpdateWithURL, user1Comment + " " + commentURL + " ");
		
		// Verify that the URL preview widget and thumbnail image are displayed in the EE for the status update URL
		String urlPreviewEE = CSSBuilder.getURLPreviewWidgetSelector_EE(statusUpdateURL);
		String thumbnailImageEE = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(statusUpdateURL);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewEE, thumbnailImageEE}, null, true);
		
		// Verify that the URL preview widget and thumbnail image are NOT displayed in the EE for the comment URL
		urlPreviewEE = CSSBuilder.getURLPreviewWidgetSelector_EE(commentURL);
		thumbnailImageEE = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(commentURL);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewEE, thumbnailImageEE}, null, false);
		
		ui.endTest();
	}
}