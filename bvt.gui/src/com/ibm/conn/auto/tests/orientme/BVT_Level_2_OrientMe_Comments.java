package com.ibm.conn.auto.tests.orientme;

import com.ibm.conn.auto.webui.constants.OrientMeUIConstants;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.OrientMeUI;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;

import org.testng.Assert;

public class BVT_Level_2_OrientMe_Comments extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_OrientMe_Comments.class);
	private TestConfigCustom cfg;
	private OrientMeUI omUI;
	private ItmNavCnx8 itmNavCnx8;

	private User testUserA;
	private User testUserB;
	private User testUserC;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		omUI = OrientMeUI.getGui(cfg.getProductName(), driver);
		itmNavCnx8 = new ItmNavCnx8(driver);

		testUserA = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();
		testUserC = cfg.getUserAllocator().getUser();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>User can add multiple comments with mentions on status entries. Close comments button appears on the screen with number of comments posted on right of it.</li>
	*<li><B>Step:</B>Log in as UserA, create a status entry </li>
	*<li><B>Verify:</B>A success message displays</li>
	*<li><B>Step:</B>Click on the text 'Add a comment' and enter some text and a few @ mentions. Click Post.</li>
	*<li><B>Verify:</B> 1) The comment posted without error. 2) Content looks correct, 3) A 'Close comment section' icon (up arrow) appears in the lower left corner of the card, 4) The number of comments for the entry (to the right of the up arrow icon) is correct</li>
	*<li><B>Step:</B>Hover over the @mention to see the business card</li>
	*<li><B>Verify:</B>The user's business card displays ok </li>
	*<li><B>Step:</B>Add one more comment</li>
	*<li><B>Verify:</B>:  1) comments get added without error, 2) the comment count, to the right of the 'up arrow' icon gets incremented as each comment gets posted, 3) count is correct.</li>
	*<li><B>Step:</B>Click on the 'up arrow' icon to close the comment section.</li>
	*<li><B>Verify:</B>The comment section is closed.  Should no longer see the 'Add a comment' section.  Only the last comment added will display.</li>
	*<li><B>Step:</B>Using a status entry that have multiple comments, hover over the 'Open comment section' icon.</li>
	*<li><B>Verify:</B>The alternate text includes the number of comments.  Something like this:  'X comments. Open comment section'.</li>
	*<li><B>Step:</B>Click on the 'Open comment section' icon.</li>
	*<li><B>Verify:</B>All previously posted comments display on screen.</li>
	*/
	
	@Test(groups = {"level2", "cplevel2"})
	public void addCommentsTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		omUI.startTest();
		
		String message = "OM " + Helper.genRandString(8);
		String comment1 = "Comment" + Helper.genRandString(8);
		String comment2 = "Comment" + Helper.genRandString(8);
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.loginAndGoTOLatestUpdatesTab(testUserA, false);
		logger.strongStep(testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		log.info("INFO: " + testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		omUI.postStatus(message,"Your status update was successfully posted.");	
		
		omUI.postCommentWithMentions(message,comment1,testUserB,testUserC);
		omUI.fluentWaitTextPresent(comment1);
		
		omUI.verifyCommentsMentions(comment1, testUserB, testUserC);
		
		omUI.fluentWaitElementVisible(OrientMeUIConstants.closeCommentSection);
		log.info("INFO: Close comment section button is visible on the screen");
		omUI.fluentWaitElementVisible(OrientMeUIConstants.commentCount);
		String commentCount=omUI.getElementText(OrientMeUIConstants.commentCount);
		Assert.assertEquals(commentCount,"1","The number of comments for the entry is correct");
		log.info("INFO: The number of comments for the entry is 1");
		omUI.getFirstVisibleElement(OrientMeUIConstants.closeCommentSection).click();
		
		omUI.postCommentWithMentions(message,comment2,testUserB,testUserC);
		omUI.fluentWaitTextPresent(comment2);
		
		omUI.verifyCommentsMentions(comment2, testUserB, testUserC);
		
		log.info("INFO: Close comment section button is visible on the screen");
		omUI.fluentWaitElementVisible(OrientMeUIConstants.commentCount);
		commentCount=omUI.getElementText(OrientMeUIConstants.commentCount);
		Assert.assertEquals(commentCount,"2","The number of comments for the entry is correct");
		log.info("INFO: The number of comments for the entry is 2");
		log.info("INFO: Click close comments");
		omUI.getFirstVisibleElement(OrientMeUIConstants.closeCommentSection).click();
		
		Assert.assertTrue(driver.isTextNotPresent(comment1), "User cannot see previously entered comments after closing comments section");
		log.info("INFO: User cannot see previously entered comments after closing comments section");
		
		log.info("INFO: Verify The alternate text includes the number of comments");
		String commentSelector= OrientMeUIConstants.buttonComment.replace("##innertext##", message);
		String alternateText=omUI.getFirstVisibleElement(commentSelector).getAttribute("title");
		Assert.assertEquals("2 comments. Open comment section", alternateText,"The alternate text includes the number of comments. Alternate text : 2 comments. Open comment section");
		log.info("INFO: The alternate text includes the number of comments. Alternate text :" + alternateText);
		
		log.info("INFO: Verify all previously posted comments display on screen");
		omUI.getFirstVisibleElement(commentSelector).click();
		omUI.fluentWaitTextPresent(comment1);
		omUI.fluentWaitTextPresent(comment2);
		log.info("INFO: Comments " + comment1 + " and " + comment1 + " displayed on screen.");
		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Comment a post with hashtag and verify it.</li>
	*<li><B>Pre-req:</B>Log into Orient Me as UserB.  Add UserA as an 'important' person</li>
	*<li><B>Step:</B>Log in as UserA, From 'Top Updates' tab, create a status entry. </li>
	*<li><B>Step:</B>Add a comment with a hashtag and click Post</li>
	*<li><B>Verify:</B>The comment was posted without error and content/hashtag looks ok</li>
	*<li><B>Step:</B>Log into Orient Me as UserB. Click on UserA's icon in the Important to me bar.</li>
	*<li><B>Verify:</B>UserA's status entries should display</li>
	*<li><B>Step:</B>Locate the entry with the comment that has a hashtag. Click on the hashtag.</li>
	*<li><B>Verify:</B>Search results page displays entries that contain the hashtag clicked on</li>
	*/
	// disabled due to defect ORIENTME-31
	@Test(groups = {"regression"}, enabled=false )
	public void addHashtagComment() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		omUI.startTest();
		String message = "OM " + Helper.genRandString(8);
		String successMessage="Your status update was successfully posted.";
		String comment = "Comment" + Helper.genRandString(8);
		String hashtag = " #hashtag_" + Helper.genRandString(8);
		
		// As UserB, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserB.getDisplayName());
		omUI.goToOrientMe(testUserB, false);
		
		// Data setup
		WebElement user = itmNavCnx8.getItemInImportantToMeList(testUserA.getDisplayName(), false);
		if (user == null)  {
			log.info("Add " + testUserA.getDisplayName() + " to the Important to Me list.");
			itmNavCnx8.addImportantItem(testUserA.getDisplayName(), true);
		} else {
			log.info(testUserA.getDisplayName() + " is already in the Important to Me list.");
		}
		omUI.logout();
		
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.loginAndGoTOLatestUpdatesTab(testUserA, true);
		
		logger.strongStep(testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		log.info("INFO: " + testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		omUI.postStatus(message, successMessage);

		logger.strongStep(testUserA.getDisplayName() + " post a comment on newly created status entry");
		log.info("INFO: " + testUserA.getDisplayName() + " post a comment on newly created status entry");
		omUI.postComment(message,comment + hashtag);
		omUI.fluentWaitTextPresent(message);
		omUI.fluentWaitTextPresent(comment);
		omUI.fluentWaitTextPresent(hashtag);
		log.info("INFO: User successfully added a comment (" + comment + ") with hashtag (" + hashtag + ") on newly created status (" + message + ")");
		omUI.logout();
		
		// As UserB, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserB.getDisplayName());
		omUI.goToOrientMe(testUserB, true);
		Assert.assertTrue(itmNavCnx8.clickImportantItem(testUserA.getDisplayName()),testUserB.getDisplayName() + " clicked on " + testUserA.getDisplayName() + " icon in the Important to me bar");
		omUI.waitForPageLoaded(driver);
		omUI.fluentWaitTextPresent(message);
		omUI.fluentWaitTextPresent(comment);
		omUI.fluentWaitTextPresent(hashtag);
		log.info("INFO: Search results page displays entry that contains a comment (" + comment + ") with hashtag (" + hashtag + ") on status (" + message + ") created by " + testUserA.getDisplayName());
		omUI.verifyHashtag(message, hashtag);

		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Comment a post with more than 1000 characters and verify error.</li>
	*<li><B>Step:</B>Log in as UserA, From 'Top Updates' tab, create a status entry. </li>
	*<li><B>Step:</B>Add a comment that has more than 1000 characters</li>
	*<li><B>Verify:</B>An error message displays indicating that the character limit has been exceeded</li>
	*/
	
	@Test(groups = {"regression"})
	public void commentExceedsCharacterLimitsTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		omUI.startTest();
		String message = "OM " + Helper.genRandString(8);
		String successMessage="Your status update was successfully posted.";
		String comment = Helper.genRandString(1001);
		Assert.assertTrue(comment.length()==1001,"Length of comment is " + comment.length());
		log.info("Status length is : " + comment.length());
		
		// As UserA, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		omUI.loginAndGoTOLatestUpdatesTab(testUserA, true);

		logger.strongStep(testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		log.info("INFO: " + testUserA.getDisplayName() + " post a status entry from Latest Updates tab");
		omUI.postStatus(message, successMessage);

		logger.strongStep(testUserA.getDisplayName() + " post a comment with more that 1000 characters");
		log.info("INFO: " + testUserA.getDisplayName() + " post a comment with more that 1000 characters");
		omUI.postComment(message,comment);
		Assert.assertTrue(omUI.fluentWaitTextPresent("Exceeds character limit by 1"), "Exceeds character limit by 1 error message is displayed");
		log.info("INFO: Error message with text 'Exceeds character limit by 1' is displayed on the screen");
		omUI.endTest();
		
	}
}