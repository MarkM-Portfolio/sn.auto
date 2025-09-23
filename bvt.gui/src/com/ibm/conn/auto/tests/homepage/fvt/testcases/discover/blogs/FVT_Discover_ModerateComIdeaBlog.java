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

public class FVT_Discover_ModerateComIdeaBlog extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser3;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser3;
	private BaseBlogPost baseBlogPost;
	private BaseCommunity baseCommunity;
	private BlogPost blogIdea;
	private CommunitiesUI uiCo;
	private Community moderatedCommunity;
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
		
		// User 1 will now create a moderated community, add the Ideation Blogs widget and will also add User 3 as a member of the community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, testUser3, isOnPremise, testUser1, communitiesAPIUser1);
		
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
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateComIdeaBlog()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with moderated access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.created is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C8CFCEB69A798380852578760079EAA0">TTT - DISC - IDEABLOGS - 00010 - IDEABLOG.CREATED - MODERATED COMMUNITY IDEABLOG</a></li>	
	 * @author Naomi Pakenham
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void ModerateComIdeaBlog() {

		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createIdeationBlogEvent = CommunityBlogNewsStories.getCreateIdeationBlogNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create ideation blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeationBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateComIdea()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with moderated access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Add an idea in the widget</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.created is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/61D0753E7AD5BEB9852578760079EAA2">TTT - DISC - IDEABLOGS - 00020 - IDEABLOG.IDEA.CREATED - MODERATED COMMUNITY IDEABLOG</a></li>	
	 * @author Naomi Pakenham
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void ModerateComIdea() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
				
		// Create the news story to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the create idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateComIdeaEdit()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with moderated access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Update an existing idea in the widget</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.idea.updated is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/45F1549139940A17852578760079EAA6">TTT - DISC - IDEABLOGS - 00030 - IDEABLOG.IDEA.UPDATED - MODERATED COMMUNITY IDEABLOG</a></li>	
	 * @author Naomi Pakenham
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void ModerateComIdeaEdit() {

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
						
		// Verify that the update idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateIdeaEvent, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateComIdeaVote()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with moderated access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Add an idea</li>
	 * <li><B>Step:</B> As another user select the vote button beside an existing idea in the widget</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.idea.voted is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F00C1007C89A59D3852578760079EAAD">TTT - DISC - IDEABLOGS - 00060 - IDEABLOG.IDEA.VOTED - MODERATED COMMUNITY IDEABLOG</a></li>	
	 * @author Naomi Pakenham
	 */	 
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 4)
	public void ModerateComIdeaVote() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
				
		// User 3 will now vote for the idea
		CommunityBlogEvents.likeOrVote(blogIdea, testUser3, communityBlogsAPIUser3);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
								
		// Create the news story to be verified
		String voteForIdeaEvent = CommunityBlogNewsStories.getVotedForTheIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());
								
		// Verify that the vote for idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{voteForIdeaEvent, blogIdea.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateComTrackbackIdea()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with moderated access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Add an idea in the widget</li>
	 * <li><B>Step:</B> As another user, create a comment and enable the checkbox for "add to by blog" before saving the comment</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.idea.graduated is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/63FBC8BC6B863EE6852578760079EAAC">TTT - DISC - IDEABLOGS - 00050 - IDEABLOG.TRACKBACK.CREATED - MODERATED COMMUNITY IDEABLOG</a></li>	
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 5)
	public void ModerateComTrackbackIdea() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
		
		// User 3 will now log in and leave a trackback on the idea
		String trackbackComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityBlogEvents.loginAndAddIdeationBlogTrackbackComment(ui, driver, testUser3, baseCommunity, communitiesAPIUser3, moderatedCommunity, uiCo, baseBlogPost, trackbackComment, false);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
												
		// Create the news story to be verified
		String trackbackEvent = CommunityBlogNewsStories.getLeftATrackbackOnTheIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());
												
		// Verify that the graduate idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{trackbackEvent, blogIdea.getContent().trim(), trackbackComment}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateComIdeaGraduate()</li>
	 * <li><B>Step:</B> Log in to Communities</li>
	 * <li><B>Step:</B> Open a community with moderated access that you have owner access to</li>
	 * <li><B>Step:</B> Add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> Add an idea</li>
	 * <li><B>Step:</B> Graduate the idea</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.idea.graduated is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7354BF2865F0B51D852578760079EABA">TTT - DISC - IDEABLOGS - 00080 - IDEABLOG.IDEA.GRADUATED - MODERATED COMMUNITY IDEABLOG</a></li>	
	 * @author Naomi Pakenham
	 */	 
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 6)
	public void ModerateComIdeaGraduate() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
		
		// User 1 will now log in and graduate the idea
		CommunityBlogEvents.loginAndGraduateIdea(ui, uiCo, moderatedCommunity, baseCommunity, baseBlogPost, testUser1, communitiesAPIUser1, false);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
										
		// Create the news story to be verified
		String graduateIdeaEvent = CommunityBlogNewsStories.getGraduatedTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
										
		// Verify that the graduate idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{graduateIdeaEvent, blogIdea.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	private void createIdeaIfRequired() {
		if(blogIdea == null) {
			// User 1 will now add an idea to the community ideation blog
			baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(getClass().getSimpleName() + Helper.genStrongRand());
			blogIdea = CommunityBlogEvents.createIdea(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		}
	}
}