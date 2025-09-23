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

public class FVT_Discover_SU_AdminDeletionUI extends SetUpMethodsFVT {

	private String[] TEST_FILTERS;

	private APIProfilesHandler profilesAPIUser1;
	private HashMap<String, APIProfilesHandler> statusUpdatesToBeDeleted = new HashMap<String, APIProfilesHandler>();
	private User adminUser, testUser1;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfAdminUsers(1);
		adminUser = listOfAdminUsers.get(0);
		
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
		
		// Remove all of the status updates created during the tests
		Set<String> setOfStatusUpdates = statusUpdatesToBeDeleted.keySet();
		
		for(String statusUpdateId : setOfStatusUpdates) {
			statusUpdatesToBeDeleted.get(statusUpdateId).deleteBoardMessage(statusUpdateId);
		}
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Admin_deleteStatusUpdateUI_Discover()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections as the News Administrator</B></li>
	*<li><B>Step: testUser 1 add a status update</B></li>
	*<li><B>Step: adminUser go to Homepage / Updates / Discover / Status Updates</B></li>
	*<li><B>Step: adminUser click the 'X' on the story of the status update added by testUser1</B></li>
	*<li><B>Verify: Verify the story of the status update has been deleted</B></li>		
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/2B876D9171076E2685257959003D5764">TTT - Activity Stream - Discover - 00014 - News Admin can delete any status update</a></li>
	*@author Patrick Doherty - FVT Homepage and News Automation
	*</ul>
	*/	
	@Test(groups={"fvtonprem"})
	public void test_Admin_deleteStatusUpdateUI_Discover() {
			
		ui.startTest();
				
		// User 1 (a non-Admin user) will now post a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		
		// Add status update id to HashMap for AfterClass deletion
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
		
		// Log in as an Admin user and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, adminUser, false);
		
		// Filter the UI by 'Status Updates'
		UIEvents.filterBy(ui, HomepageUIConstants.FilterSU);
		
		// Admin User will now delete User 1's status update using the UI
		ProfileEvents.deleteStatusUpdateUsingUI(ui, statusUpdate);

		// Verify that the status update is NOT displayed in any of the views after deletion
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{statusUpdate}, TEST_FILTERS, false);

		// After test clean up - status update should be removed by this stage
		statusUpdatesToBeDeleted.remove(statusUpdateId);
		ui.endTest();			
	}		

	/**
	*<ul>
	*<li><B>Name: test_Admin_deleteStatusUpdateCommentUI_Discover()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections as the News Administrator</B></li>
	*<li><B>Step: testUser 1 add a status update</B></li>
	*<li><B>Step: testUser 1 comment on the status update</B></li>
	*<li><B>Step: adminUser go to Homepage / Updates / Discover / Status Updates</B></li>
	*<li><B>Step: adminUser click the 'X' on the story of the comment on the status update</B></li>
	*<li><B>Verify: Verify the story of the comment on the status update has been deleted</B></li>		
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/71381C2ED18F425385257959003D7D10">TTT - Activity Stream - Discover - 00015 - News Admin can delete any comment</a></li>
	*@author Patrick Doherty - FVT Homepage and News Automation
	*</ul>
	*/	
	@Test(groups={"fvtonprem"})
	public void test_Admin_deleteStatusUpdateCommentUI_Discover() {
			
		ui.startTest();
				
		// User 1 (a non-Admin user) will now post a status update
		String statusUpdate = Data.getData().UpdateStatus + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		
		// User 1 (a non-Admin user) will now add a comment to the status update
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, comment, profilesAPIUser1);
		
		// Add status update id to HashMap for AfterClass deletion
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
		
		// Log in as an Admin user and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, adminUser, false);
		
		// Filter the UI by 'Status Updates'
		UIEvents.filterBy(ui, HomepageUIConstants.FilterSU);
		
		// Create the news story to which the comment has been posted
		String commentOnSUEvent = ProfileNewsStories.getCommentedOnTheirOwnMessageNewsStory(ui, testUser1.getDisplayName());
		
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
}