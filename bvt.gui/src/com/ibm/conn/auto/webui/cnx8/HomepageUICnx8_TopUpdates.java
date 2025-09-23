package com.ibm.conn.auto.webui.cnx8;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.OrientMeUIConstants;

public class HomepageUICnx8_TopUpdates extends HCBaseUI {
	
	private static Logger log = LoggerFactory.getLogger(HomepageUICnx8_TopUpdates.class);
	private Assert cnxAssert;

	public HomepageUICnx8_TopUpdates(RCLocationExecutor driver) {
		super(driver);
		cnxAssert = new Assert(log);
	}
	
	/**
	 * Method to share post in mention category
	 * @param user - user used in post
	 * @param updatePost - message which is posted
	 */
	public void postAtMention(User user, String updatePost) {		
		String searchUser = "@"+user.getDisplayName();
		waitForElementVisibleWd(By.xpath(HomepageUIConstants.topUpdatesShareBox), 5);
		clickLinkWd(By.xpath(HomepageUIConstants.topUpdatesShareBox), "Click On ShareBox");
		typeWithDelayWd(searchUser, By.xpath(HomepageUIConstants.topUpdatesShareBox));
		
		waitForElementVisibleWd(By.xpath(HomepageUIConstants.topUpdatesTypeAheadList.replace("PLACEHOLDER", user.getDisplayName())), 5);
		scrollToElementWithJavaScriptWd(findElement(By.xpath(HomepageUIConstants.topUpdatesTypeAheadList.replace("PLACEHOLDER", user.getDisplayName()))));
		clickLinkWd(By.xpath(HomepageUIConstants.topUpdatesTypeAheadList.replace("PLACEHOLDER", user.getDisplayName())), "Click on typeahead frame list");
		
		typeWithDelayWd(updatePost, By.xpath(HomepageUIConstants.topUpdatesShareBox));
		clickLinkWaitWd(By.xpath(HomepageUIConstants.topUpdatesPost), 3, "Click on post button");
	}
   
	/**
	 * Method to verify mention post
	 * @param user - user used in post
	 * @param msg - message which is posted
	 * @return boolean value based on visibility of post
	 */
    public boolean verifyMentionsPost(User user, String msg){ 
    	String mentionsPostLocator = HomepageUIConstants.topUpdatesMentionsPost.replace("PLACEHOLDER1", user.getDisplayName()).replace("PLACEHOLDER2", msg);
    	waitForElementVisibleWd(By.xpath(mentionsPostLocator), 5);
        return isElementDisplayedWd(By.xpath(mentionsPostLocator));
    }
    
    /**
     * Method to comment on Top Updates post.
     * @param commentMessage - message to post in comment.
     * @param postMessage - String used to generate post message locator.
     */
	public void commentOnTopUpdates(String commentMessage,String postMessage) {	
		scrollToElementWithJavaScriptWd(By.xpath(HomepageUIConstants.topUpdatesCommentBox));
		waitForElementVisibleWd(By.xpath(HomepageUIConstants.topUpdatesCommentBox), 3);
		clickLinkWd(By.xpath(HomepageUIConstants.topUpdatesCommentBox), "Click on comment text area");
		typeWithDelayWd(commentMessage, By.xpath(HomepageUIConstants.topUpdatesCommentBox));
		clickLinkWaitWd(By.xpath(HomepageUIConstants.topUpdatesCommentPost), 3, "Click on comment post button");
	}
	
	/**
	 * Best effort to ensure the Top Updates page is ready to interact
	 * eg. post message in Share Something
	 */
	public void waitForTopUpdatesInteractable()  {
		int secToWait = 10;
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), secToWait);
		driver.turnOffImplicitWaits();
		
		try {
			ExpectedCondition<Boolean> expected = new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driver) {
					// check for All quiet image which means content is loaded but empty
					if (isElementPresentWd(By.cssSelector(OrientMeUIConstants.allQuietImage))) {
						return true;
					} else {
						if (!isElementPresentWd(By.xpath(HomepageUIConstants.topUpdatesShareBox))) {
							return false;
						} else {
							// this is not perfect as the loading element may not even started to show at this point
							// but we should not always expect it will show
							if (isElementPresentWd(By.cssSelector(OrientMeUIConstants.tilesLoading))) {
								return false;
							} else {
								return true;
							}
						}
					}
				}
			};
			wait.until(expected);
			waitForPageLoaded(driver);
		} catch (TimeoutException te)  {
			cnxAssert.assertTrue(false, "Expect either All Quiet Home image is shown or tiles animation is gone after " + secToWait + " sec.");
			driver.turnOnImplicitWaits();
			throw te;
		}
		driver.turnOnImplicitWaits();
	}
	
}
