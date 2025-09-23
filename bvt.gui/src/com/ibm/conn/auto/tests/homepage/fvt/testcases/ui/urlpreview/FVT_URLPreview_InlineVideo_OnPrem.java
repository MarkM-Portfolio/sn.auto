package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.urlpreview;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
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

public class FVT_URLPreview_InlineVideo_OnPrem extends SetUpMethodsFVT {
	
	private User testUser1;
									   
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}
	
	/**
	* urlPreview_inlineVideo_beforePosting() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Login to Homepage and open the global sharebox</B></li>
	*<li><B>Step: Select "Everyone" from the dropdown options</B></li>
	*<li><B>Step: Type a new status update which contains a valid video URL in the Homepage global sharebox and press space after it</B></li>
	*<li><B>Verify: Verify that the video appears in the URL Preview before the status update is posted</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8AAB205FE1AAE56585257C2F00463AE9">TTT - URL PREVIEW - GLOBAL SHAREBOX - 00050 - INLINE VIDEO CAN BE ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void urlPreview_inlineVideo_beforePosting() {

		ui.startTest();
		
		// Log in as User 1
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Open the global sharebox in the UI and verify that all components are displayed correctly
		UIEvents.openGlobalShareboxAndVerifyAllComponents(ui);
		
		// User 1 will now enter a status update with video URL into the global sharebox
		String user1GlobalShareboxMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String videoURL = Data.getData().ibmConnectionsVideo;
		
		// Type the URL into the global sharebox in order to generate the URL Preview widget
		boolean urlPreviewDisplayed = UIEvents.typeStatusWithVideoURLInGlobalSharebox(ui, user1GlobalShareboxMessage, videoURL);
		
		// Verify that the URL preview widget and video thumbnail were displayed as expected
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewDisplayed, true);
		
		ui.endTest();
	}

	/**
	* urlPreview_inlineVideo_beforePosting_noPlay() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Login to Homepage and open the global sharebox</B></li>
	*<li><B>Step: Select "Everyone" from the dropdown options</B></li>
	*<li><B>Step: Type a new status update which contains a valid video URL in the Homepage global sharebox and press space after it</B></li>
	*<li><B>Step: When the URL preview appears attempt to play the video</B></li>
	*<li><B>Verify: Verify that the video appears in the URL Preview before the status update is posted, but cannot be played</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AE12A7D08F9A14B585257C2F00463AEA">TTT - URL PREVIEW - GLOBAL SHAREBOX - 00051 - INLINE VIDEO CANT BE PLAYED FROM MICROBLOGGING FORM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void urlPreview_inlineVideo_beforePosting_noPlay() {

		ui.startTest();
		
		// Log in as User 1
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Open the global sharebox in the UI and verify that all components are displayed correctly
		UIEvents.openGlobalShareboxAndVerifyAllComponents(ui);
		
		// User 1 will now enter a status update with video URL into the global sharebox
		String user1GlobalShareboxMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String videoURL = Data.getData().ibmConnectionsVideo;
		
		// Type the URL into the global sharebox in order to generate the URL Preview widget
		boolean urlPreviewDisplayed = UIEvents.typeStatusWithVideoURLInGlobalSharebox(ui, user1GlobalShareboxMessage, videoURL);
		
		// Verify that the URL preview widget and video thumbnail were displayed as expected
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewDisplayed, true);
		
		// Attempt to play the video in the URL preview widget
		boolean videoDidNotPlay = UIEvents.playVideoInURLPreviewWidgetAndVerifyVideoPlaying(ui, driver, user1GlobalShareboxMessage, videoURL, false);
		
		// Verify that the video did not play since the URL preview widget had not been posted to the news feed
		HomepageValid.verifyBooleanValuesAreEqual(videoDidNotPlay, true);
		
		ui.endTest();
	}
}