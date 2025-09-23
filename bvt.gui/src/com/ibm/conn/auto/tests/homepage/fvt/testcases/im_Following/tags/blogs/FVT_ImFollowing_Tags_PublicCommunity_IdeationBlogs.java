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

public class FVT_ImFollowing_Tags_PublicCommunity_IdeationBlogs extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private APICommunitiesHandler communitiesAPIUser2;
	private APICommunityBlogsHandler communityBlogsAPIUser2;
	private BaseCommunity baseCommunity, baseCommunityWithTag;
	private CommunitiesUI uiCo;
	private Community publicCommunity, publicCommunityWithTag;
	private String tagToFollow;
	private User testUser1 , testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);		
		
		communityBlogsAPIUser2 = initialiseAPICommunityBlogsHandlerUser(testUser2);
		
		// User 1 will now follow a tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 will now create a public community and will add the Ideation Blog widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a public community which includes the tag User 1 is following and will add the Ideation Blog widget
		baseCommunityWithTag = CommunityBaseBuilder.buildBaseCommunityWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC, tagToFollow);
		publicCommunityWithTag = CommunityEvents.createNewCommunityAndAddWidget(baseCommunityWithTag, BaseWidget.IDEATION_BLOG, isOnPremise, testUser2, communitiesAPIUser2);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser2.deleteCommunity(publicCommunity);
		communitiesAPIUser2.deleteCommunity(publicCommunityWithTag);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_AddIdeationBlogWidget_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a public community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.created story is displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19DB00EA93BD8F51852579AC007F3132">TTT - AS - FOLLOW - TAG - BLOGS - 00121 - ideationblog.created - PUBLIC COMMUNITY IDEATIONBLOG</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_AddIdeationBlogWidget_PublicCommunity(){

		ui.startTest();
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createIdeationBlogEvent = CommunityBlogNewsStories.getCreateIdeationBlogNewsStory(ui, baseCommunityWithTag.getName(), testUser2.getDisplayName());
		
		// Verify that the create ideation blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeationBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_AddIdea_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a public community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All Communities, Blogs & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.created story is displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5F6A4C7FA9F93EB4852579AC008156E4">TTT - AS - FOLLOW - TAG - BLOGS - 00131 - ideationblog.idea.created - PUBLIC COMMUNITY IDEATIONBLOG</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_AddIdea_PublicCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the create idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Name: test_Tags_UpdateIdea_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a public community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 2 edits the idea, updates and saves the idea</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.updated story is displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B602A20E0226D7C5852579AC00815B22">TTT - AS - FOLLOW - TAG - BLOGS - 00141 - ideationblog.idea.updated - PUBLIC COMMUNITY IDEATIONBLOG</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_UpdateIdea_PublicCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following and will edit the description of the idea
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdeaAndEditDescription(publicCommunity, baseBlogPost, editedDescription, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String updateIdeaEvent = CommunityBlogNewsStories.getUpdateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the update idea event and updated description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateIdeaEvent, editedDescription}, filter, true);
			
			// Verify that the original description is NOT displayed in any of the filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{baseBlogPost.getContent()}, null, false);
		}
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Name: test_Tags_VoteIdea_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a public community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 2 votes for the idea</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.voted story is displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B30AA483093B0090852579AD00033605">TTT - AS - FOLLOW - TAG - BLOGS - 00171 - ideationblog.idea.voted - PUBLIC COMMUNITY IDEATIONBLOG</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_VoteIdea_PublicCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following and will then vote for the idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdeaAndVoteForIdea(publicCommunity, baseBlogPost, testUser2, communityBlogsAPIUser2, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		String voteForIdeaEvent = CommunityBlogNewsStories.getVotedForTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create idea event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the vote for idea event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{voteForIdeaEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_GraduateIdea_PublicCommunity</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a public community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 2 selects the Graduate button</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.graduated story is displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B710F821BF022F7A852579AD000446A0">TTT - AS - FOLLOW - TAG - BLOGS - 00191 - ideationblog.idea.graduated - PUBLIC COMMUNITY IDEATIONBLOG</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_GraduateIdea_PublicCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost, testUser2, communityBlogsAPIUser2);
		
		// User 2 will now log in and graduate the idea
		CommunityBlogEvents.loginAndGraduateIdea(ui, uiCo, publicCommunity, baseCommunity, baseBlogPost, testUser2, communitiesAPIUser2, false);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
				
		// Create the news story to be verified
		String graduateIdeaEvent = CommunityBlogNewsStories.getGraduatedTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the graduate idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{graduateIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}			
}