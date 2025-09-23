package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.eecomment;

import java.util.List;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
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

public class FVT_VisitorModel_EE_Comment_ForumTopic extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_EE_Comment_ForumTopic.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;
	private APICommunitiesHandler communityAPIUser1, communityAPIUser3;
	private APIForumsHandler forumsAPIUser1;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);

		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while(testUser1.getDisplayName().equals(testUser3.getDisplayName()));
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		communityAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communityAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		forumsAPIUser1 = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
	}

	/**
	* visitor_ee_comment_privateCommunity_visitorAdded_forumTopicReply() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow testUser1</B></li>
	*<li><B>Step: testUser1 creates a topic in a forum in the restricted community.</B></li>
	*<li><B>Step: testUser1 creates a reply to the topic.</B></li>
	*<li><B>Step: testUser3 log into Homepage / Discover / All</B></li>
	*<li><B>Step: testUser3 goes to the story of the forum topic reply</B></li>
	*<li><B>Step: testUser3 clicks into the comment box- Verification Step 1</B></li>
	*<li><B>Verify: Verification Step 1: Warning message "Comments might be seen by people external to your organization." appears when the user clicks to add a comment</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BDB759AD3E6F593085257C86004E84B9">TTT - VISITORS - EE - 00021 - PRIVATE COMMUNITY.FORUM.TOPIC. REPLY - COMMENT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_ee_comment_privateCommunity_visitorAdded_forumTopicReply() {

		String testName = ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " creates a private community");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community restrictedCommunity = communityAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add " + testUser2.getDisplayName() + " to the community as a member");
		communityAPIUser1.addMemberToCommunity(testUser2, restrictedCommunity, StringConstants.Role.MEMBER);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add " + testUser3.getDisplayName() + " to the community as a member");
		communityAPIUser1.addMemberToCommunity(testUser3, restrictedCommunity, StringConstants.Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " will now follow the restricted community");
		communityAPIUser3.followCommunity(restrictedCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " creates a topic in a forum in the restricted community");
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = communityAPIUser1.CreateForumTopic(restrictedCommunity, baseForumTopic);
		
		log.info("INFO: " + testUser1.getDisplayName() + " creates a reply to the topic");
		String replyToForumTopic = Data.getData().buttonOK + Helper.genStrongRand();
		forumsAPIUser1.createForumReply(forumTopic, replyToForumTopic);
		
		log.info("INFO: " + testUser3.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);
		
		log.info("INFO: " + testUser3.getDisplayName() + " go to Homepage / Updates / I'm Following / All");
		ui.gotoImFollowing();
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		// Assign the news story to be clicked in order to open the EE
		String newsStory = ui.replaceNewsStory(Data.CREATE_THEIR_OWN_REPLY, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		log.info("INFO: " + testUser3.getDisplayName() + " opens the EE of the story");
		ui.filterNewsItemOpenEE(newsStory);
		
		// Verify that all forum topic details have loaded correctly in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{baseForumTopic.getTitle(), baseForumTopic.getDescription().trim(), replyToForumTopic}, null, true);
		
		// Verify that all Shared Externally components have loaded correctly in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_Icon_EE, HomepageUIConstants.SharedExternally_Message_EE}, null, true);
		
		log.info("INFO: " + testUser3.getDisplayName() + " clicks into the comment box");
		ui.moveToClick(HomepageUIConstants.EERepliesTab, HomepageUIConstants.EERepliesTab);
		
		/**
		 * At this point, Selenium will find 2 frames in the Replies tab. Only one of those frames will have an attribute
		 * of isDisplayed() == true. This is the frame that we wish to switch to in order to access the comments input box.
		 */
		List<Element> listOfFrames = driver.getElements(HomepageUIConstants.StatusUpdateFrame);
		for(Element frame : listOfFrames) {
			if(frame.isDisplayed()) {
				driver.switchToFrame().selectFrameByElement(frame);
				break;
			}
		}
		ui.fluentWaitElementVisible(HomepageUIConstants.StatusUpdateTextField);
		ui.moveToClick(HomepageUIConstants.StatusUpdateTextField, HomepageUIConstants.StatusUpdateTextField);
		driver.switchToFrame().returnToTopFrame();
		driver.switchToFrame().selectSingleFrameBySelector(HomepageUIConstants.GenericEEFrame);
		
		/**
		 * At this point, Selenium will find 2 warning icons in the Replies tab. Only one of those icons will have an attribute
		 * of isDisplayed() == true as it should have appeared on-screen having clicked into the "Reply to this topic" input
		 * box in the previous step.
		 * 
		 * Verification now follows to assert that only one icon is visible, as expected.
		 */
		List<Element> listOfWarningIcons = driver.getElements(HomepageUIConstants.SharedExternally_AddComments_WarningIcon_EE);
		int visibleIcons = 0;
		for(Element icon : listOfWarningIcons) {
			if(icon.isDisplayed()) {
				visibleIcons ++;
			}
		}
		
		// Verify that all comment warning message components have displayed correctly in the EE
		HomepageValid.verifyIntValuesAreEqual(visibleIcons, 1);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{Data.VisitorModel_CommentWarningMsg}, null, true);
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
}