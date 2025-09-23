package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015, 2017                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Legacy Mentions replaced with CKEditor] FVT UI Automation for Story 139553 and Story 139607
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139666
 * @author Patrick Doherty
 */

public class FVT_SU_NoShowMore extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser1;
	private String user1StatusWith4LinesId, user1StatusWith10LinesId;
	private User testUser1;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		user1StatusWith4LinesId = null;
		user1StatusWith10LinesId = null;
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the status updates created during the test
		if(user1StatusWith4LinesId != null) {
			profilesAPIUser1.deleteBoardMessage(user1StatusWith4LinesId);
		}
		if(user1StatusWith10LinesId != null) {
			profilesAPIUser1.deleteBoardMessage(user1StatusWith10LinesId);
		}
	}

	/**
	* addStatusUpdate_4Lines() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Click into the embedded sharebox</B></li>
	*<li><B>Step: Add a status update with 4 lines</B></li>
	*<li><B>Step: Add another status update with 10 lines</B></li>
	*<li><B>Step: Refresh - verification point 1</B></li>
	*<li><B>Step: Click "show more" - verification point 2</B></li>
	*<li><B>Verify: Verify that the status update with 4 lines does not have the "...Show more" link and the status update with 10 lines does have the "...Show more" link</B></li>
	*<li><B>Verify: Verify that the "...Show more" link disappears when it has been clicked and the user can see the entire status added</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/523A01B90830B7738525799C003EA953">TTT - Microblogs Limit - 00012 - If large status update added a show more link should appear</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addStatusUpdate_NoShowMore() {

		ui.startTest();
		
		// Create the status update with 4 lines
		String statusUpdate1Line1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate1Line2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate1Line3 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate1Line4 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate1WithNewLines = statusUpdate1Line1 + "\n" + statusUpdate1Line2 + "\n" + statusUpdate1Line3 + "\n" + statusUpdate1Line4;
		String statusUpdate1WithSpaces = statusUpdate1Line1 + " " + statusUpdate1Line2 + " " + statusUpdate1Line3 + " " + statusUpdate1Line4;
		
		// Create the status update with 10 lines
		String statusUpdate2Line1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate2Line2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate2Line3 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate2Line4 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate2Line5 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate2Line6 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate2Line7 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate2Line8 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate2Line9 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate2Line10 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdate2WithNewLines = statusUpdate2Line1 + "\n" + statusUpdate2Line2 + "\n" + statusUpdate2Line3 + "\n" + statusUpdate2Line4 + "\n" +
											statusUpdate2Line5 + "\n" + statusUpdate2Line6 + "\n" + statusUpdate2Line7 + "\n" + statusUpdate2Line8 + "\n" +
											statusUpdate2Line9 + "\n" + statusUpdate2Line10;
		String statusUpdate2WithSpaces = statusUpdate2Line1 + " " + statusUpdate2Line2 + " " + statusUpdate2Line3 + " " + statusUpdate2Line4 + " " +
											statusUpdate2Line5 + " " + statusUpdate2Line6 + " " + statusUpdate2Line7 + " " + statusUpdate2Line8 + " " +
											statusUpdate2Line9 + " " + statusUpdate2Line10;
		
		// User 1 will now post the status update with 4 lines
		user1StatusWith4LinesId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate1WithNewLines);
		
		// User 1 will now post the status update with 10 lines
		user1StatusWith10LinesId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate2WithNewLines);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the 'Show More' links to be verified
		String statusUpdateWith4LinesShowMore = HomepageUIConstants.ShowMoreSUContent_Unique.replace("PLACEHOLDER", statusUpdate1WithSpaces);
		String statusUpdateWith10LinesShowMore = HomepageUIConstants.ShowMoreSUContent_Unique.replace("PLACEHOLDER", statusUpdate2WithSpaces);
		
		// Verify that the status update with 4 lines does NOT have a 'Show More' link displayed
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{statusUpdateWith4LinesShowMore}, null, false);
		
		// Verify that the status update with 10 lines has a 'Show More' link displayed
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{statusUpdateWith10LinesShowMore}, null, true);
		
		// Click on the 'Show More' link to expand the status update with 10 lines
		UIEvents.clickShowMoreContentLinkForStatusUpdate(ui, statusUpdate2WithSpaces);
		
		// Verify that the status update with 10 lines does NOT have a 'Show More' link displayed
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{statusUpdateWith10LinesShowMore}, null, false);
		
		ui.endTest();
	}
}