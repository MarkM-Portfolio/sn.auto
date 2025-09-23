package com.ibm.conn.auto.lcapi;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseDogear.Access;
import com.ibm.conn.auto.lcapi.common.APIHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.dogear.DogearService;

public class APIDogearHandler extends APIHandler<DogearService>{

	private static final Logger log = LoggerFactory.getLogger(APIDogearHandler.class);
	private String userName;

	public APIDogearHandler(String serverURL, String username, String password) {

		super("dogear", serverURL, username, password);
		userName = username;
		
	}
	
	@Override
	protected DogearService getService(AbderaClient abderaClient, ServiceEntry generalService) {
		try {
			return new DogearService(abderaClient, generalService);
		} catch (LCServiceException e) {
			Assert.fail("Unable create Dogear service: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Creates a standalone bookmark
	 * 
	 * @param bookmark - The BaseDogear instance of the bookmark to be created
	 * @return - The Bookmark instance created if the operation is successful, null otherwise
	 */
	public Bookmark createBookmark(BaseDogear bookmark){
		
		log.info("API: Creating Bookmark:");

		Bookmark testBookmark = new Bookmark(bookmark.getTitle(), bookmark.getDescription(), bookmark.getURL(), bookmark.getTags());
		if(bookmark.getAccess().equals(Access.RESTRICTED))
			testBookmark.setIsPrivate(true);
		
		Entry DogearResult = (Entry) service.createBookmark(testBookmark);
		
		// Validate Bookmark created successfully with a POST check
		log.info("Bookmark Headers: " + service.getDetail());
		log.info("Checking return code of createBookmark call, it should be 201");
		int responseCode = service.getRespStatus();
		if (responseCode != 201){
			log.info("Bookmark not created successfully through API, User name: " + userName);
			Assert.fail("User: " + userName + " received response: " 
					+ responseCode + "; expected: 201; Bookmark was not created");
		}
		log.info("Bookmark successfully created through API");
		log.info("Retrieve that Bookmark for full info");	
		
		// Create a bookmark based on the Entry - this is the bookmark that will be returned (will now include the Bookmark ID)
		Bookmark bookmarkFromEntry = new Bookmark(DogearResult);
		
		// Re-assign the attributes of bookmarkFromEntry to match those stored in testBookmark (critical step for BVT tests)
		bookmarkFromEntry.setTitle(testBookmark.getTitle());
		bookmarkFromEntry.setContent(testBookmark.getContent());
		bookmarkFromEntry.setLink(bookmark.getURL());
		bookmarkFromEntry.setTags(testBookmark.getTags());
		
		if (APIUtils.resultSuccess(DogearResult, "Dogear")) {
			return bookmarkFromEntry;
		} else {
			return null;
		}
	}
	
	/**
	 * Updates the description of any bookmark
	 * 
	 * @param bookmark - The bookmark whose description is to be updated
	 * @param editedContent - The edited content which is to be attached to the bookmark
	 * @return - The updated bookmark instance if the operation is successful, the original and unedited instance otherwise
	 */
	public Bookmark editBookmarkDescription(Bookmark bookmark, String editedContent) {
		
		log.info("INFO: Editing the description of the bookmark with title: " + bookmark.getTitle());
		
		String oldContent = bookmark.getContent();
		bookmark.setContent(editedContent);
		log.info("INFO: The bookmark content will been set to '" + editedContent + "'");
		
		Entry updateResult = (Entry) service.editBookmark(bookmark.getEditLink(), bookmark);
		
		if(updateResult.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The bookmark description has been updated successfully");
			return bookmark;
		} else {
			log.info("ERROR: There was a problem with updating the bookmark description");
			log.info(updateResult.toString());
			bookmark.setContent(oldContent);
			return bookmark;
		}
	}
	
	/**
	 * Notify another user about a bookmark that has been created
	 * 
	 * @param bookmark - The bookmark to be attached to the notification
	 * @param bookmarkOwner - The creator of the bookmark
	 * @param notificationRecipient - The user to whom the notification will be sent
	 * @return - Returns true to indicate that the notification has been sent successfully or false to indicate otherwise
	 */
	public boolean notifyUserAboutBookmark(Bookmark bookmark, APIProfilesHandler bookmarkOwner, 
											APIProfilesHandler notificationRecipient) {
		
		log.info("INFO: Sending " + notificationRecipient.getDesplayName() + " a notification about the bookmark " + bookmark.getTitle());

		// Convert the bookmark back to an entry and set up a new entry to be used for the notification
		Entry bookmarkEntry = bookmark.toEntry();
		Factory factory = new Abdera().getFactory();
		Entry notificationEntry = factory.newEntry();
		
		// Add the notification category to the entry
		notificationEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "notification", null);
		
		log.info("INFO: Setting the recipient email address as: " + notificationRecipient.getEmail());
		Element userEmailElement = notificationEntry.addSimpleExtension(StringConstants.SNX_RECIPIENT_EMAIL, null);
		userEmailElement.setAttributeValue("email", notificationRecipient.getEmail());
		
		log.info("INFO: Extracting the Bookmark ID from the bookmark");
		String bookmarkId = bookmarkEntry.getId().toString();
		String bookmarkLinkId = bookmarkId.substring(bookmarkId.indexOf(":link:") + 6);
		log.info("INFO: The Bookmark ID has been determined to be: " + bookmarkLinkId);
		
		// Now set the bookmark ID to the entry
		Element bookmarkIdElement = notificationEntry.addSimpleExtension(StringConstants.SNX_LINK, null);
		bookmarkIdElement.setAttributeValue("linkid", bookmarkLinkId);
		
		// Set the message to be sent to the recipient
		String message = "Hi-\nI thought you might be interested in this bookmark.\n" + bookmarkOwner.getDesplayName();
		
		log.info("INFO: Notification message content: " + message);
		notificationEntry.setContent(message);
		
		log.info("INFO: Sending " + notificationRecipient.getDesplayName() + " the notification about the bookmark");
		String resultElementString = service.sendBookmark(notificationEntry).toString();
		
		if(resultElementString.indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: Notification successfully sent to " + notificationRecipient.getDesplayName());
			return true;
		} else {
			log.info("INFO: There was an error with sending the notification to " + notificationRecipient.getDesplayName());
			return false;
		}
	}
	
	/**
	 * Deletes a bookmark
	 * 
	 * @param bookmark - The bookmark which is to be deleted
	 * @return - Returns true if the bookmark was deleted successfully, false otherwise
	 */
	public boolean deleteBookmark(Bookmark bookmark){
		
		return service.deleteBookmark(bookmark.getEditLink());
	}
}