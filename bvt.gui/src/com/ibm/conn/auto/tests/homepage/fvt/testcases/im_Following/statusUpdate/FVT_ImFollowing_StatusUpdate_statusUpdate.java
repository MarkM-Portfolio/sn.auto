package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.statusUpdate;

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
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / StatusUpdate) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 11/2015
 */

public class FVT_ImFollowing_StatusUpdate_statusUpdate extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private String user2StatusUpdate, user2StatusUpdateId;
	private User testUser1, testUser2;				
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);	
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);			

		// User 1 will now follow User 2
		ProfileEvents.followUser(profilesAPIUser2, profilesAPIUser1);
		
		// User 2 will now post a status update
		user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the status update created during the test
		profilesAPIUser2.deleteBoardMessage(user2StatusUpdateId);
		
		// User 1 will now unfollow User 2 now that the test has completed
		ProfileEvents.unfollowUser(profilesAPIUser2, profilesAPIUser1);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_StatusUpdate_Comment on StatusUpdates()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Connections</B></li>
	*<li><B>Step: User 1 follow User 2</B></li>
	*<li><B>Step: User 2 log into Connections</B></li>
	*<li><B>Step: User 2 add a Status Update</B></li>
	*<li><B>Step: User 2 comment on the status update</B></li>
	*<li><B>Step: User 1 go to Homepage / I'm Following / All & Status Updates</B></li>
	*<li><B>Verify: Verify that the comment is displayed in I'm Following / All & Status Updates filters</B></li>	
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1A1A1DA94FB84D2485257941005EE3FD">TTT - Homepage / I'm Following - 00011 - Comment on Status Updates</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void CommentOnStatusUpdate(){
		
		ui.startTest();
		
		// User 2 will now comment on their status update
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user2StatusUpdateId, user2Comment, profilesAPIUser2);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnStatusEvent = ProfileNewsStories.getCommentedOnTheirOwnMessageNewsStory(ui, testUser2.getDisplayName());
		
		// Verify that the comment on status update event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusEvent, user2StatusUpdate, user2Comment}, TEST_FILTERS, true);
		
		ui.endTest();	
	}		
}		