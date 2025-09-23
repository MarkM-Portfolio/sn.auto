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

public class FVT_VisitorModel_SharedExternallyHeader_MicrobloggingEvents_VisitorAdded extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_SharedExternallyHeader_MicrobloggingEvents_VisitorAdded.class);

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
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_statusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 creates a community status update</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD6426C3FEAEB36A85257C8A00585DD9">TTT - VISITORS - ACTIVITY STREAM - 00075 - SHARED EXTERNALLY HEADER - MICROBLOGGING EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_statusUpdate(){
		
		String testName = ui.startTest();
		
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community restrictedCommunity = executeCommonCommunityCreationSteps(baseCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add a status update to the community");
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = communityAPIUser1.addStatusUpdate(restrictedCommunity, statusUpdate);
		
		// Log in as User 3 and navigate to I'm Following
		executeCommonUserLoginSteps();
				
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.ADD_COMMUNITY_STATUS_UPDATE, baseCommunity.getName(), null, testUser1.getDisplayName());
		
		// Verify all news stories and 'Shared Externally' components
		verifySharedExternallyIsDisplayedInAllFilters(newsStory, statusUpdate, null, null);
		
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
	
	/**
	 * Executes all required verifications for the 'Shared Externally' component displayed
	 * 
	 * @param newsStoryContent - The news story with which the 'Shared Externally' component should be displayed
	 */
	private void verifySharedExternallyIsDisplayedInAllFilters(String newsStoryContent, String storyDescription, String comment1, String comment2) {
				
		// Verify that the news story is displayed in the All filter
		verifyNewsStoryComponentsAreDisplayed(HomepageUIConstants.FilterAll, newsStoryContent, storyDescription, comment1, comment2);
		
		// Verify that the Shared Externally message is displayed in the All Filter
		verifySharedExternallyIsDisplayed(newsStoryContent, null);
		
		// Verify that the news story is displayed in the Communities filter
		verifyNewsStoryComponentsAreDisplayed(HomepageUIConstants.FilterCommunities, newsStoryContent, storyDescription, comment1, comment2);
		
		// Verify that the Shared Externally message is displayed in the Communities Filter
		verifySharedExternallyIsDisplayed(newsStoryContent, null);
	}
	
	/**
	 * Verifies all news story components are displayed - including all comments
	 * 
	 * @param newsStoryContent - The news story content to be verified as displayed
	 * @param storyDescription - The description of the news story event to be verified (null if no verification required)
	 * @param comment1 - The first comment posted to the news story event to be verified (null if no verification required)
	 * @param comment2 - The second comment posted to the news story event to be verified (null if no verification required)
	 */
	private void verifyNewsStoryComponentsAreDisplayed(String filter, String newsStoryContent, String storyDescription, String comment1, String comment2) {
		
		if(storyDescription == null && comment1 == null && comment2 == null) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryContent}, filter, true);
		} else if(comment1 == null && comment2 == null) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryContent, storyDescription}, filter, true);
		} else if(comment2 == null){
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryContent, storyDescription, comment1}, filter, true);
		} else {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryContent, storyDescription, comment1, comment2}, filter, true);
		}
	}
	
	/**
	 * Verifies that the Shared Externally components are displayed
	 * 
	 * @param newsStoryContent - The news story with which the 'Shared Externally' component should be displayed
	 */
	private void verifySharedExternallyIsDisplayed(String newsStoryContent, String filter) {
		
		// Create the CSS selectors for the icon and the message
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStoryContent);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStoryContent);
				
		// Verify that both selectors are displayed in the UI
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelector}, filter, true);
	}
}