package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.eecomment;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_EE_Comment_StatusUpdate extends SetUpMethods2{
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser3;
	private APIProfilesHandler profilesAPIUser1;
	private BaseCommunity baseCommunity;
	private Community visitorModelCommunity;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		} while(testUser1.getDisplayName().equals(testUser3.getDisplayName()));
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communitiesAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		// User 1 will now create a visitor model community with User 2 and User 3 added as members and User 3 following the community
		User[] membersList = { testUser2, testUser3 };
		baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand());
		visitorModelCommunity = CommunityEvents.createNewCommunityWithMultipleMembersAndOneFollower(baseCommunity, testUser1, communitiesAPIUser1, membersList, testUser3, communitiesAPIUser3);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(visitorModelCommunity);
	}

	/**
	* visitor_ee_comment_privateCommunity_visitorAdded_statusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow testUser1</B></li>
	*<li><B>Step: testUser1 creates a community status update</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: testUser3 goes to the story of the Community Status Update</B></li>
	*<li><B>Step: testUser3 opens the EE</B></li>
	*<li><B>Step: testUser3 clicks into the comment box</B></li>
	*<li><B>Verify: Warning message "Comments might be seen by people external to your organization." appears when the user clicks to add a comment </B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/561311E328EC862485257C86003E1CE7">TTT - VISITORS - EE - 00010 - PRIVATE COMMUNITY - STATUS UPDATE - COMMENT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_ee_comment_privateCommunity_visitorAdded_statusUpdate() {
		
		ui.startTest();
		
		// User 1 will now add a status update to the community
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		CommunityEvents.addStatusUpdate(visitorModelCommunity, communitiesAPIUser1, profilesAPIUser1, statusUpdate);
		
		// Log in as User 3 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be used to open the EE
		String addCommunityStatusUpdateEvent = ui.replaceNewsStory(Data.ADD_COMMUNITY_STATUS_UPDATE, baseCommunity.getName(), null, testUser1.getDisplayName());
		
		// User 3 will now open the EE for the community status update news story
		UIEvents.openEE(ui, addCommunityStatusUpdateEvent);
		
		// Verify that the community status update is displayed correctly in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate}, null, true);
		
		// Verify that all Shared Externally components (icon and message) are displayed in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_Icon_EE, HomepageUIConstants.SharedExternally_Message_EE}, null, true);
		
		// User 3 will now click into the comment input field in the EE
		UIEvents.switchToEECommentOrRepliesFrame(ui, true);
		
		// Return focus back to the EE
		UIEvents.switchToEEFrame(ui);
		
		// Verify that the comment warning icon component is displayed in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_AddComments_WarningIcon_EE}, null, true);
		
		// Verify that the comment warning message is displayed in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{Data.VisitorModel_CommentWarningMsg}, null, true);
		
		ui.endTest();
	}
}