package com.ibm.conn.auto.tests.mt;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_Level_2_ExternalUser_Like extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_ExternalUser_Like.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser_orgA, extTestUser1;
	private String serverURL, serverURL_MT_orgA;
	private APICommunitiesHandler apiHandler;
	private BlogsUI bUI;
	private WikisUI wikiUI;
	private FileViewerUI uiViewer;
	private APIFileHandler apiFileOwner;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		extTestUser1 = cfg.getUserAllocator().getGroupUser("external_users_orga");
		cfg.getUserAllocator().getGroupUser("external_users_orga");
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();	
		testConfig.useBrowserUrl_Mt_OrgB();	
		cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
		apiFileOwner = new APIFileHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		bUI = BlogsUI.getGui(cfg.getProductName(), driver);
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		wikiUI = WikisUI.getGui(cfg.getProductName(), driver);
		uiViewer = FileViewerUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify Vote/Unvote a ideation blog's idea and like/unlike a comment in that idea as a External user</li>
	 * <li><B>Step: </B>Create external restricted community with API for regular Org userA having external userB as member</li>
	 * <li><B>Step: </B>Add Ideation blog widget in community with API</li>
	 * <li><B>Step: </B>Login to Community as Regular OrgA User</li>
	 * <li><B>Step: </B>Navigating to Ideation Blogs and click on default ideation blog</li>
	 * <li><B>Step: </B>Create a new idea in ideation blog and add a comment in idea</li>
	 * <li><B>Step: </B>Logout and login as External userB </li>
	 * <li><B>Step: </B>Navigate to the ideation blog and open default ideation blog </li>
	 * <li><B>Step: </B>Vote for first idea </li>
	 * <li><B>Verify: </B>Verify vote count and vote text</li>
	 * <li><B>Step: </B>Remove vote from the idea</li>
	 * <li><B>Verify: </B>Verify vote count and vote text after removing vote</li>
	 * <li><B>Step: </B>Open comment and like the comment</li> 
	 * <li><B>Verify: </B>Verify Comment count and link</li>
	 * <li><B>Step: </B>Unlike the comment</li>
	 * <li><B>Verify: </B>Verify Comment count and link after unlike the comment</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T197</li> 
	 * </ul>
	 */
	@Test(groups = { "mtlevel2"})
	public void likeUnlikeCommentAndVoteIdeaInIdeationBlogs() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED)
																.allowExternalUserAccess(true)
																.rbl(true)
																.shareOutside(true)
																.description("Test description for testcase " + testName)
																.build();
		
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("Entry " + testName + Helper.genDateBasedRandVal())
				 										 .tags("IdeaTag" + Helper.genDateBasedRand())
				 										 .content("Test Content for " + testName)
				 										 .build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName)
													 .build();
		
		Member member = new Member(CommunityRole.MEMBERS, extTestUser1);
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);
		
		logger.strongStep("Add Ideation Blog widget");
		log.info("INFO: Add ideation blog widget with api");
		orgaExternalRestricted.addWidgetAPI(comAPI, apiHandler, BaseWidget.IDEATION_BLOG);
		
		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to members tab");
		log.info("INFO: Navigate to members tab" );
		Community_TabbedNav_Menu.MEMBERS.select(ui);
		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			Assert.assertFalse(false, "Add member to community failed due to "+e.getMessage());
		}
		
		logger.strongStep("Click on the Save button");
		log.info("INFO: Select Save button"); 
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);
		
		
		logger.strongStep("Select Ideation blogs from the tabbed nav menu");
		log.info("INFO: Select Ideation blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui,2);
		
		logger.strongStep("Click on Ideation Blog");
		log.info("INFO: Select the default ideation blog link");
		ui.clickLinkWait(bUI.getCommIdeationBlogLink(orgaExternalRestricted));

		logger.strongStep("Select New Idea button");
		log.info("INFO: Select New Idea button");
		ui.clickLink(BlogsUIConstants.NewIdea);

		logger.strongStep("Create a new idea");
		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(bUI);
		
		logger.strongStep("Add a comment in created idea");
		log.info("INFO: Add a comment in created idea");
		bUI.createBlogIdeaComment(comment);
		
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify comment is added");
		log.info("INFO: Verify comment is added");
		ui.fluentWaitTextPresentRefresh(comment.getContent());
		
		logger.strongStep("Logout as user: " + testUser_orgA.getDisplayName());
		log.info("INFO: Logout as user: " + testUser_orgA.getDisplayName());
		ui.logout();
		
		logger.strongStep("Load Communities and Log In again as: " + extTestUser1.getDisplayName());
		log.info("INFO: Log In with external user as: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(extTestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		logger.strongStep("Navigate to the Ideation Blogs");
		log.info("INFO: Select Ideation blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui);
		
		logger.strongStep("Click on Ideation Blog");
		log.info("INFO: Select the default ideation blog link");
		ui.clickLinkWait(bUI.getCommIdeationBlogLink(orgaExternalRestricted));
		
		// Vote for the first idea
		logger.strongStep("Click on the Vote button");
		log.info("INFO: Select the Vote button");
		ui.clickLinkWait(BlogsUIConstants.IdeaVoteBtn);

		// Verify the vote number is 1 and text is Voted
		logger.strongStep("Validate that the vote count is now equal to 1 and text is changed to 'Voted'");
		log.info("INFO: Verify the vote count has changed to 1 and text is 'Voted'");
		String vNum = driver.getSingleElement(BlogsUIConstants.VoteNumber).getText();
		String vText = driver.getSingleElement(BlogsUIConstants.IdeaVoteBtn).getText();
		Assert.assertTrue(vNum.contains("1"), "The vote number is equal to 1 after one user votes on it. ");
		Assert.assertTrue(vText.contains("Voted"), "The text is 'Voted' after one user votes on it. ");
		
		// Remove Vote from voted idea
		logger.strongStep("Select Voted link and click ok button");
		log.info("INFO: Click on the Voted link");
		ui.clickLinkWait(BlogsUIConstants.IdeaVoteBtn);
		ui.clickLinkWait(BlogsUIConstants.VoteSubmitBtn);

		// Verify the vote number Vote text
		logger.strongStep("Validate that the vote count is now equal to 0 and text changes to 'Vote'");
		log.info("INFO: Verify the vote count has changed to 0 and text is 'Vote'");
		String voteNum = driver.getSingleElement(BlogsUIConstants.VoteNumber).getText();
		String voteText = driver.getSingleElement(BlogsUIConstants.IdeaVoteBtn).getText();
		Assert.assertTrue(voteNum.contains(""), "The vote number is equal 0 after removing Vote. ");
		Assert.assertTrue(voteText.contains("Vote"), "Text is 'Vote' after removing Vote. ");
		
		logger.strongStep("Open Comments");
		log.info("INFO: Click on Comments link");
		ui.clickLinkWait(BlogsUIConstants.IdeaCommentLink);
		
		logger.strongStep("Click Like to like the idea's comment");
		log.info("INFO: Click Like to like the idea's comment");
		ui.clickLinkWait(BlogsUIConstants.BlogsEntryCommentLike);
		
		logger.strongStep("Validate that the like count is now equal to 1 and 'Unlike' link is visible");
		log.info("INFO: Verify the like count has changed to 1 and 'Unlike' link is visible");
		String number = driver.getSingleElement(BlogsUIConstants.BlogsEntryLikeCount).getText();
		Assert.assertTrue(number.contains("1"), "The like count is equal to 1");	
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEntryUnlike), "'Unlike' link is present. ");
		
		logger.strongStep("Click Unlike link from comment");
		log.info("INFO: Click Unlike link from comment");
		ui.clickLinkWait(BlogsUIConstants.BlogsEntryUnlike);
		
		logger.strongStep("Validate that the like count is now equal to 0 and 'Like' link is visible");
		log.info("INFO: Verify the like count has changed to 0 and 'Like' link is visible");
		String count = driver.getSingleElement(BlogsUIConstants.BlogsEntryLikeCount).getText();
		Assert.assertTrue(count.contains(""), "The like count is equal to 0");	
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEntryCommentLike),"'Like' link is present. ");
		
		logger.strongStep("Delete the community using API");
		log.info("INFO: Delete the community using API");
		apiHandler.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the like/Unlike a community blog entry and entry comment as a external user of that community</li>
	*<li><B>Step:</B>Create external restricted community with API for regular Org userA having external userB as member</li>
	*<li><B>Step:</B>Create a Blog Entry in the community using API as the same internal user</li>
	*<li><B>Step:</B>Create a Comment for the Blog Entry using API as the same internal user</li>
	*<li><B>Step:</B>Get the UUID of the community</li>
	*<li><B>Step:</B>Load the Communities component and login as an external user</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Navigate to the Blog tab in the community</li>
	*<li><B>Verify:</B>The newly created Blog Entry is visible on the Blog page</li>
	*<li><B>Step:</B>Click on the link for the Blog Entry</li>
	*<li><B>Step:</B>Click on the Like link for the Blog Entry</li>
	*<li><B>Verify:</B>The Like link's text changes to Unlike after clicking on it</li>
	*<li><B>Verify:</B>The text 'You like this' appears after clicking on the Like link</li>
	*<li><B>Verify:</B>The like count changes to 1 after clicking on the Like link</li>
	*<li><B>Verify:</B>The heart icon's color changes to blue after clicking on the Like link</li>
	*<li><B>Verify:</B>Click on the like count for the Blog Entry</li>
	*<li><B>Verify:</B>The currently logged in external user's link appears in the like count popup</li>
	*<li><B>Step:</B>Click on the Unlike link for the Blog Entry</li>
	*<li><B>Verify:</B>The Unlike link's text changes to Like after clicking on it</li>
	*<li><B>Verify:</B>The text 'You like this' does not appear after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The like count changes to none after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The heart icon's color is not blue after clicking on the Unlike link</li>
	*<li><B>Step:</B>Click on the Like link for the Blog Comment</li>
	*<li><B>Verify:</B>The Like link's text changes to Unlike after clicking on it</li>
	*<li><B>Verify:</B>The text 'You like this' appears after clicking on the Like link</li>
	*<li><B>Verify:</B>The like count changes to 1 after clicking on the Like link</li>
	*<li><B>Verify:</B>The heart icon's color changes to blue after clicking on the Like link</li>
	*<li><B>Step:</B>Click on the like count for the Blog Comment</li>
	*<li><B>Verify:</B>The currently logged in external user's link appears in the like count popup</li>
	*<li><B>Step:</B>Click on the Unlike link for the Blog Comment</li>
	*<li><B>Verify:</B>The Unlike link's text changes to Like after clicking on it</li>
	*<li><B>Verify:</B>The text 'You like this' does not appear after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The like count changes to none after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The heart icon's color is not blue after clicking on the Unlike link</li>
	*<li><B>Step:</B>Delete the community</li>
	*<li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T197</li> 
	*</ul>
	*/	
	@Test(groups = {"mtlevel2"})
	public void likeUnlikeCommunityBlogEntryAndComment() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String blogComment = "BlogComment" + Helper.genDateBasedRand();
		
		APIBlogsHandler apiBlogsHandler = new APIBlogsHandler(serverURL, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
		
		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).allowExternalUserAccess(true)
																.rbl(true)
																.shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, extTestUser1))
																.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand())
				 												.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
																.content("Test description for testcase " + testName)
			 													.build();

		logger.strongStep("Create a community using API as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Create a community using API as: " + testUser_orgA.getDisplayName());
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Create a Blog Entry in the community " + orgaExternalRestricted.getName() + " using API as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Create a Blog Entry in the community " + orgaExternalRestricted.getName() + " using API as: " + testUser_orgA.getDisplayName());
		BlogPost entry = apiHandler.createBlogEntry(blogEntry, comAPI);	

		logger.strongStep("Create a Comment for the Blog Entry " + entry.getTitle() + " using API as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Create a Comment for the Blog Entry " + entry.getTitle() + " using API as: " + testUser_orgA.getDisplayName());
		apiBlogsHandler.createBlogComment(blogComment, entry);		

		logger.strongStep("Get the UUID of the community");
		log.info("INFO: Get the UUID of the community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load the Communities component and login as the external user: " + extTestUser1.getDisplayName());
		log.info("INFO: Load the Communities component and login as the external user: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(extTestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();
		
		logger.strongStep("Navigate to the Blog tab in the community");
		log.info("INFO: Navigate to the Blog tab in the community");
		Community_TabbedNav_Menu.BLOG.select(ui);
		
		logger.strongStep("Verify that the newly created Blog Entry is visible on the Blog page");
		log.info("INFO: Verify that the newly created Blog Entry is visible on the Blog page");
		Assert.assertTrue(driver.isElementPresent(BlogsUI.getBlogPost(blogEntry)),
				"Blog Entry is visible on the Blog page");
		
		logger.strongStep("Click on the link for the Blog Entry");
		log.info("INFO: Click on the link for the Blog Entry");
		ui.clickLinkWait(BlogsUI.getBlogPost(blogEntry));

		String blogEntryContainer = BlogsUI.getEntryContainer(blogEntry.getTitle());
		String blogCommentContainer = BlogsUI.getEntryCommentContainer(blogComment);

		//Like the Blog Entry and validate the Like functionality for the entry
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, blogEntryContainer, extTestUser1, true, true);

		//Unlike the Blog Entry and validate the Like functionality for the entry
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, blogEntryContainer, extTestUser1, false, true);

		//Like the Blog Comment and validate the Unlike functionality for the comment
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, blogCommentContainer, extTestUser1, true, true);

		//Unlike the Blog Comment and validate the Unlike functionality for the comment
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, blogCommentContainer, extTestUser1, false, true);

		//Delete the community
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the like and unlike functionalities for a File uploaded in a community</li>
	*<li><B>Step:</B>Create external restricted community with API for regular Org userA having external userB as member</li>
	*<li><B>Step:</B>Upload a file in the community using API as the same internal user</li>
	*<li><B>Step:</B>Get the UUID of the community</li>
	*<li><B>Step:</B>Load the Communities component and login as an external user</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Navigate to the Files tab in the community and select the List view</li>
	*<li><B>Verify:</B>The newly uploaded file is visible on the Files page</li>
	*<li><B>Step:</B>Click on the link for the file</li>
	*<li><B>Verify:</B>The FIDO Viewer opens after clicking on the link for the file</li>
	*<li><B>Step:</B>Click on the heart icon in the FIDO Viewer</li>
	*<li><B>Verify:</B>The blue colored heart icon is visible</li>
	*<li><B>Step:</B>Close the FIDO Viewer</li>
	*<li><B>Verify:</B>The like count for the file is 1</li>
	*<li><B>Step:</B>Click on the link for the file and wait for the FIDO Viewer to open</li>
	*<li><B>Step:</B>Click on the blue colored heart icon in the FIDO Viewer</li>
	*<li><B>Verify:</B>The non-blue heart icon is visible</li>
	*<li><B>Step:</B>Close the FIDO Viewer</li>
	*<li><B>Verify:</B>The like count for the file is 0</li>
	*<li><B>Step:</B>Delete the community</li>
	*<li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T197</li> 
	*</ul>
	*/
	@Test(groups = {"mtlevel2"})
	public void likeUnlikeCommunityFile() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).allowExternalUserAccess(true).rbl(true).shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, extTestUser1))
																.build();

		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
																.comFile(true)
																.rename("CommunityFile" + Helper.genDateBasedRand())
			 													.extension(".jpg")
		 														.build();

		logger.strongStep("Create a community using API as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Create a community using API as: " + testUser_orgA.getDisplayName());
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);

		logger.strongStep("Upload a file in the community using API as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Upload a file in the community using API as: " + testUser_orgA.getDisplayName());
		File file = new File(FilesUI.getFileUploadPath(baseFile.getName(), cfg));
		baseFile.createAPI(apiFileOwner, file, comAPI);
		
		baseFile.setName(baseFile.getRename()+baseFile.getExtension());
		
		logger.strongStep("Get the UUID of the community");
		log.info("INFO: Get the UUID of the community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load the Communities component and login as the external user: " + extTestUser1.getDisplayName());
		log.info("INFO: Load the Communities component and login as the external user: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(extTestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to the Files tab in the community");
		log.info("INFO: Navigate to the Files tab in the community");
		Community_TabbedNav_Menu.FILES.select(ui,2);

		logger.strongStep("Click on the List view");
		log.info("INFO: Click on the List view");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Verify that the newly uploaded file is visible on the Files page");
		log.info("INFO: Verify that the newly uploaded file is visible on the Files page");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUI.getFileIsUploaded(baseFile)),
				"The uploaded file is visible on the Files page");

		logger.strongStep("Click on the link for the file");
		log.info("INFO: Click on the link for the file");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(baseFile));

		logger.strongStep("Verify that the FIDO Viewer opens after clicking on the link for the file");
		log.info("INFO: Verify that the FIDO Viewer opens after clicking on the link for the file");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.sharingTabInFiDO),
				"FIDO Viewer opened after clicking on the link for the file");
		
		logger.strongStep("Click on the heart icon in the FIDO Viewer");
		log.info("INFO: Click on the heart icon in the FIDO Viewer");
		ui.clickLinkWait(FilesUIConstants.likeIcon);

		logger.strongStep("Verify that the blue colored heart icon is visible");
		log.info("INFO: Verify that the blue colored heart icon is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.unlikeIcon),
				"The blue colored heart icon was visible");

		logger.strongStep("Close the FIDO Viewer");
		log.info("INFO: Close the FIDO Viewer");
		ui.clickLinkWait(FilesUIConstants.closeIcon);

		String likeCount = FilesUI.getLikeCount(baseFile);

		logger.strongStep("Verify that the like count for the file is 1");
		log.info("INFO: Verify that the like count for the file is 1");
		ui.fluentWaitPresentWithRefresh(likeCount);
		Assert.assertEquals(driver.getSingleElement(likeCount).getAttribute("title"), "1 person likes this",
				"The number of likes for the file was not equal to 1");

		logger.strongStep("Click on the link for the file");
		log.info("INFO: Click on the link for the file");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(baseFile));

		logger.strongStep("Wait for the FIDO Viewer to open after clicking on the link for the file");
		log.info("INFO: Wait for the FIDO Viewer to open after clicking on the link for the file");
		ui.fluentWaitElementVisible(FilesUIConstants.sharingTabInFiDO);

		logger.strongStep("Click on the blue colored heart icon in the FIDO Viewer");
		log.info("INFO: Click on the blue colored heart icon in the FIDO Viewer");
		ui.clickLinkWait(FilesUIConstants.unlikeIcon);

		logger.strongStep("Verify that the non-blue heart icon is visible");
		log.info("INFO: Verify that the non-blue heart icon is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.likeIcon),
				"The non-blue heart icon was visible");

		logger.strongStep("Close the FIDO Viewer");
		log.info("INFO: Close the FIDO Viewer");
		ui.clickLinkWait(FilesUIConstants.closeIcon);

		logger.strongStep("Verify that the like count for the file is 0");
		log.info("INFO: Verify that the like count for the file is 0");
		Assert.assertEquals(driver.getSingleElement(likeCount).getAttribute("title"), "0 people like this",
				"The number of likes for the file was not equal to 0");

		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>Verify the like and unlike functionalities for a Status Update and comment to the update on the Homepage</li>
	*<li><B>Step:</B>Post a Status Update as an internal user on the Homepage using API</li>
	*<li><B>Step:</B>Post a comment to the Status Update using API as the same internal user</li>
	*<li><B>Step:</B>Load the Homepage component and login as an external user</li>
	*<li><B>Verify:</B>The Status Update and the comment are visible on the Homepage</li>
	*<li><B>Step:</B>Click on the Like link for the Status Update</li>
	*<li><B>Verify:</B>The Like link's text changes to Unlike after clicking on it</li>
	*<li><B>Verify:</B>The like count changes to 1 after clicking on the Like link</li>
	*<li><B>Verify:</B>The heart icon's color changes to blue after clicking on the Like link</li>
	*<li><B>Verify:</B>Click on the like count for the Status Update</li>
	*<li><B>Verify:</B>The currently logged in external user's link appears in the like count popup</li>
	*<li><B>Step:</B>Click on the Unlike link for the Status Update</li>
	*<li><B>Verify:</B>The Unlike link's text changes to Like after clicking on it</li>
	*<li><B>Verify:</B>The like count changes to none after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The heart icon's color is not blue after clicking on the Unlike link</li>
	*<li><B>Step:</B>Click on the Like link for the comment</li>
	*<li><B>Verify:</B>The Like link's text changes to Unlike after clicking on it</li>
	*<li><B>Verify:</B>The text 'You like this' appears after clicking on the Like link</li>
	*<li><B>Verify:</B>The like count changes to 1 after clicking on the Like link</li>
	*<li><B>Verify:</B>The heart icon's color changes to blue after clicking on the Like link</li>
	*<li><B>Step:</B>Click on the like count for the comment</li>
	*<li><B>Verify:</B>The currently logged in external user's link appears in the like count popup</li>
	*<li><B>Step:</B>Click on the Unlike link for the comment</li>
	*<li><B>Verify:</B>The Unlike link's text changes to Like after clicking on it</li>
	*<li><B>Verify:</B>The text 'You like this' does not appear after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The like count changes to none after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The heart icon's color is not blue after clicking on the Unlike link</li>
	*<li><B>Step:</B>Delete the Status Update</li>
	*<li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T197</li> 
	*</ul>
	*/
	@Test(groups = {"mtlevel2"})
	public void likeUnlikeHomepageStatusAndComment() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		APIProfilesHandler profileInternalHandler = new APIProfilesHandler(serverURL, testUser_orgA.getEmail(), testUser_orgA.getPassword());
		APIProfilesHandler profileExternalHandler = new APIProfilesHandler(serverURL, extTestUser1.getEmail(), extTestUser1.getPassword());

		String testName = ui.startTest();
		String statusUpdate = testName + Helper.genDateBasedRand() + " Status Update for";
		String commentOnStatus = "This is the comment "+Helper.genDateBasedRand();
		
		Mentions mention = new Mentions.Builder(extTestUser1, profileExternalHandler.getUUID())
																.browserURL(serverURL)
																.beforeMentionText(statusUpdate)
																.build();
		
		logger.strongStep("Post a Status Update on the Homepage with a mention for " + extTestUser1.getDisplayName() + " as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Post a Status Update on the Homepage with a mention for " + extTestUser1.getDisplayName() + " as: " + testUser_orgA.getDisplayName());
		String statusUpdateId = profileInternalHandler.addMentionsStatusUpdate(mention);
		
		logger.strongStep("Post a comment to the Status Update using API as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Post a comment to the Status Update using API as: " + testUser_orgA.getDisplayName());
		profileInternalHandler.postComment(statusUpdateId, commentOnStatus);
		
		logger.strongStep("Load the Homepage component and login as the external user: " + extTestUser1.getDisplayName());
		log.info("INFO: Load the Homepage component and login as the external user: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(extTestUser1);

		String statusUpdateContainer = HomepageUI.getStatusUpdateContainer(statusUpdate);
		String commentContainer = HomepageUI.getStatusUpdateCommentContainer(commentOnStatus);
		
		logger.strongStep("Verify that the Status Update was visible to: " + extTestUser1.getDisplayName());
		log.info("INFO: Verify that the Status Update was visible to: " + extTestUser1.getDisplayName());
		Assert.assertTrue(ui.fluentWaitElementVisible(statusUpdateContainer),
				"The Status Update was visible to: " + extTestUser1.getDisplayName());

		logger.strongStep("Verify that the comment on the Status Update is visible to: " + extTestUser1.getDisplayName());
		log.info("INFO: Verify that the comment on the Status Update is visible to: " + extTestUser1.getDisplayName());
		Assert.assertTrue(ui.fluentWaitElementVisible(statusUpdateContainer),
				"The comment on the Status Update was visible to: " + extTestUser1.getDisplayName());

		//Like the Status Update and validate the Like functionality for the update
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, statusUpdateContainer, extTestUser1, true, false);

		//Unlike the Status Update and validate the Like functionality for the update
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, statusUpdateContainer, extTestUser1, false, false);

		//Like the Comment on the Status Update and validate the Like functionality for the comment
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, commentContainer, extTestUser1, true, false);

		//Unlike the Comment on the Status Update and validate the Unlike functionality for the comment
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, commentContainer, extTestUser1, false, false);

		logger.strongStep("Delete the Status Update");
		log.info("INFO: Delete the Status Update");
		profileInternalHandler.deleteBoardMessage(statusUpdateId);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>Verify the like and unlike functionalities for a Forum Topic and Reply to the Topic in a community</li>
	*<li><B>Step:</B>Create external restricted community with API for regular Org userA having external userB as member</li>
	*<li><B>Step:</B>Create a Forum Topic in the community using API as the same internal user</li>
	*<li><B>Step:</B>Create a Topic Reply for the Forum Topic using API as the same internal user</li>
	*<li><B>Step:</B>Get the UUID of the community</li>
	*<li><B>Step:</B>Load the Communities component and login as an external user</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Navigate to the Forums tab in the community</li>
	*<li><B>Verify:</B>The newly created Forum Topic is visible on the Forums page</li>
	*<li><B>Step:</B>Click on the link for the Forum Topic</li>
	*<li><B>Step:</B>Click on the Like link for the Forum Topic</li>
	*<li><B>Verify:</B>The Like link's text changes to Unlike after clicking on it</li>
	*<li><B>Verify:</B>The text 'You like this' appears after clicking on the Like link</li>
	*<li><B>Verify:</B>The like count changes to 1 after clicking on the Like link</li>
	*<li><B>Verify:</B>The heart icon's color changes to blue after clicking on the Like link</li>
	*<li><B>Verify:</B>Click on the like count for the Forum Topic</li>
	*<li><B>Verify:</B>The currently logged in external user's link appears in the like count popup</li>
	*<li><B>Step:</B>Click on the Unlike link for the Forum Topic</li>
	*<li><B>Verify:</B>The Unlike link's text changes to Like after clicking on it</li>
	*<li><B>Verify:</B>The text 'You like this' does not appear after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The like count changes to none after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The heart icon's color is not blue after clicking on the Unlike link</li>
	*<li><B>Step:</B>Click on the Like link for the Topic Reply</li>
	*<li><B>Verify:</B>The Like link's text changes to Unlike after clicking on it</li>
	*<li><B>Verify:</B>The text 'You like this' appears after clicking on the Like link</li>
	*<li><B>Verify:</B>The like count changes to 1 after clicking on the Like link</li>
	*<li><B>Verify:</B>The heart icon's color changes to blue after clicking on the Like link</li>
	*<li><B>Step:</B>Click on the like count for the Topic Reply</li>
	*<li><B>Verify:</B>The currently logged in external user's link appears in the like count popup</li>
	*<li><B>Step:</B>Click on the Unlike link for the Topic Reply</li>
	*<li><B>Verify:</B>The Unlike link's text changes to Like after clicking on it</li>
	*<li><B>Verify:</B>The text 'You like this' does not appear after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The like count changes to none after clicking on the Unlike link</li>
	*<li><B>Verify:</B>The heart icon's color is not blue after clicking on the Unlike link</li>
	*<li><B>Step:</B>Delete the community</li>
	*<li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T197</li> 
	*</ul>
	*/
	@Test(groups = {"mtlevel2"})
	public void likeUnlikeCommunityForumTopicAndReply() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		APIForumsHandler forumsHandler = new APIForumsHandler(serverURL, testUser_orgA.getEmail(), testUser_orgA.getPassword());

		String testName = ui.startTest();
		String topicReply = "TopicReply" + Helper.genDateBasedRand();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).allowExternalUserAccess(true).rbl(true).shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, extTestUser1))
																.build();

		logger.strongStep("Create a community using API as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Create a community using API as: " + testUser_orgA.getDisplayName());
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);

		logger.strongStep("Create a Forun Topic in the community: " + orgaExternalRestricted.getName() + " as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Create a Forun Topic in the community: " + orgaExternalRestricted.getName() + " as: " + testUser_orgA.getDisplayName());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic("ForumTopic" + Helper.genDateBasedRand(), orgaExternalRestricted);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser_orgA, apiHandler, comAPI, baseForumTopic);

		logger.strongStep("Create a Topic Reply for the topic: " + baseForumTopic.getTitle() + " as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Create a Forun Topic in the community: " + orgaExternalRestricted.getName() + " as: " + testUser_orgA.getDisplayName());
		CommunityForumEvents.createForumTopicReply(testUser_orgA, forumsHandler, forumTopic, topicReply);

		logger.strongStep("Get the UUID of the community");
		log.info("INFO: Get the UUID of the community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load the Communities component and login as the external user: " + extTestUser1.getDisplayName());
		log.info("INFO: Load the Communities component and login as the external user: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(extTestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to the Forums tab in the community");
		log.info("INFO: Navigate to the Forums tab in the community");
		Community_TabbedNav_Menu.FORUMS.select(ui,3);
		
		ui.waitForPageLoaded(driver);

		logger.strongStep("Verify that the newly created Forum Topic is visible on the Forums page");
		log.info("INFO: Verify that the newly created Forum Topic is visible on the Forums page");
		Assert.assertTrue(ui.fluentWaitElementVisible(ForumsUI.selectComForumTopic(baseForumTopic)),
				"Forum Topic is visible on the Forums page");

		logger.strongStep("Click on the link for the Forum Topic");
		log.info("INFO: Click on the link for the Forum Topic");
		ui.clickLinkWait(ForumsUI.selectComForumTopic(baseForumTopic));

		String topicContainer = ForumsUI.getTopicContainer(forumTopic.getTitle());
		String replyContainer = ForumsUI.getTopicReplyContainer(topicReply);

		//Like the Forum Topic and validate the Like functionality for the topic
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, topicContainer, extTestUser1, true, true);

		//Unlike the Forum Topic and validate the Unlike functionality for the topic
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, topicContainer, extTestUser1, false, true);

		//Like the Topic Reply and validate the Like functionality for the reply
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, replyContainer, extTestUser1, true, true);

		//Unlike the Topic Reply and validate the Unlike functionality for the reply
		validateLikeUnlikeForBlogForumStatusUpdateContents(logger, replyContainer, extTestUser1, false, true);

		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify Like/Unlike a wiki page as a External user and created as a regular org userA</li>
	 * <li><B>Step: </B>Create external restricted community with API for regular Org userA having external userB as member</li>
	 * <li><B>Step: </B>Login to Community as Regular OrgA User</li>
	 * <li><B>Step: </B>Navigating to Wiki page</li>
	 * <li><B>Step: </B>Create a new wiki page</li>
	 * <li><B>Step: </B>Logout and login as External userB </li>
	 * <li><B>Step: </B>Navigate to the wiki page </li>
	 * <li><B>Step: </B>Like the created wiki page </li>
	 * <li><B>Verify: </B>Verify the message 'You like this' appears</li>
	 * <li><B>Verify: </B>Verify Like link changes to Unlike</li>
	 * <li><B>Verify: </B>Verify vote count is correct/li>
	 * <li><B>Step: </B>Click on like count</li>
	 * <li><B>Verify: </B>Verify user name is listed in the pop-up</li>
	 * <li><B>Step: </B>Unlike the wiki page</li> 
	 * <li><B>Verify: </B>Verify the message 'You like this' disappears</li>
	 * <li><B>Verify: </B>Verify Unlike link changes to Like</li>
	 * <li><B>Verify: </B>Verify Comment count is correct</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T197</li> 
	 * </ul>
	 */
	@Test(groups = { "mtlevel2"})
	public void likeUnlikeWikiPage() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		User extTestUser1 = cfg.getUserAllocator().getGroupUser("external_users_orga");
		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED)
																.allowExternalUserAccess(true)
																.rbl(true)
																.shareOutside(true)
																.description("Test description for testcase " + testName)
																.build();
		
		BaseWikiPage page = new BaseWikiPage.Builder("Page" + Helper.genMonthDateBasedRandVal(), PageType.NavPage)
											.tags("tagforliketest")
											.description("this is a test description for creating a Peer wiki page")
											.build();
		
		Member member = new Member(CommunityRole.MEMBERS, extTestUser1);
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);
		
		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		ui.waitForCommunityLoaded();
		
		logger.strongStep("Navigate to members tab");
		log.info("INFO: Navigate to members tab" );
		Community_TabbedNav_Menu.MEMBERS.select(ui);
		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			Assert.assertFalse(false, "Add member to community failed due to "+e.getMessage());
		}
		
		logger.strongStep("Click on the Save button");
		log.info("INFO: Select Save button"); 
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);
		
		logger.strongStep("Navigate to the Wiki");
		log.info("INFO: Select Wikifrom the tabbed nav menu");
		Community_TabbedNav_Menu.WIKI.select(ui);
		
		logger.strongStep("Create a wiki page in default Wiki");
		log.info("INFO: Create a wiki page in default Wiki");
		page.create(wikiUI);
		
		logger.strongStep("Logout as user: " + testUser_orgA.getDisplayName());
		log.info("INFO: Logout as user: " + testUser_orgA.getDisplayName());
		ui.logout();
		
		logger.strongStep("Load Communities and Log In again as: " + extTestUser1.getDisplayName());
		log.info("INFO: Log In with external user as: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(extTestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();
		
		logger.strongStep("Navigate to the Wiki");
		log.info("INFO: Select Wiki from the tabbed nav menu");
		Community_TabbedNav_Menu.WIKI.select(ui);
				
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Open page " + page.getName());
		log.info("INFO: Open page " + page.getName());
		wikiUI.openWikiPageNav(page);

		logger.strongStep("Like wiki page");
		log.info("INFO: Like wiki page");
		ui.clickLinkWait(WikisUIConstants.likeLink);
		
		logger.strongStep("Verify the message 'You like this' appears");
		log.info("INFO: Verify the message 'You like this' appears");
		Assert.assertTrue(ui.fluentWaitTextPresent("You like this"), "The message 'You like this' appears. ");
		
		logger.strongStep("Verify 'Unlike' Link appears");
		log.info("INFO: Verify 'Unlike' Link appears");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.unlikeLink), "'Unlike' link is visible. ");
		
		logger.strongStep("Verify like count is correct");
		log.info("INFO: Verify like count is correct");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.LikeCount).getText().contains("1"), "Like count is equal to 1. ");	
		
		logger.strongStep("Click on like count and Verify User: " + extTestUser1.getDisplayName() + " is listed in pop-up");
		log.info("INFO: Click on like count and Verify User: " + extTestUser1.getDisplayName() + " is listed in pop-up");
		ui.clickLinkWait(WikisUIConstants.LikeCount);
		Assert.assertTrue(driver.isElementPresent(WikisUI.getUserLinkInPopUp(extTestUser1)), "" + extTestUser1.getDisplayName() + " is listed in pop-up. ");
		ui.clickLinkWait(WikisUIConstants.CloseListOfUsersWhoLikeAPage);
		
		logger.strongStep("Unlike wiki page using 'Unlike' link");
		log.info("INFO: Unlike wiki page using 'Unlike' link");
		ui.clickLinkWait(WikisUIConstants.unlikeLink);
		
		logger.strongStep("Verify the messege 'You like this' disappears");
		log.info("INFO: Verify the messege 'You like this' disappears");
		Assert.assertTrue(ui.fluentWaitTextNotPresent("You like this"), "The messege 'You like this' disappears. ");
		
		logger.strongStep("Verify 'Like' Link appears");
		log.info("INFO: Verify 'Like' Link appears");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.likeLink), "'Like' link is visible. ");
			
		logger.strongStep("Verify like count is correct");
		log.info("INFO: Verify like count is correct");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.LikeCount).getText().contains(""), "Like count is equal to 0. ");
		
		logger.strongStep("Delete the community using API");
		log.info("INFO: Delete the community using API");
		apiHandler.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify Like/Unlike a standalone file as a External user and created as a regular org userA</li>
	 * <li><B>Step: </B>Upload a Standalone file with API</li>
	 * <li><B>Step: </B>Share the file with external UserB</li>
	 * <li><B>Step: </B>Login to Files as External userB</li>
	 * <li><B>Step: </B>Click on 'Files shared With Me' Link</li>
	 * <li><B>Step: </B>Switch the display from default Tile to Details</li>
	 * <li><B>Step: </B>Open uploaded file</li>
	 * <li><B>Step: </B>Click on Like icon</li>
	 * <li><B>Verify: </B>Verify Like icon changes</li>
	 * <li><B>Verify: </B>Verify Like count is correct/li>
	 * <li><B>Step: </B>Open File</li>
	 * <li><B>Step: </B>Unlike the file</li> 
	 * <li><B>Verify: </B>Verify Like icon changes</li>
	 * <li><B>Verify: </B>Verify Like count is correct</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T197</li> 
	 * </ul>
	 */
	@Test(groups = { "mtlevel2"})
	public void likeUnlikeStandaloneFile() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		APIProfilesHandler apiFileFollower = new APIProfilesHandler(serverURL_MT_orgA, extTestUser1.getEmail(), extTestUser1.getPassword());
		
		BaseFile file = new BaseFile.Builder(Data.getData().file3)
									.extension(".jpg")
									.rename(testName + Helper.genDateBasedRand())
									.shareLevel(ShareLevel.PEOPLE)
									.sharedWith(apiFileFollower.getUUID())
									.build();
		
		// Upload a file
		logger.strongStep("Upload a file via API and share that file");
		FileEntry fileEntry = uiViewer.upload(file, testConfig, testUser_orgA);
		file.shareFileAPI(apiFileOwner, fileEntry);
		file.setName(file.getRename() + file.getExtension());
		
		// Load the component
		logger.strongStep("Load Files and login as: " + extTestUser1.getDisplayName());
		log.info("INFO: Load Files and login as: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(extTestUser1);

		// Switch the display from default Tile to Details
		logger.strongStep("Go to 'Files Shared With Me' view. Select 'Details' display button");
		log.info("INFO: Go to 'Files Shared With Me' view. Select 'Details' display button");
		ui.clickLinkWithJavascript(FilesUIConstants.filesSharedWithMe );
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Open Uploaded File");
		log.info("INFO: Open Uploaded File");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(file));
		
		logger.strongStep("Like the file");
		log.info("INFO: Like the file");
		driver.getSingleElement(FilesUIConstants.likeIcon).click();
		
		logger.strongStep("Verify Like is Changes to unlike");
		log.info("INFO: Verify Like is Changes to unlike");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.unlikeIcon), "Like Icon changes. ");
		
		logger.strongStep("Close the FIDO");
		log.info("INFO: Close the FIDO");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		logger.strongStep("Verify Like count is increased by 1");
		log.info("INFO: Verify Like count is increased by 1");
		String num=driver.getSingleElement(FilesUI.getLikeCount(file)).getText();
		Assert.assertTrue(num.contains("1"), "Like count is equal to 1. ");
		
		logger.strongStep("Open Uploaded File");
		log.info("INFO: Open Uploaded File");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(file));
		
		logger.strongStep("Unlike the file");
		log.info("INFO: Unlike the file");
		driver.getSingleElement(FilesUIConstants.unlikeIcon).click();
		
		logger.strongStep("Verify Unlike icon is changed to Like");
		log.info("INFO: Verify Unlike icon is changed to Like");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.likeIcon), "Unlike Icon changes. ");
		
		logger.strongStep("Close the FIDO");
		log.info("INFO: Close the FIDO");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		logger.strongStep("Verify Like count is decreased by 1");
		log.info("INFO: Verify Like count is decreased by 1");
		String num1=driver.getSingleElement(FilesUI.getLikeCount(file)).getText();
		Assert.assertTrue(num1.contains(""), "Like count is equal to 0. ");
		
		logger.strongStep("Delete the file using API");
		log.info("INFO: Delete the file using API");
		apiFileOwner.deleteFile(fileEntry);
		
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify Like/Unlike an posted entry in Recent Update as an external userB</li>
	 * <li><B>Step: </B>Create external restricted community with API for regular Org userA having external userB as member</li>
	 * <li><B>Step: </B>Login to Community as Regular OrgA User</li>
	 * <li><B>Step: </B>Navigate to the created community</li>
	 * <li><B>Step: </B>Navigate to Recent Updates Page</li>
	 * <li><B>Step: </B>Post an entry</li>
	 * <li><B>Step: </B>Logout and login as External userB </li>
	 * <li><B>Step: </B>Navigate to the community created by regular OrgA user</li>
	 * <li><B>Step: </B>Navigate to the Recent Updates Page </li>
	 * <li><B>Verify: </B>Verify Posted entry is visible</li>
	 * <li><B>Step: </B>Like that entry</li>
	 * <li><B>Verify: </B>Verify Unlike link is displayed</li>
	 * <li><B>Verify: </B>Verify vote count is increased by 1</li>
	 * <li><B>Step: </B>Click on like count</li>
	 * <li><B>Verify: </B>Verify user name is listed in the pop-up</li>
	 * <li><B>Step: </B>Unlike the entry</li> 
	 * <li><B>Verify: </B>Verify Like link is displayed</li>
	 * <li><B>Verify: </B>Verify vote count is decreased by 1</li>
	 * <li><B>Step: </B>Delete the community via API</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T197</li> 
	 * </ul>
	 */
	@Test(groups = { "mtlevel2"})
	public void likeUnlikeRecentUpdatesEntryInCommunity() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
		
		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED)
																.allowExternalUserAccess(true)
																.rbl(true)
																.shareOutside(true)
																.description("Test description for testcase " + testName)
																.build();
		
		Member member = new Member(CommunityRole.MEMBERS, extTestUser1);
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);
		
		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();
		
		logger.strongStep("Navigate to members tab");
		log.info("INFO: Navigate to members tab" );
		Community_TabbedNav_Menu.MEMBERS.select(ui);
		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			Assert.assertFalse(false, "Add member to community failed due to "+e.getMessage());
		}
		
		logger.strongStep("Click on the Save button");
		log.info("INFO: Select Save button"); 
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);
		
		logger.strongStep("Navigate to Recent Updates view");
		log.info("INFO: Navigating to Recent Updates view");
		Community_TabbedNav_Menu.RECENT_UPDATES.select(ui);
	    
		// Type Status message and Post it
		logger.strongStep("Type the Status messge and click on post");
		log.info("INFO: Type the Status messge and click on post");
		ui.typeMessageInShareBox("Posting a status message in community", true);
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
		
		logger.strongStep("Logout as user: " + testUser_orgA.getDisplayName());
		log.info("INFO: Logout as user: " + testUser_orgA.getDisplayName());
		ui.logout();
		
		logger.strongStep("Load Communities and Log In again as: " + extTestUser1.getDisplayName());
		log.info("INFO: Log In with external user as: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(extTestUser1);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();
		
		logger.strongStep("Navigate to Recent Updates view");
		log.info("INFO: Navigating to Recent Updates view");
		Community_TabbedNav_Menu.RECENT_UPDATES.select(ui);
		
		// Test the Status Message is getting displayed
		logger.strongStep("Verify Status message is displayed");
		log.info("INFO: Verify Status message is displayed");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText()
				.contains("Posting a status message in community"), "Status message appears. ");
		
		logger.strongStep("Click on the Like link to like status message");
		log.info("INFO: Click on the Like link to like status message");
		driver.getFirstElement(HomepageUIConstants.EELike).click();
		
		logger.strongStep("Verify the 'Unlike' link displays on the Recent updates page");
		log.info("INFO: Verify the 'Unlike' link displays on the Recent updates page");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.EELikeUndo), "The Unlike link appears. ");
		
		// Verify the number of Likes is 1.
		logger.strongStep("Verify that the number 1 now appears before the 'Unlike' link");
		log.info("INFO: Verify that the number 1 now appears before the 'Unlike' link");
		Assert.assertTrue(driver.getSingleElement(HomepageUIConstants.EELikeCount).getText().contains("1"), "Like count is equal to 1. ");
		
		logger.strongStep("Click on like count and Verify User: " + extTestUser1.getDisplayName() + " is listed in pop-up");
		log.info("INFO: Click on like count and Verify User: " + extTestUser1.getDisplayName() + " is listed in pop-up");
		ui.clickLinkWait(WikisUIConstants.LikeCount);
		Assert.assertTrue(driver.isElementPresent(WikisUI.getUserLinkInPopUp(extTestUser1)), extTestUser1.getDisplayName() + " is listed in pop-up. ");
		ui.clickLinkWait(WikisUIConstants.CloseListOfUsersWhoLikeAPage);
		
		logger.strongStep("Click on the Unlike link");
		log.info("INFO: Click on the Unlike link");
		driver.getFirstElement(HomepageUIConstants.EELikeUndo).click();
		
		// Verify that the 'Unike' link become 'Like' link
		logger.strongStep("Verify the 'Like' link displays");
		log.info("INFO: Verify the 'Like' link displays");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.EELike), "The Like link appears. ");
		
		// Verify the number of Likes is 0.
		logger.strongStep("Verify that the number 0 now appears before the 'Like' link");
		log.info("INFO: Verify that the number 0 now appears before the 'Like' link");
		Assert.assertTrue(driver.getSingleElement(HomepageUIConstants.EELikeCount).getText().contains(""), "Like count is equal to 0. ");
		
		logger.strongStep("Delete the community using API");
		log.info("INFO: Delete the community using API");
		apiHandler.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify Like/Unlike a comment in posted Recent Update entry as an external userB</li>
	 * <li><B>Step: </B>Create external restricted community with API for regular Org userA having external userB as member</li>
	 * <li><B>Step: </B>Login to Community as Regular OrgA User</li>
	 * <li><B>Step: </B>Navigate to the community</li>
	 * <li><B>Step: </B>Navigate to Status Updates Page</li>
	 * <li><B>Step: </B>Post an entry</li>
	 * <li><B>Step: </B>Post a comment in that entry</li>
	 * <li><B>Step: </B>Logout and login as External userB </li>
	 * <li><B>Step: </B>Navigate to the community created by regular OrgA user</li>
	 * <li><B>Step: </B>Navigate to the Status Updates Page </li>
	 * <li><B>Step: </B>Like the comment created in status update entry</li>
	 * <li><B>Verify: </B>Verify Unlike link is displayed</li>
	 * <li><B>Verify: </B>Verify vote count is increased by 1</li>
	 * <li><B>Step: </B>Unlike the entry</li> 
	 * <li><B>Verify: </B>Verify Like link is displayed</li>
	 * <li><B>Verify: </B>Verify vote count is decreased by 1</li>
	 * <li><B>Step: </B>Delete the community via API</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T197</li> 
	 * </ul>
	 */
	@Test(groups = { "mtlevel2"})
	public void likeUnlikeCommentInStatusUpdatesEntryInCommunity() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
		
		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED)
																.allowExternalUserAccess(true)
																.rbl(true)
																.shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, extTestUser1))
																.build();
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);
		
		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		logger.strongStep("Navigate to Status Updates view");
		log.info("INFO: Navigating to Status Updates view");
		Community_TabbedNav_Menu.STATUSUPDATES.select(ui);
		
		logger.strongStep("Type the Status messge and click on post");
		log.info("INFO: Type the Status messge and click on post");
		ui.typeMessageInShareBox("Posting a status message in community", true);
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
		
		// Post a comment in status updates entry
		logger.strongStep("Click on Comment link and post a comment");
		log.info("INFO: Click on Comment link and post a comment");
		ui.clickLinkWait(CommunitiesUIConstants.StatusComment);
		ui.fluentWaitElementVisible(CommunitiesUIConstants.ShareBoxComment_iFrame);
		driver.getSingleElement(CommunitiesUIConstants.ShareBoxComment_iFrame).click();
		driver.getSingleElement(CommunitiesUIConstants.ShareBoxComment_iFrame).type("Comment for the status update entry");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
		
		logger.strongStep("Logout as user: " + testUser_orgA.getDisplayName());
		log.info("INFO: Logout as user: " + testUser_orgA.getDisplayName());
		ui.logout();
		
		logger.strongStep("Load Communities and Log In again as: " + extTestUser1.getDisplayName());
		log.info("INFO: Log In with external user as: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(extTestUser1);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		logger.strongStep("Navigate to Status Updates view");
		log.info("INFO: Navigating to Status Updates view");
		Community_TabbedNav_Menu.STATUSUPDATES.select(ui);
		
		logger.strongStep("Like the comment added by Regular user");
		log.info("INFO: Like the comment added by Regular user");
		driver.getFirstElement(CommunitiesUIConstants.statusUpdateFirstEECommentLike).click();
		
		logger.strongStep("Verify the 'Unlike' link displays");
		log.info("INFO: Verify the 'Unlike' link displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.statusUpdateFirstEECommentUnlike), "The Unlike link appears. ");
		
		logger.strongStep("Verify that the number 1 now appears before the 'Unlike' link");
		log.info("INFO: Verify that the number 1 now appears before the 'Unlike' link");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.statusUpdateFirstEECommentLikeNum).getText().contains("1"), "Like count is equal to 1. ");
		
		logger.strongStep("Click on the Unlike link");
		log.info("INFO: Click on the Unlike link");
		driver.getFirstElement(CommunitiesUIConstants.statusUpdateFirstEECommentUnlike).click();
		
		// Verify that the 'Unike' link become 'Like' link
		logger.strongStep("Verify the 'Like' link displays");
		log.info("INFO: Verify the 'Like' link displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.statusUpdateFirstEECommentLike), "The Like link appears. ");
		
		// Verify the number of Likes is 0.
		logger.strongStep("Verify that the number 0 now appears before the 'Like' link");
		log.info("INFO: Verify that the number 0 now appears before the 'Like' link");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.statusUpdateFirstEECommentLikeNum).getText().contains(""), "Like count is equal to 0. ");
		
		logger.strongStep("Delete the community using API");
		log.info("INFO: Delete the community using API");
		apiHandler.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}
	
	/**
	*This method validates the like and unlike functionalities for Blog Entries and Comments and Forum Topics and Replies
	*@param logger for defect logging
	*@param container for the container in which the Blog Entry/Comment or the Forum Topic/Reply resides
	*@param user for the user liking/disliking the content
	*@param isLiked to ascertain whether to test the Like or Unlike functionality
	*@param isLikeDescription to ascertain whether to validate the text 'You like this'
	*/
	private void validateLikeUnlikeForBlogForumStatusUpdateContents(DefectLogger logger, String container, User user, boolean isLiked, boolean isLikeDescription) {
		
				
		Element likeLink = driver.getFirstElement(container + "//a[@class='lotusLikeAction']");
		
		logger.strongStep("Click on the " + likeLink.getText() + " link");
		log.info("INFO: Click on the " + likeLink.getText() + " link");
		likeLink.click();
		
		String likeDescriptionText = container + "//span[@class='lotusLikeDescription'][text()='You like this']";

		Element likeCount = driver.getFirstElement(container + "//div[@class='lotusLikeText']");
		
		if (isLiked) {
			
			boolean isHeartIconBlue = driver.isElementPresent(container + "//a[@title='1 person likes this']/img[@class='lotusIconLike']");

			logger.strongStep("Verify that the Like link's text changes to Unlike after clicking on it");
			log.info("INFO: Verify that the Like link's text changes to Unlike after clicking on it");
			Assert.assertEquals(likeLink.getText(), "Unlike",
					"The Like link's text did not change to Unlike after clicking on it");
			
			if (isLikeDescription) {
				
				logger.strongStep("Verify that the text 'You like this' appears after clicking on the Like link");
				log.info("INFO: Verify that the text 'You like this' appears after clicking on the Like link");
				Assert.assertTrue(ui.fluentWaitElementVisible(likeDescriptionText),
						"The text 'You like this' appeared after clicking on the Like link");
			
			}
			
			logger.strongStep("Verify that the heart icon's color changes to blue after clicking on the Like link");
			log.info("INFO: Verify that the heart icon's color changes to blue after clicking on the Like link");
			Assert.assertTrue(isHeartIconBlue,
					"The heart icon's color changed to blue after clicking on the Like link");

			logger.strongStep("Verify that the like count changes to 1 after clicking on the Like link");
			log.info("INFO: Verify that the like count changes to 1 after clicking on the Like link");
			Assert.assertEquals(likeCount.getText(), "1",
					"The like count did not change to 1 even after clicking on the Like link");
						
			logger.strongStep("Click on the like count");
			log.info("INFO: Click on the like count");
			likeCount.click();
			
			String personInLikeCountPopUp = driver.getSingleElement(BlogsUIConstants.BlogsEntryLikePopup + " " + "a[class='fn lotusPerson bidiAware']").getText();
			
			logger.strongStep("Verify that the user's link in the like count popup belongs to: " + user.getDisplayName());
			log.info("INFO: Verify that the user's link in the like count popup belongs to: " + user.getDisplayName());
			Assert.assertEquals(personInLikeCountPopUp, user.getDisplayName(),
					"The user's link in the like count popup did not belong to: " + user.getDisplayName());
			
			logger.strongStep("Close the like count popup");
			log.info("INFO: Close the like count popup");
			ui.clickLinkWithJavascript(BlogsUIConstants.BlogsEEUserPopupClose);
			
		}
		
		else {
			
			boolean isHeartIconNotBlue = driver.isElementPresent(container + "//a[@title='0 people like this']/img[@class='lotusIconLike']");

			logger.strongStep("Verify that the Unlike link's text changes to Like after clicking on it");
			log.info("INFO: Verify that the Unlike link's text changes to Like after clicking on it");
			Assert.assertEquals(likeLink.getText(), "Like",
					"The Unlike link's text did not change to Like even after clicking on it");
			
			if (isLikeDescription) {
				
				driver.turnOffImplicitWaits();
				
				logger.strongStep("Verify that the text 'You like this' does not appear after clicking on the Unlike link");
				log.info("INFO: Verify that the text 'You like this' does not appear after clicking on the Unlike link");
				Assert.assertFalse(driver.isElementPresent(likeDescriptionText),
						"The text 'You like this' was not visible after clicking on the Unlike link");
				
				driver.turnOnImplicitWaits();
				
			}
				
			logger.strongStep("Verify that the heart icon's color is not blue after clicking on the Unlike link");
			log.info("INFO: Verify that the heart icon's color is not blue after clicking on the Unlike link");
			Assert.assertTrue(isHeartIconNotBlue,
					"The heart icon's color was not blue after clicking on the Unlike link");
						
			logger.strongStep("Verify that the like count changes to none after clicking on the Unlike link");
			log.info("INFO: Verify that the like count changes to none after clicking on the Unlike link");
			Assert.assertEquals(likeCount.getText(), " ",
					"The like count did not change to none even after clicking on the Unlike link");
			
		}

	}
}
