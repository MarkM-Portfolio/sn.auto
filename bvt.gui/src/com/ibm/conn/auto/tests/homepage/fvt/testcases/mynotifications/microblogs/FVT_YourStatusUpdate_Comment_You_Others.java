package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.microblogs;

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
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;

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
 * [Roll Up of Notifcation Events] FVT UI automation for Story 141809
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/145048
 * @author Patrick Doherty
 */	

public class FVT_YourStatusUpdate_Comment_You_Others extends SetUpMethodsFVT {

	private String[] TEST_FILTERS;

	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private String statusUpdate, statusUpdateId;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// Set the filters to be tested - "Profiles" filter is NOT available on Smart Cloud
		if(isOnPremise){
			TEST_FILTERS = new String[2];
			TEST_FILTERS[1] = HomepageUIConstants.FilterProfiles;
		} else {
			TEST_FILTERS = new String[1];
		}
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
				
		// User 1 will now post a status update
		statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the status update created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
	}

	/**
	* test_YourStatusUpdate_Comment_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 1 comment on their status update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 1</B></li>
	*<li><B>Step: User 2 log into Homepage</B></li>
	*<li><B>Step: User 2 comment on the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 2</B></li>
	*<li><B>Step: User 3 comment on the Status Update</B></li>
	*<li><B>Step: User 1 comment on the Status Update again</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you commented on your message."</B></li>
	*<li><B>Verify: Verify the event shows "You and 2 others commented on your message."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C302C93EA53ECEEE85257DEA0051CF50">TTT - MY NOTIFICATIONS - MICROBLOGGING - 00011 - COMMENT ON YOUR STATUS UPDATE ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourStatusUpdate_Comment_You_Others_Rollup(){

		ui.startTest();
		
		// User 1 will now post their first comment to their own status update
		String user1Comment1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user1Comment1, profilesAPIUser1);
				
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
				
		// Create the news story to be verified
		String commentOnMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_You(ui);
		
		// Verify that the comment on message event and User 1's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnMessageEvent, statusUpdate, user1Comment1}, TEST_FILTERS, false);
		
		// User 2 will now post a comment to User 1's status update
		String user2Comment = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user2Comment, profilesAPIUser2);
		
		// Create the news story to be verified
		commentOnMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_UserAndYou(ui, testUser2.getDisplayName());
		
		// Verify that the comment on message event, User 1's comment and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnMessageEvent, statusUpdate, user1Comment1, user2Comment}, TEST_FILTERS, true);
		
		// User 3 will now post a comment to User 1's status update
		String user3Comment = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user3Comment, profilesAPIUser3);
		
		// User 1 will now post their second comment to their own status update
		String user1Comment2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user1Comment2, profilesAPIUser1);
		
		// Create the news story to be verified
		commentOnMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_YouAndMany(ui, "2");
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on message event, User 3's comment and User 1's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnMessageEvent, statusUpdate, user3Comment, user1Comment2}, filter, true);
			
			// Verify that User 1's first comment and User 2's comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1, user2Comment}, null, false);
		}
		ui.endTest();
	}
}