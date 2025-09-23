package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.eecomment;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

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

/**
 * @author Anthony Cox
 */

public class FVT_VisitorModel_EE_Comment_StatusUpdate_NoVisitor_PublicCommunity extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_EE_Comment_StatusUpdate_NoVisitor_PublicCommunity.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1, testUser3;
	private APIProfilesHandler testUser1Profile;
	private APICommunitiesHandler communityAPIUser1, communityAPIUser3;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		} while(testUser1.getDisplayName().equals(testUser3.getDisplayName()));
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		testUser1Profile = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		communityAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communityAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());	
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
	}
	
	/**
	* visitor_ee_comment_publicCommunity_noVisitor_statusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a public community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 creates a community status update</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: testUser3 goes to the story of the Community Status Update</B></li>
	*<li><B>Step: testUser3 opens the EE</B></li>
	*<li><B>Step: testUser3 clicks into the comment box</B></li>
	*<li><B>Verify: Verification point 1 -  The  Warning message "Comments might be seen by people external to your organization." DOES NOT APPEAR when the user clicks to add a comment</a></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/44E352E7A23D43C585257C8900465935">TTT - VISITORS NOT ADDED TO COMMUNITY - EE - 00040 - PUBLIC COMMUNITY.STATUS.UPDATE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_ee_comment_publicCommunity_noVisitor_statusUpdate() {
		
		String testName = ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " creates a public community");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = executeCommonCreateAndFollowCommunitySteps(baseCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " creates a community status update");
		String communityStatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String communityStatusUpdateId = communityAPIUser1.addStatusUpdate(publicCommunity, communityStatusUpdate);
		
		// Log into Connections as User 3
		executeCommonUser3LoginSteps();
		
		// Assign the news story to be clicked in order to open the EE
		String newsStory = ui.replaceNewsStory(Data.ADD_COMMUNITY_STATUS_UPDATE, baseCommunity.getName(), null, testUser1.getDisplayName());
		
		// Execute all verifications in the EE - verifying that all Shared Externally and warning message components are NOT displayed
		openEEAndExecuteAllCommonVerifications(newsStory, communityStatusUpdate, null);
		
		log.info("INFO: Perform clean-up now that the test has completed");
		testUser1Profile.deleteBoardMessage(communityStatusUpdateId);
		communityAPIUser1.deleteCommunity(publicCommunity);
		ui.endTest();
	}
	
	/**
	 * Performs the common steps:
	 * 
	 * Step 1: User 1 creates a community
	 * Step 2: User 1 adds User 3 to the community
	 * Step 3: User 3 follows the community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @return - The Community instance of the created community
	 */
	private Community executeCommonCreateAndFollowCommunitySteps(BaseCommunity baseCommunity) {
		
		Community community = communityAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add " + testUser3.getDisplayName() + " to the community as a member");
		communityAPIUser1.addMemberToCommunity(testUser3, community, StringConstants.Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " will now follow the community");
		communityAPIUser3.followCommunity(community);
		
		return community;
	}
	
	/**
	 * Performs the common steps:
	 * 
	 * Step 1: User 3 logs into Connections
	 * Step 2: User 3 navigates to the I'm Following view
	 */
	private void executeCommonUser3LoginSteps() {
		
		log.info("INFO: " + testUser3.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);
		
		log.info("INFO: " + testUser3.getDisplayName() + " go to Homepage / Updates / I'm Following / All");
		ui.gotoImFollowing();
	}
	
	/**
	 * Performs the common steps and verifications:
	 * 
	 * Step 1: Opens the EE for the news story provided in the eeNewsStory String parameter
	 * Step 2: Verifies that the community status message is displayed in the EE
	 * Step 3: Verifes that neither the Shared Externally message or icon are displayed in the EE
	 * Step 4: Navigates to and clicks into the Comments input box in the UI
	 * Step 5: Verifies that neither the warning icon or warning message are displayed in the EE
	 * 
	 * @param eeNewsStory - The news story to be clicked on, opening the EE
	 * @param statusComment - The comment posted with the status update (null if no comment has been posted)
	 * @param communityStatusMessage - The community status update which should be displayed in the EE
	 */
	private void openEEAndExecuteAllCommonVerifications(String eeNewsStory, String communityStatusMessage, String statusComment) {
		
		log.info("INFO: " + testUser3.getDisplayName() + " opens the EE of the story");
		ui.filterNewsItemOpenEE(eeNewsStory);
		
		// Verify that the community status update (and comment if relevant) is displayed correctly in the EE
		if(statusComment == null) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{communityStatusMessage}, null, true);
		} else {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{communityStatusMessage, statusComment}, null, true);
		}
		
		// Verify that all Shared Externally components are NOT displayed in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_Icon_EE, HomepageUIConstants.SharedExternally_Message_EE}, null, false);
		
		log.info("INFO: " + testUser3.getDisplayName() + " clicks into the comment box");
		ui.clickLinkWait(HomepageUIConstants.EECommentsTab);
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUIConstants.StatusUpdateFrame));
		ui.fluentWaitElementVisible(HomepageUIConstants.StatusUpdateTextField);
		driver.getSingleElement(HomepageUIConstants.StatusUpdateTextField).click();
		driver.switchToFrame().returnToTopFrame();
		driver.switchToFrame().selectSingleFrameBySelector(HomepageUIConstants.GenericEEFrame);
		
		// Verify that all comment warning message components are NOT displayed in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_AddComments_WarningIcon_EE}, null, false);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{Data.VisitorModel_CommentWarningMsg}, null, false);
	}
}