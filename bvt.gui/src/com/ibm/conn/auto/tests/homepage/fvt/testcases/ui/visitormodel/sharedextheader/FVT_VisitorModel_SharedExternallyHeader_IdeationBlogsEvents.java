package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_SharedExternallyHeader_IdeationBlogsEvents extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_SharedExternallyHeader_IdeationBlogsEvents.class);

	private HomepageUI ui;
	private CommunitiesUI uiCo;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;
	
	private APICommunitiesHandler apiOwner, apiFollower;
	private APIBlogsHandler blogsAPI;
	private String serverURL = "";		
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while (testUser1.getDisplayName().equals(testUser3.getDisplayName()));		
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiFollower = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		blogsAPI = new APIBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);				
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_ideationBlogAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2578A8EDBD302BC385257C8A0039A132">TTT - VISITORS - ACTIVITY STREAM - 00050 - SHARED EXTERNALLY HEADER - IDEABLOG EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_ideationBlogAdded() {
		
		String testName = ui.startTest();
		
		// Creating the visitor model community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());	
		Community newCommunity = executeCommonCreateAndFollowCommunitySteps(baseCommunity);
		
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));

		// Login as User 3 and go to I'm Following view
		loginUser3AndGoToImFollowing(false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_IDEATION_BLOG, baseCommunity.getName(), null, testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are also displayed
		verifySharedExternallyIsDisplayedInAllFilters(newsStory, null, null, null);
		
		log.info("INFO: Delete the community clean up");		
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();			
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_newIdea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Blogs - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2578A8EDBD302BC385257C8A0039A132">TTT - VISITORS - ACTIVITY STREAM - 00038 - SHARED EXTERNALLY HEADER - BLOG EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_newIdea() {
		
		String testName = ui.startTest();
		
		// Creating the visitor model community with all setup steps completed		
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());	
		Community newCommunity = executeCommonCreateAndFollowCommunitySteps(baseCommunity);
		
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));

		log.info("INFO: " + testUser1.getDisplayName() + " will now add an idea to the Ideation Blog");
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = apiOwner.createIdea(baseBlogPost, newCommunity);
		
		// Login as User 3 and go to I'm Following view
		loginUser3AndGoToImFollowing(false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_IDEATION_BLOG_IDEA, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are also displayed
		verifySharedExternallyIsDisplayedInAllFilters(newsStory, baseBlogPost.getContent().trim(), null, null);
		
		log.info("INFO: Delete the community clean up");
		blogsAPI.deleteBlogPost(idea);
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();			
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_ideaComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser1 comment on the idea</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2578A8EDBD302BC385257C8A0039A132">TTT - VISITORS - ACTIVITY STREAM - 00038 - SHARED EXTERNALLY HEADER - BLOG EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_ideaComment() {
		
		String testName = ui.startTest();
		
		// Creating the visitor model community with all setup steps completed		
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());	
		Community newCommunity = executeCommonCreateAndFollowCommunitySteps(baseCommunity);
		
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));

		log.info("INFO: " + testUser1.getDisplayName() + " will now add an idea to the Ideation Blog");
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = apiOwner.createIdea(baseBlogPost, newCommunity);
		
		log.info("INFO " + testUser1.getDisplayName() + " will now post a comment to the idea");
		String comment = Helper.genStrongRand();
		BlogComment blogComment = blogsAPI.createBlogComment(comment, idea);		
		
		// Login as User 3 and go to I'm Following view
		loginUser3AndGoToImFollowing(false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.COMMENT_ON_THEIR_OWN_IDEATION_BLOG_IDEA, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are also displayed
		verifySharedExternallyIsDisplayedInAllFilters(newsStory, baseBlogPost.getContent().trim(), comment, null);
	
		log.info("INFO: Delete the community clean up");
		blogsAPI.deleteComment(blogComment);
		blogsAPI.deleteBlogPost(idea);
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();		
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_trackBack() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser1 comment on the idea</B></li>
	*<li><B>Step: testUser1 create a trackback on the ideation blog comment</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2578A8EDBD302BC385257C8A0039A132">TTT - VISITORS - ACTIVITY STREAM - 00038 - SHARED EXTERNALLY HEADER - BLOG EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_trackBack() {
		
		String testName = ui.startTest();
		
		// Creating the visitor model community with all setup steps completed		
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());	
		Community newCommunity = executeCommonCreateAndFollowCommunitySteps(baseCommunity);
		
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add an idea to the Ideation Blog");
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = apiOwner.createIdea(baseBlogPost, newCommunity);
		
		log.info("INFO " + testUser1.getDisplayName() + " will now post a comment to the idea");
		String comment = Helper.genStrongRand();
		BlogComment blogComment = blogsAPI.createBlogComment(comment, idea);		
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now log into Connections");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		log.info("INFO: Navigating to the community using the community UUID");
		baseCommunity.navViaUUID(uiCo);
		
		log.info("INFO: Select ideation blogs from left navigation menu");
		Community_LeftNav_Menu.IDEATIONBLOG.select(uiCo);
		
		log.info("INFO: Wait for the Ideation Blogs UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseIdeationBlogs);
		
		log.info("INFO: Navigate to the Ideas tab");
		ui.clickLinkWait(CommunitiesUIConstants.IdeasTab);
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseIdeationBlogsIdeas);
		
		log.info("INFO: Clicking on the link for the idea with title: " + idea.getTitle());
		ui.clickLinkWait("link=" + idea.getTitle());
		
		log.info("INFO: Wait for the Ideation Blogs Ideas UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForIdeationBlogIdeas);
		
		log.info("INFO: " + testUser1.getDisplayName() + " create a trackback on the blog");
		String trackbackComment = Data.getData().commonComment + Helper.genStrongRand();
		uiCo.postTrackbackOnCommunityBlog(idea, trackbackComment);
		
		ui.gotoHome();
		ui.logout();
		
		// Login as User 3 and go to I'm Following view
		loginUser3AndGoToImFollowing(true);

		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_TRACKBACK_IDEATION, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are also displayed
		verifySharedExternallyIsDisplayedInAllFilters(newsStory, baseBlogPost.getContent().trim(), comment, trackbackComment);
		
		log.info("INFO: Delete the community clean up");
		blogsAPI.deleteComment(blogComment);
		blogsAPI.deleteBlogPost(idea);
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();				
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_duplicateIdea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser1 duplicate the idea</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Blogs - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2578A8EDBD302BC385257C8A0039A132">TTT - VISITORS - ACTIVITY STREAM - 00038 - SHARED EXTERNALLY HEADER - BLOG EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_duplicateIdea() {
		
		String testName = ui.startTest();		
		
		// Creating the visitor model community with all setup steps completed		
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());	
		Community newCommunity = executeCommonCreateAndFollowCommunitySteps(baseCommunity);
		
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));

		log.info("INFO: " + testUser1.getDisplayName() + " will now add an idea to the Ideation Blog");
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = apiOwner.createIdea(baseBlogPost, newCommunity);	
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add a duplicate idea to the Ideation Blog");
		BaseBlogPost baseBlogPostDuplicate = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost ideaDuplicate = apiOwner.createIdea(baseBlogPostDuplicate, newCommunity);	
		
		// Log in as User 1 and duplicate the idea
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		baseCommunity.navViaUUID(uiCo);

		log.info("INFO: Select ideation blogs from left navigation menu");
		Community_LeftNav_Menu.IDEATIONBLOG.select(uiCo);
		
		log.info("INFO: Wait for the Ideation Blogs UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseIdeationBlogs);
		
		log.info("INFO: Navigate to the Ideas tab");
		ui.clickLinkWait(CommunitiesUIConstants.IdeasTab);
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseIdeationBlogsIdeas);
		
		log.info("INFO: Clicking on the link for the idea with title: " + idea.getTitle());
		ui.clickLinkWait("link=" + idea.getTitle());
		
		log.info("INFO: Wait for the Ideation Blogs Ideas UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForIdeationBlogIdeas);

		log.info("INFO: Mark the idea as a duplicate");
		ui.clickLinkWait(BlogsUIConstants.BlogsMoreActions);
		ui.clickLinkWait(BlogsUIConstants.MarkAsDuplicate);
		driver.getSingleElement(BlogsUIConstants.DuplicateIdeaTextbox).typeWithDelay(baseBlogPostDuplicate.getTitle());

		log.info("INFO: Selecting the duplicate idea from the typeahead");
		List<Element> options = driver.getVisibleElements(BlogsUIConstants.DuplicateIdeaTypeahead);
		
		// Iterate through the list and select the duplicate idea from drop down
		Iterator<Element> iterator = options.iterator();
		while (iterator.hasNext()) {
			Element option = iterator.next();
			if (option.getText().contains(baseBlogPostDuplicate.getTitle())){
				log.info("INFO: Found duplicate: " + baseBlogPostDuplicate.getTitle());
				option.click();
			}
		}
		
		log.info("INFO: Save the duplicate");
		ui.clickLinkWait(BlogsUIConstants.DuplicateIdeaSaveBtn);
		
		/**
		 *  Using BlogsUI.BlogsGraduateOK as it works for the button which "OKs" the duplication
		 */
		log.info("INFO: Confirm the duplicate");
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduateOK);
		
		ui.fluentWaitTextPresent(Data.getData().IdeaDuplicatedMsg);
		
		ui.gotoHome();
		ui.logout();
		
		// Login as User 3 and go to I'm Following view
		loginUser3AndGoToImFollowing(true);

		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.DUPLICATE_IDEA, baseBlogPost.getTitle(), baseCommunity.getName(), baseBlogPostDuplicate.getTitle(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are also displayed
		verifySharedExternallyIsDisplayedInAllFilters(newsStory, baseBlogPostDuplicate.getContent().trim(), null, null);
		
		log.info("INFO: Delete the community clean up");
		blogsAPI.deleteBlogPost(ideaDuplicate);
		blogsAPI.deleteBlogPost(idea);
		blogsAPI.deleteBlogPost(ideaDuplicate);
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();			
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_graduateIdea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser1 graduate the idea</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Blogs - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2578A8EDBD302BC385257C8A0039A132">TTT - VISITORS - ACTIVITY STREAM - 00038 - SHARED EXTERNALLY HEADER - BLOG EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_graduateIdea() {
		
		String testName = ui.startTest();	
		
		// Creating the visitor model community with all setup steps completed		
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());	
		Community newCommunity = executeCommonCreateAndFollowCommunitySteps(baseCommunity);
		
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));

		log.info("INFO: " + testUser1.getDisplayName() + " will now add an idea to the Ideation Blog");
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = apiOwner.createIdea(baseBlogPost, newCommunity);	
		
		log.info("INFO: " + testUser1.getDisplayName() + " graduate the Idea using the UI");		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		baseCommunity.navViaUUID(uiCo);

		log.info("INFO: Select ideation blogs from left navigation menu");
		Community_LeftNav_Menu.IDEATIONBLOG.select(uiCo);
		
		log.info("INFO: Wait for the Ideation Blogs UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseIdeationBlogs);
		
		log.info("INFO: Navigate to the Ideas tab");
		ui.clickLinkWait(CommunitiesUIConstants.IdeasTab);
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseIdeationBlogsIdeas);
		
		log.info("INFO: Clicking on the link for the idea with title: " + idea.getTitle());
		ui.clickLinkWait("link=" + idea.getTitle());
		
		log.info("INFO: Wait for the Ideation Blogs Ideas UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForIdeationBlogIdeas);

		log.info("INFO: Graduate the idea");
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduate);
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduateOK);

		ui.fluentWaitTextPresent(Data.getData().IdeaGraduatedMsg);
		
		ui.gotoHome();
		ui.logout();
		
		// Login as User 3 and go to I'm Following view
		loginUser3AndGoToImFollowing(true);	

		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.GRADUATE_IDEA, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are also displayed
		verifySharedExternallyIsDisplayedInAllFilters(newsStory, baseBlogPost.getContent().trim(), null, null);
				
		log.info("INFO: Delete the community clean up");	
		blogsAPI.deleteBlogPost(idea);
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();			
	}
	
	/**
	 * Performs the commonly used procedural steps for each test case that invokes it
	 * 
	 * Step 1: User 1 creates a restricted community
	 * Step 2: User 1 adds User 2 to the restricted community
	 * Step 3: User 1 adds User 3 to the restricted community
	 * Step 4: User 3 follows the restricted community
	 * Step 5: User 1 adds the Ideation Blogs widget to the restricted community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @return - The Community instance of the restricted community
	 */
	private Community executeCommonCreateAndFollowCommunitySteps(BaseCommunity baseCommunity) {
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create the community with title: " + baseCommunity.getName());
		Community newCommunity = baseCommunity.createAPI(apiOwner);
		
		log.info("INFO: " + testUser1.getDisplayName() + " adding " + testUser3.getDisplayName() + " as a member to PrivateCommunity");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser1.getDisplayName() + " adding " + testUser2.getDisplayName() + " as a member to PrivateCommunity");
		apiOwner.addMemberToCommunity(testUser2, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " follow the Community using API");		
		apiFollower.followCommunity(newCommunity);
		
		log.info("INFO: Adding Ideation Blog widget to community");
		baseCommunity.addWidgetAPI(newCommunity, apiOwner, BaseWidget.IDEATION_BLOG);
		
		return newCommunity;
	}
	
	/**
	 * Performs the common steps required to login as User 3 and navigate to the I'm Following view
	 */
	private void loginUser3AndGoToImFollowing(boolean preserveInstance) {
		
		log.info("INFO: Logging in with " + testUser3.getDisplayName() + " to verify news story");
		if(preserveInstance) {
			ui.loadComponent(Data.getData().ComponentHomepage, true);
		} else {
			ui.loadComponent(Data.getData().ComponentHomepage);
		}
		ui.login(testUser3);

		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();
	}
	
	/**
	 * Executes all required verifications for the 'Shared Externally' component displayed
	 * 
	 * @param newsStoryContent - The news story with which the 'Shared Externally' component should not be displayed
	 */
	private void verifySharedExternallyIsDisplayedInAllFilters(String newsStoryContent, String storyDescription, String comment1, String comment2) {
				
		// Verify that the news story is displayed in the All filter
		verifyNewsStoryComponentsAreDisplayed(HomepageUIConstants.FilterAll, newsStoryContent, storyDescription, comment1, comment2);
		
		// Verify that the Shared Externally message is not displayed in the All Filter
		verifySharedExternallyIsDisplayed(newsStoryContent, null);
		
		// Verify that the news story is displayed in the Communities filter
		verifyNewsStoryComponentsAreDisplayed(HomepageUIConstants.FilterCommunities, newsStoryContent, storyDescription, comment1, comment2);
		
		// Verify that the Shared Externally message is not displayed in the Communities Filter
		verifySharedExternallyIsDisplayed(newsStoryContent, null);
		
		// Verify that the news story is displayed in the Blogs filter
		verifyNewsStoryComponentsAreDisplayed(HomepageUIConstants.FilterBlogs, newsStoryContent, storyDescription, comment1, comment2);
		
		// Verify that the Shared Externally message is not displayed in the Blogs Filter
		verifySharedExternallyIsDisplayed(newsStoryContent, null);
	}
	
	/**
	 * Verifies all news story components are displayed - including all comments
	 * 
	 * @param newsStoryContent - The news story content to be verified as displayed
	 * @param storyDescription - The description of the news story event to be verified (null if no verification required)
	 * @param comment1 - The first comment posted to the news story event to be verified (null if no verification required)
	 * @param comment2 - The second comment posted to the news story event to be verified (null if no verification required)
	 */
	private void verifyNewsStoryComponentsAreDisplayed(String filter, String newsStoryContent, String storyDescription, String comment1, String comment2) {
		
		if(storyDescription == null && comment1 == null && comment2 == null) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryContent}, filter, true);
		} else if(comment1 == null && comment2 == null) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryContent, storyDescription}, filter, true);
		} else if(comment2 == null){
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryContent, storyDescription, comment1}, filter, true);
		} else {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryContent, storyDescription, comment1, comment2}, filter, true);
		}
	}
	
	/**
	 * Verifies that the Shared Externally components are displayed
	 * 
	 * @param newsStoryContent - The news story with which the 'Shared Externally' component should not be displayed
	 */
	private void verifySharedExternallyIsDisplayed(String newsStoryContent, String filter) {
		
		// Create the CSS selectors for the icon and the message
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStoryContent);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStoryContent);
				
		// Verify that both selectors are displayed in the UI
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelector}, filter, true);
	}
}