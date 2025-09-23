package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.urlpreview;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
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

public class FVT_Discover_URL_Preview extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private ProfilesUI uiProfiles;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiProfiles = ProfilesUI.getGui(cfg.getProductName(),driver);

		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);

		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiProfiles = ProfilesUI.getGui(cfg.getProductName(),driver);
	}
	
	/**
	* urlPreview_Discover_StatusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 post a new status update in the embedded sharebox which contains a valid URL</B></li>
	*<li><B>Step: testUser2 logs in to Homepage / Discover / All and Status Updates</B></li>
	*<li><B>Verify: Verify that URL Preview appears in both filters in Discover</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9E8CE4C088CC8F6985257BCC0046A96A">TTT - DISCOVER - MICROBLOGGING - 00100 - STATUS ADDED WITH URL PREVIEW- SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_Discover_StatusUpdate() {

		ui.startTest();
		
		// Log in as User 1 and navigate to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Post a status update with URL - this URL will NOT produce a thumbnail image
		String statusMessageBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().bbcURL;
		boolean urlPreviewIsDisplayed = UIEvents.postStatusWithURL(ui, statusMessageBeforeURL, url, false);
		
		// Verify that the URL preview widget was displayed before posting the status update with URL
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsDisplayed, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the elements to be verified
		String statusWithURL = statusMessageBeforeURL + " " + url;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, statusWithURL, url);
		String thumbnailImageStatusUpdate = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, statusWithURL, url);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the status update message with URL is displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURL}, filter, true);
			
			// Verify that the URL preview widget is displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget}, null, true);
			
			// Verify that the thumbnail image is NOT displayed with the URL preview widget
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{thumbnailImageStatusUpdate}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* urlPreview_Discover_StatusUpdate_Comment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 post a new status update in the embedded sharebox which contains a valid URL</B></li>
	*<li><B>Step: testUser1 post a comment on the status update in the embedded sharebox which contains a valid URL</B></li>
	*<li><B>Step: testUser2 logs in to Homepage / Discover / All and Status Updates</B></li>
	*<li><B>Verify: Verify that URL Preview for the status update appears in both filters in Discover</B></li>
	*<li><B>Verify: Verify that the comment appears, but the URL Preview for the comment does not appear in either filter in Discover</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AC038EBE35A7137A85257BD5002F0394">TTT - DISCOVER - MICROBLOGGING - 00120 - STATUS COMMENT ADDED WITH URL PREVIEW- SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_Discover_StatusUpdate_Comment() {
		
		ui.startTest();
		
		// Log in as User 1 and navigate to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Post a status update with URL - this URL will produce a thumbnail image
		String statusMessageBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusURL = Data.getData().skyNewsURL;
		String statusMessageWithURL = statusMessageBeforeURL + " " + statusURL;
		boolean urlPreviewAndThumbnailDisplayed = UIEvents.postStatusWithURL(ui, statusMessageBeforeURL, statusURL, true);
		
		// Verify that the URL preview widget and thumbnail image were displayed before posting the status update with URL
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewAndThumbnailDisplayed, true);
		
		// User 1 will now comment on the status update and will add a URL to the comment
		String commentBeforeURL  = Data.getData().commonComment + Helper.genStrongRand();
		String commentURL = Data.getData().StumbleUponURL;
		boolean urlPreviewIsNotDisplayed = UIEvents.postStatusUpdateCommentWithURL(ui, statusMessageWithURL, commentBeforeURL, commentURL);
		
		// Verify that the URL preview widget was NOT displayed for the URL in the comment
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsNotDisplayed, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the elements to be verified
		String commentOnMessageEvent = ProfileNewsStories.getCommentedOnTheirOwnMessageNewsStory(ui, testUser1.getDisplayName());
		String commentWithURL = commentBeforeURL + " " + commentURL;
		String urlPreviewStatusUpdate = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, commentOnMessageEvent, statusURL);
		String thumbnailImageStatusUpdate = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, commentOnMessageEvent, statusURL);
		String urlPreviewComment = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, commentOnMessageEvent, commentURL);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on message event, the status update and the comment posted to the status update are all displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnMessageEvent, statusMessageWithURL, commentWithURL}, filter, true);
			
			// Verify that the URL preview widget and thumbnail image for the status update are both displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewStatusUpdate, thumbnailImageStatusUpdate}, null, true);
			
			// Verify that the URL preview widget for the comment posted to the status update is not displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewComment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* urlPreview_Discover_BoardMessage() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 post a new board message which contains a valid URL on testUser2's profile page</B></li>
	*<li><B>Step: testUser3 logs in to Homepage / Discover / All and Status Updates</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears in both filters in Discover</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D6A3F036F075816E85257BCC0046FC8E">TTT - DISCOVER - MICROBLOGGING - 00101 - BOARD MESSAGE ADDED WITH URL PREVIEW- SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_Discover_UsingBoardMessage() {

		ui.startTest();
		
		// Log in as User 1 and navigate to User 2's profile screen
		ProfileEvents.loginAndNavigateToUserProfile(ui, uiProfiles, testUser1, profilesAPIUser2, false);
				
		// Post a board message with URL - this URL will NOT produce a thumbnail image
		String boardMessageBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String boardMessageURL = Data.getData().ebayIrlURL;
		boolean urlPreviewIsDisplayed = ProfileEvents.postBoardMessageWithURLUsingUI(ui, boardMessageBeforeURL, boardMessageURL, false);
		
		// Verify that the URL preview widget was displayed both before posting the board message and in the Profiles UI AS after posting
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsDisplayed, true);
		
		// Return to the Home screen and log out from Connections
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 3 and navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser3, true);
		
		// Create the elements to be verified
		String postedBoardMessageEvent = ProfileNewsStories.getPostedAMessageToUserNewsStory(ui, testUser2.getDisplayName(), testUser1.getDisplayName());
		String boardMessageWithURL = boardMessageBeforeURL + " " + boardMessageURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, postedBoardMessageEvent, boardMessageURL);
		String thumbnailImageBoardMessage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, postedBoardMessageEvent, boardMessageURL);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the posted board message event and board message with URL are all displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{postedBoardMessageEvent, boardMessageWithURL}, filter, true);
			
			// Verify that the URL preview widget is displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget}, null, true);
			
			// Verify that the thumbnail image is NOT displayed with the URL preview widget
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{thumbnailImageBoardMessage}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* urlPreview_Discover_BoardMessage_Comment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 post a new board message which contains a valid URL on testUser2's profile page</B></li>
	*<li><B>Step: testUser2 goes to Homepage and post a comment on the board message</B></li>
	*<li><B>Step: testUser3 logs in to Homepage / Discover / All and Status Updates</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears in both filters in Discover</B></li>
	*<li><B>Verify: Verify that the comment appears, but the URL Preview for the comment does not appear in either filter in Discover</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/437999C10973C40385257BD50032BFC4">TTT - DISCOVER - MICROBLOGGING - 00121 - BOARD MESSAGE COMMENT ADDED WITH URL PREVIEW- SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_Discover_UsingBoardMessage_Comment() {
		
		/**
		 * In order to prevent duplicate 'User 1 posted a message to you' news stories appearing in the news feed
		 * this test case will use User 3 (as User 2) and User 2 (as User 3)
		 */
		ui.startTest();
		
		// Log in as User 1 and navigate to User 2's profile screen
		ProfileEvents.loginAndNavigateToUserProfile(ui, uiProfiles, testUser1, profilesAPIUser3, false);
				
		// Post a board message with URL - this URL will produce a thumbnail image
		String boardMessageBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String boardMessageURL = Data.getData().IbmURL;
		boolean urlPreviewAndThumbnailDisplayed = ProfileEvents.postBoardMessageWithURLUsingUI(ui, boardMessageBeforeURL, boardMessageURL, true);
		
		// Verify that the URL preview widget and thumbnail image were displayed before posting the board message and in the Profiles UI AS after posting
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewAndThumbnailDisplayed, true);
		
		// Return to the Home screen and log out from Connections
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 2 and navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser3, true);
		
		// User 1 will now comment on the status update and will add a URL to the comment
		String messagePostedToYouEvent = ProfileNewsStories.getPostedAMessageToYouNewsStory(ui, testUser1.getDisplayName());
		String commentBeforeURL  = Data.getData().commonComment + Helper.genStrongRand();
		String commentURL = Data.getData().FacebookURL;
		boolean urlPreviewIsNotDisplayed = UIEvents.postStatusUpdateCommentWithURL(ui, messagePostedToYouEvent, commentBeforeURL, commentURL);
		
		// Verify that the URL preview widget was NOT displayed for the URL in the comment
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsNotDisplayed, true);
		
		// Return to the Home screen and log out from Connections
		LoginEvents.gotoHomeAndLogout(ui);
				
		// Log in as User 3 and navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the elements to be verified
		String commentOnMessageEvent = ProfileNewsStories.getCommentedOnUsersMessagePostedToUserNewsStory(ui, testUser3.getDisplayName(), testUser1.getDisplayName(), testUser3.getDisplayName());
		String boardMessageWithURL = boardMessageBeforeURL + " " + boardMessageURL;
		String commentWithURL = commentBeforeURL + " " + commentURL;
		String urlPreviewBoardMessage = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, commentOnMessageEvent, boardMessageURL);
		String thumbnailImageBoardMessage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, commentOnMessageEvent, boardMessageURL);
		String urlPreviewComment = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, commentOnMessageEvent, commentURL);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on message event, the board message and the comment posted to the board message are all displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnMessageEvent, boardMessageWithURL, commentWithURL}, filter, true);
			
			// Verify that the URL preview widget and thumbnail image for the board message are both displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewBoardMessage, thumbnailImageBoardMessage}, null, true);
			
			// Verify that the URL preview widget for the comment posted to the status update is not displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewComment}, null, false);
		}
		ui.endTest();
	}
}