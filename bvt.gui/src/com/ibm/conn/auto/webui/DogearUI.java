package com.ibm.conn.auto.webui;

import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseDogear.dogearFields;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.cloud.DogearUICloud;
import com.ibm.conn.auto.webui.onprem.DogearUIOnPrem;

public abstract class DogearUI extends ICBaseUI {

	public DogearUI(RCLocationExecutor driver) {
		super(driver);
	}
	protected static Logger log = LoggerFactory.getLogger(DogearUI.class);

	public static String BookmarksHome_ExpandDetailsOptions_Bookmark(String optionName, int bookmarkPosition){
		return "css=tr[id='details_"+bookmarkPosition+"'] a:contains('"+optionName+"')";
	}
	
	
	public String getBookmarkSelector(BaseDogear bookmark){
		return "css=a:contains('" + bookmark.getTitle() + "')";
	}
	
	public static String getBookmarkLink(BaseDogear bookmark){
		return "css=input[value='" + bookmark.getTitle() + "'] ~ a:contains(Add to My Bookmarks)";
	}
	
	/**
	 * selectCommunityToAddBookmarkTo - method selects the community to add the standalone bookmark to
	 * @param community
	 * @return
	 */
	public static String selectCommunityToAddBookmarkTo(BaseCommunity community){
		return "css=select option:contains('" + community.getName() + "')";
	}
	
	/**
	 * selectedPage - method locates selected page at specified position
	 * @param selectdPage
	 * @return
	 */
	public static String selectedPage(int selectdPage){
		return "//div[contains(@class,'cnx8Paging')]//li[contains(@class,'pagePrevIconList')]/parent::ul//li[text()='"+selectdPage+"']";
	}
	
	/**
	 * selectActivityToAddBookmarkTo - method selects the activity to add the standalone bookmark to
	 * @param activity
	 * @return
	 */
	public static String selectActivityToAddBookmarkTo(BaseActivity activity){
		return "css=select option:contains('" + activity.getName() + "')";
	}
	
	/**
	 * selectBlogToAddBookmarkTo - method selects the blog to add the standalone bookmark to
	 * @param blog
	 * @return
	 */
	public static String selectBlogToAddBookmarkTo(BaseBlog blog){
		return "css=select option:contains('" + blog.getName() + "')";
	}
	
	
	/** End of Selectors section*/
	
	/** Bookmark Views (Tabs)*/
	public enum SelectBookmarkViews {
		MyBookmarks,
		PublicBookmarks,
		Popular,
		MyUpdates;
	}
	
	/**
	 * Creates a bookmark in a community
	 * @param dogear
	 * @param community
	 */
	public void create(BaseDogear dogear, BaseCommunity community){

		log.info("INFO: Create community bookmark for " + community.getName());
		
		// Enter the URL for the bookmark
		log.info("INFO: Entering bookmark URL " + dogear.getURL());
		Assert.assertTrue(fluentWaitPresent(DogearUIConstants.commAddBookmark_Url),
				  "ERROR: Create community bookmark page is not displayed");
		this.driver.getSingleElement(DogearUIConstants.commAddBookmark_Url).click();
		this.driver.getSingleElement(DogearUIConstants.commAddBookmark_Url).type(dogear.getURL());
		
		//Enter title of bookmark
		log.info("INFO: Entering bookmark title " + dogear.getTitle());
		this.driver.getSingleElement(DogearUIConstants.commAddBookmark_Title).type(dogear.getTitle());

		//Enter the description for the bookmark if one is provided
		if(dogear.getDescription()!= null){
			log.info("INFO: Entering bookmark discription");
			this.driver.getSingleElement(DogearUIConstants.commAddBookmark_Description).type(dogear.getDescription());
		}
		
		//Enter the tags for the bookmark if any are provided
		if(dogear.getTags()!= null){
			log.info("INFO: Entering bookmark tags");
			this.driver.getSingleElement(DogearUIConstants.commAddBookmark_Tags).type(dogear.getTags());
		}
		

		
		//Save the bookmark
		log.info("INFO: Saving the bookmark " + dogear.getTitle());
		this.driver.getFirstElement(DogearUIConstants.commAddBookmark_Save).click();
		
	}
	
	/**
	 * Adds the public bookmark to the current user's My Bookmarks view
	 * @param BaseDogear a bookmark object
	 */
	public void addToMyBookmarks(BaseDogear bookmark) {
				
		// Click on the Public Bookmark Tab
		log.info("INFO: Select Public Bookmark from tabs");
		clickLinkWait(DogearUIConstants.Nav_PublicBookmarks);

		// Click on Display Details View button
		log.info("INFO: Switching the view to display Details");
		clickLinkWait(DogearUIConstants.BookmarksList_DetailsView);

		// Click on the Add to My Bookmarks link for the specified bookmark
		log.info("INFO: Select Add to My Bookmarks on bookmark item " + bookmark.getTitle());
		clickLinkWait(getBookmarkLink(bookmark));

		// Get original window handle since Add to My Bookmarks action opens a new window
		String originalWindow = driver.getWindowHandle();

		// Switch to the Bookmark form
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");

		// Add the bookmark to My Bookmarks
		log.info("INFO: Select Save to add bookmark " + bookmark.getTitle() +  " to My Bookmarks view");
		
		//Added click function with java script
		clickLinkWithJavascript(DogearUIConstants.SubmitSaveButton);

		// Return the to original window
		driver.switchToWindowByHandle(originalWindow);
	}
	
	/**
	 * Selects the specified Bookmark views (tabs)
	 * @param sViewChosen - view to be selected
	 */
	public void selectBookmarkView(SelectBookmarkViews sViewChosen) {
	
		final String sBookmarkViewChoice;
		
		switch(sViewChosen) {
		case MyBookmarks:
			sBookmarkViewChoice = DogearUIConstants.Nav_MyBookmarks;
		break;
		case PublicBookmarks:
			sBookmarkViewChoice = DogearUIConstants.Nav_PublicBookmarks;
		break;
		case Popular:
			sBookmarkViewChoice = DogearUIConstants.Nav_Popular;

		break;
		case MyUpdates:
			sBookmarkViewChoice = DogearUIConstants.Nav_MyUpdates;
		break;
		default:
			//this is default no change needed
			log.info("WARNING: No selected made");	  
			sBookmarkViewChoice = "";
			break;
		}
		
		log.info("Left nav link chosen is: "+sBookmarkViewChoice);
		driver.getFirstElement(sBookmarkViewChoice).click();
	}
	
	/**
	 * Clicks on More Actions link for a particular bookmark.
	 * @param bookmark - The bookmark for which More Actions should be clicked on.
	 */
	public void ClickOnMoreActionsLinkForTheGivenBookmark(BaseDogear bookmark){
		
		List <Element> allBookmarks = driver.getVisibleElements(DogearUIConstants.Bookmark_Link);
		for (int i = 0; i < allBookmarks.size(); i++)
			if (allBookmarks.get(i).getText().equals(bookmark.getTitle()))
				driver.getVisibleElements(DogearUIConstants.MoreActionsLink).get(i).click();
		
	}
	
	/**
	 * 
	 * Creates a bookmark corresponding with the BaseDogear bookmark passed in the parameter
	 * Must be at location where the "Add a Bookmark" or "Add Bookmark" button is available
	 * 
	 * This method has different steps for the DogearUICloud and DogearUIOnPrem, please see those implementations for more details.
	 * 
	 * @see DogearUICloud 
	 * @see DogearUIOnPrem
	 * @see DogearUIProduction
	 * @param BaseDogear A bookmark (usually returned with the BaseDogear.Builder after .build is called)
	 * 
	 * @author djones
	 */
	public abstract void create(BaseDogear dogear);
	
	/**
	 * Deletes an existing bookmark using a dogear base state object
	 * Must be located in My Bookmark view when deleting
	 * NOTE: Cloud does not support Native Dogear
	 * @param BaseDogear - dogear object
	 */
	public abstract void delete(BaseDogear dogear);

	/**
	 * Adds the user to the Watchlist and verify user is listed 
	 * Then immediately removes the user and verify user is no longer listed
	 * @param sUser - user logged in
	 */
	public abstract void addAndRemoveFromWatchList(User sUser);

	/**
	 * Edit the title of the bookmark from My Bookmarks view
	 * @param sExistingTitle - title of the existing bookmark to find in the view
	 * @param sNewTitle - new title for the bookmark
	 * @throws Exception
	 */
	public abstract void editBookmarkTitle(String sExistingTitle, String sNewTitle);
			
	public void gotoAddBookmark() {
		clickLink(DogearUIConstants.AddBookmark);
	}
	
	/**
	 * Deletes all existing bookmark via "Select All" button
	 * Must be located in My Bookmark view when deleting
	 * NOTE: Cloud does not support Native Dogear
	 **/
	public void deleteAllMyBookmarks(){
		//Get original window handle
		String originalWindow = driver.getWindowHandle();
				
		this.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		
		clickLink(DogearUIConstants.MyBookmarks_SelectAll);
		clickLink(DogearUIConstants.MyBookmarks_Delete);
		// Accept to Delete bookmarks
		clickLink(DogearUIConstants.ConfirmBookmarkDelete);
		assertTrue(driver.isTextPresent(Data.getData().NoBookmarks), "Delete All Bookmarks failed.");
		// Switch to original window
		driver.switchToWindowByHandle(originalWindow);
	}

	/**
	 * Add tag for all my bookmarks
	 * Must be located in My Bookmarks view
	 * NOTE: Cloud dose not support Native dogear
	 */
	public void addTagsForAllMyBookmarks(String multiTags){
		this.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
        clickLink(DogearUIConstants.MyBookmarks_SelectAll);
		clickLink(DogearUIConstants.MyBookmarks_MoreActions);

		driver.getSingleElement(DogearUIConstants.MyBookmarks_AddTags).click();
		
		//verify "Tags Required" message shows when no tags input
		driver.getSingleElement(DogearUIConstants.MyBookmarks_MultiTags).type("");
		driver.getFirstElement(DogearUIConstants.commAddBookmark_Save).click();
		assertTrue(driver.getSingleElement(DogearUIConstants.MyBookmarks_TagIsRequiredMsg).isTextPresent(Data.getData().TagIsRequired),
				"Tag is Required window is not shown out.");
		driver.getSingleElement(DogearUIConstants.MyBookmarks_CloseAlertMsg).click();
		
		driver.getSingleElement(DogearUIConstants.MyBookmarks_MultiTags).type(multiTags);
		this.isTextPresent(multiTags);
		driver.getFirstElement(DogearUIConstants.commAddBookmark_Save).click();
		//verify "New tags have been added to the selected bookmarks." confirm message shows correctly on UI
		assertTrue(driver.getSingleElement(DogearUIConstants.SuccessMsgBox).isTextPresent("New tags have been added to the selected bookmarks."),
				"Add Tags failed.");
		
		this.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		//count bookmarks amount in My Bookmarks tab page
		String pagingString;
		pagingString = driver.getSingleElement(DogearUIConstants.pagingBox).getText();
		String[] myBookmarkNum = pagingString.split(" ");
		
		//Verify the new tags have been added successfully for all my bookmarks
		String AddTags = "//a[text()='"+ multiTags +"']";
		assertTrue(driver.getElements(AddTags.toLowerCase()).size()>=Integer.parseInt(myBookmarkNum[2]),
				multiTags + " add to bookmarks failed.");
	}
	/**
	 * Replace tags from My Bookmarks
	 * Must located in My Bookmarks view
	 * NOTE: Cloud dose not support Native Dogear
	 * @param oldTag - the existing tag in anyone of my bookmarks
	 * @param newTags - the new tag/tags you want to replace, use comma in the string to separate tags
	 */
	public void replaceTagFromMyBookmarks(String oldTag, String newTags){
		this.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		String RemoveTag = "//a[text()='"+ oldTag +"']";
		String AddTags = "//a[text()='"+ newTags +"']";
		int oldTagcount = driver.getElements(RemoveTag.toLowerCase()).size();
		
        clickLink(DogearUIConstants.MyBookmarks_SelectAll);
		clickLink(DogearUIConstants.MyBookmarks_MoreActions);
		
		driver.getSingleElement(DogearUIConstants.MyBookmarks_ReplaceTag).click();
		
		//Verify Non-input confirm message shows correctly
		driver.getVisibleElements(DogearUIConstants.commAddBookmark_Save).get(0).click();
		assertTrue(driver.getSingleElement(DogearUIConstants.MyBookmarks_OldTagIsRequired).isTextPresent(Data.getData().OldTagIsRequired),
				"Old tag is required window prompts failed.");
		driver.getSingleElement(DogearUIConstants.MyBookmarks_CloseAlertMsg).click();
		
		driver.getSingleElement(DogearUIConstants.MyBookmarks_OldTag).type(oldTag);
		driver.getSingleElement(DogearUIConstants.MyBookmarks_NewTags).click();

		driver.getVisibleElements(DogearUIConstants.commAddBookmark_Save).get(0).click();
		assertTrue(driver.getSingleElement(DogearUIConstants.MyBookmarks_NewTagsIsRequired).isTextPresent(Data.getData().NewTagIsRequired),
				"New tag(s) is required window prompts failed.");
		driver.getSingleElement(DogearUIConstants.MyBookmarks_CloseAlertMsg).click();
		
		driver.getSingleElement(DogearUIConstants.MyBookmarks_NewTags).type(newTags);
		driver.getVisibleElements(DogearUIConstants.commAddBookmark_Save).get(0).click();
		
		String successMsg = driver.getSingleElement(DogearUIConstants.SuccessMsgBox).getText();
		if(successMsg.equals(Data.getData().partiallyReplaceSuccessMsg))
			assertTrue(driver.getSingleElement(DogearUIConstants.SuccessMsgBox).isTextPresent(Data.getData().partiallyReplaceSuccessMsg),
					"Replace tag dose not complete successfully.");
		else if(successMsg.equals(Data.getData().ReplaceSuccessMsg))
			assertTrue(driver.getSingleElement(DogearUIConstants.SuccessMsgBox).isTextPresent(Data.getData().ReplaceSuccessMsg),
					"Replace tag dose not complete successfully.");
		else
			assertTrue(false, "Replace tag doesn't complete successfully.");
		
		clickLink(DogearUIConstants.Nav_MyBookmarks);
		//Verify the new tags have been added successfully for all my bookmarks		
		assertTrue(driver.getElements(RemoveTag.toLowerCase()).size()==0,
				RemoveTag + "remove from bookmarks failed.");
		assertTrue(driver.getElements(AddTags.toLowerCase()).size()==oldTagcount,
				newTags + "replace to bookmarks failed.");
	}
	/**
	 * Delete existing tags in my bookmarks
	 * Must be located in My Bookmarks view
	 * NOTE: Cloud dose not support Native Dogear
	 */
	public void deleteTagsFromMyBookmarks(String delTags){
		this.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
        clickLink(DogearUIConstants.MyBookmarks_SelectAll);
		clickLink(DogearUIConstants.MyBookmarks_MoreActions);
		driver.getSingleElement(DogearUIConstants.MyBookmarks_DeleteTags).click();
		
		//Verify Non-input confirm message shows correctly
		driver.getVisibleElements(DogearUIConstants.commAddBookmark_Save).get(0).click();
		assertTrue(driver.getSingleElement(DogearUIConstants.MyBookmarks_TagIsRequiredMsg).isTextPresent(Data.getData().TagIsRequired),
				"Tag is required window prompts failed.");
		driver.getSingleElement(DogearUIConstants.MyBookmarks_CloseAlertMsg).click();
		driver.getSingleElement(DogearUIConstants.MyBookmarks_DeleteTag).type(delTags);
		driver.getVisibleElements(DogearUIConstants.commAddBookmark_Save).get(0).click();
		assertTrue(driver.getSingleElement(DogearUIConstants.SuccessMsgBox).isTextPresent(Data.getData().DeleteMsg),
				"Delete tag dose not complete successfully.");
	}
	
	/**
	 * Get the UUID of the bookmark
	 * @param BaseDogear a bookmark object
	 * @return String containing the UUID
	 */
	public String getUUID(BaseDogear dogear) {
		String href = driver.getFirstElement(getBookmarkSelector(dogear)).getAttribute("href");
			return href.substring(href.lastIndexOf("link=") + 5);					
	}

	public void edit(BaseDogear dogear){
		
	}
	
	public void edit(BaseDogear bookmark, BaseCommunity community){
		
		//switch to bookmark
		log.info("INFO: Select Bookmark from left navigation menu");
		Community_LeftNav_Menu.BOOKMARK.select(this);
		
		editBookmark(bookmark, community);
	}
	
	public void editUsingDropdown(BaseDogear dogear){
		
	}
	
	public void editUsingDropdown(BaseDogear bookmark, BaseCommunity community){
		
		//switch to bookmark
		log.info("INFO: Select Bookmark from left navigation menu");
		Community_TabbedNav_Menu.BOOKMARK.select(this);
		
		editBookmark(bookmark, community);
	}
	
	public void editBookmark(BaseDogear bookmark, BaseCommunity community){

		//collect all bookmarks
		log.info("INFO: Collect all bookmarks");
		List<Element> bookmarks = driver.getElements("css=tr[id^='b_summary_']");
		
		log.info("INFO: Found " + bookmarks.size() + " bookmark(s)");	
		String id = bookmarks.get(0).getAttribute("id");
		
		//select more
		log.info("INFO: Select more from our bookmark");
		clickLinkWait("css=a[id='" + id.replace("summary", "show") + "']");
				
		//select edit
		log.info("INFO: Select edit from our bookmark");
		clickLinkWait("css=a[id='" + id.replace("summary", "edit") + "']");
		
		//Iterate through list of edits to the object
		log.info("INFO: Iterate through the changes");
		Iterator<dogearFields> iterator = bookmark.getEdits().iterator();
        while(iterator.hasNext()){

            switch (iterator.next()) {
 
            case TITLE:
            	 log.info("INFO: Change title of bookmark");
    			 clearText(DogearUIConstants.editBookmarkTitle);
    			 typeText(DogearUIConstants.editBookmarkTitle, bookmark.getTitle());
            	 break;

            case URL:
    			 log.info("INFO: Change URL of bookmark");
    			 clearText(DogearUIConstants.editBookmarkURL);
    			 typeText(DogearUIConstants.editBookmarkURL, bookmark.getURL());
            	 break;
            	
            case TAGS:
   			 	log.info("INFO: Change Tag of bookmark");
   				clearText(DogearUIConstants.editBookmarkTag);
   				typeText(DogearUIConstants.editBookmarkTag, bookmark.getTags());;
   			 	break;

            case DESCRIPTION:
   			 	log.info("INFO: Change Description of bookmark");
   			 	clearText(DogearUIConstants.editBookmarkDescription);
   			 	typeText(DogearUIConstants.editBookmarkDescription, bookmark.getDescription());
   			 	break;
             
            
            default: break;
            }
            
            
        }
		//save changes
        log.info("INFO: Save the changes to the bookmark");
		clickLinkWithJavascript(DogearUIConstants.commAddBookmark_Save);
	}
	
	/**
	 * Determine whether GUI under test is onprem or cloud
	 *
	 */
	public static DogearUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  DogearUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  DogearUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new  DogearUICloud(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  DogearUIOnPrem(driver);
		} else if(product.toLowerCase().equals("multi")) {
			return new  DogearUIOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
}
