package com.ibm.conn.auto.tests.homepage.mt.visitorModel;


import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.wikis.WikiEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiComment;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

public class BVT_VisitorModel_MentionView extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_VisitorModel_MentionView.class);
	
	private HomepageUI ui;
	private ProfilesUI profilesUI;
	private APIProfilesHandler profilesAPIUser1,profilesAPIUser2;
	private TestConfigCustom cfg;	
	private User testInternalUser,testExternalUser;
	private APICommunitiesHandler apiOwner;
	private APIForumsHandler forumOwner;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIFileHandler filesAPIUser1;
	private APIWikisHandler wikisAPIUser1;
	private String serverURL_MT_orgA;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		testInternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testExternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.externalOrgA, this);
		apiOwner = new APICommunitiesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		profilesAPIUser1 = new APIProfilesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL_MT_orgA, testExternalUser.getAttribute(cfg.getLoginPreference()), testExternalUser.getPassword());
		forumOwner = new APIForumsHandler(serverURL_MT_orgA,testInternalUser.getAttribute(cfg.getLoginPreference()),testInternalUser.getPassword());
		filesAPIUser1= new APIFileHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		wikisAPIUser1 = new APIWikisHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		profilesUI=ProfilesUI.getGui(cfg.getProductName(),driver);
		
	}

	/**
	*<ul>
	*<li><B>Info: Verify that external user can see post/message created by internal user where he has been mentioned </B></li>
	*<li><B>Step: Internal user logs in and navigate to profile view.</B></li>
	*<li><B>Step: Internal user posts a board message with mentions - mentioning a visitor</B></li>
	*<li><B>Step: Internal user logout form connections</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->Mention view</B></li>
	*<li><B>Verify: Verify that external user is able to see the above post where he is mentioned by internal user.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitorModel_Mentions_ProfileStatusUpdate() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName=ui.startTest();
				
		// Internal user will log in and navigate to the Status Updates view
		log.info("INFO: Internal user "+testInternalUser.getDisplayName()+" log in and navigate to the Status Updates view");
		logger.strongStep("Internal user "+testInternalUser.getDisplayName()+" log in and navigate to the Status Updates view");
		ProfileEvents.loginAndNavigateToUserProfile(ui, profilesUI, testInternalUser, profilesAPIUser1, false);
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testExternalUser, profilesAPIUser2, serverURL_MT_orgA,testName, "after text");
				
		// Internal user will now post a board message with mentions -mentioning a visitor
		logger.strongStep("Internal user will now post a board message with mentions - mentioning a visitor");
		ProfileEvents.addStatusUpdateWithMentionsUsingUIMT(ui, driver, testInternalUser, testExternalUser, mentions);

		// Return to the Home screen and log out from Connections
		log.info("INFO: Log out from Connections");
		logger.strongStep("Log out from Connections");
		LoginEvents.gotoHomeAndLogout(ui);

		// Log in as external user and navigate to the Mentions view
		logger.strongStep("Log in as external user and navigate to the Mentions view");
		LoginEvents.loginAndGotoMentions(ui, testExternalUser, true);

		// Create the elements to be verified
		String mentionedYouEvent = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testInternalUser.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testExternalUser.getDisplayName() + " " + mentions.getAfterMentionText();
				
		// Verify that the mentions event is displayed in the Mentions view
		logger.strongStep("Verify that the mentions event is displayed in the Mentions view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, true);
						
		ui.endTest();	
	}

	/**
	*<ul>
	*<li><B>Info: Verify that external user can see community forum reply created by internal user where external user has been mentioned</B></li>
	*<li><B>Step: [API] Internal orgA user will now create a restricted community with external user as a member. </B></li>
	*<li><B>Step: [API] Internal user creates forum topic</B></li>
	*<li><B>Step: [API] Internal user creates reply to above forum topic mentioning external user</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->Mention view</B></li>
	*<li><B>Verify: Verify that external user is able to see the forum topic reply where he is mentioned by internal user.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })

	public void visitorModel_Mentions_CommForumReply() {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + "_" + Helper.genDateBasedRand());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testInternalUser,communitiesAPIUser1, testExternalUser);

		log.info("INFO: " + testInternalUser.getDisplayName() + " creating Forum Topic in Community");
		logger.strongStep(testInternalUser.getDisplayName() + " creating Forum Topic in Community");
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + "_" + Helper.genDateBasedRand(), baseCommunity);
		ForumTopic topic = apiOwner.CreateForumTopic(restrictedCommunity, baseForumTopic);

		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testExternalUser, profilesAPIUser2, serverURL_MT_orgA,testName + Helper.genDateBasedRandVal(), "afterText");

		log.info("INFO: " + testInternalUser.getDisplayName() + " creating response to topic (API)");
		logger.strongStep(testInternalUser.getDisplayName() + " creating response to topic (API)");
		forumOwner.createTopicReplyMention(topic, mentions);

		// Log in as a external user and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testExternalUser, true);

		// Create the news story text to be verified
		String newsStory = ui.replaceNewsStory(Data.MENTIONED_YOU_FORUM_TOPIC_REPLY, topic.getTitle(),baseCommunity.getName(), testInternalUser.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testExternalUser.getDisplayName() + " "+ mentions.getAfterMentionText();
		logger.strongStep("Verify that external user is able to see the forum topic reply where he is mentioned by internal user.");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, mentionsText}, null, true);

		log.info("INFO: Delete the community clean up");
		logger.weakStep("Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see stand-alone file comment created by internal user where external user has been mentioned</B></li>
	*<li><B>Step: [API] Internal user uploads file which is shared with external user</B></li>
	*<li><B>Step: [API] Internal user add comment mentioning external user on file uploaded in above step </B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->Mention view</B></li>
	*<li><B>Verify: Verify that external user is able to see the file comment where he is mentioned by internal user.</B></li>
	*</ul>
	*/

	@Test(groups = { "mtlevel3" })
	public void visitorModel_Mentions_StanaloneFile() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
	
		// Internal user will now upload a private file
		log.info("INFO: Internal user will now upload a private file");
		logger.strongStep("Internal user will now upload a private file");
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.PEOPLE,profilesAPIUser2);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testInternalUser, filesAPIUser1);

		// Internal user  will add a comment mentioning external user
		log.info("INFO: Internal user  will add a comment mentioning external user");
		logger.strongStep("Internal user  will add a comment mentioning external user");
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testExternalUser, profilesAPIUser2, serverURL_MT_orgA,testName + Helper.genDateBasedRandVal(), "after text");
		FileEvents.addFileMentionsComment(testInternalUser, filesAPIUser1, fileEntry, mentions);

		// External user log in and go to Mentions
		log.info("INFO: External user log in and go to Mentions");
		logger.strongStep("External user log in and go to Mentions");
		LoginEvents.loginAndGotoMentions(ui, testExternalUser, false);

		// Create the news story text  to be verified
		String commentEvent = ui.replaceNewsStory(Data.MENTIONED_YOU_FILE_COMMENT, null, null,testInternalUser.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testExternalUser.getDisplayName() + " "+ mentions.getAfterMentionText();

		// Verify that the mentions event is displayed in the Mentions view
		logger.strongStep("Verify that the mentions event is displayed in the Mentions view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, mentionsText}, null, true);

		// Delete the file 
		logger.weakStep("Delete the file");
		filesAPIUser1.deleteFile(fileEntry);
		ui.endTest();

	}
		
	/**
	 * <ul>
	 * <li><B>Info: Verify that external user can see community wiki page comment created by internal user where he has been mentioned</B></li>
	 * <li><B>Step: [API] Internal orgA user creates a restricted community with external user as a member and Wiki widget added.</B></li>
	 * <li><B>Step: [API] Internal user creates Wiki page</B></li>
	 * <li><B>Step: [API] Internal user creates comment on Wiki page mentioning external user </B></li>
	 * <li><B>Step: testExternalUser log in as a External User</B></li>
	 * <li><B>Step: Go to Home page ->Mention view</B></li>
	 * <li><B>Verify: Verify that external user is able to see the community wiki page community comment where he is mentioned by internal user.</B>
	 * </li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel3" })
	public void visitorModel_Mentions_WikiPageComment() {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		// Internal orgA user will now create a restricted community with external user added as a member and wiki widget to it  
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + "_" + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity,BaseWidget.WIKI, testExternalUser, true, testInternalUser, apiOwner);
	
		Wiki communityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser1);
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genDateBasedRandVal());

		// Internal user will now add a wiki page to the community wiki
		log.info("INFO: Internal user will now add a wiki page to the community wiki");
		logger.strongStep("Internal user will now add a wiki page to the community wiki");
		WikiPage wikiPage = WikiEvents.createWikiPage(communityWiki, baseWikiPage, testInternalUser, wikisAPIUser1);

		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testExternalUser, profilesAPIUser2, serverURL_MT_orgA,testName+Helper.genDateBasedRandVal(), "afterText");
		
		// Internal user will post a comment to the wiki page
		logger.strongStep(testInternalUser.getDisplayName() + " will now post a comment to the wiki page with mentions to " +mentions.getUserToMention().getDisplayName());
		log.info("INFO: " + testInternalUser.getDisplayName() + " will now post a comment to the wiki page with mentions to " +mentions.getUserToMention().getDisplayName());
		WikiComment wikiComment = wikisAPIUser1.addMentionCommentToWikiPage(wikiPage, mentions);

		log.info("INFO: Verify that the comment posted to the wiki page successfully");
		logger.strongStep("Verify that the comment posted to the wiki page successfully");
		Assert.assertNotNull(wikiComment,"ERROR: The wiki comment was NOT created successfully and was returned as null");

		// Log in as external user and navigate to the Mentions view
		log.info("INFO: Log in as external user and navigate to the Mentions view");
		logger.strongStep("Log in as external user and navigate to the Mentions view");
		LoginEvents.loginAndGotoMentions(ui, testExternalUser, true);

		// Create the news story text to be verified
		String newsStory = ui.replaceNewsStory(Data.MENTIONED_YOU_WIKIPAGE_COMMENT, baseWikiPage.getName(),baseCommunity.getName(), testInternalUser.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testExternalUser.getDisplayName() + " "+ mentions.getAfterMentionText();
	
		// Verify that the mentions event is displayed in the Mentions view
		logger.strongStep("Verify that the mentions event is displayed in the Mentions view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, mentionsText}, null, true);

		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}

}
