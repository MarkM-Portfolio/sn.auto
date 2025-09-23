package com.ibm.conn.auto.util.eventBuilder.community;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunityBookmarkEvents {

	private static Logger log = LoggerFactory.getLogger(CommunityBookmarkEvents.class);
	
	/**
	 * Creates a new bookmark in a community
	 * 
	 * @param community - The Community instance of the community in which the bookmark is to be created
	 * @param baseBookmark - The BaseDogear instance of the bookmark to be created
	 * @param userCreatingBookmark - The User instance of the user creating the bookmark
	 * @param apiUserCreatingBookmark - The APICommunitiesHandler instance of the user creating the bookmark
	 * @return - The Bookmark instance of the bookmark
	 */
	public static Bookmark createBookmark(Community community, BaseDogear baseBookmark, User userCreatingBookmark, APICommunitiesHandler apiUserCreatingBookmark) {
		
		log.info("INFO: " + userCreatingBookmark.getDisplayName() + " will now create a community bookmark with title: " + baseBookmark.getTitle());
		Bookmark bookmark = apiUserCreatingBookmark.createBookmark(community, baseBookmark);
		
		log.info("INFO: Verify that the community bookmark was created successfully");
		Assert.assertNotNull(bookmark, 
								"ERROR: The community bookmark was NOT created and was returned as null");
		return bookmark;
	}
	
	/**
	 * Edits / updates the description for a bookmark
	 * 
	 * @param bookmark - The Bookmark instance of the bookmark to be updated
	 * @param newBookmarkContent - The String content of the new description to be set to the bookmark
	 * @param userUpdatingBookmark - The User instance of the user editing / updating the bookmark
	 * @param apiUserUpdatingBookmark - The APICommunitiesHandler instance of the user editing / updating the bookmark
	 * @return - The updated Bookmark instance
	 */
	public static Bookmark editBookmarkDescription(Bookmark bookmark, String newBookmarkContent, User userUpdatingBookmark, APICommunitiesHandler apiUserUpdatingBookmark) {
		
		log.info("INFO: " + userUpdatingBookmark.getDisplayName() + " will now update the description for the bookmark with title: " + bookmark.getTitle());
		Bookmark updatedBookmark = apiUserUpdatingBookmark.editBookmarkDescription(bookmark, newBookmarkContent);
		
		log.info("INFO: Verify that the bookmark description update was successful");
		Assert.assertTrue(updatedBookmark.getContent().trim().equals(newBookmarkContent), 
							"ERROR: The bookmark description could NOT be updated as expected");
		return updatedBookmark;
	}
	
	/**
	 * Creates a new community bookmark and then edits / updates the description of that bookmark
	 * 
	 * @param community - The Community instance of the community in which the bookmark is to be created and updated
	 * @param baseBookmark - The BaseDogear instance of the bookmark to be created and updated
	 * @param userCreatingBookmark - The User instance of the user creating and updating the bookmark
	 * @param apiUserCreatingBookmark - The APICommunitiesHandler instance of the user creating and updating the bookmark
	 * @param newBookmarkContent - The String content of the new description to be set to the bookmark
	 * @return - The updated Bookmark instance
	 */
	public static Bookmark createBookmarkAndEditDescription(Community community, BaseDogear baseBookmark, User userCreatingBookmark, APICommunitiesHandler apiUserCreatingBookmark, String newBookmarkContent) {
		
		// Create the new community bookmark
		Bookmark bookmark = createBookmark(community, baseBookmark, userCreatingBookmark, apiUserCreatingBookmark);
		
		// Edit the description of the bookmark
		return editBookmarkDescription(bookmark, newBookmarkContent, userCreatingBookmark, apiUserCreatingBookmark);
	}
	
	/**
	 * Logs in to Communities UI, navigates to the specified bookmark and edits the description of that bookmark
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param community - The Community instance of the community to navigate to in the UI
	 * @param baseCommunity - The BaseCommunity instance of the community to navigate to in the UI
	 * @param bookmarkToEdit - The Bookmark instance of the bookmark whose description is to be edited / updated
	 * @param newBookmarkContent - The String content of the new description to be set to the bookmark
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param userToLogin - The User instance of the user to be logged in and edit the bookmark description
	 * @param apiUserToLogin - The APICommunitiesHandler instance of the user to be logged in and edit the bookmark description
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndEditBookmarkDescription(RCLocationExecutor driver, Community community, BaseCommunity baseCommunity, Bookmark bookmarkToEdit, String newBookmarkContent,
														HomepageUI ui, CommunitiesUI uiCo, User userToLogin, APICommunitiesHandler apiUserToLogin, boolean preserveInstance) {
		// Log into Communities UI and navigate to the community
		CommunityEvents.loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userToLogin, apiUserToLogin, preserveInstance);
		
		// Navigate to bookmarks UI
		log.info("INFO: Select bookmarks from the left navigation menu");
		Community_LeftNav_Menu.BOOKMARK.select(uiCo);
		
		// Navigate to the bookmark to be edited
		navigateToEditBookmark(ui, bookmarkToEdit);
		
		// Edit / update the bookmark description
		editBookmarkDescription(ui, driver, bookmarkToEdit, newBookmarkContent);
		
		// Return to home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);	
	}
	
	/**
	 * Navigates to the edit bookmark UI screen for the specified community bookmark
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param bookmark - The Bookmark instance of the community bookmark to be navigated to in the UI
	 */
	public static void navigateToEditBookmark(HomepageUI ui, Bookmark bookmark) {
		
		log.info("INFO: Wait for the bookmarks UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseBookmarks);
		
		// Determine the ID for the bookmark for use in the CSS selectors
		String bookmarkId = bookmark.getId().toString().trim();
		bookmarkId = bookmarkId.substring(bookmarkId.lastIndexOf("referenceId=") + 12).trim();
		
		String bookmarkMoreLinkCSSSelector = DogearUIConstants.Community_Bookmark_More_Link_Unique.replaceAll("PLACEHOLDER", bookmarkId);
		String bookmarkEditLinkCSSSelector = DogearUIConstants.Community_Bookmark_Edit_Link_Unique.replaceAll("PLACEHOLDER", bookmarkId);
		
		log.info("INFO: Now clicking on the 'More' link for the bookmark with title: " + bookmark.getTitle());
		ui.clickLinkWait(bookmarkMoreLinkCSSSelector);
		
		log.info("INFO: Now clicking on the 'Edit' link for the bookmark with title: " + bookmark.getTitle());
		ui.clickLinkWait(bookmarkEditLinkCSSSelector);
		
		log.info("INFO: Wait for the edit bookmark UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseBookmarks);
	}
	
	/**
	 * Edits / updates the bookmark description for the specified bookmark using the UI
	 * 
	 * @param ui - The Homepage UI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param bookmark - The Bookmark instance of the bookmark to be edited / updated
	 * @param newBookmarkContent - The String content of the new description to be set to the bookmark
	 */
	public static void editBookmarkDescription(HomepageUI ui, RCLocationExecutor driver, Bookmark bookmark, String newBookmarkContent) {
		
		log.info("INFO: Now clicking into the bookmark description field");
		Element bookmarkDescription = driver.getFirstElement(CommunitiesUIConstants.EditBookmarkDescription);
		bookmarkDescription.click();
		
		log.info("INFO: Now clearing the existing bookmark description");
		bookmarkDescription.clear();
		
		log.info("INFO: Now entering the new bookmark description with content: " + newBookmarkContent);
		UIEvents.typeStringWithNoDelay(ui, newBookmarkContent);
		
		log.info("INFO: Now clicking on the 'Save' button to save the changes made to this bookmark");
		UIEvents.clickSaveButton(ui);
		
		log.info("INFO: Wait for the bookmarks UI screen to load with the bookmark title displayed");
		ui.fluentWaitTextPresent(bookmark.getTitle());
	}
}
