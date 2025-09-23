package com.ibm.conn.auto.webui;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.google.common.base.Strings;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.extensions.waitConditions.Conditions;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.util.webeditors.fvt.utils.DriverUtils;
import com.ibm.conn.auto.util.webeditors.fvt.utils.WindowContextHandler;
import com.ibm.conn.auto.webui.cloud.SharepointWidgetUICloud;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.conn.auto.webui.onprem.SharepointWidgetUIOnPrem;

public abstract class SharepointWidgetUI extends HCBaseUI {

	private static Logger log = LoggerFactory.getLogger(SharepointWidgetUI.class);

	private DriverUtils driverUtl;
	private OfficeWebAppUI officeWebAppUI;
	private SharepointUI sharepointUI;

	private static final String 
		// CommunitiesUI.leftNavOverviewButton does not work on Selenium 2.43 :(
		IC_LEFT_NAV_MENU_DDBX = "css=div#dropdownNavMenuTitle", // div#dropdownNavMenuTitle > ul > li#dropdownNavMenuTitleLink > a#dropdownNavMenuTitleLinkAnc
		IC_LEFT_NAV_OVERVIEW_OPT = "css=div#dropdownNavMenu > ul#lotusNavBar > li#communityHomeLink > a",
		IC_LEFT_NAV_WIDGET_FULL_PAGE_OPT = "css=div#dropdownNavMenu > ul#lotusNavBar > li#%1$s_navItem > a",
		// %1$s = 1st parameter is a widget UID as supplied by getWidgetUIDByTitle
		NO_CONFIGURATION_ERROR_MSG_LBL = "css=div#view_msg_feedback > div.lotusMessageBody",
		NO_CONFIGURATION_ERROR_ICON_IMG = "css=div#view_msg_feedback > img.lotusIconMsgError",
		// @Hikari?: NO_CONFIGURATION_ERROR_ICON_URL = "/connections/resources/web/com.ibm.social.hikari.theme/sprite/lotusHSprite-8bit.png?etag=20160229.210122",
		NO_CONFIGURATION_ERROR_ICON_URL = "lotusHSprite-8bit.png?etag=",
		NO_CONFIGURATION_ERROR_ICON_OFFSET = "-22px -8px", // @Hikari?: "-41px 0px"
		CONFIG_URL_ERROR_ICON_IMG = "css=div#edit_feedback > img[alt='Error']",
		CONFIG_URL_OK_ICON_IMG = "css=div#edit_feedback > img[alt='Success']",
		//CONFIG_NO_URL_ERROR_MSG_LBL = "div#edit_feedback > div.lotusMessageBody",
		CONFIG_NO_URL_ERROR_MSG = "The Sharepoint URL must not be empty.",
		//CONFIG_URL_ERROR_MSG_LBL = "div#edit_feedback > div.lotusMessageBody",
		CONFIG_URL_ERROR_MSG = "The input value is not a valid URL",
		//CONFIG_URL_OK_MSG_LBL = "div#edit_feedback > div.lotusMessageBody",
		CONFIG_URL_OK_MSG = "Your changes for Sharepoint Files have been saved.",
	
		CONNECTIONS_BUNDLE_LIST_PAGE = "css=html body pre";
	      

	// list of <URL string, boolean> pairs; if boolean is true then it's a good URL, else bad URL;
	private static List<MutablePair<String, Boolean>> urlTests;

	public static class Widget {
		
		public static final String 
			IFRAME = "css=iframe#pc3";
			
		private static final String 
			CONFIG_SECTION = "css=div#%1$sSection > div#%1$sSubArea > div#%1$s",
			// %1$s = 1st parameter is a widget UID as supplied by getWidgetUIDByTitle
			CONFIG_URL_TXTBX = "css=input#editSharepointURL",
			CONFIG_SAVE_BTN = "css=input.lotusFormButton[name=SharepointJustSave]",
			CONFIG_SAVED_MESSAGE = "css=div#edit_feedback.lotusMessage2.lotusSuccess > div.lotusMessageBody",

			TITLE_LBL = "css=div#col2Wrapper span.ibmDndDragHandle",
			// %1$s = 1st parameter is a widget title as it appears in the browser
			TITLE_ID_POSFIX = "Id", 
			MENU_BTN = "css=div#%1$sSection > h2 > a.lotusIcon.lotusActionMenu",
			// %1$s = 1st parameter is a widget UID as supplied by getWidgetUIDByTitle
			MENUITEM_BTN = "css=table#%1$smoreActions > tbody.dijitReset > tr[aria-label*='%2$s']"
			// %1$s = 1st parameter is a widget UID as supplied by getWidgetUIDByTitle
			// %2$s = 2nd parameter is a menuitem text, located in the widget's menu
			;
	}

	protected SharepointWidgetUI(RCLocationExecutor driver) {
		super(driver);

		// init DriverUtils
		driverUtl = new DriverUtils(driver);
		
		// OfficeWebAppUI
		officeWebAppUI = new OfficeWebAppUI(driver);
		
		// SharepointUI
		sharepointUI = new SharepointUI(driver);
		
		// init list of URLs for testing
		List<MutablePair<String, Boolean>> bldUrlTests = new ArrayList<MutablePair<String, Boolean>>();
		bldUrlTests.add(new MutablePair<String, Boolean>("208934u-0t589u4ejejejejejejejyguijtryh", true));
		bldUrlTests.add(new MutablePair<String, Boolean>("https://dubxpcvm194.mul.ie.ibm.----------Shared%20Documents/Forms/AllItems.aspx", false));
		bldUrlTests.add(new MutablePair<String, Boolean>("aaaa", true));
		bldUrlTests.add(new MutablePair<String, Boolean>("http://aaaa/@", false));
		bldUrlTests.add(new MutablePair<String, Boolean>("https://dubxpcvm194.mul.ie.ibm.com/sites/yuri/Shared%20Documents/Forms/AllItems.aspx", true)); /// .../AllItems.aspx??? causes JS problems! :(
		bldUrlTests.add(new MutablePair<String, Boolean>("https://dubxpcvm194.mul.ie.ibm.com/sites/yuri/Shared%20Documents/Form??s/AllItems.aspx", false));
		bldUrlTests.add(new MutablePair<String, Boolean>("http://w3.ibm.com", true));
		bldUrlTests.add(new MutablePair<String, Boolean>("http://aaaa/@", false));
		bldUrlTests.add(new MutablePair<String, Boolean>("google.com", true));
		bldUrlTests.add(new MutablePair<String, Boolean>("https://", false));
		urlTests = Collections.unmodifiableList(bldUrlTests);
	}

	public static SharepointWidgetUI getGui(String product, RCLocationExecutor driver) {

		String prd = product.toLowerCase();

		if(prd.equals("multi") || prd.equals("production") || prd.equals("vmodel") ){
			throw new NotImplementedException("'" + product + "' product is not yet supported.");
		} else if(prd.equals("onprem")) {
			return new SharepointWidgetUIOnPrem(driver);
		} else if(prd.equals("cloud")){
			return new SharepointWidgetUICloud(driver);
		} else {
			throw new NotImplementedException("'" + product + "' product is unknown.");
		}
		
	}

	/**
	 * Determines a widget UID, given the widget's current title. This method searches the Overview page for widget elements 
	 * and expects the widget's title to be unique.
	 * 
	 * @param currWidgetTitle
	 *            the current title of the widget, who's UID is to be determined
	 * @return the widget's UID
	 */
	private String getWidgetUIDByTitle(String currWidgetTitle) {
		
		List<Element> widgetLabels = driver.getElements(Widget.TITLE_LBL);
		Assert.assertFalse(0 == widgetLabels.size(), "No widgets were found! Looking for:'" + Widget.TITLE_LBL + "'.");

		// Counts backwards because the Sharepoint Files widget is always placed 
		// at the bottom of the overview page. This way, it will be the first element to be evaluated.  
		Element widgetLabel = null;
		for(int i = widgetLabels.size()-1; 0<=i; --i) {
			if(currWidgetTitle.equals(widgetLabels.get(i).getText())) {
				widgetLabel = widgetLabels.get(i);
				break;
			}	
		}
		Assert.assertNotNull(widgetLabel, "Found "+widgetLabels.size()+" widgets, but could not find a widget called '" + currWidgetTitle + "'!");
		
		String widgetLabelId = widgetLabel.getAttribute("id");
		Assert.assertTrue(widgetLabelId.endsWith(Widget.TITLE_ID_POSFIX), "Widget '" + currWidgetTitle + "' label id does not end with '"
				+ Widget.TITLE_ID_POSFIX + "': label id='" + widgetLabelId + "'. This is unexpected.");

		return widgetLabelId.substring(0, widgetLabelId.length() - Widget.TITLE_ID_POSFIX.length()); // returns widgetUID
	}

	/**
	 * Executes the action specified in 'action', from the menu of the widget entitled 'currWidgetTitle'; This method differs from
	 * CommunitiesUI.performCommWidgetAction in the fact that this method does not require the execution of javascript, and as such it is compatible with
	 * browsers such as Google Chrome
	 * <p/>
	 * 
	 * @param currWidgetTitleInCommunity
	 *            the current title of the widget on which the action is to be performed
	 * @param action
	 *            the action to be performed
	 */
	public void performWidgetAction(String currWidgetTitleInCommunity, Widget_Action_Menu action) {
		final String widgetUID = getWidgetUIDByTitle(currWidgetTitleInCommunity);
		
		int retryCount = 0;
		boolean actionWasClicked = false;
		do {
			try {
				final String widgetMenuSelector = String.format(Widget.MENU_BTN, widgetUID);
				driverUtl.click(widgetMenuSelector);
		
				final String widgetMenuItemSelector = String.format(Widget.MENUITEM_BTN, widgetUID, action.getMenuItemText());
				driverUtl.click(widgetMenuItemSelector);
				
				actionWasClicked = true;
			}
			catch(IllegalStateException ex) {
				if(3 <= ++retryCount ) { throw ex; }
			}
			catch(ElementNotVisibleException ex) {
				if(3 <= ++retryCount ) { throw ex; }
			}
		}
		while(!actionWasClicked);
	}

	/**
	 * The Widget's title is used in both the overview page and the left side dropdown menu. This method uses the widget's UI menu to change that.
	 * 
	 * @param currWidgetTitle
	 *            The widget's current title.
	 * @param newWidgetTitle
	 *            The widget's new title.
	 */
	public void changeWidgetTitle(String currWidgetTitle, String newWidgetTitle) {

		log.info("INFO: Click on the action Change Title");
		performWidgetAction(currWidgetTitle, Widget_Action_Menu.CHANGETITLE);

		log.info("INFO: Clear default widget title");
		driver.getSingleElement(CommunitiesUIConstants.widgetChangeTitleInput).clear();

		log.info("INFO: Type the new widget title");
		driver.getSingleElement(CommunitiesUIConstants.widgetChangeTitleInput).type(newWidgetTitle);

		log.info("INFO: Save the new widget title");
		driver.getSingleElement(CommunitiesUIConstants.widgetChangeTitleSaveButton).click();
	}

	/**
	 * Configures the Sharepoint widget to display the contents of the Sharepoint server located @domainUrl. This method uses the widget's UI menu to accomplish
	 * this configuration.
	 * 
	 * @param currWidgetTitle
	 *            the title of the widget that is to be configured
	 * @param scheme
	 *            a URL scheme such as 'http://' or 'https://'
	 * @param domainName
	 *            the Sharepoint server's domain name
	 */
	public void configureSharepointWidget(String currWidgetTitle, URI targetSharepoint) {

		log.info("INFO: Click on the action Edit");
		performWidgetAction(currWidgetTitle, Widget_Action_Menu.EDIT);

		log.info("INFO: Type the Sharepoint target URL:'" + targetSharepoint + "'");
		driver.getSingleElement(Widget.CONFIG_URL_TXTBX).clear();
		driverUtl.type(Widget.CONFIG_URL_TXTBX, targetSharepoint.toString());

		log.info("INFO: Saving the widget configuration");
		driverUtl.click(Widget.CONFIG_SAVE_BTN);
		
		fluentWaitElementVisible(Widget.CONFIG_SAVED_MESSAGE);
		log.info("INFO: Configuration Done with success");
		
	}

	private static final int CLICK_LEFT_NAV_MENU_MAX_RETRY = 3;

	public void navigateToOverviewFullpageMode() {
		navigateViaLeftSideMenu(IC_LEFT_NAV_OVERVIEW_OPT);		
	}
	
	/**
	 * Accesses the fullpage mode of the specified widget, by clicking on the UI's left side dropdown menu.
	 * 
	 * @param widgetCurrentTitle
	 *            the title of the widget who's fullpage mode is to be accessed
	 */
	public void navigateToWidgetFullpageMode(String widgetCurrentTitle) {
		navigateViaLeftSideMenu(String.format(IC_LEFT_NAV_WIDGET_FULL_PAGE_OPT, getWidgetUIDByTitle(widgetCurrentTitle)));
	}

	private void navigateViaLeftSideMenu(final String icLeftNavDropDownMenuItemLocator) {
		boolean leftNavMenuOptionsAreVisible = false;
		int retry=1;
		do {
			log.info("INFO: click on the navigation menu, on the left side dropdown box");
			driverUtl.click( IC_LEFT_NAV_MENU_DDBX );
			
			log.info("INFO: clicking the selected menuitem, on the left side dropdown box");
			try {
				driverUtl.click( icLeftNavDropDownMenuItemLocator, 5 ); // 5 seconds timeout because Chrome has some trouble locating this one (GC47.0)
				leftNavMenuOptionsAreVisible = true;
			}
			catch(IllegalStateException ex) {
				if(++retry <= CLICK_LEFT_NAV_MENU_MAX_RETRY)
					log.info("INFO: there was a problem while clicking the menuitem, on the left side dropdown box. "
							+ "Performing attempt #" + retry + " of " + CLICK_LEFT_NAV_MENU_MAX_RETRY + "..." );
				else
					throw ex;
				leftNavMenuOptionsAreVisible = false;
			}
		}
		while(!leftNavMenuOptionsAreVisible && retry <= CLICK_LEFT_NAV_MENU_MAX_RETRY);
	}

	/**
	 * Execute the login procedure into Sharepoint's domain. This is done to avoid the browser's credentials popup when the iframe accesses Sharepoint's
	 * contents.
	 */
	public void loginIntoSharepoint() {

		String originalUrl = driver.getCurrentUrl();

		String targetSharepointURL = String.format("%s://%s:%s%s", SHAREPOINT_URL_SCHEME, SHAREPOINT_SERVER_NAME, SHAREPOINT_SERVER_PORT, SHAREPOINT_CONTENT_PATH);
		driver.navigate().to(targetSharepointURL);

		Element loginTypeSelection = driver.getSingleElement(SharepointUI.LOGIN_TYPE_CONTAINER_DDBX);
		driverUtl.click(loginTypeSelection, SharepointUI.LOGIN_FORMS_TYPE_OPT);

		Element formContainer = driver.getSingleElement(SharepointUI.CREDENTIALS_FORM_CONTAINER_TBL);
		driverUtl.type(formContainer, SharepointUI.USERNAME_TXTBX, SHAREPOINT_USERNAME);
		driverUtl.type(formContainer, SharepointUI.PASSWORD_TXTBX, SHAREPOINT_PASSWORD);
		driverUtl.click(formContainer, SharepointUI.SIGN_IN_BTN);

		Wait<RCLocationExecutor> wait = new FluentWait<RCLocationExecutor>(driver)
				.withTimeout(SHAREPOINT_WEBSITE_TIMEOUT_SEC, TimeUnit.SECONDS)
				.pollingEvery(200, TimeUnit.MILLISECONDS);

		wait.until(Conditions.titleIs(SHAREPOINT_CONTENT_TITLE));

		driver.navigate().to(originalUrl);
	}

	public static enum FileType {

		WORD(SharepointUI.CREATE_WORD_FILE_BTN, ".docx", "Microsoft Word Web App", true), 
		EXCL(SharepointUI.CREATE_EXCEL_FILE_BTN, ".xlsx", "Microsoft Excel Web App", false), 
		PWPT(SharepointUI.CREATE_POWERPOINT_FILE_BTN, ".pptx", "Microsoft PowerPoint Web App", false), 
		ONEN(SharepointUI.CREATE_ONENOTE_FOLDER_BTN, "", "Microsoft OneNote Web App", true), 
		FLDR(SharepointUI.CREATE_FOLDER_BTN, "", null, false);

		final boolean isWebInterfaceWAC;
		final String webAppTitleLabelText, createFileOfTypeBtn, expectedFileExtension;
		
		public String getExtension() {
			return expectedFileExtension;
		}
		
		private static final String 
			FOLDER_ICON_FILENAME = "folder.gif",
			POWERPOINT_ICON_FILENAME = "icpptx.png",
			WORD_ICON_FILENAME = "icdocx.png",
			EXCEL_ICON_FILENAME = "icxlsx.png",
			ONENOTE_ICON_FILENAME = "icnotebk.png"
		;
		

		private FileType(String createFileOfTypeBtn, String expectedFileExtension, String webAppTitleLabelText, boolean isWebInterfaceWAC) {
			this.createFileOfTypeBtn = createFileOfTypeBtn;
			this.expectedFileExtension = expectedFileExtension;
			this.webAppTitleLabelText = webAppTitleLabelText;
			this.isWebInterfaceWAC = isWebInterfaceWAC;
		}

		public static FileType getFileTypeFromIconElement(Element folderIcon) {
			
			final FileType retVal;
			
			final String iconSource = folderIcon.getAttribute("src");
			
			if(iconSource.endsWith(WORD_ICON_FILENAME)) 			{ retVal = FileType.WORD; }
			else if(iconSource.endsWith(EXCEL_ICON_FILENAME))		{ retVal = FileType.EXCL; }
			else if(iconSource.endsWith(POWERPOINT_ICON_FILENAME))	{ retVal = FileType.PWPT; }
			else if(iconSource.endsWith(ONENOTE_ICON_FILENAME))		{ retVal = FileType.ONEN; }
			else if(iconSource.contains(FOLDER_ICON_FILENAME))		{ retVal = FileType.FLDR; }
			else
				retVal = null;
			
			return retVal;
		}

	}
	
	public String createTestFile(final FileType fileType) {
		
		final String testFileName = SharepointUI.TEST_FILE_PREFIX + (fileType == FileType.FLDR ? "Folder " : "File ") + (new Date()).getTime();
		
		if (fileType != FileType.FLDR) {
			
			WindowContextHandler owaWindowJump = new WindowContextHandler(driverUtl);
			
			createNewDocument(fileType, testFileName);
			
			owaWindowJump.resyncSeleniumWithNewWindow();
			
			log.info("INFO: Processing Office Web App file...");
			officeWebAppUI.processOpenedOWAFile(fileType, testFileName);
			
			log.info("INFO: Closing the Sharepoint window - the OWA 'close' button just makes the browser go back to Sharepoint");
			sharepointUI.closeCurrentWindow();
			
			owaWindowJump.resyncSeleniumWithOriginalWindow();
		}
		else {
			createNewDocument(fileType, testFileName);

			log.info("INFO: switching to the frame with the Sharepoint generated content, which is part of the Sharepoint Files widget");
			driver.switchToFrame().returnToTopFrame();
			driver.switchToFrame().selectSingleFrameBySelector(Widget.IFRAME);
			
			log.info("INFO: Looking for a folder named '" + testFileName + "'");
			List<Element> tableRows = driver.getElements(String.format(SharepointUI.LIST_OF_SOME_FILES_OR_FOLDERS_TBLRW, testFileName));
			
			boolean foundNewFolder = false;
			for (Element tableRow : tableRows) {
				log.info("INFO: Selecting folder...");
				Element element = tableRow.getSingleElement(SharepointUI.FILE_OR_FOLDER_LNK);
				if (element.getText().equals(testFileName)) {
					log.info("INFO: Found the folder named '" + testFileName + "'");
					foundNewFolder = true;
					break;
				}
				else
					log.info("I am not looking for '" + element.getText() + "'");
			}
			Assert.assertTrue(foundNewFolder, "I was unable to locate the new folder '" + testFileName + "' in Sharepoint's file list!");
		}
		
		return testFileName;
	}

	private void createNewDocument(final FileType fileType, final String testFileName) {
		
		log.info("INFO: locating the frame with the Sharepoint generated content, which is part of the Sharepoint Files widget");
		driver.switchToFrame().selectSingleFrameBySelector(Widget.IFRAME);
		
		log.info("INFO: Clicking 'Create new document'");
		driverUtl.click(SharepointUI.CREATE_NEW_FILE_OR_FOLDER_BTN);
		
		log.info("INFO: Clicking type of MS file (" + fileType.expectedFileExtension + ")");
		driverUtl.click(fileType.createFileOfTypeBtn);
		
		log.info("INFO: Switching to MS html dialog box");
		driver.switchToFrame().selectSingleFrameBySelector(SharepointUI.CREATE_NEW_FILE_OR_FOLDER_DLGBX);
		
		final String expectedFilenameLabelText, filenameLabelSelector;
		if (fileType != FileType.FLDR) {
			expectedFilenameLabelText = SharepointUI.EXPECTED_FILENAME_LABELTEXT;
			filenameLabelSelector = SharepointUI.FILENAME_LBL;
		}
		else {
			expectedFilenameLabelText = SharepointUI.EXPECTED_FOLDERNAME_LABELTEXT;
			filenameLabelSelector = SharepointUI.FOLDERNAME_LBL;
		}
		
		log.info("INFO: Attempting to assert presence of filename label '" + expectedFilenameLabelText + "'");
		Assert.assertEquals(driver.getSingleElement(filenameLabelSelector).getText(), expectedFilenameLabelText, "Oh no! Wrong filename label!");
		
		if ( !Strings.isNullOrEmpty(fileType.expectedFileExtension) ) {
			log.info("INFO: Attempting to assert presence of file extension description '" + fileType.expectedFileExtension + "'");
			Assert.assertEquals(driver.getSingleElement(SharepointUI.NEW_FILE_EXTENSION_LBL).getText(), fileType.expectedFileExtension, "Oh no! Wrong file extension!");
		}
		
		if (fileType != FileType.FLDR) {
			log.info("INFO: Typing file name: '" + testFileName + "'");
			driver.getSingleElement(SharepointUI.FILENAME_TXTBX).type(testFileName);

			log.info("INFO: Clicking on 'Create New File' OK button");
			driverUtl.click(SharepointUI.CREATE_FILE_OK_BTN);
		}
		else {
			log.info("INFO: Typing folder name: '" + testFileName + "'");
			driver.getSingleElement(SharepointUI.FOLDERNAME_TXTBX).type(testFileName);
			
			log.info("INFO: Clicking on 'Create New Folder' OK button");
			for (Element element : driver.getElements(SharepointUI.CREATE_NEW_FOLDER_OK_BTN)) 
				if (element.isVisible()) // because new folder dlgbx returns two buttons; one visible one invisible
					element.click();
		}
	}
	
	public String getContentBaseFolder() {
		Assert.assertTrue(SHAREPOINT_CONTENT_PATH.startsWith("/"), "SHAREPOINT_CONTENT_PATH property must start with '/'. SHAREPOINT_CONTENT_PATH='" + SHAREPOINT_CONTENT_PATH + "'.");
		return SHAREPOINT_CONTENT_PATH.split("/")[1];
	}

	
	public void openFile(String testFilename) {
		
		log.info("INFO: Looking for a file or folder named '" + testFilename + "'");
		final String testFilenameLocator = String.format(SharepointUI.LIST_OF_SOME_FILES_OR_FOLDERS_TBLRW, testFilename);
		List<Element> tableRows = driver.getElements(testFilenameLocator);
		Assert.assertTrue( !tableRows.isEmpty(), "Could not find any file or folder with '" + testFilenameLocator + "'.");
		
		boolean fileFound = false;
		for(Element fileTableRow : tableRows) {
			Element fileLink = fileTableRow.getSingleElement(SharepointUI.FILE_OR_FOLDER_LNK);
			if (fileLink.getText().equals(testFilename)) {
				FileType type = FileType.getFileTypeFromIconElement( fileTableRow.getSingleElement(SharepointUI.FILE_OR_FOLDER_IMG) );
				
				WindowContextHandler owaWindowJump = new WindowContextHandler(driverUtl);

				log.info("INFO: Attempting to start '" + type.webAppTitleLabelText + "' by editing '" + testFilename + type.getExtension() + "'...");
				//fileLink.click(); // opens the office file in view mode - processOpenedOWAFile is not compatible with view (non-edit) mode
				driverUtl.click(fileTableRow, SharepointUI.FILE_OR_FOLDER_ELLIPSIS_MENU_LNK);
				driverUtl.click(fileTableRow, SharepointUI.FILE_OR_FOLDER_ELLIPSIS_MENU_POPUP_EDIT_LNK);
			
				owaWindowJump.resyncSeleniumWithNewWindow();
				
				log.info("INFO: Processing Office Web App file...");
				officeWebAppUI.processOpenedOWAFile(type, testFilename, false);
				
				log.info("INFO: Closing the Sharepoint window - the OWA 'close' button just makes the browser go back to Sharepoint");
				sharepointUI.closeCurrentWindow();

				owaWindowJump.resyncSeleniumWithOriginalWindow();
				
				fileFound = true;
				break;
			}
		}
		Assert.assertTrue(fileFound, "Found one or more elements but none of them is named '" + testFilename + "'.");
		
	}

	public void assertNoConfigurationErrorIsDisplayed() {
		Element noConfigErrorMsg = driver.getSingleElement(NO_CONFIGURATION_ERROR_MSG_LBL);
		Assert.assertEquals(noConfigErrorMsg.getText(), WIDGET_NO_CONFIG_ERROR_MSG, "The expected error message was not found!");
		Assert.assertTrue(noConfigErrorMsg.isVisible(), "The No Configuration Error message is not visible!");
		
		Element noConfigErrorImg = driver.getSingleElement(NO_CONFIGURATION_ERROR_ICON_IMG);
		RemoteWebElement noConfigErrorImgRWE = (RemoteWebElement) noConfigErrorImg.getBackingObject();
		String backgroundImageUrl = noConfigErrorImgRWE.getCssValue("background-image");
		Assert.assertTrue(backgroundImageUrl.toLowerCase().contains(NO_CONFIGURATION_ERROR_ICON_URL.toLowerCase()),
				String.format("The expected error icon was not found! '%s' does not contain '%s'!", backgroundImageUrl.toLowerCase(), NO_CONFIGURATION_ERROR_ICON_URL.toLowerCase()));
		Assert.assertEquals(noConfigErrorImgRWE.getCssValue("background-position"), NO_CONFIGURATION_ERROR_ICON_OFFSET,
				"The expected error icon offset was not found!");
		Assert.assertTrue(noConfigErrorImg.isVisible(), "The No Configuration Error icon is not visible!");
	}

	/**
	 * Uses the Web UI to configure the Widget to use a Sharepoint server predefined in the properties file.
	 * 
	 * @return The target URL for the Sharepoint page which will appear within the widget.
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public URL performWidgetConfigurationViaUI() throws MalformedURLException, URISyntaxException {
		URL targetSharepointURL = new URL(SHAREPOINT_URL_SCHEME, SHAREPOINT_SERVER_NAME, SHAREPOINT_SERVER_PORT, SHAREPOINT_COLLECTION_PATH + SHAREPOINT_CONTENT_PATH);
		log.info("INFO: configuring the " + TITLE_ON_CONNECTIONS + " widget to use the resources at '" + targetSharepointURL.toURI().toString() + "'");
		configureSharepointWidget(TITLE_ON_CONNECTIONS, targetSharepointURL.toURI());

		log.info("INFO: navigating to Sharepoint Files widget fullpage mode");
		navigateToWidgetFullpageMode(TITLE_ON_CONNECTIONS);
		
		return targetSharepointURL;
	}

	public void assertConfigurationUIisActive() {
		
		log.info("INFO: Click on the action Edit");
		performWidgetAction(TITLE_ON_CONNECTIONS, Widget_Action_Menu.EDIT);
		
		String widgetUID = getWidgetUIDByTitle(TITLE_ON_CONNECTIONS);
		String configSection = String.format(Widget.CONFIG_SECTION, widgetUID);
		
		final String contextMsg = "Sharepoint Files configuration UI: ";
		
		Element configSectionTitle = driver.getSingleElement(configSection + " h2.flushLeft");
		Assert.assertEquals(configSectionTitle.getText(), "Configure Sharepoint Files",  contextMsg + "title is diferent from what was expected!");
		
		Element configSectionURLlabel = driver.getSingleElement(configSection + " label[for='editSharepointURL']");
		Assert.assertEquals(configSectionURLlabel.getText(), "* URL:", contextMsg + "URL label is diferent from what was expected!");
		
		Element widgetConfigURL = driver.getSingleElement(Widget.CONFIG_URL_TXTBX);
		driverUtl.waitUntilElementIsOperable(widgetConfigURL);
		
		Element widgetConfigSaveButton = driver.getSingleElement("css=input#SP_save_btn");
		driverUtl.waitUntilElementIsOperable(widgetConfigSaveButton);
		
		Element widgetConfigCancelButton = driver.getSingleElement("css=input#SP_cancel_btn");
		driverUtl.waitUntilElementIsOperable(widgetConfigCancelButton);
	}

	public void assertConfigUiVerifiesUrlCorrectness() {
		log.info("INFO: Click on the action Edit");
		performWidgetAction(TITLE_ON_CONNECTIONS, Widget_Action_Menu.EDIT);
		
		log.info("INFO: Typing URL's into the target URL textbox'");
		driver.getSingleElement(Widget.CONFIG_URL_TXTBX).clear();
		
		log.info("INFO: Trying to save empty URL textbox");
		driverUtl.click(Widget.CONFIG_SAVE_BTN);
		
		log.info("INFO: Checking if the proper error icon is visible");
		Assert.assertTrue( fluentWaitElementVisible(CONFIG_URL_ERROR_ICON_IMG), CONFIG_URL_ERROR_ICON_IMG + " is not visible." );
		log.info("INFO: Checking if the proper no url error message is present");
		Assert.assertTrue( fluentWaitTextPresent(CONFIG_NO_URL_ERROR_MSG), CONFIG_NO_URL_ERROR_MSG + " was not found!" );
		
		Element configUrl = driver.getSingleElement(Widget.CONFIG_URL_TXTBX);
		for(MutablePair<String, Boolean> urlTest : urlTests) {
			String currUrl = null;
			boolean testingGoodURL = false;
			try {
				configUrl.clear();
				currUrl = urlTest.getLeft();
				configUrl.type(currUrl); // type the url string
				driverUtl.click(Widget.CONFIG_SAVE_BTN);
				
				testingGoodURL = urlTest.getRight();
				if(testingGoodURL) {
					log.info("INFO: Checking if the proper OK icon is visible");
					Assert.assertTrue( fluentWaitElementVisible(CONFIG_URL_OK_ICON_IMG), CONFIG_URL_OK_ICON_IMG + " is not visible; The JS verification considered '" + urlTest.getLeft() + "' to be a bad URL!" );
					log.info("INFO: Checking if the proper OK message is present");
					Assert.assertTrue( fluentWaitTextPresent(CONFIG_URL_OK_MSG),  CONFIG_URL_OK_MSG + " was not found!" );
				} else {
					log.info("INFO: Checking if the proper error icon is visible");
					Assert.assertTrue( fluentWaitElementVisible(CONFIG_URL_ERROR_ICON_IMG), CONFIG_URL_ERROR_ICON_IMG + " is not visible; The JS verification considered '" + urlTest.getLeft() + "' to be a good URL!" );
					log.info("INFO: Checking if the proper bad URL error message is present");
					Assert.assertTrue( fluentWaitTextPresent(CONFIG_URL_ERROR_MSG),  CONFIG_URL_ERROR_MSG + " was not found!" );
				}
			} catch(Exception ex) {
				throw new RuntimeException("An unexpected exception has occured while evaluating '" + currUrl + "'. This URL was expected to be considered a " + (testingGoodURL ? "good" : "bad") + " URL.", ex);
			}
		}
		
	}
	
	/**
     * Determines if the Sharepoint iWidget exists among Connections Bundle by fetching the text element on the page
     * @return boolean
     */
  public boolean sharepointExistsInConnectionBundleList() {
      if (driver.getSingleElement(CONNECTIONS_BUNDLE_LIST_PAGE).getText().contains(SHAREPOINT_BUNDLE_NAME)){
          return true;
      } else return false;
  }
	
	/**
	 * Method to Configure Sharepoint via App Registry
	 * @param User
	 * @param String
	 * @param CustomizerUI
	 * @param TestConfigCustom
	 * @param RCLocationExecutor	 
   */
	public static void sharePointConfig(User testUser, String appRegAppName, CustomizerUI uiCnx7,TestConfigCustom cfg,RCLocationExecutor driver)
	{
		SharepointWidgetUI ui = SharepointWidgetUI.getGui(cfg.getProductName(), driver);
				
		log.info("Info: Delete Sharepoint App, if already created");
		if(uiCnx7.isElementVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		log.info("Info: Create Sharepoint MT App");
		try {
			uiCnx7.createAppViaAppReg( appRegAppName);
		} catch (IOException e) {
			log.info("IO Exception thrown during Sharepoint Configuration via App Reg: " + e.getMessage());
		}
		ui.loadComponent(Data.getData().ComponentHomepage,true);
		ui.waitForPageLoaded(driver);
		ui.waitForElementsVisibleWd(ui.createByFromSizzle(CommunitiesUIConstants.megaMenuOptionCommunities), 5);
	}
	
	

}
