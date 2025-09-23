package com.ibm.conn.auto.webui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.util.webeditors.fvt.utils.DriverUtils;
import com.ibm.conn.auto.webui.SharepointWidgetUI.FileType;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.SHAREPOINT_WEBSITE_TIMEOUT_SEC;

public class OfficeWebAppUI extends ICBaseUI {

	private static Logger log = LoggerFactory.getLogger(OfficeWebAppUI.class);

	private DriverUtils driverUtl;

	protected OfficeWebAppUI(RCLocationExecutor driver) {
		super(driver);

		// init DriverUtils
		driverUtl = new DriverUtils(driver);
	}
	
	private static final String
			WEBAPP_IFRAME = "css=iframe#WebApplicationFrame",
			
			POWERPOINT_APPLY_TEMPLATE_BTN = "css=button#WACDialogActionButton",
			
			CURR_FILENAME_BREADCRUMB = "css=span#ribbon-QATRowLeft > span#BreadcrumbTitle",
			CURR_FILENAME_BREADCRUMB_WAC = "css=span#WACRibbon-QATRowLeft > span#BreadcrumbTitle",
			
			WEBAPP_NAME_LBL = "css=span#ribbon-QATRowCenter",
			WEBAPP_NAME_WAC_LBL = "css=span#WACRibbon-QATRowCenter",
			
			WEBAPP_CLOSE_BTN = "css=a#ribbon-close",
			WEBAPP_CLOSE_WAC_BTN = "css=a#WACRibbon-close"
			;

	private String getPowerpointApplyTemplateBtn()							{ return POWERPOINT_APPLY_TEMPLATE_BTN; }
	private String getCurrFilenameBreadcrumb(boolean isWebInterfaceWAC)		{ return isWebInterfaceWAC ? CURR_FILENAME_BREADCRUMB_WAC : CURR_FILENAME_BREADCRUMB; }
	private String getWebappNameLbl(boolean isWebInterfaceWAC) 				{ return isWebInterfaceWAC ? WEBAPP_NAME_WAC_LBL : WEBAPP_NAME_LBL; }
	private String getWebappCloseBtn(boolean isWebInterfaceWAC) 			{ return isWebInterfaceWAC ? WEBAPP_CLOSE_WAC_BTN : WEBAPP_CLOSE_BTN; }

	/**
	 * This method is responsible for handling the Office Web App window that pops open when Office Web App (OWA) file operations (create, open) are being tested.
	 * It assumes the Office Web App file is brand new and this is the first time it is being edited.
	 * {@code processOpenedOWAFile} is used during the {@code com.ibm.conn.auto.tests.webeditors. CommunitySharepointIWidget_Office365Tests.createFilesTest} test.
	 * 
	 * @param fileType type of Office Web App file that is being used by the current test
	 * @param filename name of Office Web App file that is being used by the current test
	 * @return the number of open browser windows before closing the OWA window
	 */
	public void processOpenedOWAFile(FileType fileType, final String filename) {
		processOpenedOWAFile(fileType, filename, true);
	}
	
	/**
	 * Assumes that the Selenium focused window has a Office Web App file open for edition, and assures that both the filename and the Office Web App name are present in the UI.  
	 * {@code processOpenedOWAFile} is not compatible with the UI that is presented when viewing a file. Only edit mode is supported.
	 * 
	 * @param fileType the type of file that is being edited;
	 * @param filename the name of the file that is being edited; 
	 * @param fileIsNew indicates whether this file was just created. This is relevant to deal with Powerpoint's templates dialog box;   
	 */
	public void processOpenedOWAFile(FileType fileType, final String filename, boolean fileIsNew ) {

		log.info("Switch to 'WebApplicationFrame' iFrame");
		driver.switchToFrame().selectSingleFrameBySelector(WEBAPP_IFRAME);

		if (fileIsNew && fileType == FileType.PWPT) {
			log.info("This is a new PPT file. Clicking on extra button...");
			driverUtl.click(getPowerpointApplyTemplateBtn(), SHAREPOINT_WEBSITE_TIMEOUT_SEC);
		}

		log.info("Asserting the breacrum is '" + filename + fileType.expectedFileExtension + "'");
		Assert.assertTrue(driver.isTextPresent(filename + fileType.expectedFileExtension), "Wrong filename in breadcrum! Could not find '" + filename + fileType.expectedFileExtension + "' on the web page.");
		final String filenameInBreadcrumSelector = getCurrFilenameBreadcrumb(fileType.isWebInterfaceWAC);
		Assert.assertEquals(driver.getSingleElement(filenameInBreadcrumSelector).getText(), filename + fileType.expectedFileExtension, "Wrong filename in breadcrum!");

		log.info("Asserting the Office Web App name in title is '" + fileType.webAppTitleLabelText + "'");
		Assert.assertTrue(driver.isTextPresent(fileType.webAppTitleLabelText), "Wrong Web App name in title! Could not find '" + fileType.webAppTitleLabelText + "' on the web page.");
		final String webappNameInTitleSelector = getWebappNameLbl(fileType.isWebInterfaceWAC);
		Assert.assertEquals(driver.getSingleElement(webappNameInTitleSelector).getText(), fileType.webAppTitleLabelText, "Wrong Web App name in title!");

		log.info("Closing Office Web App");
		driverUtl.click(getWebappCloseBtn(fileType.isWebInterfaceWAC));
		
		boolean alertFound = driverUtl.handleAlertIfPresent(true, 3); // necessary for some browsers depending on settings 
		if(alertFound)
			log.info("The close window alert popup was processed sucessfully");
	}
}
