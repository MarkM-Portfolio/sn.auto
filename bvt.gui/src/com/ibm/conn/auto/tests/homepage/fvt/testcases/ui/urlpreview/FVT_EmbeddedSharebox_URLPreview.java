package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.urlpreview;

import java.util.ArrayList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * Author:		Anthony Cox
 * Date:		28th January 2016
 */

public class FVT_EmbeddedSharebox_URLPreview extends SetUpMethodsFVT {
	
	private APIActivitiesHandler activitiesAPIUser1;
	private ArrayList<Activity> listOfActivities = new ArrayList<Activity>();
	private User testUser1;
									   
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all activities created during the tests
		for(Activity activity : listOfActivities) {
			activitiesAPIUser1.deleteActivity(activity);
		}
	}
	
	/**
	* urlPreview_embeddedSharebox_beforePosting() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Type a new status update which contains a valid URL in the Homepage embedded sharebox</B></li>
	*<li><B>Verify: Verify that URL Preview appears before the status update is posted</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EB6445F2E82BEC3B85257BD400550254">TTT - URL PREVIEW - SHAREBOX - 00001 - URL PREVIEW APPEARS WHEN USER PRESSES SPACE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_embeddedSharebox_beforePosting() {

		ui.startTest();
		
		// Log in as User 1 and navigate to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now enter a status message with URL into the status update input field
		String url = Data.getData().bbcURL;
		boolean urlPreviewIsDisplayed = UIEvents.typeStatusWithURL(ui, "", url, false);
		
		// Verify that the URL preview widget was displayed as expected
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsDisplayed, true);
		
		ui.endTest();
	}
	
	/**
	* urlPreview_embeddedSharebox_addAFileReenabled() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Type a new status update which contains a valid URL in the Homepage embedded sharebox</B></li>
	*<li><B>Step: Attempt to click the "Add a File" link for the embedded sharebox</B></li>
	*<li><B>Step: Remove the URL preview</B></li>
	*<li><B>Step: Attempt to click the "Add a File" link for the embedded sharebox</B></li>
	*<li><B>Verify: Verify that when the URL Preview appears the "Add a File" link is disabled</B></li>
	*<li><B>Verify: Verify that when the URL Preview is removed the "Add a File" link is re-enabled</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7CD71523B932DE0C85257BD500342D19">TTT - URL PREVIEW - SHAREBOX - 00007 - URL REMOVED ADD A FILE REINABLED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_embeddedSharebox_addAFileReenabled() {
		
		ui.startTest();
		
		// Log in as User 1 and navigate to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now enter a status message with URL into the status update input field while also verifying that the behaviour of the 'Add a File' link is correct
		String url = Data.getData().bbcURL;
		boolean addAFileLinkBehaviourCorrect = UIEvents.typeStatusWithURLAndRemoveURLPreviewWidgetAndVerifyAddAFileLink(ui, "", url, false);
		
		// Verify that all actions completed successfully and all behaviour for the 'Add a File' link was correct
		HomepageValid.verifyBooleanValuesAreEqual(addAFileLinkBehaviourCorrect, true);
		
		ui.endTest();
	}
	
	/**
	* urlPreview_embeddedSharebox_invalidURL() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Type a new status update which contains an invalid URL in the Homepage embedded sharebox</B></li>
	*<li><B>Verify: Verify that URL Preview does not appear before the status update is posted</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/22B858AF8DA24AD885257BC6004AF601">TTT - URL PREVIEW - SHAREBOX - 00020 - URL PREVIEW WIDGET DISPLAYED - INVALID URL</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_embeddedSharebox_invalidURL() {

		ui.startTest();
		
		// Log in as User 1 and navigate to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now enter a status message with invalid URL into the status update input field
		String invalidURL = Data.getData().bbcURL.replaceAll(".uk", "").trim();
		boolean urlPreviewIsNotDisplayed = UIEvents.typeStatusWithInvalidURL(ui, "", invalidURL);
		
		// Verify that the URL preview widget was not displayed when the invalid URL was entered
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsNotDisplayed, true);
		
		ui.endTest();
	}
	
	/**
	* urlPreview_embeddedSharebox_activityURL() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create an activity</B></li>
	*<li><B>Step: Type a new status update which contains the URL for the activity in the Homepage embedded sharebox</B></li>
	*<li><B>Verify: Verify the activities URL preview appears correctly</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B60985F6EA461BEF85257BD500372359">TTT - URL PREVIEW - SHAREBOX - 00029 - URL PREVIEW FOR ALL CONNECTIONS APPLICATIONS - ACTIVITIES</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_embeddedSharebox_activityURL() {

		String testName = ui.startTest();
		
		// User 1 will now create a public standalone activity
		BaseActivity baseActivity = ActivityBaseBuilder.buildBaseActivity(testName + Helper.genStrongRand(), false);
		Activity publicActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
		listOfActivities.add(publicActivity);
		
		// Retrieve the URL corresponding to the UI page for the public activity
		String activityURL = ActivityEvents.loginAndNavigateToActivityAndGetActivityURL(ui, driver, publicActivity, testUser1, false);
		
		// Return to the home screen and navigate to the Status Updates view
		UIEvents.gotoHomeAndGotoStatusUpdates(ui);
		
		// User 1 will now enter a status message with the activities URL into the status update input field
		boolean urlPreviewIsDisplayed = UIEvents.typeStatusWithConnectionsURL(ui, "", activityURL);
		
		// Verify that the URL preview widget was displayed as expected
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsDisplayed, true);
		
		ui.endTest();
	}
}