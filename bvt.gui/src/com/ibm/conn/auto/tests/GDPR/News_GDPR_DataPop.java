package com.ibm.conn.auto.tests.GDPR;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class News_GDPR_DataPop extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(News_GDPR_DataPop.class);
	private TestConfigCustom cfg; ICBaseUI ui;
	private HomepageUI hpUI;
	private String serverURL;
	private User testUser1, testUser2;
	private APIProfilesHandler profilesAPI1, profilesAPI2;
	private String firstCommentLikeLink;
	

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		hpUI = HomepageUI.getGui(cfg.getProductName(),driver);

		//Load Users		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
						
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		URLConstants.setServerURL(serverURL);
					
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		profilesAPI1 = new APIProfilesHandler(serverURL, testUser1.getEmail(), testUser1.getPassword());
		profilesAPI2 = new APIProfilesHandler(serverURL, testUser2.getEmail(), testUser2.getPassword());
	    
		firstCommentLikeLink = "css=a[id='TOGGLE_com_ibm_oneui_controls_Like_0']:contains(Like)";
		
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: News - Post Status Updates Entry</li>
	*<li><B>Step:</B> UserA posts a status updates entry via API</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void postStatusUpdateEntry() {
		
		String testName = hpUI.startTest();
		
		String statusMessage = testName + Helper.genDateBasedRandVal(); 

		log.info("INFO: " + testUser1.getDisplayName() + " posts a status update using API method");
		profilesAPI1.postStatusUpdate(statusMessage);
		
		hpUI.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: News - Post Status Updates Entry & Add Comment</li>
	*<li><B>Step:</B> UserA posts a status updates entry via API</li>
	*<li><B>Step:</B> UserA adds a comment to the status updates entry via EE</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void commentOnStatusUpdateEntry() {
		
		String testName = hpUI.startTest();
		
		String statusMessage = testName + Helper.genDateBasedRandVal(); 
		
		String statusComment = "My Comment: " + testName + Helper.genDateBasedRandVal();

		log.info("INFO: Log into Homepage as UserA " + testUser1.getDisplayName());
		hpUI.loadComponent(Data.getData().ComponentHomepage);
		hpUI.login(testUser1);
		
		log.info("INFO: " + testUser1.getDisplayName() + " posts a status update using API method");
		profilesAPI1.postStatusUpdate(statusMessage);

		log.info("INFO: Navigate to I'm Following");
		hpUI.gotoImFollowing();

		log.info("INFO: " + testUser1.getDisplayName() + " Locate the status message and open it in EE");
		hpUI.filterNewsItemOpenEE(statusMessage);

		log.info("INFO: Post a comment in EE");
		hpUI.addEEComment(statusComment);		
				
		log.info("INFO: Logout UserA");
		hpUI.logout();
		hpUI.close(cfg);

		hpUI.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: News - Add Comment To Another User's Status Update Entry</li>
	*<li><B>Step:</B> UserB posts a status updates entry via API</li>
	*<li><B>Step:</B> UserA adds a comment to the status updates entry via EE</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userACommentsOnStatusUpdatePostedByUserB() {
		
		String testName = hpUI.startTest();
		
		String statusMessage = testName + Helper.genDateBasedRandVal(); 
		
		String statusComment = "My Comment: " + testName + Helper.genDateBasedRandVal();

		log.info("INFO: " + testUser2.getDisplayName() + " posts a status update using API method");
		profilesAPI2.postStatusUpdate(statusMessage);
		
		log.info("INFO: Log into Homepage as UserA " + testUser1.getDisplayName());
		hpUI.loadComponent(Data.getData().ComponentHomepage);
		hpUI.login(testUser1);
			
		log.info("INFO: Navigate to Discover tab");
		hpUI.gotoDiscover();

		log.info("INFO: " + testUser1.getDisplayName() + " Locate the status message and open it in EE");
		hpUI.filterNewsItemOpenEE(statusMessage);

		log.info("INFO: Post a comment in EE");
		hpUI.addEEComment(statusComment);		
		
		log.info("INFO: Logout UserA");
		hpUI.logout();
		hpUI.close(cfg);

		hpUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: News - UserA Reposts Status Update Entry Posted By UserB</li>
	*<li><B>Step:</B> UserB posts a status updates entry via API</li>
	*<li><B>Step:</B> UserA reposts the status updates entry</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userARepostsEntryPostedByUserB() {
		
		String testName = hpUI.startTest();
		
		String statusMessage = testName + Helper.genDateBasedRandVal(); 
	
		log.info("INFO: " + testUser2.getDisplayName() + " posts a status update using API method");
		profilesAPI2.postStatusUpdate(statusMessage);
		
		log.info("INFO: Log into Homepage as UserA " + testUser1.getDisplayName());
		hpUI.loadComponent(Data.getData().ComponentHomepage);
		hpUI.login(testUser1);
		
		log.info("INFO: Click on the Discover tab");
		hpUI.clickLinkWait(HomepageUIConstants.DiscoverTab);
			
		log.info("INFO: UserA reposts UserB's status update entry");
		hpUI.moveToClick(HomepageUI.getStatusUpdateMesage(statusMessage), HomepageUIConstants.RepostAction);

		log.info("INFO: Verify that the status update was successfully reposted");
		hpUI.fluentWaitTextPresent(Data.getData().RepostedUpdateMessage);
				
		log.info("INFO: Logout UserA");
		hpUI.logout();
		hpUI.close(cfg);

		hpUI.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: News - Like Status Updates Entry</li>
	*<li><B>Step:</B> UserA posts a status updates entry via API</li>
	*<li><B>Step:</B> UserA 'Likes' the status updates entry</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void likeStatusUpdateEntry() {
		
		String testName = hpUI.startTest();
		
		String statusMessage = testName + Helper.genDateBasedRandVal(); 

		log.info("INFO: Log into Homepage as UserA " + testUser1.getDisplayName());
		hpUI.loadComponent(Data.getData().ComponentHomepage);
		hpUI.login(testUser1);

		log.info("INFO: " + testUser1.getDisplayName() + " posts a status update using API method");
		profilesAPI1.postStatusUpdate(statusMessage);
		
		log.info("INFO: Refresh page so the status updates entry appears");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Like the status update entry");
		hpUI.getFirstVisibleElement(FilesUIConstants.PopupLikeFile).click();
		
		log.info("INFO: Logout UserA");
		hpUI.logout();
		hpUI.close(cfg);

		hpUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: News - UserA Likes Status Updates Entry Posted By UserB</li>
	*<li><B>Step:</B> UserB posts a status updates entry via API</li>
	*<li><B>Step:</B> UserA 'Likes' the status updates entry</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userALikesStatusUpdatePostedByUserB() {
		
		String testName = hpUI.startTest();
		
		String statusMessage = testName + Helper.genDateBasedRandVal(); 
	
		log.info("INFO: " + testUser2.getDisplayName() + " posts a status update using API method");
		profilesAPI2.postStatusUpdate(statusMessage);
		
		log.info("INFO: Log into Homepage as UserA " + testUser1.getDisplayName());
		hpUI.loadComponent(Data.getData().ComponentHomepage);
		hpUI.login(testUser1);
		
		log.info("INFO: Click on the Discover tab");
		hpUI.clickLinkWait(HomepageUIConstants.DiscoverTab);
		
		log.info("INFO: Refresh page to make sure the status updates entry appears");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Like the status update entry");
		hpUI.getFirstVisibleElement(FilesUIConstants.PopupLikeFile).click();
		
		log.info("INFO: Logout UserA");
		hpUI.logout();
		hpUI.close(cfg);

		hpUI.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: News - Like Status Updates Comment</li>
	*<li><B>Step:</B> UserA posts a status updates entry via API</li>
	*<li><B>Step:</B> UserA posts a comment to the entry via EE</li>
	*<li><B>Step:</B> UserA 'Likes' the comment</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void likeStatusUpdateComment() {
		
		String testName = hpUI.startTest();
		
		String statusMessage = testName + Helper.genDateBasedRandVal(); 
		
		String statusComment = "My Comment: " + testName + Helper.genDateBasedRandVal();

		log.info("INFO: " + testUser1.getDisplayName() + " posts a status update using API method");
		profilesAPI1.postStatusUpdate(statusMessage);
		
		log.info("INFO: Log into Homepage as UserA " + testUser1.getDisplayName());
		hpUI.loadComponent(Data.getData().ComponentHomepage);
		hpUI.login(testUser1);
		
		log.info("INFO: Navigate to I'm Following");
		hpUI.gotoImFollowing();

		log.info("INFO: " + testUser1.getDisplayName() + " Locate the status message and open it in EE");
		hpUI.filterNewsItemOpenEE(statusMessage);

		log.info("INFO: Post a comment in EE");
		hpUI.addEEComment(statusComment);
		
		log.info("INFO: Refresh the browser");	
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Like the status comment");
		hpUI.clickLinkWithJavascript(firstCommentLikeLink);
		
		log.info("INFO: Logout UserA");
		hpUI.logout();
		hpUI.close(cfg);

		hpUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: News - UserA Likes Status Updates Comment Posted By UserB</li>
	*<li><B>Step:</B> UserB posts a status updates entry via API</li>
	*<li><B>Step:</B> UserB posts a comment to the entry</li>
	*<li><B>Step:</B> UserA 'Likes' the status update comment</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userALikesACommentPostedByUserB() {
		
		String testName = hpUI.startTest();
		
		String statusMessage = testName + Helper.genDateBasedRandVal(); 
		
		String statusComment = "My Comment: " + testName + Helper.genDateBasedRandVal();

		log.info("INFO: Log into Homepage as UserB " + testUser2.getDisplayName());
		hpUI.loadComponent(Data.getData().ComponentHomepage);
		hpUI.login(testUser2);

		log.info("INFO: " + testUser2.getDisplayName() + " posts a status update using API method");
		profilesAPI2.postStatusUpdate(statusMessage);
		
		log.info("INFO: Navigate to I'm Following");
		hpUI.gotoImFollowing();

		log.info("INFO: " + testUser1.getDisplayName() + " Locate the status message and open it in EE");
		hpUI.filterNewsItemOpenEE(statusMessage);

		log.info("INFO: Post a comment in EE");
		hpUI.addEEComment(statusComment);			
		
		log.info("INFO: Logout as UserB " + testUser2.getDisplayName());
		hpUI.logout();
		hpUI.close(cfg);
		
		log.info("INFO: Log into Homepage as UserA " + testUser1.getDisplayName());
		hpUI.loadComponent(Data.getData().ComponentHomepage);
		hpUI.login(testUser1);
		
		log.info("INFO: Navigate to Discover tab");
		hpUI.gotoDiscover();
		
		log.info("INFO: Refresh the browser");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Like the status comment");
		hpUI.clickLinkWithJavascript(firstCommentLikeLink);
		
		log.info("INFO: Logout UserA");
		hpUI.logout();
		hpUI.close(cfg);

		hpUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: News - Status Update that Mentions UserA</li>
	*<li><B>Step:</B> UserB posts a status updates entry that mentions UserA</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void statusUpdateThatMentionsUserA() {
		
		String testName = hpUI.startTest();

		String statusMessage = testName + Helper.genDateBasedRandVal(); 

		log.info("INFO: Log into Homepage as UserB " + testUser2.getDisplayName());
		hpUI.loadComponent(Data.getData().ComponentHomepage);
		hpUI.login(testUser2);

		log.info("INFO: Navigate to the Discover tab");
		hpUI.gotoDiscover();			

		log.info("INFO: Create status update entry with an @mention about UserA: " + testUser1.getEmail());
		hpUI.postAtMentionUserUpdate(testUser1, statusMessage);
		
		log.info("INFO: Click on Post");
		hpUI.clickLinkWait(HomepageUIConstants.PostStatusOld);

		hpUI.endTest();		
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: News - Status Update that Mentions UserA</li>
	*<li><B>Step:</B> UserB posts a status updates entry that mentions UserA</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void statusCommentThatMentionsUserA() {
		
		String testName = hpUI.startTest();

		String statusMessage = testName + Helper.genDateBasedRandVal(); 
		
		String statusComment = "My Comment: " + testName + Helper.genDateBasedRandVal();
		
		String mention = Character.toString('@');

		log.info("INFO: Log into Homepage as UserB " + testUser2.getDisplayName());
		hpUI.loadComponent(Data.getData().ComponentHomepage);
		hpUI.login(testUser2);

		log.info("INFO: " + testUser2.getDisplayName() + " posts a status update using API method");
		profilesAPI2.postStatusUpdate(statusMessage);
		
		log.info("INFO: Refresh browser so the status update entry appears");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: " + testUser1.getDisplayName() + " Locate the status message and open it in EE");
		hpUI.filterNewsItemOpenEE(statusMessage);
		
		log.info("INFO: Post a comment in EE");
		log.info("INFO: Switching to comments frame");
		Element commentframe = driver.getSingleElement(HomepageUIConstants.StatusUpdateFrame);
		driver.switchToFrame().selectFrameByElement(commentframe);

		log.info("INFO: Enter some text into the EE comment field");
		hpUI.fluentWaitElementVisible(HomepageUIConstants.StatusUpdateTextField);
		Element inputField = driver.getSingleElement(HomepageUIConstants.StatusUpdateTextField);
		inputField.click();
		inputField.type(statusComment);

		log.info("INFO: Enter the @mention into the EE pop-up");
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(" " + mention + testUser1.getDisplayName());

		log.info("INFO: Select user from the mentions typeahead suggestion list");
		selectTypeaheadUserInEEUsingArrowKeys();	
		hpUI.switchToEEFrame();

		log.info("INFO: Click the Post button");
		hpUI.clickLinkWait(HomepageUIConstants.OpenEEPostCommentButton);

		hpUI.endTest();		
	}
	
	 /*
	  * this method will select a user from typeahead in the EE pop-up dialog
	  */

	 private String selectTypeaheadUserInEEUsingArrowKeys() {

		 log.info("INFO: Now pressing the UP ARROW key a number of times to move up the list of typeahead menu items");
		 for(int index = 0; index < 7; index ++) {
			 driver.switchToActiveElement().type(Keys.ARROW_UP);
		 }

		 hpUI.waitForEETypeaheadMenuToLoad();

		 log.info("INFO: Retrieve all menu items to verify which one is about to be selected");
		 List<Element> menuItemElements = hpUI.getTypeaheadMenuItemsList(false);

		 String selectedMenuItem = null;
		 if(menuItemElements.size() > 5) {
			 log.info("INFO: The fifth user from the typeahead menu is now being selected");
			 selectedMenuItem = menuItemElements.get(menuItemElements.size() - 5).getText();

		 } else if(menuItemElements.size() <= 5 && menuItemElements.size() > 1) {
			 log.info("INFO: The second user from the typeahead menu is now being selected");
			 selectedMenuItem = menuItemElements.get(1).getText();

		 } else {
			 log.info("INFO: The first user from the typeahead menu is now being selected");
			 selectedMenuItem = menuItemElements.get(0).getText();
		 }
		 log.info("INFO: Now pressing the ENTER key to select the highlighted user in the typeahead menu");
		 driver.switchToActiveElement().type(Keys.ENTER);

		 return selectedMenuItem;
	 }
}
