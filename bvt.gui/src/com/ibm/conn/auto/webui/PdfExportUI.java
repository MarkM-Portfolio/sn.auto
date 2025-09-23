package com.ibm.conn.auto.webui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.conn.auto.util.DefectLogger;

public class PdfExportUI extends ICBaseUI {
	
	protected static Logger log = LoggerFactory.getLogger(PdfExportUI.class);
	TestConfiguration testConfig = cfg.getTestConfig();
	
	public static String pdfExportBtn = "css=span[class*='pdfexportBtn'][title='Export as PDF']";
	public static String pdfExportDialog = "css=div[id*=dialog_title][aria-label='Export as PDF']";
	public static String generatePdfBtn = "css=.btn-default-custom";
	public static String pdfExportDlgIframe = "css=.lotusDialogContent>iframe";
	public static String pdfExportDlgClose = pdfExportDialog + " .lotusDialogClose";
	
	// PDF content locators
	public static String pdfContentIframe = "css=#pdfExportIframe";
	public static String sideBarItems = "xpath=//*[@id='sidebarContent']//*[@id='outlineView']//*[@class='treeItem']";
	public static String pdfContentPages = "css=#viewer .page";
	
	// Entires Selection from Generate PDF page
	public static String parentEntriesCheckbox = "xpath=//label[@id='nextlabel']//input[@name='isSelectAll']";
	public static String childEntriesCheckboxs = "xpath=//span[@ng-if='multiSelectEnabled']";
	public static String selectedchildEntriesCheckboxs = "xpath=//input[contains(@class,'ng-not-empty')]//ancestor::span[@ng-if='multiSelectEnabled']//following-sibling::span";
	public static String pdfLogoTooltip = "xpath=//span[@title='Export as PDF']";
	public static String selectedEntriesTOC = "css=#outlineView div a";
	public static String gettextTOC = "xpath=//span [text()= 'Table of contents']";
	
	// PDF Information Include Section Locators
	public static String pdfTitlePageChkBox = "css=input[title='Check to render a title page for the document']";
	public static String pdfTOCChkBox = "css=input[title='Check to render a Table of Contents for the document']";
	public static String pdfCommentsChkBox = "css=input[title='Check to display the comments of each entry']";
	public static String pdfTitleChkBox = "css=input[title='Check to display the title of each entry']";
	public static String pdfAuthorChkBox = "css=input[title='Check to display the author of each entry']";
	public static String pdfSummaryChkBox = "css=input[title='Check to display the summary of each entry']";
	public static String pdfTagsChkBox = "css=input[title='Check to display all tags for each entry']";
	public static String pdfCreatedDateChkBox = "css=input[title='Check to display the date each entry is created']";
	public static String pdfModifiedDateChkBox = "css=input[title='Check to display the date each entry was last modified']";
	public static String pdfStartDateButton="css=button[class='openCalendarBtn btn btn-default'][ng-click='isStartDateCalenderOpen = !isStartDateCalenderOpen']";
	public static String pdfEndDateButton="css=button[class='openCalendarBtn btn btn-default'][ng-click='isEndDateCalenderOpen= !isEndDateCalenderOpen']";
	public static String pdfTodayDateButton="xpath=//button[text()='Today']";
	public static String pdfStartDateTextArea="xpath=//input[@ng-model='data.startDate']";
	public static String pdfEndDateTextArea="xpath=//input[@ng-model='data.endDate']";
	public static String pdfExportToDoEntry="css=input[title='Export only the todo entries from the Activity']";
	public static String pdfToDoItemDueDate="css=input[title='To Do Items With a Due Date']";
	public static String pdfExportToDoEntryAll="css=input[title='All']";
	public static String pdfPage = "css=embed[type='application/pdf']";
	
	public PdfExportUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	public static PdfExportUI getGui(String product, RCLocationExecutor driver) {
		return new PdfExportUI(driver);
	}
	
	
	/**
	 * Open the Export as PDF dialog
	 */
	public void openExportAsPdfDialog() {
		
		log.info("INFO: Scroll up to the top of the page to make link shown");
		scrollIntoViewElement(pdfExportBtn);
		
		clickLinkWait(pdfExportBtn);
		fluentWaitElementVisible(pdfExportDialog);
	}
	
	/**
	 * Open the Export as PDF dialog without scroll
	 */
	public void openExportAsPdfDialogWithoutScroll() {
		clickLinkWait(pdfExportBtn);
		fluentWaitElementVisible(pdfExportDialog);
	}

	/**
	 * Click the Generate PDF button and wait for the progress
	 * bar to appear then finish.
	 */
	public void clickGeneratePdfWaitToFinish() {
		switchToFrameBySelector(pdfExportDlgIframe);
		
		fluentWaitElementVisible(generatePdfBtn);
		clickLinkWait(generatePdfBtn);
		String exportProgressBarLocator = "#pdfExportProgressBar";
		
		driver.turnOffImplicitWaits();
		log.info("Wait up to 10 secs for the progress bar to appear.");
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 10);
		wait.pollingEvery(Duration.ofMillis(100)).until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector(exportProgressBarLocator)));
		
		log.info("Wait up to 30 secs for the progress bar to disppear.");
		WebDriverWait wait2 = new WebDriverWait((WebDriver)driver.getBackingObject(), 30);
		wait2.until(ExpectedConditions.invisibilityOfElementLocated(
				By.cssSelector(exportProgressBarLocator)));
		
		driver.turnOnImplicitWaits();
		switchToTopFrame();
	}
	
	/**
	 * Get the target entry in the PDF sidebar.
	 * @param title
	 * @return
	 */
	public Element getEntryInPdfSidebar(String title) {
		switchToFrameBySelector(pdfExportDlgIframe);
		switchToFrameBySelector(pdfContentIframe);
		
		Element item = driver.getFirstElement(sideBarItems+"//a[text()='" + title + "']");
		
		switchToTopFrame();
		return item;
	}
	
	/**
	 * Get the target entry under TOC.
	 * @param title
	 * @return
	 */
	public void validateEntriesInTOC(String[] Title, String ParentName) {
		
		switchToFrameBySelector(pdfExportDlgIframe);
		switchToFrameBySelector(pdfContentIframe);
		
		log.info("'Table of Contents' Text is displayed");
		Assert.assertTrue(isElementVisible(gettextTOC), "'Table of Contents' text is not displayed");
		
		List <Element> items = driver.getElements(selectedEntriesTOC);
		List <String> actualItems= new ArrayList<>();
		for (Element item : items) {
			String entryTitle = item.getText();
			log.info("Actual Title: "+entryTitle);
			actualItems.add(entryTitle);
		}
		actualItems.remove(ParentName);
		actualItems.remove(ParentName);
		log.info("Actual Titles are: "+actualItems);
		List <String> expectedItems= new ArrayList<>();
		for (int i = 0; i < Title.length; i++) {
			expectedItems.add(Title[i]);
		}
		log.info("Expected Titles are: "+expectedItems);
		log.info("Value is "+actualItems.containsAll(expectedItems));
		Assert.assertTrue( actualItems.containsAll(expectedItems));
		
		switchToTopFrame();
		
	}
	
	/**
	 * Check whether the given sentences exist on the given page in the PDF.
	 * @param pageNum
	 * @param lines
	 */
	public void checkLineExistsOnPage(int pageNum, String... lines)  {
		switchToFrameBySelector(pdfExportDlgIframe);
		switchToFrameBySelector(pdfContentIframe);
		
		if(testConfig.browserIs(BrowserType.FIREFOX)) {
			Element page = getPdfPage(pageNum);
			log.info("Scroll to page to allow it to load before checking content.");
			scrolltoViewElement(page.getWebElement(), (WebDriver)driver.getBackingObject());		
			for (String line : lines) {
				log.info("Checking line: " + line);
				try{
					Assert.assertFalse(page.getElements("xpath=//*[@class='textLayer']//*[text()='"+line+"']").isEmpty(),line+" is not Present in the Exported PDF.");
				}catch(AssertionError e){
					throw e;
				}
			}
		}else {
			log.info("INFO: Verify PDF Page is displayed");
			Assert.assertTrue(isElementPresent(PdfExportUI.pdfPage), "PDF is displayed");
		}
		switchToTopFrame();
	}
	
	/**
	 * Check whether date exist on the given page in the PDF.
	 * @param pageNum
	 * @param browserDate
	 */
	public void checkDateExistsOnPage(int pageNum, String browserDate)  {
		switchToFrameBySelector(pdfExportDlgIframe);
		switchToFrameBySelector(pdfContentIframe);

		Element page = getPdfPage(pageNum);
		log.info("Scroll to page to allow it to load before checking content.");
		scrolltoViewElement(page.getWebElement(), (WebDriver) driver.getBackingObject());

		try {
			Assert.assertFalse(page.getElements("xpath=//*[@class='textLayer']//*[text()='" + browserDate + "']").isEmpty(),browserDate + " is not Present in the Exported PDF.");
		} catch (AssertionError e) {
			
			// This is workaround to make tests pass in case of client machine is in different time zone of the server 
			log.info("Let's verify the PDF date with UTC date");
			Date UTCDate = getCurrentUTCDate();
			SimpleDateFormat simpleDateFormat_pdf = new SimpleDateFormat("EEEEE MMMM d, yyyy");
			String UTCDate_Str = simpleDateFormat_pdf.format(UTCDate);
			log.info("UTC date is :" + UTCDate_Str);
			Assert.assertFalse(page.getElements("xpath=//*[@class='textLayer']//*[text()='" + UTCDate_Str + "']").isEmpty(),UTCDate_Str + " is not Present in the Exported PDF.");
		}

		switchToTopFrame();
	}
	
	/**
	 * Check whether the given sentences exist on the given page in the PDF.
	 * @param pageNum
	 * @param lines
	 */
	public boolean checkLineExistsonPDFPage(int pageNum, String... lines)  {
		boolean flag=true;
		try
		{
			this.checkLineExistsOnPage(pageNum, lines);
		}
		catch(AssertionError e)
		{
			flag=false;
			switchToTopFrame();
		}
		return flag;
	}
	
	/**
	 * Check whether the given Locator exist on the given page in the PDF.
	 * @param pageNum
	 * @param Locators
	 */
	public void checkLocatorExistsOnPage(int pageNum, String... locators)  {
		switchToFrameBySelector(pdfExportDlgIframe);
		switchToFrameBySelector(pdfContentIframe);
		
		if(testConfig.browserIs(BrowserType.FIREFOX)) {
			Element page = getPdfPage(pageNum);
			log.info("Scroll to page to allow it to load before checking content.");
			scrolltoViewElement(page.getWebElement(), (WebDriver)driver.getBackingObject());
			for (String locator : locators) {
				log.info("Checking line: " + locator);
				page.getElements(locator);
				Assert.assertNotNull(page.getElements(locator));
			}
		}else {
			log.info("INFO: Verify PDF Page is displayed");
			Assert.assertTrue(isElementPresent(PdfExportUI.pdfPage), "PDF is displayed");
		}	
		switchToTopFrame();
	}
	
	/**
	 * Wait for the export dialog to dismiss on screen.
	 */
	public void waitForExportDialogDisappear() {
		driver.turnOffImplicitWaits();		
		
		log.info("Wait up to 2 secs for the export dialog to disppear.");
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 2);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(
				By.cssSelector(pdfExportDialog.substring(pdfExportDialog.indexOf("="), pdfExportDialog.length()))));
		
		driver.turnOnImplicitWaits();
	}
	
	/**
	 * Common smoketest method to be used by different components.
	 * It clicks the Export as PDF button and checks content.
	 * @param pUi
	 * @param logger
	 * @param parentName - the main entity (i.e activity name/forum name, etc)
	 * @param entryName - the child entity (i.e. activity entry name/forum post name, etc)
	 * @param entryInSidebar - whether child entry page is listed in sidebar
	 */
	public void smokeTest(PdfExportUI pUi, DefectLogger logger, 
			String parentName, String entryName, boolean entryInSidebar) {
		
		logger.strongStep("Click the Export as PDF button.");
		log.info("INFO: Click the Export as PDF button.");
		if (entryName.toLowerCase().contains("blog")) {
			pUi.openExportAsPdfDialogWithoutScroll();
		} else {
			pUi.openExportAsPdfDialog();
		}

		logger.strongStep("Click the Generate PDF button in the dialog.");
		log.info("INFO: Click the Export as PDF button in the dialog.");
		pUi.clickGeneratePdfWaitToFinish();

		// PDF renderer just uses the page headline as TOC entry, design has changed on the 
		// TOC page to just use the main entity as headline so the sidebar shows 2 identical entries now.
		logger.strongStep("Verify PDF sidebar contains the main entity and the table of content.");
		log.info("INFO: Verify PDF sidebar contains the main entity and the table of content: " + parentName);
		switchToFrameBySelector(pdfExportDlgIframe);
		switchToFrameBySelector(pdfContentIframe);
		
		if (testConfig.browserIs(BrowserType.FIREFOX)) {
			List<Element> items = driver.getElements(sideBarItems + "//a[text()='" + parentName + "']");
			Assert.assertTrue(items.size() == 2, "# of sidebar entries found with name " + parentName + " == 2, but was " + items.size());
			switchToTopFrame();

			// Activities doesn't have a sidebar entry for the entries because there is no headline.
			if (entryInSidebar) {
				logger.strongStep("Verify PDF sidebar contains entry: " + entryName);
				log.info("INFO: Verify PDF sidebar contains entry: " + entryName);
				pUi.getEntryInPdfSidebar(entryName);
			}

			logger.strongStep("Verify the parent name is on page 1: " + parentName);
			log.info("INFO: Verify the parent name is on page 1: " + parentName);
			pUi.checkLineExistsOnPage(1, parentName);

			String toc = "Table of contents";
			logger.strongStep("Verify the TOC is on page 2: " + toc + " " + parentName);
			log.info("INFO: Verify the TOC is on page 2: " + toc + " " + parentName);
			pUi.checkLineExistsOnPage(2, toc, parentName);

			logger.strongStep("Verify the entry name is on page 3: " + entryName);
			log.info("INFO: Verify the entry name is on page 3: " + entryName);
			pUi.checkLineExistsOnPage(3, entryName);
		} else {
			logger.strongStep("Verify PDF Page is displayed");
			log.info("INFO: Verify PDF Page is displayed");
			Assert.assertTrue(pUi.isElementPresent(PdfExportUI.pdfPage), "PDF is displayed");
			switchToTopFrame();
		}
		
		logger.strongStep("Close the PDF Export dialog.");
		log.info("INFO: Close the PDF Export dialog.");
		pUi.clickLinkWait(PdfExportUI.pdfExportDlgClose);
		
		logger.strongStep("Verify the PDF Export dialog disappears.");
		pUi.waitForExportDialogDisappear();
	}
	
	/**
	 * Get a specific page in PDF content.
	 * Note that it assumes caller is already in the PDF content iframe.
	 * @return
	 */
	private Element getPdfPage(int pageIdx) {	
		try {
			Element page = driver.getSingleElement(pdfContentPages+":nth-child("+pageIdx+")");
			return page;
		} catch (AssertionError e)  {
			log.error("Cannot find page " + pageIdx);
			throw e;
		}
	}
	
	/**
	 * Common noEntriesAreSelected method to be used by different components.
	 * It deselects all the selected entries.
	 */
	public void noEntriesAreSelected() {

		log.info("Verify that Entry Selection Checkbox is displayed ");
		switchToFrameBySelector(pdfExportDlgIframe);
		Assert.assertTrue(isElementVisible(parentEntriesCheckbox), "Entry Selection Checkbox is not displayed");

		log.info("By Clicking on Parent Entries Checkbox, application will deselect all default entires");
		Element checkBoxElement= driver.getFirstElement(parentEntriesCheckbox);
		if (checkBoxElement.isSelected())
		{
			checkBoxElement.click();
		}
		switchToTopFrame();
	}

	/**
	 * Common allEntriesAreSelected method to be used by different components.
	 * It will select all the child entries.
	 */
	public void allEntriesAreSelected() {

		log.info("Verify that Entry Selection Checkbox is displayed ");
		switchToFrameBySelector(pdfExportDlgIframe);
		Assert.assertTrue(isElementVisible(parentEntriesCheckbox), "Entry Selection Checkbox is not displayed");

		log.info("Verify that Parent Entries Checkbox is selected by default");
		Element checkBoxElement= driver.getFirstElement(parentEntriesCheckbox);
		if (checkBoxElement.isSelected())
		{
			log.info("Parent Entry Check box is selected");
		}
		else{
			log.info("Click on Check box");
			checkBoxElement.click();
		}
		
		switchToTopFrame();
	}
	
	/**
	 * Common multpleEntriesAreSelected method to be used by different components. 
	 * It will select only random child entries.
	 */
	public  String[] selectedEntryName() {

		log.info("Verify that Entry Selection Checkbox is displayed ");
		switchToFrameBySelector(pdfExportDlgIframe);
		Assert.assertTrue(isElementVisible(parentEntriesCheckbox), "Entry Selection Checkbox is not displayed");

		log.info("By Clicking on Parent Entries Checkbox application will deselect all default entires");
		Element checkBoxElement= driver.getFirstElement(parentEntriesCheckbox);
		if (checkBoxElement.isSelected())
		{
			checkBoxElement.click();
		}

		log.info("Select Random Checkboxes");
		List<Element> childEntries = driver.getElements(childEntriesCheckboxs);
		String[] selectedEntryName = new String[2];
		for (int i = 0; i < childEntries.size(); i++) {
			if (i % 2 == 0) {
				childEntries.get(i).click();
				//selectedEntryName[i]= childEntries.get(i).getText();
			}
		}
		List <Element> selectedEntries = driver.getElements(selectedchildEntriesCheckboxs);
		for (int i = 0; i < selectedEntries.size(); i++) {
			selectedEntryName[i]= selectedEntries.get(i).getText();		
		}
		switchToTopFrame();
		return selectedEntryName;
	}

	/**
	 * Common validatePDFLOGOtoolTip method to be used by different components.
	 * It validates the tooltip value of pdf logo.
	 */
	public void validatePDFLOGOtoolTip() {

		String expectedTitle = "Export as PDF";
		Element pdfLogo = driver.getSingleElement(pdfExportBtn);
		String actualTitle = pdfLogo.getAttribute("title");
		Assert.assertEquals(actualTitle, expectedTitle, "'Export as PDF' title is not displayed");

	}
	
	/**
	 * Common contentSelection method to be used by different components.
	 * It clicks the Export as PDF button and validate side bar content.
	 * 
	 * @param pUi
	 * @param logger
	 * @param parentName- the main entity (i.e activity name/forum name, etc)
	 * @param entryName- the child entity (i.e. activity entry name/forum post name,etc)
	 * @param entryInSidebar - whether child entry page is listed in sidebar
	 */
	public void contentSelection(PdfExportUI pUi, DefectLogger logger, String parentName, String entryName,
			boolean entryInSidebar ,String Title1, String Title2,String Title3) {

		logger.strongStep("The PDF icon is available with title 'Export as PDF' ");
		log.info("INFO: Validate title 'Export as PDF'");
		pUi.validatePDFLOGOtoolTip();

		logger.strongStep("Click the Export as PDF button.");
		log.info("INFO: Click the Export as PDF button.");
		pUi.openExportAsPdfDialog();

		logger.strongStep("Deselect Entries from 'Content' section");
		log.info("INFO: Deselect Entries from 'Content' section");
		pUi.noEntriesAreSelected();

		logger.strongStep("Click the Generate PDF button in the dialog.");
		log.info("INFO: Click the Export as PDF button in the dialog.");
		pUi.clickGeneratePdfWaitToFinish();
		
		//Validation for No Entries Selection condition is commented due to know existing issue in application
		logger.strongStep("Validate the Sidebar content of PDF");
		log.info("INFO: Validate the Sidebar content of PDF");
		/*pUi.pdfContentValidation(pUi, logger, parentName, entryName, entryInSidebar, new String []{});*/
		
		//Following two steps should be removed once the above issue is resolved
		logger.strongStep("Close the PDF Export dialog.");
		log.info("INFO: Close the PDF Export dialog.");
		pUi.clickLinkWait(PdfExportUI.pdfExportDlgClose);

		logger.strongStep("Verify the PDF Export dialog disappears.");
		pUi.waitForExportDialogDisappear();
		
		//Again User clicks on the pdf button and select all entries for content validation
		
		logger.strongStep("Click the Export as PDF button.");
		log.info("INFO: Click the Export as PDF button.");
		pUi.openExportAsPdfDialog();

		logger.strongStep("Select All Entries from 'Content' section");
		log.info("INFO: Select All Entries from 'Content' section");
		pUi.allEntriesAreSelected();

		logger.strongStep("Click the Generate PDF button in the dialog.");
		log.info("INFO: Click the Export as PDF button in the dialog.");
		pUi.clickGeneratePdfWaitToFinish();
		
		logger.strongStep("Validate the Sidebar content of PDF");
		log.info("INFO: Validate the Sidebar content of PDF");
		pUi.pdfContentValidation(pUi, logger, parentName, entryName, entryInSidebar , new String[]{Title1, Title2, Title3});
		
		//Again User clicks on the pdf button and select multiple entries for content validation
		
		logger.strongStep("Click the Export as PDF button.");
		log.info("INFO: Click the Export as PDF button.");
		pUi.openExportAsPdfDialog();

		logger.strongStep("Select Multiple Entries from 'Content' section");
		log.info("INFO: Select Multiple Entries from 'Content' section");
		String[] multipleEntries= pUi.selectedEntryName();
		for (int i = 0; i < multipleEntries.length; i++) {
			log.info("Entries:" +multipleEntries[i]);
		}

		logger.strongStep("Click the Generate PDF button in the dialog.");
		log.info("INFO: Click the Export as PDF button in the dialog.");
		pUi.clickGeneratePdfWaitToFinish();
		
		logger.strongStep("Validate the Sidebar content of PDF");
		log.info("INFO: Validate the Sidebar content of PDF");
		if(entryName.contains("ForumTopic")){
			pUi.pdfContentValidation(pUi, logger, parentName, entryName, entryInSidebar , new String []{multipleEntries[0].trim(), multipleEntries[1].trim()});
		}
		else{
		pUi.pdfContentValidation(pUi, logger, parentName, entryName, entryInSidebar , new String []{multipleEntries[1].trim(), multipleEntries[0].trim()});
		}

	}
	
	/**
	 * Common pdfContentValidation method to be used by different components.
	 * It clicks the Export as PDF button and checks content.
	 * 
	 * @param pUi
	 * @param logger
	 * @param parentName - the main entity (i.e activity name/forum name, etc)
	 * @param entryName- the child entity (i.e. activity entry name/forum post name,etc)
	 * @param entryInSidebar - whether child entry page is listed in sidebar
	 */
	public void pdfContentValidation(PdfExportUI pUi, DefectLogger logger, String parentName, String entryName,
			boolean entryInSidebar, String[] Title) {
		// PDF renderer just uses the page headline as TOC entry, design has
		// changed on the
		// TOC page to just use the main entity as headline so the sidebar shows
		// 2 identical entries now.
		logger.strongStep("Verify PDF sidebar contains the main entity and the table of content.");
		log.info("INFO: Verify PDF sidebar contains the main entity and the table of content: " + parentName);
		switchToFrameBySelector(pdfExportDlgIframe);
		switchToFrameBySelector(pdfContentIframe);
		
		if(testConfig.browserIs(BrowserType.FIREFOX)) {
			List<Element> items = driver.getElements(sideBarItems + "//a[text()='" + parentName + "']");
			Assert.assertTrue(items.size() == 2, "# of sidebar entries found with name " + parentName + " == 2, but was " + items.size());
			switchToTopFrame();

			// Activities doesn't have a sidebar entry for the entries because there is no headline.
			if (entryInSidebar) {
				logger.strongStep("Verify PDF sidebar contains entry: " + entryName);
				log.info("INFO: Verify PDF sidebar contains entry: " + entryName);
				pUi.getEntryInPdfSidebar(entryName);
			}

			logger.strongStep("Verify the parent name is on page 1: " + parentName);
			log.info("INFO: Verify the parent name is on page 1: " + parentName);
			pUi.checkLineExistsOnPage(1, parentName);

			String toc = "Table of contents";
			logger.strongStep("Verify the TOC is on page 2: " + toc + " " + parentName);
			log.info("INFO: Verify the TOC is on page 2: " + toc + " " + parentName);
			pUi.checkLineExistsOnPage(2, toc, parentName);

			logger.strongStep("Verify the Entry Names are displayed under Table of Contents page");
			log.info("Verify the Entry Names are displayed under Table of Contents page");
			pUi.validateEntriesInTOC(Title, parentName);

			logger.strongStep("Verify the entry name is on page 3: " + entryName);
			log.info("INFO: Verify the entry name is on page 3: " + entryName);
			pUi.checkLineExistsOnPage(3, entryName);
		} else {
			logger.strongStep("Verify PDF Page is displayed");
			log.info("INFO: Verify PDF Page is displayed");
			Assert.assertTrue(pUi.isElementPresent(PdfExportUI.pdfPage), "PDF is displayed");
			switchToTopFrame();
		}
		logger.strongStep("Close the PDF Export dialog.");
		log.info("INFO: Close the PDF Export dialog.");
		pUi.clickLinkWait(PdfExportUI.pdfExportDlgClose);

		logger.strongStep("Verify the PDF Export dialog disappears.");
		pUi.waitForExportDialogDisappear();

	}

	/**
	 * Check whether All Options of 'Information Included Section' is present in Export PDF Window.
	 * @param PdfExportUI
	 * @param DefectLogger
	 */
	public void validateInformationIncludeSectionList(PdfExportUI pUi, DefectLogger logger)
	{
		logger.strongStep("Click the Export as PDF button.");
		log.info("INFO: Click the Export as PDF button.");
		pUi.openExportAsPdfDialog();
		
		this.switchToFrameBySelector(pdfExportDlgIframe);
		Assert.assertTrue(this.fluentWaitElementVisible(pdfTitlePageChkBox), "Title Page Check box is present in Information Include Section List of Export PDF");
		Assert.assertTrue(this.fluentWaitElementVisible(pdfTOCChkBox), "Table of Content Check box is present in Information Include Section List of Export PDF");
		Assert.assertTrue(this.fluentWaitElementVisible(pdfCommentsChkBox), "Comments Check box is present in Information Include Section List of Export PDF");
		Assert.assertTrue(this.fluentWaitElementVisible(pdfTitleChkBox), "Title Check box is present in Information Include Section List of Export PDF");
		Assert.assertTrue(this.fluentWaitElementVisible(pdfSummaryChkBox), "Summary Check box is present in Information Include Section List of Export PDF");
		Assert.assertTrue(this.fluentWaitElementVisible(pdfTagsChkBox), "Tags Check box is present in Information Include Section List of Export PDF");
		Assert.assertTrue(this.fluentWaitElementVisible(pdfCreatedDateChkBox), "Creation Date Check box is present in Information Include Section List of Export PDF");
		Assert.assertTrue(this.fluentWaitElementVisible(pdfModifiedDateChkBox), "Modified Date Check box is present in Information Include Section List of Export PDF");
		this.switchToTopFrame();
	}
	
	/**
	 * Select All Options of 'Information Included Section' which are not default selected in Export PDF Window.
	 * @param testName
	 */
	public void SelectInformationIncludeSection(String testName)
	{
		this.switchToFrameBySelector(pdfExportDlgIframe);
		//Selecting the Comment Check box
		if(this.getFirstVisibleElement(pdfCommentsChkBox).getAttribute("class").contains("ng-empty"))
			this.getFirstVisibleElement(pdfCommentsChkBox).click();
		
		//Selecting the Summary Check box
		if(this.getFirstVisibleElement(pdfSummaryChkBox).getAttribute("class").contains("ng-empty"))
			this.getFirstVisibleElement(pdfSummaryChkBox).click();
		
		//Selecting the Tags Check box
		if(this.getFirstVisibleElement(pdfTagsChkBox).getAttribute("class").contains("ng-empty"))
			this.getFirstVisibleElement(pdfTagsChkBox).click();
		
		//Selecting the Created Date Check box
		if(this.getFirstVisibleElement(pdfCreatedDateChkBox).getAttribute("class").contains("ng-empty"))
			this.getFirstVisibleElement(pdfCreatedDateChkBox).click();
		
		//Selecting the Modification Date Check box
		if(this.getFirstVisibleElement(pdfModifiedDateChkBox).getAttribute("class").contains("ng-empty"))
			this.getFirstVisibleElement(pdfModifiedDateChkBox).click();
		
		//Selecting the Start and End Dates
		if(!testName.toLowerCase().contains("exportpdfactivity"))
		{
			if(this.getFirstVisibleElement(pdfStartDateTextArea).getAttribute("class").contains("ng-empty"))
			{	
				this.getFirstVisibleElement(pdfStartDateButton).click();
				this.getFirstVisibleElement(pdfTodayDateButton).click();
			}
			if(this.getFirstVisibleElement(pdfEndDateTextArea).getAttribute("class").contains("ng-empty"))
			{
				this.getFirstVisibleElement(pdfEndDateButton).click();
				this.getFirstVisibleElement(pdfTodayDateButton).click();
			}
		}
		this.switchToTopFrame();
	}
	
	
	/**
	 * Check whether All selected Options of 'Information Included Section' are present in the Exported PDF Preview.
	 * @param PdfExportUI
	 * @param DefectLogger
	 * @param map - contains parent and child value for description, tags, comment
	 * @param ParentName - the main entity (i.e activity name/forum name, etc)
	 * @param EntryName - the child entity (i.e. activity entry name/forum post name, etc)
	 * @param ValidationList - Options to be validated
	 */
	public void validateInformationIncludeSectionFunctionality(PdfExportUI pUi, DefectLogger logger, HashMap<String,String> map,
			String ParentName, String EntryName, String ValidationList) 
	{
		
		String pattern = "EEEEE MMMM d, yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		String year = String.valueOf((long)driver.executeScript("return new Date().getFullYear()"));
		String month = String.valueOf((long)driver.executeScript("return new Date().getMonth()")+1);
		String date_ = String.valueOf((long)driver.executeScript("return new Date().getDate()"));
		
		SimpleDateFormat scriptFormat = new SimpleDateFormat("dd/MM/yyyy");
		String date = simpleDateFormat.format(new Date());
		
		try {
			Date scriptDate;
			scriptDate = scriptFormat.parse(date_ + '/' + month + '/' + year);
			// Format browser date to fit PDF display
			SimpleDateFormat simpleDateFormat_pdf = new SimpleDateFormat("EEEEE MMMM d, yyyy");
			date = simpleDateFormat_pdf.format(scriptDate);
		} catch (ParseException e) {
			log.error("An Error occured:" + e.getMessage());
			e.printStackTrace();
		}
        
		String[] arr={ParentName, map.get("parentdesc"), map.get("parenttags")};
		
		
		logger.strongStep("Click the Generate PDF button in the dialog.");
		log.info("INFO: Click the Export as PDF button in the dialog.");
		pUi.clickGeneratePdfWaitToFinish();
		
		if (testConfig.browserIs(BrowserType.FIREFOX)) {
			// Verify the 'Title Page' in Preview of Exported PDF
			logger.strongStep("Verify the Title Page has Author Name on page 1: " + ParentName);
			log.info("INFO: Verify the Title Page has Author Name on page 1: " + ParentName);
			pUi.checkLocatorExistsOnPage(1, "xpath=//a[@title='" + map.get("author") + "']");

			logger.strongStep("Verify Author, Description, Tags on Title Page for Parent Name "
					+ "on page 1: " + ParentName + " , Description : "+ map.get("parentdesc")+" , Tags : "+map.get("parenttags"));
			log.info("INFO: Verify Author, Description, Tags, Creation Date and Modification Date on Title Page for Parent Name"
					+ " on page 1: " + ParentName + " , Description : "+ map.get("parentdesc")+" , Tags : "+map.get("parenttags"));
			pUi.checkLineExistsOnPage(1, arr);

			logger.strongStep("Verify Creation Date and Modification Date on Title Page for Parent Name "+ ParentName+"on page 1:" + " Creation Date : "+date);
			log.info("INFO: Verify Creation Date and Modification Date on Title Page for Parent Name "+ParentName+ " on page 1: "+ " Creation Date : "+date);
			pUi.checkDateExistsOnPage(1,date);

			//Verify the 'Title' in Preview of Exported PDF
			logger.strongStep("Verify the Title Page is on page 1: " + ParentName);
			log.info("INFO: Verify the Title Page is on page 1: " + ParentName);
			pUi.checkLineExistsOnPage(2, ParentName);

			//Verify the 'Table of Content' in Preview of Exported PDF
			String toc = "Table of contents";
			logger.strongStep("Verify the TOC is on page 2: " + toc + " " + ParentName);
			log.info("INFO: Verify the TOC is on page 2: " + toc + " " + ParentName);
			pUi.checkLineExistsOnPage(2, toc, ParentName);

			String[] childarr={EntryName, map.get("childdesc"),map.get("childtags")};
			logger.strongStep("Verify Author, Description, Tags, Creation Date and Modification Date on Title Page for Parent Name "
							+ "on page 1: " + EntryName + " , Description : "+ map.get("childdesc")+" , Tags : "+map.get("childtags"));
			log.info("INFO: Verify Author, Description, Tags, Creation Date and Modification Date on Title Page for Parent Name"
							+ " on page 1: " + EntryName + " , Description : "+ map.get("childdesc")+" , Tags : "+map.get("childtags"));
			pUi.checkLineExistsOnPage(3, childarr);

			logger.strongStep("Verify Creation Date and Modification Date on Title Page for Parent Name "+ EntryName+"on page 1:" + " Creation Date : "+date);
			log.info("INFO: Verify Creation Date and Modification Date on Title Page for Parent Name "+EntryName+ " on page 1: "+ " Creation Date : "+date);
			pUi.checkDateExistsOnPage(3,date);

			if(ValidationList.contains("comments")) {
				logger.strongStep("Verify Child Comments for Parent Name on page 3: " + map.get("childcomment"));
				log.info("INFO: Verify Child Comments for Parent Name on page 3: " + map.get("childcomment"));
				pUi.checkLineExistsOnPage(3, map.get("childcomment"));
			}
		} else {
			logger.strongStep("Verify PDF Page is displayed");
			log.info("INFO: Verify PDF Page is displayed");
			switchToFrameBySelector(pdfExportDlgIframe);
			switchToFrameBySelector(pdfContentIframe);
			Assert.assertTrue(pUi.isElementPresent(PdfExportUI.pdfPage), "PDF is displayed");
			
			switchToTopFrame();
			
			logger.strongStep("Close the PDF Export dialog.");
			log.info("INFO: Close the PDF Export dialog.");
			pUi.clickLinkWait(PdfExportUI.pdfExportDlgClose);
		}
		switchToTopFrame();
	}
	
	/**
	 * Common contentSelection method to be used by Wikis components.
	 * It clicks the Export as PDF button and validate side bar content.
	 * 
	 * @param pUi
	 * @param logger
	 * @param parentName- the main entity (i.e activity name/forum name, etc)
	 * @param entryName- the child entity (i.e. activity entry name/forum post name,etc)
	 * @param entryInSidebar - whether child entry page is listed in sidebar
	 */
	public void contentSelectionforWiki(PdfExportUI pUi, DefectLogger logger, String parentName, String entryName,
			boolean entryInSidebar ,String Title1, String Title2,String Title3) {

		logger.strongStep("The PDF icon is available with title 'Export as PDF' ");
		log.info("INFO: Validate title 'Export as PDF'");
		pUi.validatePDFLOGOtoolTip();

		logger.strongStep("Click the Export as PDF button.");
		log.info("INFO: Click the Export as PDF button.");
		pUi.openExportAsPdfDialog();

		logger.strongStep("Deselect Entries from 'Content' section");
		log.info("INFO: Deselect Entries from 'Content' section");
		pUi.noEntriesAreSelected();

		logger.strongStep("Click the Generate PDF button in the dialog.");
		log.info("INFO: Click the Export as PDF button in the dialog.");
		pUi.clickGeneratePdfWaitToFinish();
		
		//Validation for No Entries Selection condition is commented due to know existing issue in application
		logger.strongStep("Validate the Sidebar content of PDF");
		log.info("INFO: Validate the Sidebar content of PDF");
		/*pUi.pdfContentValidation(pUi, logger, parentName, entryName, entryInSidebar, new String []{});*/
		
		//Following two steps should be removed once the above issue is resolved
		logger.strongStep("Close the PDF Export dialog.");
		log.info("INFO: Close the PDF Export dialog.");
		pUi.clickLinkWait(PdfExportUI.pdfExportDlgClose);

		logger.strongStep("Verify the PDF Export dialog disappears.");
		pUi.waitForExportDialogDisappear();
		
		//Again User clicks on the pdf button and select all entries for content validation
		
		logger.strongStep("Click the Export as PDF button.");
		log.info("INFO: Click the Export as PDF button.");
		pUi.openExportAsPdfDialog();

		logger.strongStep("Select All Entries from 'Content' section");
		log.info("INFO: Select All Entries from 'Content' section");
		pUi.allEntriesAreSelected();

		logger.strongStep("Click the Generate PDF button in the dialog.");
		log.info("INFO: Click the Export as PDF button in the dialog.");
		pUi.clickGeneratePdfWaitToFinish();
		
		logger.strongStep("Validate the Sidebar content of PDF");
		log.info("INFO: Validate the Sidebar content of PDF");
		pUi.pdfContentValidation(pUi, logger, parentName, entryName, entryInSidebar , new String[]{Title1, Title2, Title3});
		
		//Again User clicks on the pdf button and select multiple entries for content validation
		
		logger.strongStep("Click the Export as PDF button.");
		log.info("INFO: Click the Export as PDF button.");
		pUi.openExportAsPdfDialog();

		logger.strongStep("Select Multiple Entries from 'Content' section");
		log.info("INFO: Select Multiple Entries from 'Content' section");
		String[] multipleEntries= pUi.selectedEntryNameForWiki();
		for (int i = 0; i < multipleEntries.length; i++) {
			log.info("Entries:" +multipleEntries[i]);
		}

		logger.strongStep("Click the Generate PDF button in the dialog.");
		log.info("INFO: Click the Export as PDF button in the dialog.");
		pUi.clickGeneratePdfWaitToFinish();
		
		logger.strongStep("Validate the Sidebar content of PDF");
		log.info("INFO: Validate the Sidebar content of PDF");
		pUi.pdfContentValidation(pUi, logger, parentName, entryName, entryInSidebar , new String []{multipleEntries[1].trim(), multipleEntries[3].trim()});

	}
	
	/**
	 * Common multpleEntriesAreSelected method to be used by Wiki component. 
	 * It will select only random child entries.
	 */
	public  String[] selectedEntryNameForWiki() {

		log.info("Verify that Entry Selection Checkbox is displayed ");
		switchToFrameBySelector(pdfExportDlgIframe);
		Assert.assertTrue(isElementVisible(parentEntriesCheckbox), "Entry Selection Checkbox is not displayed");

		log.info("By Clicking on Parent Entries Checkbox application will deselect all default entires");
		Element checkBoxElement= driver.getFirstElement(parentEntriesCheckbox);
		if (checkBoxElement.isSelected())
		{
			checkBoxElement.click();
		}

		log.info("Select Random Checkboxes");
		List<Element> childEntries = driver.getElements(childEntriesCheckboxs);
		String[] selectedEntryName = new String[4];
		for (int i = 0; i < childEntries.size(); i++) {
			if (i % 2 == 0) {
				childEntries.get(i).click();
				//selectedEntryName[i]= childEntries.get(i).getText();
			}
		}
		List <Element> selectedEntries = driver.getElements(selectedchildEntriesCheckboxs);
		for (int i = 0; i < selectedEntries.size(); i++) {
			selectedEntryName[i]= selectedEntries.get(i).getText();		
		}
		switchToTopFrame();
		return selectedEntryName;
	}

	/**
	 * Select the Activity ToDoItems related options for Export PDF.
	 */
	public void selectInformationIncludeToDo()
	{
		
		this.switchToFrameBySelector(pdfExportDlgIframe);
		if(this.getFirstVisibleElement(pdfExportToDoEntry).getAttribute("class").contains("ng-empty"))
			this.getFirstVisibleElement(pdfExportToDoEntry).click();
		if(this.getFirstVisibleElement(pdfExportToDoEntryAll).getAttribute("class").contains("ng-empty"))
			this.getFirstVisibleElement(pdfExportToDoEntryAll).click();
		if(this.getFirstVisibleElement(pdfToDoItemDueDate).getAttribute("class").contains("ng-empty"))
			this.getFirstVisibleElement(pdfToDoItemDueDate).click();
		this.switchToTopFrame();
	}
	
	/**
	 * Validate the Activity ToDoItems related options for Export PDF.
	 */
	public void validateToDoItems(PdfExportUI pUi, DefectLogger logger, HashMap<String,String> map,
			String ParentName, String ToDoItemName)
	{
		String[] arr={ToDoItemName, map.get("childdesc")};
		
		logger.strongStep("Click the Generate PDF button in the dialog.");
		log.info("INFO: Click the Export as PDF button in the dialog.");
		pUi.clickGeneratePdfWaitToFinish();
		
		logger.strongStep("Verify ToDoItem Title and Description is present on Page2: "  + ToDoItemName + " , Description : "+ map.get("childdesc"));
		log.info("INFO: Verify ToDoItem Title and Description is present on Page2: "  + ToDoItemName + " , Description : "+ map.get("childdesc"));
		Assert.assertTrue(pUi.checkLineExistsonPDFPage(2, arr));
				
		Assert.assertFalse(pUi.checkLineExistsonPDFPage(2, map.get("Entryname")));
	}
	
	/**
	 * Method to get the current UTC date
	 * @return
	 */
	public Date getCurrentUTCDate()
	{
		String pattern = "EEEEE MMMM d, yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		
		// set UTC time zone by using SimpleDateFormat class 
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		//create another instance of the SimpleDateFormat class for local date format  
		SimpleDateFormat ldf = new SimpleDateFormat(pattern);  
		Date date= null;
		try {
			date = ldf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage()); 
		}
		return date;
		
	}

}
