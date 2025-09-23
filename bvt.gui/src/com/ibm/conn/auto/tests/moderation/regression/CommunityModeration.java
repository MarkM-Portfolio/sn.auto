package com.ibm.conn.auto.tests.moderation.regression;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Moderation_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ModerationUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class CommunityModeration extends SetUpMethods2 {

	private static Logger log = LoggerFactory
			.getLogger(CommunityModeration.class);

	private CommunitiesUI communityui;
	private ModerationUI modui;
	private TestConfigCustom cfg;
	private User comOwner, comMember;
	private BaseCommunity community;
	private APICommunitiesHandler apiCommunityOwner;
	private APICommunitiesHandler apiCommunityMember;
	private APIFileHandler apiFileMember, apiFileOwner;
	private APIBlogsHandler apiBlogOwner, apiBlogMember;
	private APIForumsHandler apiForumOwner, apiForumMember;
	private BaseCommunity.Access defaultAccess;
	private String communityName;
	private Community comAPI;
	private String serverURL;

	private String filePath;

	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		communityui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		modui = ModerationUI.getGui(cfg.getProductName(), driver);

		communityName = Helper.stamp(Data.getData().WF_CommunityName);

		filePath = cfg
				.getTestConfig()
				.getBrowserEnvironment()
				.getAbsoluteFilePath(cfg.getUploadFilesDir(),
						Data.getData().file1);

		// Load User
		comOwner = cfg.getUserAllocator().getUser();
		log.info("INFO: Using test user: " + comOwner.getDisplayName());
		comMember = cfg.getUserAllocator().getUser();
		log.info("INFO: Using test user: " + comMember.getDisplayName());

		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiCommunityOwner = new APICommunitiesHandler(serverURL,
				comOwner.getUid(), comOwner.getPassword());

		Member member1 = new Member(CommunityRole.MEMBERS, comMember);
		community = new BaseCommunity.Builder(communityName)
				.access(defaultAccess).addMember(member1)
				.description("Test Community Moderation").build();

		// create community
		log.info("INFO: Create community using API");
		comAPI = community.createAPI(apiCommunityOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommunityOwner, comAPI);

		// add the blog widget using API
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
	 * <li><B>Info: </B>Test verifying Pre-moderation(Approve/Reject/Delete) actions for Blog Entry in Community</li>
	 * <li><B>Step: </B>Preparing data by creating 4 blog entries by API using community member</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "REJECTED" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testBlogEntryPreModeration() throws Exception {
		log.info("INFO: test Blog entry Pre-moderation");

		modui.startTest();

		String[] entryTitles = new String[4];
		String title = "Entry_Pre" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			entryTitles[i] = title + "_" + Integer.toString(i);
		}

		//Prepare the pending community blog entries 
		prepareBlogEntryDataPre(entryTitles);

		//GUI
		// navigate to the created community Moderation
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		//Verify pre-modration of blog entries
		log.info("INFO: verify pre-moderation actions of blog entry");
		modui.verifyPreModeration(entryTitles);

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Pre-moderation(Approve/Reject/Delete) actions for Blog Comment in Community</li>
	 * <li><B>Step: </B>Preparing data by creating 1 blog entry by community owner and then create 4 comments of it by API using community member</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Content Approval -> Blogs ->Comments" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "REJECTED" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	
	@Test(groups = { "regression" } , enabled=false )
	public void testBlogCommentPreModeration() throws Exception {
		log.info("INFO: test Blog comment Pre-moderation");

		modui.startTest();

		String[] entryComments = new String[4];
		String comment = "Comment_pre" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			entryComments[i] = comment + "_" + Integer.toString(i);
		}

		//prepare blog comments for pre-moderation test
		log.info("INFO: prepare blog comments for pre-moderation test");
		prepareBlogCommentDataPre(entryComments);

		//GUI
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);
		
		//open the content approval -> Blogs -> Comments view
		Moderation_LeftNav_Menu.CABLOGSCOMMENTS.select(modui);
		
		//verify pre-moderation for blog entry comments
		log.info("INFO: verify pre-moderation for blog entry comments");
		modui.verifyPreModeration(entryComments);

		modui.endTest();
	}

	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Post-moderation(Quarantine/Dismiss/Delete) actions for Blog Entry in Community</li>
	 * <li><B>Step: </B>Preparing data by creating 4 blog entries, and then flag the entries by API using community owner</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Flagged Content -> Blogs ->Entries" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is Dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testBlogEntryPostModeration() throws Exception {
		log.info("INFO: test Blog entry Post-moderation");

		modui.startTest();

		String[] entryTitles = new String[4];
		String title = "Blog_Post" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			entryTitles[i] = title + "_" + Integer.toString(i);
		}

		//prepare blog entries for post-moderation test
		log.info("INFO: prepare blog entries for post-moderation test");
		prepareBlogEntryDataPost(entryTitles);

		//GUI
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);
		
		//open the Flagged Content->Blogs->Entries view
		Moderation_LeftNav_Menu.FCBLOGSENTRIES.select(modui);

		log.info("INFO: verify post-moderation of blog entry");
		modui.verifyPostModeration(entryTitles);

		modui.endTest();

	}

	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Post-moderation(Quarantine/Dismiss/Delete) actions for Blog comment in Community</li>
	 * <li><B>Step: </B>Preparing data by creating 1 blog entry&4 comments and then flag 4 comments by API using community owner</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Flagged Content -> Blogs -> Comments" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is Dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testBlogCommentPostModeration() throws Exception {
		log.info("INFO: test Blog comment Post-moderation");

		String[] entryComments = new String[4];
		String comment = "BlogComment_Post" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			entryComments[i] = comment + "_" + Integer.toString(i);
		}

		//prepare blog comments for post-moderation test
		log.info("INFO: prepare blog comments for post-moderation test");
		prepareBlogCommentDataPost(entryComments);

		// GUI
		communityui.loadComponent(Data.getData().ComponentCommunities);
		communityui.login(comOwner);
		communityui.clickLinkWait("link="+communityName);
		
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);
		
		//go to Flagged Content->Blogs->Comments view
		Moderation_LeftNav_Menu.FCBLOGSCOMMENTS.select(modui);

		log.info("INFO: verify post-moderation of blog comments");
		modui.verifyPostModeration(entryComments);

		modui.endTest();
	}

	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Pre-moderation(Approve/Reject/Delete) actions for Ideation idea in Community</li>
	 * <li><B>Step: </B>Preparing data by creating 4 ideas by API using community member</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "REJECTED" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testIdeaPreModeration() throws Exception {
		log.info("INFO: test Ideationblog idea Pre-moderation");

		modui.startTest();
		String[] ideaTitles = new String[4];
		String title = "Idea_pre" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			ideaTitles[i] = title + "_" + Integer.toString(i);
		}

		//prepare ideationblog ideas for pre-moderation test
		log.info("INFO: prepare ideationblog ideas for pre-moderation test");
		prepareIdeaDataPre(ideaTitles);

		// GUI
		// Login to the community
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		log.info("INFO: verify pre-modration for ideationblog ideas");
		modui.verifyPreModeration(ideaTitles);

		modui.endTest();
	}

	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Pre-moderation(Approve/Reject/Delete) actions for Ideation idea in Community</li>
	 * <li><B>Step: </B>Preparing data by creating 1 idea by community owner and then create 4 comments of it by API using community member</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Content Approval -> Blogs ->Comments" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "REJECTED" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testIdeaCommentPreModeration() throws Exception {
		log.info("INFO: test ideationBlogs comments Pre-moderation");
		modui.startTest();

		String[] ideaComments = new String[4];
		String comment = "IdeaComment_pre" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			ideaComments[i] = comment + "_" + Integer.toString(i);
		}

		//prepare ideationblog comments for pre-moderation test
		log.info("INFO: prepare ideationblog comments for pre-moderation test");
		prepareBlogCommentDataPre(ideaComments);

		// GUI
		// Login to the community
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);
		
		// Go to Content Approval->Blogs->Comments view
		log.info("INFO: Go to Content Approval Blogs Comments view");
		Moderation_LeftNav_Menu.CABLOGSCOMMENTS.select(modui);

		// Verify all pre-moderation actions
		log.info("INFO: verify all pre-moderation actions");
		modui.verifyPreModeration(ideaComments);

		modui.endTest();

	}

	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Post-moderation(Quarantine/Dismiss/Delete) actions for Ideation idea in Community</li>
	 * <li><B>Step: </B>Preparing data by creating 4 ideas, and then flag the ideas by API using community owner</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Flagged Content -> Blogs ->Entries" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is Dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testIdeaPostModeration() throws Exception {
		log.info("INFO: test IdeaBlog idea Post-moderation");

		modui.startTest();

		String[] entryTitles = new String[4];
		String title = "IdeaPost " + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			entryTitles[i] = title + "_" + Integer.toString(i);
		}

		//prepare ideationblog comments for post-moderation test
		log.info("INFO: prepare ideationblog comments for post-moderation test");
		prepareIdeaDataPost(entryTitles);

		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		//go to Flagged content->Blogs->Entries
		Moderation_LeftNav_Menu.FCBLOGSENTRIES.select(modui);

		log.info("INFO: verify post-moderation of ideation blog ideas");
		modui.verifyPostModeration(entryTitles);

		modui.endTest();

	}

	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Post-moderation(Quarantine/Dismiss/Delete) actions for Ideation comment in Community</li>
	 * <li><B>Step: </B>Preparing data by creating 1 idea & 4 comments and then flag 4 comments by API using community owner</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Flagged Content -> Blogs -> Comments" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is Dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testIdeaCommentPostModeration() throws Exception {
		log.info("INFO: test Ideationblog comment post-moderation");

		String[] entryComments = new String[4];
		String comment = "IdeaCommentPost" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			entryComments[i] = comment + "_" + Integer.toString(i);
		}

		//prepare ideationblog comments for post-moderation test
		log.info("INFO: prepare ideationblog comments for post-moderation test");
		prepareIdeaCommentDataPost(entryComments);

		// GUI
		// Log community member to community and create one pending comment
		communityui.loadComponent(Data.getData().ComponentCommunities);
		communityui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		//go to Flagged Content ->Blogs -> Comments view
		Moderation_LeftNav_Menu.FCBLOGSCOMMENTS.select(modui);

		log.info("INFO: verify post-moderation of ideation blog comments");
		modui.verifyPostModeration(entryComments);

		modui.endTest();
	}

	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Pre-moderation(Approve/Reject/Delete) actions for Forums Topic in Community</li>
	 * <li><B>Step: </B>Preparing data by creating 4 Forums topics by API using community member</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Content approval -> Forums -> Posts" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "REJECTED" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testForumTopicPreModeration() throws Exception {
		log.info("INFO: test Forum Topic Pre-moderation");

		modui.startTest();

		String[] topics = new String[4];
		String topic = "Topic_pre" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			topics[i] = topic + "_" + Integer.toString(i);
		}

		//prepare forum post for pre-moderation test
		log.info("INFO: prepare forum posts for pre-moderation test");
		prepareForumTopicDataPre(topics);

		// GUI
		// Login to the community
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		// Go to Content Approval->Forums -> Posts view
		log.info("INFO: Go to Content Approval -> Forums Posts view");
		Moderation_LeftNav_Menu.CAFORUMSPOSTS.select(modui);

		// Verify all pre-moderation actions
		log.info("INFO: verify all pre-moderation actions");
		modui.verifyPreModeration(topics);

		modui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Post-moderation(Quarantine/Dismiss/Delete) actions for Forums topic in Community</li>
	 * <li><B>Step: </B>Preparing data by creating 4 forums topics and then flag 4 topics by API using community owner</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Flagged Content -> Forums -> Posts" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is Dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testForumTopicPostModeration() throws Exception {
		log.info("INFO: test Forum posts Post-moderation");

		modui.startTest();

		String[] topics = new String[4];
		String topic = "TopicPost" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			topics[i] = topic + "_" + Integer.toString(i);
		}

		//prepare Forum posts for post-moderation test
		log.info("INFO: prepare Forums post for post-moderation test");
		prepareForumTopicDataPost(topics);

		// GUI
		// Login to the community
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		//go to Flagged Content->Forums->Posts view
		Moderation_LeftNav_Menu.FCFORUMSPOSTS.select(modui);

		log.info("INFO: Veirify post-moderation actions of forums posts");
		modui.verifyPostModeration(topics);

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Pre-moderation(Approve/Reject/Delete) actions for Files file in Community</li>
	 * <li><B>Step: </B>Preparing data by uploading 4 files by API using community membber</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Content Approval -> Files -> Files" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "REJECTED" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testFilePreModeration() throws Exception {
		log.info("INFO: test File Pre-moderation");

		modui.startTest();

		// the uploaded file is "file1"(Desert.jpg)
		String file = new String();
		file = Data.getData().file1;

		String fileNames[] = new String[4];

		String title = "File_Pre" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			fileNames[i] = title + "_" + Integer.toString(i);
		}

		//prepare Files for pre-moderation test
		log.info("INFO: prepare files for pre-moderation test");
		prepareFileDataPre(file, fileNames);

		for (int i = 0; i < 4; i++) {
			fileNames[i] = fileNames[i] + ".jpg";
		}
		// GUI
		// Login to the community
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		// Go to Content Approval->Files->Content view
		log.info("INFO: Go to Content Approval -> Files ->Content view");
		Moderation_LeftNav_Menu.CAFILESCONTENT.select(modui);

		// Verify all pre-moderation actions
		log.info("INFO: verify all pre-moderation actions");
		modui.verifyPreModeration(fileNames);

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Pre-moderation(Approve/Reject/Delete) actions for Files Comment in Community</li>
	 * <li><B>Step: </B>Preparing data by uploading 1 file by community owner and then create 4 comments of it by community member by API</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Content Approval -> Files ->Comments" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Reject" button</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "REJECTED" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Approve" button</li>
	 * <li><B>Verify: </B>Verify the item is approved successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	
	@Test(groups = { "regression" })
	public void testFileCommentPreModeration() throws Exception {
		log.info("INFO: test Files Comment Pre-moderation");

		modui.startTest();

		String[] comments = new String[4];

		String comment = "FileComment_Pre" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			comments[i] = comment + "_" + Integer.toString(i);
		}

		//prepare Files comments for pre-moderation test
		log.info("INFO: prepare Files comments for pre-moderation test");
		prepareFileCommentDataPre(comments);

		// GUI
		// Login to the community
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		// Go to Content Approval->Files->Comments view
		log.info("INFO: Go to Content Approval -> Files->comments view");
		Moderation_LeftNav_Menu.CAFILESCOMMENTS.select(modui);

		//Verify the pending comments list here
		Assert.assertTrue(modui.fluentWaitTextPresent(comments[0]), "Pending file comment "+comments[0]+ "not found");
		
		// Verify all pre-moderation actions
		log.info("INFO: verify all pre-moderation actions");
		modui.verifyPreModeration(comments);

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Post-moderation(Quarantine/Dismiss/Delete) actions for Files file in Community</li>
	 * <li><B>Step: </B>Preparing data by uploading 4 files, and then flag the files by API using community owner</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Flagged Content -> Files ->Files" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is Dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testFilePostModeration() throws Exception {
		log.info("INFO: test File Post-moderation");

		modui.startTest();

		String[] fileNames = new String[4];
		String name = "File_post" + Helper.genDateBasedRand();
		for (int i = 0; i < 4; i++) {
			fileNames[i] = name + "_" + Integer.toString(i);
		}

		//prepare flagged files for post-moderation test
		log.info("INFO: prepare flagged files for post-moderation test");
		prepareFileDataPost(fileNames);

		// GUI
		// Login to the community
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		//go to Flagged Content->Files-Content view
		Moderation_LeftNav_Menu.FCFILESCONTENT.select(modui);

		// Verify all post-moderation actions
		log.info("INFO: verify all post-moderation actions");
		modui.verifyPostModeration(fileNames);

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test verifying Post-moderation(Quarantine/Dismiss/Delete) actions for Files comment in Community</li>
	 * <li><B>Step: </B>Preparing data by uploading 1 file & 4 comments, and then flag the comments by API using community owner</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Flagged Content -> Files ->Comments" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 4 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item1" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is Dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Quarantine" button</li>
	 * <li><B>Verify: </B>Verify the item is quarantined successfully without error</li>
	 * <li><B>Step: </B>Click "item4" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the "item2" and "item3" are listed on Moderation page</li>
	 * <li><B>Step: </B>Click "item2" link to open the landing page of it and click "Dismiss" button</li>
	 * <li><B>Verify: </B>Verify the item is dismissed successfully without error</li>
	 * <li><B>Step: </B>Click "item3" link to open the landing page of it and click "Delete" button</li>
	 * <li><B>Verify: </B>Verify the item is deleted successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testFileCommentPostModeration() throws Exception {
		log.info("INFO: test Files comments Post-moderation");

		modui.startTest();

		String[] comments = new String[4];
		String comment = "FileComment_post" + Helper.genDateBasedRand();

		for (int i = 0; i < 4; i++) {
			comments[i] = comment + "_" + Integer.toString(i);
		}

		//prepare flagged files comments for post-moderation test
		log.info("INFO: prepare flagged files comments for post-moderation test");
		prepareFileCommentDataPost(comments);

		// GUI
		// Login to the community
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		//go to Flagged Content->Files-Comments view
		Moderation_LeftNav_Menu.FCFILESCOMMENTS.select(modui);

		//Verify the flagged comments list here
		Assert.assertTrue(modui.fluentWaitTextPresent(comments[0]), "Flagged file comment "+comments[0]+ "not found");

		// Verify all post-moderation actions
		log.info("INFO: verify all post-moderation actions");
		modui.verifyPostModeration(comments);

		modui.endTest();
	}

	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test edit pending and reject Blog Entry in Community moderation</li>
	 * <li><B>Step: </B>Preparing data by creating 1 blog entry by API using community member</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Verify: </B>Verify the 1 item are listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * <li><B>Step: </B>Click "Reject" button to reject it</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully</li>
	 * <li><B>Step: </B>Click "Rejected" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the item with new edited name is listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testEditPendingAndRejectedEntry() {
		log.info("INFO: test editing pending and rejected Blog entry in Moderation");

		modui.startTest();

		String[] entryTitles = new String[1];
		String title = "Entry_Pre" + Helper.genDateBasedRand();
		for (int i = 0; i < 1; i++) {
			entryTitles[i] = title + "_" + Integer.toString(i);
		}

		//prepare one pending entry
		log.info("INFO: prepare one pending entry");
		prepareBlogEntryDataPre(entryTitles);

		//GUI
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		modui.openLandingPage(entryTitles[0]);

		//edit the pending entry and return the new entry title
		log.info("INFO: edit the pending entry");
		String newTitle = modui.EditBlogEntry() + "_" + entryTitles[0];

		//go to Content Approval->Blogs->Entries view
		//modui.gotoContentApprovalBlogsEntriesView();
		Moderation_LeftNav_Menu.CABLOGSENTRIES.select(modui);

		//reject one entry
		modui.openLandingPage(newTitle);
		modui.RejectItem();

		//go to rejected view
		log.info("INFO: Go to Rejected list page");
		modui.clickLink(ModerationUI.RejectedTab);

		modui.openLandingPage(newTitle);
		
		//Edit the rejected blog entry
		log.info("INFO: edit the rejected entry");
		modui.EditBlogEntry();

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test edit flagged and quarantined Blog Entry in Community moderation</li>
	 * <li><B>Step: </B>Preparing data by creating 1 blog entry and then flag it by API using community owner</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Flagged Content -> Blogs ->Entries" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 1 item is listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantine" button to quarantine it</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the item with new edited name is listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testEditFlaggedAndQuarantinedEntry() {
		log.info("INFO: test editing flagged and quaratnined blog entry in Moderation");

		modui.startTest();

		String[] entryTitles = new String[1];
		String title = "Entry_Pre" + Helper.genDateBasedRand();
		for (int i = 0; i < entryTitles.length; i++) {
			entryTitles[i] = title + "_" + Integer.toString(i);
		}

		//prepare one flagged entry
		log.info("INFO: prepare one flagged entry");
		prepareBlogEntryDataPost(entryTitles);

		//GUI
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community overview page using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		Moderation_LeftNav_Menu.FCBLOGSENTRIES.select(modui);
		modui.openLandingPage(entryTitles[0]);

		//edit the flagged entry and return the new entry title
		log.info("INFO: edit the flagged entry");
		String newTitle = modui.EditBlogEntry() + "_" + entryTitles[0];

		//go to Flagged Content->Blogs->Entries view
		Moderation_LeftNav_Menu.FCBLOGSENTRIES.select(modui);

		//quarantine the entry
		modui.openLandingPage(newTitle);
		modui.QuarantineItem();

		//go to quarantined view
		log.info("INFO: Go to Quarantined list page");
		modui.clickLink(ModerationUI.QuarantinedTab);


		//Edit the quarantined blog entry
		log.info("INFO: edit the quarantined entry");
		modui.openLandingPage(newTitle);
		modui.EditBlogEntry();

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test edit flagged and quarantined Ideation idea in Community moderation</li>
	 * <li><B>Step: </B>Preparing data by creating 1 idea and then flag it by API using community owner</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Flagged Content -> Blogs ->Entries" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 1 item is listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantine" button to quarantine it</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the item with new edited name is listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testEditFlaggedAndQuarantinedIdea() {
		log.info("INFO: test editing ideationBlog idea in moderation");

		modui.startTest();

		String[] entryTitles = new String[1];
		String title = "Idea_Pre" + Helper.genDateBasedRand();
		for (int i = 0; i < entryTitles.length; i++) {
			entryTitles[i] = title + "_" + Integer.toString(i);
		}

		//prepare one flagged idea
		log.info("INFO: prepare one flagged idea");
		prepareIdeaDataPost(entryTitles);

		//GUI
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		//go to Flagged Content->Blogs->Entries view
		Moderation_LeftNav_Menu.FCBLOGSENTRIES.select(modui);
		modui.openLandingPage(entryTitles[0]);

		//edit the flagged idea and return the new idea title
		log.info("INFO: edit the flagged idea");
		String newTitle = modui.EditBlogEntry() + "_" + entryTitles[0];

		//go to Flagged Content->Blogs->Entries view
		Moderation_LeftNav_Menu.FCBLOGSENTRIES.select(modui);

		//quarantine the idea
		modui.openLandingPage(newTitle);
		modui.QuarantineItem();

		//go to quarantined view
		log.info("INFO: Go to Quarantined list page");
		modui.clickLink(ModerationUI.QuarantinedTab);

		
		//Edit the quarantined idea
		log.info("INFO: edit the quarantined idea");
		modui.openLandingPage(newTitle);
		modui.EditBlogEntry();

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test edit pending and rejected Ideation idea in Community moderation</li>
	 * <li><B>Step: </B>Preparing data by creating 1 idea by API using community member</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Verify: </B>Verify the 1 item are listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * <li><B>Step: </B>Click "Reject" button to reject it</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully</li>
	 * <li><B>Step: </B>Click "Rejected" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the item with new edited name is listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testEditPendingAndRejectedIdea() {
		log.info("INFO: test editing pending and rejected idea in moderation");

		modui.startTest();

		String[] ideaTitles = new String[1];
		String title = "Idea_Pre" + Helper.genDateBasedRand();
		for (int i = 0; i < 1; i++) {
			ideaTitles[i] = title + "_" + Integer.toString(i);
		}

		//prepare one pending idea
		log.info("INFO: prepare one pending idea");
		prepareIdeaDataPre(ideaTitles);

		//GUI
		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		modui.openLandingPage(ideaTitles[0]);

		//edit the pending idea and return the new idea title
		log.info("INFO: edit the pending idea");
		String newTitle = modui.EditBlogEntry() + "_" + ideaTitles[0];

		//go to Content Approval->Blogs->Entries view
		//modui.gotoContentApprovalBlogsEntriesView();
		Moderation_LeftNav_Menu.CABLOGSENTRIES.select(modui);

		//Reject the idea
		modui.openLandingPage(newTitle);
		modui.RejectItem();

		//go to rejected view
		log.info("INFO: Go to Rejected list page");
		modui.clickLink(ModerationUI.RejectedTab);
		
		//Edit the rejected idea
		log.info("INFO: edit the rejected idea");
		modui.openLandingPage(newTitle);
		modui.EditBlogEntry();

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test edit pending and reject Forum topic in Community moderation</li>
	 * <li><B>Step: </B>Preparing data by creating 1 forum topic by API using community member</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Content Approval -> Forums ->Posts" on left navigation</li>
	 * <li><B>Verify: </B>Verify the item is listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * <li><B>Step: </B>Click "Reject" button to reject it</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully</li>
	 * <li><B>Step: </B>Click "Rejected" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the item with new edited name is listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testEditPendingAndRejectedTopic() {
		log.info("INFO: test editing pending and rejected forum topic");

		modui.startTest();

		String[] topics = new String[1];
		String topic = "Topic_pre" + Helper.genDateBasedRand();
		for (int i = 0; i < 1; i++) {
			topics[i] = topic + "_" + Integer.toString(i);
		}

		//prepare one pending topic
		log.info("INFO: prepare one pending topic");
		prepareForumTopicDataPre(topics);

		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		Moderation_LeftNav_Menu.CAFORUMSPOSTS.select(modui);

		modui.openLandingPage(topics[0]);

		//edit the pending topic and return the new topic title
		log.info("INFO: edit the pending topic");
		String newTitle = modui.EditForumPost() + "_" + topics[0];

		Moderation_LeftNav_Menu.CAFORUMSPOSTS.select(modui);

		modui.openLandingPage(newTitle);
		modui.RejectItem();

		//go to rejected view
		log.info("INFO: Go to Rejected list page");
		modui.clickLink(ModerationUI.RejectedTab);
		
		//Edit the rejected topic
		log.info("INFO: edit the rejected topic");
		modui.openLandingPage(newTitle);
		modui.EditForumPost();

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test edit pending and rejected Forum reply in Community moderation</li>
	 * <li><B>Step: </B>Preparing data by creating 1 forum topic by community owner and then create 1 reply by API using community member</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Content Approval -> Forums ->Posts" on left navigation</li>
	 * <li><B>Verify: </B>Verify the item is listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * <li><B>Step: </B>Click "Reject" button to reject it</li>
	 * <li><B>Verify: </B>Verify the item is rejected successfully</li>
	 * <li><B>Step: </B>Click "Rejected" tab to open the rejected page</li>
	 * <li><B>Verify: </B>Verify the item with new edited name is listed on Moderation page</li>
	 * <li><B>Step: </B>Click item link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testEditPendingAndRejectedReply() {
		log.info("INFO: test editing pending and rejected forum reply");

		modui.startTest();

		String[] repliesContent = new String[1];
		String replyContent = "Reply_pre" + Helper.genDateBasedRand();
		String replyTitle = "Topic Reply";
		for (int i = 0; i < repliesContent.length; i++) {
			repliesContent[i] = replyContent + "_" + Integer.toString(i);
		}

		//prepare one pending reply
		log.info("INFO: prepare one pending reply");
		prepareForumReplyDataPre(repliesContent);

		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		Moderation_LeftNav_Menu.CAFORUMSPOSTS.select(modui);

		modui.openLandingPage(replyTitle);

		//edit the pending reply and return the new reply title
		log.info("INFO: edit the pending reply");
		String newTitle = modui.EditForumPost() + "_" + replyTitle;

		Moderation_LeftNav_Menu.CAFORUMSPOSTS.select(modui);

		modui.openLandingPage(newTitle);
		modui.RejectItem();

		//go to rejected view
		log.info("INFO: Go to Rejected list page");
		modui.clickLink(ModerationUI.RejectedTab);
		
		//Edit the rejected reply
		log.info("INFO: edit the rejected reply");
		modui.openLandingPage(newTitle);
		modui.EditForumPost();

		modui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test edit flagged and quarantined Forum topic and reply in Community moderation</li>
	 * <li><B>Step: </B>Preparing data by creating 1 forum topic and 1 reply and then flag them by API using community owner</li>
	 * <li><B>Step: </B>Login to the community by community owner user</li>
	 * <li><B>Step: </B>Click "Moderation" link on left navigation pane</li>
	 * <li><B>Step: </B>Click "Flagged Content -> Forums ->Posts" on left navigation</li>
	 * <li><B>Verify: </B>Verify the 2 items are listed on Moderation page</li>
	 * <li><B>Step: </B>Click reply link to open the landing page and click "Edit" button, edit the reply title and save it</li>
	 * <li><B>Verify: </B>Verify the reply is edited successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantine" button to quarantine it</li>
	 * <li><B>Verify: </B>Verify the reply is quarantined successfully</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the item with new edited name is listed on Moderation page</li>
	 * <li><B>Step: </B>Click reply link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 *
	 * <li><B>Step: </B>Click "Flagged Content -> Forums ->Posts" on left navigation</li>
	 * <li><B>Verify: </B>Verify the topic is listed on Moderation page</li>
	 * <li><B>Step: </B>Click topic link to open the landing page and click "Edit" button, edit the topic title and save it</li>
	 * <li><B>Verify: </B>Verify the topic is edited successfully without error</li>
	 * <li><B>Step: </B>Click "Quarantine" button to quarantine it</li>
	 * <li><B>Verify: </B>Verify the topic is quarantined successfully</li>
	 * <li><B>Step: </B>Click "Quarantined" tab to open the quarantined page</li>
	 * <li><B>Verify: </B>Verify the item with new edited name is listed on Moderation page</li>
	 * <li><B>Step: </B>Click topic link to open the landing page of it and click "Edit" button, edit the entry title and save it</li>
	 * <li><B>Verify: </B>Verify the item is edited successfully without error</li>
	 * </ul>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testEditFlaggedAndQuarantinedPost() {
		log.info("INFO: test editing flagged and quarantined forum topic and reply");

		modui.startTest();

		String[] replies = new String[1];
		String reply = "Reply_post" + Helper.genDateBasedRand();
		String replyTitle = "Topic Reply";

		for (int i = 0; i < replies.length; i++) {
			replies[i] = reply + "_" + Integer.toString(i);
		}

		//prepare one flagged topic and one flagged reply
		log.info("INFO: prepare one flagged topic and one flagged reply");
		String topic = prepareForumReplyDataPost(replies);

		modui.loadComponent(Data.getData().ComponentCommunities);
		modui.login(comOwner);
		modui.clickLinkWait("link="+communityName);
		// navigate to the API community Moderation
		log.info("INFO: Navigate to the community moderation using UUID");
		Community_LeftNav_Menu.MODERATION.select(communityui);

		Moderation_LeftNav_Menu.FCFORUMSPOSTS.select(modui);

		//Edit flagged and quarantined forum reply

		//edit the flagged reply and return the new reply title
		log.info("INFO: edit the pending idea");
		modui.openLandingPage(replyTitle);
		String newReplyTitle = modui.EditForumPost() + "_" + replyTitle;

		Moderation_LeftNav_Menu.FCFORUMSPOSTS.select(modui);

		//Quarantine the reply
		modui.openLandingPage(newReplyTitle);
		modui.QuarantineItem();

		// Edit quarantined forum reply
		log.info("INFO: edit the quarantined forum reply");

		log.info("INFO: Go to Quarantined list page");
		modui.clickLink(ModerationUI.QuarantinedTab);

		modui.openLandingPage(newReplyTitle);
		modui.EditForumPost();

		// Edit flagged and quarantined forum topic
		//go to Flagged Content->Forums->Post view
		Moderation_LeftNav_Menu.FCFORUMSPOSTS.select(modui);

		//edit the flagged forum topic and return the new topic title
		log.info("INFO: edit the flagged topic");
		modui.openLandingPage(topic);
		String newTopicTitle = modui.EditForumPost() + "_" + topic;

		Moderation_LeftNav_Menu.FCFORUMSPOSTS.select(modui);

		//Quarantine the topic
		modui.openLandingPage(newTopicTitle);
		modui.QuarantineItem();

		//edit the quarantined topic
		log.info("INFO: edit the quarantined topic");

		log.info("INFO: Go to Quarantined list page");
		modui.clickLink(ModerationUI.QuarantinedTab);

		modui.openLandingPage(newTopicTitle);
		modui.EditForumPost();

		modui.endTest();
	}

	private void prepareBlogEntryDataPre(String entryTitles[]) {
		// for creating the blog entry
		apiCommunityMember = new APICommunitiesHandler(serverURL,
				comMember.getUid(), comMember.getPassword());

		for (int i = 0; i < entryTitles.length; i++) {
			BaseBlogPost NewEntry1 = new BaseBlogPost.Builder(entryTitles[i])
					.tags("ModerationBlog").content(entryTitles[i]).build();

			BlogPost blogPostEntry = apiCommunityMember.createBlogEntry(
					NewEntry1, comAPI);
			Assert.assertTrue(blogPostEntry != null,
					"Failed to create blog entry in community using API.");
		}
	}

	private void prepareBlogCommentDataPre(String comments[]) {

		String entry = "BlogCommentPre" + Helper.genDateBasedRand();

		BaseBlogPost NewEntry1 = new BaseBlogPost.Builder(entry)
				.tags("ModerationBlog").content(entry).build();

		// Add Post to blog
		apiCommunityOwner = new APICommunitiesHandler(serverURL,
				comOwner.getUid(), comOwner.getPassword());

		BlogPost blogPostEntry = apiCommunityOwner.createBlogEntry(NewEntry1,
				comAPI);
		Assert.assertTrue(blogPostEntry != null,
				"Failed to create blog entry in community using API.");

		apiBlogMember = new APIBlogsHandler(serverURL, comMember.getUid(),
				comMember.getPassword());

		for (int i = 0; i < comments.length; i++) {
			apiBlogMember.createBlogComment(comments[i], blogPostEntry);
		}

	}

	private void prepareIdeaDataPre(String ideaTitles[]) {
		// for creating the IdeationBlog idea
		apiCommunityMember = new APICommunitiesHandler(serverURL,
				comMember.getUid(), comMember.getPassword());

		for (int i = 0; i < ideaTitles.length; i++) {
			BaseBlogPost NewEntry1 = new BaseBlogPost.Builder(ideaTitles[i])
					.tags("Moderation").content(ideaTitles[i]).build();

			// Add Post to blog
			BlogPost blogPostEntry = apiCommunityMember.createIdea(NewEntry1,
					comAPI);
			Assert.assertTrue(blogPostEntry != null,
					"Failed to create IdeationBlog idea in community using API.");
		}
	}

	private void prepareBlogEntryDataPost(String entryTitles[]) {
		// for creating the blog entry
		apiCommunityOwner = new APICommunitiesHandler(serverURL,
				comOwner.getUid(), comOwner.getPassword());

		for (int i = 0; i < entryTitles.length; i++) {
			BaseBlogPost NewEntry1 = new BaseBlogPost.Builder(entryTitles[i])
					.tags("ModerationBlog").content(entryTitles[i]).build();

			// Add Post to blog
			BlogPost blogPostEntry = apiCommunityOwner.createBlogEntry(
					NewEntry1, comAPI);
			Assert.assertTrue(blogPostEntry != null,
					"Failed to create blog entry in community using API.");
			
		}
		apiBlogOwner = new APIBlogsHandler(serverURL, comOwner.getUid(),
				comOwner.getPassword());
		for(int i=0;i<entryTitles.length;i++)
			apiBlogOwner.flagBlogEntry(entryTitles[i]);

	
	}

	private void prepareBlogCommentDataPost(String comments[]) {

		String entry = "Entry" + Helper.genDateBasedRand();

		BaseBlogPost NewEntry1 = new BaseBlogPost.Builder(entry)
				.tags("ModerationBlog").content(entry).build();

		// Add Post to blog
		apiCommunityOwner = new APICommunitiesHandler(serverURL,
				comOwner.getUid(), comOwner.getPassword());
		BlogPost blogPostEntry = apiCommunityOwner.createBlogEntry(NewEntry1,
				comAPI);
		Assert.assertTrue(blogPostEntry != null,
				"Failed to create blog entry in community using API.");

		apiBlogOwner = new APIBlogsHandler(serverURL, comOwner.getUid(),
				comOwner.getPassword());
		for (int i = 0; i < comments.length; i++) {
			apiBlogOwner.createBlogComment(comments[i], blogPostEntry);
		}

		apiBlogOwner.flagBlogComments(entry);
		
	}

	private void prepareIdeaDataPost(String ideaTitles[]) {
		// for creating the IdeationBlog idea

		apiCommunityOwner = new APICommunitiesHandler(serverURL,
				comOwner.getUid(), comOwner.getPassword());

		for (int i = 0; i < ideaTitles.length; i++) {
			BaseBlogPost NewEntry1 = new BaseBlogPost.Builder(ideaTitles[i])
					.tags("Moderation").content(ideaTitles[i]).build();

			// Add Post to blog
			BlogPost blogPostEntry = apiCommunityOwner.createIdea(NewEntry1,
					comAPI);
			Assert.assertTrue(blogPostEntry != null,
					"Failed to create IdeationBlog idea in community using API.");
		}

		apiBlogOwner = new APIBlogsHandler(serverURL, comOwner.getUid(),
				comOwner.getPassword());
		for(int i=0;i<ideaTitles.length;i++)
			apiBlogOwner.flagBlogEntry(ideaTitles[i]);

	}

	private void prepareIdeaCommentDataPost(String comments[]) {

		String entry = "Idea" + Helper.genDateBasedRand();

		BaseBlogPost NewEntry1 = new BaseBlogPost.Builder(entry)
				.tags("ModerationBlog").content(entry).build();

		// Add Post to blog
		apiCommunityOwner = new APICommunitiesHandler(serverURL,
				comOwner.getUid(), comOwner.getPassword());

		BlogPost blogPostEntry = apiCommunityOwner
				.createIdea(NewEntry1, comAPI);
		Assert.assertTrue(blogPostEntry != null,
				"Failed to create blog entry in community using API.");

		apiBlogOwner = new APIBlogsHandler(serverURL, comOwner.getUid(),
				comOwner.getPassword());
		for (int i = 0; i < comments.length; i++) {
			apiBlogOwner.createBlogComment(comments[i], blogPostEntry);
		}
		apiBlogOwner.flagBlogComments(entry);


	}

	private void prepareForumTopicDataPre(String topic[]) {
		// for creating the Forums topic
		apiCommunityMember = new APICommunitiesHandler(serverURL,
				comMember.getUid(), comMember.getPassword());

		for (int i = 0; i < topic.length; i++) {
			BaseForumTopic baseTopic = new BaseForumTopic.Builder(topic[i])
					.tags("ModerationForum").description(topic[i]).build();

			// Add Post to blog
			ForumTopic forumTopic = apiCommunityMember.CreateForumTopic(comAPI,
					baseTopic);
			Assert.assertTrue(forumTopic != null,
					"Failed to create Forum topic in community using API.");

		}
	}

	private void prepareForumReplyDataPre(String replies[]) {

		String topic = "Topic " + Helper.genDateBasedRand();

		BaseForumTopic baseTopic = new BaseForumTopic.Builder(topic)
				.tags("ModerationForum").description(topic).build();

		// Add Post to blog
		apiCommunityOwner = new APICommunitiesHandler(serverURL,
				comOwner.getUid(), comOwner.getPassword());
		ForumTopic forumTopic = apiCommunityOwner.CreateForumTopic(comAPI,
				baseTopic);
		Assert.assertTrue(forumTopic != null,
				"Failed to create Forum topic in community using API.");

		apiForumMember = new APIForumsHandler(serverURL, comMember.getUid(),
				comMember.getPassword());
		for (int i = 0; i < replies.length; i++) {
			ForumReply reply = apiForumMember.createForumReply(forumTopic,
					replies[i]);
			Assert.assertTrue(reply != null,
					"Failed to create Forum reply in community using API.");

		}

	}

	private void prepareForumTopicDataPost(String topic[]) {
		// for creating the Forums topic
		for (int i = 0; i < topic.length; i++) {
			BaseForumTopic baseTopic = new BaseForumTopic.Builder(topic[i])
					.tags("ModerationForum").description(topic[i]).build();

			// Add Post to blog
			apiCommunityOwner = new APICommunitiesHandler(serverURL,
					comOwner.getUid(), comOwner.getPassword());
			ForumTopic forumTopic = apiCommunityOwner.CreateForumTopic(comAPI,
					baseTopic);
			Assert.assertTrue(forumTopic != null,
					"Failed to create Forum topic in community using API.");
			apiForumOwner = new APIForumsHandler(serverURL, comOwner.getUid(),
					comOwner.getPassword());
			apiForumOwner.flagCommunityTopic(forumTopic);
		}

	}

	private String prepareForumReplyDataPost(String replies[]) {

		String topic = "Topic_post" + Helper.genDateBasedRand();

		BaseForumTopic baseTopic = new BaseForumTopic.Builder(topic)
				.tags("ModerationForum").description(topic).build();

		// Add Post to blog
		apiCommunityOwner = new APICommunitiesHandler(serverURL,
				comOwner.getUid(), comOwner.getPassword());

		ForumTopic forumTopic = apiCommunityOwner.CreateForumTopic(comAPI,
				baseTopic);
		Assert.assertTrue(forumTopic != null,
				"Failed to create Forum topic in community using API.");

		apiForumOwner = new APIForumsHandler(serverURL, comOwner.getUid(),
				comOwner.getPassword());

		apiForumOwner.flagCommunityTopic(forumTopic);
		for (int i = 0; i < replies.length; i++) {
			ForumReply reply = apiForumOwner.createForumReply(forumTopic,
					replies[i]);
			Assert.assertTrue(reply != null,
					"Failed to create Forum reply in community using API.");
			apiForumOwner.flagCommunityReply(reply);
		}


		return topic;
	}

	private void prepareFileDataPre(String files, String[] fileNames) {
		// for creating the IdeationBlog idea

		// String []fileNames = new String[4];
		for (int i = 0; i < fileNames.length; i++) {
			// fileNames[i]= "File " + Helper.genDateBasedRand();

			BaseFile baseFile = new BaseFile.Builder(files).extension(".jpg")
					.rename(fileNames[i]).tags("ModerationFile").build();

			File file = new File(filePath);

			apiFileMember = new APIFileHandler(serverURL, comMember.getUid(),
					comMember.getPassword());

			FileEntry fileEntry = apiFileMember.CreateFile(baseFile, file,
					comAPI);

			Assert.assertTrue(fileEntry != null,
					"Failed to upload file in community using API.");

		}

	}

	private void prepareFileCommentDataPre(String[] comments) {
		// for creating the IdeationBlog idea

		String fileName = "File " + Helper.genDateBasedRand();

		String fileToUpload = Data.getData().file1;

		BaseFile baseFile = new BaseFile.Builder(fileToUpload)
				.extension(".jpg").rename(fileName).tags("ModerationFile")
				.build();

		File file = new File(filePath);
		apiFileOwner = new APIFileHandler(serverURL, comOwner.getUid(),
				comOwner.getPassword());
		FileEntry fileEntry = apiFileOwner.CreateFile(baseFile, file, comAPI);
		Assert.assertTrue(fileEntry != null,
				"Failed to upload file in community using API.");

		apiFileMember = new APIFileHandler(serverURL, comMember.getUid(),
				comMember.getPassword());

		FileComment fComment;

		for (int i = 0; i < comments.length; i++) {
			fComment = new FileComment(comments[i]);

			log.info("comment is" + comments[i]);
			FileComment publishComment = apiFileMember.CreateFileComment(
					fileEntry, fComment, comAPI);

			Assert.assertTrue(publishComment != null,
					"Failed to create comment to file in community using API.");
		}
	}

	/**
	 * Prepare some flagged files for post-moderation test
	 * @param fileNames
	 */
	private void prepareFileDataPost(String[] fileNames) {

		//uploaded file: file1 -> Disert.jpg
		String uploadFile = new String();
		uploadFile = Data.getData().file1;

		apiFileOwner = new APIFileHandler(serverURL, comOwner.getUid(),
				comOwner.getPassword());

		log.info("INFO: upload some files using API");
		for (int i = 0; i < fileNames.length; i++) {
			BaseFile baseFile = new BaseFile.Builder(uploadFile)
					.extension(".jpg").rename(fileNames[i])
					.tags("ModerationFile").build();

			File file = new File(filePath);
			FileEntry fileEntry = apiFileOwner.CreateFile(baseFile, file,
					comAPI);
			Assert.assertTrue(fileEntry != null,
					"Failed to upload file in community using API.");
			
			apiFileOwner.flagCommunityFile(fileEntry);
		}
		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = fileNames[i] + ".jpg";
		}

	
	}

	/**
	 * Prepare some flagged files comments for post-moderation testing
	 * @param comments
	 */
	private void prepareFileCommentDataPost(String[] comments) {

		String fileName = "File " + Helper.genDateBasedRand();

		//the upload file: file1 -> Disert.jpg
		String fileToUpload = Data.getData().file1;
		apiFileOwner = new APIFileHandler(serverURL, comOwner.getUid(),
				comOwner.getPassword());

		BaseFile baseFile = new BaseFile.Builder(fileToUpload)
				.extension(".jpg").rename(fileName)
				.tags("Moderation" + Helper.genDateBasedRand()).build();

		File file = new File(filePath);

		log.info("INFO: Upload one file in community using API");
		FileEntry fileEntry = apiFileOwner.CreateFile(baseFile, file, comAPI);
		Assert.assertTrue(fileEntry != null,
				"Failed to upload file in community using API.");


		log.info("INFO: Create some comments to the new uploaded file using API");
		FileComment fComment;
		for (int i = 0; i < comments.length; i++) {
			fComment = new FileComment(comments[i]);
			FileComment publishComment = apiFileOwner.CreateFileComment(
					fileEntry, fComment, comAPI);
			log.info("commmmmmm " + publishComment.toString());
			Assert.assertTrue(publishComment != null,
					"Failed to create comment to file in community using API.");
			apiFileOwner.flagCommunityFileComment(publishComment);
		}

	}

}
