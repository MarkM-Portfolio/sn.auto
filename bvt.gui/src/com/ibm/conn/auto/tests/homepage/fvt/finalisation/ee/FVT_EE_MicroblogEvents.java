package com.ibm.conn.auto.tests.homepage.fvt.finalisation.ee;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016			                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	12th October 2016
 */

public class FVT_EE_MicroblogEvents extends SetUpMethods2 {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	
	private APIProfilesHandler profilesAPIUser1;
	private HomepageUI ui;
	private String serverURL, statusUpdateForComment, statusUpdateForCommentId, statusUpdateForLike, statusUpdateForLikeId;
	private TestConfigCustom cfg;	
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);

		testUser1 = cfg.getUserAllocator().getUser(this);
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		// User 1 will now post a status update - this status update will be used in the 'EE Like' test case
		statusUpdateForLike = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdateForLikeId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdateForLike);
		
		// User 1 will now post a status update - this status update will be used in the 'EE Comment' test case
		statusUpdateForComment = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdateForCommentId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdateForComment);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);		
	}  
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the status updates created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdateForLikeId);
		profilesAPIUser1.deleteBoardMessage(statusUpdateForCommentId);
	}
	
	/**
	* test_Microblog_EE_Like() 
	*<ul>
	*<li><B>1: Log in as User 1</b></li>
	*<li><B>2: User 1 post a status update</B></li>
	*<li><B>3: Navigate to the Discover view</B></li>
	*<li><B>4: Open the EE for the status update news story</B></li>
	*<li><b>5: Click 'like' in the EE to like / recommend the status update - verification point 1</B></li>
	*<li><b>6: Look again at the Discover view - verification point 2</b></li>
	*<li><B>Verification Point 1: Verify that the 'like' link has now changed to an 'unlike' link in the EE</B></li>
	*<li><b>Verification Point 2: Verify that the like status update event is now displayed in the AS</B></li>
	*<li>No TTT link for this test - this test has been created solely for finalisation automation purposes</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_Microblog_EE_Like() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Open the EE for the status update event
		UIEvents.openEE(ui, statusUpdateForLike);
		
		// Click 'Like' in the EE to like / recommend the wiki page
		UIEvents.clickLikeInEEUsingUI(ui);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		// Create the news story to be verified
		String likedYourMessageEvent = ProfileNewsStories.getLikedYourMessageNewsStory_You(ui);
		
		// Verify that the like status update event news story and status update are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likedYourMessageEvent, statusUpdateForLike}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_Microblog_EE_Comment() 
	*<ul>
	*<li><B>1: Log in as User 1</b></li>
	*<li><B>2: User 1 post a status update</B></li>
	*<li><B>3: Navigate to the Discover view</B></li>
	*<li><B>4: Open the EE for the status update news story</B></li>
	*<li><b>5: Post a comment to the status update using the EE - verification point 1</B></li>
	*<li><b>6: Look again at the Discover view - verification point 2</b></li>
	*<li><B>Verification Point 1: Verify that the comment is displayed in the EE after posting the comment</B></li>
	*<li><b>Verification Point 2: Verify that the comment on status update event and the comment are now displayed in the AS</B></li>
	*<li>No TTT link for this test - this test has been created solely for finalisation automation purposes</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_Microblog_EE_Comment() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Post a comment to the status update using the EE
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addEECommentUsingUI(ui, driver, statusUpdateForComment, testUser1, user1Comment);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		// Create the news story to be verified
		String commentOnYourMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_You(ui);
				
		// Verify that the comment on status update event, the status update and the comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnYourMessageEvent, statusUpdateForComment, user1Comment}, TEST_FILTERS, true);
				
		ui.endTest();
	}
}