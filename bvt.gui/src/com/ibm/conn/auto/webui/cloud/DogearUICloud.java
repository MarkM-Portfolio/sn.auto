package com.ibm.conn.auto.webui.cloud;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;

public class DogearUICloud extends DogearUI {
	
	//override DogearUI String
	public static final String Form_AddBookmark_Url = "css=textarea#addBookmarkUrl"; //addBookmarkUrl
	public static final String Form_AddBookmark_Title = "css=input#addBookmarkName";
	public static final String Form_AddBookmark_Tags = "css=input#autocompletetags2";
	public static final String Form_AddBookmark_Description = "css=textarea#addBookmarkDescription";
	
	public DogearUICloud(RCLocationExecutor driver) {
		super(driver);
	}

	@Override
	public void selectBookmarkView(SelectBookmarkViews sViewChosen) {
		//do nothing as this is not supported in SC
		
	}
	
	/**
	 * <ul>
	 * <li>Modeled after DogearUI's implementation </li>
	 * <li>Creates a bookmark using a dogear base state object </li>
	 * <li>Must be at location where the "Add Bookmark" button is available </li>
	 * </ul>
	 * @param BaseDogear - dogear object
	 * @author Matt Maffa
	 */
	@Override
	public void create(BaseDogear dogear) {
		//Click the Add Bookmark button
		log.info("INFO: Create a new bookmark using Create Bookmark button");
		clickLinkWait(DogearUIConstants.AddBookmark); //on cloud it is "Create Bookmark", not "Create A Bookmark"
		
		//Type in the URL field
		log.info("INFO: Entering bookmark URL " + dogear.getURL());
		driver.getSingleElement(Form_AddBookmark_Url).type(dogear.getURL());
				
		//Type in the  title of bookmark
		log.info("INFO: Entering bookmark title " + dogear.getTitle());
		driver.getSingleElement(Form_AddBookmark_Title).type(dogear.getTitle());

		//Enter the description for the bookmark if one is provided
		if(dogear.getDescription()!= null){
			log.info("INFO: Entering bookmark discription");
			driver.getSingleElement(Form_AddBookmark_Description).type(dogear.getDescription());
		}
		
		//Enter the tags for the bookmark if any are provided
		if(dogear.getTags()!= null){
			log.info("INFO: Entering bookmark tags");
			driver.getSingleElement(Form_AddBookmark_Tags).type(dogear.getTags());
		}
		
		//Save the bookmark
		log.info("INFO: Saving the bookmark");
		clickLink(DogearUIConstants.commAddBookmark_Save);
	}

	@Override
	public void delete(BaseDogear dogear) {
		//NOTE: Cloud does not support Native Dogear
		
	}
	
	@Override
	public void addAndRemoveFromWatchList(User sUser) {
		//do nothing as this is not supported in SC
		
	}
	
	@Override
	public void editBookmarkTitle(String sExistingTitle, String sNewTitle) {
		//edit the bookmark
		clickLink("css=tbody tr td a[aria-label='More']");
		clickLink("css=div ul li a:contains(Edit)");
		clearText(DogearUIConstants.Form_EditBookmark_Title);
		typeText(DogearUIConstants.Form_EditBookmark_Title, sNewTitle);
		clickButton("Save");
		fluentWaitTextPresent(sNewTitle);
	}
	
	public void deleteCommBookmark(String title) throws Exception {
		clickLink("css=tbody tr td a[aria-label='More']");
		clickLink("css=div ul li a[aria-label='Delete']");
		clickButton("OK");
		waitForPageLoaded(driver);
		driver.isTextNotPresent(title);

	}
}
