package com.ibm.conn.auto.tests.moderation.regression;

import java.io.File;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.BlogRole;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.BlogSettings_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.ModerationUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;


public class CreateModerationData extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(CreateModerationData.class);

		private static CommunitiesUI communityui;
		private static BlogsUI blogui;
		private static ForumsUI forumui;
		private static FilesUI fileui;
		private static ModerationUI modui;
		private static TestConfigCustom cfg;
		private static User comOwner,comMember,adminUser;
		private static BaseCommunity community;
		private static APICommunitiesHandler apiCommunityOwner;
		private static APIFileHandler apiFileOwner;
		private static APIBlogsHandler apiBlogOwner;
		private static APIForumsHandler apiForumOwner;
		private static BaseCommunity.Access defaultAccess;
		private static String communityName;
		private static Community comAPI;
		private static String serverURL;

		private static String filePath;

		private GatekeeperConfig gkc;
		
		@BeforeClass(alwaysRun = true)
		public void setUp() throws Exception {

			// initialize the configuration
			cfg = TestConfigCustom.getInstance();

			blogui = BlogsUI.getGui(cfg.getProductName(), driver);
			forumui = ForumsUI.getGui(cfg.getProductName(), driver);
			fileui = FilesUI.getGui(cfg.getProductName(), driver);
			modui = ModerationUI.getGui(cfg.getProductName(), driver);
			communityui = CommunitiesUI.getGui(cfg.getProductName(), driver);

			communityName = Helper.stamp(Data.getData().WF_CommunityName);

			filePath = cfg
					.getTestConfig()
					.getBrowserEnvironment()
					.getAbsoluteFilePath(cfg.getUploadFilesDir(),
							Data.getData().file1);
			
			// Load User
			comOwner = cfg.getUserAllocator().getUser();
			log.info("INFO: Using test user as community owner: "
					+ comOwner.getDisplayName());

			comMember = cfg.getUserAllocator().getUser();
			log.info("INFO: Using test user as community member: "
					+ comMember.getDisplayName());

			defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());

			serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
			apiCommunityOwner = new APICommunitiesHandler(serverURL,
					comOwner.getUid(), comOwner.getPassword());

			adminUser = cfg.getUserAllocator().getAdminUser();
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);

			Member member1 = new Member(CommunityRole.MEMBERS, comMember);
			community = new BaseCommunity.Builder(communityName)
					.access(defaultAccess).addMember(member1)
					.description("Test Community Moderation").build();

			// create community using API
			log.info("INFO: Create community using API");
			comAPI = community.createAPI(apiCommunityOwner);

			// add the UUID to community log.info("INFO: Get UUID of community");
			community.setCommunityUUID(community.getCommunityUUID_API(
					apiCommunityOwner, comAPI));
			apiCommunityOwner.addMemberToCommunity(comMember, comAPI, Role.MEMBER);

			// add the blog and idea widgets using API
			log.info("INFO: Add the blogs widget using the API");
			community.addWidgetAPI(comAPI, apiCommunityOwner, BaseWidget.BLOG);
			community.addWidgetAPI(comAPI, apiCommunityOwner,
					BaseWidget.IDEATION_BLOG);
		}

		/*
		 *
		 */
		@AfterMethod(alwaysRun = true)
		public void cleanUp() throws Exception {
			// Return Users
			cfg.getUserAllocator().checkInAllUsers();
		}

		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Pending Blog Entry in Community</li>
		 * <li><B>Step: </B>Login to the community created by API by member user</li>
		 * <li><B>Step: </B>Click "New entry" link on overview page and create one blog entry</li>
		 * <li><B>Verify: </B>Verify the entry is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreatePendingEntry() throws Exception {
			log.info("INFO: test Create blog Entry and verify it is submitted for approval in Community");

			String testName = blogui.startTest();

			String entryTitle = "BlogEntry" + Helper.genDateBasedRand();
			BaseBlogPost blogEntry = new BaseBlogPost.Builder(entryTitle)
					.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
					.content("Test description for testcase " + testName).build();

			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);

			// Click on the blogs link on the overview page
			log.info("INFO: Click 'Start your first entry' link or 'Create Blog Entry'");

			if (driver.isElementPresent(ModerationUI.BlogNewFirstEntryLink)) {
				modui.clickLink(ModerationUI.BlogNewFirstEntryLink);
			} else {
				modui.clickLink(ModerationUI.BlogNewEntryLink);
			}

			// Verify that the community blog opens and New Entry button displays
			log.info("INFO: Verify that the community blog opens and New Entry button displays");
			Assert.assertTrue(driver.isTextPresent("New Entry"),
					"ERROR: 'New Entry' text not found in the opened blog");

			// Add an Entry
			log.info("INFO: Add a new entry to the blog");
			blogEntry.create(blogui);

			// Verify that new entry is submitted for approval
			log.info("INFO: Verify that the new blog entry is submitted for approval");
			Assert.assertTrue(
					blogui.fluentWaitTextPresent(ModerationUI.BlogSubmitMessage),
					"ERROR: Entry not submitted");

			blogui.endTest();
		}


		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Pending Ideationblog idea in Community</li>
		 * <li><B>Step: </B>Login to the community created by API by member user</li>
		 * <li><B>Step: </B>Click "New idea" link on overview page and create one idea</li>
		 * <li><B>Verify: </B>Verify the idea is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreatePendingIdea() throws Exception {
			log.info("INFO: test Create idea and it's sumitted for approval in Community");

			String testName = modui.startTest();

			String entryTitle = "Idea" + Helper.genDateBasedRand();
			BaseBlogPost blogEntry = new BaseBlogPost.Builder(entryTitle)
					.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
					.content("Test description for testcase " + testName).build();

			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);

			// Click on the blogs link in the nav
			log.info("INFO: Click 'Start your first entry' or 'Contribute an Idea' link");

			if (driver.isElementPresent(ModerationUI.IdeaNewFirstIdeaLink)) {
				modui.clickLink(ModerationUI.IdeaNewFirstIdeaLink);
			} else {
				modui.clickLink(ModerationUI.IdeaNewIdeaLink);
			}

			// Verify that "new entry" form opened
			log.info("INFO: Verify that the community new entry forum opens and New Entry text displays");

			Assert.assertTrue(driver.isTextPresent("New Idea"),
					"ERROR: 'New Idea' text not found in the opened blog");

			// Add an idea
			log.info("INFO: Add a new idea to the blog");
			blogEntry.create(blogui);

			// Verify that new idea is submitted for approval
			log.info("INFO: Verify that the new Idea is submitted for approval");
			Assert.assertTrue(
					modui.fluentWaitTextPresent(ModerationUI.BlogSubmitMessage),
					"ERROR: Idea not submitted");

			modui.endTest();
		}


		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Pending Forums topic in Community</li>
		 * <li><B>Step: </B>Login to the community created by API by member user</li>
		 * <li><B>Step: </B>Click "Start a topic" link on overview page and create one forum topic</li>
		 * <li><B>Verify: </B>Verify the topic is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreatePendingTopic() throws Exception {
			log.info("INFO: test Create forum topic and it is submitted for approval in Community");

			modui.startTest();

			String topicTitle = "Topic" + Helper.genDateBasedRand();

			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);

			// Click on the "start first topic" link
			log.info("INFO: Click 'Start the First Topic' link");

			if (driver.isElementPresent(ModerationUI.ForumNewFirstTopicLink)) {
				modui.clickLink(ModerationUI.ForumNewFirstTopicLink);
			} else {
				modui.clickLink(ModerationUI.ForumStartTopicLink);
			}

			// Verify that "new topic" form opened
			log.info("INFO: Verify that the community new entry forum opens and New Entry text displays");

			Assert.assertTrue(driver.isTextPresent("Start a Topic"),
					"ERROR: 'Start a Topic' text not found in the opened blog");

			// Add an topic
			log.info("INFO: Add a new topic to the community forum");

			// Add title
			log.info("INFO: Entering title of new forum topic");
			this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputText_Title)
					.type(topicTitle);

			// Enter forum topic tags if provided
			log.info("INFO: Entering any forum topic tags");
			this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputText_Tags)
						.type(topicTitle);

			// Enter forum topic description if provided
			log.info("INFO: Entering forum topic description");
			modui.typeNativeInCkEditor(topicTitle+" description");
			

			// Select Save
			log.info("INFO: Attempting to save new topic");
			modui.clickLink(ForumsUIConstants.Save_Forum_Topic_Button);

			// Verify that new topic is submitted for approval
			log.info("INFO: Verify that the new topic is submitted for approval");
			Assert.assertTrue(modui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Topic not submitted");

			modui.endTest();
		}

		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Pending File in Community</li>
		 * <li><B>Step: </B>Login to the community created by API by member user</li>
		 * <li><B>Step: </B>Click "Add a new file" link on overview page and upload one file</li>
		 * <li><B>Verify: </B>Verify the file is uploaded successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreatePendingFile() throws Exception {
			log.info("INFO: test Create file and verify it's submitted for approval in Community");

			DefectLogger logger=dlog.get(Thread.currentThread().getId());
			
			fileui.startTest();

			// upload file1 (Disert.jpg)
			String fileName = Data.getData().file1;

			BaseFile file = new BaseFile.Builder(fileName).comFile(true)
					.extension(".jpg").build();

			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);

			//Select Files from left menu
			log.info("INFO: Select Fiels from left navigation menu");
			Community_LeftNav_Menu.FILES.select(communityui);
			
			logger.strongStep("Add a new file A");
			log.info("INFO: Add a new file: " + file.getName());
			if(!cfg.getSecurityType().equalsIgnoreCase("false"))
				file.upload(fileui,gkc);
			else
				file.upload(fileui);
			
			// Verify that new entry is submitted for approval message
			log.info("INFO: Verify that the new blog entry is submitted for approval");
			Assert.assertTrue(fileui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: File not submitted");

			fileui.endTest();
		}

		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Pending Blog Entry comment in Community</li>
		 * <li><B>Step: </B>Create one Blog entry in community by API</li>
		 * <li><B>Step: </B>Login to the community by member user</li>
		 * <li><B>Step: </B>Click the link of new created blog entry on overview page</li>
		 * <li><B>Verify: </B>Verify the entry is is opened</li>
		 * <li><B>Step: </B>Click "Add a comment" link and add content and click "Save"</li>
		 * <li><B>Verify: </B>Verify the comment is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreatePendingEntryComment() throws Exception {
			log.info("INFO: test Create blog comment and verify it's sumitted for approval in Community");

			String testName = blogui.startTest();

			String entryTitle = "BlogEntry" + Helper.genDateBasedRand();
			String commentContent = "BlogComment" + Helper.genDateBasedRand();

			BaseBlogPost blogEntry = new BaseBlogPost.Builder(entryTitle)
					.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
					.content("Test description for testcase " + testName).build();

			// Add Post to blog
			BlogPost blogPostEntry = apiCommunityOwner.createBlogEntry(blogEntry,
					comAPI);
			Assert.assertTrue(blogPostEntry != null,
					"Failed to create blog entry in community using API.");

			// GUI
			// Log community member to community and create one pending comment
			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);


			String entryLink = "link=" + entryTitle;

			// Click on the blogs entry link on the overview page
			log.info("INFO: Click the entry link");
			communityui.clickLink(entryLink);

			// Verify that the community blog opens and New Entry button displays
			log.info("INFO: Verify that the blog entry opens and 'Add a Comment' displays");

			Assert.assertTrue(driver.isTextPresent("Add a Comment"),
					"ERROR: 'New Entry' text not found in the opened blog");

			// Add an comment to Entry
			log.info("INFO: Add a new comment to entry");
			
			//Click on the Add a comment link for entry
			log.info("INFO: Select the Add a comment link for entry");
			blogui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

			//Fill in the comment form
			log.info("INFO: Fill in the comment form");
			
			log.info("INFO: Add a comment for entry");
			driver.switchToFrame().selectFrameByIndex(0);

			driver.getSingleElement(ModerationUI.BlogEntryCommentBox).click();
			blogui.typeText(ModerationUI.BlogEntryCommentBox, commentContent);
			
			driver.switchToFrame().returnToTopFrame();
	
			
			//Submit comment
			log.info("INFO: Submit the comment");
			blogui.clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);

			// Verify that new comment is submitted for approval
			log.info("INFO: Verify that the new blog comment is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.BlogCommentSubmitMessage),
					"ERROR: Entry not submitted");

			blogui.endTest();
		}

		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Pending Ideationblog idea comment in Community</li>
		 * <li><B>Step: </B>Create one Ideationg idea in community by API</li>
		 * <li><B>Step: </B>Login to the community by member user</li>
		 * <li><B>Step: </B>Click the link of new created idea on overview page</li>
		 * <li><B>Verify: </B>Verify the idea is is opened</li>
		 * <li><B>Step: </B>Click "Add a comment" link and add content and click "Save"</li>
		 * <li><B>Verify: </B>Verify the comment is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreatePendingIdeaComment() throws Exception {
			log.info("INFO: test Create idea comment and verify it's submitted for approval in Community");

			String testName = blogui.startTest();
			String commentContent = "IdeaComment" + Helper.genDateBasedRand();

			String ideaTitle = "Idea" + Helper.genDateBasedRand();
			BaseBlogPost blogEntry = new BaseBlogPost.Builder(ideaTitle)
					.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
					.content("Test description for testcase " + testName).build();

			// Add idea to blog
			BlogPost blogPostEntry = apiCommunityOwner
					.createIdea(blogEntry, comAPI);
			Assert.assertTrue(blogPostEntry != null,
					"Failed to create idea in community using API.");


			// GUI
			// Log community member to community and create one pending comment
			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);

			String entryLink = "link=" + ideaTitle;

			// Click on the blogs entry link on the overview page
			log.info("INFO: Click the entry link");
			communityui.clickLink(entryLink);

			// Verify that the community blog opens and New Entry button displays
			log.info("INFO: Verify that the blog entry opens and 'Add a Comment' displays");

			Assert.assertTrue(driver.isTextPresent("Add a Comment"),
					"ERROR: 'Add a comment' text not found in the opened blog");

			// Add an comment to Entry
			log.info("INFO: Add a new comment to entry");
			
			//Click on the Add a comment link for entry
			log.info("INFO: Select the Add a comment link for entry");
			blogui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

			log.info("INFO: Add a comment for entry");
			driver.switchToFrame().selectFrameByIndex(0);

			driver.getSingleElement(ModerationUI.BlogEntryCommentBox).click();
			blogui.typeText(ModerationUI.BlogEntryCommentBox, commentContent);
			
			driver.switchToFrame().returnToTopFrame();
			
			
			//Submit comment
			log.info("INFO: Submit the comment");
			blogui.clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);

			// Verify that new comment is submitted for approval
			log.info("INFO: Verify that the new blog comment is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.BlogCommentSubmitMessage),
					"ERROR: Entry not submitted");

			blogui.endTest();

		}


		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Pending File comment in Community</li>
		 * <li><B>Step: </B>Upload one file in community by API</li>
		 * <li><B>Step: </B>Login to the community by member user</li>
		 * <li><B>Step: </B>Click the link of new uploaded file on overview page</li>
		 * <li><B>Verify: </B>Verify the file overview page is is opened</li>
		 * <li><B>Step: </B>Click "Add a comment" link and add content and click "Save"</li>
		 * <li><B>Verify: </B>Verify the comment is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreatePendingFileComment() throws Exception {
			log.info("INFO: test Create file comment and verify it's submitted for approval in Community");

			fileui.startTest();

			// upload file1 (Disert.jpg)
			String fileName = Data.getData().file1;

			BaseFile baseFile = new BaseFile.Builder(fileName).comFile(true)
					.rename("Desert").extension(".jpg").build();

			File file = new File(filePath);
			apiFileOwner = new APIFileHandler(serverURL, comOwner.getUid(),
					comOwner.getPassword());

			FileEntry fileEntry = apiFileOwner.CreateFile(baseFile, file, comAPI);

			Assert.assertTrue(fileEntry != null,
					"Failed to upload file in community using API.");

			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);

			String entryLink = "link=" + fileName;
			String commentContent = "FileComment" + Helper.genDateBasedRand();

			// Click on the file link on the overview page
			log.info("INFO: Click the entry link");
			communityui.clickLink(entryLink);


			log.info("INFO: Add a comment in file viewer");
			driver.switchToFrame().selectFrameByIndex(0);

			driver.getSingleElement(ModerationUI.FilesViewerCommentBox).click();
			fileui.typeText(ModerationUI.FilesViewerCommentBox, commentContent);
			
			driver.switchToFrame().returnToTopFrame();
			fileui.clickLinkWait(ModerationUI.FilesViewerPostButton);
		
			
			// Verify that new comment is submitted for approval
			log.info("INFO: Verify that the new comment is submitted for approval");
			Assert.assertTrue(fileui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: File comment not submitted");

			fileui.endTest();
		}

		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Flagged Blog Entry and comment in Community</li>
		 * <li><B>Step: </B>Create one Blog entry and then create one comment of it in community by API</li>
		 * <li><B>Step: </B>Login to the community by member user</li>
		 * <li><B>Step: </B>Click the link of new created blog entry on overview page</li>
		 * <li><B>Verify: </B>Verify the entry is opened</li>
		 * <li><B>Step: </B>Click "Flag as inappropriate" link of comment and add content and click "Flag"</li>
		 * <li><B>Verify: </B>Verify the flag of comment is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * <li><B>Step: </B>Click "Flag as inappropriate" link of entry and add content and click "Flag"</li>
		 * <li><B>Verify: </B>Verify the flag of entry is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreateFlaggedEntryAndComment() throws Exception {
			log.info("INFO: test create flagged entry and comment and verify they are submitted for approval in Community");

			String testName = blogui.startTest();

			String entryTitle = "BlogEntry" + Helper.genDateBasedRand();
			String commentContent = "BlogComment" + Helper.genDateBasedRand();

			BaseBlogPost blogEntry = new BaseBlogPost.Builder(entryTitle)
					.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
					.content("Test description for testcase " + testName).build();

			// Add Post to blog
			BlogPost blogPostEntry = apiCommunityOwner.createBlogEntry(blogEntry,
					comAPI);
			apiBlogOwner = new APIBlogsHandler(serverURL, comOwner.getUid(),
					comOwner.getPassword());

			Assert.assertTrue(blogPostEntry != null,
					"Failed to create blog entry in community using API.");

			apiBlogOwner.createBlogComment(commentContent, blogPostEntry);

			// GUI
			// Log community member to community and create one pending comment
			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);

			String entryLink = "link=" + entryTitle;

			// Click on the blogs entry link on the overview page
			log.info("INFO: Click the entry link");
			communityui.clickLink(entryLink);

			// Verify that the community blog opens and New Entry button displays
			log.info("INFO: Verify that the blog entry opens and 'Add a Comment' displays");

			Assert.assertTrue(driver.isTextPresent("Add a Comment"),
					"ERROR: 'Add a comment' text not found in the opened blog");

			// Flag an entry
			log.info("INFO: Flag one blog entry");
			modui.flagBlogEntry();

			// Verify that new entry is submitted for approval
			log.info("INFO: Verify that the new blog entry is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Entry not submitted");

			// Close the submit for review message
			modui.clickLink(ModerationUI.CloseX);

			// Flag an entry comment
			log.info("INFO: Flag one blog entry");
			modui.flagBlogComments();

			// Verify that new entry is submitted for approval
			log.info("INFO: Verify that the new blog entry is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Entry not submitted");

			blogui.endTest();
		}

		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Flagged Ideation idea and comment in Community</li>
		 * <li><B>Step: </B>Create one Ideation idea and then create one comment of it in community by API</li>
		 * <li><B>Step: </B>Login to the community by member user</li>
		 * <li><B>Step: </B>Click the link of new created idea on overview page</li>
		 * <li><B>Verify: </B>Verify the idea is opened</li>
		 * <li><B>Step: </B>Click "Flag as inappropriate" link of comment and add content and click "Flag"</li>
		 * <li><B>Verify: </B>Verify the flag of comment is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * <li><B>Step: </B>Click "Flag as inappropriate" link of entry and add content and click "Flag"</li>
		 * <li><B>Verify: </B>Verify the flag of idea is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreateFlaggedIdeaAndComment() throws Exception {
			log.info("INFO: test Create flagged idea and comment and verify they are submitted for approval in Community");

			String testName = blogui.startTest();
			String commentContent = "IdeaComment" + Helper.genDateBasedRand();

			String ideaTitle = "Idea" + Helper.genDateBasedRand();
			BaseBlogPost blogEntry = new BaseBlogPost.Builder(ideaTitle)
					.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
					.content("Test description for testcase " + testName).build();

			// Add idea to blog
			BlogPost blogPostEntry = apiCommunityOwner
					.createIdea(blogEntry, comAPI);
			apiBlogOwner = new APIBlogsHandler(serverURL, comOwner.getUid(),
					comOwner.getPassword());

			Assert.assertTrue(blogPostEntry != null,
					"Failed to create idea in community using API.");
			apiBlogOwner.createBlogComment(commentContent, blogPostEntry);

			// GUI
			// Log community member to community and create one pending comment
			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);

			String entryLink = "link=" + ideaTitle;

			// Click on the blogs entry link on the overview page
			log.info("INFO: Click the entry link");
			communityui.clickLink(entryLink);

			// Verify that the community blog opens and New Entry button displays
			log.info("INFO: Verify that the blog entry opens and 'Add a Comment' displays");

			Assert.assertTrue(driver.isTextPresent("Add a Comment"),
					"ERROR: 'Add a comment' text not found in the opened blog");

			// Flag an entry
			log.info("INFO: Flag one blog entry");
			modui.flagBlogEntry();

			// Verify that new entry is submitted for approval
			log.info("INFO: Verify that the new blog entry is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Entry not submitted");

			// Close the submit for review message
			modui.clickLink(ModerationUI.CloseX);

			// Flag an entry comment
			log.info("INFO: Flag one blog entry comment");
			modui.flagBlogComments();

			// Verify that new entry is submitted for approval
			log.info("INFO: Verify that the new blog entry is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Entry not submitted");

			blogui.endTest();
		}

		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Flagged Forum topic in Community</li>
		 * <li><B>Step: </B>Create one Forum topic in community by API</li>
		 * <li><B>Step: </B>Login to the community by member user</li>
		 * <li><B>Step: </B>Click the link of new created forum topic on overview page</li>
		 * <li><B>Verify: </B>Verify the topic is is opened</li>
		 * <li><B>Step: </B>Click "Flag as inappropriate" link of topic and add content and click "Flag"</li>
		 * <li><B>Verify: </B>Verify the flag of topic is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreateFlaggedTopic() throws Exception {
			log.info("INFO: test Create flagged forum topic and verify it's submitted for approval in Community");

			forumui.startTest();

			String topicTitle = "Topic" + Helper.genDateBasedRand();

			BaseForumTopic topic = new BaseForumTopic.Builder(topicTitle)
					.tags(Data.getData().commonTag)
					.description(Data.getData().commonDescription)
					.partOfCommunity(community).build();

			ForumTopic forumTopic = apiCommunityOwner.CreateForumTopic(comAPI,
					topic);
			Assert.assertTrue(forumTopic != null,
					"Failed to create Forum topic in community using API.");

			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);

			String entryLink = "link=" + topicTitle;
			// Click on the blogs entry link on the overview page
			log.info("INFO: Click the entry link");
			communityui.clickLink(entryLink);

			// Verify that the community blog opens and New Entry button displays
			log.info("INFO: Verify that the blog entry opens and 'Flag as Inappropriate' displays");

			Assert.assertTrue(driver.isTextPresent("Flag as Inappropriate"),
					"ERROR: 'Flag as Inappropriate' text not found in the opened blog");

			// Add an Entry
			log.info("INFO: Add a new entry to the blog");
			modui.flagForumPosts();

			// Verify that new entry is submitted for approval
			log.info("INFO: Verify that the new blog entry is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Entry not submitted");

			forumui.endTest();
		}

		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Flagged file and comment in Community</li>
		 * <li><B>Step: </B>Upload one file and then create one comment of it in community by API</li>
		 * <li><B>Step: </B>Login to the community by member user</li>
		 * <li><B>Step: </B>Click the link of new uploaded file on overview page</li>
		 * <li><B>Verify: </B>Verify the file is opened</li>
		 * <li><B>Step: </B>Click "Flag as inappropriate" link of comment and add content and click "Flag"</li>
		 * <li><B>Verify: </B>Verify the flag of comment is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * <li><B>Step: </B>Click "Flag as inappropriate" link of entry and add content and click "Flag"</li>
		 * <li><B>Verify: </B>Verify the flag of file is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreateFlaggedFileAndComment() throws Exception {
			log.info("INFO: test Create flagged file/comment and verify they are sumbitted for approval in Community");

			modui.startTest();

			// upload file1 (Disert.jpg)
			String fileName = Data.getData().file1;
			String commentContent = "FileComment" + Helper.genDateBasedRand();

			BaseFile baseFile = new BaseFile.Builder(fileName).comFile(true)
					.rename("Desert").extension(".jpg").build();

			File file = new File(filePath);
			apiFileOwner = new APIFileHandler(serverURL, comOwner.getUid(),
					comOwner.getPassword());
			FileEntry fileEntry = apiFileOwner.CreateFile(baseFile, file, comAPI);
			Assert.assertTrue(fileEntry != null,
					"Failed to upload file in community using API.");

			FileComment fileComment = new FileComment(commentContent);
			apiFileOwner.CreateFileComment(fileEntry, fileComment, comAPI);

			communityui.loadComponent(Data.getData().ComponentCommunities);
			communityui.login(comMember);

			// navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(communityui);

			String entryLink = "link=" + fileName;
			// Click on the blogs entry link on the overview page
			log.info("INFO: Click the entry link");
			communityui.clickLink(entryLink);

			// Verify that the community blog opens and New Entry button displays
			log.info("INFO: Verify that the file page opens and comment content displays");

			Assert.assertTrue(driver.isTextPresent(commentContent),
					"ERROR: content not found in the opened file page");

			modui.flagFileComments();

			// Verify that flagged comment is submitted for approval
			log.info("INFO: Verify that the flagged comment is submitted for approval");
			Assert.assertTrue(fileui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: File comment not submitted");

			// Close the submit for review message
			modui.clickLink(ModerationUI.CloseX);

			// flag file
			log.info("INFO: Flag the file");
			modui.flagFile();

			// Verify that new comment is submitted for approval
			log.info("INFO: Verify that the flagged file is submitted for approval");
			Assert.assertTrue(modui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Flagged File not submitted");

			modui.endTest();
		}
		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Pending Blog Entry comment for standalone blog</li>
		 * <li><B>Step: </B>Create one Blog by API and then create one entry in it by API</li>
		 * <li><B>Step: </B>Login to the blog by blog owner and add one member in blog. Then logout the owner</li>
		 * <li><B>Step: </B>Login to the blog by member user</li>
		 * <li><B>Step: </B>Click the link of new created blog entry</li>
		 * <li><B>Verify: </B>Verify the entry is is opened</li>
		 * <li><B>Step: </B>Click "Add a comment" link and add content and click "Save"</li>
		 * <li><B>Verify: </B>Verify the comment is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * <li><B>Step: </B>Click "New entry" link and add content and click "Save"</li>
		 * <li><B>Verify: </B>Verify the entry is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreatePendingEntryAndComment_SA() throws Exception {
			log.info("INFO: test create pending entry&comment and verify they are submitted for approval in standalone blog");

			String testName = blogui.startTest();
			
			apiBlogOwner = new APIBlogsHandler(serverURL, comOwner.getUid(),
					comOwner.getPassword());

			String blogName = "SABlog"+Helper.genDateBasedRand();
			BaseBlog baseBlog = new BaseBlog.Builder(blogName, Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
					.tags("HPBlogtag" + Helper.genDateBasedRand() + " blogtag")
					.description("Test description for testcase ")
					.build();
			Blog blog = apiBlogOwner.createBlog(baseBlog);
			
			String ownerEntryTitle = "OwnerEntry" + Helper.genDateBasedRand();
			String authorEntryTitle = "AuthorEntry" + Helper.genDateBasedRand();
			String commentContent = "BlogComment" + Helper.genDateBasedRand();
			
			BaseBlogPost ownerEntry = new BaseBlogPost.Builder(ownerEntryTitle)
			.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
			.content("Test description for testcase " + testName).build();
			
			BaseBlogPost authorEntry = new BaseBlogPost.Builder(authorEntryTitle)
			.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
			.content("Test description for testcase " + testName).build();


			// Add Post to blog
			apiBlogOwner.createBlogEntry(ownerEntry, blog);
			
			
			Member blogAuthor = new Member(BlogRole.AUTHOR, comMember);

			//Load the component
			blogui.loadComponent(Data.getData().ComponentBlogs);
			blogui.login(comOwner);
			
			log.info("INFO: Navigate to My Blogs");
			blogui.clickLinkWait(BlogsUIConstants.MyBlogs);
			
			blogui.clickLinkWait("link=" + blogName);

			//Access the blog settings page
			log.info("INFO: Select Manage Blog");
			blogui.clickLinkWait(BlogsUIConstants.blogsSettings);
			
			//Navigate to Authors page
			log.info("INFO: Navigate to Authors left navigation choice");
			BlogSettings_LeftNav_Menu.AUTHORS.select(blogui);

		
			log.info("INFO: Add an author member to the blog");
			blogui.addMember(blogAuthor);
			
			//Verify author and drafter were added
			log.info("INFO Validate that the author was added to the blog members");
			Assert.assertTrue(blogui.fluentWaitPresent(BlogsUI.getBlogMember(comMember)),
							  "ERROR: Author was not added");
			

			blogui.logout();
			driver.close();
			
			// go back to the blog and log in with the author, make an entry
			blogui.loadComponent(Data.getData().ComponentBlogs);
			blogui.login(comMember);
			
			//Navigate to the page to create a new blog entry for the blog
			log.info("INFO: Navigate to the page and click new entry");
			blogui.clickLinkWait("link="+blogName);
			
			String ownerEntryLink = "link=" + ownerEntryTitle;

			// Click on the blogs entry link on the overview page
			log.info("INFO: Click the entry link");
			communityui.clickLink(ownerEntryLink);

			// Verify that the blog opens and New Entry button displays
			log.info("INFO: Verify that the blog entry opens and 'Add a Comment' displays");

			Assert.assertTrue(driver.isTextPresent("Add a Comment"),
					"ERROR: 'New Entry' text not found in the opened blog");

			// Add an comment to Entry
			log.info("INFO: Add a new comment to entry");
			
			//Click on the Add a comment link for entry
			log.info("INFO: Select the Add a comment link for entry");
			blogui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

			//Fill in the comment form
			log.info("INFO: Fill in the comment form");
			driver.switchToFrame().selectFrameByIndex(0);

			driver.getSingleElement(ModerationUI.BlogEntryCommentBox).click();
			blogui.typeText(ModerationUI.BlogEntryCommentBox, commentContent);
			
			driver.switchToFrame().returnToTopFrame();
	
			
			//Submit comment
			log.info("INFO: Submit the comment");
			blogui.clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);

			// Verify that new comment is submitted for approval
			log.info("INFO: Verify that the new blog entry comment is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.BlogCommentSubmitMessage),
					"ERROR: comment not submitted");
			
			blogui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
			
			// Add an Entry
			log.info("INFO: Add a new entry to the blog");
			authorEntry.create(blogui);

			// Verify that new entry is submitted for approval
			log.info("INFO: Verify that the new blog entry is submitted for approval");
			Assert.assertTrue(
					blogui.fluentWaitTextPresent(ModerationUI.BlogSubmitMessage),
					"ERROR: Entry not submitted");

			blogui.endTest();

		}
		
		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Flagged Blog Entry and comment in StandAlone blog</li>
		 * <li><B>Step: </B>Create one Blog by API, and then create one entry and one comment of it in the blog by API</li>
		 * <li><B>Step: </B>Login to the blog by the blog owner</li>
		 * <li><B>Step: </B>Click the link of new created blog</li>
		 * <li><B>Verify: </B>Click the blog page is opened</li>
		 * <li><B>Step: </B>Click the link of new created blog entry</li>
		 * <li><B>Verify: </B>Verify the entry is opened</li>
		 * <li><B>Step: </B>Click "Flag as inappropriate" link of comment and add content and click "Flag"</li>
		 * <li><B>Verify: </B>Verify the flag of comment is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * <li><B>Step: </B>Click "Flag as inappropriate" link of entry and add content and click "Flag"</li>
		 * <li><B>Verify: </B>Verify the flag of entry is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreateFlaggedEntryAndComment_SA() throws Exception {
			log.info("INFO: test create flagged entry and comment and verify they are submitted for approval in Community");

			String testName = blogui.startTest();
			
			apiBlogOwner = new APIBlogsHandler(serverURL, comOwner.getUid(),
					comOwner.getPassword());

			String blogName = "SABlog"+Helper.genDateBasedRand();
			BaseBlog baseBlog = new BaseBlog.Builder(blogName, Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
					.tags("HPBlogtag" + Helper.genDateBasedRand() + " blogtag")
					.description("Test description for testcase ")
					.build();
			Blog blog = apiBlogOwner.createBlog(baseBlog);
			
			String entryTitle = "BlogEntry" + Helper.genDateBasedRand();
			String commentContent = "BlogComment" + Helper.genDateBasedRand();
			
			BaseBlogPost blogEntry = new BaseBlogPost.Builder(entryTitle)
					.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
					.content("Test description for testcase " + testName).build();

			// Add Post to blog
			BlogPost blogPost = apiBlogOwner.createBlogEntry(blogEntry, blog);

		
			Assert.assertTrue(blogPost != null,
					"Failed to create blog entry in community using API.");

			apiBlogOwner.createBlogComment(commentContent, blogPost);

			// GUI
			// Log the member to blog and create one pending comment
			blogui.loadComponent(Data.getData().ComponentBlogs);
			blogui.login(comOwner);

			blogui.clickLinkWait("link="+blogName);
			
			String entryLink = "link=" + entryTitle;

			// Click on the blogs entry link on the overview page
			log.info("INFO: Click the entry link");
			communityui.clickLink(entryLink);

			// Verify that the standalone blog opens and New Entry button displays
			log.info("INFO: Verify that the blog entry opens and 'Add a Comment' displays");

			Assert.assertTrue(driver.isTextPresent("Add a Comment"),
					"ERROR: 'Add a comment' text not found in the opened blog");

			// Flag an entry
			log.info("INFO: Flag one blog entry");
			modui.flagBlogEntry();

			// Verify that new entry is submitted for approval
			log.info("INFO: Verify that the new blog entry is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Entry not submitted");

			// Close the submit for review message
			modui.clickLink(ModerationUI.CloseX);

			// Flag an entry comment
			log.info("INFO: Flag one blog entry");
			modui.flagBlogComments();

			// Verify that new entry is submitted for approval
			log.info("INFO: Verify that the new blog entry is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Entry not submitted");

			blogui.endTest();
		}
		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Pending Forums topic in standalone forum</li>
		 * <li><B>Step: </B>Create one standalone Forum by API</li>
		 * <li><B>Step: </B>Create one forum topic in the new create forum by API</li>
		 * <li><B>Step: </B>Login to the standalone forum by member user</li>
		 * <li><B>Step: </B>Click "Start a topic" link in forum and create one forum topic</li>
		 * <li><B>Verify: </B>Verify the topic is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */		
		@Test(groups = { "regression" } , enabled=false )
		public void testCreatePendingTopic_SA() throws Exception {
			log.info("INFO: test Create forum topic and it is submitted for approval in Community");

			modui.startTest();

			String TopicTitle = "Topic" + Helper.genDateBasedRand();

		
			apiForumOwner = new APIForumsHandler(serverURL, comOwner.getUid(),
					comOwner.getPassword());
			
			String forumName = "SAForum" + Helper.genDateBasedRandVal();
			
			//Create one standalone Forum using API
			BaseForum baseForum = new BaseForum.Builder(forumName)
					.tags(Data.getData().commonTag + Helper.genDateBasedRand())
					.description(forumName)
					.build();

			log.info("INFO: " + comOwner.getDisplayName() + " creating forum (API)");
			apiForumOwner.createForum(baseForum); 

			
			communityui.loadComponent(Data.getData().ComponentForums);
			communityui.login(comMember);

			log.info("INFO: Select left menu option Public Forums");
			forumui.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);
			
			// Click link to the page of the forum created above
			log.info("INFO: Select the forum created above");
			forumui.clickLinkWait("link=" + forumName);

			// Click on the "start first topic" link
			log.info("INFO: Click 'Start a Topic' link");
			forumui.clickLink(ForumsUIConstants.Start_A_Topic);

			// Verify that "new topic" form opened
			log.info("INFO: Verify that the new entry forum opens and New Entry text displays");

			Assert.assertTrue(driver.isTextPresent("Start a Topic"),
					"ERROR: 'Start a Topic' text not found in the opened blog");

			// Add an topic
			log.info("INFO: Add a new topic to the community forum");

			// Add title
			log.info("INFO: Entering title of new forum topic");
			this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputText_Title)
					.type(TopicTitle);

			// Enter forum topic tags if provided
			log.info("INFO: Entering any forum topic tags");
			this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputText_Tags)
						.type(TopicTitle);

			// Enter forum topic description if provided
			log.info("INFO: Entering forum topic description");
			modui.typeNativeInCkEditor(TopicTitle + " description");

			// Select Save
			log.info("INFO: Attempting to save new topic");
			modui.clickLink(ForumsUIConstants.Save_Forum_Topic_Button);

			// Verify that new topic is submitted for approval
			log.info("INFO: Verify that the new topic is submitted for approval");
			Assert.assertTrue(modui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Topic not submitted");

			modui.endTest();
		}

		
		/**
		 * <ul>
		 * <li><B>Info: </B>Test Creating Flagged Forum topic in standalone forum</li>
		 * <li><B>Step: </B>Create one standalone forum by API and then create one topic in this forum by API</li>
		 * <li><B>Step: </B>Login to the standalone forum by the forum owner</li>
		 * <li><B>Step: </B>Click the link of new created forum topic in forum</li>
		 * <li><B>Verify: </B>Verify the topic is is opened</li>
		 * <li><B>Step: </B>Click "Flag as inappropriate" link of topic and add content and click "Flag"</li>
		 * <li><B>Verify: </B>Verify the flag of topic is created successfully without error</li>
		 * <li><B>Verify: </B>Verify there's successful message "submit for review"</li>
		 * </ul>
		 * 
		 * @throws Exception
		 */
		@Test(groups = { "regression" } , enabled=false )
		public void testCreateFlaggedTopic_SA() throws Exception {
			log.info("INFO: test Create flagged standalone forum topic and verify it's submitted for approval");

			forumui.startTest();

			apiForumOwner = new APIForumsHandler(serverURL, comOwner.getUid(),
					comOwner.getPassword());
			
			String forumName = "SAForum" + Helper.genDateBasedRandVal();
			
			//Create one standalone Forum using API
			BaseForum baseForum = new BaseForum.Builder(forumName)
					.tags(Data.getData().commonTag + Helper.genDateBasedRand())
					.description(forumName)
					.build();

			log.info("INFO: " + comOwner.getDisplayName() + " creating forum (API)");
			Forum forum = apiForumOwner.createForum(baseForum); 
			
			String topicTitle = "Topic" + Helper.genDateBasedRand();

			BaseForumTopic baseTopic = new BaseForumTopic.Builder(topicTitle)
				.parentForum(forum)
				.tags("ModerationForum").description(topicTitle).build();

			ForumTopic forumTopic = apiForumOwner.createForumTopic(baseTopic);
			Assert.assertTrue(forumTopic != null,
					"Failed to create standalone Forum topic using API.");

			communityui.loadComponent(Data.getData().ComponentForums);
			communityui.login(comOwner);

			log.info("INFO: Select left menu option Public Forums");
			forumui.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);
			
			// Click link to the page of the forum created above
			log.info("INFO: Select the forum created above");
			forumui.clickLinkWait("link=" + forumName);

			String entryLink = "link=" + topicTitle;
			// Click on the forums topic link on the overview page
			log.info("INFO: Click the entry link");
			communityui.clickLink(entryLink);

			// Verify that the community blog opens and New Entry button displays
			log.info("INFO: Verify that the topic opens and 'Flag as Inappropriate' displays");

			Assert.assertTrue(driver.isTextPresent("Flag as Inappropriate"),
					"ERROR: 'Flag as Inappropriate' text not found in the opened blog");

			// Add an Entry
			log.info("INFO: Flag the new topic as inappropriate");
			modui.flagForumPosts();

			// Verify that new entry is submitted for approval
			log.info("INFO: Verify that the flagged forum topic is submitted for approval");
			Assert.assertTrue(blogui
					.fluentWaitTextPresent(ModerationUI.SubmitForReveiwMessage),
					"ERROR: Flagged topic not submitted");

			forumui.endTest();
		}
		

}
