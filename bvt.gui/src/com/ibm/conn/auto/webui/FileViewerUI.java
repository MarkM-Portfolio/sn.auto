package com.ibm.conn.auto.webui;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.core.Element.SelectDropdown;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.role.FilesRole;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.menu.FileViewer_Panel_Menu;
import com.ibm.conn.auto.webui.FilesUI.FileType;

public class FileViewerUI extends ICBaseUI {
	protected static Logger log = LoggerFactory.getLogger(FileViewerUI.class);

	public static String MessageList = "css=ul#messages";
	public static String MessageItem = "css=li";
	private String[] hasConditions = new String[] { "fileviewer-tearoff",
			"fileviewer-panels", "ckeditor-lite-mentions" };

	public static String Viewer = "css=div.ics-viewer";
	public static String Link = "css=a";
	public static String SaveCommentButton = "css=div.commentActions a.save-button";
	public static String CommentContainer = "css=div[class^='panelContent streamContent']";
	public static String LegacyCommentBox = "css=div.lotusMentionsDiv";
	public static String Ckeditor = BaseUIConstants.CKEditor_iFrame + ":nth(0)";
	public static String MultiShareButton = "css=div.multiAddContainer a.actionLink, a[class='manualAdd']:contains(Share)";
	public static String MemberTypeSelector = "css=select[id*='memberTypeSelector']";
	public static String RoleSelector= "css=select[id*='roleSelector']";
	public static String MemberTypeAheadInput = "css=input[class^=' lotusText'], input[class='dijitReset dijitInputInner";
	public static String MultiShareContainer= "css=div[class^='ics-typeaheadbox-container ics-typeaheadboxmulti-container active']";
	public static String MultiShareSaveButton = "css=div.multiAddContainer a.manualAdd";
	public static String ReadersContainer = "css=div.ics-viewer-shareSection div.reader";
	public static String EditorsContainer = "css=div.ics-viewer-shareSection div.editor";
	public static String UserEveryone = "css=div.ics-panel-entry.everyone";
	public static String UploadNewVersionButton = "css=a.ics-viewer-upload-new-version";
	public static String FileInput = "css=input[type='file']";
	public static String UploadSaveButton = "css=a.ics-viewer-upload-submit";
	public static String UploadNewVersionSummaryBox = "css=div.ics-viewer-upload-new-version textarea";
	public static String EditTagsButton = "css=div.ics-panel-entry.tags a.header1Action";
	public static String TagsContainer = "css=div.ics-viewer-tagwidget";
	public static String TagsInputBox = "css=div.ics-viewer-tagwidget input";
	public static String SaveTagButton = "css=a.save-tag-button";
	public static String MoreActionsButton = "css=li.ics-viewer-more-actions span.dijitButtonNode";
	public static String TogglePinButton = "css=*.ics-viewer-action-favorite";
	public static String LikeButton = "css=*.ics-viewer-action-like .unchecked, *.ics-viewer-action-liked .unchecked";
	public static String UnlikeButton = "css=*.ics-viewer-action-like .checked, *.ics-viewer-action-liked .checked";
	private static String LikeUnlikeButton_CommonPath = "css=div[class='ics-viewer-banner'] > ul[class='ics-viewer-toolbar'] > li[class*='ics-viewer-action-like'] > div";
	public static String LikeButton_FiDO = LikeUnlikeButton_CommonPath + " > a[title*='Like']";
	public static String UnlikeButton_FiDO = LikeUnlikeButton_CommonPath + " > a[title*='Unlike']";
	public static String ToggleFollowingButton = "xpath=//td[contains(text(), 'Follow')]";
	public static String ToggleLockButton = "xpath=//td[contains(text(), 'ock File')]";
	public static String MoveToTrashAction = "xpath=//td[contains(text(), 'Move to Trash')]";
	public static String TearOffButton = "css=*.ics-viewer-action-tearoff";
	public static String TogglePanelButton = "css=*.ics-viewer-action-panel";
	public static String CloseButton = "css=*.ics-viewer-action-close";
	public static String EditFilenameLink = "css=*.ics-viewer-title-name-container a.ics-viewer-title-name";
	public static String EditFilenameInputBox = "css=*.ics-viewer-title-name-container div.ics-editBox input";
	public static String DialogOkButton = "css=div.ics-dialog-action *.ics-viewer-ok-button";
	public static String HTML5VideoContainer = "css=video[id^='fileviewer_'][id$='_vjsVideo_html5_api']";
	public static String VideoPauseButton = "css=div[title='Pause']";
	public static String DeleteComment = "css=div[class='actions edit delete'] span[class='action delete'] a.deleteAnchor";
	public static String CancelDialog = "css=a#cancel.cancel.neutral";
	public static String ConfirmDialog = "css=a#ok.confirm.bad";
	public static String DeleteSharingOrganization = "css=div[class='ics-panel-entry bidiAware emptyContent share everyone ics-viewer-last'] a[class='remove action']";
	public static String ExpandLikes = "css=div[class='ics-panel-entry bidiAware dropdownStream metadata metadata2 emptyContent'] span.header2 a.dropdownLink";
	public static String CloseGetLinks = "css=a#ok.confirm.bad";
	public static String SaveDescriptionButton = "css=div[class='commentActions descriptionBox'] ul li a[class='save-button']";
	public static String CancelDescriptionButton = "css=div[class='commentActions descriptionBox'] a[class='cancel-button']";
	public static String AddADescriptionLink = "css=div[class='ics-panel-entry bidiAware emptyContent about metadata description'] span[class='header2'] a:contains(Add a description)";
    public static String ShortenDescriptionLink = "css=div[class='ics-viewer-validation'] a:contains(Shorten description?)";
    public static String ReadMoreLink = "css=div[class='ics-viewer-expandable-text bidiAware collapsable'] a[class='readMore']";
    public static String ReadLessLink = "css=div[class='ics-viewer-expandable-text bidiAware collapsable expanded'] a[class='readLess']";
    public static String PostCommentButton = "css=div[class='commentActions'] > ul > li[class='ics-viewer-first'] > a[class='save-button']";
	public static String addACommentBox = "css=div[class='newCommentBox'], body[class^='cke_editable cke_editable_themed] span:contains(Add a comment...)";
    
	// For Standard FiDO Regression
	/** Preview */
	public static String PreviewLinkTitle = "css=a[class='ics-viewer-title-name']";
	
	public static String PreviewImageZoomOutButton = "css=div[class='ics-zoom-buttons'] a:contains('Zoom out')";
	public static String PreviewImageResetButton = "css=div[class='ics-zoom-buttons'] a:contains('Reset')";
	public static String PreviewImageZoomInButton = "css=div[class='ics-zoom-buttons'] a:contains('Zoom in')";
	
	
	public static String PreviewImage = "css=img[class^='ics-zoom-img']";
	public static String PreviewVideo = "css=video[class='vjs-tech']";
	public static String PreviewViewerIFrame = "css=iframe[class='viewer-frame']";  
	public static String PreviewSheetNode ="css=div[id='sheet_node']";
	public static String PreviewEditorIFrame ="css=iframe[id='editorFrame']";
	public static String PreviewPresentationAPP ="css=div[id^='pres_widget_App']";
	public static String PreviewPdfViewer ="css=div[class='pdfViewer']";
	public static String PreviewUnsupported = "css=div[class='ics-viewer-icon-preview']";
	
	public enum DocType_DocsViewerSupported {
		DOC, DOCX ,PPT ,PPTX ,XLS ,XLSX,ODT,ODP,ODS,PDF,XLSM
	};
	
	
	
	/** Like/Unlike */
	public static String LikesNumLink = "css=div[class^='ics-panel-entry bidiAware dropdownStream metadata metadata2']:contains('Likes') span[class='header2'] a";
	public static String LikesNumNoLink = "css=div[class^='ics-panel-entry bidiAware dropdownStream metadata metadata2']:contains('Likes') span[class='header2'] span";
	public static String LikesUserList = "css=div[class='ics-panel-entry bidiAware emptyContent name ics-viewer-last']";
	/** Versions */
	public static final String VersionChangeSummaryInputBox = "css=div[class='ics-editBox'] div textarea";
	public static String versionTab = "css=li[class='version'] a:contains(Versions)";
	/** About */
	public static final String EditDescriptionButton = "css=div[class='ics-panel-entry bidiAware emptyContent about metadata description'] a[class='header1Action action']";
	public static final String GetLinksButton = "css=div[class='ics-panel-entry bidiAware emptyContent about metadata metadata2'] span.header2 a";
	public static final String DescriptionInputBox = "css=div[class='ics-editBox'] div textarea";
	public static final String DescriptionContainer = "css=div[class='textContainer bidiAware'] span[class='bidiAware']";
	public static final String PanelCurrentVersion = "css=div[data-dojo-attach-point='versionContainer'] span[class='header1']";
	public static final String FileSize_CurrentVersion = "css=div[data-dojo-attach-point='filesizeContainer'] span[class='header2']";
	public static final String CreatedBy = "css=div[data-dojo-attach-point='createdDateContainer'] span[class='header2']";
	/** Other */
	public static String viewListIsActive = "css=a[class^='lotusSprite lotusView lotusDetailsOn']";
	public static String viewGridIsActive = "css=a[class^='lotusSprite lotusView lotusTileOn']";
	/** Share */
	public static final String QuickShareReaderLink = "css=div[class='streamContainer reader'] div[class='ics-viewer-action ics-viewer-action-share'] a";
	public static final String QuickShareEditorLink = "css=div[class='streamContainer editor'] div[class='ics-viewer-action ics-viewer-action-share'] a";
    public static String sharingTab = "css=li[class='share'] a:contains(Sharing)";
	public static String fileOwnerUserLink = "css=div.Owner span.vcard > a.lotusPerson";
   	public static String fileEditorUserLink = "css=div.editor span.vcard > a.lotusPerson";
	public static final String TypeaheadBox = "css=div[class='lconnPeopleTypeAheadMenu dijitReset dijitMenu dijitComboBoxMenu'], div[class='dijitReset dijitMenu bhcPeopleTypeAheadMenu dijitComboBoxMenu']";
	public static final String ItemInTypeahead = "css=div[class='dijitMenuItem'], div[class='containerNode'] div[id^='bhc_PeopleTypeAheadMenu']";
	public static final String SearchInTypeahead = "css=div[class^='dijitMenuItem searchDirectory']";
	

	public FileViewerUI(RCLocationExecutor driver) {
		super(driver);
	}

	public String testViewer(BaseFile file) {
		testActions();
		testPanels();
		return "testViewer SUCCESSFUL";
	}

	public void testActions() {
		pin();
		like();
		unlike();
		follow();
		lock();
		rename();
	}

	public void testPanels() {
		addComment();
		shareWithEveryone();
		uploadNewVersion();
		addTags();
	}

	public void selectAction(String selector) {
		fluentWaitElementVisible(MoreActionsButton);

		boolean isInDropdown = true;
		if (isElementCurrentlyPresent(selector)) {
			Element element = driver.getSingleElement(selector);
			if (element.isDisplayed()) {
				isInDropdown = false;
			}
		}

		if (!isInDropdown) {
			getFirstVisibleElement(selector).getSingleElement(Link).click();
		} else {
			clickLinkWait(MoreActionsButton);
			getFirstVisibleElement(selector).click();
		}
	}

	public void rename() {
		log.info("INFO: Renaming the file");

		// Close the panels before editing the name because the grid servers
		// currently
		// run at a low resolution such that the filename is not visible
		closePanel();

		clickLinkWait(EditFilenameLink);

		Element inputBox = getFirstVisibleElement(EditFilenameInputBox);

		String originalName = inputBox.getAttribute("value");
		String newName = "newname_" + originalName;
		
		inputBox.clearWithSendKeys();
		inputBox.type(newName);

		log.info("INFO: Viewer = " + Viewer);
		final Element element = getFirstVisibleElement(Viewer);
		log.info("INFO: Click Viewer element to save new filename, element = " + element);
		element.getWebElement().click();
		log.info("INFO: EditFilenameLink = " + EditFilenameLink);
		boolean success = false;
		try {
			success = fluentWaitElementVisible(EditFilenameLink);
		} catch (final Throwable t) {
			log.info("INFO : t " + t.getMessage());
			log.info("INFO : failed to locate " + EditFilenameLink
					+ " going to try again.");
			success = fluentWaitElementVisible(EditFilenameLink);
		}
		log.info("INFO: success = " + success);
		Assert.assertTrue(success);

		final Element element2 = getFirstVisibleElement(EditFilenameLink);
		log.info("INFO: getFirstVisibleElement(EditFilenameLink) = " + element2);

		final String text = element2.getText();
		log.info("INFO: text = " + text);

		log.info("INFO: newName = " + newName);

		final boolean textIsNewName = text.equals(newName);
		log.info("INFO: textIsNewName = " + textIsNewName);

		Assert.assertTrue(textIsNewName);

		openPanel();
	}

	public void pin() {
		log.info("INFO: Selecting Pin button");
		selectAction(TogglePinButton);
		fluentWaitTextPresent("pinned this file");

		log.info("INFO: Selecting Unpin button");
		selectAction(TogglePinButton);
		fluentWaitTextPresent("unpinned this file");
	}

	public void like() {
		log.info("INFO: Selecting Like button");
		selectAction(LikeButton);
		Assert.assertTrue(isElementPresent(UnlikeButton));
	}

	public void unlike() {
		log.info("INFO: Selecting Unlike button");
		selectAction(UnlikeButton);
		Assert.assertTrue(isElementPresent(LikeButton));
	}

	public void follow() {
		log.info("INFO: Selecting Follow button");
		selectAction(ToggleFollowingButton);
		fluentWaitTextPresent("stopped following this file");

		log.info("INFO: Selecting Stop Following button");
		selectAction(ToggleFollowingButton);
		fluentWaitTextPresent("now following this file");
	}

	public void lock() {
		log.info("INFO: Selecting Lock button");
		selectAction(ToggleLockButton);
		fluentWaitTextPresent("file is now locked");

		log.info("INFO: Selecting Unlock button");
		selectAction(ToggleLockButton);
		fluentWaitTextPresent("file is now unlocked");
	}

	public void addComment() {
		log.info("INFO: Adding a comment");
		FileViewer_Panel_Menu.COMMENTS.select(this);

		String commentText = Data.getData().commonComment;

		if (isElementCurrentlyPresent(LegacyCommentBox)) {
			driver.getSingleElement(LegacyCommentBox).click();
			getFirstVisibleElement(LegacyCommentBox).type(commentText);
		} else {
			fluentWaitElementVisible(Ckeditor);
			driver.getSingleElement(Ckeditor).click();
			typeInCkEditor(commentText);
		}

		clickLinkWait(SaveCommentButton);
		fluentWaitTextPresent(commentText);

	}

	public void moveToTrash() {
		log.info("INFO: Moving the file to the trash");
		selectAction(MoveToTrashAction);
		clickLinkWait(DialogOkButton);
		Assert.assertFalse(isOpen());
	}

	public void getLinks() {
		FileViewer_Panel_Menu.ABOUT.select(this);
		log.info("INFO: Clicking on Copy Links button");
		driver.getSingleElement(GetLinksButton).click();
		driver.getSingleElement(CloseGetLinks).click();
		log.info("INFO: Cancelled Get Links dialog");
	}

	public void deleteComment() {
		FileViewer_Panel_Menu.COMMENTS.select(this);
		log.info("INFO: Deleting a comment");
		clickHoverButton(DeleteComment);
		getFirstVisibleElement(ConfirmDialog).click();
		log.info("INFO: Cancelled deleting a comment");
	}

	public void deleteOrganizationSharing() {
		FileViewer_Panel_Menu.SHARING.select(this);
		log.info("INFO: Deleting sharing with organization");
		clickHoverButton(DeleteSharingOrganization);
		getFirstVisibleElement(ConfirmDialog).click();
		log.info("INFO: Cancelled deleting organization");
	}

	public void expandLikes() {
		FileViewer_Panel_Menu.ABOUT.select(this);
		log.info("INFO: Clicking on the number of likes to expand");
		driver.getSingleElement(ExpandLikes);
	}

	public void tearOff() {
		log.info("Clicking tear off button");
		selectAction(TearOffButton);
	}

	public void closePanel() {
		log.info("Closing the right panel");

		if (isPanelOpen()) {
			selectAction(TogglePanelButton);
		} else {
			log.info("Panel was already closed");
		}
	}

	public void openPanel() {
		log.info("Opening the right panel");

		if (!isPanelOpen()) {
			selectAction(TogglePanelButton);
		} else {
			log.info("Panel was already open");
		}
	}

	public boolean isPanelOpen() {
		Element viewer = getFirstVisibleElement(Viewer);
		return viewer.getAttribute("class").contains(
				"ics-viewer-details-expanded");
	}

	public void close() {
		log.info("INFO: Closing the file viewer");
		Assert.assertTrue(isOpen());
		final Element viewer = getFirstVisibleElement(Viewer);
		Assert.assertTrue(viewer.isElementPresent(CloseButton));
		selectAction(CloseButton);
		Assert.assertFalse(isOpen());
	}

	public void shareWithEveryone() {
		log.info("INFO: Sharing the file with everyone");
		FileViewer_Panel_Menu.SHARING.select(this);
		clickLinkWait(MultiShareButton);
		SelectDropdown dropdown = driver.getSingleElement(MemberTypeSelector)
				.useAsDropdown();
		dropdown.selectOptionByValue("everyone");
		clickLinkWait(MultiShareSaveButton);
		fluentWaitElementVisible(ReadersContainer);
		Element readersContainer = getFirstVisibleElement(ReadersContainer);
		Assert.assertTrue(readersContainer.isElementPresent(UserEveryone));
	}

	public void uploadNewVersion() {
		log.info("INFO: Uploading a new version");
		FileViewer_Panel_Menu.VERSIONS.select(this);
		clickLinkWait(UploadNewVersionButton);

		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		Element fileInput = getFirstVisibleElement(FileInput);
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
				.extension(".jpg").rename(Helper.genDateBasedRand()).build();
		fileInput.typeFilePath(FilesUI.getFileUploadPath(file.getName(), cfg));
		clickLinkWait(UploadSaveButton);
		fluentWaitTextPresent("The new version was saved");
		log.info("The new version was saved. File name: " + file.getName());
	}

	public void addTags() {
		log.info("INFO: Adding tags");
		FileViewer_Panel_Menu.ABOUT.select(this);
		fluentWaitPresent(EditTagsButton);
		clickHoverButton(EditTagsButton);
		getFirstVisibleElement(TagsInputBox).type("tag1 tag2");
		clickLinkWait(SaveTagButton);
		fluentWaitElementVisible(TagsContainer);
		Element tagsContainer = getFirstVisibleElement(TagsContainer);
		Assert.assertTrue(tagsContainer.isTextPresent("tag1"));
		Assert.assertTrue(tagsContainer.isTextPresent("tag2"));
	}

	public void waitForOpen() {
		Assert.assertTrue(fluentWaitElementVisible(Viewer));
	}

	public boolean isOpen() {
		return isElementCurrentlyPresent(Viewer);
	}

	private void clickHoverButton(String selector) {
		fluentWaitPresent(selector);
		Element element = driver.getFirstElement(selector);
		element.hover();
		element.click();
	}

	private boolean isElementCurrentlyPresent(String selector) {
		driver.turnOffImplicitWaits();
		boolean isPresent = driver.isElementPresent(selector);
		driver.turnOnImplicitWaits();
		return isPresent;
	}

	public static FileViewerUI getGui(String product, RCLocationExecutor driver) {
		return new FileViewerUI(driver);
	}

	/**
	 * Navigate to the iframe page for the given file entry
	 * 
	 * @param fileEntry
	 * @param preserveInstance
	 *            specify whether to reuse an existing window or open a new
	 *            window
	 */
	public void navigateToIframePage(FileEntry fileEntry,
			boolean preserveInstance) {
		String url = cfg.getTestConfig().getBrowserURL()
				+ "connections/resources/web/ic-share/fileviewer/embed.test.html#file="
				+ getFileId(fileEntry);
		String hasParameter = "";
		for (int i = 0; i < hasConditions.length; i++) {
			if (i > 0) {
				hasParameter += ",";
			}
			hasParameter += hasConditions[i];
		}
		url += "&has=" + hasParameter;

		driver.load(url, preserveInstance);
		switchToViewerFrame();
		waitForOpen();
	}

	/**
	 * Refresh the iframe page and wait for the file viewer to open
	 */
	public void refreshIframePage() {
		driver.navigate().refresh();
		switchToViewerFrame();
		waitForOpen();
	}

	/**
	 * Switch to the frame containing the file viewer
	 */
	private void switchToViewerFrame() {
		driver.switchToFrame().selectFrameByIndex(0);
	}

	/**
	 * Get the file entry ID from the namespaced ID
	 * 
	 * @param fileEntry
	 * @return the file ID
	 */
	private String getFileId(FileEntry fileEntry) {
		String fileId = fileEntry.getId().toString();
		// Remove namespace
		return fileId.substring(fileId.lastIndexOf(":") + 1);
	}

	/**
	 * @param message
	 * @return true if the page has received a window.postMessage() event for
	 *         the given message
	 */
	public boolean hasMessage(String message) {
		for (String m : getMessages()) {
			if ((message.length() == m.length() && message.equals(m))
					|| (message.length() != m.length() && m.startsWith(message))) {
				return true;
			}
		}
		return false;
	}

	public List<String> getMessages() {
		List<String> messages = new ArrayList<String>();

		driver.switchToFrame().returnToTopFrame();
		Element messagesElement = driver.getSingleElement(MessageList);
		for (Element messageElement : messagesElement.getElements(MessageItem)) {
			messages.add(messageElement.getText());
		}
		switchToViewerFrame();

		return messages;
	}

	/**
	 * Upload the file using the upload API
	 * 
	 * @param baseFile
	 *            the file to upload
	 * @return the file entry of the uploaded file
	 */
	public FileEntry upload(BaseFile baseFile, TestConfiguration testConfig,
			User testUser) {
		log.info("INFO: Uploading file using API");

		String filePath = "resources" + File.separator + baseFile.getName();
		File file = new File(filePath);
		log.info("INFO: File path is " + file.getAbsolutePath());

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig
				.getBrowserURL());
		APIFileHandler fileHandler = new APIFileHandler(serverURL,
				testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());

		return fileHandler.CreateFile(baseFile, file);
	}
	
	
	/**
	 * Upload the file to community file using the upload API
	 * 
	 * @param baseFile
	 *            the file to upload
	 * @return the file entry of the uploaded file
	 */
	public FileEntry upload(BaseFile baseFile, TestConfiguration testConfig,
			User testUser,Community community) {
		log.info("INFO: Uploading file to community using API");

		String filePath = "resources" + File.separator + baseFile.getName();
		File file = new File(filePath);
		log.info("INFO: File path is " + file.getAbsolutePath());

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig
				.getBrowserURL());
		APIFileHandler fileHandler = new APIFileHandler(serverURL,
				testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());

		return fileHandler.CreateFile(baseFile, file, community);
	}
	

	// For Standard FiDO Regression (FiDo == File Details Overlay == File
	// Viewer) - Begin

	/**
	 * For FoiDo : select file in Grid View
	 * 
	 * @param file
	 * @return preview link in Grid View for file viewer
	 */
	public static String selectFileInGridView(BaseFile file) {
		return "css=div[class^='ic-thumb-widget-flip-card']:contains("
				+ file.getRename() + file.getExtension() + ")";
	}

	/**
	 * For FiDO : Preview in Grid View to launch file viewer file viewer test
	 * 
	 * @param file
	 * @return preview link in Grid View for file viewer
	 */
	public static String previewLinkInGridView(BaseFile file) {
		return "css=div[class='card-back']:contains(" + file.getRename()
				+ file.getExtension() + ")";
	}

	/**
	 * For FiDO : openFiDOByView In main page of file app or community file
	 * widget main page , Open FiDO against Launcher (grid view , list ,
	 * customize view , file detail page) and verify the FiDO is opened or not
	 * 
	 * @param launcher
	 *            views in files main page top right corner
	 * @param baseFile
	 * @param fileType
	 * @return True : open FiDO successfully ; False : open FiDO failed .
	 */
	public boolean openFiDOByView(Files_Display_Menu launcher,
			BaseFile baseFile, FileType fileType) {
		log.info("INFO : testFiDO Start");
		String fileName = "";

		fileName = baseFile.getRename() + baseFile.getExtension();
		log.info("INFO: Open FiDO for file :  " + fileName + " in " + launcher);
		if (launcher == Files_Display_Menu.TILE) {
			Files_Display_Menu.TILE.select(this);
			if (!driver.isElementPresent(selectFileInGridView(baseFile))) {
				log.info("ERROR: Fail to find the uploaded file " + fileName
						+ " in " + launcher);
				return false;
			}
			fluentWaitPresent(Files_Display_Menu.TILE.getMenuItemLink());

			if (!driver.isElementPresent(viewGridIsActive))
				Files_Display_Menu.TILE.select(this);

			log.info("INFO: Click Preview icon in GridView for file "
					+ fileName);
			// DEV will make front side clickable to open viewer by defect
			// 149585: Grid view thumbnail click handler causes problems in
			// Selenium , hover won't needed when 149585 is fixed
			driver.getSingleElement(selectFileInGridView(baseFile)).hover();
			clickLink(previewLinkInGridView(baseFile));

		} else if (launcher == Files_Display_Menu.DETAILS) {
			log.info("INFO: Click file name link in File list view for file :  "
					+ fileName);
			fluentWaitPresent(Files_Display_Menu.DETAILS.getMenuItemLink());

			if (!driver.isElementPresent(viewListIsActive))
				Files_Display_Menu.DETAILS.select(this);

			try {
				clickLinkWait(FilesUI.getFileIsUploaded(baseFile));
			} catch (final Throwable t) {
				log.info("INFO : t " + t.getMessage());
				log.info("INFO : "
						+ FilesUI.getFileIsUploaded(baseFile)
						+ " might have been launched but the response event have time out exception. Would check in later step");
			}
		}

		log.info("INFO: Wait for FiDo of  :  " + fileName + " in " + launcher);
		waitForOpen();
		if (!isOpen()) {
			log.error("ERROR: Fail to open FiDO of " + fileName + "in "
					+ launcher);
			return false;
		}
		return true;
	}

	/**
	 * For FiDO : changeName Add Data before file name (without extension name)
	 * 
	 * @param fileName
	 * @return The new name
	 */
	public String changeName(String fileName) {
		if (fileName.indexOf(".") < 0)
			throw new AssertionError(
					"ERROR: please input the file name with extension name");
		return Helper.genDateBasedRand() + "_"
				+ fileName.substring(0, fileName.indexOf('.'));
	}

	/**
	 * For FiDO : deleteFiles by API
	 * 
	 * @param fileName
	 * @return The new name
	 */
	public void deleteFilesByAPI(APIFileHandler apiFileOwner,
			List<FileEntry> fileEntries) {

		for (FileEntry fileEntry : fileEntries) {
			log.info("INFO: delete file " + fileEntry.getTitle()
					+ " by Files API");
			apiFileOwner.deleteFile(fileEntry);
		}

	}

	/**
	 * For FiDO: add comment in FiDo
	 * 
	 * @param text
	 * @return if comment can be searched out in comment container , return true
	 *         ; or else , false
	 */
	public boolean addComments(String text) {
		log.info("INFO: Add a comment in FiDO");
		fluentWaitElementVisible(addACommentBox);
		driver.getSingleElement(addACommentBox).click();
		typeInCkEditor(text);
		clickLinkWait(SaveCommentButton);
		log.info("INFO:Comments:"
				+ driver.getSingleElement(CommentContainer).getText());
		return driver.getSingleElement(CommentContainer).getText()
				.contains(text);

	}

	/**
	 * For FiDO : upload new version of the file
	 * 
	 * @param changeSummary
	 * @return True : Upload new version successfully ; False : Upload new
	 *         version failed.
	 */
	public boolean uploadNewVersion(String changeSummary) {
		log.info("INFO: Uploading a new version");
		log.info("INFO: click on Upload new version button");
		clickLinkWait(UploadNewVersionButton);
		
		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		Element fileInput = getFirstVisibleElement(FileInput);

		BaseFile file = new BaseFile.Builder(Data.getData().file1)
				.extension(".jpg").rename(Helper.genDateBasedRand()).build();
		log.info("INFO: Input the file path");
		fileInput.typeFilePath(FilesUI.getFileUploadPath(file.getName(), cfg));

		log.info("INFO: Add changeSummrary for the new version");
		if (changeSummary != null && changeSummary != "") {
			log.info("INFO: Input the changesummary for the new version");
			Element changeSummaryInput = getFirstVisibleElement(VersionChangeSummaryInputBox);
			changeSummaryInput.click();
			changeSummaryInput.type(changeSummary);
		}
		log.info("INFO: Upload the new version");
		clickLinkWait(UploadSaveButton);
		return fluentWaitTextPresent("The new version was saved");
	}

	/**
	 * For FiDO : Edit Description
	 * 
	 * @param description
	 * @return True : Edit description successfully ; False : Add description
	 *         failed.
	 */
	public boolean editDescription(String description) {
		log.info("INFO: Edit Description");

		if (description != null && description != "") {
			log.info("INFO: Click on Edit Description Button");
			clickHoverButton(EditDescriptionButton);
			fluentWaitElementVisible(DescriptionInputBox);
			Element descriptionInput = getFirstVisibleElement(DescriptionInputBox);
			descriptionInput.click();
			log.info("INFO: Input the Description");
			descriptionInput.type(description);
			
			log.info("INFO: Click on the Save button");
			this.clickLinkWait(SaveDescriptionButton);

			return description.contains(driver.getFirstElement(
					DescriptionContainer).getText());

		} else {
			log.info("Error: Description Input should not be null");
			return false;
		}

	}

	/**
	 * For FiDO : Add Tags
	 * 
	 * @param tags
	 *            Tags with spaces as split e.g "tag1 tag2 tag3"
	 * @return True : Add Tags successfully ; False : Add Tags failed.
	 */
	public boolean addTags(String tags) {
		log.info("INFO: add Tags");
		if (tags != null && tags != "") {
			log.info("INFO: Click on add tags Button");
			clickHoverButton(EditTagsButton);
			fluentWaitElementVisible(TagsInputBox);
			Element tagsInputBox = getFirstVisibleElement(TagsInputBox);
			tagsInputBox.click();
			log.info("INFO: Input the tags");
			tagsInputBox.type(tags);
			log.info("INFO: Save the tags");
			clickLink(SaveTagButton);

			String[] tagsArray = tags.split(" ");
			for (String tag : tagsArray) {
				String selector = "css=a:contains('" + tag + "')";
				if (!fluentWaitElementVisible(selector)) {
					return false;
				}
			}
			return true;
		} else {
			log.info("Error: tags input should not be null");
			return false;
		}

	}

	/**
	 * For FiDO : Rename
	 * 
	 * @param new Name
	 * @return True : Rename the file Successfully ; False : Rename the file
	 *         failed.
	 */

	public boolean rename(String newName) {
		log.info("INFO: Renaming the file");

		// Close the panels before editing the name because the grid servers
		// currently
		// run at a low resolution such that the filename is not visible
		closePanel();

		clickLinkWait(EditFilenameLink);
		Element inputBox = getFirstVisibleElement(EditFilenameInputBox);

		inputBox.clear();
		inputBox.type(newName);

		log.info("INFO: Viewer = " + Viewer);
		final Element element = getFirstVisibleElement(Viewer);
		log.info("INFO: element = " + element);
		element.click();
		log.info("INFO: EditFilenameLink = " + EditFilenameLink);
		boolean success = false;
		try {
			success = fluentWaitElementVisible(EditFilenameLink);
		} catch (final Throwable t) {
			log.info("INFO : t " + t.getMessage());
			log.info("INFO : failed to locate " + EditFilenameLink
					+ " going to try again.");
			success = fluentWaitElementVisible(EditFilenameLink);
		}
		log.info("INFO: success = " + success);
		if (!success) {
			return success;
		}

		final Element element2 = getFirstVisibleElement(EditFilenameLink);
		log.info("INFO: getFirstVisibleElement(EditFilenameLink) = " + element2);

		final String text = element2.getText();
		log.info("INFO: text = " + text);
		log.info("INFO: newName = " + newName);

		final boolean textIsNewName = text.equals(newName);
		log.info("INFO: textIsNewName = " + textIsNewName);

		openPanel();

		return textIsNewName;

	}

	/**
	 * For FiDo : Verify whether user shared with Editor/Reader in FiDo share
	 * with panel
	 * 
	 * @param role
	 * @param userName
	 * @return
	 */
	public boolean isUserSharedWithRole(FilesRole role, String userName) {
		if (role.equals(FilesRole.EDITOR)) {
			return driver.getSingleElement(EditorsContainer).getText()
					.contains(userName);
		} else if (role.equals(FilesRole.READER))
			return driver.getSingleElement(ReadersContainer).getText()
					.contains(userName);
		else {
			log.info("ERROR: Please input correct role to be shared !");
			return false;
		}
	}
	
	/**
	 * For FiDo :
	 * 
	 * @param typeAheadItem
	 * @param searchString
	 * @return
	 */
	private boolean selectItemInTypeAhead(List<Element> typeAheadItems,
			String searchString) {
		boolean result = false;
		for (Element el : typeAheadItems) {
			if (el.getText().contains(searchString)) {
				clickLinkWait(itemInTypeAheadById(el.getAttribute("id")));
				result = true;
				log.info("INFO : Found and selected item in typeahead , item is :"
						+ searchString);
				break;
			}
		}
		return result;
	}

	/**
	 * For FiDo :
	 * 
	 * @param id
	 * @return
	 */
	private String itemInTypeAheadById(String id) {
		return "css=div[id='" + id + "']";
	}
	
	/**
	 * For FiDo : add shares of user/community/everyone as reader/editor in to the share queue
	 * 
	 * @param role
	 *            : roles in FilesRole
	 * @param shareObject
	 *            : User/Community/Everyone(public)
	 * @param searchString
	 *            : input string , eg : user name / email /community name , if
	 *            share with everyone as reader , searchString can be null when
	 *            invoke .
	 * @param expectedDisplayName
	 *            : expected Display name in reader/editor panel after added
	 * @return
	 */
	public boolean addShare(FilesRole role, String shareObject,
			String searchString, String expectedDisplayName) {
		
		boolean found = false;
		fluentWaitElementVisible(MemberTypeSelector);
		
		if (role.equals(FilesRole.EDITOR)) {
			log.info("INFO: Add share for " + shareObject + "as role "+role.name);
			driver.getSingleElement(MemberTypeSelector).useAsDropdown()
					.selectOptionByValue(shareObject);
			driver.getSingleElement(RoleSelector).useAsDropdown()
			.selectOptionByValue(role.name.toLowerCase());
			typeText(MemberTypeAheadInput, searchString);
		} else if (role.equals(FilesRole.READER)) {
			log.info("INFO: Add share for " + shareObject + "as role "+role.name);
			driver.getSingleElement(MemberTypeSelector).useAsDropdown()
					.selectOptionByValue(shareObject);
			
			if (!shareObject.equals(Data.TypeAheadSelectorValueEveryone)){
				driver.getSingleElement(RoleSelector).useAsDropdown()
				.selectOptionByValue(role.name.toLowerCase());
			    typeText(MemberTypeAheadInput, searchString);
			}
			
		} else {
			log.info("ERROR: Please input correct role to be shared ! Test has not been done because of the wrong role input");
			return false;
		}
		
		if (!shareObject.equals(Data.TypeAheadSelectorValueEveryone)) {
			fluentWaitElementVisible(TypeaheadBox);
			List<Element> results = driver.getElements(ItemInTypeahead);
			if (results.size() > 0)
				found = selectItemInTypeAhead(results, searchString);						
			// If search result is no results found , click 'use full search'
			// item to search again
			if (results.size() == 0) {
				clickLinkWait(SearchInTypeahead);
				fluentWaitElementVisible(TypeaheadBox);
				found = selectItemInTypeAhead(
						driver.getElements(ItemInTypeahead), searchString);
			}
			if (!found) {
				return false;
			}
			
			return isShareAddedintoShareQueue(role, expectedDisplayName);
			
		} else {
			
			return driver.getSingleElement(MemberTypeSelector).getAttribute("value").equals(Data.TypeAheadSelectorValueEveryone);
		}
		
	}
	
	/**
	 * For FiDo : Verify whether share with Editor/Reader added into the share queue
	 * with panel
	 * 
	 * @param role
	 * @param userName
	 * @return
	 */
	public boolean isShareAddedintoShareQueue(FilesRole role, String userName) {
		if (role.equals(FilesRole.EDITOR)) {
			return driver.getSingleElement(MultiShareContainer).getText()
					.contains("Editors")&& driver.getSingleElement(MultiShareContainer).getText()
					.contains(userName);
		} else if (role.equals(FilesRole.READER))
			return driver.getSingleElement(MultiShareContainer).getText()
					.contains("Readers") && driver.getSingleElement(MultiShareContainer).getText()
					.contains(userName);
		else {
			log.info("ERROR: Please input correct role to be shared !");
			return false;
		}
	}
	
	/**
	 * For FiDo : If the role selected as the expected result. 
	 * with panel
	 * 
	 * @param role
	 * @param userName
	 * @return
	 */
	public boolean isRoleSelected(FilesRole role) {
		return driver.getSingleElement(RoleSelector).getAttribute("value").equalsIgnoreCase(role.name);
	}
	
	
	/**
	 * For File Viewer : isFiDOPreviewed  to 
	 * @param fileType
	 * @param docsDocType(Office)  Docs Supported document types; Ignored for other File Type;
	 * @return True : FiDo previewed the file ; False : FiDo previewed the file failed. 
	 */
	public boolean isFiDOPreviewed(FileType filetype,DocType_DocsViewerSupported docsDocType){
		log.info("Info: Verify if the file could be previewed.");
		switch(filetype){
			case IMAGE:
			    return driver.isElementPresent(PreviewImage);
			case VIDEO:
			    return driver.isElementPresent(PreviewVideo);
			case OFFICE:			
				if (docsDocType != null){
					
					if (driver.isElementPresent(PreviewViewerIFrame)){
						log.info("Info: Viewer Iframe displayed!");
						Element frameElement = driver.getFirstElement(PreviewViewerIFrame);
						driver.switchToFrame().selectFrameByElement(frameElement);
						
						Boolean isMainConentDisplayed=false;
						
						switch (docsDocType){
							case DOCX:
							case DOC:
							case ODT:
								isMainConentDisplayed= fluentWaitPresent(PreviewEditorIFrame);					
						    break;
						    
							case XLS:
							case XLSX:
							case XLSM:
							case ODS:
								isMainConentDisplayed= fluentWaitPresent(PreviewSheetNode);
						    break;
						    
							case PPT:
							case PPTX:
							case ODP:
								isMainConentDisplayed= fluentWaitPresent(PreviewPresentationAPP);
						    break;
							case PDF:
								isMainConentDisplayed= fluentWaitPresent(PreviewPdfViewer);
							break;
							default:
								isMainConentDisplayed = false;
							}
						if (isMainConentDisplayed){
							log.info("Info: Docs main content displayed and switch back to main window !");
							driver.switchToWindowByHandle(driver.getWindowHandle());
							return true;
						
						}
					} else {
						return false;
					}
				}
				
			case UNSUPPORTED:
			    return driver.isElementPresent(PreviewUnsupported);
			 
			default:
			     return false;
		}
		
	 	}

	

	// For Standard FiDO Regression - End

}
