package com.ibm.conn.auto.webui;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.menu.Homepage_LeftNav_Menu;
import com.ibm.conn.auto.webui.cloud.HomepageUICloud;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.conn.auto.webui.cnx8.HomepageSecNav;
import com.ibm.conn.auto.webui.onprem.HomepageUIOnPrem;
import com.ibm.conn.auto.webui.production.HomepageUIProduction;

public abstract class HomepageUI extends HCBaseUI {

	public HomepageUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(HomepageUI.class);

	public static String newStoryPostedLink(String link) {
		return "css=div[class*='activityStreamNewsItemContainer'] div[class*='lotusPostAction'] a:contains('" + link + "')";
	}

	public static String fileAttachmentDetails(String filename) {
		return "css=span[dojoattachpoint='fileAttachmentDetailsNode'] span:contains('" + filename + "')";
	}
	public static String userNameLink(User userName) {
		return "css=span.vcard a.fn:contains('@" + userName.getDisplayName() + "')";
	}
	public static String URLPreview(String URLTitle) {
		return "//a[contains(text(),'"+URLTitle+"')]//ancestor::div[contains(@id,'urlPreview')]";
	}

	/**
	 * ClickForActionsOption - Homepage my page widget action menu
	 * @param String id - ID of the widget
	 * @param String Action - text in the menu of desired action
	 * @return String - CSS location value of computed web element
	 */
	public static String ClickForActionsOption(String id, String Action){		
		return "css=div[id^='"+ id.substring(0, id.length()-9) +"'] td[id^='dijit_MenuItem_']:contains(" + Action + ")";
	}

	/**
	 * getWidgetActionMenu(String id)
	 * @param id
	 * @return String - CSS location value of computed web element
	 */
	public static String getWidgetActionMenu(String id){		
		return "css=a[id^='" + id.substring(0,id.length() - 9) + "']";
	}
	
	/**
	 * getWidgetMenu
	 * @param id
	 * @return String - CSS location value of computed web element
	 */
	public static String getWidgetMenu(String id){		
		return "css=div[id='" + id + "'] h2[class*='ibmDndDragHandle'] div[class='lotusRight'] a";
	}
	
	/**
	 * getWidgetTitle -
	 * @param id
	 * @return String - CSS location value of computed web element
	 */
	public static String getWidgetTitle(String id){
		return "css=div[id='" + id + "'] h2[class*='ibmDndDragHandle'] div[class='lotusLeft'] a";
	}

	/**
	 * getWidgetClickForActionsSelector -
	 * @param widgetName
	 * @return String - CSS location value of computed web element
	 */
	public static String getWidgetClickForActionsSelector(String widgetName){
		return "css=div[class='lotusWidget homepage-widget dojoDndItem'] h2:contains(" +
				widgetName + ") a:[class='lotusIcon lotusActionMenu']";
	}
	
	/**
	 * getWidgetTitleLinkSelector -
	 * @param widgetTitle
	 * @return String - CSS location value of computed web element
	 */
	public static String getWidgetTitleLinkSelector(String widgetTitle){		
		return "css=h2^=ibmDndDragHandle div.lotusLeft a:contains("+widgetTitle+")";
	}
	
	/**
	 * getWidgetCustomizeCategorySelector -
	 * @param widgetCategoryName
	 * @return String - CSS location value of computed web element
	 */
	public static String getWidgetCustomizeCategorySelector(String widgetCategoryName){		
		return "css=div[id^='lconn_core_paletteOneUI_PaletteList_'] a:contains("+widgetCategoryName+")";
	}
	
	/**
	 * getWidgetCustomizeWidgetSelector -
	 * @param widgetName
	 * @return String - XPath location value of computed web element
	 */
	public static String getWidgetCustomizeWidgetSelector(String widgetName){		
		return "//div[contains(@id,'lconn_core_paletteOneUI_WidgetButton_')]//a[text()='"+widgetName+"']";
	}

	/**
	 * getStoryLink - 
	 * @param title
	 * @return String - CSS location value of computed web element
	 */
	public static String getStoryLink(String title) {
		return "css=div[class='lotusPostContent'] a:contains(" + title + ")";
	}
	
	/**
	 * @param title
	 * @return String - XPath locator of Save this link for stream item
	 */
	public static String getSaveThisLink(String title) {
		return "//a[contains(text(),'"+ title +"')]/../..//li/a";
	}

	public static String getStatusLink(String message){
		return "css=div[class='lotusPostContent']:contains(" + message + ")";
	}
	
	/**
	 * getStatusUpdateContainer - Returns the container element for a Status Update
	 * @param statusUpdate - Text of the Status Update
	 * @return String - xpath of the container of the Status Update
	 */
	public static String getStatusUpdateContainer(String statusUpdate){
		return "xpath=//div[contains(@class,'bidiAware')][contains(text(),'" + statusUpdate + "')]//ancestor::div[@class='lotusPostContent']";
	}

	/**
	 * getStatusUpdateCommentContainer - Returns the container element for the comment on a Status Update
	 * @param statusUpdate - Text of the comment
	 * @return String - xpath of the container of the comment
	 */
	public static String getStatusUpdateCommentContainer(String commentOnStatus){
		return "xpath=//div[@dojoattachpoint='commentContent'][contains(text(),'" + commentOnStatus + "')]//ancestor::ul[@class='lotusCommentList']";
	}

	/**
	 * getActionMenu - 
	 * @param menuName
	 * @param sAction
	 * @return String - CSS location value of computed web element
	 */
	public static String getActionMenu(String menuName, String sAction){
		return "css=table[id='" + menuName + "'] tbody[class='dijitReset'] tr[id^='dijit_MenuItem_'] td:contains(" + sAction + ")";
	}
	
	/**
	 * getUserSelectAtMentionUser -
	 * @param user
	 * @return String - CSS location value of computed web element
	 */
	public static String getUserSelectAtMentionUser(String user){
		return "css=ul[id='mentionsTypeaheadNode_0_PersonMentionsType_popup'] div[id^='mentionsTypeaheadNode_0_PersonMentionsType_popup'] div[role='option']:contains("+user+")";
	}
	
	/**
	 * getNewsItem -
	 * @param elementID
	 * @return String - CSS location value of computed web element
	 */
	public static String getNewsItem(String elementID){
		return "css= li[id='" + elementID + "'] a[title='Show more details about this item']";
	}
	
	/**
	 * getNewsItemForOwnershipTransfer - Return the CSS locator for the news item related to the transfer of ownership of a file 
	 * @param user - User instance for the previous owner of the file
	 * @param file - BaseFile instance for the file that was transferred to a new user
	 * @return String - CSS locator for the news item related to the transfer of ownership of a file
	 */
	public static String getNewsItemForOwnershipTransfer(User user, BaseFile file){
		return "css=div[class*='activityStreamNewsItemContainer'][aria-label^='" + user.getDisplayName() + " transferred ownership of a file to you. " + file.getName() + "']";
	}
	
	/**
	 * getNotificationForOwnershipTransfer - Return the CSS locator for the notification related to the transfer of ownership of a file depending on the type of Notification Center
	 * @param user - User instance for the previous owner of the file
	 * @return String - CSS locator for the notification related to the transfer of ownership of a file depending on the type of Notification Center
	 */
	public String getNotificationForOwnershipTransfer(User user){
		
		driver.changeImplicitWaits(5);
		
		if (isElementVisible(NotificationCenterUI.notificationDownArrow)) {
			driver.turnOnImplicitWaits();
			return "css=div[class='ic-notification-main'][aria-label^='" + user.getDisplayName() + " transferred ownership of a file to you.']";
		}
		
		else {
			driver.turnOnImplicitWaits();
			return "css=div[id*='NotificationsItem'][aria-label^='" + user.getDisplayName() + " transferred ownership of a file to you.']";
		}
	}
	
	/**
	 * getAppLinkInMegaMenu - Return the link for an app in the mega-menu
	 * @param appName - The application for which the link is required
	 * @return String - CSS locator for the app's link in the mega-menu
	 */
	public static String getAppLinkInMegaMenu(String appName){
		return "css=div#lotusBanner a:contains(" + appName + ")";
	}
	
	/**
	 * getLikeCountLink -
	 * @param numLikes
	 * @return String - CSS location value of computed web element
	 */
	public static String getLikeCountLink(int numLikes){
		return "css=a.lotusLikeCount:contains(" + numLikes + ")";
	}
	
	/**
	 * getCommentFileCountLink -
	 * @param numComments
	 * @return
	 */
	public static String getCommentFileCountLink(int numComments){
		return "css=li[id$='_commentsTab'] a[id$='_comments_link']:contains(Comments (" + numComments + ")";
	}
	
	/**
	 * getCommentBlogsCountLink -
	 * @param numComments
	 * @return
	 */
	public static String getCommentBlogsCountLink(int numComments){
		return "css=li[dojoattachpoint='commentsTab'] a:contains(Comments (" + numComments + ")";
	}
	
	/**
	 * getAtMentionMessage -
	 * @param message
	 * @return
	 */
	public static String getAtMentionMessage(String message){		
		return"css=li[id^='com_ibm_social_as_item_RollupStatusNewsItem_']:contains("+ message + ")";
	}
	
	/**
	 * getPostAtMentionUser - 
	 * @param testUser
	 * @return String - CSS location value of computed web element
	 */
	public String getPostAtMentionUser(User testUser){
		return HomepageUIConstants.mentionUserSelector + ":contains("+testUser.getEmail()+")";
	}

	/**
	 * getStatusUpdateMessage
	 * @param message
	 * @return String - CSS location value of the computed web element
	 */
	public static String getStatusUpdateMesage(String message){
		return "css=div.lotusPostContent:contains('"+ message + "')";
	}
	
	/**
	 * getNewsItemBody -
	 * @param elementID
	 * @return
	 */
	public static String getNewsItemBody(String elementID){
		return "css=ul[id='asPermLinkAnchor'] li[id='" + elementID + "'] div[class^='activityStreamNewsItemContainer lotusPost'], ul[id='activityStreamMain'] li[id='" + elementID + "'] div[class^='activityStreamNewsItemContainer lotusPost']";
	}
	
	/**
	 * getNewsItemEEOpener 
	 */
	public static String getNewsItemEEOpener(String elementID){
		return "css=li#" + elementID + " a[data-eeopener=true]";
	}
	
	/**
	 * UpdateOpt -
	 * @author rleblanc
	 *
	 */
	public enum UpdateOpt {		   
		 all("All"),
		 statusUpdates("Status Updates"),
		 activities("Activities"),
		 blogs("Blogs"),
		 bookmarks("Bookmarks"),
		 communities("Communities"),
		 files("Files"),
		 forums("Forums"),
		 libraries("Libraries"),
		 people("People"),
		 profiles("Profiles"),
		 wikis("Wikis"),
		 tags("Tags");
		 		 
		    public String optionStr;
		    private UpdateOpt(String option){
		            this.optionStr = option;
		    }
		    
		    @Override
		    public String toString(){
		            return optionStr;
		    }
		 
	 }

	/** Add a widget from the customization panel */
	public void widgetsCustomize(String widgetCategorySelector, String widgetToAddSelector) {

		//open customisation panel
		fluentWaitElementVisible(HomepageUIConstants.WidgetsCustomize);
		clickLink(HomepageUIConstants.WidgetsCustomize);
		
		fluentWaitPresent(widgetCategorySelector);
		
		waitForPageLoaded(driver);

		//Select component category
		clickLinkWithJavascript(widgetCategorySelector);
		scrollIntoViewElement(widgetToAddSelector);
		fluentWaitPresent(widgetToAddSelector);

		//Add the widget
		clickLink(widgetToAddSelector);
		fluentWaitPresent(HomepageUIConstants.WidgetAdded);

		//close customisation panel
		clickLink(HomepageUIConstants.ClosePalette);
	}
	
	/** Check to see if the widget is present - if it is not here then add it */
	public void addWidgetIfNotPresent(String widgetTitleName, String widgetCategoryName, String widgetToAddName) {

		//check to see if component is present
		boolean componentPresent = driver.isElementPresent(getWidgetTitleLinkSelector(widgetTitleName));
		
		//Add the missing widget
		if (componentPresent == false){
			log.info("Component " + widgetTitleName + " not present.  Adding....");
			widgetsCustomize(getWidgetCustomizeCategorySelector(widgetCategoryName), getWidgetCustomizeWidgetSelector(widgetToAddName));
		}else if (componentPresent == true){
			//Widget is already loaded so no action is needed
			log.info("Component " + widgetTitleName + " is present.  No action needed.");
		}
		//validate component is present
		fluentWaitPresent(getWidgetTitleLinkSelector(widgetTitleName));
	}

	/** Goes to Administration and adds opensocial */
	public void addNewOpenSocialWidget() {
				addNewOpenSocialWidget(Data.getData().NewWidgetTitle);
	}
	
	/** Add the Open Social Widget if it is not present */
	public void addWidgetIfNotPresentOpenSocial(Boolean ComponentNotLoaded, String widgetTitleName, String widgetCategoryName, String widgetToAddName) {

		//Click on the Link to bring to the component and verify
		if (driver.isElementPresent(getWidgetTitleLinkSelector(widgetTitleName))) {
			//Means that the widget has already being added before so do nothing
		}else{
			//Add the missing widget
			if (ComponentNotLoaded == true){
				widgetsCustomize(getWidgetCustomizeCategorySelector(widgetCategoryName), getWidgetCustomizeWidgetSelector(widgetToAddName));
			}else if (ComponentNotLoaded == false){
				//Widget is already loaded so no action is needed
			}
		}
		fluentWaitPresent(getWidgetTitleLinkSelector(widgetTitleName));
	}
	
	/** Verify that the Open Social Widget is displayed */
	public void addNewOpenSocialWidget(String widgetTitle) {

		//Select add another widget button
		clickLinkWait(HomepageUIConstants.AddAnotherWidget);

		//Fill out and save new widget form
		clickLinkWait(HomepageUIConstants.OpenSocialRadioButton);
		driver.getSingleElement(HomepageUIConstants.WidgetTitleText).type(widgetTitle);
		driver.getSingleElement(HomepageUIConstants.WidgetUrlText).type(Data.getData().newWidgetUrl);
		clickLinkWait(HomepageUIConstants.DisplayOnWidgetsPage);
		clickLinkWait(HomepageUIConstants.DisplayOnUpdatePage);
		clickLinkWait(HomepageUIConstants.Save);
		
		//sleep for a minute to allow object creation
		fluentWaitTextPresent(widgetTitle);
		
		Assert.assertTrue(dropdownContains(HomepageUIConstants.DisabledWidgetsList, widgetTitle), widgetTitle + " Open Social Widget was not added to the disabled list");
	}
	
	/** Enable the widget */
	public void enableWidget(String widgetTitle) {
		
		driver.getSingleElement(HomepageUIConstants.DisabledWidgetsList).useAsDropdown().selectOptionByVisibleText(widgetTitle);
		driver.getSingleElement(HomepageUIConstants.Enable).click();
		
		Assert.assertTrue(dropdownContains(HomepageUIConstants.EnabledWidgetsList, widgetTitle), "Widget: '" + widgetTitle + "' was not added to the enabled list");
	}
	
	public void checkOpenSocialContainer() {
		driver.switchToFrame().selectSingleFrameBySelector(HomepageUIConstants.IFrame);
		Assert.assertTrue(driver.isTextPresent(Data.getData().OpenSocialTitle), "Open Social content missing");
		Assert.assertTrue(driver.isTextPresent(Data.getData().OpenSocialContent), "Open Social content missing");
		driver.switchToFrame().returnToTopFrame();
	}

	/** Perform a Status Update  */
	public void statusUpdate(String statusMessage) {

		//Enter and Save a status update
		if(driver.isElementPresent(HomepageUIConstants.EnterMentionsStatusUpdate)){
			driver.getSingleElement(HomepageUIConstants.EnterMentionsStatusUpdate).type(statusMessage);
		}else{
			enterStatus(statusMessage);
		}
		clickLinkWait(HomepageUIConstants.PostStatusOld);
		log.info("INFO: Status update was successful");
	}
	/** Perform a Status Update  */
	public void statusUpdateCKEditor(String statusMessage) {

		log.info("INFO: Switching to Status Update frame");
		Element frame = driver.getSingleElement(HomepageUIConstants.StatusUpdateFrame);
		driver.switchToFrame().selectFrameByElement(frame);
		
		log.info("INFO: Find the status update input field");
		Element inputField = driver.getSingleElement(HomepageUIConstants.StatusUpdateTextField);
        
		log.info("INFO: Click into status input field");
		inputField.click();

		log.info("INFO: Enter Status update");
		inputField.type(statusMessage);

		//switch back to default frame
		log.info("INFO: Switching back to main frame");
		driver.switchToFrame().returnToTopFrame();

		log.info("INFO: Select the post status link");
        clickLinkWithJavascript(HomepageUIConstants.StatusUpdatePost);
		
	}
	
	/**
	 * Posts a status update in the Homepage status update input box. Please note that this UI
	 * method uses the slower "typeWithDelay" method to enter the status update contents
	 * 
	 * @param statusMessage - The status update message to be posted
	 */
	public void postHomepageUpdate(String statusMessage) {

		//Enter and Save a status update
		if(driver.isElementPresent(HomepageUIConstants.SU_TextArea)){
			driver.getSingleElement(HomepageUIConstants.SU_TextArea).type(statusMessage);
		}
		else{
			Element statusUpdate = getStatusUpdateElement();
			
			statusUpdate.click();
			statusUpdate.typeWithDelay(statusMessage);
			
			log.info("INFO: Returning to top Frame to click 'Post' button");
			driver.switchToFrame().returnToTopFrame();
		}
		
		clickLinkWait(HomepageUIConstants.PostComment);
		log.info("INFO: Status update was successful");
	}
	
	/**
	 * Compatible with CK Editor - retrieves the status update body element from the status update iframe
	 * 
	 * @return - The status update body element
	 */
	public Element getStatusUpdateElement() {
		
		log.info("INFO: Retrieving all status update iframe elements");
		switchToCKEditorSUFrame(1);
		
		log.info("INFO: Ensure that the cursor is moved to the end of any text entered into the status update frame");
		driver.switchToActiveElement().type(Keys.END);
		
		return driver.switchToActiveElement();
	}
	
	/**
	 * Enters / types a status update in the Homepage status update input box. Please note that this UI
	 * method uses the standard / faster "type" method to enter the status update contents
	 * 
	 * @param statusMessage - The status update message to be entered
	 */
	public void typeHomepageUpdateWithoutDelay(String statusMessage) {
		
		Element statusUpdateBody = getStatusUpdateElement();
		
		// Click into the status update input field and enter the status message to be posted
		statusUpdateBody.click();
		statusUpdateBody.type(statusMessage);
		
		log.info("INFO: Verify that the status update is displayed in the status update input field");
		Assert.assertTrue(driver.isTextPresent(statusMessage), 
							"ERROR: The status update was displayed in the status update input field");
	}
	
	/**
	 * Posts a status update in the Homepage status update input box. Please note that this UI
	 * method uses the standard / faster "type" method to enter the status update contents
	 * 
	 * @param statusMessage - The status update message to be posted
	 */
	public void postHomepageUpdateWithoutDelay(String statusMessage) {
		
		// Type the status update into the status update input field
		typeHomepageUpdateWithoutDelay(statusMessage);
		
		// Post the status update
		postStatusUpdate(false);
	}

	/** Filter the Discover view by the component */
	public void filterBy(String component) {
		if (cfg.getUseNewUI()) {
			log.info("Click on Filter Button");
			waitForClickableElementWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton), 5);
			clickLinkWithJavaScriptWd(findElement(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton)));
			clickLinkWaitWd(By.xpath(getDropDownSelectionLocator(component)), 7);
		} else {
			waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.FilterBy), 5);
			driver.getSingleElement(HomepageUIConstants.FilterBy).useAsDropdown().selectOptionByVisibleText(component);
			log.info("INFO: filtered the AS by the component: " + component);
			waitForPageLoaded(driver);
			
		}
	}
	
	/**
	 * Returns locator for dropdown selection based on component provided
	 * @param String component
	 */
	public String getDropDownSelectionLocator(String component) {
		return HomepageUIConstants.discoverLatestUpdateFilterValue.replace("PLACEHOLDER", component);
	}
	
	/**
	 * Activities add options
	 * 
	 * @param chosenOption
	 */
	public void selectAddOption(String chosenOption) {
		waitForPageLoaded(driver);
		clickLink(BaseUIConstants.AddButton);
		clickLink(BaseUIConstants.menuOption+":contains("+chosenOption+")");
		log.info("INFO: option chosen from the Add dropdown is: "+chosenOption);
	}

	public void verifyEE(BaseBlogPost blogPost){

		//Verify the EE widget
		log.info("INFO: Verify the EE Title");
		fluentWaitTextPresent(blogPost.getTitle());
		
		//Verify tag
		log.info("INFO: Verify the EE Tags");
		fluentWaitTextPresent(blogPost.getTags());

		//Verify like
		log.info("INFO: Verify the EE Like");
		fluentWaitPresent(HomepageUIConstants.EELike);
		
		//Verify Description
		log.info("INFO: Verify the EE Description");
		fluentWaitTextPresent(blogPost.getContent());
		
		//Verify Read more
		log.info("INFO: Verify the EE Read More link");
		fluentWaitPresent(HomepageUIConstants.EEReadmore);
		
		//Verify the Tab for Comments
		log.info("INFO: Verify the EE Comment Tab");
		fluentWaitPresent(HomepageUIConstants.EECommentsTab);
		
		//Verify the Tab for Recent Updates
		log.info("INFO: Verify the EE Recent Updates Tab");
		fluentWaitPresent(HomepageUIConstants.EEHistoryTab);
	}
	
	public void verifyEE(BaseFile file, String FollowOption){
		//Verify the EE widget
		fluentWaitTextPresent(file.getName());
		//Verify like, Preview, Download
		fluentWaitPresent(HomepageUIConstants.EELike);
		fluentWaitPresent(HomepageUIConstants.Preview);
		fluentWaitPresent(HomepageUIConstants.Download);
		if(FollowOption.matches("Follow")){
			fluentWaitPresent(HomepageUIConstants.FollowFileButton);
		}else if(FollowOption.matches("Stop Following")){
			fluentWaitPresent(HomepageUIConstants.StopFollowFileButton);
		}
		//Verify the Tabs Recent updates and Comments
		fluentWaitPresent(HomepageUIConstants.EEFilesCommentsTab);
		fluentWaitPresent(HomepageUIConstants.EEFilesHistoryTab);
	}
	
	/**
	 * This method will go to the "Comments" tab, or "Replies" tab for a forum event
	 * in the open EE and post a comment. NOTE must use the following method to post comment in
	 * the file overlay - com.ibm.conn.auto.webui.FilesUI.addFileOverlayComment(String)
	 * 
	 * @param EETestComment - The comment to be posted from the EE
	 */
	public void addEEComment(String EETestComment){
		log.info("INFO: Adding comment ");
		
		log.info("INFO: Checking that 'Comments' tab is present ");
		if(driver.isElementPresent(HomepageUIConstants.EECommentsTab)){
			clickLink(HomepageUIConstants.EECommentsTab);
		}
		else{
			log.info("INFO: Going to 'Replies' tab ");
			clickLinkWait(HomepageUIConstants.EERepliesTab);
		}
		
		log.info("INFO: Switching to comments frame");
		Element commentframe = driver.getSingleElement(HomepageUIConstants.StatusUpdateFrame);
		driver.switchToFrame().selectFrameByElement(commentframe);
		
		log.info("INFO: Enter comment into field");
		fluentWaitElementVisible(HomepageUIConstants.StatusUpdateTextField);
		Element inputField = driver.getSingleElement(HomepageUIConstants.StatusUpdateTextField);
		inputField.click();
		inputField.type(EETestComment);
		
		// Switch the focus back to the EE frame
		switchToEEFrame();
		
        log.info("INFO: Select post");
        scrollIntoViewElement(HomepageUIConstants.OpenEEPostCommentButton);
        clickLinkWait(HomepageUIConstants.OpenEEPostCommentButton);
	}
	
	/**
	 * Handles switching to either the comments frame in the EE or the replies frame in the EE
	 * 
	 * @param switchToCommentFrame - True if the user wishes to switch to the comment frame in the EE, false if they want to switch to the replies tab
	 * @return - True if the chosen frame was correctly switched to, false if the operation has failed for any reason
	 */
	public boolean switchToEECommentOrRepliesFrame(boolean switchToCommentFrame) {
		
		String commentOrRepliesTab = "";
		String tabCSSSelector = "";
		if(switchToCommentFrame) {
			commentOrRepliesTab = "'Comments'";
			tabCSSSelector = HomepageUIConstants.EECommentsTab;
		} else {
			commentOrRepliesTab = "'Replies'";
			tabCSSSelector = HomepageUIConstants.EERepliesTab;
		}
		
		log.info("INFO: Searching for the " + commentOrRepliesTab + " tab in the EE");
		boolean tabIsPresent = driver.isElementPresent(tabCSSSelector);
		
		if(tabIsPresent) {
			log.info("INFO: Now clicking on the " + commentOrRepliesTab + " tab in the EE");
			clickLink(tabCSSSelector);
		} else {
			log.info("ERROR: Could not find the " + commentOrRepliesTab + " tab in the EE");
			return false;
		}
	
		// Retrieve all visible frames for the comment input field - sometimes, if using replies, multiple frames are picked up by Selenium
		List<Element> listOfFrames = driver.getElements(HomepageUIConstants.StatusUpdateFrame);
		
		Element visibleCommentFrame = null;
		if(listOfFrames.size() == 0) {
			log.info("ERROR: Could not find any comment / reply input frames after clicking on the " + commentOrRepliesTab + " tab");
			return false;
			
		} else if(listOfFrames.size() == 1) {
			// Just the one frame has been found - set this as the frame to be switched to in order to post the comment / reply
			visibleCommentFrame = listOfFrames.get(0);
			
		} else {
			// Multiple frames have been found - only one of these will be set as visible and needs to be set as the frame to switch to
			int index = 0;
			boolean foundVisibleFrame = false;
			while(index < listOfFrames.size() && foundVisibleFrame == false) {
				Element currentFrame = listOfFrames.get(index);
				
				if(currentFrame.isDisplayed()) {
					log.info("INFO: A visible comment / reply input frame has been found with ID: " + currentFrame.getAttribute("id"));
					visibleCommentFrame = currentFrame;
					foundVisibleFrame = true;
				}
				index ++;
			}
			
			if(!foundVisibleFrame) {
				log.info("ERROR: Could not find any visible comment / reply input frames after clicking on the " + commentOrRepliesTab + " tab");
				return false;
			}
		}
		log.info("INFO: Found comment / reply input frame after clicking on the " + commentOrRepliesTab + " tab");
		
		log.info("INFO: Switching to the comment / reply input frame");
		driver.switchToFrame().selectFrameByElement(visibleCommentFrame);
		
		log.info("INFO: Waiting for the comment / reply input field to be visible");
		fluentWaitElementVisible(HomepageUIConstants.StatusUpdateTextField);
		
		/**
		 * In some browsers (currently FF and Chrome), when the user clicks into the EE the cursor defaults to the leftmost position in the EE, even when
		 * text is present. Because of this, if the EE contains links (such as URLs or mentions links), Selenium often clicks the links in the EE instead of
		 * placing the cursor in that leftmost position. Clicking into the EE using Selenium therefore requires a very precise "click" location in order to
		 * activate the cursor at the beginning of the comments / reply input field without affecting any other text in the EE.
		 * 
		 * Therefore we are setting this method to click 1 pixel inside of the box horizontally and 3/4 down in height vertically.
		 * This ensures that for any comments / multiple mentions which span 2 lines - the cursor will be moved to the end of the 2nd line in the next action.
		 */
		log.info("INFO: Now clicking into the comment / reply input field");
		Element commentInputFrame = driver.getFirstElement(HomepageUIConstants.StatusUpdateTextField);
		commentInputFrame.clickAt(commentInputFrame.getLocation().x + 1, commentInputFrame.getLocation().y + (int)(3 * (commentInputFrame.getSize().height / 4)));
		
		/**
		 * In order to move the cursor to the end of any text present in the EE reliably and consistently, it is important
		 * to press the END key. The END key must be pressed to ensure that the cursor is pushed to the end of the current line.
		 * 
		 * Also, trying to move the cursor in any other way can trigger certain elements (such as mentions links) to be highlighted an
		 * the cursor to then disappear entirely.
		 */
		log.info("INFO: Now moving the cursor to the end of any text within the comment / reply input field by pressing the END key");
		driver.switchToActiveElement().type(Keys.END);
		
		return true;
	}
	
	/**
	 * Switches focus to an existing EE frame
	 */
	public void switchToEEFrame() {
		
		// Switch focus back to the main frame
		switchToTopFrame();
		
		log.info("INFO: Now switching focus to the EE frame");
		List<Element> eeFrames = driver.getVisibleElements(HomepageUIConstants.GenericEEFrame);
		
		for(Element frame : eeFrames){      										
			log.info("INFO: Now switching to the EE frame with ID: " + frame.getAttribute("id"));
			driver.switchToFrame().selectFrameByElement(frame);		
		}
	}
	
	public void recentUpdatesInEE(String Componentname, String EETestComment, String EETestLike){
		//Click on the comment tab in EE
		if (Componentname.matches("Files")){
			clickLink(HomepageUIConstants.EEFilesHistoryTab);
		}else if (Componentname.matches("Blogs")){
			clickLink(HomepageUIConstants.EEHistoryTab);
		}
		fluentWaitTextPresent(EETestComment);
		fluentWaitTextPresent(EETestLike);
	}
	
	public void verifyReadMore(String CommunityName, String Homepage){
		clickLink(HomepageUIConstants.EEReadmore);
		switchToNewTabByName(CommunityName);
		Assert.assertTrue(driver.isTextPresent(""));
		this.close(cfg);
		switchToNewTabByName(Homepage);
	}
	
	
	/**
	 * clickLikeCountEE -
	 * @param numLikes
	 */
	public void clickLikeCountEE(int numLikes){

		log.info("INFO: Select like count " + numLikes);
		clickLinkWait(getLikeCountLink(numLikes));   
	}

	/**
	 * AddWidgetIfNotPresent() check if widget is present if not add it.  Added Second chance.
	 * @param widgetCategory - name of menu item category used to select for widget if needed
	 * @param widgetName - name of widget to check
	 * @throws Exception
	 * 
	 * @author Ralph LeBlanc
	 */
	public Element addWidgetIfNotPresent(String widgetCategory, String widgetName){
		
		//Wait for document.readystate to be complete
		waitForPageLoaded(driver);

		//Gather enabled widgets and look for Widget under test
		log.info("INFO: Gathering enabled widgets");
		Element widgetUnderTest = findWidget(widgetName);
		
		//Add the widget if not present
		if(widgetUnderTest==null){
			//Gather a second time the enabled widgets and look for Widget under test
			//log.info("INFO: Checking a second time for " + widgetName + " widget");
			//widgetUnderTest = findWidget(widgetName);
			//If still unable to find widget under test in list of enabled widgets attempt to enable it
			if(widgetUnderTest==null){
				log.info("INFO: " + widgetName + " widget does not appear to be enabled");
				log.info("INFO: Attempting to enable the " + widgetName + " widget");
				widgetsCustomize(getWidgetCustomizeCategorySelector(widgetCategory),
								getWidgetCustomizeWidgetSelector(widgetName));			
				widgetUnderTest = findWidget(widgetName);
			}
		}
		
		return widgetUnderTest;
	}
	
	/**
	 * getHomepageWidgets()
	 * @return
	 */
	public List<Element> getHomepageWidgets(){

		return driver.getElements(HomepageUIConstants.genericWidgetId);
	}
	
	/**
	 * findWidgetId(String widgetLookFor)
	 * @param widgetLookFor
	 * @return A locator string for the widget if the widget was found on the
	 * page, and null if it was not. Previously the last widget on the page
	 * was returned if the widget was not found, but I feel that an eventual
	 * NullPointerException is better than returning erroneous data.
	 */

	public String findWidgetId(String widgetLookFor){		

		String id = "";
		List<Element> visibleWidgets = getHomepageWidgets();
		
		Iterator<Element> visWidget = visibleWidgets.iterator();
		while(visWidget.hasNext())
			{
		    Element widget = visWidget.next();
		    id = widget.getAttribute("id");
		    String innerText = driver.getSingleElement(getWidgetTitle(id)).getText();
		    if(innerText.contentEquals(widgetLookFor))
		    	{
		    	log.info("INFO: " + driver.getSingleElement(getWidgetTitle(id)).getText());
		    	log.info("INFO: ID " + id);
		    	return id;
				}
		
			}
		
		// if control reaches here we have iterated through all elements and found nothing
		return null;
	}
	
	
	/**
	 * findWidget
	 * @param widgetList String
	 * @return Element you are looking for or null
	 * <p>
	 * @author Ralph LeBlanc
	 */
	public Element findWidget(String widgetLookFor){	
		
		//collect all enabled widgets and compare them to base state count of default widgets	
		log.info("INFO: Looking for widget " + widgetLookFor);
		List<Element> visibleWidgets = collectEnabledWidget();
		
		//check to see if the widget is in list
		Element widget=null;		
		Iterator<Element> widgetList = visibleWidgets.iterator();
		while(widgetList.hasNext()){			
		    widget = widgetList.next();
		    if (widget.getText().equals(widgetLookFor)){
			    log.info("INFO: Widget " + widget.getText() + " is found and loaded");
	    		break;
		    	}
		    widget=null;
			}
		
		return widget;
	}
	
	/**
	 * Method that will collect visible Widget Visible elements and validate that match expected
	 * @param RCLocationExecutor driver - web driver to use.
	 * @param int expectWid - expected amount of widgets
	 * @return List<Element> - returns list of web elements to use.
	 * <p>
	 * @author Ralph LeBlanc
	 */
	public List<Element> collectEnabledWidget(){
		
		//Wait for document.readystate to be complete
		waitForPageLoaded(driver);
		
		//collect the visible widget web elements  genericWidgetId
		List<Element> visibleWidgets = driver.getVisibleElements(HomepageUIConstants.getWidgetActionsSelectors);
		log.info("INFO: Enabled Widgets " + visibleWidgets.size());
		
		//Log Each Widget visible for debug purposes
		Iterator<Element> visWidget = visibleWidgets.iterator();
		while(visWidget.hasNext())
			{
		    Element widget = visWidget.next();
		    log.info("INFO: Widget " + widget.getText() + " is enabled");
			}

		return visibleWidgets;		
	}
	
	/**
	 * preformActionHomePageWidgets(): This method will execute the action specified from 
	 * the widget action menu to the list of widgets
	 * <p>
	 * @param eWidget List of Elements - List of Widget elements to preform action on 
	 * @param sAction String - name of the action from the pull down menu.
	 * @author Ralph LeBlanc
	 */
	public void preformActionHomePageWidget(Element enabledWidget, String sAction) {
	
		String menuName;
		String ClickForAction; 
		String ClickWidgetActionIcon;

		//Find the widget action selector box
		ClickWidgetActionIcon = getWidgetClickForActionsSelector(enabledWidget.getText());						
		log.info("INFO: Widget: " + enabledWidget.getText() + " Action: " + sAction);

		//Selecting the widget action menu
		log.info("INFO: Selecting the widget action menu");		
		Element eWidgetActionMenu = driver.getSingleElement(ClickWidgetActionIcon);		
		eWidgetActionMenu.click();	
					
		if (driver.isElementPresent(HomepageUIConstants.activeDropdown)){
			//Find table id associated with popup action menu
			log.info("INFO: selecting action");
			menuName = driver.getSingleElement(HomepageUIConstants.activeDropdown).getAttribute("id");
		}else {
			//Selecting the widget action menu
			log.info("WARNING: Action Menu does not appear to be up");
			log.info("INFO: Selecting the widget action menu for a second time");
			eWidgetActionMenu.click();
			//Find table id associated with popup action menu
			log.info("INFO: selecting action");
			menuName = driver.getSingleElement(HomepageUIConstants.activeDropdown).getAttribute("id");
		}		
		
		//Selecting action
		ClickForAction = getActionMenu(menuName, sAction);  
		try{
			log.info("INFO: Attempting to select action " + sAction);
			driver.getSingleElement(ClickForAction).click();
		}catch(Exception clickAction){
			log.info("INFO: Attempting a second time to select the Menu for " + enabledWidget.getText());
			driver.getSingleElement(ClickWidgetActionIcon).click();
			driver.getSingleElement(ClickForAction).click();	
		}
		
		log.info("INFO: Action complete");
		
	}
	
	public void gotoProfile() {
		String url = driver.getFirstElement(HomepageUIConstants.profilePhoto).getAttribute("href");
		driver.navigate().to(url);
	}
	
	public void gotoHome() {
		clickLink(HomepageUIConstants.HomeIcon);
	}
	
	/**
	 * Selects 'Updates' from the left navigation menu in Homepage
	 */
	public void gotoUpdates() {
		if(cfg.getUseNewUI()) {
			log.info("INFO:click on Latest Updates menu from  Homepage secondary Nav'");
			HomepageSecNav secNavUI= new HomepageSecNav(driver);
			secNavUI.clickSecNavItem(secNavUI.updates);

		}else {
			log.info("INFO: Now selecting the 'Updates' tab in the left nav menu");
			Homepage_LeftNav_Menu.UPDATES.select(this);
		}
	}
	
	/**
	 * Validates 'Updates' tab menu in Homepage
	 */
	public void gotoUpdatesTabsValidation() {
		if(cfg.getUseNewUI()) {
			log.info("INFO:click on Latest Updates menu from  Homepage secondary Nav'");
			HomepageSecNav secNavUI= new HomepageSecNav(driver);
			secNavUI.clickSecNavItem(secNavUI.updates);

		}else {
			
			waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.scUpdates),5);
			log.info("INFO: Now selecting the 'Updates' tab in the left nav menu");
			Homepage_LeftNav_Menu.UPDATES.select(this);
			waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.ImFollowingTab),5);
			clickLinkWaitWd(createByFromSizzle(HomepageUIConstants.ImFollowingTab),3,"Click on ImFollowing Tab");
			waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.StatusUpdatesTab),5);
			clickLinkWaitWd(createByFromSizzle(HomepageUIConstants.StatusUpdatesTab),3,"Click on StatusUpdates Tab");
			waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.DiscoverTab),5);
			clickLinkWaitWd(createByFromSizzle(HomepageUIConstants.DiscoverTab),3,"Click on Discover Tab");
		}
	}
	
	/**
	 * A JS script to close the Guided Tour dialog. This dialog can appear sometimes and obscure other elements
	 * 
	 * By making the call to close the dialog in JS - the action will be carried out correctly if the dialog is present in the UI
	 * and will be skipped entirely (ie. Selenium will NOT register it as a failure) if the dialog is NOT present in the UI
	 * 
	 * There is very little additional overhead as regards execution times for tests using this method as a result of using JS
	 */
	public void closeGuidedTourDialog() {
		log.info("INFO: Execute JS to ensure that the Guided Tour dialog box is removed and thus cannot obscure any other elements");
		JavascriptExecutor jse = (JavascriptExecutor) driver.getBackingObject();
		try{
			Object isGuidedTourDialog=jse.executeScript("return document.getElementById('close-tooltip')");
			if(isGuidedTourDialog!=null){
				jse.executeScript("document.getElementById('close-tooltip').click()");
			}
		}catch(Exception e){
			log.info("INFO:Guided Tour is not visible:"+e);
		}
	}
	
	/**
	 * Waits for the UI to load by ensuring that the following text is displayed: "Feed for these entries"
	 */
	private void waitForUIToLoad_FeedForTheseEntries() {
		log.info("INFO: Waiting for the text with content '" + Data.getData().feedsForTheseEntries + "' to be displayed in the UI");
		fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
	}
	
	/**
	 * Navigates to the Discover view in the UI
	 */
	public void gotoDiscover() {
		if(cfg.getUseNewUI()) {
			log.info("INFO:click on Discover menu from  Homepage secondary Nav'");
			HomepageSecNav secNavUI= new HomepageSecNav(driver);
			secNavUI.clickSecNavItem(secNavUI.discover);
		}else {
			log.info("INFO: Clicking on 'Updates', then 'Discover'");
			gotoUpdates();
			closeGuidedTourDialog();
			clickLinkWait(HomepageUIConstants.DiscoverTab);
			waitForUIToLoad_FeedForTheseEntries();

		}
	}
	
	/**
	 * Select Help menu
	 */
	public void gotoHelp() {
		if (cfg.getUseNewUI()) {
			log.info("INFO: Click on the 'Help' menu from side navigation");
			AppNavCnx8.HELP.select(this);
		} else {
			log.info("INFO: Click on the help link on homepage");
			findElement(By.cssSelector(HomepageUIConstants.HelpHeaderLink)).click();
		}
	}
	
	/**
	 * Navigates to the My Page view in the UI
	 */
	public void gotoMyPage() {
		if(cfg.getUseNewUI()) {
			log.info("INFO: click on My page tab from  Homepage top Nav");
			clickLinkWaitWd(By.cssSelector(HomepageUIConstants.MyPageCnx8), 10, "click on My page tab from  Homepage top Nav");
		}else {
			log.info("INFO: clicking on My Page tab");
			clickLinkWaitWd(createByFromSizzle(HomepageUIConstants.MyPage), 5);

		}
	}
	
	/**
	 * Navigates to the Administration view in the UI
	 */
	public void gotoAdministration() {
		if(cfg.getUseNewUI()) {
			log.info("INFO: click on Administration tab from  Homepage top Nav");
			clickLinkWaitWd(By.cssSelector(HomepageUIConstants.AdministrationCnx8), 10, "click on Administration tab from  Homepage top Nav");
		}else {
			log.info("INFO: clicking on Administration tab");
			clickLink(HomepageUIConstants.Administration);

		}
	}

	/**
	 * Navigates to the I'm Following view in the UI
	 */
	public void gotoImFollowing() {
		gotoUpdates();
		closeGuidedTourDialog();	
		waitForUIToLoad_FeedForTheseEntries();
	}
	
	/**
	 * Navigates to the Status Updates view in the UI
	 */
	public void gotoStatusUpdates() {
		
		if(cfg.getUseNewUI()) {
			log.info("INFO:click on Latest Updates menu from  Homepage secondary Nav'");
			HomepageSecNav secNavUI= new HomepageSecNav(driver);
			secNavUI.clickSecNavItem(secNavUI.updates);
		}else {
			gotoUpdates();
			closeGuidedTourDialog();
			clickLinkWait(HomepageUIConstants.StatusUpdatesTab);
			waitForUIToLoad_FeedForTheseEntries();
		}
		
	}

	/**
	 * Navigates to an app using the top navigation bar of CNX7 or the left navigation panel of CNX8
	 */
	public void gotoMegaMenuApps(String appName){
		
        if(cfg.getUseNewUI()) {
        	
        	CommonUICnx8 commonUI = new CommonUICnx8(driver);

			switch(appName) {

			case "Activities":
				AppNavCnx8.ACTIVITIES.select(commonUI);
				break;
			case "Blogs":
				AppNavCnx8.BLOGS.select(commonUI);
				break;
			case "Bookmarks":
				AppNavCnx8.BOOKMARKS.select(commonUI);
				break;
			case "Communities":
				AppNavCnx8.COMMUNITIES.select(commonUI);
				break;
			case "Files":
				AppNavCnx8.FILES.select(commonUI);
				break;
			case "Forums":
				AppNavCnx8.FORUMS.select(commonUI);
				break;
			case "Wikis":
				AppNavCnx8.WIKIS.select(commonUI);
				break;

			}

		}

		else {

			if (appName.equals("Communities")) {

				clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
				clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);
				clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView);
				clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuOwner);

			}

			else {

				clickLinkWait(getMegaMenuApps());
				clickLinkWithJavascript("css=a>strong:contains(" + appName + ")");

			}

		}
        
	}

	/**
	 * Navigates to the My Notifications view in the UI
	 */
	public void gotoMyNotifications(){
        if(cfg.getUseNewUI()) {
			
			gotoUpdates();
			clickLinkWaitWd(By.xpath(HomepageUIConstants.personalFilterBtn),3,"Click on Personal Filter Button");
			clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesMyNotificationsFilter),3,"Click on My Notification Filter Option from dropdown");
			waitForElementVisibleWd(By.xpath(HomepageUIConstants.latestUpdatesMyNotificationsFilter),3);
			
		}

		else {
			
			waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.HomepageMyNotifications),5);
			Homepage_LeftNav_Menu.MYNOTIFICATIONS.select(this);
			closeGuidedTourDialog();
			waitForUIToLoad_FeedForTheseEntries();	

		}
	}

	/**
	 * Navigates to the Mentions view in the UI
	 */
	public void gotoMentions(){
		if(cfg.getUseNewUI()) {
			gotoUpdates();
			clickLinkWaitWd(By.xpath(HomepageUIConstants.personalFilterBtn),3,"Click on Personal Filter Button");
			clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesCategoriesMentionsFilter),3,"Click on Mentions Filter Option from dropdown");
			waitForElementVisibleWd(By.xpath(HomepageUIConstants.latestUpdatesCategoriesMentionsFilter),3);		
		}
		else {
			
			waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.AtMentions),5);
			Homepage_LeftNav_Menu.MENTIONS.select(this);
			closeGuidedTourDialog();
			waitForUIToLoad_FeedForTheseEntries();
			waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.AtMentionsTab),5);
			clickLinkWaitWd(createByFromSizzle(HomepageUIConstants.AtMentionsTab),3,"Click on Mention Tab");

		}
	}

	/**
	 * Navigates to the Saved view in the UI
	 */
	public void gotoSaved(){
		if(cfg.getUseNewUI()) {
			gotoUpdates();
			clickLinkWaitWd(By.xpath(HomepageUIConstants.personalFilterBtn),3,"Click on Personal Filter Button");
			clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesCategoriesSavedFilter),3,"Click on Saved Filter Option from dropdown");
			waitForElementVisibleWd(By.xpath(HomepageUIConstants.latestUpdatesCategoriesSavedFilter),3);
		}
		else {
			
		waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.scSaved),5);
		Homepage_LeftNav_Menu.SAVED.select(this);
		closeGuidedTourDialog();
		waitForUIToLoad_FeedForTheseEntries();
		waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.SavedTab),5);
		clickLinkWaitWd(createByFromSizzle(HomepageUIConstants.SavedTab),3,"Click on Saved Tab");
		}
	}
	
	/**
	 * Navigates to the Getting Started view in the UI
	 */
	public void gotoGettingStarted(){
		Homepage_LeftNav_Menu.GETTINGSTARTED.select(this);
		fluentWaitTextPresent(Data.getData().GettingStartedGreeting);
	}
	
	/**
	 * Navigates to the Action Required view in the UI
	 */
	public void gotoActionRequired() {
		
        if(cfg.getUseNewUI()) {
			
			gotoUpdates();
			clickLinkWaitWd(By.xpath(HomepageUIConstants.personalFilterBtn),3,"Click on Personal Filter Button");
			waitForElementVisibleWd(By.xpath(HomepageUIConstants.latestUpdatesActionRequiredFilter),3);
			clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesActionRequiredFilter),3,"Click on Action Required Filter Option from dropdown");
			
		}

		else {

			clickLink(HomepageUIConstants.ActionRequired);
			closeGuidedTourDialog();
			fluentWaitPresent(HomepageUIConstants.ActionReqView);
			waitForUIToLoad_FeedForTheseEntries();
		
		}
        
	}
	
	public void hoverOverNewsStory(BaseBlog blog) {
		String storyLink = getStoryLink(blog.getName());
		String originalWindow = driver.getWindowHandle();
		this.getFirstVisibleElement(storyLink).hover();
		clickLink(storyLink);
		//clickLink(blogLink);
		driver.switchToFirstMatchingWindowByPageTitle(blog.getName());
		this.close(cfg);
		driver.switchToWindowByHandle(originalWindow);
		fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
	}

	public void saveNewsStory(String newsItem){
		
		log.info("INFO: Save news story");
		clickLinkWait("css=li#" + findNewsItem(newsItem).getAttribute("id") +
				   " div.activityStreamNewsItemContainer div.lotusPostContent div.lotusMeta ul.lotusInlinelist li[class*='lotusFirst savethisAction'] a");
	}
	
	/**
	 * Save any story using the UI (including saving stories that require the user to click "More" and then "Save This")
	 * 
	 * @param newsItem - The text/content of the news story item which is to be saved
	 * @return - True if the 'Save This' link has been clicked successfully, false otherwise
	 */
	public boolean saveNewsStoryUsingUI(String newsItem) {
		
		// Verify / establish which 'Save This' link is displayed for the news story to be saved
		String saveThisCSSSelector = verifyNewsStorySaveLinkIsDisplayedUsingUI(newsItem);
		
		if(saveThisCSSSelector == null) {
			log.info("ERROR: Could not click the 'Save This' link for the news story with content: " + newsItem);
			return false;
		}
		
		log.info("INFO: Saving the news story with content: " + newsItem);
		clickLinkWait(saveThisCSSSelector);
		
		log.info("INFO: Verify that the 'Save This' link for the news story is now unclickable (ie. the link has now changed to a 'Saved' link)");
		Assert.assertTrue(fluentWaitPresent(HomepageUIConstants.SavedLinkInactive.replaceAll("PLACEHOLDER", newsItem)),
				"ERROR: The 'Save This' link has not changed to a 'Saved' link as expected after the news story was marked as saved using the UI");
		
		return true;
	}
	
	/**
	 * Verifies that the 'Save This' link for the specified news story item is displayed
	 * 
	 * @param newsItem - The String content of the news item whose 'Save This' link is to be verified as displayed
	 * @return - The CSS selector of the visible 'Save This' link found
	 */
	public String verifyNewsStorySaveLinkIsDisplayedUsingUI(String newsItem) {
		
		log.info("INFO: Verifying that the 'Save This' link for the news story is displayed");
		
		// Reset the view to the top of the news feed
		resetASToTop();
		
		String newsStoryId = getNewsStoryId(newsItem);
		log.info("INFO: News Story ID has been found: " + newsStoryId);
		
		log.info("INFO: Bringing the news story item into view");
		String entireStoryCss = HomepageUIConstants.NewsStoryElementById.replace("PLACEHOLDER", newsStoryId);
		bringNewsStoryIntoView(driver.getFirstElement(entireStoryCss));
		
		// Set up the remaining CSS selector links
		String saveThisCss = HomepageUIConstants.NewsStorySaveThisLinkById.replace("PLACEHOLDER", newsStoryId);
		String moreCss = HomepageUIConstants.NewsStoryMoreLinkById.replace("PLACEHOLDER", newsStoryId);
		String moreSaveThisCss = HomepageUIConstants.NewsStoryMoreSaveThisLinkById.replace("PLACEHOLDER", newsStoryId);
		String linksDisplayDivCss = HomepageUIConstants.NewsStoryActiveLinksDivById.replace("PLACEHOLDER", newsStoryId);
		
		// Retrieve the visible links from the news story
		boolean saveThisLinkVisible = false;
		boolean clickedMoreLink = false;
		String visibleLinks = driver.getFirstElement(linksDisplayDivCss).getText();
		if(visibleLinks.indexOf("More") > -1) {
			
			log.info("INFO: Clicking on the 'More' link: " + moreCss);
			clickLink(moreCss);
			
			log.info("INFO: Verifying that the 'Save This' link for the news story is now displayed");
			saveThisLinkVisible = isElementVisible(moreSaveThisCss);
			clickedMoreLink = true;
		} else {
			
			log.info("INFO: Verifying that the news story contains a visible 'Save This' link");
			saveThisLinkVisible = isElementVisible(saveThisCss);
		}
		
		// Process which element was displayed in the UI and return the relevant CSS selector
		if(saveThisLinkVisible) {
			log.info("INFO: The 'Save This' link is displayed for the news story with content: " + newsItem);
			if(clickedMoreLink) {
				return moreSaveThisCss;
			} else {
				return saveThisCss;
			}
		} else {
			log.info("ERROR: The 'Save This' link was NOT displayed for the news story with content: " + newsItem);
			log.info("ERROR: Can NOT return a valid CSS selector to be clicked since the element is NOT displayed");
			return null;
		}
	}
	
	/**
	 * Opens the file details overlay for the specified news story
	 * 
	 * @param newsStory - The String content of the news story to click on and open the file details overlay
	 */
	public void openNewsStoryFileDetailsOverlay(String newsStory) {
		
		log.info("INFO: Now opening the file details overlay for the news story with content: " + newsStory);
		
		// Reset the view to the top of the news feed, bring the news story into view and open the file details overlay
		resetASToTopAndBringNewsStoryIntoViewAndClickNewsStoryElement(newsStory);
	}
	
	/**
	 * Opens the EE for the specified news story
	 * 
	 * @param newsStory - The String content of the news story to click on and open the EE
	 */
	public void openNewsStoryEE(String newsStory) {
		
		log.info("INFO: Now opening the EE overlay for the news story with content: " + newsStory);
		
		// Reset the view to the top of the news feed, bring the news story into view and open the EE
		resetASToTopAndBringNewsStoryIntoViewAndClickNewsStoryElement(newsStory);
		
		// Switch focus to the EE frame
		switchToEEFrame();
	}
	
	/**
	 * Resets the AS back to the top of the news feed and then brings the required news item into view
	 * 
	 * @param newsStory - The String content of the news story to bring into view in the UI
	 * @return - The Element instance of the news story element which has been brought into view
	 */
	private Element resetASToTopAndBringNewsStoryIntoView(String newsStory) {
		
		// Reset the view to the top of the news feed
		driver.executeScript("scroll(0,0);");
		
		// Retrieve the ID for the required news story
		String newsStoryId = getNewsStoryId(newsStory);
		
		// Retrieve the element corresponding to the news story
		String newsStoryCSSSelector = HomepageUIConstants.NewsStoryElementById.replace("PLACEHOLDER", newsStoryId);
		Element newsStoryElement = getFirstVisibleElement(newsStoryCSSSelector);
		
		log.info("INFO: Bringing the news story item into view");
		bringNewsStoryIntoView(newsStoryElement);
		
		return newsStoryElement;
	}
	
	/**
	 * Resets the AS back to the top of the news feed, brings the required news item into view and then clicks on the news item to open the file details overlay / EE
	 * 
	 * @param newsStory - The String content of the news story to click on and open the file details overlay / EE
	 */
	private void resetASToTopAndBringNewsStoryIntoViewAndClickNewsStoryElement(String newsStory) {
		
		// Bring the required news story into view
		Element newsStoryElement = resetASToTopAndBringNewsStoryIntoView(newsStory);
		
		// Click on the news story to open the file details overlay
		clickElement(newsStoryElement);
	}
	
	/**
	 * Retrieves the news story ID for the news story that contains the news item content
	 * 
	 * This method has been set as "private" since there is no requirement to use this method externally from this
	 * class at this time
	 *  
	 * @param newsItemContent - The text/content of the news story
	 * @return - Returns the news story ID
	 */
	private String getNewsStoryId(String newsItemContent) {
		
		log.info("INFO: Retrieving the story ID for: " + newsItemContent);
		Element innerStoryElement = driver.getFirstElement(HomepageUIConstants.NewsStoryInnerDiv.replace("PLACEHOLDER", newsItemContent));
		String storyId = innerStoryElement.getAttribute("aria-describedby").replace("_openeedescription", "");
		
		log.info("INFO: Returning the story ID as " + storyId);
		return storyId;
	}
	
	/**
	 * Clicks at the position of a news story Element in the UI - bringing the element into view on-screen if it is not 
	 * immediately present on-screen (does not click the exact location of the news story as this would open the EE).
	 * 
	 * This method has been set as "private" since there is no requirement to use this method externally from this class
	 * at this time
	 * 
	 * @param webElement - The Element to be brought into view
	 */
	private void bringNewsStoryIntoView(Element newsStory) {
		
		// Get the elements location in the UI
//		Point elementPosition = newsStory.getLocation();
//		int storyXPosition = (int) elementPosition.getX() - 10;
//		int storyYPosition = (int) elementPosition.getY();
		
		log.info("INFO: Clicking at the elements location in the UI");
		//clicking on it causes MoveTargetOutOfBounceException in Selenium 3.
		//driver.clickAt(storyXPosition, storyYPosition + (int) newsStory.getSize().getHeight());
		newsStory.hover();
	}
	
	/**
	 * Clicks on the timestamp for any news story in the news feed
	 * 
	 * @param newsStoryContent - The news story content relating to the news story whose timestamp is to be clicked
	 */
	public void clickNewsStoryTimeStamp(String newsStoryContent) {
		
		// Reset the view to the top of the news feed and bring the news story into view
		resetASToTopAndBringNewsStoryIntoView(newsStoryContent);
				
		log.info("INFO: Now retrieving the timestamp element for the news story with content: " + newsStoryContent);
		String timeStampCSSSelector = HomepageUIConstants.NewsStoryTimeLink.replaceAll("PLACEHOLDER", newsStoryContent);
		Element timeStampElement = driver.getFirstElement(timeStampCSSSelector);
		
		log.info("INFO: Now clicking on the timestamp element");
		clickElement(timeStampElement);
	}
	
	/**
	 * Clicks on a hashtag for any news story in the news feed
	 * 
	 * @param newsStoryOuterContent - The news story which appears in the UI (eg. "User1 posted a message to User2")
	 * @param newsStoryInnerContent - The news story content relating to the news story and including the hashtag which is to be clicked
	 * @param commentContent - If the tag is included in a comment then include the comment string, otherwise use null for this parameter
	 * @param mentionsContent - If the tag is included in a status message with mentions then include the Mentions object, otherwise use null for this parameter
	 * @param hashTag - The hashtag (without the '#' character) which is to be clicked
	 */
	public void clickNewsStoryHashTag(String newsStoryOuterContent, String newsStoryInnerContent, String commentContent, Mentions mentionsContent, String hashTag) {
		
		log.info("INFO: Clicking on the news story hashtag: #" + hashTag);
		
		// Retrieve the CSS selector corresponding to the news story
		String newsStoryId = getNewsStoryId(newsStoryOuterContent);	
		String newsStoryCSSSelector = HomepageUIConstants.NewsStoryElementById.replaceAll("PLACEHOLDER", newsStoryId);
		
		// Reset the view to the top of the news feed
		resetASToTop();
				
		log.info("INFO: Bringing the news story item into view");
		Element newsStoryElement = driver.getFirstElement(newsStoryCSSSelector);
		bringNewsStoryIntoView(newsStoryElement);
		
		log.info("INFO: Clicking on the hashtag now that it is in view");
		String hashTagElement;
		if(newsStoryInnerContent == null && commentContent == null && mentionsContent == null) {
			// Hashtag is displayed in the outer-div section of the news story
			hashTagElement= replaceNewsStory(HomepageUIConstants.NewsStoryHashTag_Outer, newsStoryOuterContent, hashTag.toLowerCase(), null);
			
		} else if(newsStoryInnerContent != null && commentContent == null && mentionsContent == null) {
			// Hashtag is displayed in the inner-div section of the news story
			hashTagElement= replaceNewsStory(HomepageUIConstants.NewsStoryHashTag_Inner_Or_Mentions, newsStoryInnerContent, hashTag.toLowerCase(), null);
			
		} else if(commentContent != null) {
			// Hashtag is displayed in a comment posted to the news story
			hashTagElement = replaceNewsStory(HomepageUIConstants.NewsStoryHashTag_Comment, commentContent, hashTag.toLowerCase(), null);
			
		} else {
			// Hashtag is displayed in the inner-div section of the news story in a mentions status update
			hashTagElement = replaceNewsStory(HomepageUIConstants.NewsStoryHashTag_Inner_Or_Mentions, mentionsContent.getBeforeMentionText(), hashTag.toLowerCase(), null);
		}
		clickLinkWait(hashTagElement);
	}
	
	/**
	 * 	This method works with saved news stories in the users Saved list.
	 * 
	 * 	Locates the entire news story element, hovers over it so as the "X" link to remove the news story
	 * 	will appear in the upper right corner of the story, and then clicks on the "X" to remove that story.
	 * 	Also handles the confirmation of removal of the story in the dialog box that appears once the
	 * 	"X" link has been clicked.
	 * 
	 * 	@param News story content String
	 */
	public void removeNewsStoryUsingUI(String newsItem) {
		
		log.info("INFO: Removing the saved news story");
		
		// Bring the required news story into view and hover over the news story to display the 'X' removal link for the news story
		resetASToTopAndBringNewsStoryIntoViewAndHoverOverNewsStory(newsItem);
		
		log.info("INFO: Clicking on the 'X' removal link attached to the news story");
		String deleteNewsStoryLink = HomepageUIConstants.NewsStoryXLinkById.replaceAll("PLACEHOLDER", getNewsStoryId(newsItem));
		clickLink(deleteNewsStoryLink);
		
		log.info("INFO: Verify: The 'Confirm Removal' dialog box has appeared");
		fluentWaitPresent(HomepageUIConstants.RemoveSavedStory);
		
		log.info("INFO: Clicking on 'Remove' in the 'Confirm Removal' dialog box to confirm news story removal");
		clickLink(HomepageUIConstants.RemoveSavedStory);
	}
	
	public void moveToClick(String element1, String clickOn){
		WebDriver wd = (WebDriver) driver.getBackingObject();	
		Actions builder = new Actions(wd);
		builder.moveToElement((WebElement) driver.getFirstElement(element1).getBackingObject()).moveToElement((WebElement) driver.getFirstElement(clickOn).getBackingObject()).click().perform();
	}
	
	public void verifySavedStory(BaseBlog blog) {
		clickLink(HomepageUIConstants.Saved);
		fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		fluentWaitPresent(HomepageUIConstants.SavedView);
		Assert.assertTrue(fluentWaitTextPresentRefresh(blog.getName()), 
						  "ERROR: Story doesn't show up in the Saved view");
		log.info("INFO: Story is appearing in the Saved view");
	}
	
	/**
	 * Types a string of text into status update field.
	 * <b>NOTE: </b> Must make separate call to click the Post button
	 * @param status - text to type
	 */
	public void enterStatus(String status) {
		//Enter the text
		Element statusUpdate = getFirstVisibleElement(BaseUIConstants.StatusUpdate_iFrame);
		driver.switchToFrame().selectFrameByElement(statusUpdate);
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
		log.info("INFO: Types update post message: " +status);
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(status);
		driver.switchToFrame().returnToTopFrame();
	}
	
	/**
	 * Types the @ followed by user string into the status update field
	 * and selects the user from the mentions typeahead suggestion list.
	 * <b>NOTE: </b> Must make separate call to click the Post button
	 * @param sUser - user string to type  
	 */
	public void selectAtMention(String sUser) {     

        //Enter user name your sending to
        log.info("INFO: Types at mention @" + sUser);
        driver.turnOffImplicitWaits();
        boolean textAreaPresent = driver.isElementPresent(HomepageUIConstants.atMentionTextArea);
        driver.turnOnImplicitWaits();
        dismissTourWelcomePopup();
        
        //Must determine if the ckeditor lite feature is enabled for SU area
        if(textAreaPresent){
              driver.getSingleElement(HomepageUIConstants.atMentionTextArea).click();
              typeTextWithDelay(HomepageUIConstants.atMentionTextArea , "@" + sUser);
        }else{
              enterStatus("@" + sUser);
        }
        
        //Collect all the options
        log.info("INFO: collecting dropdown options");
//	List<Element> options = driver.getVisibleElements("css=div[id^='lconn_core_PeopleTypeAheadMenu_'][class^='dijitMenuItem']");
        List<Element> options = driver.getVisibleElements("//div[starts-with(@id,\"lconn_core_PeopleTypeAheadMenu_\")][starts-with(@class,\"dijitMenuItem\")]");
        
        //Iterate through the list and select the user from drop down
        log.info("INFO: found " + options.size() + " options");
        Iterator<Element> iterator = options.iterator();
        while (iterator.hasNext()) {
              Element option = iterator.next();
              log.info("INFO: Name " + option.getText());
              if (option.getText().contains(sUser + " ")){
                    log.info("INFO: Found user " + sUser);
                    option.click();
                    log.info("INFO: Selected user " + sUser);
                    break; //exits as the user was found
              }
        }
  }



	/**
	 * Types the user's last name and selects the user from the mentions typeahead suggestion list.
	 * Then types a string of text into the status update field
	 * <b>NOTE: </b> Must make separate call to click the Post button
	 * @param username - User object
	 * @param updatePost - Text to type
	 */
	public void postAtMentionUserUpdate(User user, String updatePost){

		String mention = Character.toString('@');
		boolean displayed = false;
		// it's been observed that the guided tour would pop up sometimes around here
		// and obstruct typeahead selection so try to close it again.
		driver.executeScript(getCloseTourScript());
		
		for(int i=0;i<=10;i++)
		{
			try{
				List<WebElement> frames = findElements(createByFromSizzle(BaseUIConstants.StatusUpdate_iFrame));
				displayed = frames.get(0).isDisplayed();
			} catch (IndexOutOfBoundsException exception){
				driver.navigate().refresh();
				waitForPageLoaded(driver);
			}	
			if(displayed)
				break;
		}
		
		Element frame = driver.getVisibleElements(BaseUIConstants.StatusUpdate_iFrame).get(0);
		log.info("INFO: Frame toString: " + frame.toString());
		log.info("INFO: Frame location: " + frame.getLocation());
		log.info("INFO: Switching to the first frame");
		driver.switchToFrame().selectFrameByElement(frame);

		// Enter the user name
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(mention + user.getDisplayName());
		driver.switchToFrame().returnToTopFrame();

		log.info("INFO: Select the option from the mentions typeahead suggestion list");
		driver.getFirstElement((HomepageUIConstants.MentionsTypeaheadSelection)).doubleClick();

		// Enter the message
		driver.switchToFrame().selectFrameByElement(frame);
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
		log.info("INFO: Types update post message: " + updatePost);
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(" " + updatePost);

		driver.switchToFrame().returnToTopFrame();
	}
	
	public boolean waitForAndSwitchToEEFrame(String frameSelector, String selectorForElementWithinFrame, int timeoutSecs){
		int sleepCounter = 0;
		while(sleepCounter < timeoutSecs){
			List<Element> frames = driver.getElements(frameSelector);// get all of the frames that match the selector
			log.info("INFO: AMOUNT OF FRAME "+frames.size());
			
			for(Element frame : frames){										// step through each one
				driver.switchToFrame().selectFrameByElement(frame);					// change scope to within this frame
				if(driver.isElementPresent(selectorForElementWithinFrame))			// if it contains the element we're looking for
					return true;																// ee has loaded and we can return
				else																// otherwise switch back to the top level frame
					switchToTopFrame();
			}
			sleep(1000);														// the element either does not exist (-> defect) or the widget is still loading so we sleep
			sleepCounter++;														// and increment the timeout counter
		}
		return false;
	}

	/**
	 * findNewsItem - 
	 * @param Entry
	 * @return element
	 */
	public Element findNewsItem(String Entry){

		Element newsItem = null;
		
		List<Element> ActStremNewsItems = driver.getElements(HomepageUIConstants.activityStreamNewsItems);

		//reset the pointer to prevent issues
		resetASToTop();
		
		//Find news item and open EE window
		Iterator<Element> it = ActStremNewsItems.iterator();
	    log.info("INFO: Going through News items");
		while(it.hasNext())
		{
		    newsItem = it.next();
		    String newsInfo[] = newsItem.getText().split("\n");		    
		    log.info("INFO: News Item: " + newsInfo[0]);
		    if(newsItem.getText().contains(Entry)){
		    	log.info("INFO: Found news story");
			    break;		
		    }
		}
		
		return newsItem;
		
	}
	
	
	/** Method to select the entry in AS and then open the EE*/
	public void filterNewsItemOpenEE(String Entry){
		filterNewsItemOpenEE(Entry, null);
	}
	
	/** Method to select the entry in AS and then open the EE*/
	public void filterNewsItemOpenFileOverlay(String Entry){
		filterNewsItemOpenFileOverlay(Entry, null);
	}

	public void filterNewsItemOpenEE(String Entry, String OrEntry){

		boolean found = false;
		boolean OrEntryCheck = false;
		String elementID = "";		
		
		List<Element> ActStremNewsItems = driver.getElements(HomepageUIConstants.activityStreamNewsItems);

		//reset the pointer to prevent issues
		//commenting out since this clicks on the top story and inadvertently opens EE
		//resetASToTop();
		driver.executeScript("scroll(0,0);");
		
		//Find news item and open EE window
		Iterator<Element> it = ActStremNewsItems.iterator();
	    log.info("INFO: Going through News items");
	    
		while(it.hasNext())
		{
		    Element newsItem = it.next();
		    //check for OrEntry if null
		    if(OrEntry == null)
				OrEntryCheck = false;
			else
				OrEntryCheck = newsItem.getText().contains(OrEntry);
		    
		    String newsInfo[] = newsItem.getText().split("\n");		    
		    log.info("INFO: News Item: " + newsInfo[0]);
		    if(newsItem.getText().contains(Entry) || OrEntryCheck){
		    	log.info("INFO: Found News item: " + Entry);
		    	elementID = newsItem.getAttribute("id");
		    	//look for news item body element
		    	log.info("INFO: Open news item EE");
		    	Element targetStory = getFirstVisibleElement(getNewsItemBody(elementID));
		    	targetStory.hover();
				clickLinkWithJavascript(getNewsItemEEOpener(elementID));
				
				//Check that EE appeared.If it has not it will use a different method to click.
				if(!fluentWaitPresent(HomepageUIConstants.GenericEEFrame))
					clickLink(getNewsItemEEOpener(elementID));
				
				found = true;
			    break;		
		    }
		}
		
		//Validate that the news item was found
		Assert.assertTrue(found,
						  "ERROR: Unable to find " + Entry + " in list of news items." );
			
		//Find opened EE frame	
		List<Element> frames = driver.getVisibleElements(HomepageUIConstants.GenericEEFrame);
		
		Assert.assertTrue(frames.size() > 0,
				          "ERROR: EE frame was not opened after clicking on " + Entry);
		
		for(Element frame : frames){      										
			log.info("INFO: Frame id: " + frame.getAttribute("id"));
			driver.switchToFrame().selectFrameByElement(frame);					
		}
		
	}

	public void filterNewsItemOpenFileOverlay(String Entry, String OrEntry){

		boolean found = false;
		boolean OrEntryCheck = false;
		String elementID = "";		
		
		List<Element> ActStremNewsItems = driver.getElements(HomepageUIConstants.activityStreamNewsItems);

		//reset the pointer to prevent issues
		resetASToTop();
		
		//Find news item and open EE window
		Iterator<Element> it = ActStremNewsItems.iterator();
	    log.info("INFO: Going through News items");
	    
		while(it.hasNext())
		{
		    Element newsItem = it.next();
		    //check for OrEntry if null
		    if(OrEntry == null)
				OrEntryCheck = false;
			else
				OrEntryCheck = newsItem.getText().contains(OrEntry);
		    
		    String newsInfo[] = newsItem.getText().split("\n");		    
		    log.info("INFO: News Item: " + newsInfo[0]);
		    if(newsItem.getText().contains(Entry) || OrEntryCheck){
		    	log.info("INFO: Found News item: " + Entry);
		    	elementID = newsItem.getAttribute("id");		    	
		    	//look for news item body element
		    	fluentWaitPresent(getNewsItemBody(elementID));
		    	log.info("INFO: Open news item EE");
				getFirstVisibleElement(getNewsItemBody(elementID)).hover();
				clickLinkWithJavascript(getNewsItemEEOpener(elementID));
				
				found = true;
			    break;		
		    }
		}
		
		//Validate that the news item was found
		Assert.assertTrue(found,
						  "ERROR: Unable to find " + Entry + " in list of news items." );
		
	}
	
	/** Method to select the entry in AS and then open the EE*/
	public void selectAndOpenEE(String ComponentToFilterBy, String Entry, String commName){
		//Click on the dropdown and choose to filter with Blogs
		fluentWaitPresent(BaseUIConstants.FilterByComponentName);
		driver.getSingleElement(BaseUIConstants.FilterByComponentName).useAsDropdown().selectOptionByVisibleText(ComponentToFilterBy);
		fluentWaitTextPresent(Entry);
		
		String blogLink = getStoryLink(commName);
				
		WebDriver wd = (WebDriver) driver.getBackingObject();	
		Actions builder = new Actions(wd);
		this.getFirstVisibleElement(blogLink).hover();
		try {
			builder.moveToElement((WebElement) driver.getFirstElement(blogLink).getBackingObject()).moveToElement((WebElement) driver.getFirstElement(HomepageUIConstants.selectEntryLoadEE).getBackingObject()).click().perform();
		} catch (ElementNotVisibleException e) {
			log.info("WARNING: Element Not Visible exception caught. Use Javascript click.");
			Element el = driver.getFirstElement(HomepageUIConstants.selectEntryLoadEE);
			driver.executeScript("arguments[0].click();", (WebElement) el.getBackingObject());
		}
	}

	/**
	 * Returns News Story Element ID
	 * @param newsStory
	 * @return
	 */
	public String getNewsStoryElementID(String newsStory){
		
		String elementID = "";
		
		//refresh page
		driver.navigate().refresh();
				
		//wait for sametime to load if enabled
		waitForSameTime();
		waitForPageLoaded(driver);
		driver.executeScript(getCloseTourScript());
		
		List<Element> newsItem = driver.getElements(HomepageUIConstants.newsStories);
		for(Element e : newsItem){			

			if (e.isTextPresent(newsStory)){
				elementID = e.getAttribute("id");
				break;
			}
		}
		return elementID;
		
	}
	
	/**
	 * 
	 * @param user
	 * @return boolean found or not
	 */
	public boolean findPersonInLikeList(User user){
		
		boolean foundPerson = false;
		
		List<Element> people = driver.getElements(HomepageUIConstants.LikeUserLink);
		log.info("INFO: Looking for " + user.getDisplayName());
		for(Element person : people){      										
			if(person.getText().contentEquals(user.getDisplayName())){
				log.info("INFO: Found " + user.getDisplayName());
				foundPerson = true;
				break;
			}
		}
		return foundPerson;
	}
	
	/**
	 * selectUserFromTypeAhead
	 * @param userName
	 * @return String - CSS list item of the user name in the typeahead
	 */
	public static String selectUserFromTypeAhead(String userName){
		return " li[role='option']:contains("+ userName + " "+")";
	}
	
	
	/**
	 * 
	 */
	protected abstract void changeAccess();
	
	/**
	 * 
	 */
	public abstract void verifyMyPageLink();
	
	/**
	 * Verify the Meetings Widget in right panel
	 */
	public abstract void verifyMeetingsWidget();
	
	public String replaceNewsStory(String NewsStory,String FirstComponentName,String SecondComponentName, String Username) {
		
		if (FirstComponentName != null){
			NewsStory = NewsStory.replaceAll("PLACEHOLDER", FirstComponentName);			
		}	
		if (SecondComponentName != null){
			NewsStory = NewsStory.replaceAll("REPLACE_THIS", SecondComponentName);
		}
		if (Username != null){
			NewsStory = NewsStory.replaceAll("USER", Username);
		}
		log.info(NewsStory);
		return NewsStory;
	}

	public String replaceNewsStory(String NewsStory,String FirstComponentName,String SecondComponentName, String ThirdComponentName, String Username) {
		
		if (FirstComponentName != null){
			NewsStory = NewsStory.replaceAll("PLACEHOLDER", FirstComponentName);		
		}
		
		if (ThirdComponentName != null){
			NewsStory = NewsStory.replaceAll("REPLACE_THIS_TOO", ThirdComponentName);
		}
		
		if (SecondComponentName != null){
			NewsStory = NewsStory.replaceAll("REPLACE_THIS", SecondComponentName);
		}
		
		if (Username != null){
			NewsStory = NewsStory.replaceAll("USER", Username);
		}
		
		return NewsStory;
	}
	
	/**
	 * 
	 * @param user - The user/community whose name will be selected from the typeahead
	 * @param typeahead - The typeahead from which the selection will be made
	 */
	public void typeaheadSelection(String user, String typeahead){

		//Collect all the options
		List<Element> options = driver.getVisibleElements(typeahead);
		
		//Iterate through the list and select the user/community from drop down
		Iterator<Element> iterator = options.iterator();
		while (iterator.hasNext()) {
			Element option = iterator.next();
			if (option.getText().contains(user + " ") || option.getText().endsWith(user)){
				log.info("INFO: Found user " + user);
				option.click();
			}
		}
		
	}
	
	/**
	 * This method enters a status update, but does not post it
	 * It can be used where verification that an element, or text, is present before posting is required
	 * @param statusUpdate - The status update which has to be added
	 */
	public void enterStatusUpdate(String statusUpdate){
		
		if(driver.isElementPresent(HomepageUIConstants.EnterMentionsStatusUpdate)){
			driver.getSingleElement(HomepageUIConstants.EnterMentionsStatusUpdate).type(statusUpdate);
		}else{
			driver.getSingleElement(HomepageUIConstants.EnterStatusUpdate).type(statusUpdate);
		}
	}
	
	/**
	 * @author Patrick Doherty
	 * This method enables the user to add a comment to a selected status update
	 * @param statusUpdate - The status update to which the comment will be added
	 * @param comment - The comment to be added
	 */
	public void addStatusUpdateComment(String statusUpdate, String comment){

		// Click on the 'Comment' link for this status update
		clickStatusUpdateCommentLink(statusUpdate);
		
		// Switch focus to the now visible comment frame
		switchToStatusUpdateCommentFrame(statusUpdate);
		
		log.info("INFO: Now entering the comment with content: " + comment);
		typeStringWithNoDelay(comment);
		
		// Switch focus back to the main frame
		switchToTopFrame();
		
		// Post the comment to the status update
		postStatusUpdateComment(statusUpdate, comment, true);
	}
	
	/**
	 * Clicks the 'Comment' link for the specified status update
	 * 
	 * @param statusUpdate - The String content of the status update whose 'Comment' link is to be clicked
	 */
	public void clickStatusUpdateCommentLink(String statusUpdate) {
		
		// Bring the news story into view before clicking on the 'Comment' link for the news story
		resetASToTopAndBringNewsStoryIntoView(statusUpdate);
		
		// Click on the 'Comment' link for the specified status update
		String commentLinkCSSSelector = HomepageUIConstants.StatusCommentLink_Unique.replace("PLACEHOLDER", statusUpdate);
		clickLinkWait(commentLinkCSSSelector);
	}
	
	/**
	 * Switches focus to the comment frame of a status update
	 * 
	 * @param statusUpdate - The String content of the status update whose comment frame is to be switched to
	 */
	public void switchToStatusUpdateCommentFrame(String statusUpdate) {
		
		log.info("INFO: Switching focus to the comment frame for the status update with content: " + statusUpdate);
		switchToCKEditorSUFrame(2);
	}
	
	/**
	 * Posts a pre-entered comment to a status update and verifies whether the comment is then displayed in the UI (if verification boolean is set to true)
	 * 
	 * @param comment - The String content of the comment to be posted to the status update
	 * @param verifyCommentIsDisplayed - True if the verification for whether the comment is displayed after posting is to be carried out, false otherwise
	 */
	public void postStatusUpdateComment(String statusUpdate, String comment, boolean verifyCommentIsDisplayed) {
		
		// Click on the 'Post' link to post the comment to the specified status update
		String postCommentCSSSelector = HomepageUIConstants.PostComment_Unique.replace("PLACEHOLDER", statusUpdate);
		clickLinkWait(postCommentCSSSelector);
		
		if(verifyCommentIsDisplayed) {
			log.info("INFO: Verifying that the comment is now displayed in the UI after posting it to the status update");
			Assert.assertTrue(fluentWaitTextPresent(comment.trim()), 
								"ERROR: The comment was NOT displayed in the UI after posting it to the status update");
		}
	}
	
	/**
	 * Posts a comment with URL to a status update
	 * 
	 * @param statusUpdate - The String content of the status update whose comment frame is to be switched to
	 * @param commentBeforeURL - The String content of the comment text to appear before the URL
	 * @param url - The String content of the URL to appear at the end of the comment
	 * @return - True if the URL preview widget for the comment is displayed, false otherwise
	 */
	public boolean addStatusUpdateCommentWithURL(String statusUpdate, String commentBeforeURL, String url) {
		
		// Click on the 'Comment' link for this status update
		clickStatusUpdateCommentLink(statusUpdate);
		
		// Switch focus to the now visible comment frame
		switchToStatusUpdateCommentFrame(statusUpdate);
		
		// Enter the comment with URL
		typeBeforeURLTextAndURL(commentBeforeURL, url);
		
		// Switch focus back to the main frame
		switchToTopFrame();
		
		// Post the comment to the status update
		String commentPosted = commentBeforeURL + " " + url;
		postStatusUpdateComment(statusUpdate, commentPosted, false);
		
		// Create the URL preview widget element to be verified as absent
		String urlPreviewForCommentURL = replaceNewsStory(HomepageUIConstants.URL_PREVIEW_NEWS_FEED, commentPosted, url, null);
		
		log.info("INFO: Now checking if the URL preview widget is displayed for the comment posted to the status update");
		return isElementVisible(urlPreviewForCommentURL);
	}
	
	/**
	 * @author Patrick Doherty
	 * This method enables the user to search a specified element(elementToBeSearched) for a specified target text(target)
	 * If the target is found, the boolean found is set to true.
	 * @param target - The text of the search's target object
	 * @param elementToBeSearched - The element which is being searched for the target.  This could be a menu, container etc.
	 * @return found - A boolean which indicates whether, or not, the target has been found
	 */
	public boolean searchForElement(String target, String elementToBeSearched){
		
		log.info("INFO: Now retrieving all visible elements with CSS selector: " + elementToBeSearched);
		List<Element> elements = driver.getVisibleElements(elementToBeSearched);
		
		int index = 0;
		boolean foundElement = false;
		while(index < elements.size() && foundElement == false) {
			Element currentElement = elements.get(index);
			
			String elementText = currentElement.getText().trim();
			log.info("INFO: Found element with text content: " + elementText);
			if(elementText.contains(target)) {
				log.info("INFO: Found target element with text content: " + elementText);
				foundElement = true;
			}
			index ++;
		}
		return foundElement;
	}
	
	/**
	 * @param elementToClick - The page element to click if it is visible
	 */
	public void clickIfVisible(String elementToClick){

		if(driver.getSingleElement(elementToClick).isVisible()){
			clickLinkWait(elementToClick);	
		}

	}
	
	/**
	 * This method enables the user to follow a specific tag
	 * @param tagToFollow - A String object The tag which will be followed 
	 */
	public void followTag(String tagToFollow){
		
		log.info("INFO: Click the 'Manage Tags' link");
		clickLinkWait(HomepageUIConstants.ManageTags);
		
		log.info("INFO: Clear the text box and type the supplied tag - " + tagToFollow);
		driver.getSingleElement(HomepageUIConstants.TagTextBox).clear();
		driver.getSingleElement(HomepageUIConstants.TagTextBox).type(tagToFollow);
		
		log.info("INFO: Click the 'Add Tag' button");
		clickLinkWait(HomepageUIConstants.AddTagBtn);

		log.info("INFO: Verify that the tag has been successfully added");
		fluentWaitTextPresent(Data.getData().TagFollowedText);
		
		log.info("INFO: Click the 'Done' button");
		clickLinkWait(HomepageUIConstants.AddTagDoneBtn);
		
	}
	
	public abstract void switchToHomepageTab();
	
	public static HomepageUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  HomepageUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  HomepageUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new  HomepageUIProduction(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  HomepageUIOnPrem(driver);
		} else if(product.toLowerCase().equals("multi")) {
			return new  HomepageUIOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
	/**
	 * Clicks the notification center button to open the notification center flyout - makes 3 attempts to open the flyout and confirm that it is displayed
	 * 
	 * @return - True if the flyout is displayed as expected after clicking the notification center button, false otherwise
	 */
	public boolean clickNotificationCenter() {
		
		log.info("INFO: Opening the Notification Center flyout via a mouse click");
		
		// Reset the view back to the top of the screen
		resetASToTop();
		
		log.info("INFO: Verify that the Notification Center button is displayed in the UI");
		Assert.assertTrue(fluentWaitPresent(HomepageUIConstants.NotificationCenterBtn),
							"ERROR: The Notification Center button was NOT displayed in the UI");
		
		// Retrieve the element for the notification center button and determine the center point of the button
		Element notificationCenterButton = driver.getSingleElement(HomepageUIConstants.NotificationCenterBtn);
		int centerXPos = notificationCenterButton.getLocation().x + (notificationCenterButton.getSize().width / 2);
		int centerYPos = notificationCenterButton.getLocation().y + (notificationCenterButton.getSize().height / 2);
		
		// Click on the center of the notification center button
		driver.clickAt(centerXPos, centerYPos);
		
		// Verify that the Notification Center flyout is now displayed in the UI
		boolean flyoutIsDisplayed = verifyNotificationCenterFlyoutIsDisplayedInTheUI();
		int numberOfRetries = 0;
		while(numberOfRetries < 2 && flyoutIsDisplayed == false) {
			
			// Click at the center of the notification center button again
			driver.clickAt(centerXPos, centerYPos);
			
			// Verify that the Notification Center flyout is now displayed in the UI
			flyoutIsDisplayed = verifyNotificationCenterFlyoutIsDisplayedInTheUI();
			
			numberOfRetries ++;
		}
		return flyoutIsDisplayed;
	}
	
	/**
	 * Hovers over the notification center button to open the notification center flyout - makes 3 attempts to open the flyout and confirm that it is displayed
	 * 
	 * @return - True if the flyout is displayed as expected after hovering over the notification center button, false otherwise
	 */
	public boolean hoverOverNotificationCenter() {
		
		log.info("INFO: Opening the Notification Center flyout via hovering over the Notification Center icon");
		
		// Reset the view back to the top of the screen
		resetASToTop();
		
		log.info("INFO: Verify that the Notification Center button is displayed in the UI");
		Assert.assertTrue(fluentWaitPresent(HomepageUIConstants.NotificationCenterBtn),
							"ERROR: The Notification Center button was NOT displayed in the UI");
		
		// Retrieve the element for the notification center button
		Element notificationCenterButton = driver.getSingleElement(HomepageUIConstants.NotificationCenterBtn);
		
		// Hover over the notification center button
		hoverOverElement(notificationCenterButton);
		
		// Verify that the Notification Center flyout is now displayed in the UI
		boolean flyoutIsDisplayed = verifyNotificationCenterFlyoutIsDisplayedInTheUI();
		int numberOfRetries = 0;
		while(numberOfRetries < 2 && flyoutIsDisplayed == false) {
			
			// Hover over the notification center button
			hoverOverElement(notificationCenterButton);
			
			// Verify that the Notification Center flyout is now displayed in the UI
			flyoutIsDisplayed = verifyNotificationCenterFlyoutIsDisplayedInTheUI();
			
			numberOfRetries ++;
		}
		return flyoutIsDisplayed;
	}
	
	/**
	 * Verifies that the notification center flyout is displayed in the UI
	 * 
	 * @return - True if the flyout is displayed, false otherwise
	 */
	public boolean verifyNotificationCenterFlyoutIsDisplayedInTheUI() {
		
		log.info("INFO: Verify that the Notification Center flyout is displayed in the UI");
		return isElementVisible(HomepageUIConstants.NotificationCenterFlyout);
	}
	
	/**
	 * Clicks on an element - this click is more precise and more robust than using element.click()
	 * 
	 * @param elementToBeClicked - The Element instance of the element to be clicked
	 */
	public void clickElement(Element elementToBeClicked) {
		
		log.info("INFO: Now clicking on the element with ID: " + elementToBeClicked.getAttribute("id"));
		WebDriver webDriver = (WebDriver) driver.getBackingObject();
		Actions actions = new Actions(webDriver);
		actions.moveToElement((WebElement) elementToBeClicked.getBackingObject()).click().perform();
	}
	
	/**
	 * Hovers over an element - this hover is more precise and more robust than using element.hover()
	 * 
	 * @param elementToHoverOver - The Element instance of the element to hover over
	 */
	public void hoverOverElement(Element elementToHoverOver) {
		
		log.info("INFO: Now hovering over the element with ID: " + elementToHoverOver.getAttribute("id"));
		WebDriver webDriver = (WebDriver) driver.getBackingObject();
		Actions actions = new Actions(webDriver);
		
		// Retrieve the central height of the element
		int centreOfHeight = (int) (elementToHoverOver.getSize().height / 2);
		
		// Hover over a position 5 pixels in from the left side of the element and central to the height of the element
		actions.moveToElement((WebElement) elementToHoverOver.getBackingObject()).moveByOffset(5, centreOfHeight).perform();
	}
	
	/**
	 * Retrieves any CSS attribute value from an element
	 * 
	 * @param element - The Element instance of the element from which the CSS attribute value is to be retrieved
	 * @param cssAttribute - The CSS attribute whose value is to be retrieved
	 * @return - The CSS attribute value
	 */
	private String getElementCSSAttributeValue(Element element, String cssAttribute) {
		
		log.info("INFO: Now retrieving the '" + cssAttribute + "' CSS attribute value for the element with ID: " + element.getAttribute("id"));
		WebElement webElement = (WebElement) element.getBackingObject();
		String attributeValue = webElement.getCssValue(cssAttribute);
		
		log.info("INFO: The value for the '" + cssAttribute + "' CSS attribute has been retrieved as: " + attributeValue);
		return attributeValue;
	}
	
	/**
	 * Retrieves the background colour of an element in rgba format
	 * 
	 * @param element - The Element instance of the element whose background colour is to be retrieved
	 * @return - The background colour string in rgba format
	 */
	public String getElementBackgroundColour(Element element) {
		
		// Retrieve the background colour for the element
		return getElementCSSAttributeValue(element, "background-color");
	}
	
	/**
	 * Retrieves the text colour of an element in rgba format
	 * 
	 * @param element - The Element instance of the element whose text colour is to be retrieved
	 * @return - The text colour string in rgba format
	 */
	public String getElementTextColour(Element element) {
		
		// Retrieve the text colour for the element
		return getElementCSSAttributeValue(element, "color");
	}
	
	/**
	 * Retrieves the background colour of an element in hexadecimal format
	 * 
	 * @param element - The Element instance of the element whose background colour is to be retrieved
	 * @return - The background colour string in hexadecimal format
	 */
	public String getElementBackgroundColourAsHex(Element element) {
		
		// Retrieve the background colour for the element
		String backgroundAsRGBA = getElementBackgroundColour(element);
		
		return convertRGBAColourToHex(backgroundAsRGBA);
	}
	
	/**
	 * Converts an RGBA colour to a hexadecimal colour
	 * 
	 * @param rgbaValueString - The String content of the RGBA colour value
	 * @return - The String content containing the hexadecimal colour
	 */
	private String convertRGBAColourToHex(String rgbaValueString) {
		
		log.info("INFO: Now coverting the RGBA value returned for the colour into hexadecimal values");
		String rgbaValues = rgbaValueString.substring(5, rgbaValueString.length() - 1);
		
		int red = Integer.parseInt(rgbaValues.substring(0, rgbaValues.indexOf(',')));
		rgbaValues = rgbaValues.substring(rgbaValues.indexOf(',') + 1).trim();
		
		int green = Integer.parseInt(rgbaValues.substring(0, rgbaValues.indexOf(',')));
		rgbaValues = rgbaValues.substring(rgbaValues.indexOf(',') + 1).trim();
		
		int blue = Integer.parseInt(rgbaValues.substring(0, rgbaValues.indexOf(',')));
		
		String hexadecimalColour = ("#" + Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue)).toUpperCase();
		
		log.info("INFO: The hexadecimal value of the colour has been retrieved: " + hexadecimalColour);
		return hexadecimalColour;
	}
	
	/**
	 * Retrieves the text colour of an element in hexadecimal format
	 * 
	 * @param element - The Element instance of the element whose text colour is to be retrieved
	 * @return - The text colour string in hexadecimal format
	 */
	public String getElementTextColourAsHex(Element element) {
		
		// Retrieve the text colour for the element
		String textAsRGBA = getElementTextColour(element);
		
		return convertRGBAColourToHex(textAsRGBA);
	}
	
	/**
	 * Retrieves the tile element for a notification center news story
	 * 
	 * @param newsStory - The news story content which appears inside of the notification center tile to be retrieved
	 * @return - The Element instance of the tile
	 */
	public Element getNotificationCenterTileElement(String newsStory) {
		
		log.info("INFO: Now retrieving the notification center tile with news story content: " + newsStory);
		String tileCSSSelector = HomepageUIConstants.NotificationCenter_Tile.replaceAll("PLACEHOLDER", newsStory);
		return driver.getFirstElement(tileCSSSelector);
	}
	
	/**
	 * Retrieves the blue dot element from inside a tile element in the notification center
	 * 
	 * @param newsStory - The news story content which appears inside of the notification center tile
	 * @return - The Element instance of the tiles blue dot
	 */
	public Element getBlueDotElementFromNotificationCenterTile(String newsStory) {
		
		log.info("INFO: Now retrieving the blue dot element from the notification center tile");
		String blueDotCSSSelector = HomepageUIConstants.NotificationCenter_BlueDot.replaceAll("PLACEHOLDER", newsStory);
		return driver.getFirstElement(blueDotCSSSelector);
	}
	
	/**
	 * gets the web element for a link within a notification center event
	 * @param linkName - text of the link required
	 * @return web element for the link
	 */
	private Element getLinkInNotificationCenter(String linkName){
		
		List<Element> elements = driver.getElements(HomepageUIConstants.NotificationCenterTitleLink);
		for(Element element:elements){
			if (element.getText().matches(linkName)){
				return element;
			}
		}
		return null;
		
	}
	
	/**
	 * clicks a link within the notification center, link should be unique
	 * @param linkName text of the link you wish to click
	 */
	@Deprecated
	public void clickLinkInNotificationCenter(String linkName){
		
		/*
		 * Getting the location of the footer and its dimensions
		 * Will later click the bottom right corner of the footer
		 * to ensure that the focus is inside the flyout
		 * and that the flyout doesn't close when we go to click
		 * the selected link.  This avoids the issue that can arise
		 * when the focus moves from the NC icon diagonally to the
		 * link name
		 */
		Point footerLocation = driver.getSingleElement(HomepageUIConstants.NotificationCenterFooterDiv).getLocation();
		Dimension footerDimension = driver.getSingleElement(HomepageUIConstants.NotificationCenterFooterDiv).getSize();

		Element element = getLinkInNotificationCenter(linkName);

		driver.clickAt((int) footerLocation.getX() + ((int)footerDimension.getWidth() - 5), (int) footerLocation.getY() + ((int)footerDimension.getHeight() - 5));

		// Click the news story in the notification centre - using Element.click() closes the flyout before Selenium has time to click the link
		Point elementLocation = element.getLocation();
		driver.clickAt((int) elementLocation.getX() + 5, (int) elementLocation.getY() + 5);
	}
	
	/**
	 * Clicks on the specified link in the Notification Center flyout
	 * 
	 * @param newsStoryContainingLink - The String content of the news story in which the link to be clicked appears
	 * @param linkToBeClicked - The String content of the link to be clicked within the Notification Center (should be a unique link)
	 */
	public void clickLinkInNotificationCenter(String newsStoryContainingLink, String linkToBeClicked) {
		
		log.info("INFO: Now retrieving the Notification Center news story element with content: " + newsStoryContainingLink);
		String ncNewsStoryElement = HomepageUIConstants.NotificationCenter_Tile.replace("PLACEHOLDER", newsStoryContainingLink);
		Element ncNewsStoryTile = getFirstVisibleElement(ncNewsStoryElement);
		
		log.info("INFO: Now clicking on the link in the Notification Center news story element with content: " + linkToBeClicked);
		String linkToClick = "link=" + linkToBeClicked;
		List<Element> matchingLinks = ncNewsStoryTile.getElements(linkToClick);
		
		log.info("INFO: Verify that a matching link has been found in the Notification Center news story element");
		Assert.assertTrue(matchingLinks.size() > 0, 
							"ERROR: There are NO matching links found in the Notification Center news story element with selector: " + linkToClick);
		
		log.info("INFO: Verify that the matching link is unique (ie. only one link should be found)");
		Assert.assertTrue(matchingLinks.size() == 1, 
							"ERROR: The matching link is NOT unique - " + matchingLinks.size() + " matching links have been found for selector: " + linkToClick);
		
		log.info("INFO: Now clicking on the link with selector: " + linkToClick);
		matchingLinks.get(0).click();
	}
	
	/**
	 * Checks a notification tile in the Notification Center to verify if the profile picture element associated with that tile
	 * belongs to the user with the specified user ID (ie. verifies the correct profile pic is displayed for the correct user)
	 * 
	 * @param newsStoryContainingPhoto - The String content of the news story to which the profile picture is attached
	 * @param profilePicUserId - The String content of the UUID for the user whose profile picture is to be displayed
	 * @return - True if the profile pic is verified as belonging to the user with the specified ID, false otherwise
	 */
	public boolean checkNotificationCenterProfilePicture(String newsStoryContainingPhoto, String profilePicUserId) {
		
		log.info("INFO: Now retrieving the Notification Center news story element with content: " + newsStoryContainingPhoto);
		String ncNewsStoryElement = HomepageUIConstants.NotificationCenter_Tile.replace("PLACEHOLDER", newsStoryContainingPhoto);
		Element ncNewsStoryTile = getFirstVisibleElement(ncNewsStoryElement);
		
		log.info("INFO: Now retrieving the profile picture element linked to the Notification Center news story element");
		Element authorPhoto = ncNewsStoryTile.getSingleElement(HomepageUIConstants.NotificationCenter_Tile_ProfilePic);
		
		// Retrieve the SRC attribute for this profile pic - the SRC should contain the UUID for the relevant user
		String profilePicSrc = authorPhoto.getAttribute("src");
		log.info("INFO: The SRC attribute for the profile picture has been retrieved: " + profilePicSrc);
		
		if(profilePicSrc.indexOf(profilePicUserId) > -1) {
			log.info("INFO: The profile picture found belongs to the user with ID: " + profilePicUserId);
			return true;
		}
		log.info("ERROR: The profile picture found does NOT belong to the user with ID: " + profilePicUserId);
		return false;
	}
	
	/** 
	 * Checks to see if the title of a particular notification event is present in notification center flyout
	 * 
	 * @param titleText - the text of the title of the notification story you wish to verify is present
	 * @return true if event title is present in the notification center stories list, false if not
	 */
	public boolean checkNotificationTitle(String titleText) {
		
		log.info("INFO: Searching through the notification center events for the news story matching: " + titleText);
		
		String notificationCenterNewsStoryItem = HomepageUIConstants.NotificationCenter_Tile.replaceAll("PLACEHOLDER", titleText);
		
		List<Element> listOfMatchingElements = driver.getElements(notificationCenterNewsStoryItem);
		if(listOfMatchingElements.size() == 0) {
			log.info("ERROR: No notification center event could be found containing news story: " + titleText);
			return false;
		} else if(listOfMatchingElements.size() > 1) {
			log.info("ERROR: More than one matching event was found in the notification center for news story: " + titleText);
			return false;
		} else {
			log.info("INFO: Successfully found the event in the notification center");
			return true;
		}
	}
	
	/**
	 * Switches to the currently active element and enters the provided text
	 * 
	 * @param textToType - Text to be entered into the active element
	 * @return - Returns the currently active element
	 */
	public void switchToActiveElementAndTypeText(String textToType) {
		WebDriver webDriver = (WebDriver) driver.getBackingObject();
		WebElement activeElement = webDriver.switchTo().activeElement();
		
		activeElement.sendKeys(textToType);
	}
	
	/**
	 * Determines if the element identified by the CSS selector is visible on-screen or not
	 * 
	 * @param cssSelector - The element to be identified
	 * @return - True if the element is visible, false otherwise
	 */
	public boolean isElementVisible(String cssSelector) {
		if(driver.getVisibleElements(cssSelector).size() == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method uses the generic FileThumbnailContainer appObject to build a CSS selector
	 * which can be used to find a specific thumbnail container for a playable (MP4 etc.) file
	 * in the Homepage Activity Stream
	 * 
	 * @param fileName - The name of the file
	 * @param fileExtension - The file's extension, e.g. .mp4
	 * @return thumbnailContainer - A String object which can be used as a CSS selector
	 * for the playable file's thumbnail container in the Homepage Activity Stream
	 */
	public String createThumbnailContainerCSS(String fileName, String fileExtension){
		
		String fullFileTitle = fileName + fileExtension;
		String thumbnailContainer = HomepageUIConstants.FileThumbnailContainer.replace("NAME_EXTENSION", fullFileTitle);
		return thumbnailContainer;
	}
	
	/**
	 * Retrieves the currently selected option from any <SELECT> element
	 * 
	 * @param selectDropDownCSSSelector - The CSS selector of the select element whose selected option is to be retrieved
	 * @return - The currently selected option in the <SELECT> element
	 */
	public String getSelectedDropDownMenuOption(String selectDropDownCSSSelector) {
		
		log.info("INFO: Retrieving the currently selected option for the <SELECT> element with CSS selector: " + selectDropDownCSSSelector);
		Select selectMenu = retrieveSelectElement(selectDropDownCSSSelector);
		return selectMenu.getFirstSelectedOption().getText();
	}
	
	/**
	 * Retrieves any <SELECT> element based on its CSS selector
	 * 
	 * @param selectDropDownCSSSelector - The CSS selector of the select element to be retrieved
	 * @return - The Select instance of the <SELECT> element
	 */
	private Select retrieveSelectElement(String selectDropDownCSSSelector) {
		
		// Retrieve the <SELECT> element based on its CSS selector and convert to a Select object
		WebElement selectElement = (WebElement) driver.getFirstElement(selectDropDownCSSSelector).getBackingObject();
		return new Select(selectElement);
	}
	
	/**
	 * Retrieves the list of possible selectable options from any <SELECT> element
	 * 
	 * @param selectDropDownCSSSelector - The CSS selector of the select element whose list of selectable options are to be retrieved
	 * @return - The list of all selectable options in the <SELECT> element
	 */
	public List<String> getAllDropDownMenuOptions(String selectDropDownCSSSelector) {
		
		log.info("INFO: Retrieving all selectable options for the <SELECT> element with CSS selector: " + selectDropDownCSSSelector);
		Select selectMenu = retrieveSelectElement(selectDropDownCSSSelector);
		
		List<String> selectableOptions = new ArrayList<String>();
		List<WebElement> listOfOptions = selectMenu.getOptions();
		
		for(WebElement webElement : listOfOptions) {
			if(webElement.isEnabled()) {
				log.info("INFO: Adding the selectable option '" + webElement.getText() + "' to the list");
				selectableOptions.add(webElement.getText());
			}
		}
		return selectableOptions;
	}
	
	/**
	 * Switches to the specified CKEditor (Status Updates) iFrame and clicks into the input field, ready for an update / comment to be entered
	 * This iFrame also matches selectors on other UI screens, eg posting comments to blog entries
	 * 
	 * @param frameNumberToSwitchTo - The numerical value of the frame to be switched to
	 */
	public void switchToCKEditorSUFrame(int frameNumberToSwitchTo) {
		driver.changeImplicitWaits(5);
		List<Element> foundFrames = driver.getVisibleElements(BaseUIConstants.StatusUpdate_iFrame);
		driver.turnOnImplicitWaits();
		int frameCount = 1;
		boolean switchedToNewFrame = false;
		while(frameCount <= foundFrames.size() && switchedToNewFrame == false) {
			Element currentIFrame = foundFrames.get(frameCount - 1);
			
			log.info("INFO: Now processing iFrame number " + frameCount + " of " + foundFrames.size() + " iFrames found on this page");
			log.info("INFO: iFrame toString(): " + currentIFrame.toString());
			log.info("INFO: iFrame getLocation() : " + currentIFrame.getLocation());
			
			// Switch to the required iFrame if this is the correct iFrame
			if(frameCount == frameNumberToSwitchTo){
				log.info("INFO: Switching to iFrame " + frameCount + " of " + foundFrames.size() + " iFrames found on this page");
				driver.switchToFrame().selectFrameByElement(currentIFrame);
				switchedToNewFrame = true;
			}
			frameCount ++;
		}
		
		log.info("INFO: Verify that the correct iFrame has been found in the UI");
		Assert.assertTrue(switchedToNewFrame, "ERROR: The requested iFrame (iFrame number " + frameNumberToSwitchTo + ") could NOT be found in the UI");
		
		log.info("INFO: Now clicking into the input field");
		Element statusUpdateInputFrame = driver.getFirstElement(BaseUIConstants.StatusUpdate_Body);
		statusUpdateInputFrame.clickAt(statusUpdateInputFrame.getLocation().x + 1, statusUpdateInputFrame.getLocation().y + (int)(statusUpdateInputFrame.getSize().height / 2));
		
		log.info("INFO: Now moving the cursor to the end of any text within the comment / reply input field by pressing the END key");
		/**
		 * Now, in order to move the cursor to the end of any text present in the status update input field reliably and consistently, it is important
		 * to press the END key. Trying to move the cursor any other way can trigger certain elements (such as mentions links) to be highlighted and
		 * the cursor to then disappear entirely.
		 */
		driver.switchToActiveElement().type(Keys.END);
	}
	
	/**
	 * Enters text into an input field without using any delay
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke the switchToActiveElement() method
	 * @param contentToBeEntered - The String content to be entered into the input field
	 */
	public void typeStringWithNoDelay(String contentToBeEntered) {
		
		log.info("INFO: Now entering (with no delay) the text content '" + contentToBeEntered + "' into the input field");
		driver.switchToActiveElement().type(contentToBeEntered);
	}
	
	/**
	 * Enters text into an input field using delay
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke the switchToActiveElement() method
	 * @param contentToBeEntered - The String content to be entered into the input field
	 */
	public void typeStringWithDelay(String contentToBeEntered) {
		
		log.info("INFO: Now entering (with delay) the text content '" + contentToBeEntered + "' into the input field");
		driver.switchToActiveElement().typeWithDelay(contentToBeEntered);
	}
	
	/**
	 * Enters the before mentions string from a mention
	 * 
	 * @param mentions - The Mentions instance of the mention to be entered
	 */
	public void typeBeforeMentionsString(Mentions mentions) {
		
		log.info("INFO: Now entering the before mentions text with content: '" + mentions.getBeforeMentionText().trim() + "'");
		typeStringWithNoDelay(mentions.getBeforeMentionText() + " ");
	}
	
	/**
	 * Enters the mentions string from a mention
	 * 
	 * @param mentions - The Mentions instance of the mention to be entered
	 * @param numberOfCharactersToType - The number of user name characters to type (supporting partial mentions)
	 */
	public void typeMentions(Mentions mentions, int numberOfCharactersToType) {
		
		String mentionType;
		String userNameToType;
		if(numberOfCharactersToType < mentions.getUserToMention().getDisplayName().length()) {
			userNameToType = mentions.getUserToMention().getDisplayName().substring(0, numberOfCharactersToType);
			mentionType = "partial mention";
		} else {
			userNameToType = mentions.getUserToMention().getDisplayName();
			mentionType = "mention";
		}
		
		log.info("INFO: Now entering a " + mentionType +" to the user with user name: " + mentions.getUserToMention().getDisplayName().trim());
		typeStringWithDelay("@" + userNameToType.substring(0, userNameToType.length() - 1));
		typeStringWithDelay(userNameToType.substring(userNameToType.length() - 1));
	}
	
	/**
	 * Enters the after mentions string from a mention
	 * 
	 * @param mentions - The Mentions instance of the mention to be entered
	 */
	public void typeAfterMentionsString(Mentions mentions) {
		
		log.info("INFO: Now entering the after mentions text with content: '" + mentions.getAfterMentionText().trim() + "'");
		typeStringWithNoDelay(" " + mentions.getAfterMentionText());
	}
	
	/**
	 * Switches the focus back to the main frame and waits for the typeahead menu to appear
	 */
	public void waitForTypeaheadMenuToLoad() {
		
		log.info("INFO: Ensuring that the focus is on the main frame so as the selector for the typeahead menu will work correctly");
		switchToTopFrame();
		
		log.info("INFO: Ensuring that the typeahead menu is visible before proceeding");
		fluentWaitPresent(HomepageUIConstants.MentionsTypeaheadSelection);
	}
	
	/**
	 * Switches the focus back to the EE frame and waits for the typeahead menu to appear
	 */
	public void waitForEETypeaheadMenuToLoad() {
		
		log.info("INFO: Ensuring that the focus is on the main frame so as the selector for the typeahead menu will work correctly");
		switchToEEFrame();
		
		log.info("INFO: Ensuring that the typeahead menu is visible before proceeding");
		fluentWaitPresent(HomepageUIConstants.MentionsTypeaheadSelection);
	}
	
	/**
	 * Switches the focus back to the Global Sharebox frame and waits for the typeahead menu to appear
	 */
	public void waitForGlobalShareboxTypeaheadMenuToLoad() {
		
		log.info("INFO: Ensuring that the focus is on the global sharebox frame so as the selector for the typeahead menu will work correctly");
		switchToGlobalShareboxFrame();
		
		log.info("INFO: Ensuring that the typeahead menu is visible before proceeding");
		fluentWaitPresent(HomepageUIConstants.MentionsTypeaheadSelection);
	}
	
	/**
	 * Highlights a mentions link and presses the DELETE key to remove it.
	 * 
	 * PLEASE NOTE: This method can only work if the cursor is directly at the end of the mentions link on-screen
	 * 
	 * @param mentions - The Mentions instance of the mentioned user link to be removed
	 */
	public void highlightAndDeleteMentionsLink(Mentions mentions) {
		
		log.info("INFO: The mentions text will now be highlighted and deleted with content: @" + mentions.getUserToMention().getDisplayName());
		
		// Retrieve the WebDriver instance of driver and initialise Actions
		WebDriver webDriver = (WebDriver) driver.getBackingObject();	
		Actions actions = new Actions(webDriver);
		
		log.info("INFO: Now holding down the SHIFT key");
		actions.keyDown(Keys.SHIFT).perform();
		
		log.info("INFO: Now pressing the LEFT_ARROW key multiple times in order to highlight each character in the mentions link");
		int lengthOfMentionsText = ("@" + mentions.getUserToMention().getDisplayName()).length();
		for(int index = 0; index < lengthOfMentionsText; index ++) {
			actions.sendKeys(Keys.ARROW_LEFT).perform();
		}
		
		log.info("INFO: Now releasing the SHIFT key");
		actions.keyUp(Keys.SHIFT).perform();
		
		log.info("INFO: Now pressing the DELETE key to delete the highlighted mentions link");
		actions.sendKeys(Keys.DELETE).perform();
	}
	
	/**
	 * Selects a user to be mentioned from the typeahead menu using the arrow keys
	 * 	
	 * 	-> 	The typeahead menu highlight defaults to the last selectable user + 1 on On Premise (ie. the highlight sits on the 'Person Not Listed? Use Full Search' option)
	 * 		Therefore the arrow keys must scroll UP into the list before selecting a user.
	 * 	
	 * 	-> 	The typeahead menu defaults to the first selectable user on Smart Cloud (ie. the first user at the very top of the list)
	 * 		Therefore the arrow keys must scroll DOWN into the list before selecting a user.
	 * 
	 * @param isTopFrameMentions - True if the mentions typeahead is appearing for a mentions in the top frame. False otherwise
	 * @param isEEMentions - True if the mentions typeahead is appearing for a mentions in the EE. False otherwise
	 * @return - The user name of the selected user
	 */
	public String selectTypeaheadUserUsingArrowKeys(boolean isTopFrameMentions, boolean isEEMentions) {
		
		// Determine whether the current test is being run against On Premise or against Smart Cloud
		boolean isOnPremise;
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		if(isOnPremise) {
			log.info("INFO: Now pressing the UP ARROW key a number of times to move up the list of typeahead menu items");
			for(int index = 0; index < 7; index ++) {
				driver.switchToActiveElement().type(Keys.ARROW_UP);
			}
			log.info("INFO: Now pressing the DOWN ARROW key to finish on the user who will be mentioned");
			driver.switchToActiveElement().type(Keys.ARROW_DOWN);
		} else {
			log.info("INFO: Now pressing the DOWN ARROW key a number of times to move down through the list of typeahead menu items");
			for(int index = 0; index < 5; index ++) {
				driver.switchToActiveElement().type(Keys.ARROW_DOWN);
			}
			log.info("INFO: Now pressing the UP ARROW key to finish on the user who will be mentioned");
			driver.switchToActiveElement().type(Keys.ARROW_UP);
		}
		
		if(isTopFrameMentions) {
			// Wait for the top frame typeahead menu to load
			waitForTypeaheadMenuToLoad();
		} else if(isEEMentions) {
			// Wait for the EE typeahead menu to load
			waitForEETypeaheadMenuToLoad();
		}
		log.info("INFO: Retrieve all menu items to verify which one is about to be selected");
		List<Element> menuItemElements = getTypeaheadMenuItemsList(false);
		
		String selectedMenuItem = null;
		if(menuItemElements.size() > 5) {
			if(isOnPremise) {
				log.info("INFO: The fifth last user from the typeahead menu is now being selected");
				selectedMenuItem = menuItemElements.get(menuItemElements.size() - 5).getText();
			} else {
				log.info("INFO: The fifth user from the typeahead menu is now being selected");
				selectedMenuItem = menuItemElements.get(4).getText();
			}
		} else if(menuItemElements.size() <= 5 && menuItemElements.size() > 1) {
			if(isOnPremise) {
				log.info("INFO: The second user from the typeahead menu is now being selected");
				selectedMenuItem = menuItemElements.get(1).getText();
			} else {
				log.info("INFO: The second last user from the typeahead menu is now being selected");
				selectedMenuItem = menuItemElements.get(menuItemElements.size() - 2).getText();
			}
		} else {
			log.info("INFO: The first user from the typeahead menu is now being selected");
			selectedMenuItem = menuItemElements.get(0).getText();
		}
		log.info("INFO: Now pressing the ENTER key to select the highlighted user in the typeahead menu");
		driver.switchToActiveElement().type(Keys.ENTER);
		
		// Replace any unnecessary characters from the chosen user string from the typeahead string
		selectedMenuItem = removeUnwantedTypeaheadMenuCharacters(selectedMenuItem);
		
		return getUserNameFromTypeaheadMenuItemElementText(selectedMenuItem);
	}
	
	/**
	 * Replaces all unwanted characters from the Strings retrieved from the typeahead menu
	 * 
	 * @param typeaheadItemString - The String content taken from the typeahead menu item
	 * @return - The String content of the typeahead menu item text after all unwanted characters have been removed
	 */
	private String removeUnwantedTypeaheadMenuCharacters(String typeaheadItemString) {
		
		// Replace any unnecessary characters from the chosen user string from the typeahead string
		typeaheadItemString = typeaheadItemString.replaceAll("\n", " ").trim();
		typeaheadItemString = typeaheadItemString.replaceAll("<", "").trim();
		typeaheadItemString = typeaheadItemString.replaceAll(">", "").trim();
		typeaheadItemString = typeaheadItemString.replaceAll("\"", "").trim();
		
		return typeaheadItemString;
	}
	
	/**
	 * Retrieve the menu items displayed in the typeahead menu (can also return just the first element if required)
	 * 
	 * @param onlyReturnFirstElement - True if we only want to return the first element, false if we want to return all elements
	 * @return - A List of Element instances representing all visible menu items in the typeahead menu
	 */
	public List<Element> getTypeaheadMenuItemsList(boolean onlyReturnFirstElement) {
		
		log.info("INFO: Retrieving the list of menu item elements from the now visible typeahead menu");
		List<Element> listOfTypeaheadMenuItems = driver.getElements(HomepageUIConstants.MentionsTypeaheadMenuItem);
		
		log.info("INFO: There were " + listOfTypeaheadMenuItems.size() + " menu items found in the typeahead menu");
		
		// Loop through the elements and ignore any unwanted elements (ie. the "Previous" element or "Next" element since these are not user items)
		List<Element> validTypeaheadMenuItems = new ArrayList<Element>();
		int index = 0;
		while(index < listOfTypeaheadMenuItems.size()) {
			Element element = listOfTypeaheadMenuItems.get(index);
			String elementId;
			try {
				elementId = element.getAttribute("id");
			} catch (org.openqa.selenium.StaleElementReferenceException e) {
				log.info("User list refreshed so redraw the list again");
				listOfTypeaheadMenuItems = driver.getElements(HomepageUIConstants.MentionsTypeaheadMenuItem);
				element = listOfTypeaheadMenuItems.get(index);
				elementId = element.getAttribute("id");
			}

			if(elementId.indexOf("_prev") == -1 && elementId.indexOf("_searchDir") == -1 && elementId.indexOf("_next") == -1) {
				log.info("INFO: Valid typeahead menu item found with ID: " + elementId);
				validTypeaheadMenuItems.add(element);
			}
			
			if(onlyReturnFirstElement && validTypeaheadMenuItems.size() == 1) {
				log.info("INFO: First typeahead menu item element has been found successfully");
				index = listOfTypeaheadMenuItems.size();
			}
			index ++;
		}
		return validTypeaheadMenuItems;
	}
	
	/**
	 * Retrieves a list of the photo elements associated with each of the typeahead menu items
	 * 
	 * @param listOfTypeaheadMenuItems - A List of Element instances representing all visible menu items in the typeahead menu
	 * @return - A HashMap of the menu item Element instances mapped to their corresponding photo Element instances
	 */
	public HashMap<Element, Element> getTypeaheadMenuItemPhotos(List<Element> listOfTypeaheadMenuItems) {
		
		log.info("INFO: Now retrieving the photo associated with each of the menu items in the typeahead");
		
		HashMap<Element, Element> mapOfElementsWithPhotos = new HashMap<Element, Element>();
		for(Element menuItemElement : listOfTypeaheadMenuItems) {
			List<Element> photoElements = menuItemElement.getElements(HomepageUIConstants.MentionsTypeaheadMenuItemPhoto);
			
			if(photoElements.size() == 0) {
				log.info("INFO: No photo could be found for the menu item element with ID: " + menuItemElement.getAttribute("id"));
				mapOfElementsWithPhotos.put(menuItemElement, null);
			} else {
				log.info("INFO: A photo was found for the menu item element with ID: " + menuItemElement.getAttribute("id"));
				mapOfElementsWithPhotos.put(menuItemElement, photoElements.get(0));
			}
		}
		return mapOfElementsWithPhotos;
	}
	
	/**
	 * Searches through the list of typeahead menu item elements for the menu item containing the user to be mentioned
	 * 
	 * @param listOfTypeaheadMenuItems - The List<Element> instance of menu item elements
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @return - The Element instance of the menu item which contains the user to be mentioned if it is found successfully, null otherwise
	 */
	public Element getMenuItemContainingUserToBeMentioned(List<Element> listOfTypeaheadMenuItems, Mentions mentions) {
		
		log.info("INFO: Searching through the typeahead menu items in an attempt to locate the user with user name: " + mentions.getUserToMention().getDisplayName());
		int index = 0;
		boolean foundUserToSelect = false;
		Element elementToBeClicked = null;
		while(index < listOfTypeaheadMenuItems.size() && foundUserToSelect == false) {
			Element currentMenuItem = listOfTypeaheadMenuItems.get(index);
		
			if(currentMenuItem.getText().indexOf(mentions.getUserToMention().getDisplayName() + " ") > -1 ||
					currentMenuItem.getText().indexOf(mentions.getUserToMention().getDisplayName() + "\n") > -1) {
				log.info("INFO: Found the typeahead menu item containing the user to be mentioned");
				elementToBeClicked = currentMenuItem;
				foundUserToSelect = true;
			}
			index ++;
		}
		if(foundUserToSelect) {
			log.info("INFO: The user to be selected was found successfully in the typeahead menu");
			return elementToBeClicked;
		} else {
			log.info("ERROR: The user to be selected could not be found in the typeahead menu");
			log.info("ERROR: User name being searched for: " + mentions.getUserToMention().getDisplayName());
			log.info("ERROR: User names found in the typeahead menu: " + listOfTypeaheadMenuItems.toString());
			return null;
		}
	}
	/**
	 * Retrieves a list of the text contents of all selectable options from the typeahead menu
	 * 
	 * @param listOfTypeaheadMenuItems - The list of menu items (as Elements) whose text values are to be retrieved
	 * @return - An ArrayList instance of all text values from each of the items visible in the typeahead menu list
	 */
	public ArrayList<String> getTypeaheadUsersListTextContents(List<Element> listOfTypeaheadMenuItems) {
		
		log.info("INFO: Now retrieving all String values from each of the typeahead menu items");
		
		ArrayList<String> typeaheadMenuItems = new ArrayList<String>();
		for(Element element : listOfTypeaheadMenuItems) {
			String userMenuItem = element.getText().trim();
				
			// Replace any unnecessary characters from the chosen user string from the typeahead string
			userMenuItem = removeUnwantedTypeaheadMenuCharacters(userMenuItem);
			
			log.info("INFO: A user menu item has been retrieved with text: " + userMenuItem);
			typeaheadMenuItems.add(userMenuItem);
		}
		log.info("INFO: Verify that user menu item text values were found correctly in the typeahead menu");
		Assert.assertTrue(typeaheadMenuItems.size() > 0,
							"ERROR: There was a problem with retrieving the text contents from the user menu items in the typeahead menu list in the file details overlay");	
		return typeaheadMenuItems;
	}
	
	/**
	 * Retrieves the user name from the String content obtained from a typeahead menu item element
	 * 
	 * @param menuItemText - The String content of the typeahead menu item element
	 * @return - The user name
	 */
	public String getUserNameFromTypeaheadMenuItemElementText(String menuItemText) {
		
		// Replace any unnecessary characters from the typeahead string
		menuItemText = removeUnwantedTypeaheadMenuCharacters(menuItemText);
				
		// Retrieve the user name chosen from the element - first, find the second space in the element string 
		int indexOfSecondSpace = -1;
		int numberOfSpacesFound = 0;
		int index = 0;
		while(index < menuItemText.length() && indexOfSecondSpace == -1) {
			if(menuItemText.charAt(index) == ' ') {
				numberOfSpacesFound ++;
				
				if(numberOfSpacesFound == 2) {
					indexOfSecondSpace = index;
				}
			}
			index ++;
		}
		return menuItemText.substring(0, indexOfSecondSpace);
	}
	
	/**
	 * Searches the activity stream for a string using the AS search panel
	 * 
	 * @param searchText - The String content of the text to be used in the search
	 */
	public void searchUsingASSearch(String searchText) {
		
		log.info("INFO: Now clicking on the button to open the Activity Stream search panel");
		clickLinkWait(HomepageUIConstants.AS_SearchOpenElement);
		
		log.info("INFO: Now entering the text with content '" + searchText + "' into the search panel input field");
		clickLinkWait(HomepageUIConstants.AS_SearchTextBox);
		switchToActiveElementAndTypeText(searchText);
		
		log.info("INFO: Now clicking on the button to search for all AS events matching to the text content '" + searchText + "'");
		clickLinkWait(HomepageUIConstants.AS_SearchFind);
	}
	
	/**
	 * Cancels an Activity Stream search (ie. closes / hides the panel)
	 * 
	 * @param useXIconToCancel - True if the 'X' icon is to be used to close the AS search panel, false if the magnifying glass icon is to be used
	 */
	public void cancelASSearch(boolean useXIconToCancel) {
		
		if(useXIconToCancel) {
			log.info("INFO: Now closing the Activity Stream search panel by clicking on the 'X' icon");
			clickLinkWait(HomepageUIConstants.AS_SearchClose);
		} else {
			log.info("INFO: Now closing the Activity Stream search panel by clicking on the magnifying glass icon");
			clickLinkWait(HomepageUIConstants.AS_SearchOpenElement);
		}
	}
	
	/**
	 * Opens the global sharebox by clicking on the 'Share' button
	 */
	public void openGlobalSharebox() {
		
		log.info("INFO: Now clicking on the 'Share' button to open the global sharebox");
		clickLinkWait(CommunitiesUIConstants.ShareLink);
	}
	
	/**
	 * Switches focus to the global sharebox frame
	 */
	public void switchToGlobalShareboxFrame() {
		
		log.info("INFO: Ensuring that the focus is switched back to the main frame before continuing");
		switchToTopFrame();
		
		log.info("INFO: Waiting for the global sharebox frame to be visible in the UI");
		fluentWaitPresent(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		
		log.info("INFO: Now switching to the global sharebox frame");
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
	}
	
	/**
	 * Verifies that all global sharebox components are displayed / have loaded correctly
	 */
	public void verifyGlobalShareboxComponents() {
		
		log.info("INFO: Waiting for the global sharebox component to load");
		fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);
		
		log.info("INFO: Verify that the 'Post Updates To:' drop down menu is displayed in the global sharebox");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ShareBoxPostType),
							"ERROR: The drop down menu for the 'Post Updates To:' selection was NOT displayed in the global sharebox");
		
		log.info("INFO: Verify that the status update component IFrame is displayed in the global sharebox");
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.StatusUpdate_iFrame),
							"ERROR: The status update component IFrame was NOT displayed in the global sharebox");
		
		log.info("INFO: Verify that the 'Add a File' link is displayed in the global sharebox");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ShareBoxAddAFile),
							"ERROR: The 'Add a File' link was NOT displayed in the global sharebox");
		
		log.info("INFO: Verify that the 'Post' button is displayed in the global sharebox");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ShareBoxPost),
							"ERROR: The 'Post' button was NOT displayed in the global sharebox");
		
		log.info("INFO: Verify that the 'Cancel' button is displayed in the global sharebox");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ShareBoxCancel),
							"ERROR: The 'Cancel' button was NOT displayed in the global sharebox");	
	}
	
	/**
	 * Types a status update with URL into the status update input box.
	 * 
	 * Sometimes the URL Preview widget does not load correctly which is NOT reproducable manually. This method 
	 * attempts to improve the reliability of the test by entering and/or (if needed) re-entering the URL up to three
	 * times, each time checking if the URL Preview widget has loaded correctly.
	 * 
	 * PLEASE NOTE: This method does NOT post the status update - it merely types in the message and verifies that
	 * 				the URL preview widget has been displayed before posting
	 * 
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param isGlobalShareboxStatus - True if the status with URL is being entered into the global sharebox status update field, false otherwise
	 * @param isConnectionsOrVideoURL - True if the URL to be entered is a URL linking to a connections component (ie. an activity) or a YouTube video, false otherwise
	 * @param hasThumbnailImage - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @param verifyAddAFileLink - True if the 'Add a File' links behaviour is to be verified while posting the URL, false if these verifications are to be ignored
	 * @return - True if the widget has loaded correctly, false otherwise
	 */
	public boolean typeStatusUpdateWithURL(String statusMessageBeforeURL, String url, boolean isGlobalShareboxStatus, boolean isConnectionsOrVideoURL, boolean hasThumbnailImage, boolean verifyAddAFileLink) {
		
		// Enter the status update with URL into the status update input field
		enterStatusUpdateWithURL(statusMessageBeforeURL, url, isGlobalShareboxStatus, verifyAddAFileLink);
		
		// Switch focus back to the main frame
		switchToTopFrame();
		
		// If the status is being entered into the global sharebox - switch focus back to the global sharebox frame
		if(isGlobalShareboxStatus) {
			switchToGlobalShareboxFrame();
		}
		
		// Create the URL to be set to the CSS selector for the URL preview widget
		String urlInPreviewWidget;
		if(isConnectionsOrVideoURL) {
			// Truncate the URL - this only needs to be done when connections URLs or video URLs are being entered
			urlInPreviewWidget = url.substring(0, url.indexOf(".com") + 4);
			urlInPreviewWidget = urlInPreviewWidget.replaceAll("https://", "http://");
		} else {
			urlInPreviewWidget = url;
		}
		
		log.info("INFO: Verify that the URL preview widget is displayed after typing the URL into the status update input field");
		String urlPreview = HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST.replaceAll("PLACEHOLDER", urlInPreviewWidget);
		boolean urlPreviewLoaded = isElementVisible(urlPreview);
		
		int numberOfRetries = 0;
		while(!urlPreviewLoaded && numberOfRetries < 2) {
			
			log.info("INFO: The URL preview widget did not load correctly. Now re-trying to enter the URL");
			driver.switchToActiveElement().typeWithDelay(Keys.END);
			driver.switchToActiveElement().typeWithDelay(Keys.BACK_SPACE);
			driver.switchToActiveElement().typeWithDelay(Keys.BACK_SPACE);
			
			// Type the URL in again
			typeBeforeURLTextAndURL("", url);
			
			// Switch focus back to the main frame
			switchToTopFrame();
			
			// If the status is being entered into the global sharebox - switch focus back to the global sharebox frame
			if(isGlobalShareboxStatus) {
				switchToGlobalShareboxFrame();
			}
			
			log.info("INFO: Verify that the URL preview widget is displayed after typing the URL into the status update input field");
			urlPreviewLoaded = isElementVisible(urlPreview);
			numberOfRetries ++;
		}
		
		if(!urlPreviewLoaded) {
			log.info("ERROR: The URL preview widget did NOT appear after three attempts to enter a URL into the status update input field");
			return false;
		}
		
		if(verifyAddAFileLink) {
			log.info("INFO: Verify that the 'Add a File' link is NOT displayed now a URL has been entered into the status update input field");
			Assert.assertFalse(isElementVisible(HomepageUIConstants.AttachAFile),
								"ERROR: The 'Add a File' link was displayed after a URL had been entered into the status update input field");
		}
		
		if(hasThumbnailImage) {
			// Verify that all of the thumbnail image components for the URL preview widget are displayed
			return verifyThumbnailImageComponents(urlInPreviewWidget, false);
		} else {
			// Retrieve the element corresponding to the URL preview widget
			Element urlPreviewWidget = driver.getSingleElement(urlPreview);
			
			log.info("INFO: Verify that the thumbnail image is NOT displayed in the URL preview widget");
			Assert.assertFalse(urlPreviewWidget.getSingleElement(HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST_THUMBNAIL_IMAGE).isDisplayed(),
								"ERROR: The thumbnail image was unexpectedly displayed in the URL preview widget");
		}
		return true;
	}
	
	/**
	 * Types a status update with invalid URL into the status update input box.
	 * 
	 * PLEASE NOTE: This method does NOT post the status update - it merely types in the message and verifies that
	 * 				the URL preview widget has NOT been displayed
	 * 
	 * @param statusMessageBeforeInvalidURL - The String content of any text to appear before the invalid URL
	 * @param invalidURL - The invalid URL to be posted with the status message content
	 * @param isGlobalShareboxStatus - True if the status with invalid URL is being entered into the global sharebox status update field, false otherwise
	 * @param verifyAddAFileLink - True if the 'Add a File' links behaviour is to be verified while posting the URL, false if these verifications are to be ignored
	 * @return - True if all actions are completed successfully
	 */
	public boolean typeStatusUpdateWithInvalidURL(String statusMessageBeforeInvalidURL, String invalidURL, boolean isGlobalShareboxStatus, boolean verifyAddAFileLink) {
		
		// Enter the status update with invalid URL into the status update input field
		enterStatusUpdateWithURL(statusMessageBeforeInvalidURL, invalidURL, isGlobalShareboxStatus, verifyAddAFileLink);
		
		// Switch focus back to the main frame
		switchToTopFrame();
		
		// If the status is being entered into the global sharebox - switch focus back to the global sharebox frame
		if(isGlobalShareboxStatus) {
			switchToGlobalShareboxFrame();
		}
		
		log.info("INFO: Verify that the URL preview widget is NOT displayed after typing an invalid URL into the status update input field");
		String urlPreview = HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST.replaceAll("PLACEHOLDER", invalidURL);
		Assert.assertFalse(isElementVisible(urlPreview), 
							"ERROR: The URL preview widget was displayed after typing in an invalid URL into the status update input field");
		
		if(verifyAddAFileLink) {
			log.info("INFO: Verify that the 'Add a File' link is displayed after typing an invalid URL into the status update input field");
			Assert.assertTrue(isElementVisible(HomepageUIConstants.AttachAFile),
								"ERROR: The 'Add a File' link was NOT displayed after typing an invalid URL into the status update input field");
		}
		return true;
	}
	
	/**
	 * Re-usable method which enters the status message with URL appended into a generic status update input field.
	 * Also supports entering URL's into the status update input field of the global sharebox
	 * 
	 * @param statusMessageBeforeURL - The String content to appear before the URL
	 * @param url - The URL to be entered
	 * @param isGlobalShareboxStatus - True if the status is being entered into the global sharebox, false otherwise
	 * @param verifyAddAFileLink - True if the 'Add a File' link behaviour is to be verified before the URL is entered, false otherwise
	 * @return - The Element corresponding to the status update input field
	 */
	private Element enterStatusUpdateWithURL(String statusMessageBeforeURL, String url, boolean isGlobalShareboxStatus, boolean verifyAddAFileLink) {
		
		log.info("INFO: Now clicking into the status update input field");
		Element statusUpdateElement = getStatusUpdateElement();
		
		if(verifyAddAFileLink) {
			// Switch focus back to the main frame
			switchToTopFrame();
			
			// If the status is being entered into the global sharebox - switch focus back to the global sharebox frame
			if(isGlobalShareboxStatus) {
				switchToGlobalShareboxFrame();
			}
			
			log.info("INFO: Verify that the 'Add a File' link is displayed after clicking into the status update input field");
			Assert.assertTrue(isElementVisible(HomepageUIConstants.AttachAFile),
								"ERROR: The 'Add a File' link was NOT displayed after clicking into the status update input field");
		}
		
		log.info("INFO: Now entering a status update with content: " + statusMessageBeforeURL + " " + url);
		
		// Type the before URL text and then append the URL to the end
		typeBeforeURLTextAndURL(statusMessageBeforeURL, url);
		
		return statusUpdateElement;
	}
	
	/**
	 * Types the before URL text and URL text into the active input field
	 * 
	 * @param beforeURLText - The String content to appear before the URL
	 * @param url - The URL to be entered
	 */
	private void typeBeforeURLTextAndURL(String beforeURLText, String url) {
		
		// Create the WebDriver and Actions instances for entering the status update with URL
		WebDriver webDriver = (WebDriver) driver.getBackingObject();	
		Actions actions = new Actions(webDriver);
		
		if(beforeURLText.length() > 0) {
			log.info("INFO: Now entering the text to appear before the URL with content: " + beforeURLText);
			actions.sendKeys(beforeURLText + " ").perform();
		}
		log.info("INFO: Now entering the URL with content: " + url);
		actions.sendKeys(url + " ").perform();
	}
	
	/**
	 * Verifies all thumbnail image components for the URL Preview widget - including a check for whether the thumbnail image checkbox is selected / not selected
	 * 
	 * @param url - The URL used to generate the URL Preview widget
	 * @param checkboxIsSelected - True if the thumbnail image checkbox has been selected, False if it has not been selected
	 * @return - True if all checks are completed successfully
	 */
	private boolean verifyThumbnailImageComponents(String url, boolean checkboxIsSelected) {
		
		log.info("INFO: Verify that the thumbnail image checkbox is displayed with the URL Preview widget");
		String thumbnailImageCheckbox = HomepageUIConstants.URLPreview_ThumbnailCheckbox_Unique.replaceAll("PLACEHOLDER", url);
		Assert.assertTrue(fluentWaitElementVisible(thumbnailImageCheckbox), 
							"ERROR: The thumbnail image checkbox was NOT displayed with the URL preview widget");
		
		// Create the CSS selector for the URL preview widget in order to retrieve the thumbnail image component
		String urlPreviewWidgetCSSSelector = HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST.replaceAll("PLACEHOLDER", url);
		Element urlPreviewWidget = driver.getFirstElement(urlPreviewWidgetCSSSelector);
		
		// Retrieve the thumbnail image for the URL preview widget
		Element thumbnailImage = urlPreviewWidget.getSingleElement(HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST_THUMBNAIL_IMAGE);
		
		if(checkboxIsSelected) {
			log.info("INFO: Verify that the thumbnail image checkbox is selected (ie. it has been checked)");
			Assert.assertTrue(driver.getFirstElement(thumbnailImageCheckbox).isSelected(), 
								"ERROR: The thumbnail image checkbox was NOT selected (ie. it was NOT checked)");
			
			log.info("INFO: Verify that the thumbnail image is NOT displayed with the URL preview widget");
			Assert.assertFalse(thumbnailImage.isDisplayed(),
								"ERROR: The thumbnail image was displayed with the URL preview widget");
		} else {
			log.info("INFO: Verify that the thumbnail image checkbox is NOT selected (ie. it has not been checked)");
			Assert.assertFalse(driver.getFirstElement(thumbnailImageCheckbox).isSelected(), 
								"ERROR: The thumbnail image checkbox is selected (ie. it was checked)");
			
			log.info("INFO: Verify that the thumbnail image is displayed with the URL Preview widget");
			Assert.assertTrue(thumbnailImage.isDisplayed(),
								"ERROR: The thumbnail image is NOT displayed with the URL Preview widget");
		}
		return true;
	}
	
	/**
	 * Removes the thumbnail image preview from the URL preview widget
	 * 
	 * @param url - The URL used to generate the URL preview widget
	 * @return - True if all operations and verifications are performed correctly
	 */
	public boolean removeURLPreviewWidgetThumbnailImage(String url) {
		
		log.info("INFO: Now removing the thumbnail image from the URL preview widget before the status message is posted");
		
		// Verify that all thumbnail image components are displayed and that the checkbox is NOT selected before proceeding
		boolean allDisplayedAndUnchecked = verifyThumbnailImageComponents(url, false);
		
		log.info("INFO: Verifying that all checks for the thumbnail image components before removing the thumbnail image have completed successfully");
		Assert.assertTrue(allDisplayedAndUnchecked, 
							"ERROR: The checks for the thumbnail image components before removing the thumbnail image have failed");
		
		log.info("INFO: Now clicking on the thumbnail image checkbox to remove the thumbnail image from the URL preview widget");
		String thumbnailImageCheckbox = HomepageUIConstants.URLPreview_ThumbnailCheckbox_Unique.replaceAll("PLACEHOLDER", url);
		clickLinkWait(thumbnailImageCheckbox);
		
		// Verify that the thumbnail image is now removed and the checkbox selected
		return verifyThumbnailImageComponents(url, true);
	}
	
	/**
	 * Removes a URL preview widget by clicking on its 'X' icon
	 * A URL preview widget can only be removed via this method before it is posted to the activity stream
	 * 
	 * @param url - The URL used to generate the URL preview widget
	 * @param isGlobalShareboxStatus - True if the URL preview widget to be removed is part of a status message entered in the global sharebox, false otherwise
	 * @param verifyAddAFileLink - True if the 'Add a File' link behaviour is to be verified while removing the URL preview widget, false otherwise
	 */
	public boolean removeURLPreviewWidget(String url, boolean isGlobalShareboxStatus, boolean verifyAddAFileLink) {
		
		// Ensure that the focus is switched back to the appropriate frame before attempting to remove the URL preview widget
		switchToTopFrame();
		
		if(isGlobalShareboxStatus) {
			switchToGlobalShareboxFrame();
		}
		
		// Create the CSS selector for the URL preview widget
		String urlPreviewWidget = HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST.replaceAll("PLACEHOLDER", url);
		
		log.info("INFO: Verify that the URL preview widget is displayed before attempted removal");
		Assert.assertTrue(isElementPresent(urlPreviewWidget), 
							"ERROR: The URL preview widget was NOT displayed before any attempt was made to remove it");
		
		if(verifyAddAFileLink) {
			log.info("INFO: Verify that the 'Add a File' link is NOT displayed before the URL preview widget is removed");
			Assert.assertFalse(isElementVisible(HomepageUIConstants.AttachAFile),
								"ERROR: The 'Add a File' link was displayed before any attempt was made to remove the URL preview widget");
		}
		
		log.info("INFO: Now retrieving all elements associated with the URL preview removal CSS selector - only the visible element will be clickable");
		Element removeURLPreviewIcon = null;
		List<Element> listOfRemovalIcons = driver.getElements(HomepageUIConstants.URLPreview_Remove);
		
		log.info("INFO: Now searching through the list of URL preview removal icons to determine which icon is displayed");
		int index = 0;
		boolean foundClickableIcon = false;
		while(index < listOfRemovalIcons.size() && foundClickableIcon == false) {
			Element currentIcon = listOfRemovalIcons.get(index);
				
			if(currentIcon.isDisplayed()) {
				removeURLPreviewIcon = currentIcon;
				foundClickableIcon = true;
			}
			index ++;
		}
		
		if(removeURLPreviewIcon == null) {
			log.info("ERROR: A clickable removal 'X' icon for the URL preview widget could NOT be found");
			return false;
		}
		
		log.info("INFO: Now removing the URL preview widget by clicking on the 'X' icon");
		clickElement(removeURLPreviewIcon);
		
		log.info("INFO: Verify that the URL preview widget is NOT displayed after its removal");
		Assert.assertFalse(isElementVisible(urlPreviewWidget), 
							"ERROR: The URL preview widget was still displayed in the UI after removal");
		
		if(verifyAddAFileLink) {
			log.info("INFO: Verify that the 'Add a File' link is now displayed after the removal of the URL preview widget");
			Assert.assertTrue(isElementVisible(HomepageUIConstants.AttachAFile),
								"ERROR: The 'Add a File' link was NOT displayed after the removal of the URL preview widget");
		}
		return true;
	}
	
	/**
	 * Selects "a Community" from the list of drop-down meny items in the global sharebox
	 * 
	 * @param communityName - The name of the community to be selected
	 */
	public void selectGlobalShareboxCommunityDropDownMenuOption(String communityName) {
		
		log.info("INFO: Select 'a Community' from the drop-down options for the global sharebox");
		driver.getSingleElement(CommunitiesUIConstants.ShareBoxPostType).useAsDropdown().selectOptionByVisibleText("a Community");
		
		log.info("INFO: Enter the name of the community to select");
		driver.getSingleElement(CommunitiesUIConstants.ShareBoxCommunityPickerTextBox).type(communityName);
		driver.getSingleElement(CommunitiesUIConstants.ShareBoxCommunityPickerPopup).click();
	}
	
	/**
	 * Clicks on the 'Post' link / button for either a status update / global sharebox status update in order to post the update in the UI
	 * 
	 * @param isGlobalShareboxStatusMessage
	 */
	public void postStatusUpdate(boolean isGlobalShareboxStatusMessage) {
		
		if(isGlobalShareboxStatusMessage) {
			log.info("INFO: Switching focus back to the global sharebox frame");
			switchToGlobalShareboxFrame();
			
			log.info("INFO: Post the status update by clicking on the 'Post' button in the global sharebox");
			clickLinkWait(CommunitiesUIConstants.ShareBoxPost);
			
			log.info("INFO: Switching focus back to the main frame");
			switchToTopFrame();
		} else {
			log.info("INFO: Switching focus back to the main frame");
			switchToTopFrame();
			
			log.info("INFO: Now clicking on the 'Post' link to post the status message");
			clickLinkWait(HomepageUIConstants.PostComment);
		}
		
		log.info("INFO: Verify that the success message was displayed after posting the status update");
		Assert.assertTrue(fluentWaitTextPresent(Data.getData().postSuccessMessage), 
							"ERROR: The success message was NOT displayed after posting a status update");
	}
	
	/**
	 * Retrieves the handle value of the currently active browser window
	 * 
	 * @return - The String value of the current browser window handle
	 */
	public String getCurrentBrowserWindowHandle() {
		
		log.info("INFO: Now retrieving the browser window handle value for the currently active browser window");
		return driver.getWindowHandle();
	}
	
	/**
	 * Retrieves all of the handles for the currently active browser window
	 * 
	 * @return - The Set<String> of all browser window handles
	 */
	public Set<String> getAllBrowserWindowHandles() {
		
		log.info("INFO: Retrieving all currently open browser window handle values");
		return driver.getWindowHandles();
	}
	
	/**
	 * Switches to a browser window based on its handle value
	 * 
	 * @param browserWindowHandle - The String content of the browser window which is to be switched to
	 */
	public void switchToWindowByHandle(String browserWindowHandle) {
		
		log.info("INFO: Now switching to the browser window with handle value: " + browserWindowHandle);
		driver.switchToWindowByHandle(browserWindowHandle);
	}
	
	/**
	 * Switches to the first / next open browser window based on its handle value - only switches if it finds a handle value different to the current value provided
	 * 
	 * @param currentBrowserWindowHandle - The String content of the currently active window handle value
	 * @return - True if a new window is switched to successfully, false otherwise
	 */
	public boolean switchToNextOpenBrowserWindowByHandle(String currentBrowserWindowHandle) {
		
		// Retrieve all of the possible browser window handles
		Set<String> setOfBrowserWindows = getAllBrowserWindowHandles();
		
		// Switch to the first browser window that does NOT match to the current browser window
		Iterator<String> iterator = setOfBrowserWindows.iterator();
		boolean foundNextBrowserWindow = false;
		String browserWindowToSwitchTo = null;
		while(iterator.hasNext() && foundNextBrowserWindow == false) {
			String browserWindowHandle = iterator.next();
			
			log.info("INFO: Currently processing the browser window with handle: " + browserWindowHandle);
			if(!browserWindowHandle.equals(currentBrowserWindowHandle)) {
				log.info("INFO: Found a new browser window to switch to with handle: " + browserWindowHandle);
				browserWindowToSwitchTo = browserWindowHandle;
				foundNextBrowserWindow = true;
			}
		}
		
		if(foundNextBrowserWindow) {
			// Switch to the new browser window
			switchToWindowByHandle(browserWindowToSwitchTo);
			
			// Wait for the page to load before proceeding
			waitForPageLoaded(driver);
		} else {
			log.info("ERROR: Could not find a browser window with a handle value other than: " + currentBrowserWindowHandle);
			log.info(setOfBrowserWindows.toString());
		}
		return foundNextBrowserWindow;
	}
	
	/**
	 * Closes the currently active browser window
	 */
	public void closeCurrentBrowserWindow() {
		
		log.info("INFO: Now closing the currently active browser window");
		driver.close();
	}
	
	/**
	 * Resets the AS back to the top
	 */
	public void resetASToTop() {
		
		log.info("INFO: Now clicking at position (0, 0) in the view to reset the AS back to the top");
		driver.clickAt(0, 0);
	}
	
	/**
	 * Scroll to a story
	 */
	public void scrollToASStory(Element story) {
		log.info("INFO: Now scrolling to target story");
		driver.executeScript("arguments[0].scrollIntoView(true);", story.getWebElement());
	}

	/**
	 * 	This method works with status updates
	 * 
	 * 	Locates the entire news story element, hovers over it so as the "X" link to remove the status update
	 * 	will appear in the upper right corner of the story, and then clicks on the "X" to remove that status update.
	 * 	Also handles the confirmation of removal of the status update in the dialog box that appears once the
	 * 	"X" link has been clicked.
	 * 
	 * 	@param Status update content String
	 */
	public void deleteStatusUpdateUsingUI(String statusUpdate) {
		
		// Hover over the required news story in order for the 'X' removal icon for the status update to be displayed
		resetASToTopAndBringNewsStoryIntoViewAndHoverOverNewsStory(statusUpdate);
				
		log.info("INFO: Clicking on the 'X' removal link attached to the news story");
		String deleteNewsStoryLink = HomepageUIConstants.NewsStoryXLinkById.replaceAll("PLACEHOLDER", getNewsStoryId(statusUpdate));
		clickLink(deleteNewsStoryLink);
		
		log.info("INFO: Clicking on 'Delete' in the 'Confirm Removal' dialog box to confirm news story removal");
		clickLinkWait(HomepageUIConstants.DeleteStatusUpdateConfirm);
	}
	
	/**
	 * Resets the AS back to the top of the news feed, brings the required news item into view and then hovers over the news item to display the 'X' removal icons
	 * 
	 * @param newsStory - The String content of the news story to hover over in order to bring up the 'X' removal icons
	 */
	private void resetASToTopAndBringNewsStoryIntoViewAndHoverOverNewsStory(String newsStory) {
		// Bring the required news story into view
		Element newsStoryElement = resetASToTopAndBringNewsStoryIntoView(newsStory);
		
		log.info("INFO: Hovering over the news story element - required for the 'X' removal link to be correctly identified for both the news story and any inline comments");
		WebDriver webDriver = (WebDriver) driver.getBackingObject();	
		Actions builder = new Actions(webDriver);
		builder.moveToElement((WebElement) newsStoryElement.getBackingObject()).perform();
	}
	
	/**
	 * communitynavbarThis method works with any news story
	 * 
	 * Locates the entire news story element, hovers over it so as the "X" links to remove the displayed comments
	 * will appear in the upper right corner of each comment, and then clicks on the "X" to remove the specified comment.
	 * Also handles the confirmation of removal of the comment in the dialog box that appears once the
	 * "X" link has been clicked.
	 * 
	 * @param newsStory - The String content of the news story to which the comment to be deleted has been posted
	 * @param commentToBeDeleted - The String content of the comment to be deleted from the news story
	 */
	public void deleteNewsStoryCommentUsingUI(String newsStory, String commentToBeDeleted) {
		
		// Hover over the required news story in order for the 'X' removal icon for the comment to be displayed
		resetASToTopAndBringNewsStoryIntoViewAndHoverOverNewsStory(newsStory);
		
		log.info("INFO: Clicking on the 'X' removal link attached to the comment");
		String commentDeleteXIcon = replaceNewsStory(HomepageUIConstants.DeleteSUCommentXIcon, newsStory, commentToBeDeleted, null);
		clickLink(commentDeleteXIcon);
		
		log.info("INFO: Clicking on 'Delete' in the 'Confirm Removal' dialog box to confirm comment removal");
		clickLinkWait(HomepageUIConstants.DeleteStatusUpdateConfirm);
	}
	
	public void closeTooltip(){
		clickLinkWait(HomepageUIConstants.toolTipCloseButton);
	}
	
	/**
	 * Check for presence of the Tour Welcome Popup and close it. This popup can obscure Homepage links underneath so
	 * we need to be able to detect its presence and close it.
	 */
	public void dismissTourWelcomePopup() {
		log.info("INFO: Click X to close the Homepage Tour Welcome Popup if present");
		if (driver.isTextPresent(Data.getData().tourWelcomePopupText)) {
			clickLinkWait(HomepageUIConstants.tourWelcomePopupClose);
			log.info("INFO: Verify the Homepage Tour Welcome Popup was dismissed");
			Assert.assertTrue(driver.isTextNotPresent(Data.getData().tourWelcomePopupText), "ERROR: Tour Welcome Popup still displaying");
		}
	}
	
	public void verifyBizCard(Element userNamelink) {

		log.info("INFO: Hover over internal user name's link");
		userNamelink.hover();
		
		verifyBizCardContent(userNamelink.getText());
	}
	
	/**
	 * Verify business card should appear.
	 * Verify user's name displayed on business card is not a clickable link.
	 * Verify Expected user name and User name displayed on business card is same as in param.
	 * 
	 * @param userName - The String contain the name of the user.
	 */
	public void verifyBizCardContent(String userName) {
		// Verify business card should appear
		if(!cfg.getUseNewUI())
		{
			log.info("INFO: Verify business card should appear");
			isElementPresentWd(By.cssSelector(HomepageUIConstants.bizCard));
			waitForElementVisibleWd(createByFromSizzle(HomepageUIConstants.bizCard),3);
			WebElement bizCard = findElement(createByFromSizzle(HomepageUIConstants.bizCard));
			Assert.assertTrue(bizCard.isDisplayed());

			// Verify user's name displayed on business card is not a click-able link
			log.info("INFO: Verify user's name displayed on business card is not a clickable link");
			WebElement bizCardUserName = findElement(createByFromSizzle(HomepageUIConstants.bizCardUsersName));
			Assert.assertFalse(bizCardUserName.getTagName().equals("a"));

			driver.turnOffImplicitWaits();
			List<WebElement> cardHeader = findElements(By.cssSelector("#cardTable div[class='lotusPersonInfo'] h2 span>a"));
			driver.turnOffImplicitWaits();
			log.info("Size is:"+cardHeader.size());
			Assert.assertTrue(cardHeader.isEmpty());

			// Verify correct business card for the respective user should be displayed
			log.info("INFO: Verify correct business card for the user "+ userName +" should be displayed");
			log.info("INFO: Expected user name is " + userName + ". User name displayed on business card is " + bizCardUserName.getText());
			Assert.assertTrue(userName.replace("@", "").equals(bizCardUserName.getText()));
		}
		else
		{
			log.info("INFO: Verify new UI business card");
			waitForElementsVisibleWd(createByFromSizzle(HomepageUIConstants.bizCard), 5);
			isElementDisplayedWd(createByFromSizzle(HomepageUIConstants.bizCard));
			isElementDisplayedWd(createByFromSizzle(HomepageUIConstants.bizCardUserName));
			isElementDisplayedWd(createByFromSizzle(HomepageUIConstants.bizCardUserEmail));
			isElementDisplayedWd(By.cssSelector(HomepageUIConstants.bizCardPhoneNumber));
			isElementDisplayedWd(By.cssSelector(HomepageUIConstants.bizCardChatIcon));
			isElementDisplayedWd(By.cssSelector(HomepageUIConstants.bizCardProfilePicture));
			isElementDisplayedWd(By.cssSelector(HomepageUIConstants.bizCardVerticalEllipsisIcon));
			isElementDisplayedWd(By.cssSelector(HomepageUIConstants.bizCardEmailIcon));

		}
		// Dismiss hovering state of business card by moving focus to Home icon
		/*
		 * log.
		 * info("INFO: Dismiss hovering state of business card by moving focus to home icon"
		 * ); Element home_Icon = driver.getFirstElement(HomepageUI.HomeIcon);
		 * home_Icon.hover();
		 */
	}
	
	public void switchTabs(int expectedWindowsCount, int SwitchtoWindow) throws Exception {
		WebDriver wd = (WebDriver) driver.getBackingObject();
		(new WebDriverWait(wd, 3)).until(ExpectedConditions.numberOfWindowsToBe(expectedWindowsCount));

		log.info("INFO: Verify that clicked link now opened in new tab ");
		Assert.assertEquals(expectedWindowsCount, 2);

		ArrayList<String> tabs2 = new ArrayList<String>(wd.getWindowHandles());
		wd.switchTo().window(tabs2.get(SwitchtoWindow));
	}

	public void verifyLinkOpenInNewBrowser(String entryLinkToOpen, String expectedPageTitle, String waitForHeader)
			throws Exception {
		log.info("INFO: Select entry link");
		clickLinkWithJavascript(entryLinkToOpen);

		log.info("INFO: Verify user switches to new tab after selecting entry link");
		switchTabs(2, 1);
		waitForPageLoaded(driver);
		scrollIntoViewElement(waitForHeader);
		fluentWaitPresent(waitForHeader);

		log.info("INFO: Verify that user navigated to the correct page");
		Assert.assertEquals(driver.getTitle(), expectedPageTitle,
				"Error: User is not navigated to new browser tab after clicking link");
	}
	
	
	 
	 
	 /* Verify Business Card for Internal User
	  * @param - Invited user
	 */
	 
	public void verifyBizCardContentForInternalUser(User userName) {
		// Verify business card should appear
		HomepageUIConstants.userSurNamelink= HomepageUIConstants.userSurNamelink.replace("PLACEHOLDER", userName.getLastName());
		log.info("name is -- " + HomepageUIConstants.userSurNamelink);
		Element userSurName = driver.getFirstElement(HomepageUIConstants.userSurNamelink);
		userSurName.hover();
		
		log.info("INFO: Verify business card should appear");
		this.fluentWaitElementVisible(HomepageUIConstants.bizCard);
		Element bizCard = driver.getFirstElement(HomepageUIConstants.bizCard);
		Assert.assertTrue(bizCard.isDisplayed());

		// Verify user's name displayed on business card is not a click-able link
		log.info("INFO: Verify user's name displayed on business card is not a clickable link");
		Element bizCardUserName = driver.getFirstElement(HomepageUIConstants.bizCardUserName);
		Assert.assertEquals(bizCardUserName.getText(),userName.getDisplayName());
		
		log.info("INFO: Verify user's name displayed on business card is not a clickable link");
		Element bizCardUserEmail = driver.getFirstElement(HomepageUIConstants.bizCardUserEmail);
		Assert.assertEquals(bizCardUserEmail.getText(),userName.getEmail());
		
		scrollIntoViewElement(HomepageUIConstants.bizCardProfileLink);
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.bizCardProfileLink).isDisplayed());
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.bizCardCommunitiesLink).isDisplayed());
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.bizCardActivitiesLink).isDisplayed());
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.bizCardforumsLink).isDisplayed());
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.bizCardFilesLink).isDisplayed());
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.bizCardWikisLink).isDisplayed());
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.bizCardBookmarksLink).isDisplayed());
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.bizCardBlogsLink).isDisplayed());

	}


	/**
	 * Validate For Me and From Me tab to the My Notifications view in the UI
	 */
	public void validateforandfromme() {
		if (!cfg.getUseNewUI()) {

			clickLinkWaitWd(createByFromSizzle(HomepageUIConstants.ForMeTab), 3, "Click on For Me Tab");
			Assert.assertTrue(isElementVisibleWd(createByFromSizzle(HomepageUIConstants.ForMeTab), 5),"Verify ForMe Tab is displayed");
			clickLinkWaitWd(createByFromSizzle(HomepageUIConstants.FromMeTab), 3, "Click on Form Me Tab");
			Assert.assertTrue(isElementVisibleWd(createByFromSizzle(HomepageUIConstants.FromMeTab), 5),"Verify From Me Tab is displayed");

		}

	}
	
	public void selectHomepageMenu(String menuToSelect) {
		if (!cfg.getUseNewUI()) {
			switch (menuToSelect) {
			case "Action Required":
				clickLinkWaitWd(createByFromSizzle(HomepageUIConstants.Ckpt_ActionRequired), 3);
				break;
			case "Saved" :
				clickLinkWaitWd(createByFromSizzle(HomepageUIConstants.Ckpt_Saved), 3);
				break;
			}

		} else {
			clickLinkWaitWd(By.xpath(HomepageUIConstants.personalFilterBtn),3);

			switch (menuToSelect) {
			case "I'm Following":
				clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesImFollowingFilter),3);
				break;

			case "Mentions":
				clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesCategoriesMentionsFilter),3);
				break;

			case "My Notifications":
				clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesMyNotificationsFilter),3);
				break;

			case "Action Required":
				clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesActionRequiredFilter),3);
				break;

			case "Saved":
				clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesCategoriesSavedFilter),3);
				break;
			default:
				throw new IllegalArgumentException();
			}
		}
	}
	
	/**
	 * Select the Language to be switched in
	 * @param: Choosen language will be used as parameter
	 * @return: It will return the xpath/locator for the selected language element
	 */
	
	public static String switchToLanguage(String language) {
		return "//td[contains(text(),'"+language+"')]";
	}
	
	/**
	 * Select and click Language from Language Selector
	 * @param: Choosen language will be used as parameter
	 */
	public void selecteLanguage(String lang) {
		if(cfg.getUseNewUI()) {
			clickLinkWithJavaScriptWd(findElement(By.xpath(HomepageUIConstants.navBarLanguageSelectorDropdown)));
		}else {
			clickLinkWaitWd(By.xpath(HomepageUIConstants.lsDropdown), 5, "Click on Language Selector Dropdown");	
		}		
		switchToLanguage(lang);
		waitForElementVisibleWd(By.xpath(switchToLanguage(lang)), 3);
		clickLinkWaitWd(By.xpath(switchToLanguage(lang)), 3,
				"Click on any specfic language Option from dropdown");
		waitForPageLoaded(driver);

	}
	
	/**
	 * Validate the position any Element on different languages
	 * @param: Selected Element and language will be used as parameter
	 * 
	 */
	public void validateElementPosition(WebElement element, String lang) {
		
		int winHeight = ((WebDriver)driver.getBackingObject()).manage().window().getSize().getHeight();
		int winWidth =  ((WebDriver)driver.getBackingObject()).manage().window().getSize().getWidth();
		
		log.info("INFO: Get the element's x-cordination, y-cordination , height and weight against browser's screen resolution");
		int xPos = element.getLocation().getX();
		int yPos = element.getLocation().getY();
		int eleHeight = element.getSize().getHeight();
		int eleWidth = element.getSize().getWidth();
		
		log.info("INFO: Verify the position of the element based on the selected lanuage");
		if (lang == "English") {
			Assert.assertTrue(((xPos + eleWidth) <= winWidth / 2) && ((yPos + eleHeight) <= winHeight / 2),
					"Logo is in the upper left quadrant");
		}
		if (lang == "Arabic") {
			Assert.assertFalse(((xPos + eleWidth) <= winWidth / 2) && ((yPos + eleHeight) <= winHeight / 2),
					"Logo is in the upper right quadrant");

		}
	

	}

}
