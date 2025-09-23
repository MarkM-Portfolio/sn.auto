package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
/* Copyright IBM Corp. 2015, 2017                                    */
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

public class FVT_ClearMessage extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser1;
	private String user1StatusUpdate, user1StatusUpdateId;
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will now post a status update
		user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, user1StatusUpdate);
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the status update created during the test
		profilesAPIUser1.deleteBoardMessage(user1StatusUpdateId);
	}

	/**
	* clearStatusUpdate_AS() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Enter a status in the sharebox and select "Clear"</B></li>
	*<li><B>Verify: Verify that the status update is removed.</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/650CBB3C8F276B5885257942004239B4">TTT - Activity Stream Sharebox - 00020 - Status Updates should not be posted when user selects clear button</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void clearStatusUpdate_AS(){
		
		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now enter and then cancel a status update
		String user1StatusUpdateForCancel = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		boolean cancelSuccessful = ProfileEvents.typeStatusUpdateAndCancelStatusUpdateUsingUI(ui, driver, testUser1, user1StatusUpdateForCancel);
		
		// Verify that the status update was entered and cancelled successfully
		HomepageValid.verifyBooleanValuesAreEqual(cancelSuccessful, true);
		
		ui.endTest();
	}

	/**
	* cancelComment_AS() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Post a status update in the sharebox</B></li>
	*<li><B>Step: Enter a comment and select "Cancel"</B></li>
	*<li><B>Verify: Verify that the comment is removed.</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/650CBB3C8F276B5885257942004239B4">TTT - Activity Stream Sharebox - 00020 - Status Updates should not be posted when user selects clear button</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void cancelComment_AS(){

		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now enter and then cancel a comment on the status update
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		boolean cancelSuccessful = ProfileEvents.typeStatusUpdateCommentAndCancelCommentUsingUI(ui, driver, testUser1, user1StatusUpdate, user1Comment);
		
		// Verify that the comment was entered and cancelled successfully
		HomepageValid.verifyBooleanValuesAreEqual(cancelSuccessful, true);
				
		ui.endTest();
	}

	/**
	* cancelComment_EE() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Post a status update in the sharebox</B></li>
	*<li><B>Step: Enter a comment in the EE and select "Cancel"</B></li>
	*<li><B>Verify: Verify that the comment is removed.</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/650CBB3C8F276B5885257942004239B4">TTT - Activity Stream Sharebox - 00020 - Status Updates should not be posted when user selects clear button</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void cancelComment_EE(){
		
		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now enter and then cancel a comment on the status update using the EE
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		boolean cancelSuccessful = UIEvents.addEECommentAndCancelCommentUsingUI(ui, driver, testUser1, user1StatusUpdate, user1Comment);
		
		// Verify that the comment was entered and cancelled successfully using the EE
		HomepageValid.verifyBooleanValuesAreEqual(cancelSuccessful, true);
		
		ui.endTest();
	}	
}