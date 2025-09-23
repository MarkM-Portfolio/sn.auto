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

public class FVT_URLPreview_EE_Thumbnail extends SetUpMethodsFVT {
	
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}
	
	/**
	* urlPreview_EE_Thumbnail() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Post a new status update in the embedded sharebox which contains a valid URL that produces a thumbnail</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for that story</B></li>
	*<li><B>Verify: Verify that the URL, title, description and thumbnail appears in the URL preview in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BA50436786D5D4FF85257C35004C6AF1">TTT - URL PREVIEW - EE - 00015 - URL PREVIEW APPEAR IN EE WITH THUMBNAIL</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_EE_Thumbnail() {

		ui.startTest();
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now post a status update with URL
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String validURL = Data.getData().IbmURL;
		UIEvents.postStatusWithURL(ui, user1StatusUpdate, validURL, true);
		
		// Create the elements to be verified
		String statusWithURL = user1StatusUpdate + " " + validURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, statusWithURL, validURL);
		String thumbnailImageNewsFeed = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, statusWithURL, validURL);
		
		// Verify that the status update with URL is displayed in the news feed
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURL}, null, true);
		
		// Verify that the URL preview widget and thumbnail image are displayed in the news feed
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImageNewsFeed}, null, true);
		
		// User 1 will now open the EE for the status update with URL
		UIEvents.openEE(ui, statusWithURL);
		
		// Create the elements to be verified in the EE
		String urlPreviewWidgetEE = CSSBuilder.getURLPreviewWidgetSelector_EE(validURL);
		String thumbnailImageEE = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(validURL);
		
		// Verify that the status update with URL is displayed in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURL}, null, true);
		
		// Verify that URL preview widget and thumbnail image are displayed in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidgetEE, thumbnailImageEE}, null, true);
		
		ui.endTest();
	}

	/**
	* urlPreview_EE_noThumbnail() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Enter a new status update in the embedded sharebox which contains a valid URL and produces a thumbnail</B></li>
	*<li><B>Step: Tick the checkbox to remove the thumbnail and post (Additional step that is not in TTT scenario)</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for that story</B></li>
	*<li><B>Verify: Verify that the URL, title, description, but NO thumbnail appears in the URL preview in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B92A189AC4DBECAE85257C35004DE382">TTT - URL PREVIEW - EE - 00016 - URL PREVIEW APPEAR IN EE WITHOUT THUMBNAIL</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_EE_noThumbnail() {

		ui.startTest();
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now post a status update with URL
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String validURL = Data.getData().skyNewsURL;
		UIEvents.postStatusWithURLAndRemoveThumbnail(ui, user1StatusUpdate, validURL);
		
		// Create the elements to be verified
		String statusWithURL = user1StatusUpdate + " " + validURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, statusWithURL, validURL);
		String thumbnailImageNewsFeed = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, statusWithURL, validURL);
		
		// Verify that the status update with URL is displayed in the news feed
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURL}, null, true);
		
		// Verify that the URL preview widget is displayed in the news feed
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget}, null, true);
		
		// Verify that the thumbnail image is NOT displayed with the URL preview widget in the news feed
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{thumbnailImageNewsFeed}, null, false);
		
		// User 1 will now open the EE for the status update with URL
		UIEvents.openEE(ui, statusWithURL);
		
		// Create the elements to be verified in the EE
		String urlPreviewWidgetEE = CSSBuilder.getURLPreviewWidgetSelector_EE(validURL);
		String thumbnailImageEE = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(validURL);
		
		// Verify that the status update with URL is displayed in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusWithURL}, null, true);
		
		// Verify that URL preview widget is displayed in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidgetEE}, null, true);
		
		// Verify that the thumbnail image is NOT displayed with the URL preview widget in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{thumbnailImageEE}, null, false);
		
		ui.endTest();
	}
}