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

public class FVT_Discover_PublicComIdeaBlog extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser3;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser3;
	private BaseBlogPost baseBlogPost;
	private BaseCommunity baseCommunity;
	private BlogPost blogIdea;
	private CommunitiesUI uiCo;
	private Community publicCommunity;
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
		
		// User 1 will now create a public community and will add the Ideation Blogs widget and add User 3 as a member (so as they can log in to post the trackback comment)
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, testUser3, isOnPremise, testUser1, communitiesAPIUser1);
		
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
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* PublicComIdeaBlog() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 open a community with public access that you have owner access to</B></li>
	*<li><b>Step: User 1 add the ideation blogs widget within this community</b></li>
	*<li><B>Step: Log in to Home as User 2</B></li>
	*<li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: User 2 filter by Blogs</B></li>
	*<li><B>Verify: Verify that the news story for ideablog.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/59F26C12D6B909AA852578760079EA9E">TTT - DISC - IDEABLOGS - 00010 - IDEABLOG.CREATED - PUBLIC COMMUNITY IDEABLOG</a></li>
	* @author Naomi Pakenham
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void PublicComIdeaBlog() {
		
		ui.startTest();
		
		// Log in as User 2
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityBlogNewsStories.getCreateIdeationBlogNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Perform all verifications
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* PublicComIdea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 open a community with public access that you have owner access to</B></li>
	*<li><b>Step: User 1 add the ideation blogs widget within this community</b></li>
	*<li><b>Step: User 1 add an idea</b></li>
	*<li><B>Step: Log in to Home as User 2</B></li>
	*<li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: User 2 filter by Blogs</B></li>
	*<li><B>Verify: Verify that the news story for ideablog.idea.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/95C37FE492EE91B5852578760079EAA3">TTT - DISC - IDEABLOGS - 00020 - IDEABLOG.IDEA.CREATED - PUBLIC COMMUNITY IDEABLOG</a></li>	
	* @author Naomi Pakenham
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void PublicComIdea() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
		
		// Log in as User 2
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Perform all verifications
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* PublicComIdeaEdit() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 open a community with public access that you have owner access to</B></li>
	*<li><b>Step: User 1 add the ideation blogs widget within this community</b></li>
	*<li><b>Step: User 1 update an existing idea in the widget</b></li>
	*<li><B>Step: Log in to Home as User 2</B></li>
	*<li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: User 2 filter by Blogs</B></li>
	*<li><B>Verify: Verify that the news story for ideablog.idea.updated is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6A7AA36066AE5AD0852578760079EAA5">TTT - DISC - IDEABLOGS - 00030 - IDEABLOG.IDEA.UPDATED - PUBLIC COMMUNITY IDEABLOG</a></li>	
	* @author Naomi Pakenham
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void PublicComIdeaEdit() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
				
		// User 1 will now edit the description of the idea
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		blogIdea = CommunityBlogEvents.editDescription(blogIdea, editedDescription, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 2
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityBlogNewsStories.getUpdateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Perform all verifications
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* PublicComIdeaVote() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 open a community with public access that you have owner access to</B></li>
	*<li><b>Step: User 1 add the ideation blogs widget within this community</b></li>
	*<li><b>Step: User 1 add an idea</b></li>
	*<li><b>Step: User 3 select the vote button beside an existing idea in the widget</b></li>
	*<li><B>Step: User 2 log in to Homepage</B></li>
	*<li><B>Step: User 2 go to Home \ Activity Stream \ Discover \ All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the news story for ideablog.idea.voted is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/830D4A1F96B0439A852578760079EAAE">TTT - DISC - IDEABLOGS - 00060 - IDEABLOG.IDEA.VOTED - PUBLIC COMMUNITY IDEABLOG</a></li>	
	* @author Naomi Pakenham
	*/	 
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 4)
	public void PublicComIdeaVote() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
				
		// User 3 will now vote for the idea
		CommunityBlogEvents.likeOrVote(blogIdea, testUser3, communityBlogsAPIUser3);
		
		// Log in as User 2
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityBlogNewsStories.getVotedForTheIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());
		
		// Perform all verifications
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, blogIdea.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* PublicComTrackbackIdea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 open a community with public access that you have owner access to</B></li>
	*<li><b>Step: User 1 add the ideation blogs widget within this community</b></li>
	*<li><b>Step: User 1 add an idea</b></li>
	*<li><b>Step: As another user, create a comment and enable the checkbox for "add to by blog" before saving the comment</b></li>
	*<li><B>Step: Log in to Home as User 2</B></li>
	*<li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: User 2 filter by Blogs</B></li>
	*<li><B>Verify: Verify that the news story for ideablog.trackback.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7F0BDAF4A7EF7C5C852578760079EAAB">TTT - DISC - IDEABLOGS - 00050 - IDEABLOG.TRACKBACK.CREATED - PUBLIC COMMUNITY IDEABLOG</a></li>	
	* @author Naomi Pakenham
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 5)
	public void PublicComTrackbackIdea() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
		
		// User 3 will now log in and post a trackback comment on the ideation blog idea
		String trackbackComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityBlogEvents.loginAndAddIdeationBlogTrackbackComment(ui, driver, testUser3, baseCommunity, communitiesAPIUser3, publicCommunity, uiCo, baseBlogPost, trackbackComment, false);
		
		// Log in as User 2
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the news story to be verified
		String newsStory = CommunityBlogNewsStories.getLeftATrackbackOnTheIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());
		
		// Perform all verifications
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, blogIdea.getContent().trim(), trackbackComment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* PublicComIdeaGraduate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 open a community with public access that you have owner access to</B></li>
	*<li><b>Step: User 1 add the ideation blogs widget within this community</b></li>
	*<li><b>Step: User 1 add an idea</b></li>
	*<li><b>Step: User 1 graduate the idea</b></li>
	*<li><B>Step: Log in to Home as User 2</B></li>
	*<li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: User 2 filter by Blogs</B></li>
	*<li><B>Verify: Verify that the news story for ideablog.idea.voted is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CB8B3C3B3839BA51852578760079EAB9">TTT - DISC - IDEABLOGS - 00080 - IDEABLOG.IDEA.GRADUATED - PUBLIC COMMUNITY IDEABLOG</a></li>	
	* @author Naomi Pakenham
	*/	 
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 6)
	public void PublicComIdeaGraduate() {

		ui.startTest();
		
		// User 1 will now add an idea to the community ideation blog
		createIdeaIfRequired();
		
		// User 1 will now log in and graduate the idea
		CommunityBlogEvents.loginAndGraduateIdea(ui, uiCo, publicCommunity, baseCommunity, baseBlogPost, testUser1, communitiesAPIUser1, false);
		
		// Log in as User 2
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the news story to be verified
		String newsStory = CommunityBlogNewsStories.getGraduatedTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Perform all verifications
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, blogIdea.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	private void createIdeaIfRequired() {
		if(blogIdea == null) {
			// User 1 will now add an idea to the community ideation blog
			baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(getClass().getSimpleName() + Helper.genStrongRand());
			blogIdea = CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		}
	}
}