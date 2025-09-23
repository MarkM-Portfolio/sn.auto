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

public class FVT_1001Chars extends SetUpMethodsFVT {
	
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
		
		// User 1 will now post a valid status update to their profile
		user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, user1StatusUpdate);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Have User 1 delete the status update ID's created during the tests
		profilesAPIUser1.deleteBoardMessage(user1StatusUpdateId);
	}

	/**
	* addStatusUpdate1001_AS() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Post a status update with more than 1000 characters in the sharebox</B></li>
	*<li><B>Verify: Verify that when the character count gets to 1001 characters the Post button is disabled.</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3B8F667C6D9CE703852579420041DBDD">TTT - Activity Stream Sharebox - 00018 - User should not be able to post a status with more than 1000 characters</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addStatusUpdate1001_AS(){
		
		ui.startTest();
		
		// Log in as User 1 and go to Status Updates
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now enter a status message which is too long (ie. greater than 1000 characters) into the status update input field
		String user1InvalidStatus = Data.getData().Chars1001;
		boolean warningDisplayedAndLinkDisabled = ProfileEvents.addStatusUpdateTooLongUsingUI(ui, driver, testUser1, user1InvalidStatus);
		
		// Verify that the warning message was correctly displayed and that the 'Post' link was correctly disabled
		HomepageValid.verifyBooleanValuesAreEqual(warningDisplayedAndLinkDisabled, true);
		
		ui.endTest();
	}

	/**
	* addStatusUpdate_Comment1001_EE() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Post a status update in the sharebox</B></li>
	*<li><B>Step: Post a comment with more than 1000 characters on the status update in the EE</B></li>
	*<li><B>Verify: Verify that when the character count gets to 1001 characters the Post button is disabled.</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3B8F667C6D9CE703852579420041DBDD">TTT - Activity Stream Sharebox - 00018 - User should not be able to post a status with more than 1000 characters</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addStatusUpdate_Comment1001_EE(){

		ui.startTest();
		
		// Log in as User 1 and go to Status Updates
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now enter a comment, which is too long (ie. greater than 1000 characters), into the comment input field of the EE for the posted status message
		String user1InvalidComment = Data.getData().Chars1001;
		boolean linkDisabled = ProfileEvents.addEECommentTooLongUsingUI(ui, driver, user1StatusUpdate, testUser1, user1InvalidComment);
		
		// Verify that the 'Post' link was correctly disabled in the EE
		HomepageValid.verifyBooleanValuesAreEqual(linkDisabled, true);
		
		ui.endTest();
	}	
}