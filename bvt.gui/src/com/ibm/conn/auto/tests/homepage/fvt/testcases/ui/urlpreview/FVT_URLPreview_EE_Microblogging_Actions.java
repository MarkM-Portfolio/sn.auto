package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.urlpreview;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.cssBuilder.CSSBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;

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

public class FVT_URLPreview_EE_Microblogging_Actions extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	
	private User testUser1;
									   
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}
	
	/**
	* urlPreview_EE_notesURL() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update in the embedded sharebox which contains a valid Notes URL</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for that story</B></li>
	*<li><B>Verify: Verify that there is NO URL preview in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0A04DDCB39B54FDB85257C350058C8B0">TTT - URL PREVIEW - EE - 00024 - NOTES URLs ARE NOT SUPPORTED ENTER AS A PLAIN LINK</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_EE_notesURL() {

		ui.startTest();
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now post a status message including a URL from Notes
		String notesURL = "Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0A04DDCB39B54FDB85257C350058C8B0";
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " " + notesURL + " ";
		ProfileEvents.addStatusUpdateUsingUI(ui, testUser1, user1StatusUpdate, true);
		
		// User 1 will now open the EE for the status update posted
		UIEvents.openEE(ui, user1StatusUpdate.trim());
		
		// Verify that the status update including URL is displayed in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1StatusUpdate.trim()}, null, true);
		
		// Verify that there is no URL preview for the Notes URL in the EE
		String urlPreviewEE = CSSBuilder.getURLPreviewWidgetSelector_EE(notesURL);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewEE}, null, false);
		
		ui.endTest();
	}

	/**
	* urlPreview_EE_statusUpdate_like() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update in the embedded sharebox which contains a valid URL</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for that story</B></li>
	*<li><B>Step: Like the story from the EE</B></li>
	*<li><B>Verify: Verify that the story is liked in the Activity Stream</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D65B59DD6C2FCE6385257C3500532684">TTT - URL PREVIEW - EE - 00020 - URL PREVIEW EE ACTION - LIKE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_EE_statusUpdate_like() {

		ui.startTest();
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now post a status message including a valid URL
		String validURL = Data.getData().skyNewsURL;
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		UIEvents.postStatusWithURL(ui, user1StatusUpdate, validURL, true);
		
		// Create the elements to be verified
		String statusWithURL = user1StatusUpdate + " " + validURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, statusWithURL, validURL);
		String thumbnailImageStatusUpdate = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, statusWithURL, validURL);
				
		// Verify that the status update message with URL is displayed in the AS
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURL}, null, true);
					
		// Verify that the URL preview widget and thumbnail image are displayed in the AS
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImageStatusUpdate}, null, true);
		
		// User 1 will now open the EE for the status update posted
		UIEvents.openEE(ui, statusWithURL);
		
		// Verify that the status update including URL is displayed in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURL}, null, true);
				
		// Verify that the URL preview widget and thumbnail image are displayed in the EE
		String urlPreviewEE = CSSBuilder.getURLPreviewWidgetSelector_EE(validURL);
		String thumbnailImageEE = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(validURL);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewEE, thumbnailImageEE}, null, true);
		
		// User 1 will now 'like' the status update with URL story using the EE
		UIEvents.clickLikeInEEUsingUI(ui);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		// Create the news story to be verified in all filters
		String likedYourMessageEvent = ProfileNewsStories.getLikedYourMessageNewsStory_You(ui);
		urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, likedYourMessageEvent, validURL);
		thumbnailImageStatusUpdate = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, likedYourMessageEvent, validURL);
		
		for(String filter : TEST_FILTERS) {
			// Verify that like event and status update with URL are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likedYourMessageEvent, statusWithURL}, filter, true);
			
			// Verify that the URL Preview widget and thumbnail image are displayed in all views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImageStatusUpdate}, null, true);
		}
		ui.endTest();
	}
	
	/**
	* urlPreview_EE_resized() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update in the embedded sharebox which contains a valid URL</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for that story</B></li>
	*<li><B>Verify: Verify that the URL preview has been resized to fit in the EE container</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0DF7C120EEEA2CD785257C3500527395">TTT - URL PREVIEW - EE - 00019 - URL PREVIEW RESIZED TO FIT IN EE CONTAINER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_EE_resized() {

		ui.startTest();
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now post a status message including a valid URL
		String validURL = Data.getData().skyNewsURL;
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		UIEvents.postStatusWithURL(ui, user1StatusUpdate, validURL, true);
		
		// Create the elements to be verified
		String statusWithURL = user1StatusUpdate + " " + validURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, statusWithURL, validURL);
		String thumbnailImageStatusUpdate = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, statusWithURL, validURL);
				
		// Verify that the status update message with URL is displayed in the AS
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURL}, null, true);
					
		// Verify that the URL preview widget and thumbnail image are displayed in the AS
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImageStatusUpdate}, null, true);
		
		// User 1 will now open the EE for the status update posted
		UIEvents.openEE(ui, statusWithURL);
		
		// Verify that the URL preview widget and thumbnail image are displayed in the EE
		String urlPreviewEE = CSSBuilder.getURLPreviewWidgetSelector_EE(validURL);
		String thumbnailImageEE = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(validURL);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewEE, thumbnailImageEE}, null, true);
		
		// Verify that the URL Preview widget has been resized to fit inside the EE (ie. it is displayed inside the boundaries of the EE)
		HomepageValid.verifyElementIsInsideTheEE(ui, driver, urlPreviewEE);
		
		ui.endTest();
	}
}