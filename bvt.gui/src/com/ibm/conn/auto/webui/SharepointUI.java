package com.ibm.conn.auto.webui;

import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.util.webeditors.fvt.utils.DriverUtils;

public class SharepointUI extends ICBaseUI {
	
	private static Logger log = LoggerFactory.getLogger(SharepointUI.class);
	
	public SharepointUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	static final String 
		TEST_FILE_PREFIX = "Test", // "SharepointFilesWidgetTest " TestFile.xls TestFolder#23 etc...

		VISIBLE_CONTENT_IMG = "css=img#js-newdocWOPI-Hero-WPQ2-img",
		HIDDEN_CONTENT_IMG = "css=img.ms-siteicon-img",
		
		CREATE_NEW_FILE_OR_FOLDER_BTN = "css=a#js-newdocWOPI-Hero-WPQ2",
		CREATE_WORD_FILE_BTN = "id=js-newdocWOPI-divWord-WPQ2", 
		CREATE_EXCEL_FILE_BTN = "id=js-newdocWOPI-divExcel-WPQ2",
		CREATE_POWERPOINT_FILE_BTN = "id=js-newdocWOPI-divPowerPoint-WPQ2",
		CREATE_ONENOTE_FOLDER_BTN = "id=js-newdocWOPI-divOneNote-WPQ2", 
		CREATE_FOLDER_BTN = "id=js-newdocWOPI-divFolder-WPQ2",

		CREATE_NEW_FILE_OR_FOLDER_DLGBX = "css=iframe.ms-dlgFrame",

		FILENAME_LBL = "css=td.ms-sectionheader > h3.ms-standardheader",
		EXPECTED_FILENAME_LABELTEXT = "Document Name",
		NEW_FILE_EXTENSION_LBL = "css=span.ms-createNewDocument-extSpan",
		FILENAME_TXTBX = "css=span.ms-createNewDocument-inputSpan > input.ms-createNewDocument-filename",
		CREATE_FILE_OK_BTN = "css=td[nowrap] > input.ms-ButtonHeightWidth[type='button']",

		FOLDERNAME_LBL = "css=td.ms-formlabel  > h3.ms-standardheader > nobr",
		EXPECTED_FOLDERNAME_LABELTEXT = "Name *",
		FOLDERNAME_TXTBX = "css=td.ms-formbody > span > input.ms-long",
		CREATE_NEW_FOLDER_OK_BTN = "css=td[nowrap='nowrap'] > input[type='submit']",
		
		LIST_OF_SOME_FILES_OR_FOLDERS_TBLRW = "xpath=//table[" + DriverUtils.xpathIndexerForCssClass("ms-listviewtable") + "] / tbody "
				+ "/ tr [ td[" + DriverUtils.xpathIndexerForCssClass("ms-vb-title") + "] / div / a[" 
				+ DriverUtils.xpathIndexerForCssClass("ms-listlink") + " and starts-with(text(), \"%1$s\")] ]",
		// %1$s first parameter is the first few letters of the file or folder name to be selected 
		//LIST_OF_TEST_FILES_OR_FOLDERS_TBLRW = String.format(LIST_OF_SOME_FILES_OR_FOLDERS_TBLRW, TEST_FILE_PREFIX),
		FILE_OR_FOLDER_LNK = "css=td.ms-vb-title > div > a.ms-listlink",
		FILE_OR_FOLDER_IMG = "css=td.ms-vb-icon > img",
		FILE_OR_FOLDER_ELLIPSIS_MENU_LNK = "css=td.ms-list-itemLink-td > div.ms-list-itemLink > a.ms-lstItmLinkAnchor.ms-ellipsis-a",
		FILE_OR_FOLDER_ELLIPSIS_MENU_POPUP = "xpath=//span[" + DriverUtils.xpathIndexerForCssClass("js-callout-action") + "] "
				+ "/ a[" + DriverUtils.xpathIndexerForCssClass("ms-calloutLink") + " and starts-with(text(), \"%1$s\") ]",
				// "css=td.ms-list-itemLink-td div.js-callout-footerArea span.js-callout-action > a.ms-calloutLink"
		FILE_OR_FOLDER_ELLIPSIS_MENU_POPUP_EDIT_LNK = String.format(FILE_OR_FOLDER_ELLIPSIS_MENU_POPUP, "Edit"),
	
		LOGIN_FORMS_TYPE_OPT = "css=option[value='Forms']",
		LOGIN_TYPE_CONTAINER_DDBX = "css=div#DeltaPlaceHolderMain > select",
		USERNAME_TXTBX = "css=input.ms-inputuserfield[type='text']",
		PASSWORD_TXTBX = "css=input.ms-inputuserfield[type='password']",
		SIGN_IN_BTN = "css=input[type='submit']",
		
		CREDENTIALS_FORM_CONTAINER_TBL = "css=span.ms-error + table"
		;

	public void assertSharepointContentIsVisible() {
		boolean isEnabled, isDisplayed, isVisible;
		
		log.info("INFO: locating a Sharepoint generated image '" + VISIBLE_CONTENT_IMG + "'");

		Element visibleContent = driver.getSingleElement(VISIBLE_CONTENT_IMG);
		Assert.assertNotNull(visibleContent, VISIBLE_CONTENT_IMG + " returns no element!");
		isEnabled = visibleContent.isEnabled(); 
		isDisplayed = visibleContent.isDisplayed(); 
		isVisible = visibleContent.isVisible();
		Assert.assertTrue( isEnabled && isDisplayed && isVisible,
				"The element '" + VISIBLE_CONTENT_IMG + "' " + "should be enabled(" + isEnabled + "), "
						+ "be displayed("	+ isDisplayed + ") and " + "should be visible(" + isVisible + ").");
	}

	public void assertSharepointContentIsHidden() {
		boolean isEnabled, isDisplayed, isVisible;
		
		log.info("INFO: locating Sharepoint generated content that should be hidden '" + HIDDEN_CONTENT_IMG + "'");

		Element hiddenContent = driver.getSingleElement(HIDDEN_CONTENT_IMG);
		Assert.assertNotNull(hiddenContent, HIDDEN_CONTENT_IMG + " returns no element!");
		isEnabled = hiddenContent.isEnabled(); 
		isDisplayed = hiddenContent.isDisplayed(); 
		isVisible = hiddenContent.isVisible();
		Assert.assertTrue( isEnabled && !isDisplayed && !isVisible,
				"The element '" + HIDDEN_CONTENT_IMG + "' " + "should be enabled(" + isEnabled + "), "
						+ "not be displayed("	+ !isDisplayed + ") and " + "should not be visible(" + !isVisible + ").");
	}
		
	public void closeCurrentWindow() {
		log.info("INFO: locating a Sharepoint generated image '" + VISIBLE_CONTENT_IMG + "'");
		assertSharepointContentIsVisible();
		
		log.info("INFO: Closing current Sharepoint window");
		try {
			this.close(cfg); // close the browser window that contained the full-page Sharepoint web interface
		}
		catch(WebDriverException ex) {
			if(ex.getMessage().contains("JavaScript Error:")) {
				log.warn("A javascript error has occurred during 'driver.close'. Continuing test execution.");
			}
			else {
				throw ex;
			}
		}
		//org.openqa.selenium.WebDriverException: [JavaScript Error: "e is null" {file: "file:///R:/anonymous8081058575189163679webdriver-profile/extensions/fxdriver@googlecode.com/components/command-processor.js" line: 7854}]'[JavaScript Error: "e is null" {file: "file:///R:/anonymous8081058575189163679webdriver-profile/extensions/fxdriver@googlecode.com/components/command-processor.js" line: 7854}]' when calling method: [nsICommandProcessor::execute]
	}
}
