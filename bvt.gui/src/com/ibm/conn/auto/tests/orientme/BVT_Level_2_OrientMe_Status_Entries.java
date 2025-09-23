package com.ibm.conn.auto.tests.orientme;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.OrientMeUI;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.conn.auto.webui.constants.OrientMeUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_Level_2_OrientMe_Status_Entries extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_OrientMe_Status_Entries.class);
	private TestConfigCustom cfg;
	private OrientMeUI omUI;
	private HomepageUI homepageUI;
	private ItmNavCnx8 itmNavCnx8;

	private User testUserA;
	private User testUserB;
	private User testUserC;
	private SearchAdminService adminService;
	private User searchAdmin;
	private String serverUrl;
	private Community community;
	private APICommunitiesHandler apiCommTestUserA;
	private APICommunitiesHandler apiCommTestUserB;
	private APIProfilesHandler apiProfilesTestUserA;
	private APIFileHandler fileOwner;
	Map<String, String> resourceStrings = new HashMap<String, String>();
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		omUI = OrientMeUI.getGui(cfg.getProductName(), driver);
		homepageUI = HomepageUI.getGui(cfg.getProductName(), driver);
		itmNavCnx8 = new ItmNavCnx8(driver);

		testUserA = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();
		testUserC = cfg.getUserAllocator().getUser();
		serverUrl = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverUrl);
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		adminService = new SearchAdminService();
		apiCommTestUserA = new APICommunitiesHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());	
		apiCommTestUserB = new APICommunitiesHandler(serverUrl, 
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		apiProfilesTestUserA = new APIProfilesHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		fileOwner = new APIFileHandler(serverUrl, 
				testUserA.getEmail(), testUserA.getPassword());
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Test status message posted from Top updates tab in OrientMe displays success message. Verify post links : hashtag, mentions and like</li>
	*<li><B>Step:</B>Log in as UserA, From 'Top Updates' tab, click on the 'Share something' card. Enter some text with #_hashtag and @_mention 2 users. Click Post. </li>
	*<li><B>Verify:</B>A success message displays: 'Your status update was successfully posted'. Get back to it later in Latest Updates</li>
	*<li><B>Step:</B>Click hashtag link</li>
	*<li><B>Verify:</B>Updates Search Results page displays entry containing the hashtag appears </li>
	*<li><B>Step:</B>Click like post</li>
	*<li><B>Verify:</B>The number to the right of the heart increased by 1 </li>
	*<li><B>Step:</B>Click unlike post</li>
	*<li><B>Verify:</B>The number to the right of the heart decreased by 1 </li>
	*<li><B>Step:</B>Hover over a business card icon</li>
	*<li><B>Verify:</B>Verify business card is displayed </li>
	*/
	
	@Test(groups = {"regression"})
	public void statusFromTopUpdatesTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String statusFrom="Top Updates Tab";
		omUI.startTest();
		
		String message = "OM " + Helper.genRandString(8);
		String hashtag="hashtag_" + Helper.genRandString(8);
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.goToOrientMe(testUserA, false);

		postStatusUpdate(message,hashtag,statusFrom);
		
		omUI.endTest();
	}
		
	/**
	*<ul>
	*<li><B>Info:</B>Test status message posted from Latest updates tab in OrientMe displays success message. Verify post links : hashtag, mentions and like</li>
	*<li><B>Step:</B>Log in as UserA, From 'Latest Updates' tab, click on the 'Share something' card. Enter some text with #_hashtag and @_mention 2 users. Click Post. </li>
	*<li><B>Verify:</B>A success message displays: 'Your status update was successfully posted'. Get back to it later in Latest Updates</li>
	*<li><B>Step:</B>Click hashtag link</li>
	*<li><B>Verify:</B>Updates Search Results page displays entry containing the hashtag appears </li>
	*<li><B>Step:</B>Click like post</li>
	*<li><B>Verify:</B>The number to the right of the heart increased by 1 </li>
	*<li><B>Step:</B>Click unlike post</li>
	*<li><B>Verify:</B>The number to the right of the heart decreased by 1 </li>
	*<li><B>Step:</B>Hover over a business card icon</li>
	*<li><B>Verify:</B>Verify business card is displayed </li>
	*/
	@Test(groups = {"level2", "cplevel2"})
	public void statusFromLatestUpdatesTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());	
		String statusFrom="Latest Updates Tab";
		omUI.startTest();
		
		String message = "OM " + Helper.genRandString(8);
		String hashtag="hashtag_" + Helper.genRandString(8);
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.loginAndGoTOLatestUpdatesTab(testUserA, false);

		postStatusUpdate(message,hashtag,statusFrom);
		
		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Test status message cannot exceed 1000 characters.</li>
	*<li><B>Step:</B>Log in as UserA, From 'Top Updates' tab, click on the 'Share something' card.  Enter text more than 1000 characters.</li>
	*<li><B>Verify:</B>An error message displays indicating that the character limit has been exceeded</li>
	*/
	@Test(groups = {"regression"})
	public void statusExceedsCharacterLimitsTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		omUI.startTest();
		
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.goToOrientMe(testUserA, false);
		
		logger.strongStep(testUserA.getDisplayName() + " post a status entry with more that 1000 characters");
		log.info("INFO: " + testUserA.getDisplayName() + " post a status entry with more that 1000 characters");
		String message = Helper.genRandString(1001);
		omUI.clickLink(OrientMeUIConstants.shareSomething);
		log.info("Type status: " + message);
		
		Assert.assertTrue(message.length()>1000,"Length of message is " + message.length());
		log.info("Status length is : " + message.length());
		omUI.getFirstVisibleElement(OrientMeUIConstants.shareSomethingBox).type(message);
		log.info("Verify error popup is displayed with message: Exceeds character limit by 1");
		omUI.fluentWaitElementVisible(OrientMeUIConstants.errorMessageText);
		Assert.assertEquals(omUI.getElementText(OrientMeUIConstants.errorMessageText),"Exceeds character limit by 1", "ERROR: Exceeds character limit by 1 error message is should be displayed");
		
		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Confirm Delete status functionality.</li>
	*<li><B>Step:</B>Create a new status entry. Click on the 'More actions' icon at the lower right corner. Click on 'Delete'.</li>
	*<li><B>Verify:</B>'Delete this Status Update' confirmation dialog displays.</li>
	*<li><B>Step:</B>Click the 'Cancel' button.</li>
	*<li><B>Verify:</B>The confirmation dialog goes away and the status entry does not get deleted.</li>
	*<li><B>Step:</B>Click the 'Delete' button again. Click 'Delete' on the confirmation dialog.</li>
	*<li><B>Verify:</B>Message displays: Your status update was successfully deleted, and the status entry no longer appears on the page.</li>
	*/
	@Test(groups = {"regression"})
	public void statusEntryDeleteActionsTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		omUI.startTest();
		
		String message = "OM " + Helper.genRandString(8);
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.loginAndGoTOLatestUpdatesTab(testUserA, false);
		
		logger.strongStep(testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		log.info("INFO: " + testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		omUI.postStatus(message,"Your status update was successfully posted.");
		omUI.clickLinkWait(OrientMeUIConstants.latestUpdate);
		omUI.deleteAction(message);		
		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Confirm Repost status functionality.</li>
	*<li><B>Step:</B>Create a new status entry. Click on the 'More actions' icon at the lower right corner. Click on 'Repost this Update'. </li>
	*<li><B>Verify:</B>message displayed : The update was successfully reposted to your followers.</li>
	*/
	@Test(groups = {"regression"})
	public void statusEntryRepostActionsTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		omUI.startTest();
		
		String message = "OM " + Helper.genRandString(8);
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.loginAndGoTOLatestUpdatesTab(testUserA, false);
		
		logger.strongStep(testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		log.info("INFO: " + testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		omUI.postStatus(message,"Your status update was successfully posted.");
		// Refresh browser to workaround NPTFE defect ORIENTME-6
		UIEvents.refreshPage(driver);
		omUI.clickLinkWait(OrientMeUIConstants.latestUpdate);
		omUI.repostAction(message);		
		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>URL link in a status entry is clickable and onclick redirects user to the correct page </li>
	*<li><B>Step:</B>Create a new status entry with a URL link.</li>
	*<li><B>Verify:</B>The entry saved without error. URL preview displays ok and link is clickable.</li>
	*<li><B>Step:</B>Click on the URL link.</li>
	*<li><B>Verify:</B>User is brought to the correct page.</li>
	*/
	@Test(groups = {"regression"})
	public void statusWithLinkTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		omUI.startTest();
		
		String message = "OM " + Helper.genRandString(8);
		String URL = "www.google.com";
		String status=message + " " + URL;
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.loginAndGoTOLatestUpdatesTab(testUserA, false);
		
		logger.strongStep(testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		log.info("INFO: " + testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		omUI.postStatus(status,"Your status update was successfully posted.");	
		omUI.verifyPostLink(message, URL);
		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Top Updates/Latest update tab for UserA displays community created by UserB, UserA was added as a member, community created by UserA and any other updates made by UserA and Any other updates like status update made by UserA.</li>
	*<li><B>Pre-req:</B>UserA has set up at least 1 user (UserB) as Important people</li>
	*<li><B>Pre-req:</B>UserA has followed at least 1 community</li>
	*<li><B>Pre-req:</B>UserB creates a community and adds UserA as a member.</li>
	*<li><B>Pre-req:</B>UserA shares an update</li>
	*<li><B>Step:</B>Log into Orient Me. Click on Latest Updates tab.</li>
	*<li><B>Verify:</B>Content appear on the page: 1) Community created by UserB and UserA was added as a member 2) Community created by UserA 3) Any other updates like status update made by UserA.</li>
	*<li><B>Step:</B>Scroll to the bottom of the Top Updates page.</li>
	*<li><B>Verify:</B>The message displays at the bottom of the page: 'You've reached the end of your update stream'.</li>
	**<li><B>Step:</B>Click on 'Go to Top'.</li>
	*<li><B>Verify:</B>User is brought to the top of the page.</li>
	*/
	@Test(groups = {"regression"})
	public void viewPostLatestUpdatesTabTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		omUI.startTest();
		String message = "OM " + Helper.genRandString(8);

		// populate test data using API
		try {
			resourceStrings = filterTestDataPop(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.loginAndGoTOLatestUpdatesTab(testUserA, false);
		
		viewPosts(message,"Latest Updates Tab");
		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Top Updates/Latest update tab for UserA displays community created by UserB, UserA was added as a member, community created by UserA and any other updates made by UserA and Any other updates like status update made by UserA.</li>
	*<li><B>Pre-req:</B>UserA has set up at least 1 user (UserB) as Important people</li>
	*<li><B>Pre-req:</B>UserA has followed at least 1 community</li>
	*<li><B>Pre-req:</B>UserB creates a community and adds UserA as a member.</li>
	*<li><B>Pre-req:</B>UserA shares an update
	*<li><B>Step:</B>Log into Orient Me. Click on Top Updates tab.</li>
	*<li><B>Verify:</B>Content appear on the page: 1) Community created by UserB and UserA was added as a member 2) Community created by UserA.</li>
	*<li><B>Step:</B>Scroll to the bottom of the Top Updates page.</li>
	*<li><B>Verify:</B>The message displays at the bottom of the page: 'You've reached the end of your update stream'.</li>
	**<li><B>Step:</B>Click on 'Go to Top'.</li>
	*<li><B>Verify:</B>User is brought to the top of the page.</li>
	*/
	@Test(groups = {"level2", "cplevel2"})
	public void viewPostTopUpdatesTabTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		omUI.startTest();
		String message = "OM " + Helper.genRandString(8);

		// populate test data using API
		try {
			resourceStrings = filterTestDataPop(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		LoginEvents.loginAndGoToOMTopUpdatesTab(homepageUI, omUI, testUserA, driver, false);
		
		viewPosts(message,"Top Updates Tab");
		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Status message posted with an image attached is successfully displayed in the file preview (FiDO) without an error </li>
	*<li> TODO: File upload part from screen. Currently it is done using API. </li>
	*<li><B>Step:</B>Log in as UserA, Post a status message with image attached to it. </li>
	*<li><B>Verify:</B>Newly posted status message is displayed on latest update tab</li>
	*<li><B>Step:</B>Click on image link</li>
	*<li><B>Verify:</B>Image attached is successfully displayed in the file preview (FiDO) without an error</li>
	*/
	@Test(groups = {"regression"})
	public void statusWithAttachment() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		omUI.startTest();
		
		String message = "OM " + Helper.genRandString(8);
		String image = Data.getData().file1;
		String extn=".jpg";
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(image, extn, ShareLevel.EVERYONE);
		FileEntry publicFile = FileEvents.addFile(baseFile, testUserA, fileOwner);
		
		resourceStrings = postStatusWithImage(message, publicFile);
		
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.loginAndGoTOLatestUpdatesTab(testUserA, false);
		
		String imageSelector= OrientMeUIConstants.imagePost.replace("##innertext##", message);
		omUI.fluentWaitElementVisible(imageSelector);
		omUI.getFirstVisibleElement(imageSelector).click();
		driver.isElementPresent(OrientMeUIConstants.iframe);
		driver.switchToFrame().selectSingleFrameBySelector(OrientMeUIConstants.iframe);
		
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.fileviewer_previewLinkTitle).getText(), baseFile.getRename() + extn, "ERROR: File Viewer banner title should match the file title");
		
		omUI.endTest();
	}
	
	/**
	 * <li>Click on the 'Share something' card. Enter some text with #_hashtag and @_mention 2 users. Click Post. Verify status is posted successfully with hashtag link and 2 mentions </li>
	 * @param message
	 * @param hashtag
	 * @param statusFrom
	 * @successMessage
	 */
	public void postStatusUpdate(String message, String hashtag, String statusFrom) {
		String successMessage;
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		if(statusFrom.equals("Latest Updates Tab")){
			successMessage="Your status update was successfully posted.";
		}
		else{
			successMessage="Your status update was successfully posted. Get back to it later in Latest Updates";
		}
		
		logger.strongStep(testUserA.getDisplayName() + " post a status entry with hashtag #" + hashtag + " from " +statusFrom);
		log.info("INFO: " + testUserA.getDisplayName() + " post a status entry with hashtag #" + hashtag + " and mention " + testUserB.getDisplayName()+ ", " + testUserC.getDisplayName() + " from " + statusFrom);
		omUI.postStatusWithHashtagMentions(message, hashtag, testUserB.getDisplayName(), testUserC.getDisplayName(), successMessage);
		
		if(statusFrom.equals("Top Updates Tab")){
			logger.strongStep("Go to Latest Updates to find the newly posted status");
			log.info("INFO: Go to Latest Updates to find the status");
			omUI.clickLinkWait(OrientMeUIConstants.latestUpdate);
			omUI.getCloseTourScript();
		}
		omUI.fluentWaitTextPresent(message);
		
		//Entry needs to have been indexed before it appears on the search results page
		try {
			adminService.indexNow("status_updates", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		omUI.likeUnlikePost(message);
		message = message + " #" + hashtag + " @" + testUserB.getDisplayName() + " @"+ testUserC.getDisplayName();
		omUI.verifyMentions(message, testUserB, testUserC, hashtag);
		omUI.verifyHashtag(message, hashtag);
	}
	
	/**
	*<ul>
	*<li>UserA has followed at least 1 community</li>
	*<li>UserB creates a community and adds UserA as a member.</li>
	*<li>UserA shares an update</li>
	*<li>Content appear on the page: 1) Community created by UserB and UserA is added as a member 2) Community created by UserA 3) Any other updates like status update made by UserA.</li>
	*<li>Scroll to the bottom of the Top Updates page.</li>
	*<li>The message displays at the bottom of the page: 'You've reached the end of your update stream'.</li>
	**<li>Click on 'Go to Top'.</li>
	*<li>User is brought to the top of the page.</li>
	*/
	public void viewPosts(String message, String statusFrom) {
		boolean isScrollNeeded=false;
		
		// TODO: Divy - this doesn't work on CPBVT
//		omUI.goToOrientMe(testUserA, false);

		// Data setup
		WebElement user = itmNavCnx8.getItemInImportantToMeList(testUserB.getDisplayName(), false);
		if (user == null)  {
			log.info("Add " + testUserB.getDisplayName() + " to the Important to Me list.");
			itmNavCnx8.addImportantItem(testUserB.getDisplayName(), true);
		} else {
			log.info(testUserB.getDisplayName() + " is already in the Important to Me list.");
		}
		
		log.info("INFO: " + testUserA.getDisplayName() + " is added to the community '" + resourceStrings.get("community") + "' created by user " + testUserB.getDisplayName());
		log.info("INFO: " + testUserA.getDisplayName() + " created a community '" + resourceStrings.get("followCommunity") + "' and " + testUserB.getDisplayName() + " is following it");
		if(!statusFrom.equalsIgnoreCase("Top Updates Tab"))
			log.info("INFO: " + testUserA.getDisplayName() + " posted a status '" + message + "' entry from Latest Updates tab");
		omUI.waitForPageLoaded(driver);
		
		//Verification
		//Sometimes one or more updates are not displayed on the screen after page load. So it is important to scroll page till bottom to load all updates
		if(!statusFrom.equalsIgnoreCase("Top Updates Tab")){
			if(omUI.isTextPresent(message)){
				Assert.assertTrue(omUI.fluentWaitTextPresent(message),"Newly posted status with message '" + message + "' is not displayed on the screen.");
				log.info("INFO: Newly posted status with message '" + message + "' is displayed on the screen.");
			}
			else{
				isScrollNeeded=true;
			}
		}
		
		if(omUI.isTextPresent(resourceStrings.get("community")) && omUI.isTextPresent(resourceStrings.get("followCommunity"))){
			Assert.assertEquals(omUI.getElementText(OrientMeUIConstants.communityUpdate.replace("##innertext##", resourceStrings.get("community"))).replace(".", ""),
					testUserB.getDisplayName() + " added you to the " + resourceStrings.get("community") + " community",
					"Status " + testUserB.getDisplayName() + " added you to the " + resourceStrings.get("community") + " community is displayed on the page");
			log.info("INFO: Status " + testUserB.getDisplayName() + " added you to the " + resourceStrings.get("community") + " community is displayed on the page.");
			Assert.assertTrue(omUI.fluentWaitTextPresent(resourceStrings.get("followCommunity")),"Newly created community with message '" + resourceStrings.get("followCommunity") + "' is displayed on the screen.");
			log.info("INFO: Newly created community with message '" + resourceStrings.get("followCommunity") + "' is displayed on the screen.");
		}
		else{
			isScrollNeeded=true;
		}
		
		//Scrolling
		if(!isScrollNeeded){
			omUI.filterUpdates(testUserB.getDisplayName(), true);
		}
		else{
			try {
				adminService.indexNow("status_updates", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
				adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			driver.navigate().refresh();
			omUI.waitForPageLoaded(driver);
		}
		
		log.info("INFO: Scroll to the bottom of the Top Updates page");
		omUI.scrollThroughPage();
		log.info("INFO: Verify text 'You've reached the end of your update stream' is displayed a the bottom of the page");
		Assert.assertTrue(omUI.fluentWaitTextPresent("You've reached the end of your update stream"),"Text 'You've reached the end of your update stream' not displayed a the bottom of the page");
		log.info("INFO: Text 'You've reached the end of your update stream' is displayed a the bottom of the page");
		
		log.info("INFO: Scroll upwards slightly to get 'Go To Top' button to appear on the screen");
		Object offset=driver.executeScript("return window.pageYOffset;");
		long bottomOffset= ((Number) offset).longValue() -50;
		driver.executeScript("window.scrollTo(0, " + bottomOffset + ");");
		//check if user is already to the top of the page. This happens when there are less records on update page
		offset= driver.executeScript("return window.pageYOffset;");
		String topOffset= offset.toString();
		if(!topOffset.equals("0")){		
			omUI.fluentWaitElementVisible(OrientMeUIConstants.goToTop);
			log.info("INFO: Click on 'Go To Top' button");
			omUI.clickLink(OrientMeUIConstants.goToTop);
			omUI.waitForPageLoaded(driver);
			log.info("INFO: Verify user is brought to the top of the page");
			offset= driver.executeScript("return window.pageYOffset;");
			topOffset= offset.toString();
			Assert.assertEquals(topOffset,"0","ERROR: 'Go To Top' button should brought to the top of the page");
		}
		
		log.info("INFO: User is brought to the top of the page");
		
		if(isScrollNeeded){
			log.info("INFO: Page is scrolled till the bottom since one or more updates are not displayed on page load");
			if(!statusFrom.equalsIgnoreCase("Top Updates Tab")){
				Assert.assertTrue(omUI.fluentWaitTextPresent(message),"Newly posted status with message '" + message + "' is not displayed on the screen.");
				log.info("INFO: Newly posted status with message '" + message + "' is displayed on the screen.");
			}
			Assert.assertEquals(omUI.getElementText(OrientMeUIConstants.communityUpdate.replace("##innertext##", resourceStrings.get("community"))),
					testUserB.getDisplayName() + " added you to the " + resourceStrings.get("community") + " community",
					"Status " + testUserB.getDisplayName() + " added you to the " + resourceStrings.get("community") + " community is displayed on the page");
			log.info("INFO: Status " + testUserB.getDisplayName() + " added you to the " + resourceStrings.get("community") + " community is displayed on the page.");
			Assert.assertTrue(omUI.fluentWaitTextPresent(resourceStrings.get("followCommunity")),"Newly created community with message '" + resourceStrings.get("followCommunity") + "' is displayed on the screen.");
			log.info("INFO: Newly created community with message '" + resourceStrings.get("followCommunity") + "' is displayed on the screen.");
		}
	}
	
	/**
	 *<ul>
	 *<li><B>Data population for Status Entry test</B></li>
	 *<li><B>UserA posts a status</B></li>
	 *<li>As UserB, create a community and add UserA as a member</li>
	 *<li>UserA creates a community and UserB follows it.</li>
	 *</ul>
	 *@return map of resource created
	 * @throws Exception 
	 */
	private Map<String, String> filterTestDataPop(String message) throws Exception {
		Map<String, String> resources = new HashMap<String, String>();
		String randomString1 = Helper.genStrongRand();
		String randomString2 = Helper.genStrongRand();
		
		log.info("(API) Add a status update with text " + message);		
		String statusUpdateId=apiProfilesTestUserA.postStatusUpdate(message);
		resources.put("status-update-id", statusUpdateId);
		
		log.info("(API) Create a community as " + getClass().getSimpleName() + randomString1 + " with " + testUserB.getDisplayName() + " with member " + testUserA.getDisplayName());
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(
				getClass().getSimpleName() + randomString1, Access.RESTRICTED);
		community = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, 
				testUserB, apiCommTestUserB, testUserA);
		resources.put("community", community.getTitle());
		
		log.info("(API) Create a community as " + getClass().getSimpleName() + randomString2 + " with " + testUserA.getDisplayName() + " with " + testUserB.getDisplayName() + " follows that community");
		BaseCommunity baseCommunityFollow = CommunityBaseBuilder.buildBaseCommunity(
				getClass().getSimpleName() + randomString2, Access.PUBLIC);
		community = CommunityEvents.createNewCommunityWithOneFollower(baseCommunityFollow, 
				testUserB, apiCommTestUserB, testUserA, apiCommTestUserA);
		resources.put("followCommunity", community.getTitle());
		adminService.indexNow("status_updates", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		
		return resources;
	}
	/**
	 *<ul>
	 *<li><B>Data population for Status Entry test</B></li>
	 *<li><B>UserA posts a status with image attached</B></li>
	 *</ul>
	 *@return map of resource created
	 */
	private Map<String, String> postStatusWithImage(String message, FileEntry fileEntry) {
		Map<String, String> resources = new HashMap<String, String>();
		
		log.info("(API) Add a status update with text " + message + " and image");		
		String statusUpdateId=apiProfilesTestUserA.postStatusUpdateWithFileAttachment(message, fileEntry);
		resources.put("status-update-id", statusUpdateId);
		try {
			adminService.indexNow("status_updates", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return resources;
	}
}