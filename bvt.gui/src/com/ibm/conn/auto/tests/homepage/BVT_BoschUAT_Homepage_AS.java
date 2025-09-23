package com.ibm.conn.auto.tests.homepage;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.BlogRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.blogs.BlogEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.eventBuilder.wikis.WikiEvents;
import com.ibm.conn.auto.util.menu.BlogSettings_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.blogs.BlogNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.forums.ForumNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.wikis.WikiNewsStories;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class BVT_BoschUAT_Homepage_AS extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Homepage_AS.class);
	private TestConfigCustom cfg;
	private HomepageUI ui;
	private ActivitiesUI actUi;
	private CommunitiesUI comUi;
	private ProfilesUI profUi;
	private FilesUI filesUi;
	private User testUserA,testUserB,testUser;
	private String serverURL;
	private APICommunitiesHandler apiOwner,comAPIOwner;
	private  APIProfilesHandler profilesAPIUserA;
	private  APIFileHandler filesAPIUser;
	private FileEntry  privateSharedFile;
	private APIBlogsHandler blogApiOwner;
	private BaseFile baseSharedFile;
	private BaseCommunity.Access defaultAccess;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {
	
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser(this);
		testUserA = cfg.getUserAllocator().getUser(this);
		testUserB = cfg.getUserAllocator().getUser(this);	
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		comAPIOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		profilesAPIUserA = new APIProfilesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		filesAPIUser = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		// User  will now create a private file which will be shared with User A (acting as User 2)
		baseSharedFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.PEOPLE, profilesAPIUserA);
		privateSharedFile = FileEvents.addFile(baseSharedFile, testUser, filesAPIUser);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		actUi = ActivitiesUI.getGui(cfg.getProductName(),driver);
		comUi = CommunitiesUI.getGui(cfg.getProductName(), driver);
		filesUi = FilesUI.getGui(cfg.getProductName(),driver);
		profUi = ProfilesUI.getGui(cfg.getProductName(),driver);
		
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		ui.addOnLoginScript(ui.getCloseTourScript());	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Create an Activity Entry with a member A, and Verify the Entry Event is displayed in "I am following" wall of  followed member B</li>
	 *<li><B>Step:</B> Create an activity1 with UserA</li> 
	 *<li><B>Step:</B> Update the access level of Activity to Public.</li>
	 *<li><B>Step:</B> Logout as UserA and Login as UserB</li>
	 *<li><B>Step:</B> Start following the activity1 created by UserA</li>
	 *<li><B>Step:</B> Logout as UserB and Login as UserA</li>
	 *<li><B>Step:</B> Add an Entry1 in latest created Activity1</li>
	 *<li><B>Step:</B> Logout as USerA and Login as UserB</li>
	 *<li><B>Verify:</B> Verify that Activity1 Entry1 event , which was created by UserA, is displayed in UserB "I am following" wall</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "cnx8ui-regression"})
	public void verifyStopFollowingFromActivityStream() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		
		BaseActivity activity = new BaseActivity.Builder(Data.getData().Start_An_Activity_InputText_Name_Data + Helper.genDateBasedRandVal())
				.build();
	
		BaseActivityEntry entry = BaseActivityEntry.builder("Test entry " + Helper.genDateBasedRandVal())
                .tags(Helper.genDateBasedRandVal())
                .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
                .build();
		
		ui.startTest();
		// Load the component and login
		log.info("Load Activities and Log In as " + testUserA.getDisplayName());
		logger.strongStep("Load Activities and Log In as " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUserA, cfg.getUseNewUI());
		
		log.info("INFO: Select the Activities 'Mega Menu' option");
		logger.strongStep("Select the Activities 'Mega Menu' option");
		ui.gotoMegaMenuApps("Activities");
		
		log.info("Create new activity" + activity.getName() +"and verify the same");
		logger.strongStep("Create new activity" + activity.getName() +"and verify the same");
		activity.create(actUi);
		String activityCreatedMessage = Data.getData().Activity_Created_Message.replace("PLACEHOLDER", activity.getName());
		Assert.assertTrue(actUi.isTextPresent(activityCreatedMessage));

		log.info("INFO: Click on the Members link and change access to public");
		logger.strongStep("INFO: Click on the Members link and change access to public");
		actUi.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Members);
		
		actUi.fluentWaitElementVisibleOnce(ActivitiesUIConstants.Change_Access);
		actUi.getFirstVisibleElement(ActivitiesUIConstants.Change_Access).click();
		
		actUi.fluentWaitElementVisibleOnce(ActivitiesUIConstants.PublicAccess_RadioBtn);
		actUi.getFirstVisibleElement(ActivitiesUIConstants.PublicAccess_RadioBtn).click();

		log.info("INFO: Save the Activity change to Public access");
		logger.strongStep("INFO: Save the Activity change to Public access");
		actUi.clickSaveButton();
		
		log.info("INFO: Logout as " + testUserA.getDisplayName() + "and login as " + testUserB.getDisplayName());
		logger.strongStep("INFO: Logout as " + testUserA.getDisplayName() + "and login as " + testUserB.getDisplayName());
		ui.logout();
		
		ui.loadComponent(Data.getData().ComponentHomepage,true);
		ui.login(testUserB);
		
		log.info("INFO: Select the Activities 'Mega Menu' option");
		logger.strongStep("Select the Activities 'Mega Menu' option");
		ui.clickLinkWait(ui.getMegaMenuApps());
		ui.clickLinkWithJavascript(ActivitiesUIConstants.activitiesOption);
		ui.getFirstVisibleElement(ActivitiesUIConstants.PublicActivities_Active);
				
		log.info("INFO: Navigate to Public Active Activities");
		logger.strongStep("INFO: Navigate to Public Active Activities");
		actUi.getFirstVisibleElement(ActivitiesUIConstants.PublicActivities_Active).click();
		
		log.info("INFO: Navigate to activity created by " + testUserA.getDisplayName() +"follow the same activity");
		logger.strongStep("INFO: Navigate to activity created by " + testUserA.getDisplayName() +"follow the same activity");
		actUi.fluentWaitPresent(ActivitiesUI.getActivityLink(activity));
		actUi.getFirstVisibleElement(ActivitiesUI.getActivityLink(activity)).click();
		
		log.info("Verify follow the activity message and unfollow activity link after clicking on folow this activity link");
		logger.strongStep("Verify follow the activity message and unfollow activity link after clicking on folow this activity link");
		actUi.getFirstVisibleElement(ActivitiesUIConstants.followThisActivityLink).click();
		Assert.assertTrue(actUi.isTextPresent(Data.getData().Activity_Followed_Success_Message));
		
		driver.isElementPresent(ActivitiesUIConstants.CommunityActivityUnFollow);
		
		log.info("INFO: Logout as " + testUserB.getDisplayName() + "and login as " + testUserA.getDisplayName());
		logger.strongStep("INFO: Logout as " + testUserB.getDisplayName() + "and login as " + testUserA.getDisplayName());
		ui.logout();
		
		ui.loadComponent(Data.getData().ComponentHomepage,true);
		ui.login(testUserA);
		
		log.info("INFO: Select the Activities 'Mega Menu' option");
		logger.strongStep("INFO: Select the Activities 'Mega Menu' option");
		ui.clickLinkWait(ui.getMegaMenuApps());	
		ui.clickLinkWithJavascript(ActivitiesUIConstants.activitiesOption);
		ui.getFirstVisibleElement(ActivitiesUIConstants.PublicActivities_Active);
		
		log.info("INFO: Navigate to the latest activity created by same user");
		logger.strongStep("INFO: Navigate to the latest activity created by same user");
		actUi.fluentWaitPresent(ActivitiesUI.getActivityLink(activity));
		actUi.getFirstVisibleElement(ActivitiesUI.getActivityLink(activity)).click();
		
		log.info("INFO: Created an Entry in the Activity");
		logger.strongStep("INFO: Created an Entry in the Activity");
		actUi.createEntry(entry);
		
		log.info("INFO: Logout as " + testUserA.getDisplayName() + "and login as " + testUserB.getDisplayName());
		logger.strongStep("INFO: Logout as " + testUserA.getDisplayName() + "and login as " + testUserB.getDisplayName());
		ui.logout();
		
		ui.loadComponent(Data.getData().HomepageImFollowing,true);
		ui.login(testUserB);
		
		ui.clickLinkWait(HomepageUIConstants.homepage);
		
		log.info("Verify that the create activity entry event is displayed in all views" );
		logger.strongStep("Verify that the create activity entry event is displayed in all views");
		String newsStory = ui.replaceNewsStory(Data.CREATE_ACTIVITY_ENTRY, entry.getTitle() , activity.getName(),testUserA.getDisplayName());
        HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, null, true);
        
   
        ui.endTest();
        
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify Homepage: Entry with image , text and URLS</li>
	*<li><B>Step:</B>Login to Connections System - Homepage</li>
	*<li><B>Verify:</B>The user should be able to Login to Connections system without any errors and Homepage should be displayed </li>
	*<li><B>Step:</B>Click on the text field 'What do you want to share ?' displayed in the center pane on homepage</li>
	*<li><B>Verify:</B>A text box should be displayed with Post and Clear option at the left bottom and Add a file option at the right bottom side</li>
	*<li><B>Step:</B>Copy the URL for the video file and paste it in the text box > Click on 'Post'</li>
	*<li><B>Verify:</B>The added video file URL should be displayed</li>
	*<li><B>Verify:</B>The added video file URL new story should be displayed in I'm Following tab</li>
	*<li><B>Verify:</B>If the Video contains thumbnail it should be displayed</li>
	*<li><B>Step:</B>Now, Again click on the text box 'What do you want to share?'</li>
	*<li><B>Verify: </B>A text box should be displayed with Post and Clear option at the left bottom and Add a file option at the right bottom side</li>
	*<li><B>Step: </B>Click on Add File option present at the right bottom </li>
	*<li><B>Verify: </B>Add a File popup should be displayed with My Files, My Computer and a Browse button along with OK and Cancel button at the bottom</li>
	*<li><B>Step: </B>Click on Browse button and select the image file and select 'OK'</li>
	*<li><B>Verify: </B>Selected file name should be displayed in the pop up along with 'OK' and 'Cancel' button</li>
	*<li><B>Step: </B>Select 'OK' button</li>
	*<li><B>Verify: </B>The file should be added and file name should be displayed</li>
	*<li><B>Step: </B>Enter some text in the text box and click on Post option</li>
	*<li><B>Verify: </B>The text along with the added file should be displayed in the center pane of I'm Following tab</li>
	*<li><B>Step: </B>Click on the text box 'What do you want to share?'</li>
	*<li><B>Step: </B>A text box should be displayed with Post and Clear option at the left bottom and Add a file option at the right bottom side</li>
	*<li><B>Verify: </B>The added URL should be displayed in the center pane of I'm Following tab</li>
	*<li><B>Step: </B>Click on the URL of webpage</li>
	*<li><B>Verify: </B>User should be able to open the webpage</li>
	* 
	*</ul>
	* @throws Exception 
	*/
	@Test(groups = {"regression", "cnx8ui-regression"})
	public void verifyHomepageEntryWithImageTextURL() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String videoURL = "http://www.youtube.com/watch?v=tfzT7y7xOTY ";

		ui.startTest();
		
		logger.strongStep("Login to Connections System - Homepage");
		log.info("INFO: Login to Connections System - Homepage");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("User should be able to Login to Connections system without any errors and Homepage should be displayed");
		log.info("INFO: "+testUser.getDisplayName()+" should be able to Login to Connections system without any errors and Homepage should be displayed");
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.Updates));
		String title = cfg.getUseNewUI() ? "HCL Connections Home Page - Latest Updates" : "HCL Connections Home Page - Updates";
		Assert.assertEquals(driver.getTitle(), title);
		
		logger.strongStep("Click on the text field 'What do you want to share ?' displayed in the center pane on homepage");
		log.info("INFO: Click on the text field 'What do you want to share ?' displayed in the center pane on homepage");
		ui.waitForPageLoaded(driver);
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
		
		logger.strongStep("A text box should be displayed with Post and Clear option at the left bottom and Add a file option at the right bottom side");
		log.info("INFO: A text box should be displayed with Post and Clear option at the left bottom and Add a file option at the right bottom side");
		Assert.assertTrue(ui.isElementPresent(BaseUIConstants.StatusUpdate_Body));
		ui.switchToTopFrame();
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.PostComment));
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.ClearStatusUpdate));
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.AttachAFile));
		
		logger.strongStep("Copy the URL for the video file and paste it in the text box > Click on 'Post'");
		log.info("INFO: Copy the URL for the video file and paste it in the text box > Click on 'Post'");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		String statusUpdateContent = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(statusUpdateContent+" "+videoURL);
		ui.switchToTopFrame();
		ui.clickLinkWait(HomepageUIConstants.PostComment);
		ui.fluentWaitTextPresent(Data.getData().postSuccessMessage);
		
		logger.strongStep("The added video file URL should be displayed");
		log.info("INFO: The added video file URL should be displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUI.newStoryPostedLink(videoURL.trim())).isVisible());
		
		logger.strongStep("The added video file URL new story should be displayed in I'm Following tab");
		log.info("INFO: The added video file URL new story should be displayed in I'm Following tab");
		String videoURLPostedEvent = HomepageUIConstants.activityStreamNewsStory.replace("PLACEHOLDER", testUser.getDisplayName()+" "+statusUpdateContent+" "+videoURL.trim());
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{videoURLPostedEvent}, null, true);
		
		logger.strongStep("If the Video contains thumbnail it should be displayed");
		log.info("INFO: If the Video contains thumbnail it should be displayed");
		
		try {
			boolean isPreviewAvailable = driver.getFirstElement(HomepageUI.URLPreview("IBM Wine commercial"))
					.isVisible();

			if (isPreviewAvailable) {
				String URLPreviewThumbnailSelector = HomepageUIConstants.URLPreview_Thumbnail.replace("PLACEHOLDER",
						"IBM commercial shot in");
				Assert.assertTrue(driver.getFirstElement(URLPreviewThumbnailSelector).isVisible());
			}
		} catch (java.lang.AssertionError e) {
			log.info("Preview is not available");
		}

		logger.strongStep("Now, Again click on the text box 'What do you want to share?'");
		log.info("INFO: Now, Again click on the text box 'What do you want to share?'");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
		
		logger.strongStep("A text box should be displayed with Post and Clear option at the left bottom and Add a file option at the right bottom side");
		log.info("INFO:A text box should be displayed with Post and Clear option at the left bottom and Add a file option at the right bottom side ");
		Assert.assertTrue(ui.isElementPresent(BaseUIConstants.StatusUpdate_Body));
		ui.switchToTopFrame();
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.PostComment));
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.ClearStatusUpdate));
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.AttachAFile));
		
		logger.strongStep("Click on Add File option present at the right bottom ");
		log.info("INFO: Click on Add File option present at the right bottom ");
		ui.fluentWaitElementVisible(HomepageUIConstants.AttachAFile);
		ui.clickLinkWithJavascript(HomepageUIConstants.AttachAFile);
		
		logger.strongStep("Add a File popup should be displayed with My Files, My Computer and a Browse button along with OK and Cancel button at the bottom");
		log.info("INFO: Add a File popup should be displayed with My Files, My Computer and a Browse button along with OK and Cancel button at the bottom");
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.addAFilePopUp));
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.MyFilesTab));
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.MyComputer));
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.Ok));
		Assert.assertTrue(ui.isElementPresent(BaseUIConstants.CancelButton));
	
		logger.strongStep("Click on Browse button and select the image file and select 'OK'");
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		log.info("INFO: " + testUser.getDisplayName() + " will now select a file to be uploaded from the local hard disk with filename: " + baseFile.getName());
		filesUi.fileToUpload(baseFile.getName(), BaseUIConstants.FileInputField);
		
		logger.strongStep("Selected file name should be displayed in the pop up along with 'OK' and 'Cancel' button");
		log.info("INFO: Selected file name "+baseFile.getName()+" should be displayed in the pop up along with 'OK' and 'Cancel' button");
		Assert.assertEquals(driver.getSingleElement("css=a[class*='lconnFilenameContainer']").getText(),baseFile.getName());
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.Ok));
		Assert.assertTrue(ui.isElementPresent(BaseUIConstants.CancelButton));
		
		logger.strongStep("Select 'OK' button");
		log.info("INFO: Select 'OK' button");
		ui.clickLinkWait(HomepageUIConstants.Ok);
		
		logger.strongStep("The file should be added and file name should be displayed");
		log.info("INFO: The file should be added and file name should be displayed");
		Assert.assertTrue(ui.isElementPresent(HomepageUI.fileAttachmentDetails(baseFile.getName())));
		
		logger.strongStep("Enter some text in the text box and click on Post option");
		log.info("INFO: Enter some text in the text box and click on Post option");
		UIEvents.switchToStatusUpdateFrame(ui);
		statusUpdateContent = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		UIEvents.typeStringWithNoDelay(ui, statusUpdateContent);
		ui.switchToTopFrame();
		ui.clickLinkWait(HomepageUIConstants.PostComment);
		ui.fluentWaitTextPresent(Data.getData().postSuccessMessage);
		
		logger.strongStep("The text along with the added file should be displayed in the center pane of I'm Following tab");
		log.info("INFO: The text along with the added file should be displayed in the center pane of I'm Following tab");
		String filePostedEvent = HomepageUIConstants.activityStreamNewsStory.replace("PLACEHOLDER", testUser.getDisplayName()+" "+statusUpdateContent+" "+baseFile.getName());
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{filePostedEvent}, null, true);
		
		logger.strongStep("Click on the text box 'What do you want to share?'");
		log.info("INFO: Click on the text box 'What do you want to share?'");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
		statusUpdateContent = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(statusUpdateContent+" "+driver.getCurrentUrl()+" ");

		logger.strongStep("A text box should be displayed with Post and Clear option at the left bottom and Add a file option at the right bottom side");
		log.info("INFO:A text box should be displayed with Post and Clear option at the left bottom and Add a file option at the right bottom side");
		Assert.assertTrue(ui.isElementPresent(BaseUIConstants.StatusUpdate_Body));
		ui.switchToTopFrame();
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.PostComment));
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.ClearStatusUpdate));
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.AttachAFile));
		ui.clickLinkWait(HomepageUIConstants.PostComment);
		ui.fluentWaitTextPresent(Data.getData().postSuccessMessage);
		
		logger.strongStep("The added URL should be displayed in the center pane of I'm Following tab");
		log.info("INFO: The added URL should be displayed in the center pane of I'm Following tab");
		String URLPostedEvent = HomepageUIConstants.activityStreamNewsStory.replace("PLACEHOLDER", testUser.getDisplayName()+" "+statusUpdateContent+" "+driver.getCurrentUrl());
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{URLPostedEvent}, null, true);
		
		logger.strongStep("Click on the URL of webpage");
		log.info("INFO: Click on the URL of webpage");
		driver.getFirstElement(HomepageUI.newStoryPostedLink(driver.getCurrentUrl())).click();
		
		logger.strongStep("User should be able to open the webpage");
		log.info("INFO: User should be able to open the webpage");
		ui.switchTabs(2, 1);
		ui.waitForPageLoaded(driver);
		log.info("info is:"+driver.getTitle());
		Assert.assertEquals(driver.getTitle(), title);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Create a Community with testUserA via API</li>
	 *<li><B>Step:</B> Login with testUSerA and Navigate To My Community Page</li> 
	 *<li><B>Verify:</B> Click on I am an Owner and Verify that the Community created via API is displayed On My community Page.</li>
	 *<li><B>Verify:</B> Click On Start a Community and Verify That Start a community Page is displayed</li>
	 *<li><B>Verify:</B> Click On Start a Activity and Verify That Start a Activity Page is displayed</li>
	 *<li><B>Verify:</B> Click On Start a Blog and Verify That Start a Blog Fields are displayed</li>
	 *<li><B>Verify:</B> Click On Add a BookMarks and Verify That Add A Bookmark window is displayed</li>
	 *<li><B>Verify:</B> Click On Start a Forum and Verify That Forum Page field is displayed</li>
	 *<li><B>Verify:</B> Navigate To Files and Verify That File Upload popup window is displayed</li>
	 *<li><B>Verify:</B> Click On Start a Wikis and Verify That Wikis Page field is displayed</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","mt-exclude"})
	public void verifyCancelOutOfTheCreateFormForEachApp() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				.tags(Data.getData().commonTag + Helper.genDateBasedRand())
				.access(defaultAccess)
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description(Data.getData().commonDescription)
				.build();
		
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		// Load the component and login
		log.info("Load Activities and Log In as " + testUserA.getDisplayName());
		logger.strongStep("Load Activities and Log In as " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUserA,cfg.getUseNewUI());
		
		log.info("INFO: Navigate to Owned Communities page");
		logger.strongStep("INFO: Navigate to Owned Communities page");
		ui.gotoMegaMenuApps("Communities");
		
		log.info("Verify the Created Community is Displayed. Verify that Start a Community Button is also displayed");
		logger.strongStep("Verify the Created Community is Displayed. Verify that Start a Community Button is also displayed");
		Assert.assertTrue(comUi.isCreatedCommunityDisplayed(community));
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.StartACommunityDropDown));

		
		log.info("INFO: Click on Start a New Community and Click on Cancel Button");
		logger.strongStep("INFO: Click on Start a New Community and Click on Cancel Button");
		driver.getSingleElement(CommunitiesUIConstants.StartACommunityDropDown).click();
		driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromDropDown).click();
		
		driver.getSingleElement(CommunitiesUIConstants.CancelButton).click();

		log.info("Verify that Discover Button is displayed at Top Panel");
		logger.strongStep("Verify that Discover Button is displayed at Top Panel");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.topNavDiscoverCardView));

		log.info("INFO: Select the Activities 'Mega Menu' option");
		logger.strongStep("Select the Activities 'Mega Menu' option");
		ui.gotoMegaMenuApps("Activities");
		
		log.info("Click on Start an Activity button abd Verify that Start an Activity Page is displayed");
		logger.strongStep("Click on Start an Activity button abd Verify that Start an Activity Page is displayed");
		actUi.waitForPageLoaded(driver);
		actUi.clickLinkWait(ActivitiesUIConstants.Start_An_Activity);
		Assert.assertTrue(driver.isElementPresent(ActivitiesUIConstants.Start_An_Activity_InputText_Name));
		actUi.clickLinkWait(BaseUIConstants.CancelButton);

		log.info("INFO: Select the Blogs 'Mega Menu' option");
		logger.strongStep("Select the Blogs 'Mega Menu' option");
		ui.gotoMegaMenuApps("Blogs");
		
		log.info("Click on Start an Blog button abd Verify that Blogs Page is displayed");
		logger.strongStep("Click on Start an Blogs button abd Verify that Blog Page is displayed");
		BlogsUI blogsUi = BlogsUI.getGui(cfg.getProductName(), driver);
		blogsUi.clickLinkWait(BlogsUIConstants.StartABlog);
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsAddressObject));
		blogsUi.clickLinkWait(BaseUIConstants.CancelButton);
		
		log.info("INFO: Select the Bookmarks 'Mega Menu' option");
		logger.strongStep("Select the Activities 'Mega Menu' option");
		ui.gotoMegaMenuApps("Bookmarks");
		
		log.info("INFO: Click on Add A Bookmark button");
		logger.strongStep("INFO: Click on Add A Bookmark button");
		DogearUI dogearUi = DogearUI.getGui(cfg.getProductName(), driver);
		dogearUi.clickLinkWait(DogearUIConstants.AddABookmark);
		
		log.info("INFO: Switch to New Window");
		logger.strongStep("INFO: Switch to New Window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");

		log.info("Verify that Bookmark Page is displayed");
		logger.strongStep("Verify that Bookmark Page is displayed");
		Assert.assertTrue(driver.isElementPresent(DogearUIConstants.SubmitSaveButton));
		dogearUi.clickLinkWait(BaseUIConstants.CancelButton);

		log.info("INFO: Switch to Main Window");
		logger.strongStep("INFO: Switch to Main Window");
		driver.switchToFirstMatchingWindowByPageTitle("Bookmarks - Bookmarks for " + testUserA.getDisplayName());
		
		log.info("INFO: Select the Forums 'Mega Menu' option");
		logger.strongStep("Select the Forums 'Mega Menu' option");
		ui.gotoMegaMenuApps("Forums");
		
		log.info("INFO: Click on Forums Tab And Click On Start A Forum Button");
		logger.strongStep("INFO: Click on Forums Tab And Click On Start A Forum Button");
		ForumsUI forumUi = ForumsUI.getGui(cfg.getProductName(), driver);
		forumUi.clickLinkWait(ForumsUIConstants.Forum_Tab);
		forumUi.clickLinkWait(ForumsUIConstants.Start_A_Forum);
		
		log.info("Verify that Bookmark Page is displayed");
		logger.strongStep("Verify that Bookmark Page is displayed");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Start_A_Forum_InputText_Name));
		forumUi.clickLinkWait(BaseUIConstants.CancelButton);
		
		log.info("INFO: Select the Files 'Mega Menu' option");
		logger.strongStep("Select the Files 'Mega Menu' option");
		ui.gotoMegaMenuApps("Files");
		
		log.info("INFO: Click On Upload Button");
		logger.strongStep("INFO: Click On Upload Button");
		filesUi.clickLinkWait(FilesUIConstants.GLOBAL_NEW_BUTTON);
		filesUi.clickLinkWait(FilesUIConstants.NEW_FOLDER_IN_GLOBAL_NEW);
		
		log.info("Verify that shareWithPeople Button on File Upload PopUp Window is displayed");
		logger.strongStep("Verify that shareWithPeople Button on File Upload PopUp Window is displayed");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.shareWithPeople));
		filesUi.clickLinkWait(BaseUIConstants.CancelButton);
		
		log.info("INFO: Select the Wikis 'Mega Menu' option");
		logger.strongStep("Select the Wikis 'Mega Menu' option");
		ui.gotoMegaMenuApps("Wikis");
		
		log.info("INFO: Click On Start New wiki Button");
		logger.strongStep("INFO: Click On Start New wiki Button");
		WikisUI wikisUi = WikisUI.getGui(cfg.getProductName(), driver);
		wikisUi.clickLinkWait(WikisUIConstants.Start_New_Wiki_Button);
		
		log.info("Verify that Start A Wiki Page is displayed");
		logger.strongStep("Verify that Start A Wiki Page is displayed");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.MembershipRolesUsersDropdown));
		wikisUi.clickLinkWait(BaseUIConstants.CancelButton);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> testUSerA added testUSerB in Entry and created Blog entry.Verify that testUSerB feed wall has message of testUserA like on Blog Entry comment added by testUserB</li>
	 *<li><B>Step:</B> Login with testUserA and Navigate To My Blog Page</li> 
	 *<li><B>Verify:</B> Create a new Blog and click Post.</li>
	 *<li><B>Verify:</B> Add testUserB to the created Blog with Author access</li>
	 *<li><B>Verify:</B> Add Blog Entry with testUserA</li>
	 *<li><B>Verify:</B> Logout as testUSerA and login as testUSerB</li>
	 *<li><B>Verify:</B> testUSerB add a comment on testUSerA Blog entry</li>
	 *<li><B>Verify:</B> Logout as testUSerB and login as testUSerA </li>
	 *<li><B>Verify:</B> testUserA likes the comment added for Blog entry by testUserB</li>
	 *<li><B>Verify:</B> Logout as testUSerA and login as testUSerB</li>
	 *<li><B>Verify:</B> Navigate To Updates -> I am Following</li>
	 *<li><B>Verify:</B> Verify the message on the feed wall that testUSerA likes testUSerB comments on Blog entry </li>
	 *</ul>
	 */
	@Test(groups = {"regression","mt-exclude"})
	public void verifyActivityStreamLikeBlogEntryComment() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());	

		Member memberOwner = new Member(BlogRole.OWNER, testUserB);		

		String testName = ui.startTest();

		String randval = Helper.genDateBasedRandVal();

		BaseBlog blog = new BaseBlog.Builder("test" + randval, Data.getData().BlogsAddress1 + randval)
				.tags("Tag for "+testName  + randval)
				.description("Test description for testcase " + testName)
				.timeZone(Time_Zone.Europe_London)
				.theme(Theme.Blog_with_Bookmarks)
				.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand()).blogParent(blog)
				.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName)
				.build();

		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();

		// Load the component and login
		log.info("INFO : Load HomePage and Log In as " + testUserA.getDisplayName());
		logger.strongStep("INFO : Load HomePage and Log In as " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUserA,cfg.getUseNewUI());

		log.info("INFO: Select the Blogs 'Mega Menu' option");
		logger.strongStep("INFO : Select the Blogs 'Mega Menu' option");
		ui.gotoMegaMenuApps("Blogs");

		log.info("INFO : Create new blog " + blog.getName());
		logger.strongStep("INFO : Create new blog " + blog.getName());
		
		BlogsUI blogsUi = BlogsUI.getGui(cfg.getProductName(), driver);
		blog.create(blogsUi);

		log.info("INFO: Click on the Settings link for the first Blog listed");
		logger.strongStep("INFO: Click on the Settings link for the first Blog listed");
		ui.getFirstVisibleElement(BlogsUIConstants.SettingsLink).click();

		log.info("INFO: Click on the link Author");
		logger.strongStep("INFO: Click on the link Author");
		BlogSettings_LeftNav_Menu.AUTHORS.select(blogsUi);		

		log.info("INFO: Add the member as an 'Owner' to the Blog");
		logger.strongStep("INFO: Add the member as an 'Owner' to the Blog");
		blogsUi.waitForPageLoaded(driver);
		blogsUi.fluentWaitElementVisible(HomepageUIConstants.addMemberLink);
		blogsUi.addMember(memberOwner);	

		logger.strongStep("INFO : Click on 'My Blog' link");
		log.info("INFO: Click on 'My Blog' link");
		blogsUi.clickLinkWait(BlogsUIConstants.MyBlogs);

		//open blog
		logger.strongStep("INFO : Open the blog");
		log.info("INFO: Open blog");
		blogsUi.clickLinkWait("link=" + blog.getName());

		logger.strongStep("INFO : Select New Entry button");
		log.info("INFO: Select New Entry button");
		blogsUi.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);

		logger.strongStep("INFO : Add a new entry in Blog " +  blog.getName());
		log.info("INFO: Add a new entry in Blog " +  blog.getName());
	//	blogsUi.clickLink(BlogsUI.BlogsNewEntry);
		blogEntry.create(blogsUi);

		log.info("INFO: Logout as " + testUserA.getDisplayName() + "and login as " + testUserB.getDisplayName());
		logger.strongStep("INFO: Logout as " + testUserA.getDisplayName() + "and login as " + testUserB.getDisplayName());
		ui.logout();
		ui.loadComponent(Data.getData().ComponentHomepage,true);
		ui.login(testUserB);

		log.info("INFO : Select the Blogs 'Mega Menu' option");
		logger.strongStep("INFO: Select the Blogs 'Mega Menu' option");
		ui.clickLinkWait(ui.getMegaMenuApps());	
		ui.clickLinkWithJavascript(BlogsUIConstants.blogsOption);

		logger.strongStep("INFO: Open the blog " + blog.getName());
		log.info("INFO: Open blog " + blog.getName());
		blogsUi.waitForPageLoaded(driver);
		blogsUi.fluentWaitElementVisible("link=" + blog.getName());
		blogsUi.clickLinkWait("link=" + blog.getName());

		logger.strongStep("INFO: Open the blog Entry "+ blogEntry.getTitle());
		log.info("INFO: Open blog Entry "+ blogEntry.getTitle());
		blogsUi.clickLinkWait("link=" + blogEntry.getTitle());

		//Add a comment
		logger.strongStep("INFO: Add a comment " + comment.getContent() + "in "+ blogEntry.getTitle());
		log.info("INFO: Add a comment " + comment.getContent() + "in "+ blogEntry.getTitle());
		comment.create(blogsUi);

		log.info("INFO: Logout as " + testUserB.getDisplayName() + "and login as " + testUserA.getDisplayName());
		logger.strongStep("INFO: Logout as " + testUserB.getDisplayName() + "and login as " + testUserA.getDisplayName());
		ui.logout();
		ui.loadComponent(Data.getData().ComponentHomepage,true);
		ui.login(testUserA);

		log.info("INFO: Select the Blogs 'Mega Menu' option");
		logger.strongStep("Select the Blogs 'Mega Menu' option");
		ui.clickLinkWait(ui.getMegaMenuApps());	
		ui.clickLinkWithJavascript(BlogsUIConstants.blogsOption);

		logger.strongStep("INFO: Open the blog " + blog.getName());
		log.info("INFO: Open blog " + blog.getName());
		blogsUi.waitForPageLoaded(driver);
		blogsUi.clickLinkWait("link=" + blog.getName());

		logger.strongStep("INFO: Open the blog Entry "+ blogEntry.getTitle());
		log.info("INFO: Open blog Entry "+ blogEntry.getTitle());
		blogsUi.clickLinkWait("link=" + blogEntry.getTitle());

		log.info("INFO: Click on First Like Button For Blog Entry Comments");
		logger.strongStep("INFO: Click on First Like Button For Blog Entry Comments");
		blogsUi.clickLinkWait(BlogsUIConstants.BlogsEntryCommentLike);

		log.info("INFO: Logout as " + testUserA.getDisplayName() + "and login as " + testUserB.getDisplayName());
		logger.strongStep("INFO: Logout as " + testUserA.getDisplayName() + "and login as " + testUserB.getDisplayName());		
		ui.logout();
		ui.loadComponent(Data.getData().HomepageImFollowing,true);
		ui.login(testUserB);

		log.info("Verify the feed is displayed on Home Page I m following Tab mentioning "+ testUserA  +" liked your comment" );
		logger.strongStep("Verify the feed is displayed on Home Page I m following Tab mentioning "+ testUserA  +" liked your comment" );
		String newsStory = ui.replaceNewsStory(Data.LIKE_YOUR_BLOG_COMMENT, blogEntry.getTitle() , null,testUserA.getDisplayName());
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, null, true);

		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B>Verify activity stream event posted with mentions and URL link</li>
	 *<li><B>Step:</B> Create community using API</li> 
	 *<li><B>Step:</B> Login to Connections System - Homepage</li>
	 *<li><B>Step:</B> Go to 'What do you want to share?' box and enter some text along with mentioned user's name</li>
	 *<li><B>Verify:</B> Verify that type-ahead result should be returned</li>
	 *<li><B>Step:</B> Select a user form type ahead search results</li>
	 *<li><B>Verify:</B> Verify @mention user name should appear in blue link</li>
	 *<li><B>Step:</B> Enter the remainder of the mentions text along with url link into the status update input field</li>
	 *<li><B>Verify:</B> Verify the URL link should appear in blue</li>
	 *<li><B>Verify:</B> Verify URL preview should be displayed if it's present</li>
	 *<li><B>Step:</B> Post the message along with URL</li>
	 *<li><B>Verify:</B> Verify the added URL news story should be displayed in I'm Following tab</li>
	 *<li><B>Step:</B> Hover over the mentioned user link</li>
	 *<li><B>Verify:</B> Verify user's business card should be displayed</li>
	 *<li><B>Step:</B> Click on the mentioned user link</li>
	 *<li><B>Verify:</B> Verify new window should be opened and user should be navigated to the profile page</li>
	 *<li><B>Step:</B> Click on posted URL link</li>
	 *<li><B>Verify:</B> Verify new window should be opened and user should be navigated to the disney page</li>
	 *<li><B>Step:</B> Navigate to Profile component</li>
	 *<li><B>Verify:</B> Repeat above steps (Step#3 to Step#6 including verifications)</li>
	 *<li><B>Step:</B> Navigate to Community component</li>
	 *<li><B>Step:</B> Navigate to community created via API</li>
	 *<li><B>Step:</B> Select 'Status Updates' from top nav bar menu</li>
	 *<li><B>Verify:</B> Repeat above steps (Step#3 to Step#6 including verifications) </li>
	 *</ul>
	 */	
	@Test(groups = {"regression","cnx8ui-regression"})
	public void verifyActivityStreamWithMentionAndURL() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName=ui.startTest();
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(com.ibm.conn.auto.appobjects.base.BaseCommunity.Access.PUBLIC)
				.description("Test description for testcase " + testName).build();
		
		// create community using API
		log.info("INFO: Create community using API");
		logger.strongStep(" Create community using API");
		Community comAPI = baseCommunity.createAPI(comAPIOwner);
		
		// add the UUID to community
		log.info("INFO: Get UUID of community");
		baseCommunity.getCommunityUUID_API(comAPIOwner, comAPI);

		logger.strongStep("Login to Connections System - Homepage");
		log.info("INFO: Login to Connections System - Homepage");
		comUi.loadComponent(Data.getData().HomepageImFollowing);
		comUi.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		// Post and verify a board message with mention and URL link
		postAndVerfiyMentionsWithURL("StatusUpdateFromHomepage");
				
		logger.strongStep("Navigate to Profile component");
		log.info("INFO: Navigate to Profile component");	
		profUi.loadComponent(Data.getData().ComponentProfiles, true);
		
		// Post and verify a board message with mention and URL link
		postAndVerfiyMentionsWithURL("StatusUpdateFromProfile");
		
		logger.strongStep("Navigate to Community component");
		log.info("INFO: Navigate to Community component");	
		comUi.loadComponent(Data.getData().ComponentCommunities, true);
		
		// Navigate to the API community
		logger.strongStep("Navigate to community");
		log.info("INFO: Navigate to community using UUID");
		baseCommunity.navViaUUID(comUi);

		// Select 'Select 'Status Updates' from top nav bar menu
		logger.strongStep("Select 'Status Updates' from top nav bar menu");
		log.info("INFO: Select 'Status Updates' from top nav bar menu");
		Community_LeftNav_Menu.STATUSUPDATES.select(comUi);
		
		// Post and verify a board message with mention and URL link
		postAndVerfiyMentionsWithURL("StatusUpdateFromCommunityStausUpdate");

		ui.endTest();
	}
	
	private void postAndVerfiyMentionsWithURL(String statusText) throws Exception {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String url = "http://www.disney.com";
		String originalWindow,mentionedUserSelector,URLPostedEvent;
		Element mentionedUser,bizCard;
		
		// Create the Mentions instance of the user to be mentioned
		String beforeText = statusText + Helper.genDateBasedRandVal();
		String afterText = "URL link " + url;
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUserA, profilesAPIUserA, serverURL, beforeText,afterText + " ");
		
		logger.strongStep("Go to 'What do you want to share?' box and enter some text along with mentioned user's name");
		log.info("INFO: Go to 'What do you want to share?' box and enter some text along  with mentioned user's name");
		UIEvents.switchToStatusUpdateFrame(ui);
		UIEvents.typeBeforeMentionsTextAndTypeMentions(ui, mentions);

		logger.strongStep("Verify that type-ahead result should be returned");
		log.info("INFO: Verify that type-ahead result should be returned");
		UIEvents.waitForTypeaheadMenuToLoad(ui);
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.MentionsTypeaheadSelection));
		
		logger.strongStep("Select a user form type ahead search results");
		log.info("INFO: Select a user form type ahead search results");
		UIEvents.getTypeaheadMenuItemsListAndSelectUser(ui, driver, mentions);
		
		logger.strongStep("Verify @mention user name should appear in blue link");
		log.info("INFO: Verify @mention user name appear in blue link");
		UIEvents.switchToStatusUpdateFrame(ui);
		Element userNameEle = driver.getSingleElement(HomepageUI.userNameLink(testUserA));
		ui.verifyTextColor(userNameEle,new String[] {"#4178be","#325c80"});
			
		logger.strongStep("Enter the remainder of the mentions text along with url link into the status update input field");
		log.info("INFO: Enter the remainder of the mentions text along with url link "+url+" into the status update input field");
		UIEvents.typeAfterMentionsText(ui, mentions);
		
		logger.strongStep("Verify the URL link should appear in blue");
		log.info("INFO: Verify the URL link should appear in blue");
		String URLSelector = HomepageUIConstants.URLLink.replace("PLACEHOLDER", url);
		Element URLEle = driver.getSingleElement(URLSelector);
		ui.verifyTextColor(URLEle,new String[] {"#4178be","#325c80"});
		
		// Switch back to main frame
		ui.switchToTopFrame();
		
		logger.strongStep("URL preview should be displayed if it's present");
		log.info("INFO: URL preview should be displayed if it's present");
		String URLPreviewThumbnailSelector = HomepageUIConstants.URLPreview_EE_All.replace("PLACEHOLDER", url);
		boolean isPreviewAvailable =driver.getFirstElement(HomepageUI.URLPreview("The official home for all things Disney")).isVisible();
		if (isPreviewAvailable) {
			Assert.assertTrue(driver.getFirstElement(URLPreviewThumbnailSelector).isVisible());
		}
		
		logger.strongStep("Post the message along with URL");
		log.info("INFO: Post the message along with URL");
		ui.clickLinkWait(HomepageUIConstants.PostComment);
		ui.fluentWaitTextPresent(Data.getData().postSuccessMessage);
		
		if(statusText.contains("Community"))
		{
			logger.strongStep("The added URL news story should be displayed in I'm Following tab");
			log.info("INFO: The added URL news story should be displayed in I'm Following tab");
			URLPostedEvent = HomepageUIConstants.activityStreamNewsStory.replace("PLACEHOLDER", testUser.getDisplayName()+" posted a message");
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{URLPostedEvent}, null, true);
			
			// Get window handle of parent window
			originalWindow =driver.getWindowHandle();
			
			logger.strongStep("Hover over the mentioned user link");
			log.info("INFO: Hover over the mentioned user link");
			mentionedUserSelector = HomepageUIConstants.mentionedUserLinkInEvent.replace("PLACEHOLDER", testUser.getDisplayName()+" posted a message");
			mentionedUser= driver.getSingleElement(mentionedUserSelector);
			mentionedUser.hover();
			
			logger.strongStep("User's business card should be displayed");
			log.info("INFO: User's business card should be displayed");
			ui.fluentWaitPresent(HomepageUIConstants.bizCard);
			bizCard = driver.getFirstElement(HomepageUIConstants.bizCard);
			Assert.assertTrue(bizCard.isDisplayed());
			
			logger.strongStep("Click on the mentioned user link");
			log.info("INFO: Click on the mentioned user link");
			mentionedUser.click();
		} else {
			logger.strongStep("The added URL news story should be displayed in I'm Following tab");
			log.info("INFO: The added URL news story should be displayed in I'm Following tab");
			URLPostedEvent = HomepageUIConstants.activityStreamNewsStory.replace("PLACEHOLDER",testUser.getDisplayName() + " " + beforeText + " @" + testUserA.getDisplayName() + " " + afterText);
			HomepageValid.verifyElementsInAS(ui, driver, new String[] { URLPostedEvent }, null, true);

			// Get window handle of parent window
			originalWindow = driver.getWindowHandle();
			
			logger.strongStep("Hover over the mentioned user link");
			log.info("INFO: Hover over the mentioned user link");
			mentionedUserSelector = HomepageUIConstants.mentionedUserLinkInEvent.replace("PLACEHOLDER",testUser.getDisplayName() + " " + beforeText + " @" + testUserA.getDisplayName() + " " + afterText);
			mentionedUser = driver.getSingleElement(mentionedUserSelector);
			mentionedUser.hover();
			
			logger.strongStep("User's business card should be displayed");
			log.info("INFO: User's business card should be displayed");
			ui.fluentWaitPresent(HomepageUIConstants.bizCard);
			bizCard = driver.getFirstElement(HomepageUIConstants.bizCard);
			Assert.assertTrue(bizCard.isDisplayed());
			
			logger.strongStep("Click on the mentioned user link");
			log.info("INFO: Click on the mentioned user link");
			mentionedUser.click();
		}

		logger.strongStep("Verify new window should be opened and user should be navigated to the profile page");
		log.info("INFO: Verify new window should be opened and user should be navigated to the profile page");
		ui.switchTabs(2, 1);
		ui.waitForPageLoaded(driver);
		log.info("info is:"+driver.getTitle());
		Assert.assertEquals(driver.getTitle(), testUserA.getDisplayName()+" Profile");
		ui.closeCurrentBrowserWindow();
		
		logger.strongStep("Click on posted URL link");
		log.info("INFO: Click on posted URL link");
		ui.switchToWindowByHandle(originalWindow);
		driver.getFirstElement(HomepageUI.newStoryPostedLink(url)).click();
		
		logger.strongStep("Verify new window should be opened and user should be navigated to the disney page");
		log.info("INFO: Verify new window should be opened and user should be navigated to the disney page");
		ui.switchTabs(2, 1);
		ui.waitForPageLoaded(driver);
		Assert.assertEquals(driver.getTitle(), "Disney.com | The official home for all things Disney");
		ui.closeCurrentBrowserWindow();
		ui.switchToWindowByHandle(originalWindow);
		
	}
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify mentions notification for Activity Entry and Activity To-Do item is displayed in My notification view of mentioned user</li>
	 *<li><B>Step:</B> Login to Homepage with testUser</li> 
	 *<li><B>Step:</B> Select the Activities 'Mega Menu' option</li>
	 *<li><B>Verify:</B> Create new activity and verify the same</li>
	 *<li><B>Step:</B> Click on the Members link and change access to public</li>
	 *<li><B>Step:</B> Select 'Add Entry' for this activity</li>
	 *<li><B>Step:</B> Add title to entry</li>
	 *<li><B>Step:</B> Enter some text before mentioning user</li>
	 *<li><B>Step:</B> Enter user name to be mentioned</li>
	 *<li><B>Verify:</B> Verify that Type-ahead should returns a list of usernames matching what was typed</li>
	 *<li><B>Step:</B> Select a user form type ahead search results</li>
	 *<li><B>Step:</B> Enter the remainder of the mentions text into description field</li>
	 *<li><B>Step:</B> Select 'Save' button</li>
	 *<li><B>Step:</B> Click on Add To Do button</li>
	 *<li><B>Step:</B> Add title to To Do Item</li>
	 *<li><B>Step:</B> Click on To Do More Options</li>
	 *<li><B>Step:</B> Repeat above steps</li>
	 *<li><B>Step:</B> Logout </li>
	 *<li><B>Step:</B> Now Login to with user mentioned above that is testUserA</li>
	 *<li><B>Step:</B> Navigate to My Notifications view </li>
	 *<li><B>Verify:</B> Verify that the mentions event for Activity entry is displayed in the My Notifications->For Me view</li>
	 *<li><B>Verify:</B> Verify that the mentions event for Activity To-do item is displayed in the My Notifications->For Me view</li>
	 *</ul>
	 */	
	@Test(groups = {"regression"})
	public void mentions_ActivityEntryAndToDo_MyNotificationView() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName=ui.startTest();
		
		// Create activity builder object
		BaseActivity activity = new BaseActivity.Builder(Data.getData().Start_An_Activity_InputText_Name_Data + Helper.genDateBasedRandVal()).build();

		//Create New entry for activity created above
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal()).build();
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "ToDo" + Helper.genDateBasedRandVal()).build();
				
		// Create mentions object
		Mentions mentionsInActEntry = MentionsBaseBuilder.buildBaseMentions(testUserA, profilesAPIUserA, serverURL, "MentionInActEntry"+Helper.genDateBasedRandVal(),"after text");
		Mentions mentionsInActToDo = MentionsBaseBuilder.buildBaseMentions(testUserA, profilesAPIUserA, serverURL, "MentionInActToDo"+Helper.genDateBasedRandVal(),"after text");
		
		logger.strongStep("Login to Homepage with testUser");
		log.info("INFO:Login to Homepage with "+testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		log.info("INFO: Select the Activities 'Mega Menu' option");
		logger.strongStep("Select the Activities 'Mega Menu' option");
		ui.gotoMegaMenuApps("Activities");
		
		log.info("Create new activity" + activity.getName() +"and verify the same");
		logger.strongStep("Create new activity" + activity.getName() +"and verify the same");
		activity.create(actUi);
		String activityCreatedMessage = Data.getData().Activity_Created_Message.replace("PLACEHOLDER", activity.getName());
		Assert.assertTrue(actUi.isTextPresent(activityCreatedMessage));
		
		log.info("INFO: Click on the Members link and change access to public");
		logger.strongStep("INFO: Click on the Members link and change access to public");
		actUi.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Members);
		actUi.fluentWaitElementVisibleOnce(ActivitiesUIConstants.Change_Access);
		actUi.getFirstVisibleElement(ActivitiesUIConstants.Change_Access).click();
		actUi.fluentWaitElementVisibleOnce(ActivitiesUIConstants.PublicAccess_RadioBtn);
		actUi.getFirstVisibleElement(ActivitiesUIConstants.PublicAccess_RadioBtn).click();
		actUi.clickSaveButton();
		
		log.info("INFO: Select 'Add Entry' for this activity");
		logger.strongStep("Select 'Add Entry' for this activity");
		actUi.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Outline);
		actUi.clickLinkWait(ActivitiesUIConstants.New_Entry);
		actUi.fluentWaitPresent(ActivitiesUIConstants.New_Entry_InputText_Title);
		
		log.info("INFO: Add title to entry");
		logger.strongStep("Add title to entry");
		actUi.clearText(ActivitiesUIConstants.New_Entry_InputText_Title);
		actUi.typeText(ActivitiesUIConstants.New_Entry_InputText_Title, entry.getTitle());
		
		// Enter and verify mentions in activity entry
		enterAndVerifyMentions(mentionsInActEntry);
		
		ui.fluentWaitTextPresent(entry.getTitle());
		log.info("INFO: Created Entry: " + entry.getTitle());

		log.info("INFO: Click on Add To Do button");
		logger.strongStep("Click on Add To Do button");
		ui.clickLinkWait(ActivitiesUIConstants.AddToDo);

		log.info("INFO: Add title to To Do Item");
		logger.strongStep("Add title to To Do Item");
		ui.clearText(ActivitiesUIConstants.ToDo_InputText_Title);
		ui.typeText(ActivitiesUIConstants.ToDo_InputText_Title, toDo.getTitle());

		log.info("INFO: Click on To Do More Options");
		logger.strongStep("Click on To Do More Options");
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);
		
		// Enter and verify mentions in activity To Do
		enterAndVerifyMentions(mentionsInActToDo);

		ui.fluentWaitTextPresent(toDo.getTitle());
		log.info("INFO: Created to do item: " + toDo.getTitle());
		LoginEvents.gotoHomeAndLogout(ui);

		logger.strongStep("Login to with user mentioned above");
		log.info("INFO: Login to with user "+testUserA.getDisplayName()+" mentioned above");
		LoginEvents.loginAndGotoMyNotifications(ui, testUserA, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = ActivityNewsStories.getMentionedYouInTheEntryNewsStory(ui, entry.getTitle(), activity.getName(), testUser.getDisplayName());
		String mentionsText = mentionsInActEntry.getBeforeMentionText() + " @" + testUserA.getDisplayName() + " " + mentionsInActEntry.getAfterMentionText();
				
		// Verify that the mentions event for Activity entry is displayed in the My Notifications->For Me view
		log.info("INFO: Verify that the mentions event for Activity entry is displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the mentions event for Activity entry is displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[] { mentionedYouEvent, mentionsText }, null, true);
		
		// Create the elements to be verified
		String mentionedYouInActivityToDo= ActivityNewsStories.getMentionedYouInTheToDoItemNewsStory(ui, toDo.getTitle(), activity.getName(), testUser.getDisplayName());
		String mentionedTextInActivityToDo = mentionsInActToDo.getBeforeMentionText() + " @" + testUserA.getDisplayName() + " " + mentionsInActToDo.getAfterMentionText();
				
		// Verify that the mentions event for Activity To-do item is displayed in the My Notifications->For Me view
		log.info("INFO: Verify that the mentions event for Activity To-do item is displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the mentions event for Activity To-do item is displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[] { mentionedYouInActivityToDo, mentionedTextInActivityToDo }, null, true);
		
		ui.endTest();
		
	}

	private void enterAndVerifyMentions(Mentions mentions) throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		log.info("INFO: Enter some text before mentioning user");
		logger.strongStep("Enter some text before mentioning user");
		ui.clickLinkWait("//div[@class='cke_inner cke_reset']");
		ui.typeTextWithDelay("//div[@class='cke_inner cke_reset']", mentions.getBeforeMentionText() + " ");

		log.info("INFO: Enter user name to be mentioned");
		logger.strongStep("Enter user name "+testUserA.getDisplayName()+" to be mentioned");
		UIEvents.typeMentionsOrPartialMentions(ui, mentions,mentions.getUserToMention().getDisplayName().length());
		UIEvents.waitForTypeaheadMenuToLoad(ui);
		
		log.info("INFO: Verify that Type-ahead should returns a list of usernames matching what was typed");
		logger.strongStep("Enter user name "+testUserA.getDisplayName()+" to be mentioned");
		List<Element> listOfTypeAheadSearchResults = UIEvents.getTypeaheadMenuItemsList(ui, driver);
		for (int i = 0; i < listOfTypeAheadSearchResults.size(); i++) {
			Assert.assertTrue(listOfTypeAheadSearchResults.get(i).getText().contains(mentions.getUserToMention().getDisplayName()));
		}
		
		logger.strongStep("Select a user form type ahead search results");
		log.info("INFO: Select a user form type ahead search results");
		UIEvents.getTypeaheadMenuItemsListAndSelectUser(ui, driver, mentions);
	
		logger.strongStep("Enter the remainder of the mentions text into description field");
		log.info("INFO: Enter the remainder of the mentions textinto description field");
		UIEvents.switchToStatusUpdateFrame(ui);
		UIEvents.typeAfterMentionsText(ui, mentions);
		ui.switchToTopFrame();
		
		logger.strongStep("Select 'Save' button");
		log.info("INFO: Select 'Save' button");
		ui.clickSaveButton();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Verify mentions notification for comment on file is displayed in My notification view of mentioned user</li>
	 * <li><B>Step:</B>[ API] User 1(testUser) will now post a comment to the file with mentions to User 2(testUserA)</li>
	 * <li><B>Step:</B> Log in as User 2(testUserA) and go to the My Notification view</li>
	 * <li><B>Verify:</B> Verify that the mentions entry for file is displayed in the My Notifications->For Me view</li>
	 * <li><B>Verify:</B> Verify that the mentions text is displayed in the My Notifications->For Me view</li>
	 * </ul>
	 */

	@Test(groups = { "regression","cnx8ui-regression"})
	public void mentionInComment_fileShared_MyNotificationView() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		// User 1 will now post a comment to the file with mentions to User 2
		logger.strongStep("User 1 will now post a comment to the file with mentions to User 2 via API");
		log.info("INFO: "+testUser.getDisplayName()+" will now post a comment to the file with mentions to "+testUserA.getDisplayName());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUserA, profilesAPIUserA, serverURL, testName+Helper.genDateBasedRandVal(),"after text");
		FileEvents.addFileMentionsComment(testUser, filesAPIUser, privateSharedFile, mentions);
		
		// Log in as User 2 and go to the My Notification view
		logger.strongStep("Log in as User 2 and go to the My Notification view");
		log.info("INFO: Log in as "+testUserA.getDisplayName()+" 2 and go to the My Notification view");
		LoginEvents.loginAndGotoMyNotifications(ui, testUserA, false);
						
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getMentionedYouInACommentOnAFile(ui, testUser.getDisplayName());
		String mentionsComment = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the comment on file event is displayed in My Notification view
		log.info("INFO: Verify that the mentions entry for file is displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the mentions entry for file is displayed in the My Notifications->For Me view");
		HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseSharedFile);
									
		// Verify that the mentions text is displayed in My Notification view
		log.info("INFO: Verify that the mentions text is displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the mentions text is displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, true);
				
		ui.endTest();
	}
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify mentions notification for blog entry is displayed in My notification view of mentioned user</li>
	 *<li><B>Step:</B>[ API] User 1(testUser) will now mentions to User 2(testUserA) in blog entries's description field</li> 
	 *<li><B>Step:</B> Log in as User 2(testUserA) and go to the My Notification view</li>
	 *<li><B>Verify:</B> Verify that the mentions entry and mentions text for blog entry is displayed in the My Notifications->For Me view</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","mt-exclude","cnx8ui-regression"})
	public void mentions_standaloneBlog_entry_MyNotificationView() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName=ui.startTest();
		
		blogApiOwner = new APIBlogsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());
		BaseBlog baseBlog = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		
		Blog standaloneBlog = BlogEvents.createBlog(testUser, blogApiOwner, baseBlog);

		// User 1 will now post a comment to the file with mentions to User 2
		logger.strongStep("User 1 will now mentions to User 2 in blog entries's description field via API");
		log.info("INFO: "+testUser.getDisplayName()+" will now mentions to "+testUserA.getDisplayName()+" in blog entries's description field via API");
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUserA, profilesAPIUserA, serverURL, testName+Helper.genDateBasedRandVal(),"after text");
		BlogPost blogPost = BlogEvents.createBlogPostWithMention(testUser, blogApiOwner, standaloneBlog, mentions);
		
		// Log in as User 2 and go to the My Notification view
		logger.strongStep(" Log in as User 2(testUserA) and go to the My Notification viewI");
		log.info("INFO:  Log in as "+testUserA.getDisplayName()+" and go to the My Notification viewI");
		LoginEvents.loginAndGotoMyNotifications(ui, testUserA, false);
		
		// Create the elements to be verified
		String mentionedYouInBlogEntry= BlogNewsStories.getMentionedYouInABlogEntryNewsStory(ui, blogPost.getTitle(), baseBlog.getName(), testUser.getDisplayName());
		String mentionedTextBlogEntry = mentions.getBeforeMentionText() + " @" + testUserA.getDisplayName() + " " + mentions.getAfterMentionText();
								
		// Verify that the mentions entry for blog entry is displayed in the My Notifications->For Me view
		log.info("INFO: Verify that the mentions entry for blog entry is displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the mentions entry for blog entry is displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[] { mentionedYouInBlogEntry, mentionedTextBlogEntry }, null, true);		ui.endTest();
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify mentions notification for forum topic is displayed in My notification view of mentioned user</li>
	 *<li><B>Step:</B>[ API] User 1(testUser) will now post a comment to the file with mentions to User 2(testUserA)</li> 
	 *<li><B>Step:</B> Log in as User 2(testUserA) and go to the My Notification view</li>
	 *<li><B>Verify:</B> Verify that the mentions event and the forum topic description with mentions are displayed in the My Notifications->For Me view</li>
	 *</ul>
	 */
	
	@Test(groups = { "regression","mt-exclude","cnx8ui-regression"})
	public void mention_standalone_forumTopic_MyNotificationView() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		APIForumsHandler forumsAPIUser = new APIForumsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		// User 1 will now create a standalone forum
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		Forum standaloneForum = ForumEvents.createForum(testUser, forumsAPIUser, baseForum);

		// User 1 will now create a forum topic in the standalone forum which includes mentions to User 2
		logger.strongStep("User 1 will now create a forum topic in the standalone forum which includes mentions to User 2 via API");
		log.info("INFO: "+testUser.getDisplayName()+" will now create a forum topic in the standalone forum which includes mentions to "+testUserA.getDisplayName()+" via API");
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUserA, profilesAPIUserA, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumEvents.createForumTopicWithMentions(baseForumTopic, mentions, testUser, forumsAPIUser);
		
		// Log in as User 2 and go to the My Notification view
		logger.strongStep(" Log in as User 2(testUserA) and go to the My Notification viewI");
		log.info("INFO:  Log in as "+testUserA.getDisplayName()+" and go to the My Notification viewI");
		LoginEvents.loginAndGotoMyNotifications(ui, testUserA, false);
		
		// Create the news stories to be verified
		String mentionEvent = ForumNewsStories.getMentionedYouInATopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser.getDisplayName());
		String topicDescriptionWithMentions = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and the forum topic description with mentions are displayed in the My notification view
		log.info("INFO: Verify that the mentions event and the forum topic description with mentions are displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the mentions event and the forum topic description with mentions are displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionEvent, topicDescriptionWithMentions}, null, true);
				
		ui.endTest();
	}
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify mentions notification for wiki page is displayed in My notification view of mentioned user</li>
	 *<li><B>Step:</B>[ API] User 1(testUser) add a wiki page mentioning User 2(testUserA)</li> 
	 *<li><B>Step:</B> Log in as User 2(testUserA) and go to the My Notification->For Me view</li>
	 *<li><B>Verify:</B> Verify that the mentions event and wiki page with mentions are displayed in the My Notifications->For Me view</li>
	 *</ul>
	 */	

	@Test(groups = { "regression","mt-exclude","cnx8ui-regression"})
	public void mention_publicWiki_wikiPage_MyNotificationView() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		APIWikisHandler wikisAPIUser,wikisAPIUserA;
		wikisAPIUser = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		wikisAPIUserA = new APIWikisHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		// User 1 create a public wiki
		BaseWiki basePublicWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		Wiki publicWiki = WikiEvents.createWiki(basePublicWiki, testUser, wikisAPIUser);

		// User 1 add a wiki page mentioning User 2
		logger.strongStep("User 1 add a wiki page mentioning User 2");
		log.info("INFO: "+testUser.getDisplayName()+" add a wiki page mentioning "+testUserA.getDisplayName()+"via API");
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUserA, profilesAPIUserA, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageWithMentionsToOneWikiFollower(publicWiki, baseWikiPage, mentions, wikisAPIUserA, testUser, wikisAPIUser);
		
		// Log in as User 2 to theMy notification view
		logger.strongStep(" Log in as User 2(testUserA) and go to the My Notification viewI");
		log.info("INFO:  Log in as "+testUserA.getDisplayName()+" and go to the My Notification viewI");
		LoginEvents.loginAndGotoMyNotifications(ui, testUserA, false);
		
		// Create the news story to be verified
		String newsStory = WikiNewsStories.getMentionedYouInTheWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicWiki.getName(), testUser.getDisplayName());
		String contentWithMentions = baseWikiPage.getDescription().trim() + ". " + mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
			
		// Verify that the mentions event and the wiki page description with mentions are displayed in the My Notifications->For Me view
		log.info("INFO: Verify that the mentions event and the forum topic description with mentions are displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the mentions event and the forum topic description with mentions are displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, contentWithMentions}, null, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify the Mention comments in Home Page Status Update</li>
	 *<li><B>Step:</B> Login to application and Create a Activity</li> 
	 *<li><B>Step:</B> Navigate to HomePage and Click On status Update</li>
	 *<li><B>Step:</B> Add a comment with Mentions in Status Update and Save the Comment</li>
	 *<li><B>Verify:</B> Verify the Comment with Mentions is displayed</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "cnx8ui-regression"})
	public void verifyCreateCommentsWithMentionsInStatusUpdates() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
				
		ui.startTest();

		//Load component and login
		logger.strongStep("INFO: Load Homepage and login");
		log.info("INFO: Load Homepage and login");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		//click Status Updates
		logger.strongStep("INFO: Click 'Status Updates' in the left navigation menu");
		log.info("INFO: Click Updates on left menu");
		ui.gotoStatusUpdates();
		
		logger.strongStep("INFO: Fill in the comment form");
		log.info("INFO: Fill in the comment form");
		ui.waitForCkEditorReady();
		WebDriver wd = (WebDriver) driver.getBackingObject();
		ui.switchToFrameBySelector(BaseUIConstants.StatusUpdate_iFrameNew);
		ui.fluentWaitPresent("//body");
		wd.findElement(By.xpath("//body")).sendKeys("@"+testUserB.getDisplayName());
		ui.switchToTopFrame();
		ui.clickLinkWithJavascript(BaseUIConstants.searchlinkDropdown);
		try {
			ui.selectUserFromTypeAheadSearchResult("@"+testUserB.getDisplayName());
		} catch (StaleElementReferenceException e) {
			// type ahead list refreshed so let's try to select user again
			log.info("Type ahead list refreshed, will select user again again.");
			ui.selectUserFromTypeAheadSearchResult("@"+testUserB.getDisplayName());
		}
	
		logger.strongStep("INFO: Click n Save button");
		log.info("INFO: Click n Save button");
        ui.clickLinkWithJavascript(HomepageUIConstants.PostStatusOld);
        ui.fluentWaitTextPresent("The message was successfully posted.");
        
        logger.strongStep("INFO: Verify that the mention comment is present");
		log.info("INFO: Verify that the mention comment exists");
		Assert.assertTrue(ui.fluentWaitTextPresent("@" + testUserB.getDisplayName()), "ERROR: Comment not found");
		
		Element mentionLink = driver.getFirstElement(BaseUIConstants.mentionLink.replace("PLACEHOLDER", "@" + testUserB.getDisplayName()));

		Assert.assertTrue(mentionLink.isDisplayed(), "ERROR: Mention link not present");

		ui.endTest();	
	}


}
