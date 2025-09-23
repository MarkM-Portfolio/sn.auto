package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.eecomment;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
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

public class FVT_VisitorModel_EE_Comment_IdeationBlogIdea extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_EE_Comment_IdeationBlogIdea.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;
	private APICommunitiesHandler communityAPIUser1, communityAPIUser3;
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
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
	}

	/**
	* visitor_ee_comment_privateCommunity_visitorAdded_ideationBlogIdea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3</B></li>
	*<li><B>Step: testUser3 follow the restricted community created in Step 1</B></li>
	*<li><B>Step: testUser1 adds ideation blogs to the community</B></li>
	*<li><B>Step: testUser1 creates an idea</B></li>
	*<li><B>Step: testUser3 log into Homepage / I'm Following / All</B></li>
	*<li><B>Step: testUser3 goes to the story of the idea created</B></li>
	*<li><B>Step: testUser3 clicks into the comment box- Verification Step 1</B></li>
	*<li><B>Verify: Verification Step 1: Warning message "Comments might be seen by people external to your organization." appears when the user clicks to add a comment</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/365F32CFB0472F8785257C890032CFD3">TTT - VISITORS - EE - 00028 - PRIVATE COMMUNITY.IDEABLOG.IDEA.CREATED- COMMENT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_ee_comment_privateCommunity_visitorAdded_ideationBlogIdea() {

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
		
		log.info("INFO: Add the Ideation Blogs widget using the API");
		communityAPIUser1.addWidget(restrictedCommunity, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " creates an idea");
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		communityAPIUser1.createIdea(baseBlogPost, restrictedCommunity);
		
		log.info("INFO: " + testUser3.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);
		
		log.info("INFO: " + testUser3.getDisplayName() + " go to Homepage / Updates / I'm Following / All");
		ui.gotoImFollowing();
		
		// Assign the news story to be clicked in order to open the EE
		String newsStory = ui.replaceNewsStory(Data.CREATE_IDEATION_BLOG_IDEA, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		log.info("INFO: " + testUser3.getDisplayName() + " opens the EE of the story");
		ui.filterNewsItemOpenEE(newsStory);
		
		// Verify that all idea details have loaded correctly in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{baseBlogPost.getTitle(), baseBlogPost.getContent().trim()}, null, true);
		
		// Verify that all Shared Externally components have loaded correctly in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_Icon_EE, HomepageUIConstants.SharedExternally_Message_EE}, null, true);
		
		log.info("INFO: " + testUser3.getDisplayName() + " clicks into the comment box");
		ui.clickLinkWait(HomepageUIConstants.EECommentsTab);
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUIConstants.StatusUpdateFrame));
		ui.fluentWaitElementVisible(HomepageUIConstants.StatusUpdateTextField);
		driver.getSingleElement(HomepageUIConstants.StatusUpdateTextField).click();
		driver.switchToFrame().returnToTopFrame();
		driver.switchToFrame().selectSingleFrameBySelector(HomepageUIConstants.GenericEEFrame);
		
		// Verify that all comment warning message components have displayed correctly in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_AddComments_WarningIcon_EE}, null, true);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{Data.VisitorModel_CommentWarningMsg}, null, true);
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}	
}