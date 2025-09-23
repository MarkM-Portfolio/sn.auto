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

public class FVT_ImFollowing_Tags_ModeratedCommunity_IdeationBlogs extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private APICommunitiesHandler communitiesAPIUser2;
	private APICommunityBlogsHandler communityBlogsAPIUser2;
	private BaseCommunity baseCommunity, baseCommunityWithTag;
	private CommunitiesUI uiCo;
	private Community moderatedCommunity, moderatedCommunityWithTag;
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
		
		// User 2 will now create a moderated community and will add the Ideation Blog widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a moderated community which includes the tag User 1 is following and will add the Ideation Blog widget
		baseCommunityWithTag = CommunityBaseBuilder.buildBaseCommunityWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED, tagToFollow);
		moderatedCommunityWithTag = CommunityEvents.createNewCommunityAndAddWidget(baseCommunityWithTag, BaseWidget.IDEATION_BLOG, isOnPremise, testUser2, communitiesAPIUser2);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser2.deleteCommunity(moderatedCommunity);
		communitiesAPIUser2.deleteCommunity(moderatedCommunityWithTag);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_AddIdeationBlogWidget_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 log on to a Moderated community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.created story is displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3E2C80A26EE7EC7F852579AC007F331E">TTT - AS - FOLLOW - TAG - BLOGS - 00122 - ideationblog.created - MODERATED COMMUNITY IDEATIONBLOG</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_AddIdeationBlogWidget_ModeratedCommunity(){

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
	*<li><B>Name: test_Tags_AddIdea_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a Moderated community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All Communities, Blogs & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.created story is displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EE9140E5A94AC27B852579AC0081585E">TTT - AS - FOLLOW - TAG - BLOGS - 00132 - ideationblog.idea.created - MODERATED COMMUNITY IDEATIONBLOG</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_AddIdea_ModeratedCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdea(moderatedCommunity, baseBlogPost, testUser2, communityBlogsAPIUser2);
		
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
	*<li><B>Name: test_Tags_UpdateIdea_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a Moderated community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 2 edits the idea, updates and saves the idea</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.updated story is displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8D6FEFBF4B6168D7852579AC00815C61">TTT - AS - FOLLOW - TAG - BLOGS - 00142 - ideationblog.idea.updated - MODERATED COMMUNITY IDEATIONBLOG</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_UpdateIdea_ModeratedCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following and will edit the description of the idea
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdeaAndEditDescription(moderatedCommunity, baseBlogPost, editedDescription, testUser2, communityBlogsAPIUser2);
		
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
	*<li><B>Name: test_Tags_VoteIdea_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a Moderated community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 2 votes for the idea</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.voted story is NOT displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/27E66BE21DA1ADC2852579AD000337E4">TTT - AS - FOLLOW - TAG - BLOGS - 00172 - ideationblog.idea.voted - MODERATED COMMUNITY IDEATIONBLOG</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_VoteIdea_ModeratedCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following and will then vote for the idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdeaAndVoteForIdea(moderatedCommunity, baseBlogPost, testUser2, communityBlogsAPIUser2, testUser2, communityBlogsAPIUser2);
		
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
	*<li><B>Name: test_Tags_GraduateIdea_ModeratedCommunity</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a Moderated community that you are the owner of</B></li>
	*<li><B>Step: testUser 2 customize the Community and add the Ideationblog widget</B></li>
	*<li><B>Step: testUser 2 creates a new idea with the tag that user 1 is following</B></li>
	*<li><B>Step: testUser 2 selects the Graduate button</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the ideationblog.idea.graduated story is displayed in (1) the top level, (2) the Tags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D1A544B570E2097E852579AD00044891">TTT - AS - FOLLOW - TAG - BLOGS - 00191 - ideationblog.idea.graduated - MODERATED COMMUNITY IDEATIONBLOG</a></li>
	*</ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_GraduateIdea_ModeratedCommunity(){

		String testName = ui.startTest();
		
		// User 2 will now create an idea which includes the tag User 1 is following
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityBlogEvents.createIdea(moderatedCommunity, baseBlogPost, testUser2, communityBlogsAPIUser2);
		
		// User 2 will now log in and graduate the idea
		CommunityBlogEvents.loginAndGraduateIdea(ui, uiCo, moderatedCommunity, baseCommunity, baseBlogPost, testUser2, communitiesAPIUser2, false);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
				
		// Create the news story to be verified
		String graduateIdeaEvent = CommunityBlogNewsStories.getGraduatedTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the graduate idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{graduateIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}			
}