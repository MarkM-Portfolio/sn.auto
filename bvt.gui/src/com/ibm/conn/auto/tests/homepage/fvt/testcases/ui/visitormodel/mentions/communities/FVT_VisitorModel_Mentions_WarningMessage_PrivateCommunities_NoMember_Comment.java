package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.mentions.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
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
 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
 */

public class FVT_VisitorModel_Mentions_WarningMessage_PrivateCommunities_NoMember_Comment extends SetUpMethods2{
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private CommunitiesUI uiCo;
	private Community restrictedCommunity;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		// User 1 will now create a restricted community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Remove the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* visitorModel_mentionsWarning_privateCommunity_noMembers_comment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Communities</B></li>
	*<li><B>Step: testUser1 go to a private community you own that has no visitors as members</B></li>
	*<li><B>Step: testUser1 add a status update to the private community</B></li>
	*<li><B>Step: testUser1 add a comment to the update mentioning a visitor - verification point 1</B></li>
	*<li><B>Step: testUser1 post the comment</B></li>
	*<li><B>Step: testUser2 who is the visitor log into Homepage / Mentions - verification point 2</B></li>
	*<li><B>Verify: Verify the warning "The following people cannot view this message because they are not members of the community" appears</B></li>
	*<li><B>Verify: Verify there is no mentions event</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1EE36704EDE7F71F85257C8D00464E0B">TTT - MENTIONS - 00018 - VISITORS - WARNING MESSAGE WHEN VISITOR CANT BE MENTIONED - PRIVATE COMMUNITY COMMENT - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_mentionsWarning_privateCommunity_noMembers_comment() {

		ui.startTest();
		
		// User 1 will now post a status update to the community
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		CommunityEvents.addStatusUpdate(restrictedCommunity, communitiesAPIUser1, profilesAPIUser1, statusUpdate);
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		
		// User 1 will now post a comment with mentions to the community status update - mentioning a visitor
		boolean commentPostedCorrectly = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateCommentWithMentions(restrictedCommunity, baseCommunity, ui, driver, uiCo, statusUpdate, testUser1, communitiesAPIUser1, mentions, false, true);
		
		// Verify that the comment with mentions posted correctly and warning messages were displayed as expected during the process
		HomepageValid.verifyBooleanValuesAreEqual(commentPostedCorrectly, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = CommunityNewsStories.getMentionedYouInACommentOnMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " VISITOR " + mentions.getAfterMentionText();
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", mentionedYouEvent);
		
		/**
		 *  For the 'Shared externally' message CSS selector:
		 *		-> The messageCSSSelector string, when dealing with mentions inside of a comment, causes a
		 *  	   never ending script to be triggered in the browser which causes the test to hang and eventually crash.
		 *  
		 *  		By removing the ":contains('Shared externally')" section from the CSS selector, this issue is resolved.
		 *  		This is the only instance where this CSS selector causes this problem.
		 *  
		 *  		The following verification, using the generic version of the element, is now performed to verify it's absence:
		 *   			1) Check to verify that the more generic version of the element is NOT visible in the UI. 
		 */
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", mentionedYouEvent);
		messageCSSSelector = messageCSSSelector.substring(0, messageCSSSelector.indexOf(":contains('Shared externally')"));
		
		// Verify that the mentions event is NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, statusUpdate, mentionsText}, null, false);
		
		// Verify that the Shared Externally icon and message are NOT displayed in the Mentions view
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelector}, null, false);
				
		ui.endTest();
	}	
}