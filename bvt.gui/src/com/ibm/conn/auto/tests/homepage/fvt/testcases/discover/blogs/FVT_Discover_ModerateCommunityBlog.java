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

public class FVT_Discover_ModerateCommunityBlog extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private BaseBlogPost baseBlogPost;
	private BaseCommunity baseCommunity;
	private BlogPost blogEntry;
	private CommunitiesUI uiCo;
	private Community moderatedCommunity;
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
		
		// User 1 will now create a moderated community to be used by all tests
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
		
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
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateCommunityBlog()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Open a community with moderated access that you have owner access to</li> 
	 * <li><B>Step:</B> Add the blogs widget within this community</li> 
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.created is seen</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B542F90EA94083C7852578760079E744">TTT - DISC - BLOGS - 00010 - BLOG.CREATED - MODERATED COMMUNITY BLOG</a></li>
	 * @author Naomi Pakenham
	 */	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void ModerateCommunityBlog() {

		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBlogEvent = CommunityBlogNewsStories.getCreateBlogNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateCommunityBlogEntry()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Open a community with moderated access that you have owner access to and has the blogs widget deployed</li> 
	 * <li><B>Step:</B> Create a new blog entry </li> 
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.entry.created is seen</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5DA81FBB47A8BB78852578760079E748">TTT - DISC - BLOGS - 00020 - BLOG.ENTRY.CREATED - MODERATED COMMUNITY BLOG</a></li>
	 * @author Naomi Pakenham 
	 */	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void ModerateCommunityBlogEntry() {

		ui.startTest();
		
		// User 1 will now add a blog entry to the community blog
		createEntryIfRequired();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateCommunityBlogEntryEdit()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Open a moderated community that has an existing blog entry</li> 
	 * <li><B>Step:</B> Update an exiting blog entry</li> 
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.entry.updated is seen</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4B50A7D161A1F639852578760079E74C">TTT - DISC - BLOGS - 00030 - BLOG.ENTRY.UPDATED - MODERATED COMMUNITY BLOG</a></li>
	 * @author Naomi Pakenham
	 */	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void ModerateCommunityBlogEntryEdit() {

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
				
		// Verify that the update entry event and updated description is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateCommunityBlogEntryLike()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Open a moderated community that has an existing blog entry</li> 
	 * <li><B>Step:</B> Recommend this entry</li> 
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.entry.recommended is seen</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2D797EB395BF4708852578760079E759">TTT - DISC - BLOGS - 00060 - BLOG.ENTRY.RECOMMENDED - MODERATED COMMUNITY BLOG</a></li>
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 4)
	public void ModerateCommunityBlogEntryLike() {

		ui.startTest();
		
		// User 1 will now add a blog entry to the community blog
		createEntryIfRequired();
				
		// User 1 will now like / recommend the entry
		CommunityBlogEvents.likeOrVote(blogEntry, testUser1, communityBlogsAPIUser1);
						
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
						
		// Create the news story to be verified
		String likeEntryEvent = CommunityBlogNewsStories.getLikeTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
						
		// Verify that the like entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeEntryEvent, blogEntry.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> ModerateCommunityTrackback()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Create a blog if you do not already have one</li> 
	 * <li><B>Step:</B> Open a moderated community that has an existing blog entry</li> 
	 * <li><B>Step:</B> Create a comment on a blog entry, select the option "Add this as a new entry in my blog..." and save</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.trackback.created is seen</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/07E6017C48FD8844852578760079E755">TTT - DISC - BLOGS - 00050 - BLOG.TRACKBACK.CREATED - MODERATED COMMUNITY BLOG</a></li>
	 * @author Naomi Pakenham 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 5)
	public void ModerateCommunityTrackback() {

		ui.startTest();
		
		// User 1 will now add a blog entry to the community blog
		createEntryIfRequired();
		
		// User 1 will now log in and post a trackback on the community blog
		String trackbackComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityBlogEvents.loginAndAddCommunityTrackbackComment(ui, driver, testUser1, baseCommunity, communitiesAPIUser1, moderatedCommunity, uiCo, baseBlogPost, trackbackComment, false);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
								
		// Create the news story to be verified
		String createTrackbackEvent = CommunityBlogNewsStories.getLeftATrackbackOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
								
		// Verify that the create trackback event and comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTrackbackEvent, blogEntry.getContent().trim(), trackbackComment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	private void createEntryIfRequired() {
		if(blogEntry == null) {
			// User 1 will now add a blog entry to the community blog
			baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(getClass().getSimpleName() + Helper.genStrongRand());
			blogEntry = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, moderatedCommunity);
		}
	}
}