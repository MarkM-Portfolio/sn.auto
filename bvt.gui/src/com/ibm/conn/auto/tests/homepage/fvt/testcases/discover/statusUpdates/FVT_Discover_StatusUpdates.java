package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.statusUpdates;

import java.util.HashMap;
import java.util.Set;

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
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016 	                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Discover_StatusUpdates extends SetUpMethodsFVT {

	private String[] TEST_FILTERS;

	private APIProfilesHandler profilesAPIUser1;
	private HashMap<String, APIProfilesHandler> statusUpdatesToBeDeleted = new HashMap<String, APIProfilesHandler>();
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		if(isOnPremise){
			TEST_FILTERS = new String[3];
			TEST_FILTERS[2] = HomepageUIConstants.FilterProfiles;
		} else {
			TEST_FILTERS = new String[2];
		}
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterSU;
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Have all users delete all status updates created during the tests
		Set<String> setOfStatusUpdates = statusUpdatesToBeDeleted.keySet();
		
		for(String statusUpdateId : setOfStatusUpdates) {
			statusUpdatesToBeDeleted.get(statusUpdateId).deleteBoardMessage(statusUpdateId);
		}
	}
	
	/**
	*<ul>
	*<li><B>Name: test_deleteStatusUpdateUI_Discover()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 add a status update</B></li>
	*<li><B>Step: testUser 1 go to Homepage / Updates / Discover / Status Updates</B></li>
	*<li><B>Step: testUser 1 click the 'X' on the story of the status update</B></li>
	*<li><B>Verify: Verify the story of the status update has been deleted</B></li>		
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/9DB6824D8458505B85257944003204A6">TTT - Activity Stream - Discover - 00011 - Delete Status Update</a></li>
	*@author Patrick Doherty - FVT Homepage and News Automation
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_deleteStatusUpdateUI_Discover() {
			
		ui.startTest();
				
		// User 1 post a Status Update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		
		// Add status update id to HashMap for AfterClass deletion
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Filter the AS by 'Status Updates'
		UIEvents.filterBy(ui, HomepageUIConstants.FilterSU);
		
		// User 1 delete the status update using the UI ('X' icon)
		ProfileEvents.deleteStatusUpdateUsingUI(ui, statusUpdate);
		
		// Mark this status update as removed (ie. no requirement to remove it again in the AfterClass)
		statusUpdatesToBeDeleted.remove(statusUpdateId);

		// Verify that the status update is NOT displayed in any of the views after deletion
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{statusUpdate}, TEST_FILTERS, false);

		ui.endTest();			
	}		

	/**
	*<ul>
	*<li><B>Name: test_deleteStatusUpdateUI_Discover()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 add a status update</B></li>
	*<li><B>Step: testUser 1 comment on the status update</B></li>
	*<li><B>Step: testUser 1 go to Homepage / Updates / Discover / Status Updates</B></li>
	*<li><B>Step: testUser 1 click the 'X' on the story of the comment on the status update</B></li>
	*<li><B>Verify: Verify the story of the comment on the status update has been deleted</B></li>		
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/F475EB22A346A14B8525794400322F42">TTT - Activity Stream - Discover - 00011 - Delete Status Update</a></li>
	*@author Patrick Doherty - FVT Homepage and News Automation
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_deleteStatusUpdateCommentUI_Discover() {
			
		ui.startTest();
				
		// User 1 post a Status Update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		
		// Add status update id to HashMap for AfterClass deletion
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
				
		// User 1 will now post a comment to the status update
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, comment, profilesAPIUser1);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Filter the AS by 'Status Updates'
		UIEvents.filterBy(ui, HomepageUIConstants.FilterSU);
		
		// Create the news story to be used for removing the comment and performing all validations
		String commentOnSUEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_You(ui);
				
		// User 1 delete the status update using the UI ('X' icon)
		ProfileEvents.deleteCommentUsingUI(ui, commentOnSUEvent, comment);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the status update is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate}, filter, true);
			
			// Verify that the comment event and the comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnSUEvent, comment}, null, false);
		}
		// Perform clean up now that the test has completed
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);
		ui.endTest();			
	}
	
	/**
	*<ul>
	*<li><B>Name: test_CommentOnStatusUpdateUI_Discover()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 add a status update</B></li>
	*<li><B>Step: testUser 1 go to Homepage / Updates / Discover / Status Updates</B></li>
	*<li><B>Step: testUser 1 click "comment" - #verification point 1</B></li>
	*<li><B>Step: testUser 1 enter in comment click "Post" - #verification point 2</B></li>
	*<li><B>Verify: Verify when the user click comment that an input field is displayed under the line of actions with a "Post" button and "reset" link</B></li>
	*<li><B>Verify: Verify that when "Post" is clicked that the comment is displayed</B></li>		
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/90E820A3727036E5852579410060F69F">TTT - Homepage / Discover - 00011 - Comment on Status Updates</a></li>
	*@author Patrick Doherty - FVT Homepage and News Automation
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_CommentOnStatusUpdateUI_Discover() {
			
		ui.startTest();
				
		// User 1 post a Status Update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		
		// Add status update id to HashMap for AfterClass deletion
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Filter the AS by 'Status Updates'
		UIEvents.filterBy(ui, HomepageUIConstants.FilterSU);
		
		// User 1 add a comment to the status update using the UI
		String StatusUpdateComment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateCommentUsingUI(ui, testUser1, statusUpdate, StatusUpdateComment);

		// Create the news story
		String commentOnSUEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_You(ui);
		
		// Verify that the comment is displayed in all of the filters in the Discover view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnSUEvent, statusUpdate, StatusUpdateComment}, TEST_FILTERS, true);
		
		// Perform clean up now that the test has completed
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);
		ui.endTest();			
	}		
}