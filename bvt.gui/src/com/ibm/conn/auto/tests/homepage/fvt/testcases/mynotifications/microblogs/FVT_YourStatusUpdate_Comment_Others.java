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

public class FVT_YourStatusUpdate_Comment_Others extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS;
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7, profilesAPIUser8, profilesAPIUser9, profilesAPIUser10, profilesAPIUser11, profilesAPIUser12, profilesAPIUser13, profilesAPIUser14, profilesAPIUser15, profilesAPIUser16, profilesAPIUser17, profilesAPIUser18, profilesAPIUser19, profilesAPIUser20, profilesAPIUser21, profilesAPIUser22, profilesAPIUser23;
	private String statusUpdate, statusUpdateId;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser13, testUser14, testUser15, testUser16, testUser17, testUser18, testUser19, testUser20, testUser21, testUser22, testUser23;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(23);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		testUser8 = listOfStandardUsers.get(7);
		testUser9 = listOfStandardUsers.get(8);
		testUser10 = listOfStandardUsers.get(9);
		testUser11 = listOfStandardUsers.get(10);
		testUser12 = listOfStandardUsers.get(11);
		testUser13 = listOfStandardUsers.get(12);
		testUser14 = listOfStandardUsers.get(13);
		testUser15 = listOfStandardUsers.get(14);
		testUser16 = listOfStandardUsers.get(15);
		testUser17 = listOfStandardUsers.get(16);
		testUser18 = listOfStandardUsers.get(17);
		testUser19 = listOfStandardUsers.get(18);
		testUser20 = listOfStandardUsers.get(19);
		testUser21 = listOfStandardUsers.get(20);
		testUser22 = listOfStandardUsers.get(21);
		testUser23 = listOfStandardUsers.get(22);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		profilesAPIUser6 = initialiseAPIProfilesHandlerUser(testUser6);
		profilesAPIUser7 = initialiseAPIProfilesHandlerUser(testUser7);
		profilesAPIUser8 = initialiseAPIProfilesHandlerUser(testUser8);
		profilesAPIUser9 = initialiseAPIProfilesHandlerUser(testUser9);
		profilesAPIUser10 = initialiseAPIProfilesHandlerUser(testUser10);
		profilesAPIUser11 = initialiseAPIProfilesHandlerUser(testUser11);
		profilesAPIUser12 = initialiseAPIProfilesHandlerUser(testUser12);
		profilesAPIUser13 = initialiseAPIProfilesHandlerUser(testUser13);
		profilesAPIUser14 = initialiseAPIProfilesHandlerUser(testUser14);
		profilesAPIUser15 = initialiseAPIProfilesHandlerUser(testUser15);
		profilesAPIUser16 = initialiseAPIProfilesHandlerUser(testUser16);
		profilesAPIUser17 = initialiseAPIProfilesHandlerUser(testUser17);
		profilesAPIUser18 = initialiseAPIProfilesHandlerUser(testUser18);
		profilesAPIUser19 = initialiseAPIProfilesHandlerUser(testUser19);
		profilesAPIUser20 = initialiseAPIProfilesHandlerUser(testUser20);
		profilesAPIUser21 = initialiseAPIProfilesHandlerUser(testUser21);
		profilesAPIUser22 = initialiseAPIProfilesHandlerUser(testUser22);
		profilesAPIUser23 = initialiseAPIProfilesHandlerUser(testUser23);
		
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
	* test_YourStatusUpdate_Comment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 2 log into Homepage</B></li>
	*<li><B>Step: User 2 comment on the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 2</B></li>
	*<li><B>Step: User 4 to User 23 comment on the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{profilesAPIUser 2} commented on your message."</B></li>
	*<li><B>Verify: Verify the event shows "{profilesAPIUser 3} and  {profilesAPIUser 2} commented on your message."</B></li>
	*<li><B>Verify: Verify the event shows "{profilesAPIUser 23} and 21 others commented on your message."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F734B87AB23180F685257DEA0051CF4F">TTT - MY NOTIFICATIONS - MICROBLOGGING - 00010 - COMMENT ON STATUS UPDATE ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourStatusUpdate_Comment_Rollup(){

		ui.startTest();
		
		// User 2 will now post a comment to User 1's status update
		String user2Comment = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user2Comment, profilesAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_User(ui, testUser2.getDisplayName());
		
		// Verify that the comment on message event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnMessageEvent, statusUpdate, user2Comment}, TEST_FILTERS, true);
		
		// User 3 will now post a comment to User 1's status update
		String user3Comment = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user3Comment, profilesAPIUser3);
		
		// Create the news story to be verified
		commentOnMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_TwoUsers(ui, testUser2.getDisplayName(), testUser3.getDisplayName());
		
		// Verify that the comment on message event, User 2's comment and User 3's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnMessageEvent, statusUpdate, user2Comment, user3Comment}, TEST_FILTERS, true);
		
		// Users 4 through to User 23 will now post a comment to User 1's status update
		APIProfilesHandler[] apiUsersCommenting = { profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7, profilesAPIUser8, profilesAPIUser9, profilesAPIUser10, profilesAPIUser11, profilesAPIUser12, profilesAPIUser13, profilesAPIUser14, profilesAPIUser15, profilesAPIUser16, profilesAPIUser17, profilesAPIUser18, profilesAPIUser19, profilesAPIUser20, profilesAPIUser21, profilesAPIUser22, profilesAPIUser23};
		String[] userComments = new String[apiUsersCommenting.length];
		for(int index = 0; index < apiUsersCommenting.length; index ++) {
			userComments[index] = Data.getData().commonStatusUpdate + Helper.genStrongRand();
			ProfileEvents.addStatusUpdateComment(statusUpdateId, userComments[index], apiUsersCommenting[index]);
		}
		// Create the news story to be verified
		commentOnMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_UserAndMany(ui, testUser23.getDisplayName(), "21");
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on message event, User 22's comment and User 23's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnMessageEvent, statusUpdate, userComments[18], userComments[19]}, filter, true);
			
			// Verify that the comments posted by Users 2 through to 21 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user3Comment, userComments[0], userComments[1], userComments[2], userComments[3], userComments[4], userComments[5], userComments[6], userComments[7]}, null, false);
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{userComments[8], userComments[9], userComments[10], userComments[11], userComments[12], userComments[13], userComments[14], userComments[15], userComments[16], userComments[17]}, null, false);
		}
		ui.endTest();
	}
}