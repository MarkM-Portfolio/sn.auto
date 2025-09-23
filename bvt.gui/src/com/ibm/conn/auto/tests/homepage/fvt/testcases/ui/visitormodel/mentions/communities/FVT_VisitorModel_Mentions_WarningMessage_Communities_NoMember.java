package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.mentions.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
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

public class FVT_VisitorModel_Mentions_WarningMessage_Communities_NoMember extends SetUpMethods2 {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private CommunitiesUI uiCo; 
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
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
		
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		// User 1 will now create a public community with no visitors as members
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = communitiesAPIUser1.createCommunity(basePublicCommunity);
		
		// User 1 will now create a moderated community with no visitors as members
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = communitiesAPIUser1.createCommunity(baseModeratedCommunity);
		
		// User 1 will now create a private community with no visitors as members
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = communitiesAPIUser1.createCommunity(baseRestrictedCommunity);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Remove all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* visitorModel_mentionsWarning_publicCommunity_noMembers() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Communities</B></li>
	*<li><B>Step: testUser1 go to a public community you own that has no visitors as members</B></li>
	*<li><B>Step: testUser1 add a public community status update mentioning a visitor - verification point 1</B></li>
	*<li><B>Step: testUser1 post the update</B></li>
	*<li><B>Step: testUser2 who is the visitor log into Homepage / Mentions - verification point 2</B></li>
	*<li><B>Verify: Verify the warning "The following people cannot view this message because they are not members of the community" appears</B></li>
	*<li><B>Verify: Verify there is no mentions event</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E41AF2E12A07DBEE85257C8D004599B4">TTT - MENTIONS - 00013 - VISITORS - WARNING MESSAGE WHEN VISITOR CANT BE MENTIONED - PUBLIC COMMUNITY UPDATE - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_mentionsWarning_publicCommunity_noMembers() {
		
		ui.startTest();
			
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
				
		// User 1 will now post a community status update with mentions - mentioning a visitor
		boolean statusPostedCorrectly = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithMentions(publicCommunity, basePublicCommunity, ui, driver, uiCo, testUser1, communitiesAPIUser1, mentions, false, true);
		
		// Verify that the community status update with mentions posted correctly and all relevant warning messages were displayed as expected during the process
		HomepageValid.verifyBooleanValuesAreEqual(statusPostedCorrectly, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = CommunityNewsStories.getMentionedYouInAMessageNewsStory(ui, basePublicCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " VISITOR " + mentions.getAfterMentionText();
		
		// Verify that the mentions event is NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, false);
				
		ui.endTest();
	}

	/**
	* visitorModel_mentionsWarning_modCommunity_noMembers() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Communities</B></li>
	*<li><B>Step: testUser1 go to a moderated community you own that has no visitors as members</B></li>
	*<li><B>Step: testUser1 add a moderate community status update mentioning a visitor - verification point 1</B></li>
	*<li><B>Step: testUser1 post the update</B></li>
	*<li><B>Step: testUser2 who is the visitor log into Homepage / Mentions - verification point 2</B></li>
	*<li><B>Verify: Verify the warning "The following people cannot view this message because they are not members of the community" appears</B></li>
	*<li><B>Verify: Verify there is no mentions event</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F150AE289CB80D6D85257C8D00464CFF">TTT - MENTIONS - 00014 - VISITORS - WARNING MESSAGE WHEN VISITOR CANT BE MENTIONED - MODERATE COMMUNITY UPDATE - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_mentionsWarning_modCommunity_noMembers() {

		ui.startTest();
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
				
		// User 1 will now post a community status update with mentions - mentioning a visitor
		boolean statusPostedCorrectly = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithMentions(moderatedCommunity, baseModeratedCommunity, ui, driver, uiCo, testUser1, communitiesAPIUser1, mentions, false, true);
		
		// Verify that the community status update with mentions posted correctly and all relevant warning messages were displayed as expected during the process
		HomepageValid.verifyBooleanValuesAreEqual(statusPostedCorrectly, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = CommunityNewsStories.getMentionedYouInAMessageNewsStory(ui, baseModeratedCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " VISITOR " + mentions.getAfterMentionText();
		
		// Verify that the mentions event is NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, false);
				
		ui.endTest();
	}

	/**
	* visitorModel_mentionsWarning_privateCommunity_noMembers() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Communities</B></li>
	*<li><B>Step: testUser1 go to a private community you own that has no visitors as members</B></li>
	*<li><B>Step: testUser1 add a private community status update mentioning a visitor - verification point 1</B></li>
	*<li><B>Step: testUser1 post the update</B></li>
	*<li><B>Step: testUser2 who is the visitor log into Homepage / Mentions - verification point 2</B></li>
	*<li><B>Verify: Verify the warning "The following people cannot view this message because they are not members of the community" appears</B></li>
	*<li><B>Verify: Verify there is no mentions event</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1BDE1E71FE16F63A85257C8D0046725A">TTT - MENTIONS - 00015 - VISITORS - WARNING MESSAGE WHEN VISITOR CANT BE MENTIONED - PRIVATE COMMUNITY UPDATE - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_mentionsWarning_privateCommunity_noMembers() {

		ui.startTest();
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
				
		// User 1 will now post a community status update with mentions - mentioning a visitor
		boolean statusPostedCorrectly = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithMentions(restrictedCommunity, baseRestrictedCommunity, ui, driver, uiCo, testUser1, communitiesAPIUser1, mentions, false, true);
		
		// Verify that the community status update with mentions posted correctly and all relevant warning messages were displayed as expected during the process
		HomepageValid.verifyBooleanValuesAreEqual(statusPostedCorrectly, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = CommunityNewsStories.getMentionedYouInAMessageNewsStory(ui, baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " VISITOR " + mentions.getAfterMentionText();
		
		// Verify that the mentions event is NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, false);
				
		ui.endTest();
	}
}