package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015, 2016, 2017                        */
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

public class FVT_YourStatusUpdate_LikeComment_You_Others extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS;

	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5;
	private String statusUpdate, statusUpdateId;
	private User testUser1, testUser2, testUser3, testUser4, testUser5;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(5);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		
		// Set the filters to be tested
		if(isOnPremise) {
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
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the status update created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
	}

	/**
	* test_YourStatusUpdate_LikeComment_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 1 comment on the status update</B></li>
	*<li><B>Step: User 1 like their comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 1</B></li>
	*<li><B>Step: User 2 log into Homepage</B></li>
	*<li><B>Step: User 2 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 2</B></li>
	*<li><B>Step: User 1 add another comment on the status update</B></li>
	*<li><B>Step: User 3 to User 5 like the comment</B></li>
	*<li><B>Step: User 1 like this comment also</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you liked your comment."</B></li>
	*<li><B>Verify: Verify the event shows "You and 3 others liked your comment."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FE60B5B09B5C933585257DEA0051CF52">TTT - MY NOTIFICATIONS - MICROBLOGGING - 00031 - LIKE YOUR OWN COMMENT ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourStatusUpdate_LikeComment_You_Others_Rollup(){

		ui.startTest();
		
		// User 1 will now post a comment to the status update
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		String user1Comment1Id = ProfileEvents.addStatusUpdateComment(statusUpdateId, user1Comment1, profilesAPIUser1);
		
		// User 1 will now like / recommend the comment posted to the status update
		ProfileEvents.likeComment(profilesAPIUser1, user1Comment1Id);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeCommentEvent = ProfileNewsStories.getLikedYourCommentOnAMessageNewsStory_You(ui);
		
		// Verify that the like comment event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, statusUpdate, user1Comment1}, TEST_FILTERS, false);
		
		// User 2 will now like / recommend the comment posted to the status update
		ProfileEvents.likeComment(profilesAPIUser2, user1Comment1Id);
		
		// Create the news story to be verified
		likeCommentEvent = ProfileNewsStories.getLikedYourCommentOnAMessageNewsStory_UserAndYou(ui, testUser2.getDisplayName());
		
		// Verify that the like comment event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, statusUpdate, user1Comment1}, TEST_FILTERS, true);
		
		// User 1 will now post a second comment to the status update
		String user1Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		String user1Comment2Id = ProfileEvents.addStatusUpdateComment(statusUpdateId, user1Comment2, profilesAPIUser1);
		
		// Users 3 through to User 5 will now like / recommend the second comment with User 1 then liking / recommending the second comment
		ProfileEvents.likeComment(profilesAPIUser3, user1Comment2Id);
		ProfileEvents.likeComment(profilesAPIUser4, user1Comment2Id);
		ProfileEvents.likeComment(profilesAPIUser5, user1Comment2Id);
		ProfileEvents.likeComment(profilesAPIUser1, user1Comment2Id);
		
		// Create the news story to be verified
		likeCommentEvent = ProfileNewsStories.getLikedYourCommentOnAMessageNewsStory_YouAndMany(ui, "3");
				
		// Verify that the like comment event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, statusUpdate, user1Comment1, user1Comment2}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}