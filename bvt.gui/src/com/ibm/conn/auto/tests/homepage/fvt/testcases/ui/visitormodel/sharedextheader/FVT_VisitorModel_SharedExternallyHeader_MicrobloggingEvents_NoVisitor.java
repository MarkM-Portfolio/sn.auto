package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
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
/* Copyright IBM Corp. 2010, 2014, 2016	                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_SharedExternallyHeader_MicrobloggingEvents_NoVisitor extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_SharedExternallyHeader_MicrobloggingEvents_NoVisitor.class);

	private HomepageUI ui;	
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3, testUser4;
	private APICommunitiesHandler communityAPIUser1, communityAPIUser2, communityAPIUser3, communityAPIUser4;
	private APIProfilesHandler testUser1Profile, testUser2Profile;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);		
		
		// Select the list of users for these tests
		User listOfUsers[] = selectFourUniqueUsers();
		testUser1 = listOfUsers[0];
		testUser2 = listOfUsers[1];
		testUser3 = listOfUsers[2];
		testUser4 = listOfUsers[3];
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		communityAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communityAPIUser2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		communityAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		communityAPIUser4 = new APICommunitiesHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
		
		testUser1Profile = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		testUser2Profile = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);				
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_statusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 creates a community status update</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2DF0C9ADE6C86FBE85257C8A00585F23">TTT - VISITORS - ACTIVITY STREAM - 00076 - SHARED EXTERNALLY HEADER - MICROBLOGGING EVENTS- PRIVATE COMMUNITY -VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_statusUpdate() {
		
		String testName = ui.startTest();
		
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);	
		Community newCommunity = executeCommonCommunityCreationSteps(baseCommunity, communityAPIUser1, communityAPIUser3, testUser1, testUser3);
		
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(communityAPIUser1.getCommunityUUID(newCommunity)); 	
		
		log.info("INFO: " + testUser1.getDisplayName() + " go to a private community and add a status update");
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = communityAPIUser1.addStatusUpdate(newCommunity, statusUpdate);
		
		// Log in as User 3 and navigate to I'm Following
		executeCommonUserLoginSteps(testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.ADD_COMMUNITY_STATUS_UPDATE, baseCommunity.getName(), null, testUser1.getDisplayName());
		
		// Verify all news stories and 'Shared Externally' components
		verifySharedExternallyIsNotDisplayedInAllFilters(newsStory, statusUpdate, null, null);
		
		log.info("INFO: Delete the community for SmartCloud clean up");	
		testUser1Profile.deleteBoardMessage(statusUpdateId);
		communityAPIUser1.deleteCommunity(newCommunity);		
		ui.endTest();	
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_statusUpdateComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 creates a community status update and comments on the update</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2DF0C9ADE6C86FBE85257C8A00585F23">TTT - VISITORS - ACTIVITY STREAM - 00076 - SHARED EXTERNALLY HEADER - MICROBLOGGING EVENTS- PRIVATE COMMUNITY -VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_statusUpdateComment(){
		
		String testName = ui.startTest();
		
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);	
		Community newCommunity = executeCommonCommunityCreationSteps(baseCommunity, communityAPIUser2, communityAPIUser4, testUser2, testUser4);
		
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(communityAPIUser2.getCommunityUUID(newCommunity)); 
		
		log.info("INFO: " + testUser2.getDisplayName() + " go to a private community and add a status update");
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = communityAPIUser2.addStatusUpdate(newCommunity, statusUpdate);
		
		log.info("INFO: " + testUser2.getDisplayName() + " add a comment to  status update");
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String commentId = communityAPIUser2.commentOnStatusUpdate(statusUpdateId, comment);		
		
		// Log in as User 4 and navigate to I'm Following
		executeCommonUserLoginSteps(testUser4, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.COMMUNITY_STATUS_UPDATE_COMMENT_SAME_USER, baseCommunity.getName(), null, testUser2.getDisplayName());
		
		// Verify all news stories and 'Shared Externally' components
		verifySharedExternallyIsNotDisplayedInAllFilters(newsStory, statusUpdate, comment, null);
		
		log.info("INFO: Delete the community for SmartCloud clean up");
		communityAPIUser2.deleteStatusComment(statusUpdateId, commentId);
		testUser2Profile.deleteBoardMessage(statusUpdateId);
		communityAPIUser2.deleteCommunity(newCommunity);		
		ui.endTest();			
	}
	
	/**
	 * Performs the common procedural steps when creating a community:
	 * 
	 * 1) The user acting as User 1 creates the community
	 * 2) The user acting as User 1 adds the user acting as  User 3 to the community as a member
	 * 3) The user acting as User 3 follows the community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @return - The newly created Community instance
	 */
	private Community executeCommonCommunityCreationSteps(BaseCommunity baseCommunity, APICommunitiesHandler user1CommAPI, APICommunitiesHandler user3CommAPI,
															User user1, User user3) {
		
		Community community = user1CommAPI.createCommunity(baseCommunity);
		
		log.info("INFO: " + user1.getDisplayName() + " will now add " + user3.getDisplayName() + " as a member to the community");
		user1CommAPI.addMemberToCommunity(user3, community, Role.MEMBER);
		
		log.info("INFO: " + user3.getDisplayName() + " follow the Community using API");		
		user3CommAPI.followCommunity(community);
		
		return community;
	}
	
	/**
	 * Performs the common procedural steps to log in as the specified user and navigate to I'm Following
	 * 
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	private void executeCommonUserLoginSteps(User userToLogIn, boolean preserveInstance) {
		
		log.info("INFO: Logging in with " + userToLogIn.getDisplayName() + " to verify news story");
		if(preserveInstance) {
			ui.loadComponent(Data.getData().ComponentHomepage, true);
		} else {
			ui.loadComponent(Data.getData().ComponentHomepage);
		}
		ui.login(userToLogIn);
		ui.gotoImFollowing();
	}
	
	/**
	 * Executes all required verifications for the 'Shared Externally' component displayed
	 * 
	 * @param newsStoryContent - The news story with which the 'Shared Externally' component should not be displayed
	 */
	private void verifySharedExternallyIsNotDisplayedInAllFilters(String newsStoryContent, String storyDescription, String comment1, String comment2) {
				
		// Verify that the news story is displayed in the All filter
		verifyNewsStoryComponentsAreDisplayed(HomepageUIConstants.FilterAll, newsStoryContent, storyDescription, comment1, comment2);
		
		// Verify that the Shared Externally message is not displayed in the All Filter
		verifySharedExternallyIsNotDisplayed(newsStoryContent, null);
		
		// Verify that the news story is displayed in the Communities filter
		verifyNewsStoryComponentsAreDisplayed(HomepageUIConstants.FilterCommunities, newsStoryContent, storyDescription, comment1, comment2);
		
		// Verify that the Shared Externally message is not displayed in the Communities Filter
		verifySharedExternallyIsNotDisplayed(newsStoryContent, null);
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
	 * Verifies that the Shared Externally components are not displayed
	 * 
	 * @param newsStoryContent - The news story with which the 'Shared Externally' component should not be displayed
	 */
	private void verifySharedExternallyIsNotDisplayed(String newsStoryContent, String filter) {
		
		// Create the CSS selectors for the icon and the message
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStoryContent);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStoryContent);
				
		// Verify that both selectors are displayed in the UI
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelector}, filter, false);
	}
	
	/**
	 * Selects four unique users from OrgA
	 * 
	 * @return - The array of four unique users
	 */
	private User[] selectFourUniqueUsers() {
		
		User listOfUsers[] = new User[4];
		int chosenUsers = 0;
		
		while(chosenUsers < listOfUsers.length) {
			User currentUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
			
			boolean alreadyChosen = false;
			int index = 0;
			while(index < chosenUsers && alreadyChosen == false) {
				if(listOfUsers[index].getDisplayName().equalsIgnoreCase(currentUser.getDisplayName())) {
					alreadyChosen = true;
				}
				index ++;
			}
			if(!alreadyChosen) {
				listOfUsers[chosenUsers] = currentUser;
				chosenUsers ++;
			}
		}
		return listOfUsers;
	}
}