package com.ibm.conn.auto.webui;

import java.util.List;
import java.util.function.Supplier;

import com.ibm.conn.auto.webui.constants.OrientMeUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.webui.onprem.OrientMeOnPrem;

public class OrientMeUI extends ICBaseUI {
	
	private static Logger log = LoggerFactory.getLogger(OrientMeUI.class);
	HomepageUI homeui;

	public OrientMeUI(RCLocationExecutor driver) {
		super(driver);
		homeui=HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	
	/**
	 * Reset the count in the Responses icon.
	 * If count exists, click the icon and reload page to reset it.
	 */
	public void resetResponsesCount() {
		// check if the count is displayed, if so reload page
		String count = getResponseCount();
		if (count != null && !count.equals("0") && !count.isEmpty()) {
			log.info("Responses count = " + count + ", click icon to reset.");
			clickLink(OrientMeUIConstants.responseIcon);
			waitForPageLoaded(driver);
			driver.navigate().refresh();
		} else {
			log.info("Responses count not present, no need to reset.");
		}
		
		// check count again	
		count = getResponseCount();
		Assert.assertTrue(count == null || count.equals("0") || count.isEmpty(),
				"Responses count cannot be reset.");
	}
	
	public String getResponseCount() {
		Element responseIconElm = this.getFirstVisibleElement(OrientMeUIConstants.responseIcon);
		Element responseCountElm = responseIconElm.getSingleElement("css=span.globalText");
		
		return responseCountElm.getText();		
	}
	
	/**
	 * Click the Filter link and apply a filter
	 * @param filterText
	 * @param exactMatchInTypeahead true if the item has to exactly match the target string
	 */
	public void filterUpdates(String filterText, boolean exactMatchInTypeahead) {
		clickLink(OrientMeUIConstants.filterLink);
		getFirstVisibleElement(OrientMeUIConstants.filterBox).typeWithDelay(filterText);
		waitForTypeaheadDone();
		
		boolean isSelected = selectItemInTypeahead(OrientMeUIConstants.filterTypeahead, filterText, exactMatchInTypeahead);
		if(!isSelected){
			getFirstVisibleElement(OrientMeUIConstants.showMoreResults).click();
			isSelected = selectItemInTypeahead(OrientMeUIConstants.filterTypeahead, filterText, exactMatchInTypeahead);
		}
		Assert.assertTrue(isSelected, "Item is selected from filter updates typeahead!");
		
	}
	
	public void clickRemoveFilter(String filterSelector) {
		Element filter = getFirstVisibleElement(filterSelector+ OrientMeUIConstants.filterRemoveIcon);
		filter.click();
	}
	
	public boolean isTabSelected(Element element) {
		return element.getAttribute("class").contains("nav-item--selected");
	}

	/**
	 * Post the given message as status update.
	 * @param message
	 * @param successMessage 1. From latest updates - 'Your status update was successfully posted.' 2. From top updates - 'Your status update was successfully posted. Get back to it later in Latest Updates.'
	 */
	public void postStatus(String message, String successMessage) {
		log.info("Type status: " + message);
		clickLink(OrientMeUIConstants.shareSomething);
		getFirstVisibleElement(OrientMeUIConstants.shareSomethingBox).type(message);
		this.clickLink(OrientMeUIConstants.shareSomethingPost);
		
		log.info("Confirm status update was successfully posted message");
		Element alertMsg = driver.getFirstElement(OrientMeUIConstants.alertMessage);
		Assert.assertEquals(successMessage, 
				alertMsg.getText(), "Status update was successfully posted message is present.");		
	}
	
	/**
	 * Post the given message as status update with people mention at the end.
	 * @param message
	 * @param userToMention
	 */
	public void postStatusWithMention(String message, User userToMention) {
		String status = message + " @" + userToMention.getDisplayName();
		log.info("Type status: " + status);
		clickLink(OrientMeUIConstants.shareSomething);
		getFirstVisibleElement(OrientMeUIConstants.shareSomethingBox).typeWithDelay(status);
		waitForTypeaheadDone();
		
		boolean isSelected = selectItemInTypeahead(OrientMeUIConstants.shareSomethingTypeahead, userToMention.getDisplayName(), true);
		Assert.assertTrue(isSelected, "User is selected from typeahead!");
		
		this.clickLink(OrientMeUIConstants.shareSomethingPost);
		
		log.info("Confirm status update was successfully posted message");
		Element alertMsg = driver.getFirstElement(OrientMeUIConstants.alertMessage);
		Assert.assertEquals("Your status update was successfully posted. Get back to it later in Latest Updates", 
				alertMsg.getText(), "Status update was successfully posted message is present.");
		
	}

	/**
	 * Post a message with a # hashtag and @ mention 2 users.
	 * @param message
	 * @param hashtag
	 * @param userB
	 * @param userC
	 * @param successMessage
	 * @param status Its a status message with concatenation of actual message, hashtag and mentions
	 */
	public void postStatusWithHashtagMentions(String message, String hashtag, String userB, String userC, String successMessage) {
		String status = message + " #" + hashtag + " @" + userB;
		log.info("Type status: " + status);
		clickLink(OrientMeUIConstants.shareSomething);
		getFirstVisibleElement(OrientMeUIConstants.shareSomethingBox).typeWithDelay(status);
		waitForTypeaheadDone();
		
		boolean isSelected;
		//re-ensuring that the typehead is selected properly
		if(!(isSelected= selectItemInTypeahead(OrientMeUIConstants.shareSomethingTypeahead, userB, true)))
			isSelected= selectItemInTypeahead(OrientMeUIConstants.shareSomethingTypeahead, userB, true);
		Assert.assertTrue(isSelected, "User is selected from typeahead!");
		
		status= " @"+ userC;
		getFirstVisibleElement(OrientMeUIConstants.shareSomethingBox).typeWithDelay(status);
		waitForTypeaheadDone();
		
		//re-ensuring that the typehead is selected properly
		if(!(isSelected= selectItemInTypeahead(OrientMeUIConstants.shareSomethingTypeahead, userC, true)))
			isSelected= selectItemInTypeahead(OrientMeUIConstants.shareSomethingTypeahead, userC, true);
		Assert.assertTrue(isSelected, "User is selected from typeahead!");
		
		this.clickLink(OrientMeUIConstants.shareSomethingPost);
		
		log.info("Confirm status update was successfully posted message");
		this.fluentWaitElementVisible(OrientMeUIConstants.alertMessage);
		Element alertMsg = driver.getFirstElement(OrientMeUIConstants.alertMessage);
		Assert.assertEquals(successMessage, 
				alertMsg.getText(), "Status update was successfully posted message is present.");
	}

	/**
	 * Click hashtag link and entry containing the hashtag appears on search result page.
	 * @param message
	 * @param hashtag
	 */
	public void verifyHashtag(String message, String hashtag) {
		log.info("Verify hashtag link is present in status entry " + message);
		fluentWaitElementVisible(OrientMeUIConstants.hashtagText.replace("##hashtag##", hashtag));
		log.info("INFO: Click hashtag link");
		this.clickLink(OrientMeUIConstants.hashtagText.replace("##hashtag##", hashtag));
		String winHandleBefore = driver.getWindowHandle();
        for(String winHandle : driver.getWindowHandles()){
            driver.switchToWindowByHandle(winHandle);
        }
		
		this.waitForPageLoaded(driver);
		log.info("Verify entry containing the hashtag appears on search result page");
		Assert.assertTrue(this.fluentWaitTextPresent(message),
				"Entry containing the hashtag does not appear on search result page");
		driver.close();
		driver.switchToWindowByHandle(winHandleBefore);
	}

	/**
	 * Hover over the @ mention to see the business card are displayed
	 * @param message
	 * @param user1
	 * @param user2
	 * @param hashtag
	 * @param mention1 User1 selector after string manipulation
	 * @param mention2 User2 selector after string manipulation
	 */
	public void verifyMentions(String message, User user1, User user2, String hashtag) {
		
		String mention1= OrientMeUIConstants.statusMentions1.replace("##hashtag##", hashtag);
		String mention2= OrientMeUIConstants.statusMentions2.replace("##hashtag##", hashtag);
		log.info("INFO: Verify @mention user1 " + user1.getDisplayName() + " is present in the post");
		this.fluentWaitElementVisible(mention1);
		log.info("INFO: Verify @mention user2 " + user2.getDisplayName() + " is present in the post");
		this.fluentWaitElementVisible(mention2);
		log.info("INFO: Verify on hover over " + user2.getDisplayName() + " displays business card");
		homeui.verifyBizCard(getFirstVisibleElement(mention2));
		log.info("INFO: Verify on hover over " + user1.getDisplayName() + " displays business card");
		homeui.verifyBizCard(getFirstVisibleElement(mention1));	
	}

	/**
	 * Like/Unlike Status Entry and verify number to the right of heart is increased/decreased by 1
	 * @param message
	 * @param likePostSelector Selector after string manipulation
	 * @param eLikePost Element like/unlike post icon
	 */
	public void likeUnlikePost(String message) {
		String likePostSelector= OrientMeUIConstants.likePost.replace("##innertext##", message);
		Element eLikePost=getFirstVisibleElement(likePostSelector);

		log.info("Verify the heart icon is not selected by default");
		Assert.assertEquals("ic-icon-like", eLikePost.getAttribute("class"),"The heart icon is selected by default");
		log.info("Click heart icon to like the status");
		eLikePost.click();
		Assert.assertEquals("ic-icon-like ic-is-selected ic-action-animate", eLikePost.getAttribute("class"),"The heart icon is not selected");
		log.info("Verify after clicking like button the number to the right of heart is increased by 1");
		String likeCount=likePostSelector.replace("/button", "/span");
		Assert.assertEquals(getFirstVisibleElement(likeCount).getText(), "1","Number to the right of heart is 1");
		log.info("Click heart icon to unlike the status");
		eLikePost.click();
		log.info("Verify after clicking like button the number to the right of heart is decreased by 1");
		Assert.assertFalse(this.isElementPresent(likeCount),"The number to the right of the heart is not decreased by 1");
		
	}

	/**
	 * Hover over the link to see the URL preview is displayed. When user clicks on link then user is brought to the correct page
	 * @param message
	 * @param postLinkSelector Selector after string manipulation
	 */
	public void verifyPostLink(String message, String URL) {
		
		log.info("Click on the link present in status entry " + message);
		String postLinkSelector= OrientMeUIConstants.postLink.replace("##innertext##", message);
		this.clickLink(postLinkSelector);
		String winHandleBefore = driver.getWindowHandle();
        for(String winHandle : driver.getWindowHandles()){
            driver.switchToWindowByHandle(winHandle);
        }
		
		this.waitForPageLoaded(driver);
		log.info("Verify user is redirected to " + URL + " page");
		Assert.assertEquals(driver.getTitle(),"Google",
				"User is redirected to " + URL + " page");
		log.info("User is redirected to " + URL + " page");
		driver.close();
		driver.switchToWindowByHandle(winHandleBefore);
	}

	/**
	 * Delete Status Entry and verify delete and repost post functionality
	 * @param message
	 * @param moreActionsSelector Selector after string manipulation
	 * @param eMoreActions Element more actions button
	 */
	public void deleteAction(String message) {

		String post= OrientMeUIConstants.postText.replace("##innertext##", message);
		Element ePost=getFirstVisibleElement(post);
		ePost.hover();
		String moreActionsSelector= OrientMeUIConstants.moreActions.replace("##innertext##", message);
		Element eMoreActions=getFirstVisibleElement(moreActionsSelector);
		
		log.info("Click on 'Delete' status");
		eMoreActions.click();
		this.fluentWaitElementVisible(OrientMeUIConstants.deletePost);
		getFirstVisibleElement(OrientMeUIConstants.deletePost).click();
		
		log.info("Click the 'Cancel' button and verify that the confirmation dialog goes away and the status entry does not get deleted");
		this.fluentWaitElementVisible(OrientMeUIConstants.confirmCancel);
		getFirstVisibleElement(OrientMeUIConstants.confirmCancel).click();
		Assert.assertTrue(this.isTextPresent(message), 
				"Status entry is deleted after clicking cancel button.");
		log.info("Status entry is not deleted after clicking cancel button.");
		
		log.info("Click on 'Delete' status");
		ePost=getFirstVisibleElement(post);
		ePost.click();
		ePost.hover();
		this.fluentWaitElementVisible(moreActionsSelector);
		eMoreActions.click();
		this.fluentWaitElementVisible(OrientMeUIConstants.deletePost);
		getFirstVisibleElement(OrientMeUIConstants.deletePost).click();
		
		log.info("Confirm delete and verify that the status update is successfully deleted, and the status entry no longer appears on the page");
		this.fluentWaitElementVisible(OrientMeUIConstants.confirmDelete);
		getFirstVisibleElement(OrientMeUIConstants.confirmDelete).click();

		Element alertMsg = driver.getFirstElement(OrientMeUIConstants.alertMessage);
		Assert.assertEquals("Your status update was successfully deleted.", 
				alertMsg.getText(), "Your status update was successfully deleted.");
		
		Assert.assertFalse(this.isTextPresent(message), 
				"Status entry does not get deleted after clicking delete button.");		
	}

	/**
	 * Repost Status Entry and verify delete and repost post functionality
	 * @param message
	 * @param moreActionsSelector Selector after string manipulation
	 * @param eMoreActions Element more actions button
	 */
	public void repostAction(String message) {

		String likePostSelector= OrientMeUIConstants.likePost.replace("##innertext##", message);
		Element eLikePost=getFirstVisibleElement(likePostSelector);
		eLikePost.hover();
		String moreActionsSelector= OrientMeUIConstants.moreActions.replace("##innertext##", message);
		Element eMoreActions=getFirstVisibleElement(moreActionsSelector);

		log.info("Click on 'Repost this Update'");
		eMoreActions.click();
		this.fluentWaitElementVisible(OrientMeUIConstants.repost);
		getFirstVisibleElement(OrientMeUIConstants.repost).click();
		
		log.info("Verify message displays: The update was successfully reposted to your followers");

		Element alertMsg = driver.getFirstElement(OrientMeUIConstants.alertMessage);
		Assert.assertEquals("The update was successfully reposted to your followers.", 
				alertMsg.getText(), "Status update was successfully reposted message is present.");
		
	}
	
	/**
	 * post comment to the given status entry
	 * @param message
	 * @param comment
	 * @param commentSelector
	 * @param eComment
	 */

	public void postComment(String message, String comment) {
		String commentSelector= OrientMeUIConstants.buttonComment.replace("##innertext##", message);
		Element eComment=getFirstVisibleElement(commentSelector);

		log.info("Click on 'Open comment section' button");
		eComment.click();
		String cssCommentPlaceHolder="css=div[aria-describedby='" + driver.getFirstElement(OrientMeUIConstants.commentEditor).getAttribute("aria-describedby") + "']";
		getFirstVisibleElement(cssCommentPlaceHolder).click();
		getFirstVisibleElement(cssCommentPlaceHolder).type(comment);
		if(comment.length()<1001){
			getFirstVisibleElement(OrientMeUIConstants.postComment).click();
		}
		
	}
	
	/**
	 * post comment to the given status entry
	 * @param message
	 * @param comment
	 * @param user1
	 * @param user2
	 * @param commentSelector
	 * @param eComment
	 */

	public void postCommentWithMentions(String message, String comment, User user1, User user2) {
		String commentSelector= OrientMeUIConstants.buttonComment.replace("##innertext##", message);
		Element eComment=getFirstVisibleElement(commentSelector);

		log.info("Click on 'Open comment section' button");
		eComment.click();
		String cssCommentPlaceHolder="css=div[aria-describedby='" + driver.getFirstElement(OrientMeUIConstants.commentEditor).getAttribute("aria-describedby") + "']";
		getFirstVisibleElement(cssCommentPlaceHolder).click();
		getFirstVisibleElement(cssCommentPlaceHolder).typeWithDelay(comment + " @" + user1.getDisplayName());
		waitForTypeaheadDone();
		
		boolean isSelected = selectItemInTypeahead(OrientMeUIConstants.shareSomethingTypeahead, user1.getDisplayName(), true);
		Assert.assertTrue(isSelected, "User is selected from typeahead!");
		
		comment= " @"+ user2.getDisplayName();
		getFirstVisibleElement(cssCommentPlaceHolder).typeWithDelay(comment);
		waitForTypeaheadDone();
		isSelected = selectItemInTypeahead(OrientMeUIConstants.shareSomethingTypeahead, user2.getDisplayName(), true);
		Assert.assertTrue(isSelected, "User is selected from typeahead!");
		getFirstVisibleElement(OrientMeUIConstants.postComment).click();
		
	}
	
	/**
	 * post comment to the given status entry
	 * @param comment1
	 * @param testUserB
	 * @param testUserC
	 */

	public void verifyCommentsMentions(String comment1, User testUserB, User testUserC) {
		log.info("INFO: Verify @mention " + testUserB.getDisplayName() + " is present in the comment");
		fluentWaitElementVisible(OrientMeUIConstants.commentText.replace("##innertext##", comment1)+"/span[1]");
		log.info("INFO: Verify @mention " + testUserC.getDisplayName() + " is present in the comment");
		fluentWaitElementVisible(OrientMeUIConstants.commentText.replace("##innertext##", comment1)+"/span[2]");
		log.info("INFO: Verify on hover over " + testUserC.getDisplayName() + " displays business card");
		homeui.verifyBizCard(getFirstVisibleElement(OrientMeUIConstants.commentText.replace("##innertext##", comment1)+"/span[2]"));
		log.info("INFO: Verify on hover over " + testUserB.getDisplayName() + " displays business card");
		homeui.verifyBizCard(getFirstVisibleElement(OrientMeUIConstants.commentText.replace("##innertext##", comment1)+"/span[1]"));
	}
	
	// TODO: remove these after CNX8 move to ITM UI class is finalized
	/**
	 * Returns the target item in the Important to Me list
	 * @param item name of the target item (eg, user, community)
	 * @param includeSuggested whether it should attempt to find the item in the suggested list if it is not in the active list.
	 * @return Element of the target item, null if not found.
	 */
	/*public Element getItemInImportantToMeList(String targetItem, boolean includeSuggested) {
		String targetItemLocator = OrientMeUIConstants.importantToMeList +"[aria-label='"+targetItem+"']";
		List<Element> importantItem = driver.getVisibleElements(targetItemLocator);

		if (importantItem.size() > 0) {
			return importantItem.get(0);
		}
		
		if (includeSuggested)  {
			targetItemLocator = OrientMeUIConstants.suggestedImportantList + "[aria-label="+targetItem+"]";
			List<Element> suggestedItems = driver.getVisibleElements(targetItemLocator);
			if (suggestedItems.size() > 0) {
				return suggestedItems.get(0);
			}
		}
		
		log.info("Target item not found in the Important to Me list: " + targetItem);	
		return null;
	}*/
	
	/**
	 * Click the target item from Important to Me list
	 * @param targetItem
	 */
	/*public boolean clickImportantItem(String targetItem) {
		Element item = getItemInImportantToMeList(targetItem, false);
		if (item != null) {
			item.click();
			return true;			
		}
	
		log.info("Target item not found in the Important to Me list: " + targetItem);	
		return false;
	}*/
	
	/**
	 * Remove the target item from the Important to Me list
	 * @param target item
	 */
/*	public void removeItemFromImportantToMeList(String item)  {
		Element targetItem = getItemInImportantToMeList(item, false);
		
		if (targetItem.getWebElement().isDisplayed())  {
			clickLink(OrientMeUIConstants.removeImportantItem);
			
			// get the X icon in the bubble
			Element removeElem = targetItem.getSingleElement("css=button.remove-entry");
			removeElem.click();
			
			driver.turnOffImplicitWaits();
			WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 3);
			wait.until(ExpectedConditions.invisibilityOf(targetItem.getWebElement()));	
			driver.turnOnImplicitWaits();
			
			// exit ITM delete mode
			clickLink(OrientMeUIConstants.removeImportantItem);
		} else {
			throw new RuntimeException("Target element is not displayed.");
		} 
	}*/
	
	/**
	 * Add the target item to the Important to Me list
	 * @param targetItem
	 * @param exactMatchInTypeahead true if the item has to exactly match the target string
	 * @return element added
	 */
/*	public Element addImportantItem(String targetItem, boolean exactMatchInTypeahead) {
		clickLink(OrientMeUIConstants.addImportantItem);
		fluentWaitElementVisible(OrientMeUIConstants.addImportantBox);
		getFirstVisibleElement(OrientMeUIConstants.addImportantBox).typeWithDelay(targetItem);
		waitForTypeaheadDone();
		
		boolean isSelected = selectItemInTypeahead(OrientMeUIConstants.importantToMeTypeahead, targetItem, exactMatchInTypeahead);
		if (!isSelected && isElementVisible(OrientMeUIConstants.importantToMeTypeaheadMore))  {
			// click More if exists then try find the item again
			try {
				clickLink(OrientMeUIConstants.importantToMeTypeaheadMore);
				isSelected = selectItemInTypeahead(OrientMeUIConstants.importantToMeTypeahead, targetItem, exactMatchInTypeahead);
			} catch (AssertionError ae)  {
				Assert.assertTrue(false, "Item not found in typeahead: " + targetItem);
			}
		}
		Assert.assertTrue(isSelected, "Item is selected from typeahead!");
		
		// check the item is in the Important to Me list now
		Element elm = getItemInImportantToMeList(targetItem, false);
		Assert.assertNotNull(elm, "Item is in the Important to Me list.");
		
		return elm;
	}*/

	/**
	 * Select the target item in the given typeahead
	 * @param typeahead Top level selector of the typeahead
	 * @param targetItem
	 * @param exactMatch true if the item has to exactly match the target string
	 * @return
	 */
	private boolean selectItemInTypeahead(String typeahead, String targetItem, boolean exactMatch) {
		// uncomment sleep if we see timing issue selecting the entry again
//		sleep(1000);
		List<Element> typeaheadItems = driver.getVisibleElements(typeahead);
		
		log.info("Select item in typeahead: " + targetItem);
		boolean isSelected = false;
		for (Element item : typeaheadItems)  {
			if (exactMatch)  {
				// compare the entire string to avoid selecting 'Community for Bill User1' for 'Bill User1'
				Element entry1stLineText = item.getSingleElement("css=div.ui-typeahead-primary");
				if (entry1stLineText.getText().equalsIgnoreCase(targetItem)) {
					isSelected = true;
				}
			} else {
				if (item.getText().toLowerCase().endsWith(targetItem.toLowerCase())) {
					isSelected = true;
				}
			}
			if (isSelected) {
				item.click();
				break;
			}
		}
		return isSelected;
	}


	/**
	 * Pre CNX8: For server which OM is not the default homepage, it goes to the classic Homepage upon login.
	 * This method hits the OM url again after login.
	 * 
	 * Post CNX8: Expected behavior TBD
	 */
	public void goToOrientMe(User testUser, boolean preserveInstance) {
		if (cfg.getUseNewUI())  {
			loadComponent(Data.getData().HomepageImFollowing);
			loginAndToggleUI(testUser, cfg.getUseNewUI());
			
			// TODO: add click to go to OrientMe content in CNX8 UI
		} else  {
			log.info("Load OrientMe and Log In as: " + testUser.getDisplayName());
			LoginEvents.goToOrientMe(homeui, testUser, driver, OrientMeUIConstants.responseIcon, preserveInstance);
		}
	}


	/**
	 * This method calls goToOrientMe method and then click om go to latest update tab.
	 * It closes guided tour popup.
	 */
	public void loginAndGoTOLatestUpdatesTab(User testUser, boolean preserveInstance) {
		LoginEvents.loginAndGoToOMLatestUpdatesTab(homeui, testUser, driver, OrientMeUIConstants.responseIcon, OrientMeUIConstants.latestUpdate, preserveInstance);
	}

	/**
	 * Scroll to the end of the page on dynamically loading webpage
	 * @throws InterruptedException 
	 */
	public void scrollThroughPage(){
		Object lastHeight = driver.executeScript("return document.body.scrollHeight");
		while (true) {
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			sleep(2000);
			Object newHeight = driver.executeScript("return document.body.scrollHeight");
			if (newHeight.equals(lastHeight)) {
				break;
			}
			lastHeight = newHeight;
		}
	}
	
	/**
	 * Perform given actions (typically verification) and if exception occurs, scroll through 
	 * the page the go back to the top and do the action again.
	 * @param tester - actions to perform
	 */
	public void verifyWithScrolling(Supplier<Void> tester) {
		try {
			// perform actions
			tester.get();
			
		} catch (Exception e)  {
			if (isTextPresent("There are currently no updates"))  {
				// the page is empty
				throw e;
			} else {
				// scroll through the page, go back to the top and verify again
				log.info("INFO: Scroll to the bottom of the Top Updates page");
				scrollThroughPage();
				log.info("INFO: Verify text 'You've reached the end of your update stream' is displayed a the bottom of the page");
				Assert.assertTrue(fluentWaitTextPresent("You've reached the end of your update stream"),"Text 'You've reached the end of your update stream' not displayed a the bottom of the page");
				log.info("INFO: Text 'You've reached the end of your update stream' is displayed a the bottom of the page");

				log.info("INFO: Scroll upwards slightly to get 'Go To Top' button to appear on the screen");
				Object offset=driver.executeScript("return window.pageYOffset;");
				long bottomOffset= ((Number) offset).longValue() -50;
				driver.executeScript("window.scrollTo(0, " + bottomOffset + ");");
				//check if user is already to the top of the page. This happens when there are less records on update page
				offset= driver.executeScript("return window.pageYOffset;");
				String topOffset= offset.toString();
				if(!topOffset.equals("0")){		
					fluentWaitElementVisible(OrientMeUIConstants.goToTop);
					log.info("INFO: Click on 'Go To Top' button");
					clickLink(OrientMeUIConstants.goToTop);
					waitForPageLoaded(driver);
					log.info("INFO: Verify user is brought to the top of the page");
					offset= driver.executeScript("return window.pageYOffset;");
					topOffset= offset.toString();
					Assert.assertEquals(topOffset,"0","ERROR: 'Go To Top' button should brought to the top of the page");
				}
				
				// perform actions again
				tester.get();
			}
		}
	}
	
	/**
	 * Reset the count in the Mentions icon.
	 * If count exists, click the icon and reload page to reset it.
	 */
	public void resetMentionsCount() {
		// check if the count is displayed, if so reload page
		String count = getMentionsCount();
		if (count != null && !count.equals("0") && !count.isEmpty()) {
			log.info("Mentions count = " + count + ", click icon to reset.");
			clickLink(OrientMeUIConstants.mentionsNotificationIcon);
			waitForPageLoaded(driver);
			driver.navigate().refresh();
		} else {
			log.info("Mentions count not present, no need to reset.");
		}
		
		// check count again	
		count = getMentionsCount();
		Assert.assertTrue(count == null || count.equals("0") || count.isEmpty(),
				"Mentions count cannot be reset.");
	}
	
	public String getMentionsCount() {
		Element mentionIconElm = this.getFirstVisibleElement(OrientMeUIConstants.mentionsNotificationIcon);
		Element mentionCountElm = mentionIconElm.getSingleElement("css=span.globalText");
		return mentionCountElm.getText();		
	}
	
	/**
	 * Get Important to Me icon label text
	 * @param icon element
	 * @return label text
	 */
	public String getItmIconLabel(Element iconElem) {
		return iconElem.getSingleElement(OrientMeUIConstants.importantToMeIconLabel).getAttribute("innerText");
	}
	
	public static OrientMeUI getGui(String product, RCLocationExecutor driver) {
		// add class for other offerings if needed
		if (product.toLowerCase().equals("onprem")) {
			return new OrientMeOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
	/**
	 * Wait until the typeahead loading skeleton UI is gone.
	 */
	private void waitForTypeaheadDone()  {
		// Need to temporary turn off implicit wait otherwise WebDriverWait timeout will not be used.
		// https://github.com/SeleniumHQ/selenium/issues/718
		driver.turnOffImplicitWaits();
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 5);
		wait.until(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.ui-people-loading-item"))));
		driver.turnOnImplicitWaits();
	}
    
}
