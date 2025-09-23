package com.ibm.conn.auto.tests.homepage.mt.visitorModel;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_VisitorModel_SavedView extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_VisitorModel_SavedView.class);
	
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testInternalUser,testExternalUser;
	private APIProfilesHandler profilesAPIUser1,profilesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser1,communitiesAPIUser2;
	private String serverURL_MT_orgA;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		testInternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testExternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.externalOrgA, this);
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		communitiesAPIUser2 = new APICommunitiesHandler(serverURL_MT_orgA, testExternalUser.getAttribute(cfg.getLoginPreference()), testExternalUser.getPassword());
		profilesAPIUser1 = new APIProfilesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL_MT_orgA, testExternalUser.getAttribute(cfg.getLoginPreference()), testExternalUser.getPassword());		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the 'Added community' event in 'Saved' view after it's being saved from I'M Following view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user added as a member</B></li>
	*<li><B>Step: [API] External orgA user follow the community created above</B></li>
	*<li><B>Step: External User log into I'M Following view as a External User </B></li>
	*<li><B>Step: Select 'Save this' link from community added event to mark it as a saved</B></li>
	*<li><B>Step: Go to 'Saved' view </B></li>
	*<li><B>Verify: Verify that the same entry that's been marked as a saved should be displayed</B></li>
	*</ul>
	*/
    @Test(groups = { "mtlevel2" })
	public void visitorModel_Saved_CommunityEventFromIMFollowing() {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testInternalUser, communitiesAPIUser1, testExternalUser);
		
		// External user follow community created above
		log.info("INFO: " + testExternalUser.getDisplayName() + " follow the Community using API");		
		logger.strongStep(testExternalUser.getDisplayName()+ " follow the Community using API");
		communitiesAPIUser2.followCommunity(restrictedCommunity);
				
		// testExternalUser goes to I'm Following view
		logger.strongStep("Login to I'M Following view as " + testInternalUser.getDisplayName());
		log.info("Login to I'M Following view " + testInternalUser.getDisplayName());
		LoginEvents.loginAndGotoImFollowing(ui, testExternalUser, false);

		// Create the element for 'Save this' link from news story
		String newsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME, baseCommunity.getName(), null,testInternalUser.getDisplayName());
		String saveLink = HomepageUIConstants.Entry_SaveThis_Link.replaceAll("PLACEHOLDER", newsStory);
		String savedLink = HomepageUIConstants.Entry_Saved_Link.replaceAll("PLACEHOLDER", newsStory);
		
		// Select 'Save this' link from community invite event 
		log.info("INFO: Select 'Save this' link from community invite event");
		logger.strongStep("Select 'Save this' link from community invite event");
		ui.clickLinkWait(saveLink);
		ui.fluentWaitPresent(savedLink);
		
		// Go to Saved view
		log.info("INFO: Go to 'Saved' view");
		logger.strongStep("Go to 'Saved' view");
		ui.gotoSaved();
		
		// Create the elements to be verified
		String newsStoryInSavedView = ui.replaceNewsStory(Data.SAVED_EVENT_ADD_MEMBER, testExternalUser.getDisplayName(), baseCommunity.getName(),testInternalUser.getDisplayName());
		
		// Verify that news story which is being saved from I'M Following view should displays
		log.info("INFO: Verify that news story which is being saved from I'M Following view should displays");
		logger.strongStep("Verify that news story which is being saved from I'M Following view should displays");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryInSavedView}, null, true);
			
		log.info("INFO: Delete the community");
		logger.weakStep("Delete the community");
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
    /**
	*<ul>
	*<li><B>Info: Verify that external user is able to see post/message in 'Saved' view after it's being saved from 'Mentions' view</B></li>
	*<li><B>Step: [API] Internal user posts a message with mentions - mentioning a visitor</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->Mention view</B></li>
	*<li><B>Step: Select 'More' link of mentioned event </B></li>
	*<li><B>Step: Select 'Save this' link from file shared event to mark it as a saved</B></li>
	*<li><B>Step: Go to 'Saved' view </B></li>
	*<li><B>Verify: Verify that the same entry that's been marked as a saved should be displayed</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel3" })
	public void visitorModel_Saved_ProfileStatusFromMention() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName=ui.startTest();
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testExternalUser, profilesAPIUser2, serverURL_MT_orgA,testName, "after text");
				
		// Internal user will now post a board message with mentions -mentioning a visitor
		logger.strongStep("Internal user will now post a board message with mentions - mentioning a visitor");
		String user1MentionsUpdate1Id = ProfileEvents.addStatusUpdateWithMentions(profilesAPIUser1, mentions);

		// Log in as external user and navigate to the Mentions view
		logger.strongStep("Log in as external user and navigate to the Mentions view");
		LoginEvents.loginAndGotoMentions(ui, testExternalUser, true);

		// Create the elements to be verified
		String mentionedYouEvent = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testInternalUser.getDisplayName());
		
		// verify that mentioned event is appeared in 'Mentions' view
		logger.strongStep("Log in as external user and navigate to the Mentions view");
		log.info("INFO: Log in as external user and navigate to the Mentions view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent}, null, true);
		
		// Select 'More' link of mentioned event
		log.info("INFO: Select 'More' link of mentioned event");
		logger.strongStep("Select 'More' link of mentioned event");
		String moreLink = HomepageUIConstants.Entry_More_Link.replaceAll("PLACEHOLDER", mentionedYouEvent);
		driver.getFirstElement(moreLink).click();
		
		// Create elements to be verified
		String saveLink = HomepageUIConstants.Entry_SaveThis_Link.replaceAll("PLACEHOLDER", mentionedYouEvent);
		String savedLink = HomepageUIConstants.Entry_Saved_Link.replaceAll("PLACEHOLDER", mentionedYouEvent);
		
		// Select 'Save this' link from file shared event to mark it as a saved
		log.info("INFO: Select 'Save this' link from file shared event to mark it as a saved");
		logger.strongStep("Select 'Save this' link from file shared event to mark it as a saved");
		driver.getFirstElement(saveLink).click();
		ui.fluentWaitPresent(savedLink);
		
		// Navigate to 'Saved' view
		log.info("INFO: User navigates to 'Saved' view");
		logger.strongStep("User navigates to 'Saved' view");
		ui.gotoSaved();
		
		// Create element to be verified
		String newsStoryInSavedView = ui.replaceNewsStory(Data.SAVEDVIEW_MENTION_EVENT, testExternalUser.getDisplayName(), null, testInternalUser.getDisplayName());
		
		log.info("INFO: Verify that the saved mentioned entry sould be displayed");
		logger.strongStep("Verify that the saved mentioned entry sould be displayed");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryInSavedView}, null, true);
		
		// Delete the status updates posted by internal user
		logger.strongStep("Delete the status updates posted by internal user");
		profilesAPIUser1.deleteBoardMessage(user1MentionsUpdate1Id);
		ui.endTest();	
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the entry for file shared in 'Saved' view after it's being saved from 'My Notifications -> For Me' view</B></li>
	*<li><B>Step: [API] Internal user uploads file which is shared with external user</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->My Notifications->For Me view</B></li>
	*<li><B>Step: Select 'Save this' link from file shared event to mark it as a saved</B></li>
	*<li><B>Step: Go to 'Saved' view </B></li>
	*<li><B>Verify: Verify that the same entry that's been marked as a saved should be displayed</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitorModel_Saved_SharedStandAloneFile() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		APIFileHandler filesAPIUser1= new APIFileHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		
		ui.startTest();
	
		// Internal user will now upload a file shared with external user
		log.info("INFO: Internal user will now upload a file shared with external user");
		logger.strongStep("Internal user will now upload a file shared with external user");
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.PEOPLE,profilesAPIUser2);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testInternalUser, filesAPIUser1);

		log.info("INFO: External user log in and navigates to My Notifications ->For Me view");
		logger.strongStep("External user log in and navigates to My Notifications ->For Me view");
		LoginEvents.loginAndGotoMyNotifications(ui, testExternalUser, true);

		// Create the element for 'Save this' link from news story
		String sharedFileEvent = ui.replaceNewsStory(Data.FILE_SHARED, null, null,testInternalUser.getDisplayName());		
		String saveLink = HomepageUIConstants.Entry_SaveThis_Link.replaceAll("PLACEHOLDER", sharedFileEvent);
		String savedLink = HomepageUIConstants.Entry_Saved_Link.replaceAll("PLACEHOLDER", sharedFileEvent);
		
		// Select 'Save this' link from file shared event to mark it as a saved 
		log.info("INFO: Select 'Save this' link from file shared event to mark it as a saved");
		logger.strongStep("Select 'Save this' link from file shared event to mark it as a saved");
		ui.clickLinkWait(saveLink);
		ui.fluentWaitPresent(savedLink);
		
		// Go to Saved view
		log.info("INFO: Go to 'Saved' view");
		logger.strongStep("Go to 'Saved' view");
		ui.gotoSaved();
		
		// Create element to be verified
		String newsStoryInSavedView = ui.replaceNewsStory(Data.FILE_SHARED_BASIC, testExternalUser.getDisplayName(), null, testInternalUser.getDisplayName());
		
		log.info("INFO: Verify that the file shared entry sould be displayed");
		logger.strongStep("Verify that the file shared entry sould be displayed");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryInSavedView}, null, true);

		// Delete the file
		log.info("INFO: Delete the file");
		logger.weakStep("Delete the file");
		filesAPIUser1.deleteFile(fileEntry);
		ui.endTest();
	}
	
	/**
	*<ul>
	**<li><B>Info: Verify that external user is able to see the Community Invite event in 'Saved' view after it's being saved from Action Required view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community </B></li>
	*<li><B>Step: [API] Internal orgA user Invite external user to join restricted community </B></li>
	*<li><B>Step: External user log into Action Required view/B></li>
	*<li><B>Step: Select 'Save this' link from community invite event to mark it as a saved</B></li>
	*<li><B>Step: Go to 'Saved' view </B></li>
	*<li><B>Verify: Verify that the same entry that's been marked as a saved should be displayed</B></li>
	*<li><B>Step: Delete the community</B></li>
	*</ul>
	* @throws Exception 
	*/
	@Test(groups = { "mtlevel3" })
	public void visitorModel_Saved_CommunityInviteEvent() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		// Internal orgA user will now create a restricted community 
		log.info("INFO: Internal orgA user "+testInternalUser.getDisplayName()+" will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user "+testInternalUser.getDisplayName()+" will now create a restricted community with external user added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		
		log.info("INFO: " + testInternalUser.getDisplayName() + " creating Community " + baseCommunity.getName() + " using API method");
		Community restrictedCommunity = baseCommunity.createAPI(communitiesAPIUser1);

		// Internal orgA user invites external user to join community
		log.info("INFO: Inviting" + testExternalUser.getDisplayName() + "  to join the Public community");	
		communitiesAPIUser1.inviteUserToJoinCommunity(restrictedCommunity, profilesAPIUser2);
		
		log.info("INFO: External user log in and navigates to Action Required view");
		logger.strongStep("External user log in and navigates to Action Required view");
		LoginEvents.loginAndGotoActionRequired(ui, testExternalUser, false);
				
		// Create the news stories to be verified
		String joinCommunityEvent = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_FOR_ME, baseCommunity.getName(), null, testInternalUser.getDisplayName());
				
		// Verify that the community activity created event is displayed in the Action Required view
		log.info("INFO: Verify that the community invite event is displayed in the Action Required view");
		logger.strongStep("Verify that the community invite event is displayed in the Action Required view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{joinCommunityEvent}, null, true);
		
		// Create elements for 'Save this' link 		
		String saveLink = HomepageUIConstants.Entry_SaveThis_Link.replaceAll("PLACEHOLDER", joinCommunityEvent);
		String savedLink = HomepageUIConstants.Entry_Saved_Link.replaceAll("PLACEHOLDER", joinCommunityEvent);
				
		// Select 'Save this' link 
		log.info("INFO: Select 'Save this' link from community invite event to mark it as a saved");
		logger.strongStep("Select 'Save this' link from community invite event to mark it as a saved");
		ui.clickLinkWait(saveLink);
		ui.fluentWaitPresent(savedLink);
						
		// Go to Saved view
		log.info("INFO: Go to 'Saved' view");
		logger.strongStep("Go to 'Saved' view");
		ui.gotoSaved();
				
		// Create elements to be verified
		String newsStoryInSavedView = ui.replaceNewsStory(Data.SAVED_EVENT_COMMNITY_INVITE, testExternalUser.getDisplayName(), baseCommunity.getName(), testInternalUser.getDisplayName());

		// Verify that news story which is being saved from Action Required view should displays
		log.info("INFO: Verify that news story which is being saved from Action Required view should displays");
		logger.strongStep("Verify that news story which is being saved from Action Required view should displays");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryInSavedView}, null, true);

		log.info("INFO: Delete the community clean up");
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	

}

