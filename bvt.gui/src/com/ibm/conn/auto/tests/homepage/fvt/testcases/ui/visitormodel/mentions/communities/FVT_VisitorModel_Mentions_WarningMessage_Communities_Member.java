package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.mentions.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
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

public class FVT_VisitorModel_Mentions_WarningMessage_Communities_Member extends SetUpMethods2{
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
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
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		// User 1 will now create a restricted visitor model community with User 2 as a member
		baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand());
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUser1, communitiesAPIUser1, testUser2);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Remove the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* visitorModel_mentionsWarning_privateCommunity_members() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Communities</B></li>
	*<li><B>Step: testUser1 go to a private community you own that has a visitor as a member</B></li>
	*<li><B>Step: testUser1 add a private community status update mentioning a visitor - verification point 1</B></li>
	*<li><B>Step: testUser1 post the update</B></li>
	*<li><B>Step: testUser2 who is the visitor log into Homepage / Mentions - verification point 2</B></li>
	*<li><B>Verify: Verify the warning "The following people cannot view this message because they are not members of the community" does NOT appear</B></li>
	*<li><B>Verify: Verify there is a mentions event</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/916D2FDFE027703085257C8D0046D869">TTT - MENTIONS - 00021 - VISITORS - NO WARNING MESSAGE WHEN VISITOR CANT BE MENTIONED - PRIVATE COMMUNITY UPDATE - MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_mentionsWarning_privateCommunity_members() {

		ui.startTest();
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		
		// User 1 will now post a community status update with mentions - mentioning a visitor who is a member 
		boolean statusPostedCorrectly = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithMentions(restrictedCommunity, baseCommunity, ui, driver, uiCo, testUser1, communitiesAPIUser1, mentions, false, false);
		
		// Verify that the community status update with mentions posted correctly and warning messages were NOT displayed at any point during the process
		HomepageValid.verifyBooleanValuesAreEqual(statusPostedCorrectly, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = CommunityNewsStories.getMentionedYouInAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " VISITOR " + mentions.getAfterMentionText();
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", mentionedYouEvent);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", mentionedYouEvent);
		
		// Verify that the mentions event is displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, true);
		
		// Verify that the Shared Externally icon and message are displayed in the Mentions view
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelector}, null, true);
				
		ui.endTest();
	}
}