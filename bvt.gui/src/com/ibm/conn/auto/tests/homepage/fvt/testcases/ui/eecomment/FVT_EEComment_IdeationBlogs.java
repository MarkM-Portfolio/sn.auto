package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.eecomment;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016	                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Legacy Mentions replaced with CKEditor] FVT UI Automation for Story 139553 and Story 139607
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139666
 * @author Patrick Doherty
 */

public class FVT_EEComment_IdeationBlogs extends SetUpMethodsFVT {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		// User 1 will now create a public community and will add the ideation blog widget to that community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community and will add the ideation blog widget to that community
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseRestrictedCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a moderated community and will add the ideation blog widget to that community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}

	/**
	* eeComment_PublicCommunity_Idea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Create a public community</B></li>
	*<li><B>Step: Add the Ideation Blogs widget</B></li>
	*<li><B>Step: Create an idea</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / Blogs</B></li>
	*<li><B>Step: Open the EE for the public community idea news story</B></li>
	*<li><B>Step: Add a comment</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the EE</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void eeComment_PublicCommunity_Idea(){

		String testName = ui.startTest();
				
		// User 1 will now add a new idea to the ideation blog in the community
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the event to be used to open the EE
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), basePublicCommunity.getName(), testUser1.getDisplayName());
		
		// User 1 will now open the EE for the create idea event and will post a comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		boolean commentPostedInEE = UIEvents.addEECommentUsingUI(ui, testUser1, createIdeaEvent, comment);
				
		// Verify that the comment posted correctly in the EE - this includes verifying that the comment was displayed in the EE after posting
		HomepageValid.verifyBooleanValuesAreEqual(commentPostedInEE, true);
		
		// Switch focus back to the main frame again
		UIEvents.switchToTopFrame(ui);
				
		// Create the news story to be verified
		String commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_You(ui, baseBlogPost.getTitle(), basePublicCommunity.getName());
				
		// Verify that the comment event and the comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* eeComment_ModCommunity_Idea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Create a moderated community</B></li>
	*<li><B>Step: Add the Ideation Blogs widget</B></li>
	*<li><B>Step: Create an idea</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / Blogs</B></li>
	*<li><B>Step: Open the EE for the moderated community idea news story</B></li>
	*<li><B>Step: Add a comment</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the EE</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void eeComment_ModCommunity_Idea(){

		String testName = ui.startTest();
		
		// User 1 will now add a new idea to the ideation blog in the community
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the event to be used to open the EE
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		
		// User 1 will now open the EE for the create idea event and will post a comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		boolean commentPostedInEE = UIEvents.addEECommentUsingUI(ui, testUser1, createIdeaEvent, comment);
				
		// Verify that the comment posted correctly in the EE - this includes verifying that the comment was displayed in the EE after posting
		HomepageValid.verifyBooleanValuesAreEqual(commentPostedInEE, true);
		
		// Switch focus back to the main frame again
		UIEvents.switchToTopFrame(ui);
				
		// Create the news story to be verified
		String commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_You(ui, baseBlogPost.getTitle(), baseModeratedCommunity.getName());
				
		// Verify that the comment event and the comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* eeComment_PrivateCommunity_Idea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Create a private community</B></li>
	*<li><B>Step: Add the Ideation Blogs widget</B></li>
	*<li><B>Step: Create an idea</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / Blogs</B></li>
	*<li><B>Step: Open the EE for the private community idea news story</B></li>
	*<li><B>Step: Add a comment</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the EE</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void eeComment_PrivateCommunity_Idea(){

		String testName = ui.startTest();
		
		// User 1 will now add a new idea to the ideation blog in the community
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(restrictedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the event to be used to open the EE
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		
		// User 1 will now open the EE for the create idea event and will post a comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		boolean commentPostedInEE = UIEvents.addEECommentUsingUI(ui, testUser1, createIdeaEvent, comment);
				
		// Verify that the comment posted correctly in the EE - this includes verifying that the comment was displayed in the EE after posting
		HomepageValid.verifyBooleanValuesAreEqual(commentPostedInEE, true);
		
		// Switch focus back to the main frame again
		UIEvents.switchToTopFrame(ui);
				
		// Create the news story to be verified
		String commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_You(ui, baseBlogPost.getTitle(), baseRestrictedCommunity.getName());
				
		// Verify that the comment event and the comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}