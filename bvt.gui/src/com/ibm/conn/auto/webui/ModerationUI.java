package com.ibm.conn.auto.webui;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import java.util.List;
import java.util.Set;
import org.testng.Assert;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.util.Helper;


public class ModerationUI extends ICBaseUI {

	public ModerationUI(RCLocationExecutor driver) {
		super(driver);
	}

	protected static Logger log = LoggerFactory.getLogger(ModerationUI.class);

	public static String ApproveBtn = "css=button.lotusBtn:contains('Approve')";
	public static String RejectBtn = "css=button.lotusBtn:contains('Reject')";
	public static String DeleteBtn = "css=button.lotusBtn:contains('Delete')";
	public static String EditBtn = "css=button.lotusBtn:contains('Edit')";
	public static String QuarantineBtn = "css=button.lotusBtn:contains('Quarantine')";
	public static String DismissBtn = "css=button.lotusBtn:contains('Dismiss')";
	public static String RestoreBtn = "css=button.lotusBtn:contains('Restore')";
	public static String ModerationLink = "link=Moderation";
	public static String FirstCheckBox = "css=input[type=checkbox][title='Select entry']";
	public static String RejectedTab = "css=a:contains('Rejected')";
	public static String QuarantinedTab = "css=a:contains('Quarantined')";

	//elements in community pages
	public static String BlogNewFirstEntryLink = "link=Create Your First Entry";
	public static String BlogNewEntryLink = "link=Create Blog Entry";
	public static String IdeaNewFirstIdeaLink = "link=Create Your First Idea";
	public static String IdeaNewIdeaLink = "link=Contribute an Idea";
	public static String ForumNewFirstTopicLink = "link=Start the First Topic";
	public static String ForumStartTopicLink = "link=Start a Topic";
	public static String FileNewFirstFileLink = "link=Add Your First File";
	public static String FileNewFileLink = "link=Add Files";
	public static String FlagLink = "link=Flag as Inappropriate";
	public static String FileCommentFlagLink = "css=a[title='Flag as Inappropriate']";
	public static String fileCommentDiv = "css=div[id^='uniqName_10_']";
	public static String FilesViewerCommentBox = "css=body[class='cke_editable cke_editable_themed cke_contents_ltr lconnRTE lotusui30 lotusui30_body lotusui30dojo lotusui30_fonts ics-viewer-width-auto']";
	public static String FilesViewerPostButton = "css=a:contains('Post')";
	public static String CloseX = "css=a[class=lotusDelete][title=Close]";
	public static String BlogFlagEntryLink = "css=td[id='focusFlagAsInappropriateLink_text'][role='presentation']"; // Flag
	public static String BlogFlagEntrySubmitBtn = "css=input[value='Submit'][name='submitBtn'][class='lotusFormButton blogsPaddingRight']"; // Submit
	public static String BlogTextAreaFlag = "css=textarea[id=input_comment]";
	public static String BlogCommentMoreActionsLink = "css=a[id^='moreActions'][role=button][class=lotusAction]";
	public static String BlogCommentFlagLink = "css=tbody[class='dijitReset'] tr:contains(Flag as inappropriate)";
	public static String BlogCommentTextAreaFlag = "css=textarea[id='flag_comment'][name='comment']";
	public static String BlogEntryCommentBox = "css=body[class='cke_editable cke_editable_themed cke_contents_ltr lconnRTE lotusui30 lotusui30_body lotusui30dojo lotusui30_fonts lotusTextExpanded']";
	
	//Elements in Forums
	public static String ForumTextAreaFlag = "css=textarea[id*=lconn_forums_FlagInappropriate]"; // Flag
	public static String ForumFlagSubmitBtn = "css=input[value='Flag'][type='button'][class='lotusFormButton submit']"; // Flag
	
	//Elements in Files
	public static String FileFlagLink = "css=tbody[class='dijitReset'] tr:contains(Flag as Inappropriate)";
	public static String FileTextAreaFlag = "css=textarea[id*='_textBox_Unique']";
	public static String FileFlagSubmitBtn = "css=input[type='submit'][value='Flag']";
	public static String FileFlagBtn = "link=Flag";
	public static String FileMoreAction = "css=div[id^='uniqName_17_'] span[class='dijitReset dijitInline dijitIcon dropdown-menu-icon']";
	
	public static String BlogSubmitMessage = "submitted to moderator for approval";
	public static String SubmitForReveiwMessage = "submitted for review";
	public static String BlogCommentSubmitMessage = "submitted to the moderator for approval";
	public static String okBtn = "css=input[value='OK']";
	public static String SucMsg = "successfully";

	public static String RejectReason = "css=textarea[id*='lconn_share_widget_Dialog'][name='_reason']";
	public static String BlogEditTitle = "css=input[id=title]";
	public static String BlogSaveBtn = "css=input[id=postEntryID]";
	public static String BlogEditWindowTitle = "Blogs: Edit Entry";
	public static String ForumEditWindowTitle = "Forums";
	public static String ForumEditTitleInput = "css=input[id*='lconn_forums_PostForm'][title='Title:']";
	public static String ForumEditTitleLink = "link=Edit Title";

	/**
	 * open Community Moderation page from community application (Must loadComponent(community) and login first)
	 * @param uuid - "communityUuid=***"
	 */
	public void openCommunityModerationByUUID(String uuid) {
		String comModURI = driver.getCurrentUrl();

		String moduuid = uuid.replace("communityUuid", "proxyCommunityUuid");

		comModURI = comModURI.replaceAll(
				"communities/service/html/ownedcommunities",
				"moderation/communitiesapp?" + moduuid);

		driver.navigate().to(comModURI);

	}


	/**
	 * Approve the first item in moderation list page
	 * @param item
	 */
	public void approveFirstItem(String item) {

		//First verify the item exists in list
		log.info("INFO: Approve the first item from moderation list");
		Assert.assertTrue(driver.isTextPresent(item), item
				+ " does not exist in this list.");

		driver.getSingleElement(FirstCheckBox).click();

		//Click "Approve" button
		driver.getSingleElement(ApproveBtn).click();

		//Click "OK" button on approve dialog
		clickOKButton();

	}

	/**
	 * Flag one blog entry as inappropriate from entry page
	 */
	public void flagBlogEntry() {
		// Click "More Actions" of blog entry
		log.info("INFO: Select the More Actions link for entry");
		clickLink(BlogsUIConstants.MoreActions);

		// Click "Flag as inappropriate" link of entry
		log.info("INFO: Select the Flag as Inappropriate link for entry");
		clickLink(BlogFlagEntryLink);

		// Type flag reason in text area
		log.info("INFO: Type flag reason for entry");
		typeText(BlogTextAreaFlag, "Flag as Inappropriate");

		// Submit flagging
		log.info("INFO: Submit the flagged entry");
		clickLinkWait(BlogFlagEntrySubmitBtn);
	}


	/**
	 * flag all forum posts from the opened post page. (there are one or more posts on the page)
	 */
	public void flagForumPosts() {

		List<Element> flagLinks = driver
				.getVisibleElements(FlagLink);

		for (Element curElem : flagLinks) {
			// Click "Flag as inappropriate" link of entry
			log.info("INFO: Select the Flag as Inappropriate link for entry");
			curElem.click();

			// Type flag reason in text area
			log.info("INFO: Type flag reason for entry");
			typeText(ForumTextAreaFlag, "Flag as Inappropriate");

			// Submit flagging
			log.info("INFO: Submit the flagged entry");
			clickLinkWait(ForumFlagSubmitBtn);
		}

	}

	/**
	 * Flag one file as inappropriate from File page.
	 */
	public void flagFile() {
		// Click "Flag as inappropriate" link of entry
		log.info("INFO: Select the Flag as Inappropriate link for entry");
		
		String FileMoreAction = "css=div[id^='uniqName_17_'] span[class='dijitReset dijitInline dijitIcon dropdown-menu-icon']";
		driver.getFirstElement(FileMoreAction).click();
		
		//clickLink(FilesUI.ClickForActions);
		clickLink(FileFlagLink);

		// Type flag reason in text area
		log.info("INFO: Type flag reason for entry");
		typeText(FileTextAreaFlag, "Flag as Inappropriate");

		// Submit flagging
		log.info("INFO: Submit the flagged entry");
		getFirstVisibleElement(FileFlagBtn).click();
	}


	/**
	 * Flag all blog comments as inappropriate from entry page. (when there are many comments)
	 */
	public void flagBlogComments() {

		List<Element> flagLinks = driver
				.getVisibleElements(ModerationUI.BlogCommentMoreActionsLink);

		for (Element curElem : flagLinks) {
			// Click "Flag as inappropriate" link of entry
			log.info("INFO: Select the Flag as Inappropriate link for entry");
			// clickLink(BlogCommentMoreActionsLink);
			curElem.click();

			clickLink(BlogCommentFlagLink);
			// Type flag reason in text area
			log.info("INFO: Type flag reason for entry");
			typeText(BlogCommentTextAreaFlag, "Flag as Inappropriate");

			// Submit flagging
			log.info("INFO: Submit the flagged entry");
			clickLinkWait(ModerationUI.okBtn);
		}
	}

	/**
	 * Flag all file comments as inappropriate from File page (there are many comments).
	 */
	public void flagFileComments() {

		
		//click on comment area to display "flag" icon
		List<Element> commentDiv = driver
				.getVisibleElements(fileCommentDiv);
		
		for (Element curElem : commentDiv) {
			// Click "Flag as inappropriate" link of entry
			log.info("INFO: Select the Flag as Inappropriate link for entry");
			curElem.click();

			fluentWaitElementVisible(FileCommentFlagLink);
			
			driver.getFirstElement(FileCommentFlagLink).click();
			// Type flag reason in text area
			log.info("INFO: Type flag reason for entry");
			typeText(FileTextAreaFlag, "Flag as Inappropriate");

			// Submit flagging
			log.info("INFO: Submit the flagged file comment");
			getFirstVisibleElement(FileFlagBtn).click();
		//	clickLink(ModerationUI.CloseX);
			// driver.getVisibleElements(ModerationUI.FileFlagSubmitBtn).
		}
	}

	/**
	 * Click "Approve" button and then approve the item.
	 */
	public void ApproveItem() {
		//Click "Approve" button
		log.info("INFO: Approve the item");
		clickLink(ApproveBtn);
		fluentWaitTextPresent("Are you sure you want to");

		//Click "OK" button on approve dialog
		driver.getFirstElement(BaseUIConstants.OKButton).click();
		//Verify there is "successful" message
		Assert.assertTrue(fluentWaitTextPresent("successfully"),
				"ERROR: Failed to approve the item.");
	}

	/**
	 * click "Reject" button, input rejecting reason and then reject the item
	 */
	public void RejectItem() {
		//Click "Reject" button
		log.info("INFO: Reject the item");
		clickLink(RejectBtn);
		fluentWaitTextPresent("Reason for rejecting");

		//Input rejecting reason on the dialog
		log.info("INFO: input rejecting reason");
		typeText(RejectReason, "Reject");

		//click "OK" button and verify there's "successfully" message
		driver.getFirstElement(BaseUIConstants.OKButton).click();
		Assert.assertTrue(fluentWaitTextPresent("successfully"),
				"ERROR: Failed to reject the item!");
	}

	/**
	 * Click "Delete" button and delete the item
	 */
	public void DeleteItem() {
		//Click "Delete" button
		log.info("INFO: Delete the item");
		clickLink(DeleteBtn);
		fluentWaitTextPresent("Are you sure you want to");
		
		//click "OK" button and verify there's "successfully" message
		driver.getFirstElement(BaseUIConstants.OKButton).click();
		Assert.assertTrue(fluentWaitTextPresent("successfully"),
				"ERROR: Failed to delete the item!");
	}

	/**
	 * Click "Quarantine" button and quarantine the item
	 */
	public void QuarantineItem() {
		//click "Quarantine" button
		log.info("INFO: Quarantine the item");
		clickLink(QuarantineBtn);
		fluentWaitTextPresent("Reason for quarantining");

		//input "quarantine reason" 
		log.info("INFO: input quarantine reason");
		typeText(RejectReason, "quarantine");

		driver.getFirstElement(BaseUIConstants.OKButton).click();
		Assert.assertTrue(fluentWaitTextPresent("successfully"),
				"ERROR: Failed to quarantine the item!");
	}

	/**
	 * Click "Dismiss" button and dismiss the item
	 */
	public void DismissItem() {
		//Click "Dismiss" button
		log.info("INFO: Dismiss the item");
		clickLink(DismissBtn);
		fluentWaitTextPresent("Reason for dismissing");

		//input dismiss reason
		log.info("INFO: input dismissing reason");
		typeText(RejectReason, "dismiss");

		log.info("INFO: verify the dismiss action is successful");
		driver.getFirstElement(BaseUIConstants.OKButton).click();
		Assert.assertTrue(fluentWaitTextPresent("successfully"),
				"ERROR: Failed to dismiss the item!");
	}

	/**
	 * Click "Restore" button and restore the quarantined item
	 */
	public void RestoreItem() {
		//Click "Restore" button
		log.info("INFO: Restore the item");
		clickLink(RestoreBtn);
		fluentWaitTextPresent("Reason for restoring");

		//Input restoring reason
		log.info("INFO: Input the restore reason");
		typeText(RejectReason, "restore");

		log.info("INFO: verify the restore action is successful");
		driver.getFirstElement(BaseUIConstants.OKButton).click();
		Assert.assertTrue(fluentWaitTextPresent("successfully"),
				"ERROR: Failed to restore the item!");
	}

	/**
	 * Click "Edit" button and edit the blog entry (change the title) from landing page of the entry
	 * @return - return the new entry title.
	 */
	public String EditBlogEntry() {
		String mainWidow = driver.getWindowHandle();

		//Click "Edit" button
		log.info("INFO: Edit the blog entry");
		clickLink(EditBtn);

		driver.switchToFirstMatchingWindowByPageTitle(BlogEditWindowTitle);

		fluentWaitTextPresent("Edit Entry");

		String randEditTitle = "Edit" + Helper.genDateBasedRand();
		
		//Change the entry title to "randnum_entryTitle"
		log.info("INFO: Input the new entry title");
		typeText(BlogEditTitle, randEditTitle + "_");
		clickLink(BlogSaveBtn);

		Assert.assertTrue(fluentWaitTextPresent("successfully"),
				"ERROR: Failed to edit the item!");

		this.close(cfg);

		log.info("INFO: Verify the title of entry is changed");
		driver.switchToWindowByHandle(mainWidow);
		Assert.assertTrue(fluentWaitTextPresentRefresh(randEditTitle),
				"ERROR: Failed to edit the item!");

		return randEditTitle;
	}

	/**
	 * Click "Edit" button and edit the Forum post (change the title) from landing page of the post
	 * @return - return the new post title.
	 */
	public String EditForumPost() {
		String mainWidow = driver.getWindowHandle();

		//Click "Edit" button
		log.info("INFO: Edit the forum post");
		clickLinkWait(EditBtn);

		Set<String> handles = driver.getWindowHandles();
		String randEditTitle = "Edit" + Helper.genDateBasedRand();

		for (String a : handles) {

			driver.switchToWindowByHandle(a.toString());
			log.info("Switch to new window: " + driver.getTitle());
			
			if (!driver.getWindowHandle().equalsIgnoreCase(mainWidow)) {
				String editWindow = a.toString();
				log.info("Got Edit window: " + editWindow);

				if (driver.isElementPresent(ForumEditTitleLink)) {
					clickLink(ForumEditTitleLink);
				}

				//change the title to "randnum_postTitle"
				typeText(ForumEditTitleInput, randEditTitle + "_");
				clickLink(BaseUIConstants.SaveButton);
				Assert.assertTrue(fluentWaitTextPresent(randEditTitle),
						"ERROR: Failed to edit the item!");

				this.close(cfg);

				log.info("INFO: Verify the new post title is changed");
				driver.switchToWindowByHandle(mainWidow);
				Assert.assertTrue(fluentWaitTextPresentRefresh(randEditTitle),
						"ERROR: Failed to edit the item!");
				break;
			}
		}
		return randEditTitle;
	}

	/**
	 * Open the landing page of the moderated item
	 * @param item - the item title shown in moderation list
	 */
	public void openLandingPage(String item) {
		String itemLink = "link=" + item;
		
		Assert.assertTrue(fluentWaitTextPresent(item),
				"ERROR: The item does not exit in Moderation list.");
		getFirstVisibleElement(itemLink).click();
	}

	/**
	 * verify pre-moderation of items. Approve/Reject/Delete pending item, Approve/Delete rejected item on landing page
	 * @param items - the item titles shown in moderation list
	 */
	public void verifyPreModeration(String[] items) {

		log.info("INFO: Approve " + items[0]);
		openLandingPage(items[0]);
		ApproveItem();

		log.info("INFO: Reject " + items[1]);
		openLandingPage(items[1]);
		RejectItem();

		log.info("INFO: Reject " + items[2]);
		openLandingPage(items[2]);
		RejectItem();

		log.info("INFO: Delete " + items[3]);
		openLandingPage(items[3]);
		DeleteItem();

		log.info("INFO: Go to Rejected view of Moderation");
		clickLink(RejectedTab);

		log.info("INFO: Approve the rejected " + items[1]);
		openLandingPage(items[1]);
		ApproveItem();

		log.info("INFO: Delete the rejected " + items[2]);
		openLandingPage(items[2]);
		DeleteItem();
	}

	/**
	 * verify post-moderation of items. Dismiss/Quarantine/Delete flagged item, Restore/Delete rejected item on landing page
	 * @param items
	 */
	public void verifyPostModeration(String[] items) {

		log.info("INFO: Dismiss " + items[0]);
		openLandingPage(items[0]);
		DismissItem();

		log.info("INFO: Quarantine " + items[1]);
		openLandingPage(items[1]);
		QuarantineItem();

		log.info("INFO: Quarantine " + items[2]);
		openLandingPage(items[2]);
		QuarantineItem();

		log.info("INFO: Delete " + items[3]);
		openLandingPage(items[3]);
		DeleteItem();

		clickLink(QuarantinedTab);

		log.info("INFO: Restore the quaratnined " + items[1]);
		openLandingPage(items[1]);
		RestoreItem();

		log.info("INFO: Delete the quarantined " + items[2]);
		openLandingPage(items[2]);
		DeleteItem();
	}
	
	
	public static ModerationUI getGui(String product, RCLocationExecutor driver) {
		return new ModerationUI(driver);
	}

}
