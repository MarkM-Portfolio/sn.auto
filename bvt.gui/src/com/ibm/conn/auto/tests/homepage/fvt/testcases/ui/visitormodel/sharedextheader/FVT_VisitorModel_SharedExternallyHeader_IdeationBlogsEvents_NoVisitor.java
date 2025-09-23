package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

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

import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
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
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
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

/**
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_SharedExternallyHeader_IdeationBlogsEvents_NoVisitor extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_SharedExternallyHeader_IdeationBlogsEvents_NoVisitor.class);

	private HomepageUI ui;
	private CommunitiesUI uiCo;
	private TestConfigCustom cfg;	
	private User testUser1, testUser3;	
	private APICommunitiesHandler apiOwner, apiFollower;
	private APIBlogsHandler blogsAPI;
	private String serverURL = "";	
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while (testUser1.getDisplayName().equals(testUser3.getDisplayName()));	
		
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
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_ideationBlogAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/967A18F12E3D06C085257C8A0039A520">TTT - VISITORS - ACTIVITY STREAM - 00052 - SHARED EXTERNALLY HEADER - IDEABLOG EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_ideationBlogAdded() {
		
		String testName = ui.startTest();
		
		// Creating the Public community with all setup steps completed.	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);	
		Community newCommunity = baseCommunity.createAPI(apiOwner);
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));		

		log.info("INFO: Adding " + testUser3.getDisplayName() + " to private community as a member");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " follow the Community using API");		
		apiFollower.followCommunity(newCommunity);
		
		log.info("INFO: Adding Ideation Blog widget to community");
		baseCommunity.addWidgetAPI(newCommunity, apiOwner, BaseWidget.IDEATION_BLOG);		

		//testUser3 goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		log.info("INFO: Logging in with " + testUser3.getDisplayName() + " to verify news story");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);

		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_IDEATION_BLOG, baseCommunity.getName(), null, testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);
		
		log.info("INFO: Select Updates from Left Navigation menu to go to I'm Following");
		ui.gotoImFollowing();
		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, HomepageUIConstants.FilterBlogs, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
	
		log.info("INFO: Delete the community clean up");		
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();			
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_newIdea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Blogs - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/967A18F12E3D06C085257C8A0039A520">TTT - VISITORS - ACTIVITY STREAM - 00052 - SHARED EXTERNALLY HEADER - IDEABLOG EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_newIdea() {
		
		String testName = ui.startTest();
		
		// Creating the Public community with all setup steps completed.	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);	
		Community newCommunity = baseCommunity.createAPI(apiOwner);
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));		

		log.info("INFO: Adding " + testUser3.getDisplayName() + " to private community as a member");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " follow the Community using API");		
		apiFollower.followCommunity(newCommunity);
		
		log.info("INFO: Adding Ideation Blog widget to community");
		baseCommunity.addWidgetAPI(newCommunity, apiOwner, BaseWidget.IDEATION_BLOG);	
		
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		log.info("INFO: " + testUser1.getDisplayName() + " adding Blog Post to Community");
		BlogPost idea = apiOwner.createIdea(baseBlogPost, newCommunity);			
		
		//testUser3 goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		log.info("INFO: Logging in with " + testUser3.getDisplayName() + " to verify news story");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);
		
		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_IDEATION_BLOG_IDEA, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);
		
		log.info("INFO: Select Updates from Left Navigation menu to go to I'm Following");
		ui.gotoImFollowing();
		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, HomepageUIConstants.FilterBlogs, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);			
	
		log.info("INFO: Delete the community clean up");	
		blogsAPI.deleteBlogPost(idea);
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();
		
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_ideaComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser1 comment on the idea</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/967A18F12E3D06C085257C8A0039A520">TTT - VISITORS - ACTIVITY STREAM - 00052 - SHARED EXTERNALLY HEADER - IDEABLOG EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_ideaComment() {
		
		String testName = ui.startTest();
		
		// Creating the Public community with all setup steps completed.	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);	
		Community newCommunity = baseCommunity.createAPI(apiOwner);
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));		

		log.info("INFO: Adding " + testUser3.getDisplayName() + " to private community as a member");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " follow the Community using API");		
		apiFollower.followCommunity(newCommunity);
		
		log.info("INFO: Adding Ideation Blog widget to community");
		baseCommunity.addWidgetAPI(newCommunity, apiOwner, BaseWidget.IDEATION_BLOG);	
		
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		log.info("INFO: " + testUser1.getDisplayName() + " adding Blog Post to Community");
		BlogPost idea = apiOwner.createIdea(baseBlogPost, newCommunity);	
		
		String blogComment = Helper.genStrongRand();
		
		log.info("INFO " + testUser1.getDisplayName() + " adding Comment to Blog Entry");
		blogsAPI.createBlogComment(blogComment, idea);		
		
		//testUser3 goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		log.info("INFO: Logging in with " + testUser3.getDisplayName() + " to verify news story");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);
				
		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.COMMENT_IDEATION_BLOG_IDEA, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);
		
		log.info("INFO: Select Updates from Left Navigation menu to go to I'm Following");
		ui.gotoImFollowing();
		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent(), blogComment}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent(), blogComment}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent(), blogComment}, HomepageUIConstants.FilterBlogs, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		

		log.info("INFO: Delete the community clean up");	
		blogsAPI.deleteBlogPost(idea);
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();				
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_trackBack() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser1 comment on the idea</B></li>
	*<li><B>Step: testUser1 create a trackback on the ideation blog comment</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/967A18F12E3D06C085257C8A0039A520">TTT - VISITORS - ACTIVITY STREAM - 00052 - SHARED EXTERNALLY HEADER - IDEABLOG EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_trackBack() {
		
		String testName = ui.startTest();
		
		// Creating the Public community with all setup steps completed.	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);	
		Community newCommunity = baseCommunity.createAPI(apiOwner);
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));

		log.info("INFO: Adding " + testUser3.getDisplayName() + " to private community as a member");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " follow the Community using API");		
		apiFollower.followCommunity(newCommunity);
		
		log.info("INFO: Adding Ideation Blog widget to community");
		baseCommunity.addWidgetAPI(newCommunity, apiOwner, BaseWidget.IDEATION_BLOG);	
		
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		log.info("INFO: " + testUser1.getDisplayName() + " adding Blog Post to Community");
		BlogPost idea = apiOwner.createIdea(baseBlogPost, newCommunity);	
		
		String blogComment = Helper.genStrongRand();
		
		log.info("INFO " + testUser1.getDisplayName() + " adding Comment to Blog Entry");
		blogsAPI.createBlogComment(blogComment, idea);			
		
		//log in with user1 to add trackback comment
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);		
		
		log.info("INFO: Navigate to the community using UUID");
		baseCommunity.navViaUUID(uiCo);

		log.info("INFO: Select Ideation blogs from left navigation menu");
		Community_LeftNav_Menu.IDEATIONBLOG.select(uiCo);

		log.info("INFO: Accessing " + idea.getTitle());
		ui.clickLinkWait(BlogsUIConstants.Ideation_IdeasTab);
		ui.clickLinkWait("link=" + idea.getTitle());

		String trkBackComment = Data.getData().StatusComment + Helper.genMonthDateBasedRandVal();
		
		log.info("INFO: " + testUser1.getDisplayName() + " adding a comment");
		ui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

		if (driver.isElementPresent(BlogsUIConstants.BlogsCommentTextArea))
			ui.typeText(BlogsUIConstants.BlogsCommentTextArea, trkBackComment);
		else
		ui.typeNativeInCkEditor(trkBackComment);
		
		log.info("INFO: " + testUser1.getDisplayName() + " creating trackback");
		ui.clickLinkWait(BlogsUIConstants.BlogCommentTrackbackCheckBox);
		ui.clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
		ui.gotoHome();
		ui.logout();
		
		//testUser3 goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		log.info("INFO: Logging in with " + testUser3.getDisplayName() + " to verify news story");
		ui.loadComponent(Data.getData().ComponentHomepage, true);
		ui.login(testUser3);		

		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_TRACKBACK_IDEATION, idea.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);
		
		log.info("INFO: Select Updates from Left Navigation menu to go to I'm Following");
		ui.gotoImFollowing();
		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent(), blogComment}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory,baseBlogPost.getContent(), blogComment}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent(), blogComment}, HomepageUIConstants.FilterBlogs, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);
		
		log.info("INFO: Delete the community clean up");	
		blogsAPI.deleteBlogPost(idea);
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();			
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_duplicateIdea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser1 duplicate the idea</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Blogs - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/967A18F12E3D06C085257C8A0039A520">TTT - VISITORS - ACTIVITY STREAM - 00052 - SHARED EXTERNALLY HEADER - IDEABLOG EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_duplicateIdea() {
		
		String testName = ui.startTest();		
		
		// Creating the Public community with all setup steps completed.	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);	
		Community newCommunity = baseCommunity.createAPI(apiOwner);
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));

		log.info("INFO: Adding " + testUser3.getDisplayName() + " to private community as a member");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " follow the Community using API");		
		apiFollower.followCommunity(newCommunity);			
		
		log.info("INFO: Adding Ideation Blog widget to community");
		baseCommunity.addWidgetAPI(newCommunity, apiOwner, BaseWidget.IDEATION_BLOG);		
		
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		log.info("INFO: " + testUser1.getDisplayName() + " adding Blog Post to Community");
		BlogPost idea = apiOwner.createIdea(baseBlogPost, newCommunity);	
		
		BaseBlogPost duplicate = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		log.info("INFO: " + testUser1.getDisplayName() + " adding Blog Post to Community");
		BlogPost ideadup = apiOwner.createIdea(duplicate, newCommunity);	
		
		//log in and duplicate the idea
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		baseCommunity.navViaUUID(uiCo);

		//navigate to the idea
		log.info("INFO: accessing"+ baseBlogPost.getTitle());
		ui.clickLinkWait("link=" + baseBlogPost.getTitle());

		//Duplicate the idea
		ui.clickLinkWait(BlogsUIConstants.BlogsMoreActions);
		ui.clickLinkWait(BlogsUIConstants.MarkAsDuplicate);
		driver.getSingleElement(BlogsUIConstants.DuplicateIdeaTextbox).type(duplicate.getTitle());

		log.info("INFO: Selecting the user from the typeahead");
		//Collect all the options
		List<Element> options = driver.getVisibleElements(BlogsUIConstants.DuplicateIdeaTypeahead);
		
		//Iterate through the list and select the user from drop down
		Iterator<Element> iterator = options.iterator();
		while (iterator.hasNext()) {
			Element option = iterator.next();
			if (option.getText().contains(duplicate.getTitle())){
				log.info("INFO: Found duplicate " + duplicate.getTitle());
				option.click();
			}
		}
		
		log.info("INFO: Save the duplicate");
		ui.clickLinkWait(BlogsUIConstants.DuplicateIdeaSaveBtn);
		//Using BlogsUI.BlogsGraduateOK as it works for the button which "OKs" the duplication
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduateOK);
		
		ui.fluentWaitTextPresent(Data.getData().IdeaDuplicatedMsg);
		ui.gotoHome();
		ui.logout();
		
		//testUser3 goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		ui.loadComponent(Data.getData().ComponentHomepage, true);
		ui.login(testUser3);

		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.DUPLICATE_IDEA, baseBlogPost.getTitle(), baseCommunity.getName(), duplicate.getTitle(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);
		
		log.info("INFO: Select Updates from Left Navigation menu to go to I'm Following");
		ui.gotoImFollowing();
		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, HomepageUIConstants.FilterBlogs, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);			
	
		log.info("INFO: Delete the community clean up");	
		blogsAPI.deleteBlogPost(idea);
		blogsAPI.deleteBlogPost(ideadup);
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();		
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_graduateIdea() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add ideation blogs within this community</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser1 graduate the idea</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Blogs - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/967A18F12E3D06C085257C8A0039A520">TTT - VISITORS - ACTIVITY STREAM - 00052 - SHARED EXTERNALLY HEADER - IDEABLOG EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_graduateIdea() {
		
		String testName = ui.startTest();	
		
		// Creating the Public community with all setup steps completed.	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);	
		Community newCommunity = baseCommunity.createAPI(apiOwner);
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity));

		log.info("INFO: Adding " + testUser3.getDisplayName() + " to private community as a member");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " follow the Community using API");		
		apiFollower.followCommunity(newCommunity);			

		log.info("INFO: Adding Ideation Blog widget to community");
		baseCommunity.addWidgetAPI(newCommunity, apiOwner, BaseWidget.IDEATION_BLOG);		
		
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		log.info("INFO: " + testUser1.getDisplayName() + " adding Blog Post to Community");
		BlogPost idea = apiOwner.createIdea(baseBlogPost, newCommunity);	
		
		//log in and graduate the idea
		log.info("INFO: " + testUser1.getDisplayName() + " graduate the Idea using GUI");		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		baseCommunity.navViaUUID(uiCo);
		
		//navigate to the idea
		log.info("INFO: accessing"+ baseBlogPost.getTitle());
		ui.clickLinkWait("link=" + baseBlogPost.getTitle());

		log.info("INFO: Graduate the idea");
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduate);
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduateOK);

		ui.fluentWaitTextPresent(Data.getData().IdeaGraduatedMsg);
		ui.gotoHome();
		ui.logout();
		
		//testUser3 goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		ui.loadComponent(Data.getData().ComponentHomepage, true);
		ui.login(testUser3);

		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.GRADUATE_IDEA, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());		
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);
		
		log.info("INFO: Select Updates from Left Navigation menu to go to I'm Following");
		ui.gotoImFollowing();		
		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, HomepageUIConstants.FilterBlogs, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, false);			
	
		log.info("INFO: Delete the community clean up");	
		blogsAPI.deleteBlogPost(idea);
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();		
	}
}
