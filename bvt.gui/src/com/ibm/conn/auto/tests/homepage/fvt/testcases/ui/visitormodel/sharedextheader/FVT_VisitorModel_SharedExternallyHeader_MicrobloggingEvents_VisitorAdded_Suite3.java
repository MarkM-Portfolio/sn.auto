package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
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

public class FVT_VisitorModel_SharedExternallyHeader_MicrobloggingEvents_VisitorAdded_Suite3 extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_SharedExternallyHeader_MicrobloggingEvents_VisitorAdded_Suite3.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;
	private APICommunitiesHandler communityAPIUser1, communityAPIUser3;
	private APIProfilesHandler testUser1Profile;
	private String serverURL;	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);	
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while (testUser1.getDisplayName().equals(testUser3.getDisplayName()));		
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		communityAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communityAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		testUser1Profile = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);				
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_statusUpdate_commentWarning() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser1 creates a community status update</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / Status Updates / All</B></li>
	*<li><B>Step: testUser3 goes to the story of the Community Status Update</B></li>
	*<li><B>Step: testUser3 clicks into the comment box- Verification Step 1</B></li>
	*<li><B>Verify: Warning message "Comments might be seen by people external to your organization." appears when the user clicks to add a comment</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C27295DA2042C29585257C860039A627">TTT - VISITORS - ACTIVITY STREAM - 00012 - PRIVATE COMMUNITY.STATUS.UPDATE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_statusUpdate_commentWarning() {		
		
		String testName = ui.startTest();
		
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community restrictedCommunity = executeCommonCommunityCreationSteps(baseCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add a status update to the community");
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = communityAPIUser1.addStatusUpdate(restrictedCommunity, statusUpdate);
		
		// Log in as User 3 and navigate to I'm Following
		executeCommonUserLoginSteps();
				
		log.info("INFO: " + testUser3.getDisplayName() + " clicks into the comment box of the community status update");
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(statusUpdate), HomepageUIConstants.StatusCommentLink);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.ADD_COMMUNITY_STATUS_UPDATE, baseCommunity.getName(), null, testUser1.getDisplayName());
				
		// Set the CSS selectors for the comment warning icon and warning message
		String addCommentWarningIconCSS = HomepageUIConstants.SharedExternally_AddComments_WarningIcon.replaceAll("PLACEHOLDER", newsStory);
		String addCommentWarningMessageCSS = HomepageUIConstants.SharedExternally_AddComments_WarningMessage.replaceAll("PLACEHOLDER", newsStory);
		
		// Verify that the warning icon and message has appeared
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{addCommentWarningIconCSS, addCommentWarningMessageCSS}, null, true);
				
		log.info("INFO: Perform clean-up now that the test has completed");
		testUser1Profile.deleteBoardMessage(statusUpdateId);
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	 * Performs the common procedural steps when creating a community:
	 * 
	 * 1) User 1 creates the community
	 * 2) User 1 adds User 2 and User 3 to the community as a member
	 * 3) User 3 follows the community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @return - The newly created Community instance
	 */
	private Community executeCommonCommunityCreationSteps(BaseCommunity baseCommunity) {
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create the restricted community");
		Community community = communityAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add " + testUser2.getDisplayName() + " to the community as a member");
		communityAPIUser1.addMemberToCommunity(testUser2, community, Role.MEMBER);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add " + testUser3.getDisplayName() + " to the community as a member");
		communityAPIUser1.addMemberToCommunity(testUser3, community, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " will now follow the community");
		communityAPIUser3.followCommunity(community);
		
		return community;
	}
	
	/**
	 * Performs the common procedural steps to log in as User 3 and navigate to I'm Following
	 * 
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	private void executeCommonUserLoginSteps() {
		
		log.info("INFO: Logging in with " + testUser3.getDisplayName() + " to verify news story");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);
		
		log.info("INFO: Navigating to I'm Following view");
		ui.gotoImFollowing();
	}
}