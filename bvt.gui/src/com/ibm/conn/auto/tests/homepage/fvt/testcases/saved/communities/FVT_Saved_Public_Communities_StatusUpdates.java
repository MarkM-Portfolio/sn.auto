package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:		Anthony Cox
 * Date:		5th January 2016
 * 
 * PLEASE NOTE: This class must be kept and run in isolation of the other Saved Communities 
 * 				test classes in this package. It CANNOT be merged with other classes.
 */

public class FVT_Saved_Public_Communities_StatusUpdates extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* test_PublicCommunity_RemoveLikedCommentStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into a public community you own</B></li>
	*<li><B>2. Add a status update with a comment in the community</B></li>
	*<li><B>3. Like the comment</B></li>
	*<li><b>4: Go to Homepage / I'm Following / Communities</b></li>
	*<li><b>5: Save the story of the comment being liked</b></li>
	*<li><b>6: Go to Homepage / Saved / All & Communities</b></li>
	*<li><b>Verify: Verify that the community wall comment recommended added story appears</b></li>
	*<li><b>7: Go back to the Community</b></li>
	*<li><b>8: Delete the comment that you liked in Step 3</b></li>
	*<li><b>9: Go back to Homepage / Saved / All & Communities</b></li>
	*<li><b>Verify: Verify that the community wall comment recommended added story is removed from the Saved view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/344933D570946E3E85257A8B004552B4">TTT: AS - Saved - 00021 - Community Wall Comment Recommended Added Removed If Comment Deleted</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UsingPublicCommunity_RemoveLikedCommentStory() {
		
		ui.startTest();
		
		// User 1 will now post a status update in the community
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser1, profilesAPIUser1, statusUpdate);
				
		// User 1 will now post a comment to the community status update and will like / recommend the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String commentId = CommunityEvents.addStatusUpdateCommentAndLikeComment(profilesAPIUser1, profilesAPIUser1, statusUpdateId, comment);
				
		// Create the news story to be saved
		String likeCommentEvent = CommunityNewsStories.getLikeTheirOwnCommentInTheCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
				
		if(isOnPremise) {
			// Save the like comment event news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, likeCommentEvent, false);
						
			// Log in as User 1 and go to the Saved view
			LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		} else {
			// Log in as User 1 and go to the I'm Following view
			LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
			
			// Create the news story to be saved from the I'm Following view (this differs to the news story saved on On Premise)
			String youLikeYourCommentEvent = CommunityNewsStories.getLikeYourCommentInTheCommunityNewsStory_You(ui, baseCommunity.getName());
						
			// User 1 will now save the like comment event news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, youLikeYourCommentEvent);
						
			// Navigate to the Saved view
			UIEvents.gotoSaved(ui);
		}
		
		// Verify that the like comment event news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, statusUpdate, comment}, TEST_FILTERS, true);
		
		// User 1 will now delete the comment that they liked / recommended
		CommunityEvents.deleteStatusUpdateComment(statusUpdateId, commentId, testUser1, communitiesAPIUser1);
		
		// Verify that the like comment event news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, statusUpdate, comment}, TEST_FILTERS, false);
				
		ui.endTest();
	}
}