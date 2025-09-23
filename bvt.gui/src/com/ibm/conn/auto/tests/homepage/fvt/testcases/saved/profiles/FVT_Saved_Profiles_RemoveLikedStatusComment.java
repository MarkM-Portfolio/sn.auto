package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.profiles;

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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	@author 	Anthony Cox
 *	Date:		27th January 2016
 */

public class FVT_Saved_Profiles_RemoveLikedStatusComment extends SetUpMethodsFVT {

	private String TEST_FILTERS[];
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private String user1CommentOnMessage, user1CommentOnMessageId, user1MessageToUser2, user1MessageToUser2Id;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configurations
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		TEST_FILTERS = new String[2];
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		if(isOnPremise) {
			TEST_FILTERS[1] = HomepageUIConstants.FilterProfiles;
		} else {
			TEST_FILTERS[1] = HomepageUIConstants.FilterSU;
		}
		
		// User 1 will now post a board message to User 2
		user1MessageToUser2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1MessageToUser2Id = ProfileEvents.addBoardMessage(user1MessageToUser2, profilesAPIUser1, profilesAPIUser2);
		
		// User 1 will now post a comment to the board message
		user1CommentOnMessage = Data.getData().commonComment + Helper.genStrongRand();
		user1CommentOnMessageId = ProfileEvents.addStatusUpdateComment(user1MessageToUser2Id, user1CommentOnMessage, profilesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the board message created during the test
		profilesAPIUser1.deleteBoardMessage(user1MessageToUser2Id);
	}
	
	/**
	* test_Profiles_RemoveLikedBoardMessageComment()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into another users profile</B></li>
	*<li><B>2. Add a board message with a comment</B></li>
	*<li><B>3. Like the comment</B></li>
	*<li><B>4. Go to Discover / All</B></li>
	*<li><b>5: Save the story of the comment being liked</b></li>
	*<li><b>6: Go to Homepage / Saved / All & Profiles - the story of the comment being liked has been saved</b></li>
	*<li><b>7: Go to users profile activity stream again</b></li>
	*<li><b>8: Unlike the comment previously liked in point 3</b></li>
	*<li><b>9: Go back to the story in Homepage / Saved / Profiles</b></li>
	*<li><b>Verify: Verify that the profiles.wall.comment.recommended.added story is removed from the saved view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/7F54E9289692506185257A8B00454FFA">TTT: AS - Saved - 00021 - Profiles Wall Comment Recommended Added Removed If Comment Unliked</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Profiles_RemoveLikedBoardMessageComment() {
		
		ui.startTest();
		
		// User 1 will now like / recommend the comment posted to the board message
		ProfileEvents.likeComment(profilesAPIUser1, user1CommentOnMessageId);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be saved
		String discoverLikeCommentEvent = ProfileNewsStories.getLikedYourCommentOnABoardMessageNewsStory_You(ui, testUser2.getDisplayName());
		
		// Save the like comment event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, discoverLikeCommentEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String savedLikeCommentEvent = ProfileNewsStories.getLikedTheirOwnCommentOnABoardMessageNewsStory(ui, testUser2.getDisplayName(), testUser1.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{savedLikeCommentEvent, user1MessageToUser2, user1CommentOnMessage}, TEST_FILTERS, true);
		
		// User 1 will now unlike the comment posted to the board message
		ProfileEvents.unlikeComment(profilesAPIUser1, user1CommentOnMessageId);
		
		// Verify that the like comment event and User 1's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{savedLikeCommentEvent, user1MessageToUser2, user1CommentOnMessage}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}