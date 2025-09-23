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
 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
 */

public class FVT_URLPreview_InlineVideo extends SetUpMethodsFVT {
	
	private User testUser1;
									   
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}
	
	/**
	* urlPreview_inlineVideo_AS_posted() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Go to Homepage / Updates / Status Updates / All</B></li>
	*<li><B>Step: Post a new status update in the embedded sharebox which contains a valid video URL</B></li>
	*<li><B>Verify: Verify that URL Preview with video appears in the Activity Stream</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/89B09BF272BE23E185257C2F00419B26">TTT - URL PREVIEW - SHAREBOX - 00053 - INLINE VIDEO CAN BE POSTED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_inlineVideo_AS_posted() {

		ui.startTest();
		
		// Log in as User 1 and go to Status Updates
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now post a status update with valid video URL
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String videoURL = Data.getData().ibmWatsonVideo;
		UIEvents.postStatusWithVideoURL(ui, user1StatusUpdate, videoURL);
		
		// Create the truncated video URL to be used in the CSS selectors for verifications in the news feed
		String truncatedVideoURL = videoURL.substring(0, videoURL.indexOf(".com") + 4);
		
		// Create the elements to be verified
		String statusWithURL = user1StatusUpdate + " " + videoURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, statusWithURL, truncatedVideoURL);
		String videoThumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, statusWithURL, truncatedVideoURL);
		
		// Verify that the URL preview widget and thumbnail image are displayed in the view
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, videoThumbnailImage}, null, true);
		
		ui.endTest();
	}

	/**
	* urlPreview_inlineVideo_AS_playable() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Go to Homepage / Updates / Status Updates / All</B></li>
	*<li><B>Step: Post a new status update in the embedded sharebox which contains a valid video URL</B></li>
	*<li><B>Step: Play the video</B></li>
	*<li><B>Verify: Verify that video plays within the URL Preview in the Activity Stream</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C51BCEC91C2718C585257C2F004604C0">TTT - URL PREVIEW - SHAREBOX - 00054 - INLINE VIDEO CAN BE PLAYED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_inlineVideo_AS_playable() {

		ui.startTest();
		
		// Log in as User 1 and go to Status Updates
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now post a status update with valid video URL
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String videoURL = Data.getData().ibmSmarterPlanetVideo;
		UIEvents.postStatusWithVideoURL(ui, user1StatusUpdate, videoURL);
		
		// Create the truncated video URL to be used in the CSS selectors for verifications in the news feed
		String truncatedVideoURL = videoURL.substring(0, videoURL.indexOf(".com") + 4);
		
		// Create the elements to be verified
		String statusWithURL = user1StatusUpdate + " " + videoURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, statusWithURL, truncatedVideoURL);
		String videoThumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, statusWithURL, truncatedVideoURL);
		
		// Verify that the URL preview widget and thumbnail image are displayed in the view
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, videoThumbnailImage}, null, true);
		
		// Attempt to play the video in the URL preview widget
		boolean videoPlayed = UIEvents.playVideoInURLPreviewWidgetAndVerifyVideoPlaying(ui, driver, user1StatusUpdate, videoURL, true);
				
		// Verify that the video played as expected since the URL preview widget is located in the news feed
		HomepageValid.verifyBooleanValuesAreEqual(videoPlayed, true);
				
		ui.endTest();
	}
}