package com.ibm.conn.auto.util.eventBuilder.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * @author 	Anthony Cox
 * Date:	8th March 2016
 */

public class UIEvents {

	private static Logger log = LoggerFactory.getLogger(UIEvents.class);
	
	/**
	 * Logs in as the specified user and creates and follows a new tag using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke the various methods used in this events method
	 * @param driver - The RCLocationExecutor instance to invoke the close() method to close the browser instance
	 * @param userToFollowTag - The User instance of the user who will follow the tag
	 * @param tagToFollow - The tag which is to be created and followed by the user
	 */
	public static void followTag(HomepageUI ui, RCLocationExecutor driver, User userToFollowTag, String tagToFollow) {
		
		// Log in to Homepage as the specified user
		LoginEvents.loginAndGotoImFollowing(ui, userToFollowTag, false);
		
		log.info("INFO: Filter by 'Tags'");
		ui.filterBy(HomepageUIConstants.FilterTags);

		log.info("INFO: " + userToFollowTag.getDisplayName() + " will now follow the tag: " + tagToFollow);
		ui.followTag(tagToFollow);
		
		log.info("INFO: " + userToFollowTag.getDisplayName() + " will now log out and the browser instance closed");
		ui.logout();
		driver.close();
	}
	
	/**
	 * Refreshes the current page
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke the navigate().refresh() method
	 */
	public static void refreshPage(RCLocationExecutor driver) {
		
		log.info("INFO: Now refreshing the current page");
		driver.navigate().refresh();
	}
	
	/**
	 * Navigates to the Home screen in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoHome(HomepageUI ui) {
		
		log.info("INFO: Navigating to the Home screen in the UI");
		ui.gotoHome();
	}
	
	/**
	 * Navigates to the I'm Following view in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoImFollowing(HomepageUI ui) {
		
		log.info("INFO: Navigating to the I'm Following view");
		ui.gotoImFollowing();
	}
	
	/**
	 * Navigates to the Discover view in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoDiscover(HomepageUI ui) {
		
		log.info("INFO: Navigating to the Discover view");
		ui.gotoDiscover();
	}
	
	/**
	 * Navigates to the Status Updates view in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoStatusUpdates(HomepageUI ui) {
		
		log.info("INFO: Navigating to the Status Updates view");
		ui.gotoStatusUpdates();
	}
	
	/**
	 * Navigates to the My Notifications view in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoMyNotifications(HomepageUI ui) {
		
		log.info("INFO: Navigating to the My Notifications view");
		ui.gotoMyNotifications();
	}
	
	/**
	 * Navigates to the My Page view in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoMyPage(HomepageUI ui) {
		
		log.info("INFO: Navigating to the My Page view");
		ui.clickLinkWait(HomepageUIConstants.MyPage);
	}
	
	/**
	 * Navigates to the Getting Started view in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoGettingStarted(HomepageUI ui) {
		
		log.info("INFO: Navigating to the Getting Started view");
		ui.gotoGettingStarted();
	}
	
	/**
	 * Navigates to the Mentions view in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoMentions(HomepageUI ui) {
		
		log.info("INFO: Navigating to the Mentions view");
		ui.gotoMentions();
	}
	
	/**
	 * Navigates to the Action Required view in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoActionRequired(HomepageUI ui) {
		
		log.info("INFO: Navigating to the Action Required view");
		ui.gotoActionRequired();
	}
	
	/**
	 * Navigates to the Saved view in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoSaved(HomepageUI ui) {
		
		log.info("INFO: Navigating to the Saved view");
		ui.gotoSaved();
	}
	
	/**
	 * Navigates to the My Profile view in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoMyProfile(HomepageUI ui) {
		
		log.info("INFO: Navigating to the My Profile view");
		ui.gotoProfile();
		
		log.info("INFO: Waiting for the My Profile view to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
	}
	
	/**
	 * Navigates to the Home screen and then to the I'm Following view
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoHomeAndGotoImFollowing(HomepageUI ui) {
		
		// Go to the Home screen
		gotoHome(ui);
		
		// Go to the I'm Following view
		gotoImFollowing(ui);
	}
	
	/**
	 * Navigates to the Home screen and then to the Status Updates view
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoHomeAndGotoStatusUpdates(HomepageUI ui) {
		
		// Go to the Home screen
		gotoHome(ui);
		
		// Go to the Status Updates view
		gotoStatusUpdates(ui);
	}
	
	/**
	 * Navigates to the 'From Me' tab in the My Notifications view
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void gotoFromMe(HomepageUI ui) {
		
		// Go to the 'From Me' tab in the UI
		ui.clickLinkWait(HomepageUIConstants.FromMeTab);
	}
	
	/**
	 * Retrieves the current badge number from the Action Required badge
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke the relevant methods
	 * @return - The integer value of the My Notifications badge
	 */
	public static int getActionRequiredBadgeValue(RCLocationExecutor driver) {
		
		log.info("INFO: Retrieving the Action Required badge value");
		return getBadgeValue(driver, HomepageUIConstants.ActionRequiredBadge);
	}
	
	/**
	 * Retrieves the current badge number from the My Notifications badge
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke the relevant methods
	 * @return - The integer value of the My Notifications badge
	 */
	public static int getMyNotificationsBadgeValue(RCLocationExecutor driver) {
		
		log.info("INFO: Retrieving the My Notifications badge value");
		return getBadgeValue(driver, HomepageUIConstants.MyNotificationBadge);
	}
	
	/**
	 * Retrieves the current badge number from the Notification Center badge
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke the relevant methods
	 * @return - The integer value of the Notification Center badge
	 */
	public static int getNotificationCenterBadgeValue(RCLocationExecutor driver) {
		
		log.info("INFO: Retrieving the Notification Center badge value");
		return getBadgeValue(driver, HomepageUIConstants.NotificationCenter_Badge);
	}
	
	/**
	 * Retrieves the current badge number from the Mentions badge
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke the relevant methods
	 * @return - The integer value of the Mentions badge
	 */
	public static int getMentionsBadgeValue(RCLocationExecutor driver) {
		
		log.info("INFO: Retrieving the Mentions badge value");
		return getBadgeValue(driver, HomepageUIConstants.MentionsBadge);
	}
	
	/**
	 * Opens the EE for a news story in the activity stream
	 * 
	 * @param ui - The HomepageUI instance to invoke the filterNewsItemOpenEE() method
	 * @param newsStory - The news story corresponding to the story for which the EE will be opened
	 */
	public static void openEE(HomepageUI ui, String newsStory) {
		
		// Click on the 'Show More' link before searching the news feed for the news item
		clickShowMore(ui);
				
		log.info("INFO: Opening the EE for the news story with content: " + newsStory);
		ui.openNewsStoryEE(newsStory);
	}
	
	/**
	 * Switches to the generic EE frame
	 * 
	 * @param ui - The HomepageUI instance to invoke the switchToEEFrame() method
	 */
	public static void switchToEEFrame(HomepageUI ui) {
		
		// Switch focus to the EE frame
		ui.switchToEEFrame();
	}
	
	/**
	 * Switches to the comments frame in the EE (true value) or the replies frame in the EE (false value)
	 * 
	 * @param ui - The HomepageUI instance to invoke the switchToEECommentOrRepliesFrame() method
	 * @param switchToCommentsFrame - True if the comments tab / frame is to be switched to, false if the replies tab / frame is to be switched to
	 */
	public static void switchToEECommentOrRepliesFrame(HomepageUI ui, boolean switchToCommentsFrame) {
		
		// Switch to either the comments frame or the replies frame
		ui.switchToEECommentOrRepliesFrame(switchToCommentsFrame);
	}
	
	/**
	 * Switches to the top / main frame
	 * 
	 * @param ui - The HomepageUI instance to invoke the switchToTopFrame() method
	 */
	public static void switchToTopFrame(HomepageUI ui) {
		
		// Switch focus to the top / main frame
		ui.switchToTopFrame();
	}
	
	/**
	 * Switches to the status update frame
	 * 
	 * @param ui - The HomepageUI instance to invoke the getStatusUpdateElement() method
	 */
	public static void switchToStatusUpdateFrame(HomepageUI ui) {
		
		log.info("INFO: Now switching focus to the status update input field");
		ui.getStatusUpdateElement();
	}
	
	/**
	 * Switches to the comment frame of the specified status update
	 * 
	 * @param ui - The HomepageUI instance to invoke the switchToStatusUpdateCommentFrame() method
	 * @param statusUpdate - The String content of the status update whose comment frame is to be focused on
	 */
	public static void switchToStatusUpdateCommentFrame(HomepageUI ui, String statusUpdate) {
		
		// Switch focus to the comment frame of the specified status update
		ui.switchToStatusUpdateCommentFrame(statusUpdate);
	}
	
	/**
	 * Opens the EE and switches the focus to the comments / replies frame in the EE
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the story for which the EE will be opened
	 * @param switchToCommentsFrame - True if the comments tab / frame is to be switched to, false if the replies tab / frame is to be switched to
	 */
	public static void openEEAndSwitchToEECommentOrRepliesFrame(HomepageUI ui, String newsStory, boolean switchToCommentsFrame) {
		
		// Open the EE by clicking on the specified news story
		openEE(ui, newsStory);
		
		// Switch to either the comments frame or the replies frame
		switchToEECommentOrRepliesFrame(ui, switchToCommentsFrame);
	}
	
	/**
	 * Clicks on the 'Post' link in the EE to post the currently entered comment / reply
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickLinkWait() method
	 */
	public static void postEECommentOrReply(HomepageUI ui) {
		
		log.info("INFO: Now clicking on the 'Post' link in the EE to post the comment / reply");
		ui.clickLinkWait(HomepageUIConstants.OpenEEPostCommentButton);
	}
	
	/**
	 * Clicks on the 'Cancel' link in the EE to cancel the currently entered comment / reply
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickLinkWait() method
	 */
	public static void cancelEECommentOrReply(HomepageUI ui) {
		
		log.info("INFO: Now clicking on the 'Cancel' link in the EE to cancel the comment / reply");
		ui.clickLinkWait(HomepageUIConstants.OpenEECancelCommentButton);
	}
	
	/**
	 * Clears the currently displayed status update input field by clicking on the 'Clear' link
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void clearStatusUpdate(HomepageUI ui) {
		
		// Ensure that the focus has switched back to the top frame
		switchToTopFrame(ui);
		
		// Reset the view back to the very top - this will also hide any other UI components such as typeahead menus or the EE
		resetASToTop(ui);
		
		log.info("INFO: Now clicking on the 'Clear' link in the UI to clear the status update entry");
		ui.clickLinkWait(HomepageUIConstants.ClearStatusUpdate);
	}
	
	/**
	 * Switches focus to the EE frame and then clicks on the 'Post' link to post the currently entered comment or reply
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void switchToEEFrameAndPostCommentOrReply(HomepageUI ui) {
		
		// Switch focus to the EE frame
		switchToEEFrame(ui);
		
		// Click on the 'Post' link to post the comment / reply entered in the EE
		postEECommentOrReply(ui);
	}
	
	/**
	 * Switches focus to the EE frame and then clicks on the 'Cancel' link to cancel the currently entered comment or reply
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void switchToEEFrameAndCancelCommentOrReply(HomepageUI ui) {
		
		// Switch focus to the EE frame
		switchToEEFrame(ui);
		
		// Click on the 'Cancel' link to cancel the comment / reply entered in the EE
		cancelEECommentOrReply(ui);
	}
	
	/**
	 * Switches the focus back to the main frame and waits for the typeahead menu to appear
	 * 
	 * @param ui - The HomepageUI instance to invoke the fluentWaitPresent() method
	 */
	public static void waitForTypeaheadMenuToLoad(HomepageUI ui) {
		
		// Wait for the typeahead menu to load
		ui.waitForTypeaheadMenuToLoad();
	}
	
	/**
	 * Switches the focus back to the EE frame and waits for the typeahead menu to appear
	 * 
	 * @param ui - The HomepageUI instance to invoke the relevant methods
	 */
	public static void waitForEETypeaheadMenuToLoad(HomepageUI ui) {
		
		// Wait for the typeahead menu in the EE to load
		ui.waitForEETypeaheadMenuToLoad();
	}
	
	/**
	 * Switches the focus back to the Global Sharebox frame and waits for the typeahead menu to appear
	 * 
	 * @param ui - The HomepageUI instance to invoke the relevant methods
	 */
	public static void waitForGlobalShareboxTypeaheadMenuToLoad(HomepageUI ui) {
		
		// Wait for the typeahead menu in the EE to load
		ui.waitForGlobalShareboxTypeaheadMenuToLoad();
	}
	
	/**
	 * Enters the before mentions string from a mention
	 * 
	 * @param ui - The HomepageUI instance to invoke the typeBeforeMentionsString() method
	 * @param mentions - The Mentions instance of the mention to be entered
	 */
	public static void typeBeforeMentionsText(HomepageUI ui, Mentions mentions) {
		
		// Type in the before mentions text
		ui.typeBeforeMentionsString(mentions);
	}
	
	/**
	 * Enters the mentions string from a mention
	 * 
	 * @param mentions - The Mentions instance of the mention to be entered
	 * @param numberOfCharactersToType - The number of user name characters to type (supporting partial mentions)
	 */
	public static void typeMentionsOrPartialMentions(HomepageUI ui, Mentions mentions, int numberOfCharactersToType) {
		
		// Type in the mentions / partial mentions to the user
		ui.typeMentions(mentions, numberOfCharactersToType);
	}
	
	/**
	 * Enters the after mentions text
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 */
	public static void typeAfterMentionsText(HomepageUI ui, Mentions mentions) {
		
		// Type in the after mentions text
		ui.typeAfterMentionsString(mentions);
	}
	
	/**
	 * Retrieves just the first menu item from the typeahead menu
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @return - The List<Element> instance of all menu item elements
	 */
	public static List<Element> getFirstTypeaheadMenuItem(HomepageUI ui, RCLocationExecutor driver) {
		
		// Retrieve just the first menu item from the typeahead
		return getTypeaheadMenuItemsList(ui, driver, true);
	}
	
	/**
	 * Retrieves all menu items from the visible typeahead menu
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @return - The List<Element> instance of all menu item elements
	 */
	public static List<Element> getTypeaheadMenuItemsList(HomepageUI ui, RCLocationExecutor driver) {
		
		// Retrieve all of the menu items from the typeahead menu
		return getTypeaheadMenuItemsList(ui, driver, false);
	}
	
	/**
	 * Retrieves and selects the first menu item from the visible typeahead menu 
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @return - The user name of the user that was selected from the typeahead
	 */
	public static String getFirstTypeaheadMenuItemAndSelectUser(HomepageUI ui, RCLocationExecutor driver) {
		
		// Retrieve just the first menu item from the typeahead
		List<Element> listContainingFirstElement = getFirstTypeaheadMenuItem(ui, driver);
		
		log.info("INFO: Selecting the first menu item element with text: " + listContainingFirstElement.get(0).getText());
		Element firstElementInTypeahead = listContainingFirstElement.get(0);
		String firstElementText = firstElementInTypeahead.getText();
		firstElementInTypeahead.click();
		
		return ui.getUserNameFromTypeaheadMenuItemElementText(firstElementText);
	}
	
	/**
	 * Retrieves the entire menu items list from the typeahead and selects the appropriate user to be mentioned
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 */
	public static void getTypeaheadMenuItemsListAndSelectUser(HomepageUI ui, RCLocationExecutor driver, Mentions mentions) {
		// Retrieve the list of all menu items from the typeahead
		List<Element> listOfMenuItems = getTypeaheadMenuItemsList(ui, driver);
		
		// Retrieve the menu item to be selected from the list of menu items
		Element menuItemToBeSelected = ui.getMenuItemContainingUserToBeMentioned(listOfMenuItems, mentions);
		
		log.info("INFO: Verify that the menu item containing the user name '" + mentions.getUserToMention().getDisplayName() + "' was found successfully");
		Assert.assertNotNull(menuItemToBeSelected, 
								"ERROR: The menu item containing the user name could not be found and was returned as null");
		
		log.info("INFO: Now selecting the menu item containing the user to be mentioned");
		menuItemToBeSelected.doubleClick();
	}
	
	/**
	 * Retrieves the full list of typeahead menu items and retrieves the text content from each
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @return - An ArrayList<String> with each item containing the text content from a menu item
	 */
	public static ArrayList<String> getTypeaheadMenuItemsAsText(HomepageUI ui, RCLocationExecutor driver) {
		
		// Retrieve the full list of typeahead menu items from the typeahead menu
		List<Element> listOfMenuItems = getTypeaheadMenuItemsList(ui, driver);
		
		log.info("INFO: Retrieve the text content from each menu item");
		return ui.getTypeaheadUsersListTextContents(listOfMenuItems);
	}
	
	/**
	 * Retrieves the full list of typeahead menu items mapped to the photo for each item
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @return - A HashMap of the menu item Element instances mapped to their corresponding photo Element instances
	 */
	public static HashMap<Element, Element> getTypeaheadMenuItemsAndPhotos(HomepageUI ui, RCLocationExecutor driver) {
		
		// Retrieve the full list of typeahead menu items from the typeahead menu
		List<Element> listOfMenuItems = getTypeaheadMenuItemsList(ui, driver);
		
		// Retrieve the photo element associated with each menu item
		return ui.getTypeaheadMenuItemPhotos(listOfMenuItems);
	}
	
	/**
	 * Types the before mentions text and the partial mentions text (customisable amount of characters via the 'numberOfCharactersToType' parameter)
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param numberOfCharactersToType - The number of characters to be typed in the partial mention
	 */
	public static void typeBeforeMentionsTextAndTypePartialMentions(HomepageUI homepageUI, Mentions mentions, int numberOfCharactersToType) {
		
		// Type in the before mentions text
		typeBeforeMentionsText(homepageUI, mentions);
		
		// Type in the partial mentions text
		typeMentionsOrPartialMentions(homepageUI, mentions, numberOfCharactersToType);
	}
	
	/**
	 * Types the before mentions text and the mentions text
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 */
	public static void typeBeforeMentionsTextAndTypeMentions(HomepageUI homepageUI, Mentions mentions) {
		
		// Type in the before mentions text
		typeBeforeMentionsText(homepageUI, mentions);
		
		// Type in the mentions text
		typeMentionsOrPartialMentions(homepageUI, mentions, mentions.getUserToMention().getDisplayName().length());
	}
	
	/**
	 * Enters the before mentions text and a mention to the specified user and selects the mentioned user from the typeahead
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 */
	public static void typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUser(HomepageUI ui, RCLocationExecutor driver, Mentions mentions) {
		
		// Type in the before mentions text and mentions text
		typeBeforeMentionsTextAndTypeMentions(ui, mentions);
		
		// Wait for the typeahead menu to load
		waitForTypeaheadMenuToLoad(ui);
		
		// Get the list of menu items from the typeahead menu and select the appropriate user
		getTypeaheadMenuItemsListAndSelectUser(ui, driver, mentions);
	}
	
	/**
	 * Enters the before mentions text and a mention to the specified user and selects the mentioned user from the directory search results for MT environment
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 */
	public static void typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUserMT(HomepageUI ui, RCLocationExecutor driver, Mentions mentions, User usermentionedInStatus) {
		
		// Type in the before mentions text and mentions text
		typeBeforeMentionsTextAndTypeMentions(ui, mentions);
		
		// Wait for the type-ahead menu to load
		waitForTypeaheadMenuToLoad(ui);
		
		// Select directory search link
		ui.clickLinkWithJavascript(HomepageUIConstants.nameSearchList);
		ui.fluentWaitPresent("css=div[id='lconn_core_PeopleTypeAheadMenu_00']:contains("+usermentionedInStatus.getEmail()+")");
		
		// Get the list of menu items from the search directory results menu and select the appropriate user
		getTypeaheadMenuItemsListAndSelectUser(ui, driver, mentions);
	}
	
	/**
	 * Enters the before mentions text and a mention to the specified user and selects the mentioned user from the typeahead using the EE
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 */
	public static void typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUserInEE(HomepageUI ui, RCLocationExecutor driver, Mentions mentions) {
		
		// Type in the before mentions text and mentions text
		typeBeforeMentionsTextAndTypeMentions(ui, mentions);
		
		// Wait for the EE typeahead menu to load
		waitForEETypeaheadMenuToLoad(ui);
		
		// Get the list of menu items from the typeahead menu and select the appropriate user
		getTypeaheadMenuItemsListAndSelectUser(ui, driver, mentions);
	}
	
	/**
	 * Enters the before mentions text and a mention to the different org user / guest user and verifies that the typeahead menu
	 * does NOT appear (ie. no users should be selectable)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 */
	public static void typeMentionsToDifferentOrgUserAndGuestUser(HomepageUI ui, RCLocationExecutor driver, Mentions mentions) {
		
		// Type in the before mentions text and mentions text
		typeBeforeMentionsTextAndTypeMentions(ui, mentions);
		
		// Switch focus back to the top frame
		switchToTopFrame(ui);
		
		log.info("INFO: Ensuring that the typeahead menu is NOT visible having entered a different org / guest user");
		Assert.assertFalse(ui.isElementVisible(HomepageUIConstants.MentionsTypeaheadSelection), 
							"ERROR: The typeahead menu was displayed despite a mentions to a different org / guest user being entered");
	}
	
	/**
	 * Presses the BACK SPACE key on the keyboard to delete a mentions link and verify that the mentions link is removed (and not just the end character)
	 * PLEASE NOTE: The cursor must be positioned at the end of the mentions link for this method to function correctly
	 * 
	 * @param driver - The RCLocationExecutor instance used to invoke the switchToActiveElement() method
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userMentioned - The user name of the user that was mentioned
	 * @return - True if the deletion operation is successful and all actions are executed successfully
	 */
	public static boolean deleteMentionsLinkWithBackspaceKey(RCLocationExecutor driver, HomepageUI ui, String userMentioned) {
		
		// Use the BACKSPACE key to delete the mentions link by striking it twice
		useBackspaceKeyInCurrentActiveElement(driver, 2);
		
		// Verify that the mentions link is no longer displayed after deleting it using the backspace key
		verifyMentionsLinkHasBeenDeletedInCurrentActiveElement(ui, driver, userMentioned);
		
		return true;
	}
	
	/**
	 * Presses the BACK SPACE key on the keyboard to delete two mentions links and verifies that neither mentions link is displayed
	 * PLEASE NOTE: The cursor must be positioned at the end of the second mentions link for this method to function correctly
	 * 
	 * @param driver - The RCLocationExecutor instance used to invoke the switchToActiveElement() method
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param firstMentionedUserName - The user name of the first user that was mentioned
	 * @param secondMentionedUserName - The user name of the second user that was mentioned
	 * @return - True if the deletion operation is successful and all actions are executed successfully
	 */
	public static boolean deleteTwoMentionsLinksWithBackspaceKey(RCLocationExecutor driver, HomepageUI ui, String firstMentionedUserName, String secondMentionedUserName) {
		
		// Use the BACKSPACE key to delete all mentions links by striking it five times
		useBackspaceKeyInCurrentActiveElement(driver, 5);
				
		// Verify that the mentions link for the first user is no longer displayed after deleting it using the backspace key
		verifyMentionsLinkHasBeenDeletedInCurrentActiveElement(ui, driver, firstMentionedUserName);
		
		// Verify that the mentions link for the second user is no longer displayed after deleting it using the backspace key
		verifyMentionsLinkHasBeenDeletedInCurrentActiveElement(ui, driver, secondMentionedUserName);
				
		return true;
	}
	
	/**
	 * Highlights all of a mentions link and deletes it
	 * PLEASE NOTE: The cursor must be positioned at the end of the mentions link for this method to function correctly
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param mentions - The Mentions link of the user to be mentioned
	 */
	public static void deleteMentionsLinkWithHighlightAndDelete(HomepageUI ui, RCLocationExecutor driver, Mentions mentions) {
		
		log.info("INFO: Now highlighting the mentions link and using the DELETE key to delete the mention");
		ui.highlightAndDeleteMentionsLink(mentions);
		
		log.info("INFO: Verify that the mentions link has now been removed - ie. it is NOT displayed");
		String mentionsLinkCSSSelector = "link=@" + mentions.getUserToMention().getDisplayName();
		Assert.assertFalse(ui.isElementVisible(mentionsLinkCSSSelector), 
							"ERROR: The mentions link was NOT deleted after attempting to highlight and delete the mentions link");
		
		log.info("INFO: Verify that the entire name for the mentioned user has also been deleted");
		Assert.assertTrue(driver.isTextNotPresent(mentions.getUserToMention().getDisplayName()), 
							"ERROR: The mentioned user name was NOT deleted after attempting to highlight and delete the mentions link");
	}

	/**
	 * Opens the EE for a news story, switches to the appropriate frame and then enters a partial mention into the input field
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param newsStory - The String content of the news story corresponding to the story for which the EE will be opened
	 * @param numberOfCharactersToType - The number of characters to type in the partial mention
	 * @param switchToCommentsFrame - True if the comments frame is to be switched to, false if the replies tab is to be switched to
	 */
	public static void openEEAndTypeBeforeMentionsTextAndTypePartialMentions(HomepageUI ui, Mentions mentions, String newsStory, int numberOfCharactersToType, boolean switchToCommentsFrame) {
		
		// Open the EE for the news story and switch to the appropriate frame
		openEEAndSwitchToEECommentOrRepliesFrame(ui, newsStory, switchToCommentsFrame);
		
		// Type the partial mentions into the EE
		typeBeforeMentionsTextAndTypePartialMentions(ui, mentions, numberOfCharactersToType);
	}
	
	/**
	 * Opens the EE for a news story, switches to the appropriate frame and then enters a mention into the input field
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param newsStory - The String content of the news story corresponding to the story for which the EE will be opened
	 * @param switchToCommentsFrame - True if the comments frame is to be switched to, false if the replies tab is to be switched to
	 */
	public static void openEEAndTypeBeforeMentionsTextAndTypeMentions(HomepageUI ui, Mentions mentions, String newsStory, boolean switchToCommentsFrame) {
		
		// Open the EE for the news story and switch to the appropriate frame
		openEEAndSwitchToEECommentOrRepliesFrame(ui, newsStory, switchToCommentsFrame);
		
		// Type the mentions into the EE
		typeBeforeMentionsTextAndTypeMentions(ui, mentions);
	}
	
	/**
	 * Opens the EE and types a partial mention into the comment box (does NOT post any comments)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param switchToCommentsFrame - True if the comments frame is to be switched to, false if the replies tab is to be switched to
	 * @param numberOfCharactersToType - The integer representation of how many characters are to be typed in the partial mention
	 * @return - The user name of the chosen user from the typeahead menu
	 */
	public static String openEEAndTypeBeforeMentionsTextAndTypePartialMentionsAndSelectFirstTypeaheadMenuItem(HomepageUI ui, RCLocationExecutor driver,
																			String newsStory, Mentions mentions, int numberOfCharactersToType, boolean switchToCommentsFrame) {
		// Open the EE for the news story and enter the partial mention
		openEEAndTypeBeforeMentionsTextAndTypePartialMentions(ui, mentions, newsStory, numberOfCharactersToType, switchToCommentsFrame);
		
		// Wait for the typeahead menu to appear
		waitForEETypeaheadMenuToLoad(ui);
		
		// Retrieve the first element from the typeahead menu and select it - returning the text value of that element
		return getFirstTypeaheadMenuItemAndSelectUser(ui, driver);
	}
	
	/**
	 * Opens the EE, enters the before mentions text, the mentions text and then selects the user to be mentioned from the typeahead menu (does NOT post the mention)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param switchToCommentsFrame - True if the comments frame is to be switched to, false if the replies tab is to be switched to
	 */
	public static void openEEAndTypeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUser(HomepageUI ui, RCLocationExecutor driver, String newsStory, Mentions mentions, 
																								boolean switchToCommentsFrame) {
		// Open the EE for the news story and enter the mention
		openEEAndTypeBeforeMentionsTextAndTypeMentions(ui, mentions, newsStory, switchToCommentsFrame);
		
		// Wait for the typeahead menu to appear
		waitForEETypeaheadMenuToLoad(ui);
		
		// Retrieve the elements from the typeahead menu list and select the appropriate user
		getTypeaheadMenuItemsListAndSelectUser(ui, driver, mentions);
	}
	
	/**
	 * Opens the EE, enters the before mentions text, enters a partial mention and retrieves all text contents from the typeahead menu
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStory - The String content of the news story corresponding to the story for which the EE will be opened 
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param numberOfCharactersToType - The number of characters to type in the partial mention
	 * @param switchToCommentsFrame - True if the comments frame is to be switched to, false if the replies tab is to be switched to
	 * @return - An ArrayList<String> of the text contents of all of the typeahead menu items
	 */
	public static ArrayList<String> openEEAndTypeBeforeMentionsTextAndTypePartialMentionsAndGetTypeaheadMenuTextContents(HomepageUI ui, RCLocationExecutor driver, 
																			String newsStory, Mentions mentions, int numberOfCharactersToType, boolean switchToCommentsFrame) {
		// Open the EE for the news story and enter the partial mention
		openEEAndTypeBeforeMentionsTextAndTypePartialMentions(ui, mentions, newsStory, numberOfCharactersToType, switchToCommentsFrame);
		
		// Wait for the typeahead menu to appear
		waitForEETypeaheadMenuToLoad(ui);
		
		// Retrieve the text content from each of the visible menu items
		return getTypeaheadMenuItemsAsText(ui, driver);
	}
	
	/**
	 * Types a partial mention into the EE and uses the arrow keys to select a user from the typeahead menu
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param numberOfCharactersToType - The integer representation of how many characters are to be typed in the partial mention
	 * @param switchToCommentsFrame - True if the comments frame is to be switched to, false if the replies tab is to be switched to
	 * @return - The user name of the chosen user from the typeahead menu
	 */
	public static String typePartialMentionInEEAndSelectTypeaheadUserWithArrowKeys(HomepageUI ui, Mentions mentions, int numberOfCharactersToType, boolean switchToCommentsFrame) {
		
		// Type the partial mentions into the EE
		typeBeforeMentionsTextAndTypePartialMentions(ui, mentions, numberOfCharactersToType);
		
		// Select a user using the arrow keys
		String selectedUser = selectTypeaheadUserInEEUsingArrowKeys(ui);
		
		// Switch back to the comments / replies frame of the EE
		switchToEECommentOrRepliesFrame(ui, switchToCommentsFrame);
		
		// Verify that the mentions link is displayed for the selected user
		verifyMentionsLinkIsDisplayed(ui, selectedUser);
				
		return selectedUser;
	}
	
	/**
	 * Selects a user in the typeahead using the arrow keys for a mentions event in the top frame (ie. status update / comment mentions on Homepage)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The String content of the user who was selected from the typeahead menu using the arrow keys
	 */
	public static String selectTypeaheadUserInTopFrameUsingArrowKeys(HomepageUI ui) {
		
		return ui.selectTypeaheadUserUsingArrowKeys(true, false);
	}
	
	/**
	 * Selects a user in the typeahead using the arrow keys for a mentions event in the EE
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The String content of the user who was selected from the typeahead menu using the arrow keys
	 */
	public static String selectTypeaheadUserInEEUsingArrowKeys(HomepageUI ui) {
		
		return ui.selectTypeaheadUserUsingArrowKeys(false, true);
	}
	
	/**
	 * Types the before mentions text, mentions text, selects the user to be mentioned from the typeahead and then deletes the mentions link using the backspace key (in EE)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param switchToCommentsFrame - True if the comments frame is to be switched to, false if the replies tab is to be switched to
	 * @return - True if all events complete successfully
	 */
	public static boolean typeMentionInEEAndSelectMentionedUserAndDeleteMentions(HomepageUI ui, RCLocationExecutor driver, Mentions mentions, boolean switchToCommentsFrame) {
		
		// Type the mentions into the EE and retrieve the list of menu items from the typeahead
		typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUserInEE(ui, driver, mentions);
		
		// Switch back to the comments / replies frame of the EE
		switchToEECommentOrRepliesFrame(ui, switchToCommentsFrame);
		
		// Verify that the mentions link is displayed for the selected user
		verifyMentionsLinkIsDisplayed(ui, mentions.getUserToMention().getDisplayName());
		
		// Now delete the mentions link using the backspace key
		deleteMentionsLinkWithBackspaceKey(driver, ui, mentions.getUserToMention().getDisplayName());
		
		return true;
	}
	
	/**
	 * enters the before mentions text, the mentions text, selects the user to be mentioned from the typeahead menu and then highlights the mentions link and deletes it
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param newsStory - The String content of the news story corresponding to the story for which the EE will be opened
	 * @param switchToCommentsFrame - True if the comments frame is to be switched to, false if the replies tab is to be switched to
	 * @return - True if all events complete successfully
	 */
	public static boolean openEEAndTypeBeforeMentionsTextAndTypeMentionAndSelectMentionedUserAndHighlightAndDeleteMention(HomepageUI ui, RCLocationExecutor driver, 
																										Mentions mentions, String newsStory, boolean switchToCommentsFrame) {
		// Open the EE for the news story and enter the mention
		openEEAndTypeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUser(ui, driver, newsStory, mentions, switchToCommentsFrame);
		
		// Switch focus back to the EE comment / replies frame
		switchToEECommentOrRepliesFrame(ui, switchToCommentsFrame);
		
		// Verify that the mentions link is displayed for the selected user
		verifyMentionsLinkIsDisplayed(ui, mentions.getUserToMention().getDisplayName());
		
		// Highlight the link and delete the link
		deleteMentionsLinkWithHighlightAndDelete(ui, driver, mentions);
				
		return true;
	}
	
	/**
	 * Verifies if a mentions link to a user is displayed in the current frame
	 * 
	 * @param ui - The HomepageUI instance to invoke the fluentWaitElementVisible() method
	 * @param userName - The user name (ie. the display name) of the user mentioned
	 */
	public static void verifyMentionsLinkIsDisplayed(HomepageUI ui, String userName) {
		
		log.info("INFO: Verify that the mentions link to " + userName + " is displayed");
		Assert.assertTrue(ui.fluentWaitElementVisible("css=a:contains('@" + userName + "')"), 
							"ERROR: The mentions link to " + userName + " was NOT displayed");
	}
	
	/**
	 * Verifies if a mentions link is NOT displayed when an invalid user / different org user is mentioned
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userName - The user name (ie. the display name) of the invalid user mentioned
	 */
	public static void verifyInvalidMentionsLinkIsDisplayed(HomepageUI ui, String userName) {
		
		log.info("INFO: Verify that a valid mentions link (including the '@' character) to " + userName + " is NOT displayed");
		Assert.assertFalse(ui.isElementVisible("css=a:contains('@" + userName + "')"),
							"ERROR: The mentions link to " + userName + " was unexpectedly displayed");
		
		log.info("INFO: Verify that an invalid mentions link (NOT including the '@' character) to '" + userName + "' is displayed");
		Assert.assertTrue(ui.isElementVisible("css=a:contains('" + userName + "')"), 
							"ERROR: The mentioned user name text for the invalid user '" + userName + "' was NOT displayed");
	}
	
	/**
	 * Verifies whether a valid or invalid mentions link is displayed:
	 * 		1: Valid means that the link contains the '@' character
	 * 		2: Invalid means that the link does NOT contain the '@' character
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameMentioned - The String content of the user name who was mentioned
	 * @param warningMessageIsDisplayed - If set to true then an invalid mentions link will be verified as displayed, false means a valid link will be verified
	 */
	public static void verifyValidOrInvalidMentionsLink(HomepageUI ui, String userNameMentioned, boolean warningMessageIsDisplayed) {
		
		if(warningMessageIsDisplayed) {
			// Warning message is displayed therefore verify that an invalid user mentions link is displayed
			verifyInvalidMentionsLinkIsDisplayed(ui, userNameMentioned);
		} else {
			// Warning message is NOT displayed therefore verify that a valid mentions link is displayed
			verifyMentionsLinkIsDisplayed(ui, userNameMentioned);
		}
	}
	
	/**
	 * Clicks on the notification center - opening it in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickNotificationCenter() method
	 * @return - True if all actions are completed successfully
	 */
	public static boolean openNotificationCenter(HomepageUI ui) {
		
		log.info("INFO: Now clicking on the notification center (ie. opening the notification center flyout)");
		boolean flyoutOpened = ui.clickNotificationCenter();
		
		log.info("INFO: Verify that the notification center flyout opened as expected after clicking on the notification center button");
		Assert.assertTrue(flyoutOpened, 
							"ERROR: The notification center flyout did NOT open after 3 attempts to click on the notification center button");
		return true;
	}
	
	/**
	 * Hovers over the notification center icon - opening it in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickNotificationCenter() method
	 * @return - True if all actions are completed successfully
	 */
	public static boolean hoverOverNotificationCenter(HomepageUI ui) {
		
		log.info("INFO: Now hovering over the notification center icon (ie. opening the notification center flyout)");
		boolean flyoutOpened = ui.hoverOverNotificationCenter();
		
		log.info("INFO: Verify that the notification center flyout opened as expected after hovering over the notification center button");
		Assert.assertTrue(flyoutOpened, 
							"ERROR: The notification center flyout did NOT open after 3 attempts to hover over the notification center button");
		return true;
	}
	
	/**
	 * Opens the notification center flyout and verifies that the specified news story is unread
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStory - The news story / notification String which is associated with the notification to be verified as unread
	 * @return - The Element instance of the blue dot associated with the news story
	 */
	public static Element openNotificationCenterAndVerifyNewsStoryIsUnread(HomepageUI ui, RCLocationExecutor driver, String newsStory) {
		
		// Open the notification center flyout
		openNotificationCenter(ui);
		
		// Retrieve the blue dot element for the required news story
		Element blueDot = getBlueDotElementFromNotificationCenterTileElement(ui, newsStory);
		
		// Before marking a notification center news story as read - verify that it is currently marked as unread
		verifyNotificationCenterNewsStoryIsUnread(ui, driver, newsStory, blueDot);
		
		return blueDot;
	}
	
	/**
	 * Opens the notification center flyout and marks a notification / news story in the notification center as read
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStory - The news story / notification String which is associated with the notification to be marked as read
	 * @return - True if all actions are completed successfully
	 */
	public static boolean openNotificationCenterAndMarkNewsStoryAsRead(HomepageUI ui, RCLocationExecutor driver, String newsStory) {
		
		// Open the notification center flyout and verify that the news story is initially marked as unread
		Element blueDot = openNotificationCenterAndVerifyNewsStoryIsUnread(ui, driver, newsStory);
		
		// Mark the required news story as read
		return markNotificationCenterNewsStoryAsRead(ui, driver, blueDot, newsStory);
	}
	
	/**
	 * Opens the notification center flyout and marks a notification / news story in the notification center as read. This method also
	 * checks that the blue dot icon changes as expected as each step is performed
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStory - The news story / notification String which is associated with the notification to be marked as read
	 * @return - True if all actions are completed successfully
	 */
	public static boolean openNotificationCenterAndMarkNewsStoryAsReadAndVerifyBlueDotIconChanges(HomepageUI ui, RCLocationExecutor driver, String newsStory) {
		
		final String WHITE_COLOUR = "#FFFFFF";
		
		// Open the notification center flyout and verify that the news story is initially marked as unread
		Element blueDotUnread = openNotificationCenterAndVerifyNewsStoryIsUnread(ui, driver, newsStory);
		
		// Retrieve the background colour from the blue dot element
		String colourWhenUnread = ui.getElementBackgroundColourAsHex(blueDotUnread);
		
		log.info("INFO: Verify that the blue dot is NOT hollow before marking the story as read (ie. the background colour of the blue dot is NOT white");
		Assert.assertFalse(colourWhenUnread.equals(WHITE_COLOUR), 
							"ERROR: The background colour of the blue dot - before the story was marked as read - is white");
		
		// Mark the required news story as read
		markNotificationCenterNewsStoryAsRead(ui, driver, blueDotUnread, newsStory);
		
		// Re-open the notification center flyout to reset all elements
		openNotificationCenter(ui);
		
		// Reset the blue dot element to the changed element in the UI
		Element blueDotRead = getBlueDotElementFromNotificationCenterTileElement(ui, newsStory);
		
		// Retrieve the background colour from the blue dot element now that the story has been marked as read
		String colourWhenRead = ui.getElementBackgroundColourAsHex(blueDotRead);
		
		log.info("INFO: Verify that the colour of the blue dot has changed now that the story has been marked as read");
		Assert.assertFalse(colourWhenRead.equals(colourWhenUnread), 
							"ERROR: The background colour of the blue dot did NOT change after the story was marked as read");
		
		log.info("INFO: Verify that the blue dot is now hollow after marking the story as read (ie. the background colour of the blue dot is now white)");
		Assert.assertTrue(colourWhenRead.equals(WHITE_COLOUR), 
							"ERROR: The blue dot was NOT hollow (ie. it did NOT have a white background colour) after marking the story as read");
		return true;
	}
	
	/**
	 * Opens the notification center flyout and clicks on the specified link in the specified news story
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param newsStoryContainingLink - The String content of the news story / notification String which contains the link to be clicked
	 * @param linkToBeClicked - The String content of the link to be clicked (should be a unique link)
	 */
	public static void clickLinkInNotificationCenterFlyout(HomepageUI ui, String newsStoryContainingLink, String linkToBeClicked) {
		
		log.info("INFO: The link with content '" + linkToBeClicked + "' will now be clicked in the Notification Center flyout");
		ui.clickLinkInNotificationCenter(newsStoryContainingLink, linkToBeClicked);
	}
	
	/**
	 * Uses the activity stream search to search for matching events to the search text provided
	 * 
	 * @param ui - The HomepageUI instance to invoke the searchUsingASSearch() method
	 * @param userSearchingTheAS - The User performing the search on the AS
	 * @param searchText - The String content of the text to be used in the search
	 */
	public static void searchUsingASSearch(HomepageUI ui, User userSearchingTheAS, String searchText) {
		
		log.info("INFO: " + userSearchingTheAS.getDisplayName() + " will now use AS search to search for the text with content: " + searchText);
		ui.searchUsingASSearch(searchText);
	}
	
	/**
	 * Cancels an AS search (ie. closes the AS Search panel) by using the same magnifying glass icon used to open the panel
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void cancelASSearchUsingMagnifyingGlassIcon(HomepageUI ui) {
		
		// Cancel the AS Search panel using the magnifying glass icon
		ui.cancelASSearch(false);
	}
	
	/**
	 * Cancels an AS search (ie. closes the AS Search panel) by using the 'X' icon
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void cancelASSearchUsingXIcon(HomepageUI ui) {
		
		// Reset the AS to the top - this also hides any hover text which may be obscuring the 'X' icon
		resetASToTop(ui);
		
		// Cancel the AS Search panel using the 'X' icon
		ui.cancelASSearch(true);
	}
	
	/**
	 * Opens the global sharebox, switches focus to the global sharebox frame and then verifies that all global sharebox components have loaded correctly
	 * 
	 * PLEASE NOTE: When this method is completed, the focus is still switched over to the global sharebox frame (ie. NOT the main frame)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void openGlobalShareboxAndVerifyAllComponents(HomepageUI ui) {
		
		// Open the global sharebox
		openGlobalSharebox(ui);
		
		// Switch focus to the global sharebox frame
		switchToGlobalShareboxFrame(ui);
		
		// Verify that all global sharebox components have loaded correctly
		verifyGlobalShareboxComponentsHaveLoadedCorrectly(ui);
	}
	
	/**
	 * Types in a status message and appends a URL to the status message in a generic status update input field
	 * 
	 * PLEASE NOTE: This method does NOT post the status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param urlPreviewWidgetHasThumbnailImage - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeStatusWithURL(HomepageUI ui, String statusMessageBeforeURL, String url, boolean urlPreviewWidgetHasThumbnailImage) {
		
		// Type in the status message with URL into the status update input field
		return typeStatusUpdateWithURL(ui, statusMessageBeforeURL, url, false, false, urlPreviewWidgetHasThumbnailImage, false);
	}
	
	/**
	 * Types in a status message and appends an invalid URL to the status message in a generic status update input field
	 * 
	 * PLEASE NOTE: This method does NOT post the status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeInvalidURL - The String content of any text to appear before the invalid URL
	 * @param invalidURL - The invalid URL to be posted with the status message content
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeStatusWithInvalidURL(HomepageUI ui, String statusMessageBeforeInvalidURL, String invalidURL) {
		
		// Type in the status message with invalid URL into the status update input field
		return typeStatusUpdateWithInvalidURL(ui, statusMessageBeforeInvalidURL, invalidURL, false, false);
	}
	
	/**
	 * Types in a status message and appends a connections URL to the status message in a generic status update input field
	 * 
	 * PLEASE NOTE: This method does NOT post the status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The String content of the URL to be entered with the status message
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeStatusWithConnectionsURL(HomepageUI ui, String statusMessageBeforeURL, String url) {
		
		// Type in the status message with connections URL into the status update input field
		return typeStatusUpdateWithURL(ui, statusMessageBeforeURL, url, false, true, false, false);
	}
	
	/**
	 * Types in a status message and appends a video IRL to the status message in a generic status update input field
	 * 
	 * PLEASE NOTE: This method does NOT post the status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The String content of the URL to be entered with the status message
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeStatusWithVideoURL(HomepageUI ui, String statusMessageBeforeURL, String url) {
		
		return typeStatusUpdateWithURL(ui, statusMessageBeforeURL, url, false, true, true, false);
	}
	
	/**
	 * Types in a status message and appends a URL to the status message in a generic status update input field
	 * This method verifies the behaviour of the 'Add a File' link while entering the URL into the status update input field
	 * 
	 * PLEASE NOTE: This method does NOT post the status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param urlPreviewWidgetHasThumbnailImage - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeStatusWithURLAndVerifyAddAFileLink(HomepageUI ui, String statusMessageBeforeURL, String url, boolean urlPreviewWidgetHasThumbnailImage) {
		
		// Type in the status message with URL into the status update input field and verify the behaviour of the 'Add a File' link while doing so
		return typeStatusUpdateWithURL(ui, statusMessageBeforeURL, url, false, false, urlPreviewWidgetHasThumbnailImage, true);
	}
	
	/**
	 * Removes a URL preview widget from a status message entered into a generic status update input field
	 * This method verifies the behaviour of the 'Add a File' link while removing the URL preview widget
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods 
	 * @param url - The URL to be posted with the status message content
	 * @return - True if all actions are completed successfully
	 */
	public static boolean removeURLPreviewWidgetAndVerifyAddAFileLink(HomepageUI ui, String url) {
		
		// Remove the URL preview widget and verify the behaviour of the 'Add a File' link while doing so
		boolean successfulRemoval = removeURLPreviewWidget(ui, url, false, true);
		
		log.info("INFO: Verify that the URL preview widget was removed successfully and that the 'Add a File' link behaviour was correct");
		Assert.assertTrue(successfulRemoval,
							"ERROR: The URL preview widget removal and verifications for the 'Add a File' link behaviour returned a negative result");
		return true;
	}
	
	/**
	 * Types in a status message and appends a URL to the status message in a generic status update input field
	 * This method then removes the URL preview widget from the status message
	 * 
	 * This method verifies the behaviour of the 'Add a File' link while entering the URL into the status update input field
	 * This method also verifies the behaviour of the 'Add a File' link while removing the URL preview widget
	 * 
	 * PLEASE NOTE: This method does NOT post the status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param urlPreviewWidgetHasThumbnailImage - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeStatusWithURLAndRemoveURLPreviewWidgetAndVerifyAddAFileLink(HomepageUI ui, String statusMessageBeforeURL, String url, boolean urlPreviewWidgetHasThumbnailImage) {
		
		// Type in the status message with URL into the status update input field and verify the behaviour of the 'Add a File' link while doing so
		typeStatusWithURLAndVerifyAddAFileLink(ui, statusMessageBeforeURL, url, urlPreviewWidgetHasThumbnailImage);
		
		// Remove the URL preview widget and verify the behaviour of the 'Add a File' link while doing so
		return removeURLPreviewWidgetAndVerifyAddAFileLink(ui, url);
	}
	
	/**
	 * Types in a status message and appends a URL to the status message in the global sharebox
	 * 
	 * PLEASE NOTE: This method does NOT post the status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param urlPreviewWidgetHasThumbnailImage - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeStatusWithURLInGlobalSharebox(HomepageUI ui, String statusMessageBeforeURL, String url, boolean urlPreviewWidgetHasThumbnailImage) {
		
		// Type in the status message with URL into the global sharebox
		return typeStatusUpdateWithURL(ui, statusMessageBeforeURL, url, true, false, urlPreviewWidgetHasThumbnailImage, false);
	}
	
	/**
	 * Types in a status message and appends a video URL to the status message in the global sharebox
	 * 
	 * PLEASE NOTE: This method does NOT post the status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeStatusWithVideoURLInGlobalSharebox(HomepageUI ui, String statusMessageBeforeURL, String url) {
		
		// Type in the status message with video URL into the global sharebox
		return typeStatusUpdateWithURL(ui, statusMessageBeforeURL, url, true, true, true, false);
	}
	
	/**
	 * Types in a community status message and appends a URL to the community status message in the global sharebox
	 * 
	 * PLEASE NOTE: This method does NOT post the community status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param community - The Community instance of the community to which the status message is entered
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param urlPreviewWidgetHasThumbnailImage - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeCommunityStatusWithURLInGlobalSharebox(HomepageUI ui, Community community, String statusMessageBeforeURL, String url, boolean urlPreviewWidgetHasThumbnailImage) {
		
		// Select the "a Community" drop down menu and select the required community
		selectGlobalShareboxCommunityDropDownMenuOption(ui, community);
		
		// Type in the community status message with URL into the global sharebox
		return typeStatusWithURLInGlobalSharebox(ui, statusMessageBeforeURL, url, urlPreviewWidgetHasThumbnailImage);
	}
	
	/**
	 * Types in a status message and appends a URL into a generic status update input field
	 * This method then posts the status message with URL
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param urlPreviewWidgetHasThumbnailImage - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean postStatusWithURL(HomepageUI ui, String statusMessageBeforeURL, String url, boolean urlPreviewWidgetHasThumbnailImage) {
		
		// Type in the status with URL into the status update input field
		typeStatusWithURL(ui, statusMessageBeforeURL, url, urlPreviewWidgetHasThumbnailImage);
		
		// Post the status message with URL preview
		postStatusUpdate(ui);
		
		return true;
	}
	
	/**
	 * Types in a status message and appends a URL into a generic status update input field
	 * This method then posts the status message with URL
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param videoURL - The URL to be posted with the status message content
	 * @return - True if all actions are completed successfully
	 */
	public static boolean postStatusWithVideoURL(HomepageUI ui, String statusMessageBeforeURL, String videoURL) {
		
		// Type in the status with video URL into the status update input field
		typeStatusWithVideoURL(ui, statusMessageBeforeURL, videoURL);
		
		// Post the status message with URL preview
		postStatusUpdate(ui);
		
		return true;
	}
	
	/**
	 * Types in a status message and appends a URL into a generic status update input field
	 * This method then removes the thumbnail image from the URL preview widget and posts the status message with URL
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @return - True if all actions are completed successfully
	 */
	public static boolean postStatusWithURLAndRemoveThumbnail(HomepageUI ui, String statusMessageBeforeURL, String url) {
		
		// Type in the status with URL into the status update input field
		typeStatusWithURL(ui, statusMessageBeforeURL, url, true);
		
		// Remove the thumbnail image from the URL preview widget
		removeURLPreviewWidgetThumbnailImage(ui, url);
		
		// Post the status message with URL preview
		postStatusUpdate(ui);
				
		return true;
	}
	
	/**
	 * Types in a community status message and appends a URL to the community status message in the global sharebox
	 * This method then posts the status message with URL to the community
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param community - The Community instance of the community to which the status message is entered
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param urlPreviewWidgetHasThumbnailImage - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean postCommunityStatusWithURLInGlobalSharebox(HomepageUI ui, Community community, String statusMessageBeforeURL, String url, boolean urlPreviewWidgetHasThumbnailImage) {
		
		// Select the community to which to enter the status update with URL and then enter the status message with URL
		typeCommunityStatusWithURLInGlobalSharebox(ui, community, statusMessageBeforeURL, url, urlPreviewWidgetHasThumbnailImage);
				
		// Post the status message with URL preview
		postGlobalShareboxUpdate(ui);
		
		return true;
	}
	
	/**
	 * Types in a community status message and appends a URL to the community status message in the global sharebox
	 * This method then removes the thumbnail image from the URL preview widget
	 * 
	 * PLEASE NOTE: This method does NOT post the community status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param community - The Community instance of the community to which the status message is entered
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeCommunityStatusWithURLInGlobalShareboxAndRemoveThumbnail(HomepageUI ui, Community community, String statusMessageBeforeURL, String url) {
		
		// Select the community to which to enter the status update with URL and then enter the status message with URL
		typeCommunityStatusWithURLInGlobalSharebox(ui, community, statusMessageBeforeURL, url, true);
		
		// Remove the thumbnail image from the URL preview widget
		return removeURLPreviewWidgetThumbnailImage(ui, url);
	}
	
	/**
	 * Posts a community status update with mentions to the specified user using the Global Sharebox
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The HomepageUI instance to invoke all relevant methods
	 * @param community - The Community instance of the community to which the status update will be posted
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param verifyNonMemberWarningMessageIsDisplayed - True if the warning message about a user not being a member of the community is to be verified as displayed, false otherwise
	 */
	public static void postCommunityStatusWithMentionsInGlobalSharebox(HomepageUI ui, RCLocationExecutor driver, Community community, Mentions mentions, boolean verifyNonMemberWarningMessageIsDisplayed) {
		
		// Select the specified community from the Global Sharebox drop down menu
		selectGlobalShareboxCommunityDropDownMenuOption(ui, community);
		
		// Switch to the status update frame of the Global Sharebox
		switchToStatusUpdateFrame(ui);
		
		// Enter the before mentions text and the mentions to the specified user
		typeBeforeMentionsText(ui, mentions);
		typeMentionsOrPartialMentions(ui, mentions, mentions.getUserToMention().getDisplayName().length());
		
		// Wait for the typeahead menu to load
		UIEvents.waitForGlobalShareboxTypeaheadMenuToLoad(ui);
				
		// Select the relevant user from the typeahead
		UIEvents.getTypeaheadMenuItemsListAndSelectUser(ui, driver, mentions);
		
		if(verifyNonMemberWarningMessageIsDisplayed) {
			log.info("INFO: Verify that the warning message is displayed in the Global Sharebox with content: " + Data.getData().mentionErrorMsgGlobalSharebox);
			ui.fluentWaitTextPresent(Data.getData().mentionErrorMsgGlobalSharebox);
		}
		// Switch focus back to the status update frame of the Global Sharebox
		switchToStatusUpdateFrame(ui);
				
		// Enter the after mentions text
		typeAfterMentionsText(ui, mentions);
				
		// Post the status update
		UIEvents.postGlobalShareboxUpdate(ui);
	}
	
	/**
	 * Types in a community status message and appends a URL to the community status message in the global sharebox
	 * This method then removes the thumbnail image from the URL preview widget and posts the community status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param community - The Community instance of the community to which the status message is entered
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 */
	public static boolean postCommunityStatusWithURLInGlobalShareboxAndRemoveThumbnail(HomepageUI ui, Community community, String statusMessageBeforeURL, String url) {
		
		// Select the community to which to enter the status update with URL, enter the status message with URL and then remove the thumbnail image
		typeCommunityStatusWithURLInGlobalShareboxAndRemoveThumbnail(ui, community, statusMessageBeforeURL, url);
		
		// Post the status message with URL preview and thumbnail image removed
		postGlobalShareboxUpdate(ui);
		
		return true;
	}
	
	/**
	 * Retrieves the URL for the currently open page in the browser
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke the getCurrentUrl() method
	 * @return - The URL of the currently open browser page
	 */
	public static String getCurrentURL(RCLocationExecutor driver) {
		
		log.info("INFO: Now retrieving the current URL for the current page in the browser");
		String currentURL = driver.getCurrentUrl();
		
		log.info("INFO: The current URL has been found: " + currentURL);
		return currentURL;
	}
	
	/**
	 * Posts a comment with URL to the specified status update - also verifies that the URL preview widget did NOT appear
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusUpdateToBeCommentedOn - The String content of the status update to which to post the comment
	 * @param commentBeforeURL - The String content of the comment text to appear before the URL
	 * @param url - The URL to be posted with the comment content
	 * @return - True if all actions are completed successfully
	 */
	public static boolean postStatusUpdateCommentWithURL(HomepageUI ui, String statusUpdateToBeCommentedOn, String commentBeforeURL, String url) {
		
		// Post the comment with URL to the status update
		boolean urlPreviewIsDisplayed = ui.addStatusUpdateCommentWithURL(statusUpdateToBeCommentedOn, commentBeforeURL, url);
		
		log.info("INFO: Verify that the URL preview widget was NOT displayed when posting the comment with URL");
		Assert.assertFalse(urlPreviewIsDisplayed, 
							"ERROR: The URL preview widget was displayed when posting a comment with URL");
		return true;
	}
	
	/**
	 * Navigates to the Getting Started view, clicks on the Homepage icon and verifies that the user is redirected to the I'm Following view
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - True if all actions are completed successfully
	 */
	public static boolean navigateToGettingStartedAndClickVisitorHomepageIconAndVerifyImFollowingViewIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now navigating to the 'Getting Started' view in order to click on the Homepage icon element with CSS selector: " + HomepageUIConstants.Visitor_HomepageIcon);
		navigateToGettingStartedAndClickElement(ui, HomepageUIConstants.Visitor_HomepageIcon);
		
		// Verify that the user has been redirected to the I'm Following view
		verifyImFollowingViewIsDisplayed(ui);
		
		return true;
	}
	
	/**
	 * Navigates to the Getting Started view, clicks on the Homepage link and verifies that the user is redirected to the I'm Following view
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - True if all actions are completed successfully
	 */
	public static boolean navigateToGettingStartedAndClickVisitorHomepageLinkAndVerifyImFollowingViewIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now navigating to the 'Getting Started' view in order to click on the Homepage link element with CSS selector: " + HomepageUIConstants.Visitor_HomepageLink);
		navigateToGettingStartedAndClickElement(ui, HomepageUIConstants.Visitor_HomepageLink);
		
		// Verify that the user has been redirected to the I'm Following view
		verifyImFollowingViewIsDisplayed(ui);
		
		return true;
	}

	/**
	 * Navigates to the Getting Started view, clicks on the Communities UI icon and verifies that the user is redirected to the Communities UI screen
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - True if all actions are completed successfully
	 */
	public static boolean navigateToGettingStartedAndClickVisitorCommunitiesIconAndVerifyCommunitiesUIIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now navigating to the 'Getting Started' view in order to click on the Communities UI icon element with CSS selector: " + HomepageUIConstants.Visitor_CommunitiesIcon);
		navigateToGettingStartedAndClickElement(ui, HomepageUIConstants.Visitor_CommunitiesIcon);
		
		// Verify that the user has been redirected to the Communities UI screen
		CommunityEvents.verifyCommunitiesUIIsDisplayed(ui);
		
		return true;
	}
	
	/**
	 * Navigates to the Getting Started view, clicks on the Communities UI link and verifies that the user is redirected to the Communities UI screen
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - True if all actions are completed successfully
	 */
	public static boolean navigateToGettingStartedAndClickVisitorCommunitiesLinkAndVerifyCommunitiesUIIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now navigating to the 'Getting Started' view in order to click on the Communities UI link element with CSS selector: " + HomepageUIConstants.Visitor_CommunitiesLink);
		navigateToGettingStartedAndClickElement(ui, HomepageUIConstants.Visitor_CommunitiesLink);
		
		// Verify that the user has been redirected to the Communities UI screen
		CommunityEvents.verifyCommunitiesUIIsDisplayed(ui);
		
		return true;
	}
	
	/**
	 * Navigates to the Getting Started view, clicks on the Files UI icon and verifies that the user is redirected to the Files UI screen
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - True if all actions are completed successfully
	 */
	public static boolean navigateToGettingStartedAndClickVisitorFilesIconAndVerifyFilesUIIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now navigating to the 'Getting Started' view in order to click on the Files UI icon element with CSS selector: " + HomepageUIConstants.Visitor_FilesIcon);
		navigateToGettingStartedAndClickElement(ui, HomepageUIConstants.Visitor_FilesIcon);
		
		// Verify that the user has been redirected to the Files UI screen
		FileEvents.verifyFilesUIIsDisplayed(ui);
		
		return true;
	}
	
	/**
	 * Navigates to the Getting Started view, clicks on the Files UI link and verifies that the user is redirected to the Files UI screen
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - True if all actions are completed successfully
	 */
	public static boolean navigateToGettingStartedAndClickVisitorFilesLinkAndVerifyFilesUIIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now navigating to the 'Getting Started' view in order to click on the Files UI link element with CSS selector: " + HomepageUIConstants.Visitor_FilesLink);
		navigateToGettingStartedAndClickElement(ui, HomepageUIConstants.Visitor_FilesLink);
		
		// Verify that the user has been redirected to the Files UI screen
		FileEvents.verifyFilesUIIsDisplayed(ui);
		
		return true;
	}
	
	/**
	 * Navigates to the Getting Started view and clicks on the My Notifications link
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void navigateToGettingStartedAndClickVisitorMyNotificationsLink(HomepageUI ui) {
		
		log.info("INFO: Now navigating to the 'Getting Started' view in order to click on the My Notifications link element with CSS selector: " + HomepageUIConstants.Visitor_MyNotificationsLink);
		navigateToGettingStartedAndClickElement(ui, HomepageUIConstants.Visitor_MyNotificationsLink);
	}
	
	/**
	 * Navigates to the Getting Started view, clicks on the Action Required link
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void navigateToGettingStartedAndClickVisitorActionRequiredLink(HomepageUI ui) {
		
		log.info("INFO: Now navigating to the 'Getting Started' view in order to click on the Action Required link element with CSS selector: " + HomepageUIConstants.Visitor_ActionRequiredLink);
		navigateToGettingStartedAndClickElement(ui, HomepageUIConstants.Visitor_ActionRequiredLink);
	}
	
	/**
	 * Navigates to the Getting Started view and clicks on the Help link in the footer of the page
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void navigateToGettingStartedAndClickFooterHelpLink(HomepageUI ui) {
		
		// Navigate to Getting Started and click on the Help link in the footer
		navigateToGettingStartedAndClickElement(ui, HomepageUIConstants.HelpFooterLink);
	}
	
	/**
	 * Retrieves the handle value of the currently active browser window
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @return - The String value of the current browser window handle
	 */
	public static String getCurrentBrowserWindowHandle(HomepageUI ui) {
		
		// Retrieve the handle value of the currently active browser window
		return ui.getCurrentBrowserWindowHandle();
	}
	
	/**
	 * Switches to the first / next open browser window based on its handle value - only switches if it finds a handle value different to the current value provided
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentBrowserWindowHandle - The String content of the currently active window handle value
	 */
	public static void switchToNextOpenBrowserWindowByHandle(HomepageUI ui, String currentBrowserWindowHandle) {
		
		// Switch to the first / next open browser window with a handle value different to the one provided
		boolean switchSuccessful = ui.switchToNextOpenBrowserWindowByHandle(currentBrowserWindowHandle);
		
		log.info("INFO: Verify that the switch to a new browser window completed successfully");
		Assert.assertTrue(switchSuccessful, 
							"ERROR: There was a problem with switching to a new browser window with a handle value other than: " + currentBrowserWindowHandle);
	}
	
	/**
	 * Closes the currently active / open browser window
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void closeCurrentBrowserWindow(HomepageUI ui) {
		
		// Close the currently active browser window
		ui.closeCurrentBrowserWindow();
	}
	
	/**
	 * Closes the currently active browser window and switches focus to the specified browser window based on the provided handle value
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param browserWindowToSwitchTo - The String content of the window handle value to switch to
	 */
	public static void closeCurrentBrowserWindowAndSwitchToWindowByHandle(HomepageUI ui, String browserWindowToSwitchTo) {
		
		// Close the currently active browser window
		closeCurrentBrowserWindow(ui);
		
		// Switch to the specified browser window based on handle value
		ui.switchToWindowByHandle(browserWindowToSwitchTo);
	}
	
	/**
	 * Verifies that a mentions status update / comment is displayed correctly in the UI by performing the following checks:
	 * 		1) The before mentions text is visible
	 * 		2) The valid / invalid mentions link text is visible
	 * 		3) The after mentions text is visible
	 * 
	 * Sometimes, it is NOT consistent to verify that the entire mentions string is displayed, ie. "" + beforeMentionsText + " @" + mentionsUserName + " " + afterMentionsText
	 * 
	 * When a visitor is mentioned, the " VISITOR" string is appended to the end of their mentioned user name which breaks this type of check,
	 * therefore the only consistently reliable verification in these cases is to verify that all the components are, at the very least, displayed in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param mentions - The Mentions instance of the user that was mentioned
	 * @param validMentionsLinkIsDisplayed - True if the mentions link is a valid mentions link (ie. includes the '@' character), false if the link is invalid
	 */
	public static void verifyMentionsTextIsDisplayedInUI(HomepageUI ui, Mentions mentions, boolean validMentionsLinkIsDisplayed) {
		
		log.info("INFO: Verifying that the before mentions text is displayed after posting the status update / comment");
		Assert.assertTrue(ui.fluentWaitTextPresent(mentions.getBeforeMentionText().trim()), 
							"ERROR: The before mentions text was NOT displayed after posting the status update / comment - expected content: " + mentions.getBeforeMentionText());
		
		String mentionsLink;
		if(validMentionsLinkIsDisplayed) {
			mentionsLink = "@" + mentions.getUserToMention().getDisplayName();
		} else {
			mentionsLink = "" + mentions.getUserToMention().getDisplayName();
		}	
		log.info("INFO: Verifying that the mentions link is displayed after posting the status update / comment");
		Assert.assertTrue(ui.fluentWaitTextPresent(mentionsLink), 
							"ERROR: The mentions link was NOT displayed after posting the status update / comment - expected content: " + mentionsLink);
		
		log.info("INFO: Verifying that the after mentions text is displayed after posting the status update / comment");
		Assert.assertTrue(ui.fluentWaitTextPresent(mentions.getAfterMentionText().trim()), 
							"ERROR: The after mentions text was NOT displayed after posting the status update / comment - expected content: " + mentions.getAfterMentionText());
	}
	
	/**
	 * Types a string into the currently active input field / element without using delay
	 * 
	 * @param ui - The HomepageUI instance to invoke the typeStringWithNoDelay() method
	 * @param stringToBeTyped - The String content to be entered without delay
	 */
	public static void typeStringWithNoDelay(HomepageUI ui, String stringToBeTyped) {
		
		// Type the string into the currently active input field without using delay
		ui.typeStringWithNoDelay(stringToBeTyped);
	}
	
	/**
	 * Types a string into the currently active input field / element using delay
	 * 
	 * @param ui - The HomepageUI instance to invoke the typeStringWithDelay() method
	 * @param stringToBeTyped - The String content to be entered with delay
	 */
	public static void typeStringWithDelay(HomepageUI ui, String stringToBeTyped) {
		
		// Type the string into the currently active input field using delay
		ui.typeStringWithDelay(stringToBeTyped);
	}
	
	/**
	 * Clicks on a hashtag inside of a news story / status update (ie. a news story with NO description)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userClickingHashtag - The User instance of the user to click on the hashtag
	 * @param newsStoryContent - The String content of the news story which contains the hashtag (eg. "This is a status update...")
	 * @param hashtag - The hashtag to be clicked
	 */
	public static void clickNewsStoryHashtag(HomepageUI ui, User userClickingHashtag, String newsStoryContent, String hashtag) {
		
		log.info("INFO: " + userClickingHashtag.getDisplayName() + " will now click on the hashtag with content '" + hashtag + "' in the news story with content: " + newsStoryContent);
		clickNewsStoryHashtag(ui, newsStoryContent, null, null, null, hashtag);
	}
	
	/**
	 * Clicks on a hashtag inside of the description for a news story
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userClickingHashtag - The User instance of the user to click on the hashtag
	 * @param newsStoryContent - The String content of the news story in which the description with hashtag is located (eg. "UserName1 posted a message to UserName2.")
	 * @param newsStoryDescription - The String content of the news story description which contains the hashtag
	 * @param hashtag - The hashtag to be clicked
	 */
	public static void clickNewsStoryDescriptionHashtag(HomepageUI ui, User userClickingHashtag, String newsStoryContent, String newsStoryDescription, String hashtag) {
		
		log.info("INFO: " + userClickingHashtag.getDisplayName() + " will now click on the hashtag with content '" + hashtag + "' in the news story description with content: " + newsStoryDescription);
		clickNewsStoryHashtag(ui, newsStoryContent, newsStoryDescription, null, null, hashtag);
	}
	
	/**
	 * Clicks on a hashtag inside of a comment posted to a news story
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userClickingHashtag - The User instance of the user to click on the hashtag
	 * @param newsStoryContent - The String content of the news story in which the comment with hashtag is located (eg. "UserName commented on their own message.")
	 * @param newsStoryDescription - The String content of the news story description in which the comment with hashtag is located (eg. "This is a test description for...")
	 * @param commentContent - The String content of the comment which contains the hashtag
	 * @param hashtag - The hashtag to be clicked
	 */
	public static void clickNewsStoryCommentHashtag(HomepageUI ui, User userClickingHashtag, String newsStoryContent, String newsStoryDescription, String commentContent, String hashtag) {
		
		log.info("INFO: " + userClickingHashtag.getDisplayName() + " will now click on the hashtag with content '" + hashtag + "' in the news story comment with content: " + commentContent);
		clickNewsStoryHashtag(ui, newsStoryContent, newsStoryDescription, commentContent, null, hashtag);
	}
	
	/**
	 * Clicks on a hashtag inside of a mentions string in a news story
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userClickingHashtag - The User instance of the user to click on the hashtag
	 * @param newsStoryContent - The String content of the news story in which the mentions with hashtag is located (eg. "UserName mentioned you in a message.")
	 * @param mentions - The Mentions instance of the User being mentioned (the after mentions text includes the hashtag)
	 * @param hashtag - The hashtag to be clicked
	 */
	public static void clickNewsStoryMentionsHashtag(HomepageUI ui, User userClickingHashtag, String newsStoryContent, Mentions mentions, String hashtag) {
		
		log.info("INFO: " + userClickingHashtag.getDisplayName() + " will now click on the hashtag with content '" + hashtag + "' in the mentions event to: " + mentions.getUserToMention().getDisplayName());
		clickNewsStoryHashtag(ui, newsStoryContent, null, null, mentions, hashtag);
	}
	
	/**
	 * Clicks on the timestamp element for the specified news story
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userClickingHashtag - The User instance of the user to click on the hashtag
	 * @param newsStoryContent - The String content of the news story in which the mentions with hashtag is located (eg. "UserName mentioned you in a message.")
	 * @param mentions - The Mentions instance of the User being mentioned (the after mentions text includes the hashtag)
	 * @param hashtag - The hashtag to be clicked
	 */
	public static void clickNewsStoryTimestamp(HomepageUI ui, User userClickingTimestamp, String newsStoryContent) {
		
		log.info("INFO: " + userClickingTimestamp.getDisplayName() + " will now click on the timestamp for the news story with content '" + newsStoryContent + "'");
		ui.clickNewsStoryTimeStamp(newsStoryContent);
	}
	
	/**
	 * Clicks on an existing filter in Global Search UI to remove the filter
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickLinkWait() methods
	 * @param userRemovingFilter - The User instance of the user removing the filter
	 * @param filteredTag - The tag to be removed
	 */
	public static void removeFilteredTagInGlobalSearchUI(HomepageUI ui, User userRemovingFilter, String filteredTag) {
		
		log.info("INFO: " + userRemovingFilter.getDisplayName() + " will now click on the 'X' in the filter to remove the filter with content: " + filteredTag);
		ui.clickLinkWait(GlobalsearchUI.Remove_MatchingTag.replaceAll("PLACEHOLDER", filteredTag.toLowerCase()));
	}
	
	/**
	 * Saves a news story in the AS by clicking on the 'Save this' link associated with that news story
	 * 
	 * @param ui - The HomepageUI instance to invoke the saveNewsStoryUsingUI() method
	 * @param newsStoryContent - The String content of the news story to be saved
	 */
	public static void saveNewsStoryUsingUI(HomepageUI ui, String newsStoryContent) {
		
		log.info("INFO: Now using the UI to save the news story with content: " + newsStoryContent);
		boolean newsStorySaved = ui.saveNewsStoryUsingUI(newsStoryContent);
		
		log.info("INFO: Verify that the news story was saved successfully using the UI");
		Assert.assertTrue(newsStorySaved, 
							"ERROR: The news story could NOT be saved using the UI - false response returned");
	}
	
	/**
	 * Removes a news story from the users Action Required view by clicking on the news stories 'X' link
	 * 
	 * @param ui - The HomepageUI instance to invoke the removeNewsStoryUsingUI() method
	 * @param newsStoryContent - The String content of the news story to be removed from the Action Required view
	 */
	public static void removeNewsStoryFromActionRequiredViewUsingUI(HomepageUI ui, String newsStoryContent) {
		
		log.info("INFO: Now using the UI to remove the news story from the Action Required view with content: " + newsStoryContent);
		ui.removeNewsStoryUsingUI(newsStoryContent);
		
		log.info("INFO: Verify that the removal successful message is displayed in the Action Required view now that the news story has been removed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.SuccessfulDelFromActionRequiredList), 
							"ERROR: The news story removed successfully message was NOT displayed in the Action Required view after removing the news story");
	}
	
	/**
	 * Clicks on the 'X' icon in the banner which displays the success message in Action Required for when a user has removed a news story from their view
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickLinkWait() method
	 */
	public static void hideEntrySuccessfullyRemovedMessageInActionRequiredUsingUI(HomepageUI ui) {
		
		log.info("INFO: Now removing the Action Required news story removal success message banner with content: " + Data.SuccessfulDelFromActionRequiredList);
		ui.clickLinkWait(HomepageUIConstants.ActionRequired_HideSuccessMsg_NewsStoryRemoval);
	}
	
	/**
	 * Removes a news story from the users Saved view by clicking on the news stories 'X' link
	 * 
	 * @param ui - The HomepageUI instance to invoke the removeNewsStoryUsingUI() method
	 * @param newsStoryContent - The String content of the news story to be removed from the Saved view
	 */
	public static void removeNewsStoryFromSavedViewUsingUI(HomepageUI ui, String newsStoryContent) {
		
		log.info("INFO: Now using the UI to remove the news story from the Saved view with content: " + newsStoryContent);
		ui.removeNewsStoryUsingUI(newsStoryContent);
		
		log.info("INFO: Verify that the removal successful message is displayed in the Saved view now that the news story has been removed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.SuccessfulDelFromSavedList), 
							"ERROR: The news story removed successfully message was NOT displayed in the Saved view after removing the news story");
	}
	
	/**
	 * Opens the EE for the specified news story in the AS and posts a comment using the EE
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param newsStoryContent - The String content of the news story for which to open the EE
	 * @param commentContent - The String content of the comment to be posted to the news story
	 * @return - True if all actions are completed successfully
	 */
	public static boolean addEECommentUsingUI(HomepageUI ui, User userPostingComment, String newsStoryContent, String commentContent) {
		
		log.info("INFO: " + userPostingComment.getDisplayName() + " will now post a comment in the EE with content: " + commentContent);
		openEEAndSwitchToEECommentOrRepliesFrame(ui, newsStoryContent, true);
		
		// Enter the comment content into the 'Comments' input field
		typeStringWithNoDelay(ui, commentContent);
		
		// Switch focus back to the EE frame again and post the comment by clicking on the 'Post' link
		switchToEEFrameAndPostCommentOrReply(ui);
		
		log.info("INFO: Verify that the comment has posted correctly in the EE with content: " + commentContent.trim());
		Assert.assertTrue(ui.fluentWaitTextPresent(commentContent.trim()), 
							"ERROR: The comment was NOT displayed in the EE after clicking on the 'Post' link");
		return true;
	}
	
	/**
	 * Opens the EE for the specified news story in the AS, types in a comment and then cancels the comment entry using the EE
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userPostingComment - The User instance of the user typing the comment
	 * @param newsStoryContent - The String content of the news story for which to open the EE
	 * @param commentContent - The String content of the comment to be entered and then cancelled
	 * @return - True if all actions are completed successfully
	 */
	public static boolean addEECommentAndCancelCommentUsingUI(HomepageUI ui, RCLocationExecutor driver, User userTypingComment, String newsStoryContent, String commentContent) {
		
		log.info("INFO: " + userTypingComment.getDisplayName() + " will now enter and then cancel a comment in the EE with content: " + commentContent);
		openEEAndSwitchToEECommentOrRepliesFrame(ui, newsStoryContent, true);
		
		// Enter the comment content into the 'Comments' input field
		typeStringWithNoDelay(ui, commentContent);
		
		// Switch focus back to the EE frame again and cancel the comment by clicking on the 'Cancel' link
		switchToEEFrameAndCancelCommentOrReply(ui);
		
		log.info("INFO: Verify that the comment has NOT been posted in the EE with content: " + commentContent);
		Assert.assertTrue(driver.isTextNotPresent(commentContent), 
							"ERROR: The comment was posted in the EE after clicking on the 'Cancel' link");
		
		// Switch focus back to the EE comments frame
		switchToEECommentOrRepliesFrame(ui, true);
		
		log.info("INFO: Verify that the comment is NOT still entered in the EE comments frame after cancelling the comment entry");
		Assert.assertTrue(driver.isTextNotPresent(commentContent), 
							"ERROR: The comment was still present in the EE comments frame after cancelling the comment entry");
		return true;
	}
	
	/**
	 * Clicks on the 'Like' link in the EE using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void clickLikeInEEUsingUI(HomepageUI ui) {
		
		// Ensure that the focus is on the EE frame
		UIEvents.switchToEEFrame(ui);
		
		log.info("INFO: Now clicking on the 'Like' link in the EE");
		ui.fluentWaitPresent(HomepageUIConstants.EELike);
		
		// Using moveToClick on the 'Like' link proves to be more reliable than using Element.click()
		ui.moveToClick(HomepageUIConstants.EELike, HomepageUIConstants.EELike);
		
		log.info("INFO: Verifying that the 'Like' link has now changed to an 'Unlike' link (ie. verifying that the 'Like' link was clicked correctly)");
		ui.fluentWaitPresent(HomepageUIConstants.EELikeUndo);
	}
	
	/**
	 * Clicks on the thumbnail image of a URL preview widget in order to play the attached video
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param statusUpdateBeforeURL - The String content of the status update before the URL has been entered
	 * @param videoURL - The String content of the videos URL
	 * @param isNewsFeedURLPreviewWidget - True if the URL preview widget to be interacted with is in the news feed, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean playVideoInURLPreviewWidgetAndVerifyVideoPlaying(HomepageUI ui, RCLocationExecutor driver, String statusUpdateBeforeURL, String videoURL, boolean isNewsFeedURLPreviewWidget) {
		
		// Truncate the video URL so as it is in the correct format for the CSS selector for the URL preview widget and subsequent thumbnail image
		String truncatedVideoURL = videoURL.substring(0, videoURL.indexOf(".com") + 4);
		
		Element thumbnailImageElement;
		if(isNewsFeedURLPreviewWidget) {
			// Create the CSS selector for the thumbnail image in the URL preview widget which is in the news feed
			String thumbnailImage = ui.replaceNewsStory(HomepageUIConstants.URL_PREVIEW_NEWS_FEED_THUMBNAIL_IMAGE, statusUpdateBeforeURL + " " + videoURL, truncatedVideoURL, null);
			
			// Retrieve the URL preview widget thumbnail image element
			thumbnailImageElement = driver.getSingleElement(thumbnailImage);
		} else {
			// Add an additional truncation to the URL (ie. non-posted URL preview widgets replace https:// with http://)
			truncatedVideoURL = truncatedVideoURL.replaceAll("https://", "http://");
			
			// Create the CSS selectors for the URL preview widget which is NOT in the news feed (ie. it is attached to the status update input field)
			String urlPreviewWidget = ui.replaceNewsStory(HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST, truncatedVideoURL, null, null);
			String thumbnailImage = ui.replaceNewsStory(HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST_THUMBNAIL_IMAGE, null, null, null);
			
			// Retrieve the URL preview widget thumbnail image element
			thumbnailImageElement = driver.getSingleElement(urlPreviewWidget).getSingleElement(thumbnailImage);
		}
		
		log.info("INFO: Now clicking on the thumbnail image for the video in the URL preview widget");
		ui.clickElement(thumbnailImageElement);
		
		if(isNewsFeedURLPreviewWidget) {
			log.info("INFO: Verify that the video is playing after clicking on the thumbnail image in the news feed");
			String videoPlaying = ui.replaceNewsStory(HomepageUIConstants.URL_PREVIEW_NEWS_FEED_VIDEO_PLAYING, statusUpdateBeforeURL + " " + videoURL, truncatedVideoURL, null);
			Assert.assertTrue(ui.fluentWaitPresent(videoPlaying), 
								"ERROR: The video was NOT playing after clicking the thumbnail image of a URL preview widget in the news feed");
		} else {
			log.info("INFO: Verify that the video is NOT playing after clicking on the thumbnail image");
			String videoPlaying = ui.replaceNewsStory(HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST_VIDEO_PLAYING, truncatedVideoURL, null, null);
			Assert.assertFalse(ui.isElementVisible(videoPlaying), 
								"ERROR: The video was playing after clicking the thumbnail image of a URL preview widget that had NOT yet been posted to the news feed");
		}
		return true;
	}
	
	/**
	 * Retrieves the URL for the image displayed in the URL preview widgets thumbnail
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of the status update before the URL
	 * @param url - The String content of the URL used to generate the URL preview widget
	 * @param isNewsFeedURLPreviewWidget - True if the URL preview widget to be interacted with is in the news feed, false otherwise
	 * @return - The URL of the thumbnail image
	 */
	public static String getURLForCurrentURLPreviewWidgetThumbnailImage(HomepageUI ui, RCLocationExecutor driver, String statusMessageBeforeURL, String url, boolean isNewsFeedURLPreviewWidget) {
		
		log.info("INFO: Now retrieving the URL for the current thumbnail image displayed in the specified URL preview widget with URL: " + url);
		
		String thumbnailImageURL;
		if(isNewsFeedURLPreviewWidget) {
			// Create the CSS selector for the thumbnail image in the news feed
			String thumbnailImage = ui.replaceNewsStory(HomepageUIConstants.URL_PREVIEW_NEWS_FEED_THUMBNAIL_IMAGE, statusMessageBeforeURL, url, null);
			
			// Retrieve the URL preview widget thumbnail image element
			thumbnailImageURL = driver.getSingleElement(thumbnailImage).getAttribute("style");
		} else {
			// Create the CSS selectors for the URL preview widget which is NOT in the news feed (ie. it is attached to the status update input field)
			String urlPreviewWidget = ui.replaceNewsStory(HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST, url, null, null);
			String thumbnailImage = ui.replaceNewsStory(HomepageUIConstants.URL_PREVIEW_BEFORE_SU_POST_THUMBNAIL_IMAGE, null, null, null);
			
			// Retrieve the URL preview widget thumbnail image element
			thumbnailImageURL = driver.getSingleElement(urlPreviewWidget).getSingleElement(thumbnailImage).getAttribute("style");
		}
		thumbnailImageURL = thumbnailImageURL.substring(thumbnailImageURL.indexOf("url(\"") + 5, thumbnailImageURL.indexOf("\")"));
		
		log.info("INFO: The thumbnail image URL for the current thumbnail has been retrieved: " + thumbnailImageURL);
		return thumbnailImageURL;
	}
	
	/**
	 * Toggles the thumbnail image in the URL preview widget to the right until the next unique image is found
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param url - The String content of the URL used to generate the URL preview widget
	 * @param currentImageDisplayedURL - The String content of the URL for the current image being displayed in the thumbnail before any image toggle takes place
	 * @return - The URL of the next image found
	 */
	public static String toggleURLPreviewWidgetThumbnailImageToTheRight(HomepageUI ui, RCLocationExecutor driver, String url, String currentImageDisplayedURL) {
		
		// Create the CSS selector for the toggle right button in the URL preview widget
		String toggleRightSelector = HomepageUIConstants.URLPreview_Thumbnail_ChooseRight_Unique.replaceAll("PLACEHOLDER", url);
		
		// Toggle the images to the right until the next unique image is found
		String toggledImageURL = toggleURLPreviewWidgetThumbnailImagesUntilNextImageIsDisplayed(ui, driver, toggleRightSelector, url, currentImageDisplayedURL);
		
		log.info("INFO: Verify that the image toggled to the right successfully (ie. the image changed as expected)");
		Assert.assertNotNull(toggledImageURL, 
								"ERROR: There was a problem with toggling the images in the URL preview widget to the right - value returned as null");
		return toggledImageURL;
	}
	
	/**
	 * Toggles the thumbnail image in the URL preview widget to the left until the next unique image is found
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param url - The String content of the URL used to generate the URL preview widget
	 * @param currentImageDisplayedURL - The String content of the URL for the current image being displayed in the thumbnail before any image toggle takes place
	 * @return - The URL of the next image found
	 */
	public static String toggleURLPreviewWidgetThumbnailImageToTheLeft(HomepageUI ui, RCLocationExecutor driver, String url, String currentImageDisplayedURL) {
		
		// Create the CSS selector for the toggle left button in the URL preview widget
		String toggleLeftSelector = HomepageUIConstants.URLPreview_Thumbnail_ChooseLeft_Unique.replaceAll("PLACEHOLDER", url);
		
		// Toggle the images to the left until the next unique image is found
		String toggledImageURL = toggleURLPreviewWidgetThumbnailImagesUntilNextImageIsDisplayed(ui, driver, toggleLeftSelector, url, currentImageDisplayedURL);
		
		log.info("INFO: Verify that the image toggled to the left successfully (ie. the image changed as expected)");
		Assert.assertNotNull(toggledImageURL, 
								"ERROR: There was a problem with toggling the images in the URL preview widget to the left - value returned as null");
		return toggledImageURL;
	}
	
	/**
	 * Clicks on a generic 'Save' button in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickSaveButton() method
	 */
	public static void clickSaveButton(HomepageUI ui) {
		
		log.info("INFO: Now clicking on the 'Save' button in the UI");
		ui.clickSaveButton();
	}
	
	/**
	 * Clicks on the 'See All' link in the Notification Center flyout
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickLinkWait() method
	 */
	public static void clickSeeAllInNotificationCenter(HomepageUI ui) {
		
		log.info("INFO: Now clicking on the 'See All' link in the Notification Center");
		ui.clickLinkWait(HomepageUIConstants.NotificationCenterFooter);
	}
	
	/**
	 * Clicks on the magnifying glass icon to open AS Search
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickSaveButton() method
	 */
	public static void openASSearch(HomepageUI ui) {
		
		log.info("INFO: Now clicking on the magnifying glass icon to open AS Search");
		ui.clickLinkWait(HomepageUIConstants.AS_SearchOpenElement);
	}
	
	/**
	 * Clicks on the 'Show More' link to show more news items in the AS
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickIfVisible() method
	 */
	public static void clickShowMore(HomepageUI ui) {
		
		log.info("INFO: Now clicking on the 'Show More' link in the AS to make the test more robust by ensuring the news item has NOT been pushed out of the news feed");
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		// Reset the AS back to the top after clicking on 'Show More'
		resetASToTop(ui);
	}
	
	/**
	 * Retrieves the value of the text for the specified element
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param elementCSSSelector - The String content of the CSS selector corresponding to the element
	 * @return - The String content of the element text
	 */
	public static String getElementText(HomepageUI ui, String elementCSSSelector) {
		
		log.info("INFO: Now retrieving the element text for the element with CSS selector: " + elementCSSSelector);
		String textContent = ui.getElementText(elementCSSSelector).trim();
		
		log.info("INFO: The text content of the element has been retrieved as: " + textContent);
		return textContent;
	}
	
	/**
	 * Retrieves the value of the 'title' attribute for the specified element
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param elementCSSSelector - The String content of the CSS selector corresponding to the element
	 * @return - The String content of the 'title' attribute value
	 */
	public static String getElementTitleAttribute(RCLocationExecutor driver, String elementCSSSelector) {
		
		// Retrieve the value of the 'title' attribute for the specified element
		return driver.getSingleElement(elementCSSSelector).getAttribute("title").trim();
	}
	
	/**
	 * Resets the view back to the top of the screen
	 * 
	 * @param ui - The HomepageUI instance to invoke the resetASToTop() method
	 */
	public static void resetASToTop(HomepageUI ui) {
		
		// Reset the AS back to the top (position (0, 0))
		ui.resetASToTop();
	}
	
	/**
	 * Filters the AS by the specified filter
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param filter - The String content of the filter by which the AS is to be filtered by
	 */
	public static void filterBy(HomepageUI ui, String filter) {
		
		log.info("INFO: Now filtering the AS by '" + filter + "'");
		ui.filterBy(filter);
	}
	
	/**
	 * Opens the EE for the specified news story and clicks on the 'Repost' link in the EE to repost the news story
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param newsStoryToRepost - The String content of the news story to be reposted
	 */
	public static void openEEAndRepostNewsStory(HomepageUI ui, String newsStoryToRepost) {
		
		// Open the EE for the news story
		openEE(ui, newsStoryToRepost);
		
		log.info("INFO: Now clicking on the 'Repost' link in the EE to repost the news story");
		ui.clickLinkWait(HomepageUIConstants.RepostAction_EE);
		
		log.info("INFO: Verify that the repost success message is displayed in the EE");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().RepostedUpdateMessage), 
							"ERROR: The repost success message was NOT displayed in the EE as expected");
	}
	
	/**
	 * Retrieves all of the filter options for the current AS view
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The List<String> of all filter views in the current view
	 */
	public static List<String> getAllASViewFilterOptions(HomepageUI ui) {
		
		// Retrieve all of the filter options for the current AS view
		return ui.getAllDropDownMenuOptions(HomepageUIConstants.FilterBy);
	}
	
	/**
	 * Retrieves the currently selected filter option for the current AS view
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The String content of the current filter title that is selected
	 */
	public static String getSelectedASViewFilterOption(HomepageUI ui) {
		
		// Retrieve the currently selected filter option for the current AS view
		return ui.getSelectedDropDownMenuOption(HomepageUIConstants.FilterBy);
		
	}
	
	/**
	 * Retrieves the "Tags" UI string for the file details box for the specified file
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param fileName - The String content of the file name - eg. baseFile.getRename() + baseFile.getExtension()
	 * @return - The String content of all tags in the UI tags string for this file
	 */
	public static String getFileDetailsBoxTagsString(HomepageUI ui, String fileName) {
		
		log.info("INFO: Now retrieving the Tags UI string from the file details box for the file with filename: " + fileName);
		
		// Create the CSS selector corresponding to the tags element for the file details box
		String fileTagsCSS = ui.replaceNewsStory(HomepageUIConstants.fileDetailsTags_Element, fileName, null, null);
		
		// Retrieve the tags string from the element
		String tagsString = ui.getFirstVisibleElement(fileTagsCSS).getText();
		
		return tagsString.replaceAll("Tags: ", "").trim();
	}
	
	/**
	 * Opens the actions menu for the 'Events' widget in the UI and then clicks on the 'Help' option in the menu
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @return - True if all operations are successful, False otherwise
	 */
	public static boolean openEventsWidgetActionsMenuAndClickHelp(HomepageUI ui, RCLocationExecutor driver) {
		
		// Open the action menu for the Events widget and select the Help option
		return openASWidgetActionsMenuAndClickOption(ui, driver, "Events", "Help");
	}
	
	/**
	 * Clicks on the specified title link in the EE
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param activityToBeClicked - The Activity instance of the activity whose title is to be clicked
	 */
	public static void clickActivityTitleLinkInEEUsingUI(HomepageUI ui, Activity activityToBeClicked) {
		
		// Ensure that focus is switched back to the top frame
		switchToTopFrame(ui);
		
		// Switch focus to the EE frame
		switchToEEFrame(ui);
		
		// Click on the activity title in the EE to redirect the user to the Activities UI screen
		ui.clickLinkWait("link=" + activityToBeClicked.getTitle().trim());
	}
	
	/**
	 * Checks the specified news item in the Notification Center and verifies the specified users profile picture is displayed
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param newsStoryToVerify - The String content of the news story to which the profile picture is attached
	 * @param apiUserProfile - The String content of the UUID for the user whose profile picture is to be displayed
	 * @return - True if the profile pic is verified as belonging to the user with the specified ID, false otherwise
	 */
	public static boolean checkNotificationCenterProfilePicture(HomepageUI ui, String newsStoryToVerify, APIProfilesHandler apiUserProfile) {
		
		log.info("INFO: A Notification Center news story item will now be checked for the presence of a profile picture belonging to: " + apiUserProfile.getDesplayName());
		return ui.checkNotificationCenterProfilePicture(newsStoryToVerify, apiUserProfile.getUUID());
	}
	
	/**
	 * Clicks on the 'Show More' link to expand the specified status update
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusUpdateToExpand - The String content of the status update whose 'Show More' link is to be clicked
	 */
	public static void clickShowMoreContentLinkForStatusUpdate(HomepageUI ui, String statusUpdateToExpand) {
		
		// Create the CSS selector for the 'Show More' link for the specified status update
		String statusUpdateShowMoreLink = HomepageUIConstants.ShowMoreSUContent_Unique.replace("PLACEHOLDER", statusUpdateToExpand);
		
		log.info("INFO: Now clicking on the 'Show More' link for the status update with content: " + statusUpdateToExpand);
		ui.clickLinkWait(statusUpdateShowMoreLink);
	}
	
	/**
	 * Opens the Actions menu for the specified widget and selects the specified option from the menu
	 * 
	 * PLEASE NOTE: This method currently only supports clicking on the 'Help' link from the actions menu
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param widgetToClick - The String content of the widget whose actions menu is to be opened
	 * @param menuOptionToSelect - The String content of the actions menu option to be selected
	 * @return - True if all operations are successful, False otherwise
	 */
	private static boolean openASWidgetActionsMenuAndClickOption(HomepageUI ui, RCLocationExecutor driver, String widgetToClick, String menuOptionToSelect) {
		
		// Set the CSS selectors for both the widget element and the actions menu element
		String widgetMenuCSS = HomepageUIConstants.AS_Widget_Actions_Menu.replaceAll("PLACEHOLDER", widgetToClick);
		String widgetMenuOptionCSS = HomepageUIConstants.AS_Widget_Actions_Menu_Link.replaceAll("PLACEHOLDER", menuOptionToSelect);
				
		log.info("INFO: Waiting for the " + widgetToClick + " widget to be displayed in the UI");
		if(widgetToClick.equals("Events")) {
			ui.fluentWaitPresent(HomepageUIConstants.eventsWidget);
		}
		
		log.info("INFO: Waiting for the " + widgetToClick + " widget actions menu to be displayed in the UI");
		ui.fluentWaitPresent(widgetMenuCSS);
		
		log.info("INFO: Now hovering over the actions menu for the AS widget with title: " + widgetToClick);
		Element widgetMenuElement = driver.getSingleElement(widgetMenuCSS);
		ui.hoverOverElement(widgetMenuElement);
		
		log.info("INFO: Now clicking on the actions menu options for the AS widget with title: " + widgetToClick);
		ui.clickElement(widgetMenuElement);
		
		int numberOfAttemptedMenuOptionClicks = 0;
		boolean clickedMenuOptionLink = false;
		do {
			log.info("INFO: Attempt " + (numberOfAttemptedMenuOptionClicks + 1) + " of 3 - Clicking on the '" + menuOptionToSelect + "' actions menu option in the UI");
			
			log.info("INFO: Waiting for the " + widgetToClick + " widgets '" + menuOptionToSelect + "' menu option to be displayed in the UI");
			ui.fluentWaitPresent(widgetMenuOptionCSS);
			
			log.info("INFO: Now hovering over the '" + menuOptionToSelect + "' option from the drop down menu");
			Element widgetMenuOptionElement = driver.getSingleElement(widgetMenuOptionCSS);
			ui.hoverOverElement(widgetMenuOptionElement);
			
			log.info("INFO: Now selecting the '" + menuOptionToSelect + "' option from the drop down menu");
			ui.clickElement(widgetMenuOptionElement);
			
			if(menuOptionToSelect.equals("Help")) {
				if(ui.getAllBrowserWindowHandles().size() > 1) {
					// More than one browser window has been identified - the click on the 'Help' link has been successful and the new window has opened
					clickedMenuOptionLink = true;
				}
			}
			numberOfAttemptedMenuOptionClicks ++;
		} while(numberOfAttemptedMenuOptionClicks < 3 && clickedMenuOptionLink == false);
		
		return clickedMenuOptionLink;
	}
	
	/**
	 * Toggles the thumbnail image in the URL preview widget in the specified direction and returns the URL of the next image found
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param toggleButtonSelector - The String content of the CSS selector corresponding to the toggle button to be used to toggle the image
	 * @param url - The String content of the URL used to generate the URL preview widget
	 * @param currentImageDisplayedURL - The String content of the URL for the current image being displayed in the thumbnail before any image toggle takes place
	 * @return - The URL of the next image found if the toggle operation is successful, null otherwise
	 */
	private static String toggleURLPreviewWidgetThumbnailImagesUntilNextImageIsDisplayed(HomepageUI ui, RCLocationExecutor driver, String toggleButtonSelector, String url, String currentImageDisplayedURL) {
		
		log.info("INFO: Now toggling the thumbnail image until a new image is displayed");
		int numberOfTries = 0;
		boolean foundNewImage = false;
		String nextImageURL = null;
		
		do {
			log.info("INFO: Now clicking on the toggle button");
			ui.clickLinkWait(toggleButtonSelector);
			
			// Retrieve the URL for the current image displayed in the thumbnail
			String newImageDisplayedURL = getURLForCurrentURLPreviewWidgetThumbnailImage(ui, driver, null, url, false);
			
			if(!currentImageDisplayedURL.equals(newImageDisplayedURL)) {
				foundNewImage = true;
				nextImageURL = newImageDisplayedURL;
			}
			numberOfTries ++;
		} while(numberOfTries < 3 && foundNewImage == false);
		
		if(foundNewImage == false) {
			log.info("ERROR: A new thumbnail image could NOT be found after 3 attempts");
			return null;
		}
		return nextImageURL;
	}
	/**
	 * Clicks on a hashtag for any news story in the news feed
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickNewsStoryHashTag() method
	 * @param newsStoryOuterContent - The news story which appears in the UI (eg. "User1 posted a message to User2")
	 * @param newsStoryInnerContent - The news story content relating to the news story and including the hashtag which is to be clicked
	 * @param commentContent - If the tag is included in a comment then include the comment string, otherwise use null for this parameter
	 * @param mentionsContent - If the tag is included in a status message with mentions then include the Mentions object, otherwise use null for this parameter
	 * @param hashTag - The hashtag (without the '#' character) which is to be clicked
	 */
	private static void clickNewsStoryHashtag(HomepageUI ui, String newsStoryOuterContent, String newsStoryInnerContent, String commentContent, Mentions mentionsContent, String hashTag) {
		
		// Click on the specified hash tag posted with the required news story
		ui.clickNewsStoryHashTag(newsStoryOuterContent, newsStoryInnerContent, commentContent, mentionsContent, hashTag);
	}
	
	/**
	 * Types in a status message with URL into the required status update input field
	 * 
	 * PLEASE NOTE: This method does NOT post the status message
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param isGlobalShareboxStatus - True if the status update is to be posted in the global sharebox status update input field, false otherwise
	 * @param isConnectionsOrVideoURL - True if the URL being entered belongs to a connections component (ie. activities) or YouTube video, false otherwise
	 * @param urlPreviewHasThumbnail - True if the URL preview widget will contain a thumbnail image, false otherwise
	 * @param verifyAddAFileLink - True if the behaviour of the 'Add a File' link is to be verified, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	private static boolean typeStatusUpdateWithURL(HomepageUI ui, String statusMessageBeforeURL, String url, boolean isGlobalShareboxStatus, boolean isConnectionsOrVideoURL,
													boolean urlPreviewHasThumbnail, boolean verifyAddAFileLink) {
		// Type in the status message with URL into the required status update input box
		boolean urlPreviewDisplayed = ui.typeStatusUpdateWithURL(statusMessageBeforeURL, url, isGlobalShareboxStatus, isConnectionsOrVideoURL, urlPreviewHasThumbnail, verifyAddAFileLink);
		
		log.info("INFO: Verify that all URL preview widget components were displayed after typing the URL into the status input field");
		Assert.assertTrue(urlPreviewDisplayed,
							"ERROR: All expected URL preview widget components were NOT displayed after typing the URL into the status input field");
		return true;
	}
	
	/**
	 * Types in a status update with invalid URL into the required status update input field
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeInvalidURL - The String content of any text to appear before the invalid URL
	 * @param invalidURL - The invalid URL to be posted with the status message content
	 * @param isGlobalShareboxStatus - True if the status update is to be posted in the global sharebox status update input field, false otherwise
	 * @param verifyAddAFileLink - True if the behaviour of the 'Add a File' link is to be verified, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	private static boolean typeStatusUpdateWithInvalidURL(HomepageUI ui, String statusMessageBeforeInvalidURL, String invalidURL, boolean isGlobalShareboxStatus, boolean verifyAddAFileLink) {
		
		// Type in the status message with invalid URL into the required status update input box
		boolean urlPreviewIsNotDisplayed = ui.typeStatusUpdateWithInvalidURL(statusMessageBeforeInvalidURL, invalidURL, isGlobalShareboxStatus, verifyAddAFileLink);
		
		log.info("INFO: Verify that the URL preview widget components were NOT displayed after typing the invalid URL into the status input field");
		Assert.assertTrue(urlPreviewIsNotDisplayed, 
							"ERROR: The URL preview widget components were displayed after typing an invalid URL into the status input field");
		return true;
	}
	
	/**
	 * Posts a status message entered into a generic status update input field
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void postStatusUpdate(HomepageUI ui) {
		
		// Post the status update message
		ProfileEvents.postStatusUpdateUsingUI(ui);
	}
	
	/**
	 * Posts a status message entered into the global sharebox
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void postGlobalShareboxUpdate(HomepageUI ui) {
		
		// Post the global sharebox status message
		ProfileEvents.postGlobalShareboxUpdateUsingUI(ui);
	}
	
	/**
	 * Opens the global sharebox by clicking on the 'Share' button
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void openGlobalSharebox(HomepageUI ui) {
		
		// Open the global sharebox by clicking on the 'Share' button
		ui.openGlobalSharebox();
	}
	
	/**
	 * Switches focus to the global sharebox frame
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void switchToGlobalShareboxFrame(HomepageUI ui) {
		
		// Switch focus to the global sharebox frame
		ui.switchToGlobalShareboxFrame();
	}
	
	/**
	 * Verifies that all global sharebox components are displayed / have loaded correctly
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void verifyGlobalShareboxComponentsHaveLoadedCorrectly(HomepageUI ui) {
		
		// Verify that all components have loaded correctly
		ui.verifyGlobalShareboxComponents();
	}
	
	/**
	 * Selects the "a Community" drop down menu item in the global sharebox and enters the community name to be selected
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityToBeSelected - The Community instance of the community to be selected
	 */
	private static void selectGlobalShareboxCommunityDropDownMenuOption(HomepageUI ui, Community communityToBeSelected) {
		
		// Select the "a Community" drop down menu item in the global sharebox and enter the community name to be selected
		ui.selectGlobalShareboxCommunityDropDownMenuOption(communityToBeSelected.getTitle());
	}
	
	/**
	 * Selects the checkbox to remove the thumbnail image preview from the URL preview widget
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param url - The URL used to generate the URL preview widget
	 */
	private static boolean removeURLPreviewWidgetThumbnailImage(HomepageUI ui, String url) {
		
		// Remove the thumbnail image preview from the URL preview widget
		boolean thumbnailImageRemoved = ui.removeURLPreviewWidgetThumbnailImage(url);
		
		log.info("INFO: Verify that all actions to remove the thumbnail image from the URL preview widget completed successfully");
		Assert.assertTrue(thumbnailImageRemoved, 
							"ERROR: The thumbnail image was NOT removed as expected from the URL preview widget");
		return thumbnailImageRemoved;
	}
	
	/**
	 * Removes the URL preview widget from a pre-posted status update (also works for status updates entered in the global sharebox)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param url - The URL used to generate the URL preview widget
	 * @param isGlobalShareboxStatus - True if the URL preview widget to be removed is part of a status message entered in the global sharebox
	 * @param verifyAddAFileLink - True if the behaviour of the 'Add a File' link is to be verified, false otherwise
	 * @return - True if all actions are successful, false if they are not successful
	 */
	private static boolean removeURLPreviewWidget(HomepageUI ui, String url, boolean isGlobalShareboxStatus, boolean verifyAddAFileLink) {
		
		// Remove the URL preview widget from the relevant status update input field and verify 'Add a File' link if required
		return ui.removeURLPreviewWidget(url, isGlobalShareboxStatus, verifyAddAFileLink);
	}
	
	/**
	 * Retrieves the blue dot element for the notification center tile which contains the news story provided
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param newsStory - The news story content contained within the notification center tile
	 * @return - The Element instance of the blue dot
	 */
	private static Element getBlueDotElementFromNotificationCenterTileElement(HomepageUI ui, String newsStory) {
		
		// Retrieve the blue dot element associated with the tile element
		return ui.getBlueDotElementFromNotificationCenterTile(newsStory);
	}
	
	/**
	 * Verifies that a notification / news story in the notification center is currently marked as unread - expects that you have already
	 * retrieved both the notification center tile and subsequent blue dot elements
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStory - The news story / notification String which is associated with the notification to be verified as unread
	 * @param blueDot - The Element instance of the blue dot contained within the tile
	 * @return - True if the actions are completed successfully
	 */
	private static boolean verifyNotificationCenterNewsStoryIsUnread(HomepageUI ui, RCLocationExecutor driver, String newsStory, Element blueDot) {
		
		log.info("INFO: Now verifying that the notification is marked as 'unread' for the news story with content: " + newsStory);
		
		// Retrieve the title attribute from the blue dot - this indicates whether the story is currently marked as unread
		String readOrUnread = blueDot.getAttribute("title");
		
		log.info("INFO: Verify that the notification is currently marked as 'unread' (ie. the hover text reads 'Mark read')");
		Assert.assertTrue(readOrUnread.equals(Data.getData().NotificationCenter_MarkRead), 
							"ERROR: The notification center news story hover text was NOT 'Mark read' as expected");
		return true;
	}
	
	/**
	 * Marks a notification / news story in the notification center as read - expects that you have verified that the news story is unread
	 * and that you have the blue dot element which is to be clicked
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStory - The news story / notification String which is associated with the notification to be marked as read
	 * @return - True if all actions are completed successfully
	 */
	private static boolean markNotificationCenterNewsStoryAsRead(HomepageUI ui, RCLocationExecutor driver, Element blueDot, String newsStory) {
		
		log.info("INFO: Now marking the notification as 'read' for the news story with content: " + newsStory);
		
		log.info("INFO: Now hovering over the news story element with content: " + newsStory);
		String ncNewsStoryElement = HomepageUIConstants.NotificationCenter_Tile.replace("PLACEHOLDER", newsStory);
		ui.hoverOverElement(ui.getFirstVisibleElement(ncNewsStoryElement));
		
		log.info("INFO: Now hovering over the blue dot element for the notification center tile to be marked as 'read'");
		ui.hoverOverElement(blueDot);
		
		log.info("INFO: Now clicking on the blue dot element to change the status of this notification to 'read'");
		ui.clickElement(blueDot);
		
		// Re-open the notification center flyout to reset all elements
		openNotificationCenter(ui);
		
		// Reset the blue dot element to the changed element in the UI
		Element blueDotRead = getBlueDotElementFromNotificationCenterTileElement(ui, newsStory);
		
		// Retrieve the title attribute from the blue dot - this indicates whether the story has been marked as read / is still marked as unread
		String readOrUnread = blueDotRead.getAttribute("title");
		
		log.info("INFO: Verify that the notification is now marked as 'read' (ie. the hover text reads 'Mark unread')");
		Assert.assertTrue(readOrUnread.equals(Data.getData().NotificationCenter_MarkUnread), 
							"ERROR: The notification news story hover text did NOT change to 'Mark unread' as expected after clicking on the blue dot");
		return true;
	}
	
	/**
	 * Retrieves the menu items from the typeahead menu - can return only the first element or all elements
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param retrieveFirstElementOnly - True if only the first menu item element is to be returned, false otherwise
	 * @return - The List<Element> instance of all / the first menu item element, depending on what was requested
	 */
	private static List<Element> getTypeaheadMenuItemsList(HomepageUI ui, RCLocationExecutor driver, boolean retrieveFirstElementOnly) {
		
		if(retrieveFirstElementOnly) {
			log.info("INFO: Retrieve only the first menu item element from the typeahead menu");
		} else {
			log.info("INFO: Retrieve all menu item elements from the typeahead menu");
		}
		return ui.getTypeaheadMenuItemsList(retrieveFirstElementOnly);
	}
	
	/**
	 * Retrieves the current badge number from the badge corresponding to the CSS selector
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke the getSingleElement() method
	 * @return - The integer value of the badge
	 */
	private static int getBadgeValue(RCLocationExecutor driver, String badgeCSSSelector) {
		
		int badgeNumber = -1;
		driver.isElementPresent(badgeCSSSelector);
		String badgeText = driver.getSingleElement(badgeCSSSelector).getText();
		log.info("INFO: The badge text has been retrieved as: '" + badgeText + "'");
		
		// Set the numerical value for the badge
		if(badgeText.equals("")) {
			badgeNumber = 0;
		} else {
			try {
				badgeNumber = Integer.parseInt(badgeText);
			} catch(NumberFormatException nfe) {
				log.info("ERROR: The badge number could not be converted to an integer value as it is in an incorrect format: '" + badgeText + "'");
			}
		}
		
		log.info("INFO: Assert that the badge number has been retrieved successfully");
		Assert.assertFalse(badgeNumber == -1, 
							"ERROR: The badge number was NOT retrieved successfully and was determined to be a value of " + badgeNumber);
		
		log.info("INFO: The badge number has been successfully retrieved as: " + badgeNumber);
		return badgeNumber;
	}
	
	/**
	 * Navigates to the Getting Started screen and clicks on the element specified in the CSS selector provided
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param cssSelector - The String content of the CSS selector corresponding to the element to be clicked
	 */
	private static void navigateToGettingStartedAndClickElement(HomepageUI ui, String cssSelector) {
		
		log.info("INFO: Returning to Home to ensure that the link to the 'Getting Started' view is displayed");
		gotoHome(ui);
		
		log.info("INFO: Now navigating to the Getting Started view");
		ui.clickLinkWait(HomepageUIConstants.GettingStarted);
		
		log.info("INFO: Verifying that an element is present with CSS selector: " + cssSelector);
		Assert.assertTrue(ui.fluentWaitPresent(cssSelector), 
							"ERROR: There was NO such element in the Getting Started view with CSS selector: " + cssSelector);
		
		log.info("INFO: Now clicking on the element with CSS selector: " + cssSelector);
		ui.clickLink(cssSelector);
	}
	
	/**
	 * Verifies that the I'm Following view is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void verifyImFollowingViewIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now verifying that the I'm Following view is displayed in the UI");
		
		log.info("INFO: Waiting for the I'm Following view to load");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries), 
							"ERROR: The I'm Following view did NOT load correctly / in time with visible text: " + Data.getData().feedForTheseEntries);
		
		log.info("INFO: Verify that the I'm Following heading is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().viewFollowing), 
							"ERROR: The I'm Following heading was NOT displayed in the I'm Following view");
		
		log.info("INFO: Verify that the I'm Following description text is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().ImFollowingText), 
							"ERROR: The I'm Following description text was NOT displayed in the I'm Following view");
	}
	
	/**
	 * Strikes the BACKSPACE key the specified number of times - deleting text from the currently active element
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param numOfTimesToPressKey - The Integer number of times the BACKSPACE key is to be pressed
	 */
	private static void useBackspaceKeyInCurrentActiveElement(RCLocationExecutor driver, int numOfTimesToPressKey) {
		
		log.info("INFO: Now pressing the BACKSPACE key on the keyboard " + numOfTimesToPressKey + " times (first strike of the key sometimes acts as a dummy strike)");
		for(int index = 0; index < numOfTimesToPressKey; index ++) {
			driver.switchToActiveElement().typeWithDelay(Keys.BACK_SPACE);
		}
	}
	
	/**
	 * Verifies that the specified mentions link has been removed from the currently active element in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userMentioned - The String content of the user name who has been mentioned (and whose mentions link is to be verified as deleted)
	 */
	private static void verifyMentionsLinkHasBeenDeletedInCurrentActiveElement(HomepageUI ui, RCLocationExecutor driver, String userMentioned) {
		
		log.info("INFO: Verify that the mentions link has been removed");
		String mentionsLinkCSSSelector = "link=@" + userMentioned;
		Assert.assertFalse(ui.isElementVisible(mentionsLinkCSSSelector), 
							"ERROR: The mention to the user with user name '" + userMentioned + "' was still displayed after attempted deletion");
		
		// Retrieve the remaining text from the input field and also determine the mentioned user minus their last character
		String remainingInputText = driver.switchToActiveElement().getText();
		String userNameLessLastCharacter = userMentioned.substring(0, userMentioned.length() - 1);
		
		boolean oneCharacterDeleted;
		if(remainingInputText.length() >= userNameLessLastCharacter.length()) {
			// Determine whether the final part of the remaining UI String matches the mentioned user less their last character
			oneCharacterDeleted = remainingInputText.substring(remainingInputText.length() - userNameLessLastCharacter.length()).equals(userNameLessLastCharacter);
		} else {
			// The UI string is not long enough to contain the mentioned user less their last character
			oneCharacterDeleted = false;
		}
		log.info("INFO: Verify that the user name has been deleted and has not just had its end character deleted");
		Assert.assertFalse(oneCharacterDeleted, 
							"ERROR: The mentioned user was NOT deleted with the removal of the mention link - their name less the end character still remained");
	}
	/**
	 * Verifies that the by default I'M Following view is displayed in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyIMFollowingViewIsDisplayed(HomepageUI ui,RCLocationExecutor driver ) {
		
		log.info("INFO: Now verifying that the I'M Following view is displayed in the UI");
		
		log.info("INFO: Verify that the I'm Following tab is present ");
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.HomepageImFollowing),
				"ERROR: The I'm Following tab is NOT present");
		
		log.info("INFO: Verify that the I'm Following tab is selected by default after navigating to 'Updates'");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.HomepageImFollowing).getAttribute("class").equals("filterSelected"), 
				"ERROR: The I'm Following tab is NOT selected by default after selecting 'Updates' view");
					
	}
	
	/**
	 * Verifies that the Updates screen is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyUpdatesIsDisplayed(HomepageUI ui,RCLocationExecutor driver ) {
		
		log.info("INFO: Now verifying that the Updates view is displayed in the UI");
		
		log.info("INFO: Verify that the 'Updates' is selected from left navigation");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.UpdatesLink).getAttribute("class").contains("lotusSelected"), 
				"ERROR: The 'Updates' was NOT selected from left navigation menu");
						
		log.info("INFO: Verify that the I'm Following heading is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.HomepageImFollowing).getText().equals("I'm Following"), 
				"ERROR: The I'm Following heading was NOT displayed in the I'm Following view");
		
		log.info("INFO: Verify that the I'm Following description text is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.TabDescription).getText().equals(Data.getData().ImFollowingText), 
				"ERROR: The I'm Following description text was NOT displayed in the I'm Following view");
		
		log.info("INFO:  Verify that the Status Updates heading is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.StatusUpdates).getText().equals("Status Updates"), 
				"ERROR: The Status Updates heading was NOT displayed in the I'm Following view");		
	}
	
	/**
	 * Verifies that the Mentions screen is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyMentionsIsDisplayed(HomepageUI ui,RCLocationExecutor driver) {
		
		log.info("INFO: Now verifying that the Mentions view is displayed in the UI");
		
		log.info("INFO: Verify that the 'Mentions' tab is present ");
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.AtMentionsTab),
				"ERROR: The 'Mentions' tab is present in Mentions view");
		
		log.info("INFO: Verify that the Mentions tab is selected by default after navigating to 'Mentions' view");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.AtMentionsTab).getAttribute("class").equals("filterSelected"), 
				"ERROR: The Mentions tab is NOT selected by default after selectin 'Mentions' view");
								
		log.info("INFO: Verify that the Mentions heading is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.AtMentionsTab).getText().equals("Mentions"), 
				"ERROR: The Mentions heading is NOT displayed in the Mentions view");
		
	}
	/**
	 * Verifies that the Saved screen is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifySavedIsDisplayed(HomepageUI ui,RCLocationExecutor driver) {
		
		log.info("INFO: Now verifying that the Saved view is displayed in the UI");
		
		log.info("INFO: Verify that the 'Saved' tab is present");
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.VisitorSavedTab),
	              "ERROR: The 'Saved' tab is present in Saved view");
		
		log.info("INFO: Verify that the Saved tab is selected by default after navigating to 'Saved' view");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.VisitorSavedTab).getAttribute("class").equals("filterSelected"), 
				"ERROR: The Saved tab is NOT selected by default after selecting 'Saved' view");
				
		log.info("INFO: Verify that the 'Saved' heading is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.VisitorSavedTab).getText().equals("Saved"), 
				"ERROR: The Saved heading is NOT displayed in the Saved view");

		log.info("INFO: Verify that the Saved description text is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.TabDescription).getText().equals(Data.getData().SavedText), 
							"ERROR: The Saved description text was NOT displayed in the saved view");
	}

	/**
	 * Verifies that the My Notifications screen is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyMyNotificationsIsDisplayed(HomepageUI ui,RCLocationExecutor driver) {
		
		log.info("INFO: Verify that the For Me tab is present ");
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.ForMeTab),
				"ERROR: The For Me tab is NOT present in My Notifications view");
		
		log.info("INFO: Verify that the For Me tab is selected by default after navigating to My Notifications view ");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.ForMeTab).getAttribute("class").equals("filterSelected"), 
				"ERROR: The For Me tab is NOT selected by default in My Notifications view");
				
		log.info("INFO: Verify that the For Me heading is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.ForMeTab).getText().equals("For Me"), 
				"ERROR: The For Me heading was NOT displayed in the My Notifications view");
		
		log.info("INFO: Verify that the For Me description text is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.TabDescription).getText().equals(Data.getData().MyNotificationsText), 
				"ERROR: The my notification description text was NOT displayed in the My Notifications view");
		
		log.info("INFO:  Verify that the From Me heading is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.FromMeTab).getText().equals("From Me"), 
				"ERROR: The From Me heading was NOT displayed in the My Notifications view");
	}
	/**
	 * Verifies that the Action Required screen is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyActionRequiredIsDisplayed(HomepageUI ui,RCLocationExecutor driver) {
		
		log.info("INFO: Now verifying that the Action Required view is displayed in the UI");
		
		log.info("INFO: Verify that the 'Action Required' tab is present");
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.ActionRequiredTab),
	              "ERROR: The 'Action Required' tab is present in Action required view");
		
		log.info("INFO: Verify that the Action required tab is selected by default after navigating to Action required view ");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.ActionRequiredTab).getAttribute("class").equals("filterSelected"), 
				"ERROR: The Action required tab is NOT selected by default in Action required view");
				
		log.info("INFO: Verify that the 'Action Required' heading is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.ActionRequiredTab).getText().equals("Action Required"), 
				"ERROR: The 'Action Required' heading is NOT displayed in the Action required view");

		log.info("INFO: Verify that the Action Required description text is displayed");
		Assert.assertTrue(driver.getFirstElement(HomepageUIConstants.TabDescription).getText().equals(Data.getData().ActionRequiredText), 
							"ERROR: The Action Required description text was NOT displayed in the Action required view");

	}
	
	/**
	 * Verifies that the Recommendations widget is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyVisitorRecommendationWidgetIsDisplayed(HomepageUI ui,RCLocationExecutor driver) {
	
		log.info("INFO: Now verifying that the Recommendation Widget is displayed in the UI");
		Assert.assertTrue(ui.isElementPresent(HomepageUIConstants.recommendationsWidet),
				"ERROR: The Recommendation widget was NOT displayed ");
		
		ui.fluentWaitPresent(HomepageUIConstants.recommendationsWidgetContentText);
		log.info("INFO: Verify that staic text for no recommedation is visible under 'Recommendation' widget");
		Assert.assertTrue(ui.isElementVisible(HomepageUIConstants.recommendationsWidgetContentText),
				"ERROR: The Recommendation description text was NOT visible");
		
		log.info("INFO: Verify that there should not be any recommendation available under 'Recommendation' widget");
		Assert.assertFalse(ui.isElementPresent(HomepageUIConstants.recommendationsWidgetContainer), 
				"ERROR: There are Recommendations available in cotainer");		
	}
}
