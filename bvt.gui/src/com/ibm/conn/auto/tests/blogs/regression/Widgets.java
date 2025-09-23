package com.ibm.conn.auto.tests.blogs.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.google.common.base.Function;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class Widgets extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(Widgets.class);
	private ProfilesUI pUI;
	private BlogsUI bUI;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;		
	private User testUser1;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private BaseCommunity.Access defaultAccess;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		testUser1 = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

	}

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		pUI = ProfilesUI.getGui(cfg.getProductName(), driver);
		bUI = BlogsUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());

	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Test comments in the embedded blog frame.
	 *<li><B>Step:</B> Log in as user 2.
	 *<li><B>Step:</B> Search for the profile of user 1.
	 *<li><B>Verify:</B> User 1 appeared in the search results.
	 *<li><B>Step:</B> View user 1's profile.
	 *<li><B>Step:</B> Follow user 1.
	 *<li><B>Verify:</B> Text confirming user 1 is currently followed appears.
	 *<li><B>Step:</B> Log out user 2.
	 *<li><B>Step:</B> Log in as user 1.
	 *<li><B>Step:</B> Create a community as user 1 via the Atom API.
	 *<li><B>Step:</B> Add an ideation blog widget to the community via the Atom API.
	 *<li><B>Step:</B> Go to the community's web page.
	 *<li><B>Step:</B> Click on the Ideation Blogs link in the left navigation bar.
	 *<li><B>Step:</B> Create a new idea.
	 *<li><B>Verify:</B> Confirm the idea appears on the blog's page.
	 *<li><B>Step:</B> Log out user 1.
	 *<li><B>Step:</B> Log in as user 2.
	 *<li><B>Step:</B> Go to "I'm following" in User 2's homepage.
	 *<li><B>Verify:</B> Confirm the idea that user 1 created appears on user 2's "I'm Following" page.
	 *<li><B>Step:</B> Click on the idea to open the embedded blog frame.
	 *<li><B>Verify:</B> Confirm the embedded blog frame appears.
	 *<li><B>Verify:</B> The comments tab is visible in the embedded blog frame.
	 *<li><B>Step:</B> Add a comment.
	 *<li><B>Verify:</B> Check comment is saved successfully.
	 *<li><B>Verify:</B> Comments number increases by 1 in comment tab title.
	 *<li><B>Step:</B> Add 7 comments.
	 *<li><B>Verify:</B> Comments number increases by 1 in comment tab title for each comment.
	 *<li><B>Verify:</B> something from User 2 regarding the ideation blog entry appears in Discover.
	 *<li><B>Verify:</B> all 7 comments are listed.
	 *<li><B>Step:</B> Log out user 2.
	 *<li><B>Step:</B> Log in as user 1.
	 *<li><B>Step:</B> Open the list of owned blogs.
	 *<li><B>Step:</B> Click on the ideation blog.
	 *<li><B>Step:</B> Edit the blog settings to disable comments.
	 *<li><B>Verify:</B> blog settings were saved.
	 *<li><B>Step:</B> Log out user 1.
	 *<li><B>Step:</B> Log in as user 2.
	 *<li><B>Step:</B> Go to "Discover" in User 2's homepage.
	 *<li><B>Step:</B> Click on the idea to open the embedded blog frame for the idea.
	 *<li><B>Verify:</B> there is no add a comment text field in comments tab in ee.
	 *</ul>
	 */
	//this scenario is Failing while clicking the cordinates
	@Test (groups = { "regression" },enabled=false)
	public void EEIdeationBlogComments() throws Exception {
		blogCommentsTest(true);
	}
	
	/**
	 * Same test as above, except with a regular blog instead of an ideation blog.
	 */
	//this scenario is Failing while clicking the cordinates
	@Test (groups = { "regression" }, enabled = false)
	public void EEBlogComments() throws Exception {
		blogCommentsTest(false);
	}
	
	private void blogCommentsTest(boolean isIdeation) throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser2 = cfg.getUserAllocator().getUser();
		String testName = pUI.startTest();

		//user2 follows user 1
		//user1 creates public ideation blog and creates idea in it
		Pair<BaseCommunity, BaseBlogPost> communityAndIdea = blogsEECreateData(testUser2, testName, isIdeation);
		String communityName = communityAndIdea.first.getName();
		String ideaName = communityAndIdea.second.getTitle();

		//verify Ideation blog idea creation event show in AS
		//Load component and login
		logger.strongStep("Open the Homepage and login: " +testUser2.getDisplayName());
		pUI.loadComponent(Data.getData().ComponentHomepage, true);
		bUI.login(testUser2);

		// Go to "I'm following" in the user's homepage		
		logger.strongStep("Click on the Updates link in the left navigation menu");
		bUI.clickLinkWait(HomepageUIConstants.Updates);
		
		logger.strongStep("Switch to I'm Follwing tab");
		bUI.clickLinkWait(HomepageUIConstants.ImFollowingTab);
		
		logger.strongStep("Close the Guided Tour Popup if it appears");
		cUI.closeGuidedTourPopup();

		// Verify the idea that user 1 created appears on user 2's "I'm Following" page 
		logger.strongStep("Verify the entry that " + testUser1.getDisplayName() +
				" created appears in the I'm Following page of " + testUser2.getDisplayName());
		log.info("INFO: Verifying that the entry that " + testUser1.getDisplayName() +
				" created appears in the I'm Following page of " + testUser2.getDisplayName());
		Element ideaDiv = getBlogEntryEECreationEvent(ideaName, testUser1.getDisplayName(), communityName);
		Assert.assertFalse(ideaDiv == null, "The entry " + ideaName + " that " + testUser1.getDisplayName() +
				" created did not appear.");

		// Click on the idea to open the embedded frame
		logger.strongStep("Open the embedded blog frame for the idea");
		log.info("INFO: Clicking to open the embedded blog frame for the idea");
		openEEFromEntry(ideaDiv, bUI);

		// Switch to the embedded frame
		logger.strongStep("Navigate to the embedded blog frame");
		log.info("INFO: Switching to embedded blog frame");
		Element eeFrameElement = waitForAndSwitchToEEFrame(BlogsUIConstants.BlogsEEFrameIdentifier, "link=" + ideaName);

		//verify comments tab is visible
		logger.strongStep("Confirm that the Comments tab appears inside the embedded blog frame");
		log.info("INFO: Verifying Comments tab is visible inside embedded blog frame");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsEECommentsTab).isVisible(),
				"The comments tab is not visible.");

		//add a comment
		logger.strongStep("Switch to the Comments tab");
		bUI.clickLinkWait(BlogsUIConstants.BlogsEECommentsTab);
		String commentText1 = Data.getData().commonComment + Helper.genDateBasedRand() + "1";
		
		logger.strongStep("Input the first comment in the Embedded Experience text field");
		log.info("INFO: Entering comment 1 into Embedded Experience text field");
		typeMessageInCommentField(commentText1, eeFrameElement, bUI);
		
		logger.strongStep("Click on the Post link");
		log.info("INFO: Clicking link to post comment");
		bUI.clickLink(BlogsUIConstants.BlogsEEPostComment);

		//verify comment is saved successfully
		bUI.fluentWaitPresent(BlogsUIConstants.BlogsEECommentContent);
		String actualCommentText = driver.getSingleElement(BlogsUIConstants.BlogsEECommentContent).getText();
		String expectedCommentText = commentText1;
		logger.strongStep("Verify that the posted comment is visible");
		log.info("INFO: Verifying the comment that was posted appears");
		Assert.assertEquals(expectedCommentText, actualCommentText,
				"The comment did not appear as entered. The expected comment was: " + expectedCommentText +
				" ,the actual comment was: " + actualCommentText);

		//comments number increases by 1 in comment tab title
		String actualCommentsNumber = driver.getSingleElement(BlogsUIConstants.BlogsEECommentsTab).getText();
		String expectedCommentsNumber = "Comments (1)";
		logger.strongStep("Verify that the Comments tab shows the correct count of 1 comment");
		log.info("INFO: Verifying the correct count is shown for the comments");
		Assert.assertTrue(expectedCommentsNumber.equalsIgnoreCase(actualCommentsNumber),
				"The blogs embedded experience frame did not show the expected number of comments. " +
						"Expected: Comments (1), actual text was: " + actualCommentsNumber);

		//add 7 comments
		ArrayList<String> commentTexts = new ArrayList<String>(7);
		for(int i = 2; i < 9; i++) {
			String commentText = Data.getData().commonComment + Helper.genDateBasedRand() + i;
			commentTexts.add(commentText);
			logger.strongStep("Entering comment " + i + " into Embedded Experience text field");
			log.info("INFO: Entering comment " + i + " into Embedded Experience text field");
			typeMessageInCommentField(commentText, eeFrameElement, bUI);
			driver.typeNative(Keys.TAB);
			logger.strongStep("Click on the Post link");
			log.info("INFO: Clicking link to post comment");
			bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEEPostComment);

			//Verify the comment count is updated
			logger.strongStep("Verify that the Comments tab shows the correct count of " + i + " comment");
			log.info("INFO: Verifying the correct count is shown for the comments");
			bUI.fluentWaitTextPresent("COMMENTS (" + i + ")");
		}

		// verify something from User 2 regarding the ideation blog entry appears in Discover
		logger.strongStep("Get out of the frame and return to the webpage");
		bUI.switchToTopFrame();
		
		logger.strongStep("Click on the 'X' button to close the preview dialog");
		bUI.clickLinkWait(BlogsUIConstants.BlogsEECloseFrame);
		
		logger.strongStep("Switch to Discover tab");
		bUI.clickLinkWait(HomepageUIConstants.DiscoverTab);
		
		logger.strongStep("Verify that the idea the that " + testUser2.getDisplayName() + " commented on appears");
		log.info("INFO: Validate that the idea the that " + testUser2.getDisplayName() + " commented on is displayed");
		ideaDiv = getBlogEntryEECreationEvent(ideaName, "You commented");
		Assert.assertFalse(ideaDiv == null, "The idea that " + testUser2.getDisplayName() +
				" commented on did not appear.");
		
		logger.strongStep("Open the embedded blog frame for the idea");
		log.info("INFO: Clicking to open the embedded blog frame for the idea");
		openEEFromEntry(ideaDiv, bUI);

		// Switch to the embedded frame
		logger.strongStep("Navigate to the embedded blog frame and verify the Comments tab shows up");
		log.info("INFO: Switching to embedded blog frame and validate that the Comments tab displays");
		Assert.assertNotNull(waitForAndSwitchToEEFrame(BlogsUIConstants.BlogsEEFrameIdentifier, "link=" + ideaName),
				"Could not successfully switch to the embedded frame!");
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEECommentsTab);
		
		logger.strongStep("Switch to the Comments tab");
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEECommentsTab);
		
		logger.strongStep("Verify that Show Previous Comments link is visible");
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEEShowPrevComments);
		
		logger.strongStep("Click on Show Previous Comments link");
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEEShowPrevComments);
		
		//verify all 7 comments are listed
		for(String comment : commentTexts) {
			logger.strongStep("Verify that all previously made comments are visible");
			Assert.assertTrue(driver.isTextPresent(comment),
					"The comment " + comment + " did not appear in the comments.");
		}

		// Closing the embedded frame
		logger.strongStep("Get out of the frame and return to the webpage");
		log.info("INFO: Closing the embedded frame");
		bUI.switchToTopFrame();
		driver.clickAt(0, 0);
		
		// Go to Blogs
		String blogsURL = cfg.getServerURL() + Data.getData().ComponentBlogs;
		blogsURL = blogsURL.replace("/login", "");
		logger.strongStep("Open the URL: " + blogsURL);
		log.info("INFO: Navigating to URL: " + blogsURL);
		driver.navigate().to(blogsURL);
		
		logger.strongStep("Verify that Public Blogs tab shows up");
		bUI.fluentWaitPresent(BlogsUIConstants.PublicBlogs);

		// log out
		logger.strongStep("Log out of the session");
		bUI.logout();

		// log in
		logger.strongStep("Click on the Log In link and then login with the credentials of " + testUser1);
		bUI.clickLinkWait(BaseUIConstants.Login_Link);
		bUI.login(testUser1);

		//Open the list of owned blogs, try to find the ideation blog
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab and search for " + communityName);
		log.info("INFO: Searching for " + communityName + " in My Blogs");
		bUI.clickLinkWait(BlogsUIConstants.MyBlogs);
		String blogUUID = getBlogUUID(communityName);
		log.info("INFO: Found " + communityName + " in My Blogs with the UUID " + blogUUID);

		//Click on settings for the ideation blog
		logger.strongStep("Click on Settings link");
		log.info("INFO: Select Blog Settings");
		bUI.clickLinkWait("css=ul.lotusInlinelist a[href*='" + blogUUID + "']:contains(Settings)");

		log.info("INFO: Editing blog settings to disable comments");
		Element commentCheckbox = bUI.getFirstVisibleElement(BlogsUIConstants.blogsSettingsAllowComments);
		if (commentCheckbox.isSelected()) {
			logger.strongStep("Select the 'Allow comments for your blog:' check box");
			log.info("INFO: Click on the 'Allow comments for your blog:' check box");
			commentCheckbox.click();
		}

		if (isIdeation) {
			logger.strongStep("Select 'Update Ideation Blog Settings' button to save the changes to the blog settings");
			log.info("INFO: Clicking 'Update Ideation Blog Settings' button to save the changes to the blog settings");
			bUI.fluentWaitElementVisible(BlogsUIConstants.blogsIdeationUpdateSettings);
			bUI.clickLinkWait(BlogsUIConstants.blogsIdeationUpdateSettings);
		} else {
			logger.strongStep("Select 'Update Blog Settings' button to save the changes to the blog settings");
			log.info("INFO: Clicking 'Update Blog Settings' button to save the changes to the blog settings");
			bUI.fluentWaitElementVisible(BlogsUIConstants.blogsUpdateSettings);
			bUI.clickLinkWait(BlogsUIConstants.blogsUpdateSettings);
		}

		logger.strongStep("Verify that the blog settings were saved");
		log.info("INFO: Verifying blog settings were saved");
		if (isIdeation)
			bUI.fluentWaitTextPresent("Saved changes to Ideation Blog settings");
		else
			bUI.fluentWaitTextPresent("Saved changes to Blog settings");

		logger.strongStep("Log out of the session");
		bUI.logout();

		//login with user2
		logger.strongStep("Open the Homepage and login: " +testUser2.getDisplayName());
		bUI.loadComponent(Data.getData().ComponentHomepage, true);
		bUI.login(testUser2);

		// go to home > all updates
		logger.strongStep("Click on the Updates link in the left navigation menu");
		bUI.clickLinkWait(HomepageUIConstants.Updates);
		
		logger.strongStep("Switch to Discover tab");
		bUI.clickLinkWait(HomepageUIConstants.DiscoverTab);

		//go to this ideation blog ee
		logger.strongStep("Verify that the idea that " + testUser2.getDisplayName() + " commented on appears");
		log.info("INFO: Validate that the idea the that " + testUser2.getDisplayName() + " commented on is displayed");
		ideaDiv = getBlogEntryEECreationEvent(ideaName, "You commented", communityName);
		Assert.assertFalse(ideaDiv == null, "The idea that " + testUser2.getDisplayName() +
				" commented on did not appear.");
		
		logger.strongStep("Open the embedded blog frame for the idea");
		log.info("INFO: Clicking to open the embedded blog frame for the idea");
		openEEFromEntry(ideaDiv, bUI);
		waitForAndSwitchToEEFrame(BlogsUIConstants.BlogsEEFrameIdentifier, "link=" + ideaName);

		//verify there is no add a comment text field in comments tab in ee
		logger.strongStep("Verify that the Hidden Comment Field is visible");
		log.info("INFO: Validate that the Hidden Comment Field is visible");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEEHiddenCommentField));
		pUI.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Test voting in the embedded blog frame.
	 *<li><B>Step:</B> Log in as user 2.
	 *<li><B>Step:</B> Search for the profile of user 1.
	 *<li><B>Verify:</B> User 1 appeared in the search results.
	 *<li><B>Step:</B> View user 1's profile.
	 *<li><B>Step:</B> Follow user 1.
	 *<li><B>Verify:</B> Text confirming user 1 is currently followed appears.
	 *<li><B>Step:</B> Log out user 2.
	 *<li><B>Step:</B> Log in as user 1.
	 *<li><B>Step:</B> Create a community as user 1 via the Atom API.
	 *<li><B>Step:</B> Add an ideation blog widget to the community via the Atom API.
	 *<li><B>Step:</B> Go to the community's web page.
	 *<li><B>Step:</B> Click on the Ideation Blogs link in the left navigation bar.
	 *<li><B>Step:</B> Create a new idea.
	 *<li><B>Verify:</B> Confirm the idea appears on the blog's page.
	 *<li><B>Step:</B> Log out user 1.
	 *<li><B>Step:</B> Log in as user 2.
	 *<li><B>Step:</B> Go to "I'm following" in User 2's homepage.
	 *<li><B>Verify:</B> Confirm the idea that user 1 created appears on user 2's "I'm Following" page.
	 *<li><B>Step:</B> Click on the idea to open the embedded blog frame.
	 *<li><B>Verify:</B> Confirm the embedded blog frame appears.
	 *<li><B>Verify:</B> link to vote is visible.
	 *<li><B>Step:</B> click Vote link.
	 *<li><B>Verify:</B> Vote is successful and Undo link shows.
	 *<li><B>Step:</B> Click undo link.
	 *<li><B>Verify:</B> Verify the voting changes to original status.
	 *<li><B>Step:</B> Log out user 2.
	 *<li><B>Step:</B> Log in as user 1.
	 *<li><B>Step:</B> Graduate the idea.
	 *<li><B>Step:</B> Log out user 1.
	 *<li><B>Step:</B> Log in as user 2.
	 *<li><B>Step:</B> Go to "Discover" in User 2's homepage.
	 *<li><B>Verify:</B> There is a 'Graduated' string in the Discover page.
	 *</ul>
	 *
	 * @throws Exception
	 */
	@Test (groups = { "regression" })
	public void EEIdeationBlogVoteUndoGraduate() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser2 = cfg.getUserAllocator().getUser();
		String testName = bUI.startTest();

		//user2 follows user 1
		//user1 creates public ideation blog and creates idea in it
		Pair<BaseCommunity, BaseBlogPost> communityAndIdea = blogsEECreateData(testUser2, testName, true);
		String communityName = communityAndIdea.first.getName();
		String ideaName = communityAndIdea.second.getTitle();

		//verify Ideation blog idea creation event show in AS
		//Load component and login
		logger.strongStep("Open the Homepage and login: " +testUser2.getDisplayName());
		pUI.loadComponent(Data.getData().ComponentHomepage, true);
		bUI.login(testUser2);

		// Go to "I'm following" in the user's homepage		
		logger.strongStep("Click on the Updates link in the left navigation menu");
		bUI.clickLinkWait(HomepageUIConstants.Updates);
		
		logger.strongStep("Switch to I'm Follwing tab");
		bUI.clickLinkWait(HomepageUIConstants.ImFollowingTab);

		//Verify Ideation blog and Ideation blog entry events show
		logger.strongStep("Validate that the idea that " + testUser1.getDisplayName() +
				" created appears in the I'm Following page of " + testUser2.getDisplayName());
		log.info("INFO: Verifying that the idea that " + testUser1.getDisplayName() +
				" created appears in the I'm Following page of " + testUser2.getDisplayName());
		Element ideaDiv = getBlogEntryEECreationEvent(ideaName, testUser1.getDisplayName(), communityName);
		Assert.assertFalse(ideaDiv == null, "The idea " + ideaName + " that " + testUser1.getDisplayName() +
				" created did not appear.");

		Element ideaLink = bUI.getFirstVisibleElement("link=" + ideaName);
		String ideaUrl = ideaLink.getAttribute("href");

		// Switch to the embedded frame
		logger.strongStep("Open the embedded blog frame for the idea");
		log.info("INFO: Clicking to open the embedded blog frame for the idea");
		openEEFromEntry(ideaDiv, bUI);

		// Switch to the embedded frame
		logger.strongStep("Navigate to the embedded blog frame");
		log.info("INFO: Switching to embedded blog frame");
		waitForAndSwitchToEEFrame(BlogsUIConstants.BlogsEEFrameIdentifier, "link=" + ideaName);

		//verify link to vote is visible
		logger.strongStep("Verify that the Vote link appears inside the embedded blog frame");
		log.info("INFO: Verifying Vote link is visible inside embedded blog frame");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsEEVoteIdeation).isVisible(),
				"The link to vote is not visible.");

		//click Vote link
		logger.strongStep("Click on the Vote link");
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEEVoteIdeation);
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEEVoteIdeation);

		//Verify successful vote and Undo link shows
		logger.strongStep("Validate that the idea now shows 1 vote");
		log.info("INFO: Verifying the idea has 1 vote after voting");
		bUI.fluentWaitPresent(BlogsUIConstants.BlogsEEPopupLikeNumber + ":contains(1)");
//		String likeCount = bUI.getElementText(BlogsUI.BlogsEEPopupLikeNumber);
//		Assert.assertTrue(likeCount.trim().equals("1"),
//				"The idea does not show as having 1 vote after voting: " +
//						"expected 1, actual " + likeCount);

		//Verify Undo link exists by clicking it
		logger.strongStep("Click on the Undo link");
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEEUndo);
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEEUndo);

		//Verify it changes to original status
		logger.strongStep("Verify that the Vote link reappears");
		log.info("INFO: Validate that the Vote link is visible again");
		Assert.assertTrue(bUI.isElementPresent(BlogsUIConstants.BlogsEEVoteIdeation),
				"The link to vote does not appear after undoind a vote.");
		logger.strongStep("Validate that the idea has 0 votes after undoing a vote");
		log.info("INFO: Verify the idea has 0 votes after undoing a vote");
		String likeCount = bUI.getElementText(BlogsUIConstants.BlogsEEPopupLikeNumber);
		Assert.assertFalse(likeCount.trim().equals("1"),
				"The idea still shows as having 1 vote after undoing a vote: " +
						"expected 0, actual " + likeCount);

		//Logout with user 2
		logger.strongStep("Log out of the session and verify the Log In button is visible");
		driver.switchToFrame().returnToTopFrame();
		bUI.logout();
		bUI.fluentWaitElementVisible(BaseUIConstants.Login_Button);

		//login with user1
		logger.strongStep("Login using the credentials of: " +testUser1.getDisplayName());
		bUI.login(testUser1);

		//graduate the idea
		logger.strongStep("Open the URL: " + ideaUrl);
		log.info("INFO: Navigating to URL: " + ideaUrl);
		driver.navigate().to(ideaUrl);
		
		logger.strongStep("Click on the Graduate Idea button");
		bUI.clickLinkWait(BlogsUIConstants.BlogsGraduate);
		
		logger.strongStep("Click on the OK button");
		bUI.clickLinkWait(BlogsUIConstants.BlogsGraduateOK);

		//Logout with user 1
		logger.strongStep("Log out of the session");
		bUI.logout();

		//login with user2
		logger.strongStep("Open the Homepage and login: " +testUser2.getDisplayName());
		bUI.loadComponent(Data.getData().ComponentHomepage, true);
		bUI.login(testUser2);

		//go to Home > All updates
		logger.strongStep("Click on the Updates link in the left navigation menu");
		bUI.clickLinkWait(HomepageUIConstants.Updates);
		
		logger.strongStep("Switch to I'm Follwing tab");
		bUI.clickLinkWait(HomepageUIConstants.ImFollowingTab);

		//verify that there is 'Graduated' string in the EE
		logger.strongStep("Verify that the text confirming the idea was graduated appears");
		Assert.assertTrue(driver.isTextPresent("graduated their own " + ideaName + " idea in the " + communityName + " Ideation Blog."),
				"The text confirming the idea was graduated did not appear.");

		logger.strongStep("Log out of the session");
		bUI.logout();
		bUI.endTest();
	}
	/**
	 *<ul>
	 *<li><B>Info:</B> Test liking in the embedded blog frame.
	 *<li><B>Step:</B> Log in as user 2.
	 *<li><B>Step:</B> Search for the profile of user 1.
	 *<li><B>Verify:</B> User 1 appeared in the search results.
	 *<li><B>Step:</B> View user 1's profile.
	 *<li><B>Step:</B> Follow user 1.
	 *<li><B>Verify:</B> Text confirming user 1 is currently followed appears.
	 *<li><B>Step:</B> Log out user 2.
	 *<li><B>Step:</B> Log in as user 1.
	 *<li><B>Step:</B> Create a community as user 1 via the Atom API.
	 *<li><B>Step:</B> Add a blog widget to the community via the Atom API.
	 *<li><B>Step:</B> Go to the community's web page.
	 *<li><B>Step:</B> Click on the Blogs link in the left navigation bar.
	 *<li><B>Step:</B> Create a new blog entry.
	 *<li><B>Verify:</B> Confirm the entry appears on the blog's page.
	 *<li><B>Step:</B> Log out user 1.
	 *<li><B>Step:</B> Log in as user 2.
	 *<li><B>Step:</B> Go to "I'm following" in User 2's homepage.
	 *<li><B>Verify:</B> Confirm the blog entry that user 1 created appears on user 2's "I'm Following" page.
	 *<li><B>Step:</B> Click on the entry to open the embedded blog frame.
	 *<li><B>Verify:</B> Confirm the embedded blog frame appears.
	 *<li><B>Verify:</B> Like link shows in the embedded blog frame.
	 *<li><B>Step:</B> click Like link.
	 *<li><B>Verify:</B> an unlike link appears.
	 *<li><B>Step:</B> click Unlike link.
	 *<li><B>Verify:</B> The entry does not appear as having likes.
	 *<li><B>Step:</B> click Like link.
	 *<li><B>Step:</B> Log out user 2.
	 *<li><B>Step:</B> Log in as user 3.
	 *<li><B>Step:</B> Go to "Discover" in User 3's homepage.
	 *<li><B>Step:</B> Click on the blog entry to open the embedded blog frame for the entry.
	 *<li><B>Step:</B> click Like link.
	 *<li><B>Verify:</B> an unlike link appears.
	 *<li><B>Verify:</B> the number of people who like the entry is changed to 2.
	 *<li><B>Verify:</B> there is no add a comment text field in comments tab in ee.
	 *<li><B>Step:</B> Click the like light control pop up.
	 *<li><B>Verify:</B> User 2 and User 3 show as having liked the entry.
	 */
	@Test (groups = { "regression" })
	public void widgetsBlogEELikeUnlike() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = bUI.startTest();
		User testUser2 = cfg.getUserAllocator().getUser();
		User testUser3 = cfg.getUserAllocator().getUser();

		//user2 follows user 1
		//user1 creates public blog and creates entry in it
		Pair<BaseCommunity, BaseBlogPost> communityAndIdea = blogsEECreateData(testUser2, testName, false);
		String communityName = communityAndIdea.first.getName();
		String entryName = communityAndIdea.second.getTitle();

		//verify blog idea creation event show in AS
		//Load component and login
		logger.strongStep("Open the Homepage and login: " +testUser2.getDisplayName());
		pUI.loadComponent(Data.getData().ComponentHomepage, true);
		bUI.login(testUser2);

		// Go to "I'm following" in the user's homepage		
		logger.strongStep("Click on the Updates link in the left navigation menu");
		bUI.clickLinkWait(HomepageUIConstants.Updates);
		
		logger.strongStep("Switch to I'm Follwing tab");
		bUI.clickLinkWait(HomepageUIConstants.ImFollowingTab);

		// Verify the blog post that user 1 created appears on user 2's "I'm Following" page 
		logger.strongStep("Verify that the blog post that " + testUser1.getDisplayName() +
				" created appears in the I'm Following page of " + testUser2.getDisplayName());
		log.info("INFO: Verifying that the blog post that " + testUser1.getDisplayName() +
				" created appears in the I'm Following page of " + testUser2.getDisplayName());
		Element ideaDiv = getBlogEntryEECreationEvent(entryName, testUser1.getDisplayName(), communityName);
		Assert.assertFalse(ideaDiv == null, "The blog post " + entryName + " that " + testUser1.getDisplayName() +
				" created did not appear.");

		logger.strongStep("Open the embedded blog frame for the idea");
		log.info("INFO: Clicking to open the embedded blog frame for the idea");
		openEEFromEntry(ideaDiv, bUI);

		// Switch to the embedded frame
		logger.strongStep("Navigate to the embedded blog frame");
		log.info("INFO: Switching to embedded blog frame");
		waitForAndSwitchToEEFrame(BlogsUIConstants.BlogsEEFrameIdentifier, "link=" + entryName);

		//Verify Like link shows
		logger.strongStep("Verify that the Like link appears");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEELike),
				"The link to like a blog entry did not appear.");

		//click Like link
		logger.strongStep("Click on the Like link");
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEELike);
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEELike);

		//Verify presence of and click Undo link
		logger.strongStep("Verify the Unlike link appears");
		Assert.assertTrue(bUI.isElementPresent(BlogsUIConstants.BlogsEEUnlike),
				"The link to unlike a blog entry did not appear after liking it");
		
		logger.strongStep("Click on the Unlike link");
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEEUnlike);
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEEUnlike);

		//Verify it changes to original status
		logger.strongStep("Verify the number of likes is equal to 1");
		bUI.fluentWaitPresent(BlogsUIConstants.BlogsEELike);
		String likeText = bUI.getFirstVisibleElement(BlogsUIConstants.BlogsEELikeIcon).getAttribute("alt");
		Assert.assertFalse(likeText.equals("1 person likes this"),
				"The text '1 person likes this' remains despite unliking.");

		//click Like link
		logger.strongStep("Click on the Like link");
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEELike);
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEELike);
		//Verify display you like this and undo link shows
		
		logger.strongStep("Verify the Unlike link reappears");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEEUnlike),
				"The link to unlike a blog entry did not appear after liking it.");

		//Logout with user 1
		logger.strongStep("Return to the webpage and log out of the session");
		bUI.switchToTopFrame();
		bUI.logout();

		//login with user3, go to Home > Discover
		logger.strongStep("Login with the credentials of: " +testUser3.getDisplayName());
		bUI.login(testUser3);
		logger.strongStep("Click on the Updates link in the left navigation menu");
		bUI.clickLinkWait(HomepageUIConstants.Updates);
		
		logger.strongStep("Switch to Discover tab");
		bUI.clickLink(HomepageUIConstants.Discover);

		//click blog entry event to popup Blog EE, click Like link in it
		// Verify the blog post that user 1 created appears on user 2's "I'm Following" page 
		logger.strongStep("Verify that the blog post that " + testUser1.getDisplayName() +
				" created appears in the Discover page of " + testUser3.getDisplayName());
		log.info("INFO: Verifying that the blog post that " + testUser1.getDisplayName() +
				" created appears in the Discover page of " + testUser3.getDisplayName());
		ideaDiv = getBlogEntryEECreationEvent(entryName, testUser1.getDisplayName(), communityName);
		Assert.assertFalse(ideaDiv == null, "The blog post " + entryName + " that " + testUser1.getDisplayName() +
				" created did not appear.");

		// open the embedded frame
		logger.strongStep("Open the embedded blog frame for the idea");
		log.info("INFO: Clicking to open the embedded blog frame for the idea");
		openEEFromEntry(ideaDiv, bUI);

		// Switch to the embedded frame
		logger.strongStep("Navigate to embedded blog frame");
		log.info("INFO: Switching to embedded blog frame");
		waitForAndSwitchToEEFrame(BlogsUIConstants.BlogsEEFrameIdentifier, "link=" + entryName);

		// click like link and verify unlike link appears
		logger.strongStep("Click on the Like link");
		log.info("INFO: Liking blog entry");
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEELike);
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEELike);
		
		logger.strongStep("Verify the Unlike link appears");
		Assert.assertTrue(bUI.isElementPresent(BlogsUIConstants.BlogsEEUnlike),
				"The link to unlike a blog entry did not appear after liking it");

		//Verify the number of people who like the entry is changed to 2
		logger.strongStep("Verify the number of likes is now equal to 2");
		log.info("INFO: Verifying proper number of likes");
		String actualLikesNum = driver.getSingleElement(BlogsUIConstants.BlogsEEPopupLikeNumber).getText();
		String expectedLikesNum = "2";
		Assert.assertEquals(expectedLikesNum, actualLikesNum,
				"The number of likes is incorrect: expected 2, got " + actualLikesNum);		

		//click the number link
		logger.strongStep("Click on the heart shaped icon to view all people who have liked the blog");
		log.info("INFO: Select the heart shaped icon to view all people who have liked the blog");
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEEPopupLikeNumber);
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEEPopupLikeNumber);

		//Verify the like light control pop up, user1 and user3 show in the light box
		logger.strongStep("Verify the 'People who like this...' popup appears and contains the names of " + testUser2.getDisplayName() + " and " + testUser3.getDisplayName());
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEEPopupNumLikesPopup));
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEEPopupNumLikesPopupUser + ":contains(" + testUser2.getDisplayName() + ")"));
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEEPopupNumLikesPopupUser + ":contains(" + testUser3.getDisplayName() + ")"));

		//click the close icon in the light box to close it
		logger.strongStep("Click on the 'X' button to close the popup");
		bUI.clickLink(BlogsUIConstants.BlogsEEUserPopupClose);

		//End of test
		bUI.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Test liking in the embedded blog frame.
	 *<li><B>Step:</B> Log in as user 2.
	 *<li><B>Step:</B> Search for the profile of user 1.
	 *<li><B>Verify:</B> User 1 appeared in the search results.
	 *<li><B>Step:</B> View user 1's profile.
	 *<li><B>Step:</B> Follow user 1.
	 *<li><B>Verify:</B> Text confirming user 1 is currently followed appears.
	 *<li><B>Step:</B> Log out user 2.
	 *<li><B>Step:</B> Log in as user 1.
	 *<li><B>Step:</B> Create a community as user 1 via the Atom API.
	 *<li><B>Step:</B> Add a blog widget to the community via the Atom API.
	 *<li><B>Step:</B> Go to the community's web page.
	 *<li><B>Step:</B> Click on the Blogs link in the left navigation bar.
	 *<li><B>Step:</B> Create a new blog entry.
	 *<li><B>Verify:</B> Confirm the entry appears on the blog's page.
	 *<li><B>Step:</B> Log out user 1.
	 *<li><B>Step:</B> Log in as user 2.
	 *<li><B>Step:</B> Go to "I'm following" in User 2's homepage.
	 *<li><B>Verify:</B> Confirm user 2's "I'm Following" page contains the event of the blog being created.
	 *<li><B>Step:</B> Click on the blog creation event to open the embedded blog frame.
	 *<li><B>Verify:</B> Confirm the embedded blog frame appears.
	 *<li><B>Verify:</B> Text showing the blog was created is in the embedded blog frame.
	 *<li><B>Step:</B> Close the embedded blog frame.
	 *<li><B>Verify:</B> Confirm the blog entry that user 1 created appears on user 2's "I'm Following" page.
	 *<li><B>Step:</B> Click on the entry to open the embedded blog frame.
	 *<li><B>Verify:</B> Confirm the embedded blog frame appears.
	 *<li><B>Verify:</B> The embedded blog frame contains:.
	 *  <ul>
	 *    <li>The title of the blog entry.
	 *    <li>The blog entry's tag.
	 *    <li>A link to like the entry.
	 *    <li>A read more ... link.
	 *    <li>A comments tab.
	 *    <li>A recent updates tab.
	 *    <li>The content of the blog entry, including a thumbnail of its image.
	 *  </ul>
	 *<li><B>Step:</B> Click Like link.
	 *<li><B>Verify:</B> An unlike link appears.
	 *<li><B>Step:</B> Click the Read More link.
	 *<li><B>Verify:</B> A new window appears.
	 *<li><B>Verify:</B> The window contains the title of the blog entry.
	 *<li><B>Step:</B> Click the image.
	 *<li><B>Verify:</B> A new window appears.
	 *<li><B>Verify:</B> The window contains the title of the blog entry.
	 *<li><B>Step:</B> Add a comment.
	 *<li><B>Verify:</B> Check comment is saved successfully.
	 *<li><B>Verify:</B> Comments number increases by 1 in comment tab title.
	 *<li><B>Step:</B> Close the embedded blog frame.
	 *<li><B>Step:</B> Go to My Network in profiles.
	 *<li><B>Step:</B> View people user 2 is following.
	 *<li><B>Step:</B> View the profile of user 1.
	 *<li><B>Verify:</B> Perform the same verification as on the homepage's recent updates, except
	 *					 do not like the entry and do not open new windows.
	 *<li><B>Step:</B> Go to the list of public communities.
	 *<li><B>Step:</B> Find a link to the community in the list of public communities and click it.
	 *<li><B>Step:</B> Go to the Recent Updates page of the community.
	 *<li><B>Verify:</B> Perform the same verification as on the homepage's recent updates, except
	 *					 do not like the entry and do not open new windows.
	 *
	 */
	 //this scenario is Failing while clicking the cordinates
	@Test (groups = { "regression" },enabled = false)
	public void widgetsBlogEEFunctions() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser2 = cfg.getUserAllocator().getUser();
		String testName = pUI.startTest();

		//user2 follows user 1
		//user1 creates public blog and creates entry in it
		Pair<BaseCommunity, BaseBlogPost> communityAndIdea = blogsEECreateData(testUser2, testName, false);
		String communityName = communityAndIdea.first.getName();
		BaseBlogPost blogEntry = communityAndIdea.second;

		//verify Ideation blog idea creation event show in AS
		//Load component and login
		logger.strongStep("Open the Homepage and login: " +testUser2.getDisplayName());
		pUI.loadComponent(Data.getData().ComponentHomepage, true);
		bUI.login(testUser2);

		// Go to "I'm following" in the user's homepage		
		logger.strongStep("Click on the Updates link in the left navigation menu");
		bUI.clickLinkWait(HomepageUIConstants.Updates);
		
		logger.strongStep("Switch to I'm Following tab");
		bUI.clickLinkWait(HomepageUIConstants.ImFollowingTab);

		//Verify the home page updates contain entries for the blog being created
		//and and entry being posted, and verify their contents
		logger.strongStep("Verify the home page updates contain entries for the blog being created");
		verifyBlogCreationAndEntryInUpdates(1, testUser2, communityName, blogEntry);

		// Go to My Network in profiles
		logger.strongStep("Hover over the Profiles Mega Menu and click on My Network link");
		driver.getFirstElement(ProfilesUIConstants.megaMenuProfiles).hover();
		bUI.clickLinkWithJavascript(ProfilesUIConstants.megaMenuProfilesMyNetwork);

		//View people user 2 is following
		logger.strongStep("Click on the Following link in the left navigation menu");
		bUI.clickLinkWait(ProfilesUIConstants.LeftNavFollowing);

		//View the profile of user 1
		logger.strongStep("Click on the link for " + testUser1.getDisplayName());
		bUI.clickLinkWait("link=" + testUser1.getDisplayName());

		logger.strongStep("Click on the Profile link");
		pUI.clickLinkWait("link=Profile");
		
		//Verify the blog event , blog entry event show in profile's recent updates
		logger.strongStep("Verify the blog event, blog entry event show in profile's recent updates");
		verifyBlogCreationAndEntryInUpdates(2, testUser2, communityName, blogEntry);

		// Go to the list of public communities
		logger.strongStep("Scroll up, hover over the Communities Mega Menu and click on My Communities link");
		driver.executeScript("scroll(0,-250);");
		driver.getFirstElement(CommunitiesUIConstants.communitiesMegaMenu).hover();
		bUI.clickLinkWithJavascript(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);
		
		// Verify the community is in the list of public communities by clicking on it
		logger.strongStep("Click on the community card for " + communityName);
		bUI.clickLinkWait("css=div[aria-label='" + communityName + "']");

		// Go to the recent updates page for the community
		logger.strongStep("Click on the Recent Updates link in the navigation menu");
		log.info("INFO: Select Recent Updates from the navigation menu");
		Community_LeftNav_Menu.RECENT_UPDATES.select(bUI);

		//Verify the blog event, blog entry event show in community's recent updates
		logger.strongStep("Verify the blog event, blog entry event show in community's recent updates");
		verifyBlogCreationAndEntryInUpdates(3, testUser2, communityName, blogEntry);

		//The original test also tested unliking the blog entry and
		//disabling comments, but I felt no need to copy and paste that
		//from EEIdeationBlogComments
		bUI.endTest();
	}

	/**
	 * Log in as user 2. Search for the profile of user 1 and view it. Attempt
	 * to follow user 1's profile and log out. As user 1, attempt to create a 
	 * community and a blog or ideation blog associated with that community. Attempt
	 * to go to the web page for the blog and create an entry in it.
	 * 
	 * @param testUser2 - User 2
	 * @param testName - The name of the test, used for generating strings
	 * @param isIdeationBlog - If false, a normal blog will be created; if true,
	 * an ideation blog will be created. 
	 * @return An Pair of two objects, the first being the BaseCommunity
	 * representing the community, the second being the BaseBlogPost representing
	 * the blog entry or idea.
	 * @throws Exception
	 */	
	private Pair<BaseCommunity, BaseBlogPost> blogsEECreateData(
			User testUser2, String testName, boolean isIdeationBlog) throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String communityName;
		List<String> filesToUpload = new ArrayList<String>();

		if (!isIdeationBlog){
			filesToUpload.add(Data.getData().file1);
		}

		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
		.access(defaultAccess)
		.description("Test Widgets inside community for " + testName)
        .addMember(new Member(CommunityRole.MEMBERS, testUser2))
		.build();

		//Load the component and login
		logger.strongStep("Open the Profiles page and login: " +testUser2.getDisplayName());
		pUI.loadComponent(Data.getData().ComponentProfiles);
		pUI.login(testUser2);

		//Type user 1 in the searchbox
		logger.strongStep("Search for " + testUser1.getDisplayName() + " using the Profiles By Name link");
		pUI.searchForUser(testUser1, true);

		//wait for results page
		logger.strongStep("Verify the text 'Profile search results for Name:' appears");
		pUI.fluentWaitTextPresent(Data.PROFILES_SEARCH_RESULTS);

		//Verify the person appeared in the search results
		logger.strongStep("Verify the link for " + testUser1.getDisplayName() + " appears");
		String inviteeLink = "link=" + testUser1.getDisplayName();
		Assert.assertTrue(pUI.isElementPresent(inviteeLink),
				"The person " + testUser1.getDisplayName() + " was not found when searching.");
		
		logger.strongStep("Click on the link for " + testUser1.getDisplayName());
		pUI.clickLink(inviteeLink);
		
		logger.strongStep("Click on the Profile link");
		pUI.clickLink("link=Profile");

		//Click the invite link
		logger.strongStep("Click on the Follow button");
		pUI.clickLinkWait(ProfilesUIConstants.FollowPerson);

		//Confirm user 1 was successfully followed
		logger.strongStep("Verify the text '" + testUser1.getDisplayName() + " has been added to your following list' appears");
		pUI.fluentWaitTextPresent(testUser1.getDisplayName() +
				" has been added to your following list");
		
		//Log out
		logger.strongStep("Log out of the session and verify the Log In button appears");
		pUI.logout();

		BaseBlogPost blogEntry;
		if (isIdeationBlog) {
			blogEntry = new BaseBlogPost.Builder("Entry " + testName + Helper.genDateBasedRandVal())
			.tags("IdeaTag" + Helper.genDateBasedRand())
			.content("Test Content for " + testName)
			.build();
		} else {
			blogEntry = new BaseBlogPost.Builder("Entry " + testName + Helper.genDateBasedRandVal())
			.tags("EntryTag" + Helper.genDateBasedRand())
			.content("Test Content for " + testName)
			.useUploadedImage(Data.getData().file1)
			.build();
		}

		communityName = community.getName();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//add widget blog if requested
		if (isIdeationBlog) {
			logger.strongStep("Add Ideation Blog widget using API");
			log.info("INFO: Add Ideation Blog widget with API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		} else {
			logger.strongStep("Add Blog widget using API");
			log.info("INFO: Add Blog widget with API");
			if (apiOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty())
			{
				community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);			
			}
		}
		
		//GUI
		//Load component and login
		if (isIdeationBlog){
			logger.strongStep("Open Communities and login: " +testUser1.getDisplayName());
			pUI.loadComponent(Data.getData().ComponentCommunities, true);}
		else{
			logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
			pUI.loadComponent(Data.getData().ComponentBlogs, true);}
		bUI.login(testUser1);

		if (isIdeationBlog) {
			//navigate to the API community
			logger.strongStep("Navigate to the community using UUID");
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(cUI);

			//Click on the blogs link in the nav
			logger.strongStep("Click on the Ideation Blog link in the navigation menu");
			log.info("INFO: Select Ideation Blog from the navigation menu");
			Community_LeftNav_Menu.IDEATIONBLOG.select(cUI);
		} else {
			//If it's a regular blog, upload an image to be used in the blog entry
			//
			//I use this roundabout way of reaching the blog settings beacuse
			//the "manage blog" link for community blogs is in a dropdown menu,
			//which I cannot get to work reliably as it randomly closes in WebDriver.
			logger.strongStep("Click on My Blogs link then search for " + communityName);
			log.info("INFO: Click on My Blogs link then search for " + communityName);
			bUI.clickLinkWait(BlogsUIConstants.MyBlogs);
			String blogUUID = getBlogUUID(communityName);
			log.info("INFO: Found " + communityName + " in My Blogs with the UUID " + blogUUID);

			//Click on settings for the blog
			logger.strongStep("Click on the Settings link");
			log.info("INFO: Select Blog Settings");
			bUI.clickLinkWait("css=ul.lotusInlinelist a[href*='" + blogUUID + "']:contains(Settings)");

			//Upload a file
			logger.strongStep("Upload " + filesToUpload.size() + " new file(s)");
			log.info("INFO: Upload " + filesToUpload.size() + " new file(s)");
			bUI.blogsAddFileToUpload(filesToUpload);

			//Return to the blog
			//navigate to the API community
			logger.strongStep("Navigate to the community using UUID");
			log.info("INFO: Navigate to the community using UUID");
			pUI.loadComponent(Data.getData().ComponentCommunities, true);
			community.navViaUUID(cUI);
			bUI.fluentWaitTextPresent(community.getName());

			//Click on the blogs link in the nav
			logger.strongStep("Click on the Blog link in the navigation menu");
			log.info("INFO: Select Blog from the navigation menu");
			Community_LeftNav_Menu.BLOG.select(cUI);
		}

		if (isIdeationBlog) {
			//Try to find the ideation blog in the list of blogs
			logger.strongStep("Click on the default Ideation Blog link");
			log.info("INFO: Select the default Ideation Blog link");
			bUI.fluentWaitPresent(bUI.getCommIdeationBlogLink(community));
			//click on blog
			bUI.clickLink(bUI.getCommIdeationBlogLink(community));
		}

		//select New Entry button
		logger.strongStep("Click on New Entry button");
		log.info("INFO: Select New Entry button");
		if (isIdeationBlog){
			logger.strongStep("Click on the New Idea link");
			bUI.clickLinkWait(BlogsUIConstants.NewIdea);}
		else{
			logger.strongStep("Click on the New Entry link");
			bUI.clickLinkWait(BlogsUIConstants.BlogsNewEntry);}

		//Create a new idea
		logger.strongStep("Create a new entry and click on Post button");
		log.info("INFO: Create a new entry and submit");
		blogEntry.create(bUI);

		//Confirm the idea appears as posted
		logger.strongStep("Verify the entry is visible on the page");
		bUI.fluentWaitPresent("css=div.blogsWrapText:contains(" + blogEntry.getContent() + ")");

		//Log out
		logger.strongStep("Log out of the session");
		bUI.logout();
		Pair<BaseCommunity, BaseBlogPost> communityAndBlogEntry;
		communityAndBlogEntry = new Pair<BaseCommunity, BaseBlogPost>(community, blogEntry);

		return communityAndBlogEntry;
	}	

	/**
	 * Given the name of a blog entry and optional other text strings, attempt 
	 * to find an element on the page which:
	 * <ul>
	 * <li>Contains a link to the blog entry
	 * <li>Contains as text all other strings passed as optional arguments
	 * <li>Will open an embedded blog frame when clicked on.
	 * </ul> 
	 * 
	 * @param blogEntryName - The title of the blog entry
	 * @param mustContain - Any other strings the element on the page must contain 
	 * @return An element that will open the blog entry in an embedded frame when
	 * clicked on, or null if none could be found
	 */
	private Element getBlogEntryEECreationEvent(String... mustContain){

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Retrieve events in recent updates");
		log.info("INFO: Getting events in recent updates");
		List<Element> events = driver.getVisibleElements(HomepageUIConstants.activityStreamNewsItems);
		log.info("INFO: Got " + events.size() + " events");
		Element el = null;
		int nMatches = 0;

		for (Element curElem : events) {

			String eventText = curElem.getText();
			boolean found = true;
			
			for (String s : mustContain) {
				if (!eventText.contains(s)) {
					found = false;
					break;
				}
			}
			if (found) {
				el = curElem;
				nMatches++;
			}
		}

		// Joins the arguments to one string for display
		StringBuffer args = new StringBuffer();
		boolean first = true;
		for (String s : mustContain) {
			if (first)
				first = false;
			else
				args.append(", ");
			args.append(s);
		}
		logger.strongStep("Verify that a single event is returned");
		Assert.assertTrue(nMatches < 2, "Multiple elements found when trying to find an event: " + 
				nMatches + " found, 1 or 0 expected. Arguments were: " + args);

		return el;
	}

	/**
	 * This is a refactoring of the verification the widgetsBlogEEFunctions
	 * test performs on the recent updates feeds of the homepage, community,
	 * and profile. See the comments for the widgetsBlogEEFunctions test for
	 * more details on what is verified.
	 * 
	 * @param count - A count of how many times this function has been called
	 * @param testUser2 - The user currently logged in
	 * @param communityName - The name of the community that was cerated for the test
	 * @param blogEntry - The blog entry that was created for the test
	 */
	private void verifyBlogCreationAndEntryInUpdates(
			int count, User testUser2, String communityName, BaseBlogPost blogEntry) {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String entryName = blogEntry.getTitle();
		
		logger.strongStep("Close the Guided Tour Popup if it appears");
		cUI.closeGuidedTourPopup();
		// Wait for the page to load
		logger.strongStep("Verify the link for the entry is displayed");
		bUI.fluentWaitPresent("css=div.lotusPostContent a:contains(" + entryName + "):nth(0)");

		// Get a handle to the current window
		String originalWindow = driver.getWindowHandle();
		
		//verify user 2's "I'm Following" page contains user 1 creating a blog
		logger.strongStep("Verify that blog that " + testUser1.getDisplayName() +
				" created appears in the I'm Following page of " + testUser2.getDisplayName());
		log.info("INFO: Verifying that blog that " + testUser1.getDisplayName() +
				" created appears in the I'm Following page of " + testUser2.getDisplayName());
		Element blogDiv = getBlogEntryEECreationEvent(communityName, testUser1.getDisplayName(), "community blog");
		Assert.assertFalse(blogDiv == null, "The blog " + communityName + " that " + testUser1.getDisplayName() +
				" created did not appear.");		 

		// Verify the blog entry that user 1 created appears on user 2's "I'm Following" page 
		logger.strongStep("Verify that the blog entry that " + testUser1.getDisplayName() +
				" created appears in the I'm Following page of " + testUser2.getDisplayName());
		log.info("INFO: Verifying that the blog entry that " + testUser1.getDisplayName() +
				" created appears in the I'm Following page of " + testUser2.getDisplayName());
		Element entryDiv;
		if (count == 1)
			entryDiv = getBlogEntryEECreationEvent(entryName, testUser1.getDisplayName(), communityName);
		else // Once a comment gets posted the commenter's name appears rather than the entry's author
			entryDiv = getBlogEntryEECreationEvent(entryName, "You commented", communityName);
		logger.strongStep("Verify that the blog entry that " + testUser1.getDisplayName() + " created appears");
		Assert.assertFalse(entryDiv == null, "The blog entry " + entryName + " that " + testUser1.getDisplayName() +
				" created did not appear.");

		// Click on the blog creation event to open the embedded frame
		logger.strongStep("Open the embedded blog frame for the blog creation event");
		log.info("INFO: Clicking to open the embedded blog frame for the blog creation event");
		openEEFromEntry(blogDiv, bUI);

		// Switch to the embedded frame, verifying it contains a link to the blog
		logger.strongStep("Navigate to embedded blog frame");
		log.info("INFO: Switching to embedded blog frame");
		String headerBlogLinkSelector = "css=h1.lotusHeading > a[title^='Navigate to " + communityName + "']";
		waitForAndSwitchToEEFrame(BlogsUIConstants.BlogsEEFrameIdentifier, headerBlogLinkSelector);

		// verify the frame contains text about user 1 creating a blog
		String blogCreationText = String.format(Data.COMMUNITY_ADD_BLOG, testUser1.getDisplayName(), communityName);
		logger.strongStep("Verify the embedded frame contains the text: '" + blogCreationText + "'");
		Assert.assertTrue(bUI.isTextPresent(blogCreationText),
				"The embedded frame did not contain the text: '" + blogCreationText + "'");

		// close the embedded frame
		logger.strongStep("Return to the webpage and click on the 'X' button to close the embedded blog frame");
		bUI.switchToTopFrame();
		bUI.clickLinkWait(BlogsUIConstants.BlogsEECloseFrame);

		// Click on the blog entry to open the embedded frame
		logger.strongStep("Open the embedded blog frame for the blog creation event");
		log.info("INFO: Clicking to open the embedded blog frame for the blog creation event");
		openEEFromEntry(entryDiv, bUI);

		// Switch to the embedded frame, verifying it contains a link to the blog entry
		logger.strongStep("Navigate to embedded blog frame");
		log.info("INFO: Switching to embedded blog frame");
		Element eeFrameElement = waitForAndSwitchToEEFrame(BlogsUIConstants.BlogsEEFrameIdentifier, "link=" + entryName);
		Assert.assertNotNull(eeFrameElement, "Could not successfully switch to the embedded frame!");

		//Verify 
		//blog entry name , tag , Like .Read More... link , Comments tab , Recent Updates tab show in Blog EE
		//description and image show in Blog EE , if images are more than 4, only show 4 image thumbnails
		logger.strongStep("Verify the contents of embedded frame");
		log.info("INFO: Verifying contents of embedded frame");
		Assert.assertTrue(driver.isTextPresent(blogEntry.getTitle()),
				"The blog entry's title did not appear in the embedded frame");
		Assert.assertTrue(driver.isTextPresent(blogEntry.getTags()),
				"The blog entry's tag did not appear in the embedded frame");
		if(count == 1){
			Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEELike),
					"The link to like a blog entry did not appear in the embedded frame");
		}else{
			Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEEUnlike),
					"The link to unlike a blog entry did not appear in the embedded frame");
		}
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEEReadMore),
				"The link read more of a blog entry did not appear in the embedded frame");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEECommentsTab),
				"The comments tab did not appear in the embedded frame");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEERecentUpdatesTab),
				"The recent updates tab did not appear in the embedded frame");
		Assert.assertTrue(driver.isTextPresent(blogEntry.getContent()),
				"The blog entry's conents did not appear in the embedded frame");
		Assert.assertTrue(driver.isElementPresent("css=a.eeImgPrev img[src*='" + Data.getData().file1 + "']"),
				"The blog entry's image did not appear in the embedded frame");	
		
		// do additional checks (liking and opening new windows) on first run only
		if (count == 1) {
			//click Like to recommend this entry
			logger.strongStep("Click on the Like link");
			bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEELike);

			//Verify recommend successfully and unlike link shows
			logger.strongStep("Verify the Unlike link appears");
			Assert.assertTrue(bUI.isElementPresent(BlogsUIConstants.BlogsEEUnlike),
					"The link to unlike a blog entry did not appear after liking it");

			//click Read More... link
			logger.strongStep("Click on the 'Read more ...' link and verify a new window opens up");
			log.info("INFO: Select 'Read more ...' link and verify a new window opens up");
			bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEEReadMore);
			bUI.fluentWaitNumberOfWindowsEqual(2);

			//Verify open a new tab or new window and navigate to this entry page
			logger.strongStep("Switch to the new window and verify its contents");
			log.info("INFO: Switch to the new window and verify its contents");
			driver.switchToFirstMatchingWindowByPageTitle(communityName);
			Assert.assertTrue(driver.isTextPresent(entryName));
			Assert.assertTrue(driver.isTextPresent(communityName));

			//Switch back to original embedded frame
			logger.strongStep("Close the new window and switch to the origial window");
			log.info("INFO: Closing new window and switching to the origial window");
			bUI.close(cfg);
			driver.switchToWindowByHandle(originalWindow);

			// click one of image
			logger.strongStep("Click on the image " + Data.getData().file1);
			String imageSelector = "css=a.eeImgPrev img[src*='" + Data.getData().file1 + "']";
			bUI.clickLinkWithJavascript(imageSelector);

			//Verify open a new tab or new window and navigate to this entry page
			logger.strongStep("Verify the contents of the new window");
			log.info("INFO: Verifying contents of new window");
			driver.switchToFirstMatchingWindowByPageTitle(communityName);
			Assert.assertTrue(driver.isTextPresent(entryName));
			Assert.assertTrue(driver.isTextPresent(communityName));

			//Switch back to original embedded frame
			logger.strongStep("Close new window and switch to the origial window");
			log.info("INFO: Closing new window and switching to the origial window");
			bUI.close(cfg);
			driver.switchToWindowByHandle(originalWindow);

		}

		//add a comment
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEECommentsTab);
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEECommentsTab);
		String commentText = Data.getData().commonComment + Helper.genDateBasedRand() + count;
		logger.strongStep("Type comment " + count + " into Embedded Experience text field");
		log.info("INFO: Entering comment " + count + " into Embedded Experience text field");
		typeMessageInCommentField(commentText, eeFrameElement, bUI);
		logger.strongStep("Click on the Post link");
		log.info("INFO: Clicking link to post comment");
		bUI.fluentWaitElementVisible(BlogsUIConstants.BlogsEEPostComment);
		bUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEEPostComment);

		//Verify the comment count is updated
		logger.strongStep("Verify the comment count is now " + count);
		bUI.fluentWaitTextPresent("Comments (" + count + ")");

		//verify comment is saved successfully
		logger.strongStep("Verify the posted comment appears");
		log.info("INFO: Verifying the comment that was posted appears");
		List<Element> postedComments = driver.getVisibleElements(BlogsUIConstants.BlogsEECommentContent);
		boolean commentFound = false;
		for (Element postedComment : postedComments) {
			String postedCommentText = postedComment.getText();
			if (postedCommentText.equals(commentText)) {
				commentFound = true;
				break;
			}
		}
		
		Assert.assertTrue(commentFound, "Could not find comment with text [" + commentText + "]");

		//Switching back to the parent frame
		logger.strongStep("Switching back to the parent frame");
		log.info("INFO: Switch back to the top frame");
		bUI.switchToTopFrame();
	
	}
	
	private void openEEFromEntry (Element ideaDiv, ICBaseUI ui) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//reset the pointer to prevent issues
		driver.clickAt(0, 0);

		String elementID = ideaDiv.getAttribute("id");
    	//look for news item body element
    	ui.fluentWaitPresent(HomepageUI.getNewsItemBody(elementID));
    	logger.strongStep("Open news item EE, attempting to click article body");
    	log.info("INFO: Open news item EE, attempting to click article body");
    	ideaDiv.clickAt(2,2);
    	
    	List<Element> visibleFrames = driver.getVisibleElements(HomepageUIConstants.GenericEEFrame);
    	if (visibleFrames == null || visibleFrames.isEmpty()) {
    		logger.strongStep("No frame visible after clicking on article body, attempting to hover");
    		log.info("INFO: No frame visible after clicking on article body, attempting to hover");
    		ui.getFirstVisibleElement(HomepageUI.getNewsItemBody(elementID)).hover();
    		ui.clickLinkWithJavascript(HomepageUI.getNewsItemEEOpener(elementID));
//    		ui.getFirstVisibleElement("css=a[data-eeopener=true]").click();
    	}
    	
    	//Sometimes clicking on the article will randomly open a new window
    	//as if we clicked on the image in the article, if so, close it
    	Set<String> windows = driver.getWindowHandles();
    	if (windows.size() > 1) {
    		String originalWindow = driver.getWindowHandle();
    		for (String window : windows) {
    			if (!window.equals(originalWindow)) {
    				driver.switchToWindowByHandle(window);
    				ui.close(cfg);
    				driver.switchToWindowByHandle(originalWindow);
    				break;
    			}
    		}
    	}
	}
	 
	/**
	 * Type text in the comment field in an embedded experience frame.
	 * @param statusMessage - the text to be typed in the comment field
	 * @param eeFrameElement - the element comprising the embedded experience frame, as returned by waitForAndSwitchToEEFrame
	 * @param ui - an instance of ICBaseUI or one of its subclasses such as BlogsUI
	 */
	private void typeMessageInCommentField(String commentText, Element eeFrameElement, ICBaseUI ui){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		Element commentInputField = null;
		Element ckeFrame = null;
		driver.turnOffImplicitWaits();
		List<Element> visibleCommentFields = driver.getVisibleElements(BlogsUIConstants.BlogsCommentTextArea);
		driver.turnOnImplicitWaits();
		if (visibleCommentFields != null && visibleCommentFields.size() > 0) {
			logger.strongStep("Commment entry field found outside CKE frame");
			log.info("INFO: Commment entry field found outside CKE frame");
			commentInputField = visibleCommentFields.get(0);
		}
		
		if (commentInputField == null) {
			ckeFrame = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_iFrame);
			logger.strongStep("Switch to Frame: CKEditor");
			log.info("INFO: Switching to Frame: CKEditor");
			driver.switchToFrame().selectFrameByElement(ckeFrame);
			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(commentText);

			logger.strongStep("Returning to parent frame to click 'Post' button");
			log.info("INFO: Returning to parent frame to click 'Post' button");
			ui.switchToTopFrame();
			driver.switchToFrame().selectFrameByElement(eeFrameElement);
			logger.strongStep("Returned to parent frame to click 'Post' button");
			log.info("INFO: Returned to parent frame to click 'Post' button");
		} else {
			commentInputField.type(commentText);
		}
	}

	/**
	 * This function takes two arguments: a selector that identifies any frame
	 * and a selector that identifies the content the frame should have. The 
	 * function then performs a fluent wait for a frame that has contents that
	 * match the selector of the second argument. As a side effect, if a
	 * matching frame is found, it will be switched to.
	 * @param frameSelector - a selector that matches a frame on the page
	 * @param selectorForElementWithinFrame - a selector that matches content that should exist in the frame
	 * @return The frame's name if a frame containing content matching the selector was
	 * found within the time limit specified in cfg.getFluentwaittime(), null
	 * otherwise
	 */
	private Element waitForAndSwitchToEEFrame(final String frameSelector, final String selectorForElementWithinFrame){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Entering fluentWait.locator: " + frameSelector);
		log.info("Entering fluentWait.locator: " + frameSelector);
		String fluentWaitTimeout = cfg.getFluentwaittime();
		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
				.withTimeout(Long.valueOf(fluentWaitTimeout), TimeUnit.SECONDS)
				.pollingEvery(5000, TimeUnit.MILLISECONDS)
				.ignoring(ElementNotFoundException.class);

		Element foo = wait.until(new Function<RCLocationExecutor, Element>() {
			public Element apply(RCLocationExecutor driver)
			{
				String frameName;
				logger.strongStep("Getting frames for " + frameSelector);
				log.info("INFO: Getting frames for " + frameSelector);
				List<Element> frames = driver.getElements(frameSelector);		// get all of the frames that match the selector
				logger.strongStep("Found " + frames.size() + " frames");
				log.info("INFO: Found " + frames.size() + " frames");
				for(Element frame : frames){									// step through each one
					frameName = frame.getAttribute("name");						// get the frame's name
					logger.strongStep("Switching to frame with name: " + frameName);
					log.info("INFO: Switching to frame with name: " + frameName);
					driver.switchToFrame().selectFrameByElement(frame);			// change scope to within this frame
					logger.strongStep("Looking inside frame for element matching selector: " + selectorForElementWithinFrame);
					log.info("INFO: Looking inside frame for element matching selector: " + selectorForElementWithinFrame);
					driver.turnOffImplicitWaits();
					boolean foundElement = driver.isElementPresent(selectorForElementWithinFrame);
					driver.turnOnImplicitWaits();
					logger.strongStep("Found element inside frame: " + foundElement);
					log.info("INFO: Found element inside frame: " + foundElement);
					if(foundElement)											// if it contains the element we're looking for
						return frame;										// ee has loaded and we can return
					else														// otherwise switch back to the top level frame
						pUI.switchToTopFrame();
				}
				return null;
			}
			@Override
			public String toString(){
				return String.format("\"%s\" locator ", frameSelector);
			}
		});

		return foo;
	}

	/**
	 * Attempts to return the UUID for the given blog. Assumes the client
	 * is currently a tab with a list of blogs, and that the blog is
	 * listed in this tab.
	 * @param blogName
	 * 		The name of the blog to find the UUID of
	 * @return
	 * 		The UUID of the activity, if found
	 * @throws
	 * 		AssertionError, if the activity's UUID cannot be found
	 */
	private String getBlogUUID (String blogName) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Find the link to our activity
		String partialName;
		if (blogName.length() > 39)
			partialName = blogName.substring(0, 40);
		else
			partialName = blogName;
		Element activityLink = driver.getSingleElement("linkpartial=" + partialName);
		logger.strongStep("Verify the activity with name " + partialName + " is found");
		Assert.assertFalse(activityLink == null, "No activity with found with name " + partialName);

		//extract the Activity UUID from the link with a regular expression 
		String activityLinkURL = activityLink.getAttribute("href");
		Pattern uidPattern = Pattern.compile("blogs/([^/]+)/");
		Matcher uidMatcher = uidPattern.matcher(activityLinkURL);
		logger.strongStep("Verify that the valid UUID is found for activity " + blogName);
		Assert.assertTrue(uidMatcher.find(), "No valid UUID found for activity "
				+ blogName
				+ ", link was " + activityLinkURL);
		return uidMatcher.group(1); 
	}

	// Container to ease passing around a tuple of two objects. This object
	// provides a sensible implementation of equals(), returning true if
	// equals() is true on each of the contained objects. 

	private class Pair<F, S> {

		public final F first;
		public final S second;

		public Pair(F car, S cdr){
			first = car;
			second = cdr;
		}

		@SuppressWarnings("unused")
		public boolean equals (Pair<F, S> anotherPair) {
			return (this.first.equals(anotherPair.first) &&
					this.second.equals(anotherPair.second));
		}
	}
}
