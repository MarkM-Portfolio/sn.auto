package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.blogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
import com.ibm.conn.auto.webui.CommunitiesUI;
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
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / Tags) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 04/2015
 */

public class FVT_ImFollowing_Tags_PublicCommunity_Blogs extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private APICommunitiesHandler communitiesAPIUser2;
	private APICommunityBlogsHandler communityBlogsAPIUser2;
	private BaseCommunity baseCommunity, baseCommunityForEditWithTag, baseCommunityWithTag;
	private CommunitiesUI uiCo;
	private Community publicCommunity, publicCommunityWithTag, publicCommunityForEditWithTag;
	private String tagToFollow;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);	
		
		communityBlogsAPIUser2 = initialiseAPICommunityBlogsHandlerUser(testUser2);
		
		// User 1 will now log in and follow a tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 will now create a public community with the Blogs widget added and which also includes the tag that User 1 is following
		baseCommunityWithTag = CommunityBaseBuilder.buildBaseCommunityWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC, tagToFollow);
		publicCommunityWithTag = CommunityEvents.createNewCommunityAndAddWidget(baseCommunityWithTag, BaseWidget.BLOG, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a second public community with the Blogs widget added and which also includes the tag that User 1 is following
		baseCommunityForEditWithTag = CommunityBaseBuilder.buildBaseCommunityWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC, tagToFollow);
		publicCommunityForEditWithTag = CommunityEvents.createNewCommunityAndAddWidget(baseCommunityForEditWithTag, BaseWidget.BLOG, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a public community with the Blogs widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.BLOG, isOnPremise, testUser2, communitiesAPIUser2);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the communities created during the test
		communitiesAPIUser2.deleteCommunity(publicCommunityWithTag);
		communitiesAPIUser2.deleteCommunity(publicCommunityForEditWithTag);
		communitiesAPIUser2.deleteCommunity(publicCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_AddBlogWidget_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are the owner of with public access and add tag to it</B></li>
	*<li><B>Step: testUser 2 create a new blog by adding blog widget in customize menu</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Blogs & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the blog.created story is displayed in Homepage / All Updates filtered by Communities, Tags and Blogs</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CA432C96F08F08FD852578FC004DD6CD">TTT - AS - FOLLOW - TAG - BLOGS - 00052 - blog.created - PUBLIC COMMUNITY BLOG</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_AddBlogWidget_PublicCommunity(){

		ui.startTest();
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
			
		// Create the news story to be verified
		String createBlogEvent = CommunityBlogNewsStories.getCreateBlogNewsStory(ui, baseCommunityWithTag.getName(), testUser2.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_AddBlogEntry_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are the owner of with public access and add tag to it</B></li>
	*<li><B>Step: testUser 2 add an entry to the blog with the tag User 1 is following that is in the community</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Blogs & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: verify that the blog.entry.created story is displayed in Homepage / All Updates filtered by Communities, Tags and Blogs</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A6A9EDA4FE3C6AB9852578FC004E223C">TTT - AS - FOLLOW - TAG - BLOGS - 00063 - blog.entry.created - PUBLIC COMMUNITY BLOG</a></li>
	*</ul>
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_AddBlogEntry_publicCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create a blog entry in the community blog which includes the tag being followed by User 1
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createBlogPost(testUser2, communityBlogsAPIUser2, baseBlogPost, publicCommunity);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_Update_BlogEntry_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are the owner of with public access</B></li>
	*<li><B>Step: testUser 2 update an entry with a tag that User 1 is following within the blog in the community</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Blogs & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: verify that the blog.entry.updated story is displayed in Homepage / All Updates filtered by Communities, Tags and Blogs</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/964B0C12DD9E9F4E852578FC004E2720">TTT - AS - FOLLOW - TAG - BLOGS - 00072 - blog.entry.updated - PUBLIC COMMUNITY BLOG</a></li>
	*</ul>
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_Update_BlogEntry_publicCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create a blog entry in the community blog which includes the tag being followed by User 1 and will then update the description of that entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createBlogPostAndEditDescription(publicCommunity, baseBlogPost, editedDescription, testUser2, communityBlogsAPIUser2);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
				
		// Create the news story to be verified
		String updateEntryEvent  = CommunityBlogNewsStories.getUpdateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the update blog entry event and edited description appear in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEntryEvent, editedDescription}, filter, true);
			
			// Verify that the original description is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{baseBlogPost.getContent()}, null, false);
		}
		ui.endTest();
	}			
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_BlogSettingUpdate_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are the owner of with public access which has the tag User1 is following</B></li>
	*<li><B>Step: testUser 2 add the Blog widget to the community <tag>
	*<li><B>Step: testUser 2 change the blog settings (description)</B></li>
	*<li><B>Step: testUser 2 go to Homepage / All Updates / Communities</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Blogs & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: verify that the blog.settings.updated story is displayed in Homepage / All Updates filtered by Communities, Tags and Blogs</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C05138548654AB31852579BB00724E79">TTT - AS - FOLLOW - TAG - BLOGS - 00068 - blog.settings.updated- PUBLIC COMMUNITY BLOG</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Tags_BlogSettingUpdate_PublicCommunity(){

		ui.startTest();
		
		// Log in as User 2 and update the community blog name to match the content of the tag that User 1 is following
		CommunityBlogEvents.loginAndEditBlogName(publicCommunityForEditWithTag, baseCommunityForEditWithTag, ui, driver, uiCo, testUser2, communitiesAPIUser2, tagToFollow, false);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
		
		// Create the news story to be verified
		String editBlogEvent = CommunityBlogNewsStories.getUpdateBlogNewsStory(ui, tagToFollow, testUser2.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{editBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}