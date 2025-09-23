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
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Discover_PrivateCommunityBlog extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private BaseBlogPost baseBlogPost;
	private BaseCommunity baseCommunity;
	private BlogPost blogEntry;
	private CommunitiesUI uiCo;
	private Community restrictedCommunity;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		
		// User 1 will now create a restricted community with Blogs widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	
		// Set all other relevant global test components to null
		blogEntry = null;
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
	 * <li><B>Name:</B> PrivateCommunityBlog()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Open a community with private access that you have owner access to</li> 
	 * <li><B>Step:</B> Add the blogs widget within this community</li> 
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.created is not seen - negative test</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/684776A9927630EE852578760079E745">TTT - DISC - BLOGS - 00010 - BLOG.CREATED - PRIVATE COMMUNITY BLOG (NEG)</a></li>
	 * @author Naomi Pakenham
	 */	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void PrivateCommunityBlog() {

		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBlogEvent = CommunityBlogNewsStories.getCreateBlogNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create blog event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateCommunityBlogEntry()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Open a community with private access that you have owner access to and has the blogs widget deployed</li> 
	 * <li><B>Step:</B> Create a new blog entry </li> 
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.entry.created is not seen - negative test</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8A575E9C0E12942A852578760079E749">TTT - DISC - BLOGS - 00020 - BLOG.ENTRY.CREATED - PRIVATE COMMUNITY BLOG (NEG)</a></li>
	 * @author Naomi Pakenham 
	 */	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void PrivateCommunityBlogEntry() {

		ui.startTest();
		
		// User 1 will now add a blog entry to the community blog
		createEntryIfRequired();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateCommunityBlogEntryEdit()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Open a private community that has an existing blog entry</li> 
	 * <li><B>Step:</B> Update an exiting blog entry</li> 
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.entry.updated is not seen - negative test</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E9265B685FA49FEC852578760079E74D">TTT - DISC - BLOGS - 00030 - BLOG.ENTRY.UPDATED - PRIVATE COMMUNITY BLOG (NEG)</a></li>
	 * @author Naomi Pakenham
	 */	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void PrivateCommunityBlogEntryEdit() {

		ui.startTest();
		
		// User 1 will now add a blog entry to the community blog
		createEntryIfRequired();
				
		// User 1 will now edit the description of the entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		blogEntry = CommunityBlogEvents.editDescription(blogEntry, editedDescription, testUser1, communityBlogsAPIUser1);
				
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
				
		// Create the news story to be verified
		String updateEntryEvent = CommunityBlogNewsStories.getUpdateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the update entry event, original description and updated description are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, baseBlogPost.getContent(), editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateCommunityBlogEntryLike()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Open a private community that has an existing blog entry</li> 
	 * <li><B>Step:</B> Recommend this entry</li> 
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.entry.recommended is not seen - negative test</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/54B9C932EBC77C94852578760079E75A">TTT - DISC - BLOGS - 00060 - BLOG.ENTRY.RECOMMENDED - PRIVATE COMMUNITY BLOG (NEG)</a></li>
	 * @author Naomi Pakenham 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 4)
	public void PrivateCommunityBlogEntryLike() {

		ui.startTest();
		
		// User 1 will now add a blog entry to the community blog
		createEntryIfRequired();
				
		// User 1 will now like / recommend the entry
		CommunityBlogEvents.likeOrVote(blogEntry, testUser1, communityBlogsAPIUser1);
						
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
						
		// Create the news story to be verified
		String likeEntryEvent = CommunityBlogNewsStories.getLikeTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
						
		// Verify that the like entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeEntryEvent, blogEntry.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateCommunityTrackback()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Create a blog if you do not already have one</li> 
	 * <li><B>Step:</B> Open a private community that has an existing blog entry</li> 
	 * <li><B>Step:</B> Create a comment on a blog entry, select the option "Add this as a new entry in my blog..." and save</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.trackback.created is not seen - negative test</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8CB14B3F6C251C1B852578760079E756">TTT - DISC - BLOGS - 00050 - BLOG.TRACKBACK.CREATED - PRIVATE COMMUNITY BLOG (NEG)</a></li>
	 * @author Naomi Pakenham 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 5)
	public void PrivateCommunityTrackback() {

		ui.startTest();
		
		// User 1 will now add a blog entry to the community blog
		createEntryIfRequired();
		
		// User 1 will now log in and post a trackback on the community blog
		String trackbackComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityBlogEvents.loginAndAddCommunityTrackbackComment(ui, driver, testUser1, baseCommunity, communitiesAPIUser1, restrictedCommunity, uiCo, baseBlogPost, trackbackComment, false);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
								
		// Create the news story to be verified
		String createTrackbackEvent = CommunityBlogNewsStories.getLeftATrackbackOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
								
		// Verify that the create trackback event and comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTrackbackEvent, blogEntry.getContent().trim(), trackbackComment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	private void createEntryIfRequired() {
		if(blogEntry == null) {
			// User 1 will now add a blog entry to the community blog
			baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(getClass().getSimpleName() + Helper.genStrongRand());
			blogEntry = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity);
		}
	}
}