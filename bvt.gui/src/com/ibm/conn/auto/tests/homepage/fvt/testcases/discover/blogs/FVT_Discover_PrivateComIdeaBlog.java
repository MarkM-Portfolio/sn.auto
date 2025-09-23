package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.blogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Discover_PrivateComIdeaBlog extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser3;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser3;
	private BaseBlogPost baseBlogPost;
	private BaseCommunity baseCommunity;
	private BlogPost blogIdea;
	private CommunitiesUI uiCo;
	private Community restrictedCommunity;
	private User testUser1, testUser2, testUser3;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser3 = initialiseAPICommunitiesHandlerUser(testUser3);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		communityBlogsAPIUser3 = initialiseAPICommunityBlogsHandlerUser(testUser3);
		
		// User 1 will now create a restricted community, add the Ideation Blogs widget and will also add User 3 as a member of the community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, testUser3, isOnPremise, testUser1, communitiesAPIUser1);
	
		// Set all other relevant global test components to null
		blogIdea = null;
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateComIdeaBlog()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with private access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.created is not seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DCD5A8046574E79C852578760079EA9F">TTT - DISC - IDEABLOGS - 00010 - IDEABLOG.CREATED - PRIVATE COMMUNITY IDEABLOG (NEG)</a></li>	
	 * @author Naomi Pakenham
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void PrivateComIdeaBlog() {

		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createIdeationBlogEvent = CommunityBlogNewsStories.getCreateIdeationBlogNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create ideation blog event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeationBlogEvent}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateComIdea()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with private access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Add an idea in the widget</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.idea.created is not seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/97064B7413B431A6852578760079EAA1">TTT - DISC - IDEABLOGS - 00020 - IDEABLOG.IDEA.CREATED - PRIVATE COMMUNITY IDEABLOG (NEG)</a></li>	
	 * @author Naomi Pakenham
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void PrivateComIdea() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
				
		// Create the news story to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the create idea event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateComIdeaEdit()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with private access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Update an existing idea in the widget</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.idea.updated is not seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DE039A8B53BC9E15852578760079EAA4">TTT - DISC - IDEABLOGS - 00030 - IDEABLOG.IDEA.UPDATED - PRIVATE COMMUNITY IDEABLOG (NEG)</a></li>	
	 * @author Naomi Pakenham
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void PrivateComIdeaEdit() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
				
		// User 1 will now edit the description of the idea
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		blogIdea = CommunityBlogEvents.editDescription(blogIdea, editedDescription, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
						
		// Create the news story to be verified
		String updateIdeaEvent = CommunityBlogNewsStories.getUpdateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
						
		// Verify that the update idea event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateIdeaEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateComIdeaVote()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with private access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Add an idea</li>
	 * <li><B>Step:</B> As another user select the vote button beside an existing idea in the widget</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.idea.voted is not seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EC920126DC4C093A852578760079EAAF">TTT - DISC - IDEABLOGS - 00060 - IDEABLOG.IDEA.VOTED - PRIVATE COMMUNITY IDEABLOG (NEG)</a></li>	
	 * @author Naomi Pakenham
	 */	 
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 4)
	public void PrivateComIdeaVote() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
				
		// User 3 will now vote for the idea
		CommunityBlogEvents.likeOrVote(blogIdea, testUser3, communityBlogsAPIUser3);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
								
		// Create the news story to be verified
		String voteForIdeaEvent = CommunityBlogNewsStories.getVotedForTheIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());
								
		// Verify that the vote for idea event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{voteForIdeaEvent, blogIdea.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateComTrackbackIdea()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with private access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Add an idea in the widget</li>
	 * <li><B>Step:</B> As another user, create a comment and enable the checkbox for "add to by blog" before saving the comment</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.trackback.created is not seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/98693C9B907E01E2852578760079EAAA">TTT - DISC - IDEABLOGS - 00050 - IDEABLOG.TRACKBACK.CREATED - PRIVATE COMMUNITY IDEABLOG (NEG)</a></li>	
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 5)
	public void PrivateComTrackbackIdea() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
		
		// User 3 will now log in and leave a trackback on the idea
		String trackbackComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityBlogEvents.loginAndAddIdeationBlogTrackbackComment(ui, driver, testUser3, baseCommunity, communitiesAPIUser3, restrictedCommunity, uiCo, baseBlogPost, trackbackComment, false);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
												
		// Create the news story to be verified
		String trackbackEvent = CommunityBlogNewsStories.getLeftATrackbackOnTheIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());
												
		// Verify that the graduate idea event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{trackbackEvent, blogIdea.getContent().trim(), trackbackComment}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateComIdeaGraduate()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with private access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Add an idea</li>
	 * <li><B>Step:</B> Graduate the idea</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.idea.graduated is not seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/312E3A7C638823C6852578760079EAB8">TTT - DISC - IDEABLOGS - 00080 - IDEABLOG.IDEA.GRADUATED - PRIVATE COMMUNITY IDEABLOG</a></li>	
	 * @author Naomi Pakenham
	 */	 
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 6)
	public void PrivateComIdeaGraduate() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
		
		// User 1 will now log in and graduate the idea
		CommunityBlogEvents.loginAndGraduateIdea(ui, uiCo, restrictedCommunity, baseCommunity, baseBlogPost, testUser1, communitiesAPIUser1, false);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
										
		// Create the news story to be verified
		String graduateIdeaEvent = CommunityBlogNewsStories.getGraduatedTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
										
		// Verify that the graduate idea event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{graduateIdeaEvent, blogIdea.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	private void createIdeaIfRequired() {
		if(blogIdea == null) {
			// User 1 will now add an idea to the community ideation blog
			baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(getClass().getSimpleName() + Helper.genStrongRand());
			blogIdea = CommunityBlogEvents.createIdea(restrictedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		}
	}
}