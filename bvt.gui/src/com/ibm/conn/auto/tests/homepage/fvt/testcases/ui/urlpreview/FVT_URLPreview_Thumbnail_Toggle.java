package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.urlpreview;

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

public class FVT_URLPreview_Thumbnail_Toggle extends SetUpMethodsFVT {
	
	private User testUser1;
									   
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}
	
	/**
	* urlPreview_ToggleThumbnail() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Enter a status update with a URL with multiple thumbnails</B></li>
	*<li><B>Step: Toggle the image using the left and right arrows</B></li>
	*<li><B>Verify: Verify the user can toggle between the thumbnails</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F39D862EA235CF8F85257C8D0054B7EE">TTT - URL PREVIEW - SHAREBOX - 00070 - WHEN MULTIPLE THUMBNAILS USER CAN TOGGLE USING ARROWS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_ToggleThumbnail() {
		
		ui.startTest();
		
		// Log in as User 1 and go to Status Updates
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now enter a status update with URL which generates a URL preview with multiple thumbnail images
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String validURL = Data.getData().skyNewsURL;
		boolean widgetDisplayedWithThumbnail = UIEvents.typeStatusWithURL(ui, user1StatusUpdate, validURL, true);
		
		// Verify that the URL preview widget was displayed with a thumbnail image
		HomepageValid.verifyBooleanValuesAreEqual(widgetDisplayedWithThumbnail, true);
		
		// Retrieve the URL for the current thumbnail displayed in the URL preview widget
		String currentThumbnailURL = UIEvents.getURLForCurrentURLPreviewWidgetThumbnailImage(ui, driver, user1StatusUpdate, validURL, false);
		
		// Toggle the thumbnail images to the right to the next unique image
		String nextThumbnailURL = UIEvents.toggleURLPreviewWidgetThumbnailImageToTheRight(ui, driver, validURL, currentThumbnailURL);
		
		// Verify that the URL of the next image is different to that of the current image (ie. the images changed as expected)
		HomepageValid.verifyStringValuesAreNotEqual(currentThumbnailURL, nextThumbnailURL);
		
		// Toggle the thumbnail images to the right again until the next unique image is found
		currentThumbnailURL = nextThumbnailURL;
		nextThumbnailURL = UIEvents.toggleURLPreviewWidgetThumbnailImageToTheRight(ui, driver, validURL, currentThumbnailURL);
		
		// Verify that the URL of the next image is different to that of the current image (ie. the images changed as expected)
		HomepageValid.verifyStringValuesAreNotEqual(currentThumbnailURL, nextThumbnailURL);
		
		// Toggle the thumbnail images to the left to the next unique image
		currentThumbnailURL = nextThumbnailURL;
		String previousThumbnailURL = UIEvents.toggleURLPreviewWidgetThumbnailImageToTheLeft(ui, driver, validURL, currentThumbnailURL);
				
		// Verify that the URL of the previous image is different to that of the current image (ie. the images changed as expected)
		HomepageValid.verifyStringValuesAreNotEqual(currentThumbnailURL, previousThumbnailURL);
				
		// Toggle the thumbnail images to the left again until the next unique image is found
		currentThumbnailURL = previousThumbnailURL;
		previousThumbnailURL = UIEvents.toggleURLPreviewWidgetThumbnailImageToTheLeft(ui, driver, validURL, currentThumbnailURL);
				
		// Verify that the URL of the next image is different to that of the current image (ie. the images changed as expected)
		HomepageValid.verifyStringValuesAreNotEqual(currentThumbnailURL, nextThumbnailURL);
		
		ui.endTest();
	}
	
	/**
	* urlPreview_ToggleThumbnail() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Enter a status update with a URL with multiple thumbnails</B></li>
	*<li><B>Step: Toggle the image using the left and right arrows</B></li>
	*<li><B>Step: Select an image</B></li>
	*<li><B>Step: Post the status update</B></li>
	*<li><B>Verify: Verify the correct thumbnail has posted</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B7C2A0ABB40CBDC485257C8D005531B8">TTT - URL PREVIEW - SHAREBOX - 00071 - CORRECT THUMBNAIL POSTED WHEN USER HAS TOGGLED AND SELECTED ONE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_ToggleThumbnail_Posted() {

		ui.startTest();
		
		// Log in as User 1 and go to Status Updates
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now enter a status update with URL which generates a URL preview with multiple thumbnail images
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String validURL = Data.getData().skyNewsURL;
		boolean widgetDisplayedWithThumbnail = UIEvents.typeStatusWithURL(ui, user1StatusUpdate, validURL, true);
		
		// Verify that the URL preview widget was displayed with a thumbnail image
		HomepageValid.verifyBooleanValuesAreEqual(widgetDisplayedWithThumbnail, true);
		
		// Retrieve the URL for the current thumbnail displayed in the URL preview widget
		String currentThumbnailURL = UIEvents.getURLForCurrentURLPreviewWidgetThumbnailImage(ui, driver, user1StatusUpdate, validURL, false);
		
		// Toggle the thumbnail images to the left to the next unique image
		String previousThumbnailURL = UIEvents.toggleURLPreviewWidgetThumbnailImageToTheLeft(ui, driver, validURL, currentThumbnailURL);
						
		// Verify that the URL of the previous image is different to that of the current image (ie. the images changed as expected)
		HomepageValid.verifyStringValuesAreNotEqual(currentThumbnailURL, previousThumbnailURL);
						
		// Toggle the thumbnail images to the left again until the next unique image is found
		currentThumbnailURL = previousThumbnailURL;
		previousThumbnailURL = UIEvents.toggleURLPreviewWidgetThumbnailImageToTheLeft(ui, driver, validURL, currentThumbnailURL);
		
		// Toggle the thumbnail images to the right to the next unique image
		currentThumbnailURL = previousThumbnailURL;
		String nextThumbnailURL = UIEvents.toggleURLPreviewWidgetThumbnailImageToTheRight(ui, driver, validURL, currentThumbnailURL);
		
		// Verify that the URL of the next image is different to that of the current image (ie. the images changed as expected)
		HomepageValid.verifyStringValuesAreNotEqual(currentThumbnailURL, nextThumbnailURL);
		
		// Save the URL for this image for verification later
		String chosenThumbnailURL = nextThumbnailURL;
		
		// Post the status update with URL and with this thumbnail image selected
		ProfileEvents.postStatusUpdateUsingUI(ui);
		
		// Create the elements to be verified in the news feed
		String statusWithURL = user1StatusUpdate + " " + validURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, statusWithURL, validURL);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, statusWithURL, validURL);
		
		// Verify that the status update with URL is displayed in the news feed
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURL}, null, true);
		
		// Verify that the URL preview widget with thumbnail image are displayed in the news feed
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImage}, null, true);
		
		// Retrieve the URL for the current thumbnail displayed in the URL preview widget in the news feed
		String newsFeedThumbnailURL = UIEvents.getURLForCurrentURLPreviewWidgetThumbnailImage(ui, driver, user1StatusUpdate, validURL, true);
		
		// Verify that the thumbnail image selected before posting the status matches the image displayed in the news feed
		HomepageValid.verifyStringValuesAreEqual(chosenThumbnailURL, newsFeedThumbnailURL);
		
		ui.endTest();
	}
}