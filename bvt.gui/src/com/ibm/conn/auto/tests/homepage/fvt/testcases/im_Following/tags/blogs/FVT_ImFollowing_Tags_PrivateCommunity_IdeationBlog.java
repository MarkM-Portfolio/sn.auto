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

public class FVT_ImFollowing_Tags_PrivateCommunity_IdeationBlog extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private APICommunitiesHandler communitiesAPIUser2;
	private APICommunityBlogsHandler communityBlogsAPIUser2;
	private BaseCommunity baseCommunity, baseCommunityWithTag;
	private CommunitiesUI uiCo;
	private Community restrictedCommunity, restrictedCommunityWithTag;
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
		
		// User 2 will now create a restricted community and will add the Ideation Blog widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a restricted community which includes the tag User 1 is following and will add the Ideation Blog widget
		baseCommunityWithTag = CommunityBaseBuilder.buildBaseCommunityWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED, tagToFollow);
		restrictedCommunityWithTag = CommunityEvents.createNewCommunityAndAddWidget(baseCommunityWithTag, BaseWidget.IDEATION_BLOG, isOnPremise, testUser2, communitiesAPIUser2);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser2.deleteCommunity(restrictedCommunity);
		communitiesAPIUser2.deleteCommunity(restrictedCommunityWithTag);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_AddIdeationBlogWidget_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a private community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.created story is not displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DD39ACB3C3145AE7852579AC007F34E0">TTT - AS - FOLLOW - TAG - BLOGS - 00123 - ideationblog.created - PRIVATE COMMUNITY IDEATIONBLOG (NEG)</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_AddIdeationBlogWidget_PrivateCommunity(){

		ui.startTest();
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createIdeationBlogEvent = CommunityBlogNewsStories.getCreateIdeationBlogNewsStory(ui, baseCommunityWithTag.getName(), testUser2.getDisplayName());
		
		// Verify that the create ideation blog event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeationBlogEvent}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_AddIdea_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a private community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Blogs & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.created story is not displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF608DF543324A36852579AC008159C4">TTT - AS - FOLLOW - TAG - BLOGS - 00133 - ideationblog.idea.created - PRIVATE COMMUNITY IDEATIONBLOG (NEG)</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_AddIdea_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdea(restrictedCommunity, baseBlogPost, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the create idea event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_UpdateIdea_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a private community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 2 edits the idea, updates and saves the idea</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.updated story is not displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9AB01F7E66272280852579AC00815DAB">TTT - AS - FOLLOW - TAG - BLOGS - 00143 - ideationblog.idea.updated - PRIVATE COMMUNITY IDEATIONBLOG (NEG)</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_UpdateIdea_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following and will edit the description of the idea
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdeaAndEditDescription(restrictedCommunity, baseBlogPost, editedDescription, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String updateIdeaEvent = CommunityBlogNewsStories.getUpdateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the update idea event, original description and updated description are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateIdeaEvent, baseBlogPost.getContent(), editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_VoteIdea_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a private community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 2 votes for the idea</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.voted story is not displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3012CE6772DB56F3852579AD00033953">TTT - AS - FOLLOW - TAG - BLOGS - 00173 - ideationblog.idea.voted - PRIVATE COMMUNITY IDEATIONBLOG (NEG)</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_VoteIdea_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following and will then vote for the idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdeaAndVoteForIdea(restrictedCommunity, baseBlogPost, testUser2, communityBlogsAPIUser2, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String voteForIdeaEvent = CommunityBlogNewsStories.getVotedForTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the vote for idea event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{voteForIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, false);
					
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_GraduateIdea_PublicCommunity</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a private community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 2 selects the Graduate button</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.graduated story is not displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A166F3F654D01733852579AD00044A21">TTT - AS - FOLLOW - TAG - BLOGS - 00191 - ideationblog.idea.graduated - PRIVATE COMMUNITY IDEATIONBLOG (NEG)</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_GraduateIdea_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdea(restrictedCommunity, baseBlogPost, testUser2, communityBlogsAPIUser2);
		
		// User 2 will now log in and graduate the idea
		CommunityBlogEvents.loginAndGraduateIdea(ui, uiCo, restrictedCommunity, baseCommunity, baseBlogPost, testUser2, communitiesAPIUser2, false);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
				
		// Create the news story to be verified
		String graduateIdeaEvent = CommunityBlogNewsStories.getGraduatedTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the graduate idea event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{graduateIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, false);
		
		ui.endTest();
	}	

	/**
	*<ul>
	*<li><B>Name: test_Tags_AddIdeaTrackback_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag
	*<li><B>Step: testUser 2 log into Connections<tag></B></li>
	*<li><B>Step: testUser 2 go to a private community that you are the owner of <tag></B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 2 comments on the idea, selecting add this to my blog checkbox and choosing another blog</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.trackback.created story is not displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter </B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8CA4142E76C3F387852579AD000015D7">TTT - AS - FOLLOW - TAG - BLOGS - 00163 - ideationblog.idea.trackback.created - PRIVATE COMMUNITY IDEATIONBLOG (NEG)</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Tags_AddIdeaTrackback_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdea(restrictedCommunity, baseBlogPost, testUser2, communityBlogsAPIUser2);
		
		// User 2 will now log in and leave a trackback on the idea
		String trackbackComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityBlogEvents.loginAndAddIdeationBlogTrackbackComment(ui, driver, testUser2, baseCommunity, communitiesAPIUser2, restrictedCommunity, uiCo, baseBlogPost, trackbackComment, false);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
						
		// Create the news story to be verified
		String createTrackbackEvent = CommunityBlogNewsStories.getLeftATrackbackOnTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the left trackback event and trackback comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTrackbackEvent, baseBlogPost.getContent(), trackbackComment}, TEST_FILTERS, false);
				
		ui.endTest();
	}
}