package com.ibm.conn.auto.webui.onprem;

import static org.testng.Assert.assertTrue;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.webui.DogearUI;

public class DogearUIOnPrem extends DogearUI {

	public DogearUIOnPrem(RCLocationExecutor driver) {
		super(driver);
	}

	@Override
	public void create(BaseDogear dogear){

		String originalWindow = driver.getWindowHandle(); //needed as add bookmark form opens in new window 
		
		/*WebDriver wd = (WebDriver) driver.getBackingObject();*/
		
		//Click Add a Bookmark button
		log.info("INFO: Create a new bookmark using Create a Bookmark button");
		clickLinkWait(DogearUIConstants.AddABookmark);
		
		// Switch to the Bookmark form
		log.info("INFO: Switch to the Bookmark form window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");
		
		//Enter title of bookmark
		log.info("INFO: Entering bookmark title " + dogear.getTitle());
		
		fluentWaitPresent(DogearUIConstants.Form_AddBookmark_Title);
		
		driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Title).isDisplayed();
		
		driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Title).click();
		
		driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Title).type(dogear.getTitle());
		
		// Enter the URL for the bookmark
		log.info("INFO: Entering bookmark URL " + dogear.getURL());

		driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Url).click();
			
		driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Url).type(dogear.getURL());
			
		//Enter the tags for the bookmark if any are provided
		if(dogear.getTags()!= null){
			log.info("INFO: Entering bookmark tags");
			driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Tags).type(dogear.getTags());
		}
		
		//Enter the description for the bookmark if one is provided
		if(dogear.getDescription()!= null){
			log.info("INFO: Entering bookmark discription");
			driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Description).type(dogear.getDescription());
		}
		
		//Scrolling down to save button
		scrollIntoViewElement(BaseUIConstants.SaveButton);
				
		//Choose access type for the bookmark defaults to Public
		log.info("INFO: Selecting the type of bookmark "+ dogear.getAccess().toString());		
		driver.getSingleElement(dogear.getAccess().toString()).click();
		
		//Save the bookmark
		log.info("INFO: Saving the bookmark " + dogear.getTitle());
		//clickLinkWait(SaveButton);
		
		//Added click function with java script
		clickLinkWithJavascript(BaseUIConstants.SaveButton);
		
		//Return to the original window
		log.info("INFO: Return to the original window");
		driver.switchToWindowByHandle(originalWindow);
		fluentWaitTextPresent(dogear.getTitle());		
	}
	
	@Override
	public void delete(BaseDogear dogear){
		
		//Get the id for the for the bookmark
		String bookmarkId = driver.getFirstElement("css=td[class=''] h4 a:contains(" + dogear.getTitle() 
				+ ")").getAttribute("id");
		
		// Select the checkbox associated with the bookmark's id
		log.info("INFO: Selecting checkbox for the bookmark");
		clickLinkWait("css=input[aria-labelledby='"+bookmarkId+"']");
		
		// Click button Delete Selected
		log.info("INFO: Selecting action button Delete Selected");
		clickLinkWait(DogearUIConstants.MyBookmarks_Delete);
		
		log.info("Selecting Delete in confirmation dialog");
		clickLinkWait(DogearUIConstants.ConfirmBookmarkDelete);
		waitForPageLoaded(driver);

	}

	@Override
	public void addAndRemoveFromWatchList(User sUser) {
		
		// Add to Watchlist
		log.info("INFO: Select Add to Watchlist");
		clickLink(DogearUIConstants.MyBookmarks_AddToWatchlist);

		// Verify watcher has being added to list
		log.info("INFO: Select My Watchlist section in left navigation panel");
		clickLink(DogearUIConstants.MyBookmarks_MyWatchList);
		assertTrue(driver.getSingleElement(DogearUIConstants.MyBookmarks_UsersWatchlistedMeRegion).isTextPresent(sUser.getDisplayName()));
		log.info("INFO: Watch user was successfully added to the list");

		// Remove from Watchlist
		log.info("INFO: Select Remove from Watchlist");
		clickLink(DogearUIConstants.MyBookmarks_RemoveFromWatchlist);
		
		// Verify watcher remove from the list
		log.info("INFO: Select My Watchlist section in left navigation panel");
		clickLink(DogearUIConstants.MyBookmarks_MyWatchList);
		assertTrue(driver.getSingleElement("css=div#subsLoad").isTextPresent("No items in Watchlist."));
		log.info("INFO: Watch user was successfully removed from the list");

	}
	
	@Override
	public void editBookmarkTitle(String sExistingTitle, String sNewTitle) {
		
		// Switch to My Bookmarks view as these are the bookmarks the current user owns and can edit
		selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		
		// Click on Display Details View button
		log.info("INFO: Switching the view to display Details");
		if(cfg.getUseNewUI())
			clickLink(DogearUIConstants.BookmarksList_DetailsView+">span[class='bookmark_details_view']");
		else
			clickLink(DogearUIConstants.BookmarksList_DetailsView);
		// Click on the Edit link for the specified bookmark
		String bookmarkIdentifier = "css=input[value='" + sExistingTitle
		+ "'] ~ a:contains(Edit)";  //identifies Edit link for specific bookmark
		clickLink("css=li.lotusFirst a:contains('Edit')");
		log.info("INFO: Select Edit on bookmark item " +sExistingTitle);
		clickLink(bookmarkIdentifier);
		
		// Get original window handle since Edit action opens a new window
		String originalWindow = driver.getWindowHandle();
		
		// Switch to the Bookmark form
		driver.switchToFirstMatchingWindowByPageTitle("Edit Bookmark");

		// Edit the title of the bookmark
		clickLink(DogearUIConstants.Form_AddBookmark_Title);
		log.info("INFO: Clearing field and entering new bookmark title " + sNewTitle);
		driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Title).clear();
		driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Title).type(sNewTitle);
		
		//Scrolling down to save button
		scrollIntoViewElement(BaseUIConstants.SaveButton);
		
		log.info("INFO: Select Save to add bookmark " + sNewTitle+  " to My Bookmarks view");
		//Added click function with java script
		clickLinkWithJavascript(BaseUIConstants.SaveButton);
		//clickButton("Save");

		// Return to original window
		driver.switchToWindowByHandle(originalWindow);

	}

}
