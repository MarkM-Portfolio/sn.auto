package com.ibm.conn.auto.webui;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseHighlights;
import com.ibm.conn.auto.data.Data;

public class TinyEditorUI extends HCBaseUI {

	public TinyEditorUI(RCLocationExecutor driver) {
		super(driver);
	}

	protected static Logger log = LoggerFactory.getLogger(ActivitiesUI.class);
	private  FilesUI filesUI = FilesUI.getGui(cfg.getProductName(), driver);
	static String imageFile,imageThisCommunity;
	public static int picturecount;

	/*Tiny Editor locators which are appended with parent tag insde udateLocators() method 
	when there are 2 Tiny Editor screen on same page*/
	public String undoButton = "css=button[title='Undo']";
	public String redoButton = "css=button[title='Redo']";
	public String moreButton = "css=button[title='More...']";
	public String rightToLeftButton = "css=button[title='Right to left']";
	public String leftToRightButton = "css=button[title='Left to right']";
	public String tinyEditorFrame = "css=iframe[class='tox-edit-area__iframe'] ,iframe[title='What do you want to share?'],iframe[title='Rich Text Editor, editor1']";
	public String tinyEditorIndentIncButton = "css=div > button[title='Increase indent']";
	public String tinyEditorIndentDecButton = "css=div > button[title='Decrease indent']";
	public String tinyEditorBulletButton = "css=div[title='Bullet list']>span[class='tox-tbtn'] , div[title='Bullet list']>span[class='tox-tbtn tox-tbtn--enabled']";
	public String tinyEditorNumberButton = "css=div[title='Numbered list'] span[class='tox-icon tox-tbtn__icon-wrap']>svg";
	public String tinyEditorInsertButton = "xpath=//div/button[@title='Insert Menu']";
	public String tinyEditorPermanentPenButton = "css=button[title='Permanent pen']";
	public String tinyEditorAlignButton = "css=div > button[title=Align]";
	public String tinyEditorSearchButton = "css=[title='Find and replace']";
	public String tinyEditorSettingsMenuButton = "css=button[title='Settings Menu']";
	public String tinyEditorEmoticonsButton = "css=button[title='Emojis']";
	public String tinyEditorFullScreenButton = "css=button[title='Fullscreen']";
	public String tinyEditorBlockquoteButton = "css=button[title='Blockquote']";
	public String tinyEditorFontMenuButton = "css=button[title='Font Menu']>div[class='tox-tbtn__select-chevron']>svg";
	public String tinyEditorParagraph1 = "css=span.tox-tbtn__select-label";
	public String tinyEditorBoldButton = "css=button[title='Bold']";
	public String tinyEditorItalicButton = "css=button[title='Italic']";
	public String tinyEditorUnderlineButton = "css=button[title='Underline']";
	
	//Tiny Editor locators
	public static String tinyEditorParagraph2 = "xpath=//div/p[text()='Paragraph']";
	public static String tinyEditorBody = "css=body";
	public static String tinyEditorHeaderDiv = "xpath=//div/div[text()='Div']";
	public static String tinyEditorHeaderPre = "xpath=//div/pre[text()='Pre']";
	public static String tinyEditorFormatPainterButton = "css=button[title='Format Painter']";
	public static String tinyEditorBIUBody = "css=body>p>span>em>strong";
	public static String tinyEditorBIUBodyWithoutSelect = "css=body>p>span>span>em>strong";
	public static String tinyEditorPermanentPenBody = "css=body>p>strong>span";
	public static String tinyEditorDefaultBody = "css=body>p";
	public static String tinyEditorLeftButton = "xpath=//div/div[text()='Left']";
	public static String tinyEditorCenterButton = "xpath=//div/div[text()='Center']";
	public static String tinyEditorRightButton = "xpath=//div/div[text()='Right']";
	public static String tinyEditorJustifyButton = "xpath=//div/div[text()='Justify']";
	public static String tinyEditorNumberOfLinesInBody = "css=body>p";
	public static String tinyEditorNumberOfBulletLinesInBody = "css=body>ul>li";
	public static String tinyEditorNumberOfNumberLinesInBody = "css=body>ol>li";
	public static String tinyEditorLinkToconnectionsFiles= "css=div[title='Link to Connections Files']";
	public static String tinyEditorLinkToconnectionsFilesThisCommunity= "css=span:contains(This Community)";
	public static String tinyEditorLinkToconnectionsFilesUpload= "css=span:contains(My Computer)";
	public static String listOfImagesInInsertLinkToFilesPopup = "xpath=//div[starts-with(@id,'lconn_share_widget_Node')][@name='title']";
	public static String insertLinkButtonInInsertLinkToFilesPopup  = "css=input[value='Insert Links']";
	public static String insertLinkOkButtonForums  = "css=input#submit_button";
	public static String tinyEditorHorizontalLineButton = "css=[title='Horizontal line']";
	public static String tinyEditorSpecialCharacterButton = "css=[title='Special character...']";
	public static String tinyEditorSpecialCharacterCharacters = "//div[@class='tox-collection__item-icon']";
	public static String tinyEditorSpecialCharacterAllLink = "css=[class='tox-dialog__body-nav-item tox-tab tox-dialog__body-nav-item--active']";
	public static String tinyEditorBodyDOM = "css=body#tinymce";
	public static String tinyEditorTableButton = "css=div[title='Table']>div[class='tox-collection__item-label']";
	public static String numberOfRowsInTableOfTinyEditor = "css=body[id='tinymce']>table>tbody>tr";
	public static String numberOfColumnsInTableOfTinyEditor = "css=body#tinymce>table>tbody>tr:nth-of-type(1)>td";
	public static String numberOfRowsInNestedTableOfTinyEditor = "css=body[id='tinymce']>table table>tbody>tr";
	public static String numberOfColumnsInNestedTableOfTinyEditor = "css=body#tinymce>table table>tbody>tr:nth-of-type(1)>td";
	public static String firstCellInTableOfTinyEditor = "css=body#tinymce>table>tbody>tr:nth-of-type(1)>td:nth-of-type(1)";
	public static String secondCellInTableOfTinyEditor = "css=body#tinymce>table>tbody>tr:nth-of-type(1)>td:nth-of-type(2)";
	public static String thirdCellInTableOfTinyEditor = "css=body#tinymce>table>tbody>tr:nth-of-type(1)>td:nth-of-type(3)";
	public static String firstBulletCellInTableOfTinyEditor = "css=body#tinymce>table>tbody>tr:nth-of-type(1)>td:nth-of-type(1)>ul>li";
	public static String tinyEditorFontsButton = "xpath=//div[@title='Fonts']//div[text()='Fonts']";
	public static String tinyEditorFontSizesButton = "xpath=//div[@title='Font sizes']//div[text()='Font sizes']";
	public static String tinyEditorInsertLinkButton = "css=[title='Link...']";
	public static String tinyEditorRichTextInsertImageButton = "css=[title='Insert/Edit Image']";
	public static String tinyEditorSourceTextBox ="css=div[class='tox-control-wrap']>input";
	public static String tinyEditorWidthTextBox ="//label[text()='Width']/../input";
	public static String tinyEditorHeightTextBox ="//label[text()='Height']/../input";
	public static String tinyEditorAlternateDescTextBox ="//label[text()='Alternative description']/../input";
	public static String tinyEditorLinkURL = "css=[type='url']";
	public static String tinyEditorLinkTitle = "//div/div/label[text()='Title']/following-sibling::input";
	public static String tinyEditorLinkSaveButton="xpath=//div[@class='tox-dialog']//button[text()='Save']";	
	public static String tinyEditorParagraphFromLeft="xpath=//body/p[1]";
	public static String tinyEditorParagraphFromRight="css=body>p[dir='rtl']";
	public static String tinyEditorFindTextBox = "css=input[placeholder='Find']";
	public static String tinyEditorReplaceWithTextBox = "css=input[placeholder='Replace with']";
	public static String tinyEditorFindButton = "css=button[title='Find']";
	public static String tinyEditorReplaceButton = "css=button[title='Replace']";
	public static String tinyEditorInsertImagePopup = "css=[class='lotusDialogBorder']";
	public static String tinyEditorHelpWindow = "css=div[class='tox-dialog tox-dialog--width-md']";
	public static String tinyEditorHelpWindowCloseButton = "css=div[class='tox-dialog tox-dialog--width-md']>div[class='tox-dialog__header']>button";
	public static String tinyEditorNotFindAlertMessage ="css=div[class='tox-dialog tox-alert-dialog']";
	public static String tinyEditorNotFindAlertMessageOkButton="css=div[class='tox-dialog tox-alert-dialog'] button[title='OK']";
	public static String tinyEditorFindReplaceCloseWindowButton = "css=[class='tox-dialog__header']>button[title='Close']";
	public static String tinyEditorWrongLinkErrorDialog = "css=div.tox-dialog__body-content>p";
	public static String tinyEditorWrongLinkErrorButton = "css=button[title=No]";
	public static String tinyEditorLinkText = "xpath=//div/div/label[text()='Text to display']/following-sibling::input";
	public static String tinyEditorLinkOpenOptions = "xpath=//div/div/div/select";	
	public static String tinyEditorDOMPara = "xpath=//div[@id='descContent']/div[@id='descText']/p";
	public static String tinyEditorImageLink = "xpath=//div[@class='tox-menu tox-collection tox-collection--list tox-selected-menu']//div[contains(text(),'Image')]";
	public static String tinyEditorExistingImageLink = "css=[id='existingImageTabLink']";
	public static String tinyEditorExistingImage = "css=td.uploadedImageTd > img";
	public static String tinyEditorImageSource = "xpath=//input[@type='url']";
	public static String tinyEditorSuperScriptButton = "css=div[title='Superscript']";
	public static String tinyEditorSubScriptButton = "css=div[title='Subscript']";
	public static String tinyEditorStrikeThroughButton = "css=div[title='Strikethrough']";
	public static String tinyEditorFullScreenWindow = "css=div.tox.tox-tinymce.tox-fullscreen";
	public static String tinyEditorRestoreScreenWindow = "css=div.tox.tox-tinymce";
	public static String tinyEditorFontColor = "xpath=//div[@class='tox-collection__group']//div[@title='Red']";
	public static String tinyEditorTextColorLink = "xpath=//div[text()='Text color']";
	public static String tinyEditorBackgroundColorLink = "xpath=//div[text()='Background color'] ";
	public static String tinyEditorSettingWordCountlink = "xpath=//div[text()='Word count']";
	public static String tinyEditorSettingsWordCountValue = "xpath=//table[@class='tox-dialog__table']/tbody/tr[1]";
	public static String tinyEditorSettingsCloseButton = "css=[class='tox-dialog__footer'] button[title=Close]";
	public static String tinyEditorRichContentDOMPara = "xpath=//div[@id='showRichContent']//p";
	public static String TinyEditorMediaLink = "css=div[title='Media...']";
	public static String TinyEditorMediaSaveButton = "css=button[title='Save']";
	public static String tinyEditorCodeSampleButton = "css=div[title='Code sample...']";
    public static String tinyEditorSelectLaunguageButton = "css=div[class='tox-selectfield']";
    public static String tinyEditorSelectLaunguageDropdown = "css=div[class='tox-selectfield']>select[id^='form-field']";
    public static String tinyEditorInsertCodeTextArea = "css=textarea[class='tox-textarea']";
	public static String TinyEditorWebURLLink="xpath=//a[text()='Web URL']";
	public static String TinyEditorWebURLLable="xpath=//span[text()='Web URL']";
	public static String TinyEditorWebImageURL = "//input[@id='blogImgWebUrlInput'] | //input[@id='insertLink_url'] | //input[@name='URL']";
	public static String TinyEditorInsertImageButton="css=input[value='Insert Image']";
	public static String TinyEditorInserIFrameOption = "xpath=//div[text()='Insert/edit iframe']";
	public static String TinyEditorURLTextArea = "css=input[type=url]";
	public static String TinyEditoriFrameSaveButton="css=button[title='Save']";
	public static String TinyEditorUploadButton="css=div[class='lotusDialogFooter']>input[value='Upload Image']";
	public static String TinyEditorUploadButtonForums="css=div[class='lotusFormFooter lotusDialogFooter']>input[value='Upload Image']";
	public static String TinyEditorLinktoConnection_UploadButton="css=div[class='lotusDialogFooter']>input[value='Upload']";
	public static String TinyEditorMentionUserList = "xpath=//div[@class='tox-menu tox-collection tox-collection--list']";
	public static String TinyEditorActivityOutlineLink = "css=span#actNavPaneloutlineLabel";
    public static String PreviewTitle = "css=div[aria-label='PLACEHOLDER']>div[class='lconnPreview']>div[class='lconnPreviewContent']>div[class='lconnPreviewContentTitle']";
    public static String Browse_Button = "css=#lconn_btn_browse_files";
    public static String FileInputField = "css=input[type='file'][id*='_contents_contents']";
	public static String FileInputField3 = "css=input[type='file'][id*='_contents']";

	public static TinyEditorUI getGui(String product, RCLocationExecutor driver) {
		return new TinyEditorUI(driver);
	}
	
	/**
	 * Method used to update the CSS value by appending parent tag for relevant Tiny Editor, if in case screen has 2 Tiny Editor
	 */
	public void updateLocators(String locator) {
		rightToLeftButton = "css=[id='PLACEHOLDER'] button[title='Right to left']".replace("PLACEHOLDER", locator);
		leftToRightButton = "css=[id='PLACEHOLDER'] button[title='Left to right']".replace("PLACEHOLDER", locator);
		tinyEditorFrame = "css=[id='PLACEHOLDER'] iframe[class='tox-edit-area__iframe']".replace("PLACEHOLDER",locator);
		moreButton = "css=[id='PLACEHOLDER'] button[title='More...']".replace("PLACEHOLDER",locator);
		tinyEditorIndentIncButton = "css=[id='PLACEHOLDER'] div > button[title='Increase indent']".replace("PLACEHOLDER",locator);
		tinyEditorIndentDecButton = "css=[id='PLACEHOLDER'] div > button[title='Decrease indent']".replace("PLACEHOLDER",locator);
		tinyEditorParagraph1 = "css=[id='PLACEHOLDER'] span.tox-tbtn__select-label".replace("PLACEHOLDER",locator);
		tinyEditorAlignButton = "css=[id='PLACEHOLDER'] div > button[title=Align]".replace("PLACEHOLDER",locator);
		tinyEditorBoldButton = "css=[id='PLACEHOLDER'] button[title='Bold']".replace("PLACEHOLDER",locator);
		tinyEditorItalicButton = "css=[id='PLACEHOLDER'] button[title='Italic']".replace("PLACEHOLDER",locator);
		tinyEditorUnderlineButton = "css=[id='PLACEHOLDER'] button[title='Underline']".replace("PLACEHOLDER",locator);
		tinyEditorPermanentPenButton = "css=[id='PLACEHOLDER'] button[title='Permanent pen']".replace("PLACEHOLDER",locator);
		tinyEditorFontMenuButton = "css=[id='PLACEHOLDER'] button[title='Font Menu']".replace("PLACEHOLDER",locator);
		tinyEditorFullScreenButton = "css=[id='PLACEHOLDER'] button[title='Fullscreen']".replace("PLACEHOLDER",locator);
		tinyEditorInsertButton = "xpath=//div[@id='PLACEHOLDER']//div/button[@title='Insert Menu']".replace("PLACEHOLDER",locator);
		tinyEditorNumberButton = "css=[id='PLACEHOLDER'] div[title='Numbered list']>span[class='tox-tbtn']".replace("PLACEHOLDER",locator);
		tinyEditorBulletButton = "css=[id='PLACEHOLDER'] div[title='Bullet list']>span".replace("PLACEHOLDER",locator);
		tinyEditorBlockquoteButton = "css=[id='PLACEHOLDER'] button[title='Blockquote']".replace("PLACEHOLDER",locator);
		tinyEditorSearchButton = "css=[id='PLACEHOLDER'] [title='Find and replace']".replace("PLACEHOLDER",locator);
		tinyEditorSettingsMenuButton = "css=[id='PLACEHOLDER'] button[title='Settings Menu']".replace("PLACEHOLDER",locator);
		undoButton = "css=[id='PLACEHOLDER'] button[title='Undo']".replace("PLACEHOLDER",locator);
		redoButton = "css=[id='PLACEHOLDER'] button[title='Redo']".replace("PLACEHOLDER",locator);
		tinyEditorEmoticonsButton = "css=[id='PLACEHOLDER'] button[title='Emojis']".replace("PLACEHOLDER",locator);
	}

	/**
	 * Method to verify Undo Redo functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyUndoRedoInTinyEditor(String text) {

		this.clearInTinyEditor();
		typeInTinyEditor(text.trim());
		this.getFirstVisibleElement(undoButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertFalse(isTextPresent(text.trim()));
		switchToTopFrame();
		this.getFirstVisibleElement(redoButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertTrue(isTextPresent(text.trim()));
		switchToTopFrame();
	}

	/**
	 * Method to verify Paragraphs and Headers functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyParaInTinyEditor(String text) {
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorParagraph1).click();
		this.getFirstVisibleElement(tinyEditorParagraph2).click();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(1, driver.getVisibleElements(tinyEditorBodyDOM + ">p").size());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().trim().equalsIgnoreCase(text));
		log.info("Successfully Verified Paragraph in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorParagraph1).click();
		this.getFirstVisibleElement(getTinyEditorHeader(1)).click();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(1, driver.getVisibleElements(tinyEditorBodyDOM + ">h" + 1).size());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">h" + 1).getText().trim().equalsIgnoreCase(text));
		log.info("Successfully Verified Header1 in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorParagraph1).click();
		this.getFirstVisibleElement(getTinyEditorHeader(3)).click();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(1, driver.getVisibleElements(tinyEditorBodyDOM + ">h" + 3).size());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">h" + 3).getText().trim().equalsIgnoreCase(text));
		log.info("Successfully Verified Header3 in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorParagraph1).click();
		this.getFirstVisibleElement(getTinyEditorHeader(6)).click();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(1, driver.getVisibleElements(tinyEditorBodyDOM + ">h" + 6).size());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">h" + 6).getText().trim().equalsIgnoreCase(text));
		log.info("Successfully Verified Header6 in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorParagraph1).click();
		this.getFirstVisibleElement(tinyEditorHeaderPre).click();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(1, driver.getVisibleElements(tinyEditorBodyDOM + ">pre").size());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">pre").getText().trim().equalsIgnoreCase(text));
		log.info("Successfully Verified Header Pre in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		selectAllFromTinyEditor();
		this.getFirstVisibleElement(tinyEditorParagraph1).click();
		this.getFirstVisibleElement(getTinyEditorHeader(2)).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(driver.getVisibleElements(tinyEditorBodyDOM + ">h" + 2).get(0).getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">h" + 2).getText().trim().equalsIgnoreCase(text));
		log.info("Successfully Verified Header2 in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		selectAllFromTinyEditor();
		this.getFirstVisibleElement(tinyEditorParagraph1).click();
		this.getFirstVisibleElement(getTinyEditorHeader(4)).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(driver.getVisibleElements(tinyEditorBodyDOM + ">h" + 4).get(0).getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">h" + 4).getText().trim().equalsIgnoreCase(text));
		log.info("Successfully Verified Header4 in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		selectAllFromTinyEditor();
		this.getFirstVisibleElement(tinyEditorParagraph1).click();
		this.getFirstVisibleElement(tinyEditorHeaderDiv).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(driver.getVisibleElements(tinyEditorBodyDOM + ">div").get(0).getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">div").getText().trim().equalsIgnoreCase(text));
		log.info("Successfully Verified Header Div in Tiny Editor");
		switchToTopFrame();
	}

	/**
	 * Method to verify Bullets and Numbers functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyBulletsAndNumbersInTinyEditor(String text) {
		
		this.clearInTinyEditor();
		clickLink(tinyEditorNumberButton);
		typeTwoLinesInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertNotNull(driver.getVisibleElements(tinyEditorNumberOfNumberLinesInBody));
		List<Element> elem = driver.getVisibleElements(tinyEditorNumberOfNumberLinesInBody);
		for(int j =0;j<2;j++)
		{
			Assert.assertEquals(elem.get(j).getText().trim(), text);
		}
		switchToTopFrame();
		
		elem.clear();
		this.clearInTinyEditor();
		clickLinkWaitWd(createByFromSizzle(tinyEditorBulletButton), 4,"Click on Number button");
		typeTwoLinesInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertNotNull(driver.getVisibleElements(tinyEditorNumberOfBulletLinesInBody));
		elem = driver.getVisibleElements(tinyEditorNumberOfBulletLinesInBody);
		for(int j =0;j<2;j++)
		{
			Assert.assertEquals(elem.get(j).getText().trim(), text);
		}
		switchToTopFrame();
	}

	/**
	 * Method to get number of lines present in Tiny Editor
	 */
	public int getNumberOfLinesInTinyEditorBody() {
		switchToFrameBySelector(tinyEditorFrame);
		log.info("Return number of lines inside Tiny Editor");
		int size = driver.getVisibleElements(tinyEditorNumberOfLinesInBody).size();
		log.info("Return number of lines inside Tiny Editor as - " + size);
		switchToTopFrame();
		return size;

	}

	/**
	 * Method to get number of bullet lines present in Tiny Editor
	 */
	public int getNumberOfBulletLinesInTinyEditorBody() {
		switchToFrameBySelector(tinyEditorFrame);
		int size = driver.getVisibleElements(tinyEditorNumberOfBulletLinesInBody).size();
		log.info("Return number of bullet lines inside Tiny Editor as - " + size);

		switchToTopFrame();

		return size;
	}

	/**
	 * Method to get number of Number Lines present in Tiny Editor
	 */
	public int getNumberOfNumberLinesInTinyEditorBody() {
		log.info("Switch to Tiny Editor frame");
		switchToFrameBySelector(tinyEditorFrame);

		log.info("Return number of Number lines inside Tiny Editor");
		int size = driver.getVisibleElements(tinyEditorNumberOfNumberLinesInBody).size();

		log.info("Switch to main frame ");
		switchToTopFrame();

		return size;

	}

	/**
	 * Method to verify Bright,Italic and Underline functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyAttributesInTinyEditor(String text) {
	this.clearInTinyEditor();
	this.typeandSelectInTinyEditor(text);
	this.getFirstVisibleElement(tinyEditorBoldButton).click();
	this.getFirstVisibleElement(tinyEditorItalicButton).click();
	this.getFirstVisibleElement(tinyEditorUnderlineButton).click();
	switchToFrameBySelector(tinyEditorFrame);
	Assert.assertTrue(driver.isElementPresent(tinyEditorBIUBody));
	Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBIUBody).getText().trim().equalsIgnoreCase(text));
	switchToTopFrame();
	this.clearInTinyEditor();
	this.getFirstVisibleElement(tinyEditorBoldButton).click();
	this.getFirstVisibleElement(tinyEditorItalicButton).click();
	this.getFirstVisibleElement(tinyEditorUnderlineButton).click();
	typeInTinyEditor(text);
	switchToFrameBySelector(tinyEditorFrame);
	Assert.assertTrue(driver.isElementPresent(tinyEditorBIUBodyWithoutSelect));
	Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBIUBodyWithoutSelect).getText().trim().equalsIgnoreCase(text));
	switchToTopFrame();
	
	//Below Validation Added as part of Extended development for Bold functionality
	this.clearInTinyEditor();
	typeInTinyEditor(text);
	this.getFirstVisibleElement(tinyEditorBoldButton).click();
	Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBoldButton).getAttribute("aria-pressed"), "true");
	log.info("Bold Button is Successfully getting Enabled");
	typeInTinyEditor(text);
	this.getFirstVisibleElement(tinyEditorBoldButton).click();
	Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBoldButton).getAttribute("aria-pressed"), "false");
	log.info("Bold Button is Successfully getting Disabled");
	switchToFrameBySelector(tinyEditorFrame);
	Assert.assertTrue(this.getFirstVisibleElement(tinyEditorDefaultBody).getText().trim().equalsIgnoreCase(text+text));
	switchToTopFrame();
	typeInTinyEditor(text);
	switchToFrameBySelector(tinyEditorFrame);
	Assert.assertTrue(this.getFirstVisibleElement(tinyEditorDefaultBody+">strong").getText().trim().equalsIgnoreCase(text));
	log.info("Bold Functionality is Successfully working");
	switchToTopFrame();
	
	//Below Validation Added as part of Extended development for Italic functionality
	this.clearInTinyEditor();
	typeInTinyEditor(text);
	this.getFirstVisibleElement(tinyEditorItalicButton).click();
	Assert.assertEquals(this.getFirstVisibleElement(tinyEditorItalicButton).getAttribute("aria-pressed"), "true");
	log.info("Italic Button is Successfully getting Enabled");
	typeInTinyEditor(text);
	this.getFirstVisibleElement(tinyEditorItalicButton).click();
	Assert.assertEquals(this.getFirstVisibleElement(tinyEditorItalicButton).getAttribute("aria-pressed"), "false");
	log.info("Italic Button is Successfully getting Disabled");
	switchToFrameBySelector(tinyEditorFrame);
	Assert.assertTrue(this.getFirstVisibleElement(tinyEditorDefaultBody).getText().trim().equalsIgnoreCase(text+text));
	switchToTopFrame();
	typeInTinyEditor(text);
	switchToFrameBySelector(tinyEditorFrame);
	Assert.assertTrue(this.getFirstVisibleElement(tinyEditorDefaultBody+">em").getText().trim().equalsIgnoreCase(text));
	log.info("Italic Functionality is Successfully working");
	switchToTopFrame();

	//Below Validation Added as part of Extended development for Underline functionality
	this.clearInTinyEditor();
	typeInTinyEditor(text);
	this.getFirstVisibleElement(tinyEditorUnderlineButton).click();
	Assert.assertEquals(this.getFirstVisibleElement(tinyEditorUnderlineButton).getAttribute("aria-pressed"), "true");
	log.info("UnderLine Button is Successfully getting Enabled");
	typeInTinyEditor(text);
	this.getFirstVisibleElement(tinyEditorUnderlineButton).click();
	Assert.assertEquals(this.getFirstVisibleElement(tinyEditorUnderlineButton).getAttribute("aria-pressed"), "false");
	log.info("UnderLine Button is Successfully getting Disabled");
	switchToFrameBySelector(tinyEditorFrame);
	Assert.assertTrue(this.getFirstVisibleElement(tinyEditorDefaultBody).getText().trim().equalsIgnoreCase(text+text));
	switchToTopFrame();
	typeInTinyEditor(text);
	switchToFrameBySelector(tinyEditorFrame);
	Assert.assertTrue(this.getFirstVisibleElement(tinyEditorDefaultBody+">span[style='text-decoration: underline;']").getText().trim().equalsIgnoreCase(text));
	log.info("UnderLine Functionality is Successfully working");
	
	switchToTopFrame();
	this.clearInTinyEditor();
	this.typeandSelectInTinyEditor(text);
}

	/**
	 * Method to verify Permanent Pen functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyPermanentPenInTinyEditor(String text) {
		this.clearInTinyEditor();
		switchToTopFrame();
		this.fluentWaitElementVisible(tinyEditorPermanentPenButton);
		this.getFirstVisibleElement(tinyEditorPermanentPenButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		this.getFirstVisibleElement(tinyEditorBody).type(text);
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorPermanentPenBody).getAttribute("style").contains("color: rgb(231, 76, 60)"));
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorPermanentPenBody).getText().trim().equals(text));
		this.getFirstVisibleElement(tinyEditorBody).clear();
		switchToTopFrame();
		this.getFirstVisibleElement(tinyEditorPermanentPenButton).click();
		typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertTrue(driver.isElementPresent(tinyEditorBody));
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBody).getText().trim().equals(text));
		switchToTopFrame();
	}

	/**
	 * Method to select all text present in Tiny Editor
	 */
	public void selectAllFromTinyEditor() {
		log.info("Select all from  Tiny Editor");
		switchToFrameBySelector(tinyEditorFrame);
		WebDriver wd = (WebDriver) driver.getBackingObject();
		Actions actionObj = new Actions(wd);
		actionObj.keyDown(Keys.CONTROL)
		         .sendKeys("a")
		         .keyUp(Keys.CONTROL)
		         .perform();
		switchToTopFrame();
	}

	public void moveToNextLine() {
		Actions action = new Actions((WebDriver) driver.getBackingObject());
		action.sendKeys(Keys.ENTER).build().perform();
	}

	/**
	 * Method to verify Alignment functionality of Tiny Editor
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyAlignmentInTinyEditor(String text) {
		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorAlignButton).click();
		this.getFirstVisibleElement(tinyEditorLeftButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getAttribute("style").toString()
				.toLowerCase().split(":")[1].trim(), "left;");
		log.info("Successfully Verified Left Alignment in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorAlignButton).click();
		this.getFirstVisibleElement(tinyEditorCenterButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getAttribute("style").toString()
				.toLowerCase().split(":")[1].trim(), "center;");
		log.info("Successfully Verified Center Alignment in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorAlignButton).click();
		this.getFirstVisibleElement(tinyEditorRightButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getAttribute("style").toString()
				.toLowerCase().split(":")[1].trim(), "right;");
		log.info("Successfully Verified Right Alignment in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorAlignButton).click();
		this.getFirstVisibleElement(tinyEditorJustifyButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getAttribute("style").toString()
				.toLowerCase().split(":")[1].trim(), "justify;");
		log.info("Successfully Verified Justify Alignment in Tiny Editor");
		switchToTopFrame();
	}

	/**
	 * Method to verify Indents functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyIndentsInTinyEditor(String text) {
		this.clearInTinyEditor();
		this.fluentWaitElementVisible(tinyEditorIndentIncButton);
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorIndentIncButton).click();
		this.getFirstVisibleElement(tinyEditorIndentIncButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		String style[] = this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getAttribute("style").toString()
				.toLowerCase().split(";");
		for (String str : style) {
			if (str.contains("padding-left")) {
				Assert.assertEquals("80px", str.split(":")[1].trim());
				log.info("Successfully Verified Left Indentation in Tiny Editor");
			}
		}
		switchToTopFrame();

		this.getFirstVisibleElement(tinyEditorIndentDecButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		String style1[] = this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getAttribute("style").toString()
				.toLowerCase().split(";");
		for (String str : style1) {
			if (str.contains("padding-left")) {
				Assert.assertEquals("40px", str.split(":")[1].trim());
				log.info("Successfully Verified Right Indentation in Tiny Editor");
			}
		}
		switchToTopFrame();

	}

	/**
	 * Method to verify HorizontalLine functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyHorizontalLineInTinyEditor(String text) {
		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.clickLinkWithJavascript(tinyEditorHorizontalLineButton);
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(1, driver.getVisibleElements(tinyEditorBodyDOM + ">hr").size());
		log.info("Successfully Verified Horizontal Line Functionality in Tiny Editor");
		switchToTopFrame();
	}

	/**
	 * Method to verify Special symbol(Characters and Emotions) functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifySpecialSymbolsInTinyEditor(String text) {
		String special;
		Random random = new Random();

		this.clearInTinyEditor();

		if (text.equalsIgnoreCase("SpecialChar")) {
			this.getFirstVisibleElement(tinyEditorInsertButton).click();
			this.clickLinkWithJavascript(tinyEditorSpecialCharacterButton);
		} else if (text.equalsIgnoreCase("Emotions")) {
			this.getFirstVisibleElement(tinyEditorEmoticonsButton).click();
		}

		fluentWaitElementVisible(tinyEditorSpecialCharacterAllLink);

		WebDriver wd = (WebDriver) driver.getBackingObject();
		List<WebElement> characters = wd.findElements(By.xpath(tinyEditorSpecialCharacterCharacters));
		WebElement e1 = characters.get(random.nextInt(characters.size()));
		scrolltoViewElement(e1, wd);
		special = e1.getText();
		e1.click();

		switchToTopFrame();

		if (text.equalsIgnoreCase("SpecialChar")) {
			this.getFirstVisibleElement(tinyEditorInsertButton).click();
			this.clickLinkWithJavascript(tinyEditorSpecialCharacterButton);
		} else if (text.equalsIgnoreCase("Emotions")) {
			this.getFirstVisibleElement(tinyEditorEmoticonsButton).click();
		}

		fluentWaitElementVisible(tinyEditorSpecialCharacterAllLink);

		characters = wd.findElements(By.xpath(tinyEditorSpecialCharacterCharacters));
		WebElement e2 = characters.get(random.nextInt(characters.size()));
		scrolltoViewElement(e2, wd);
		special = special + e2.getText();
		e2.click();

		switchToFrameBySelector(tinyEditorFrame);

		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorDefaultBody).getText().trim().equals(special));

		log.info("Successfully Verified Horizontal Line Functionality in Tiny Editor");

		switchToTopFrame();
	}

	/**
	 * Method to verify Spell Check Functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifySpellCheckInTinyEditor(String text)
	{
		String incorrectWord="htis";
		String corrrectExpectedWord="this";
		this.clearInTinyEditor();
		log.info("Value of Wrong Spell - " + incorrectWord);
		this.typeWithSpaceInTinyEditor(incorrectWord);
		String corrrectActualWord = getTextFromTinyEditor().trim();
		Assert.assertEquals(corrrectActualWord, corrrectExpectedWord);
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorSettingsMenuButton).click();
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_RIGHT);
		driver.typeNative(Keys.ENTER);
		this.typeWithSpaceInTinyEditor(incorrectWord);
		String actualWord = getTextFromTinyEditor().trim();
		Assert.assertEquals(actualWord, incorrectWord);
		switchToTopFrame();
		this.getFirstVisibleElement(tinyEditorSettingsMenuButton).click();
		for(int i=1;i<5;i++)
		{
			driver.typeNative(Keys.ARROW_DOWN);
		}
		driver.typeNative(Keys.ENTER);
		
		driver.isElementPresent(tinyEditorHelpWindow);
		this.getFirstVisibleElement(tinyEditorHelpWindowCloseButton).click();
		switchToTopFrame();
		log.info("Successfully Verified Spell Check Functionality in Tiny Editor");
	}
	
	
	/**
	 * Method to verify Rows and Columns , images , text and nested table in Table  of Tiny Editor
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyRowsCoulmnOfTableInTinyEditor(String text)
	{
		int rc=3;
		String width="100";
		String height="150";
		String link = "https://image.shutterstock.com/image-vector/see-think-do-different-business-600w-1577732878.jpg";
		String alternateDesc = "verifyLinkImageInTinyEditor";
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(tinyEditorTableButton).doubleClick();
		selectRowsColumnToCreateTable();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(driver.getElements(numberOfRowsInTableOfTinyEditor).size(),rc);
		Assert.assertEquals(driver.getElements(numberOfColumnsInTableOfTinyEditor).size(),rc);
		rc++;
		driver.getFirstElement(firstCellInTableOfTinyEditor).doubleClick();
		insertRowInTable();
		insertCoulmnInTable();
		Assert.assertEquals(driver.getElements(numberOfRowsInTableOfTinyEditor).size(),rc);
		Assert.assertEquals(driver.getElements(numberOfColumnsInTableOfTinyEditor).size(),rc);
		switchToTopFrame();
		this.getFirstVisibleElement(tinyEditorBulletButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		this.getFirstVisibleElement(firstCellInTableOfTinyEditor).type(text);
		Assert.assertTrue(driver.getFirstElement(firstBulletCellInTableOfTinyEditor).getText().trim().equals(text));
		Assert.assertTrue(driver.isElementPresent(firstBulletCellInTableOfTinyEditor));
		switchToTopFrame();
		this.getFirstVisibleElement(tinyEditorBulletButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		driver.typeNative(Keys.TAB);	
		switchToTopFrame();
		
		if (text.contains("Browse")) {

			this.getFirstVisibleElement(tinyEditorInsertButton).click();
			clickLinkWait(tinyEditorImageLink);
			this.getFirstVisibleElement(Browse_Button).click();
			if (!text.contains("Forum")) {
				try {
					uploadImageFromWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}

				fluentWaitElementVisible(TinyEditorUploadButton);
				this.clickLinkWithJavascript(TinyEditorUploadButton);
				switchToFrameBySelector(tinyEditorFrame);
				Assert.assertTrue(driver.isElementPresent(secondCellInTableOfTinyEditor + " img"));
				if(picturecount==1)
					Assert.assertTrue(this.getFirstVisibleElement(secondCellInTableOfTinyEditor + " img")
							.getAttribute("src").contains("Desert.jpg"));
				else
					Assert.assertTrue(this.getFirstVisibleElement(secondCellInTableOfTinyEditor + " img")
							.getAttribute("src").contains("Lighthouse.jpg"));
				
				switchToTopFrame();
			} else {
				try {
					uploadImageFromWindowForums();
				} catch (Exception e) {
					e.printStackTrace();
				}

				fluentWaitElementVisible(TinyEditorUploadButtonForums);
				this.getFirstVisibleElement(TinyEditorUploadButtonForums).click();
				switchToFrameBySelector(tinyEditorFrame);
				Assert.assertTrue(driver.isElementPresent(tinyEditorBodyDOM + " img"));
				switchToTopFrame();
			}
		} 
		else 	
		{
			this.getFirstVisibleElement(tinyEditorInsertButton).click();
			this.getFirstVisibleElement(tinyEditorImageLink).click();
			this.getFirstVisibleElement(tinyEditorSourceTextBox).type(link);
			this.getFirstVisibleElement(tinyEditorAlternateDescTextBox).type(alternateDesc);
			this.getFirstVisibleElement(tinyEditorHeightTextBox).clear();
			this.getFirstVisibleElement(tinyEditorHeightTextBox).type(height);
			this.getFirstVisibleElement(tinyEditorWidthTextBox).clear();
			this.getFirstVisibleElement(tinyEditorWidthTextBox).type(width);
			this.getFirstVisibleElement(tinyEditorLinkSaveButton).click();
			switchToFrameBySelector(tinyEditorFrame);
			Assert.assertEquals(link,
					driver.getFirstElement(secondCellInTableOfTinyEditor + ">img").getAttribute("src").trim());
			Assert.assertEquals(width,
					driver.getFirstElement(secondCellInTableOfTinyEditor + ">img").getAttribute("width").trim());
			Assert.assertEquals(height,
					driver.getFirstElement(secondCellInTableOfTinyEditor + ">img").getAttribute("height").trim());
			Assert.assertEquals(alternateDesc,
					driver.getFirstElement(secondCellInTableOfTinyEditor + ">img").getAttribute("alt").trim());
			log.info("Successfully Verified Link Image Functionality in Tiny Editor");
			switchToTopFrame();
		}
	
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(tinyEditorTableButton).doubleClick();
		selectRowsColumnToCreateTable();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(driver.getElements(numberOfRowsInNestedTableOfTinyEditor).size(),rc-1);
		Assert.assertEquals(driver.getElements(numberOfColumnsInNestedTableOfTinyEditor).size(),rc-1);	
		switchToTopFrame();
	}
	
	/**  //automation not working so will pick after other tasks
	 * Method to verify Format Painter of Tiny Editor
	 */
	public void verifyFindReplaceInTinyEditor(String text)
	{
		String value1 ="it ";
		String value2= "this ";
		this.clearInTinyEditor();
		//this.clickOnMoreLink();
		this.typeInTinyEditor(text);
		this.fluentWaitElementVisible(tinyEditorSearchButton);
		this.getFirstVisibleElement(tinyEditorSearchButton).click();
		this.getFirstVisibleElement(tinyEditorFindTextBox).type(value1);
		this.getFirstVisibleElement(tinyEditorFindButton).click();
		driver.isElementPresent(tinyEditorNotFindAlertMessage);
		this.getFirstVisibleElement(tinyEditorNotFindAlertMessageOkButton).click();
		this.getFirstVisibleElement(tinyEditorFindTextBox).clear();
		this.getFirstVisibleElement(tinyEditorFindTextBox).type(value2);
		this.getFirstVisibleElement(tinyEditorReplaceWithTextBox).type(value1);
		this.getFirstVisibleElement(tinyEditorFindButton).click();
		this.getFirstVisibleElement(tinyEditorReplaceButton).click();
		this.getFirstVisibleElement(tinyEditorFindReplaceCloseWindowButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		text=text.replace(value2, value1).trim();
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBody).getText().trim().equals(text));
		switchToTopFrame();
		
	}
	
	public void clickOnMoreLink()
	{
		this.getFirstVisibleElement(moreButton).click();
	}
	
	/**  //automation not working so will pick after other tasks
	 * Method to verify Format Painter of Tiny Editor
	 */
	public void verifyFormatPainterInTinyEditor(String text)
	{
		text="this";
		this.clearInTinyEditor();
		typeTwoLinesInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		selectFirstLineFromTinyEditorBody();
		switchToTopFrame();
		this.getFirstVisibleElement(tinyEditorBoldButton).click();
		this.getFirstVisibleElement(tinyEditorItalicButton).click();
		this.getFirstVisibleElement(tinyEditorUnderlineButton).click();
		this.getFirstVisibleElement(tinyEditorFormatPainterButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		this.getFirstVisibleElement(tinyEditorBodyDOM+">p:nth-of-type(2)").doubleClick();	
		switchToTopFrame();
	}
	
	/**
	 * Method to add Rows and Columns from table of Tiny Editor
	 */
	public void selectRowsColumnToCreateTable()
	{
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_RIGHT);
		driver.typeNative(Keys.ARROW_RIGHT);
		driver.typeNative(Keys.ENTER);
		
	}
	
	public void insertRowInTable()
	{
		Actions action = new Actions((WebDriver) driver.getBackingObject());
		action.contextClick().build().perform();
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_RIGHT);
		driver.typeNative(Keys.ENTER);	
	}
	
	public void insertCoulmnInTable()
	{
		Actions action = new Actions((WebDriver) driver.getBackingObject());
		action.contextClick().build().perform();
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_RIGHT);
		driver.typeNative(Keys.ENTER);	
	}
	
	public void selectFirstLineFromTinyEditorBody()
	{
		Actions action = new Actions((WebDriver) driver.getBackingObject());
		action.keyDown(Keys.CONTROL).sendKeys(Keys.HOME).keyUp(Keys.CONTROL).keyDown(Keys.SHIFT)
		.sendKeys(Keys.END).keyUp(Keys.SHIFT).build().perform();		
	}
	
	public void selectSecondtLineFromTinyEditorBody()
	{
		Actions action = new Actions((WebDriver) driver.getBackingObject());
		action.keyDown(Keys.CONTROL).sendKeys(Keys.HOME).keyUp(Keys.CONTROL).sendKeys(Keys.ARROW_DOWN).keyDown(Keys.SHIFT)
		.sendKeys(Keys.END).keyUp(Keys.SHIFT).build().perform();	
	}
	
	/**
	 * Method to navigate to TE frame, type text and move back to top frame
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void typeInTinyEditor(String text) {
		log.info("Swith to Tiny Editor, type text and move to Top frame");
		switchToFrameBySelector(tinyEditorFrame);
		Element e1 = this.getFirstVisibleElement(tinyEditorBody);
		e1.type(text);
		switchToTopFrame();
	}
	
	public void typeWithSpaceInTinyEditor(String text) {
		log.info("Swith to Tiny Editor, type text and move to Top frame");
		switchToFrameBySelector(tinyEditorFrame);
		Element e1 = this.getFirstVisibleElement(tinyEditorBody);
		e1.type(text);
		driver.typeNative(Keys.SPACE);
		switchToTopFrame();
	}

	/**
	 * Method to navigate to TE frame, type 2 lines and move back to top frame
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void typeTwoLinesInTinyEditor(String text) {
		log.info("Swith to Tiny Editor, type two line and move to Top frame");
		switchToFrameBySelector(tinyEditorFrame);
		Element e1 = this.getFirstVisibleElement(tinyEditorBody);
		e1.type(text);
		moveToNextLine();
		e1.type(text);
		switchToTopFrame();
	}

	/**
	 * Method to navigate to TE frame, clear text and move back to top frame
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void clearInTinyEditor() {
		log.info("Swith to Tiny Editor, clear text and move to Top frame");
		this.fluentWaitPresent(tinyEditorFrame);
		switchToFrameBySelector(tinyEditorFrame);
		Element e1 = this.getFirstVisibleElement(tinyEditorBody);
		e1.clear();
		switchToTopFrame();
	}
	
	public String getTextFromTinyEditor()
	{
		switchToFrameBySelector(tinyEditorFrame);
		String text =  	this.getFirstVisibleElement(tinyEditorBodyDOM).getText();
		switchToTopFrame();
		return text;
	}
	
	public static String getTinyEditorHeader(int i){
		return "xpath=//div/h"+i+"[text()='Heading "+i+"']";
	}

	
	/**
	 * Method to verify font functionality in tiny editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyFontInTinyEditor(String text) {
		log.info("Entering text in Tiny Editor and then chnging its font");
		this.clearInTinyEditor();
		this.typeandSelectInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorFontsButton).click();
		driver.typeNative(Keys.ARROW_RIGHT);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ENTER);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		/*Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getAttribute("style").toString()
				.split(":")[1].trim(), "'Comic Sans MS';");*/
		 Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getAttribute("style").toString(),
				 "font-family: arial, helvetica, sans-serif;");
		log.info("Successfully Verified Font in Tiny Editor for selected text");
		switchToTopFrame();

		log.info("Selecting font from Tiny Editor menu for selected text in Tiny Editor");
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorFontsButton).click();
		driver.typeNative(Keys.ARROW_RIGHT);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ENTER);
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span>span").getAttribute("style")
			//	.toString().split(":")[1].trim(), "Courier;");font-family: "book antiqua", palatino, serif;
				.toString().split(":")[1].trim(), "\"book antiqua\", palatino, serif;");
		log.info("Successfully verified selecting font from Tiny Editor menu and Entering text in Tiny Editor");
		switchToTopFrame();
	}
	
	/**
	 * Method to verify 'Insert/Edit Image : Link to Existing Image' Functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyLinkImageInTinyEditor(String text)
	{
		String link = "https://image.shutterstock.com/image-vector/see-think-do-different-business-600w-1577732878.jpg";
		this.clearInTinyEditor();
		
		if(text.contains("url"))
		{
			this.getFirstVisibleElement(tinyEditorInsertButton).click();
			this.getFirstVisibleElement(tinyEditorImageLink).click();
			if(!text.contains("Forum")){
				this.getFirstVisibleElement(TinyEditorWebURLLink).click();
			}else{
				this.getFirstVisibleElement(TinyEditorWebURLLable).click();
			}
			this.getFirstVisibleElement(TinyEditorWebImageURL).type(link);
			this.getFirstVisibleElement(TinyEditorInsertImageButton).click();
		}
		else
		{	
			this.getFirstVisibleElement(tinyEditorInsertButton).click();
			this.getFirstVisibleElement(tinyEditorImageLink).click();
			this.getFirstVisibleElement(tinyEditorImageSource).type(link);
			this.getFirstVisibleElement(tinyEditorLinkSaveButton).click();
		}

		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertTrue(driver.getFirstElement(tinyEditorBodyDOM+" img").getAttribute("src").contains(link)); 

		log.info("Successfully Verified Insert Image Functionality in Tiny Editor");
		switchToTopFrame();
		
	}
	
	
	
	/** 
	 * Method to verify right and left in Paragraph of Tiny Editor 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyRightLeftParagraphInTinyEditor(String text)
	{
		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		fluentWaitElementVisible(rightToLeftButton);
		this.getFirstVisibleElement(rightToLeftButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertTrue(driver.isElementPresent(tinyEditorParagraphFromRight));
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorParagraphFromRight).getText().trim().equalsIgnoreCase(text));
		switchToTopFrame();
		this.getFirstVisibleElement(leftToRightButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertTrue(driver.isElementPresent(tinyEditorParagraphFromLeft));
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorParagraphFromLeft).getText().trim().equalsIgnoreCase(text));
		switchToTopFrame();
	}

	
	/**
	 * Method to verify font size functionality in tiny editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyFontSizeInTinyEditor(String text) {
		log.info("Entering text in Tiny Editor and then chnging its font size");
		this.clearInTinyEditor();
		this.typeandSelectInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorFontSizesButton).click();
		driver.typeNative(Keys.ARROW_RIGHT);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ENTER);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getAttribute("style").toString()
				.split(":")[1].trim(), "12pt;");
		log.info("Successfully Verified Font size in Tiny Editor for selected text");
		switchToTopFrame();

		log.info("Changing font size from Tiny Editor menu for the selected text");
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorFontSizesButton).click();
		driver.typeNative(Keys.ARROW_RIGHT);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ENTER);
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span>span").getAttribute("style")
				.toString().split(":")[1].trim(), "18pt;");
		log.info("Successfully verified Changing font size from Tiny Editor menu for the selected text");
		switchToTopFrame();
	}
	
	
	/**
	 * Method to verify Othertext Attributes and Full screen functionality in tiny editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyOtherTextAttributesAndFullScreenInTinyEditor(String text) {
		
		//Verifying superscript functionality
		log.info("Verifying superscipt functionality in tiny editor");
		this.clearInTinyEditor();
		this.typeandSelectInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorSuperScriptButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>sup").getText(), text);
		log.info("Successfully Verified SuperScript in Tiny Editor for selected text");
		switchToTopFrame();

		log.info("Verifying selecting superscipt menu and then verifying text in superscript in tiny editor");
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.clickLinkWithJavascript(tinyEditorSuperScriptButton);
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p sup").getText(), text);
		log.info("Successfully verified selecting superscipt menu and then verified text in superscript in tiny editor");
		switchToTopFrame();

		//Below Validation Added as part of Extended development for superscipt functionality
		log.info("Verifying superscipt functionality in tiny editor");
		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorSuperScriptButton).click();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorSuperScriptButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
			(text+text).toLowerCase());
		switchToTopFrame();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>sup").getText(), text);
		log.info("Super Script option is Successfully getting Enabled and Disabled");
		log.info("Successfully Verified SuperScript in Tiny Editor for selected text");
		switchToTopFrame();
	
		//Verifying subcript functionality
		log.info("Verifying subscipt functionality in tiny editor");
		this.clearInTinyEditor();
		this.typeandSelectInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorSubScriptButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p sub").getText(), text);
		log.info("Successfully Verified Subscript in Tiny Editor for selected text");
		switchToTopFrame();

		log.info("Verifying selecting subscript menu and then verifying text in subscript in tiny editor");
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorSubScriptButton).click();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span>sub").getText(), text);
		log.info("Successfully verified selecting subscript menu and then verified text in subscript in tiny editor");
		switchToTopFrame();
		
		//Below Validation Added as part of Extended development for SubScript functionality
		log.info("Verifying SubScript functionality in tiny editor");
		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorSubScriptButton).click();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorSubScriptButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
			(text+text).toLowerCase());
		switchToTopFrame();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>sub").getText(), text);
		log.info("Sub Script option is Successfully getting Enabled and Disabled");
		log.info("Successfully Verified SubScript in Tiny Editor for selected text");
		switchToTopFrame();
	
		//Verifying Strikethrough functionality
		log.info("Verifying Strikethrough functionality in tiny editor");
		this.clearInTinyEditor();
		this.typeandSelectInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorStrikeThroughButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		/*Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getAttribute("style")
				.toString().split(":")[1].trim(), "line-through;");*/
		waitForSameTime();
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>s").getText(), text);
		log.info("Successfully Verified Strikethrough in Tiny Editor for selected text");
		switchToTopFrame();
		
		log.info("Verifying selecting Strikethrough menu and then verifying text in superscript in tiny editor");
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorStrikeThroughButton).click();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
	/*	Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span>span").getAttribute("style")
				.toString().split(":")[1].trim(), "line-through;");*/
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span>s").getText(),text);
		log.info("Successfully verified selecting Strikethrough menu and then verified text in superscript in tiny editor");
		switchToTopFrame();
		
		//Below Validation Added as part of Extended development for Strikethrough functionality
		log.info("Verifying Strikethrough functionality in tiny editor");
		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorStrikeThroughButton).click();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorStrikeThroughButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
			(text+text).toLowerCase());
		switchToTopFrame();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
	//	Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span[style='text-decoration: line-through;'").getText(), text);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getText(), text);
		log.info("Strikethrough option is Successfully getting Enabled and Disabled");
		log.info("Successfully Verified Strikethrough in Tiny Editor for selected text");
		switchToTopFrame();
				
		if(!text.contains("highlights"))
		{
		log.info("Verifying Full Screen functionality in tiny editor");
		
		this.getFirstVisibleElement(tinyEditorFullScreenButton).click();
				
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorFullScreenWindow).isDisplayed());
		
		this.getFirstVisibleElement(tinyEditorFullScreenButton).click();
		
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorRestoreScreenWindow).isDisplayed());
		
		log.info("Successfully verified Full Screen functionality in tiny editor");
		}
		
	}

	/**
	 * Method to verify 'Insert Link' Functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyInsertLinkImageInTinyEditor(String text)
	{
		String link = "https://image.shutterstock.com/image-vector/see-think-do-different-business-600w-1577732878.jpg";
		String wrongLink="www.hcl.com";
		String title = "verifyLinkImageInTinyEditor";
		
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(tinyEditorInsertLinkButton).click();
		this.getFirstVisibleElement(tinyEditorLinkURL).type(wrongLink);
		this.fluentWaitPresent(tinyEditorLinkSaveButton);
		this.getFirstVisibleElement(tinyEditorLinkSaveButton).click();
		String Errormsg=this.getFirstVisibleElement(tinyEditorWrongLinkErrorDialog).getText().trim();
		Assert.assertTrue(Errormsg.contains("add the required https:// prefix?"),Errormsg + "Message Not Given");
		this.getFirstVisibleElement(tinyEditorWrongLinkErrorButton).click();
		log.info("Successfully Verified Error Dailog is displayed while adding Link without HTTP in Tiny Editor");
		switchToTopFrame();
		
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(tinyEditorInsertLinkButton).click();
		this.getFirstVisibleElement(tinyEditorLinkURL).type(link);
		this.getFirstVisibleElement(tinyEditorLinkSaveButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(link, driver.getFirstElement(tinyEditorBodyDOM+">p>a").getText().trim()); 
		log.info("Successfully Verified By Default URL of Link is Displayed in Tiny Editor");
		switchToTopFrame();
		
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(tinyEditorInsertLinkButton).click();
		this.getFirstVisibleElement(tinyEditorLinkURL).type(link);
		this.getFirstVisibleElement(tinyEditorLinkTitle).type(title);
		this.getFirstVisibleElement(tinyEditorLinkText).clear();
		this.getFirstVisibleElement(tinyEditorLinkText).type("CurrentWindow_"+text);
		
		this.getFirstVisibleElement("css=button[title='Open link in...']").click();
		this.getFirstVisibleElement("css=div[title='Current window']").click();
		
		this.getFirstVisibleElement(tinyEditorLinkSaveButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals("CurrentWindow_"+text, driver.getFirstElement(tinyEditorBodyDOM+">p>a").getText().trim()); 
		log.info("Successfully Verified By Default URL of Link is Displayed in Tiny Editor");
		switchToTopFrame();
		
		moveToNextLine();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(tinyEditorInsertLinkButton).click();
		this.getFirstVisibleElement(tinyEditorLinkURL).type(link);
		this.getFirstVisibleElement(tinyEditorLinkTitle).type(title);
		this.getFirstVisibleElement(tinyEditorLinkText).clear();
		this.getFirstVisibleElement(tinyEditorLinkText).type("NewWindow_"+text);
		
		this.getFirstVisibleElement("css=button[title='Open link in...']").click();
		this.getFirstVisibleElement("css=div[title='New window']").click();
		
		this.getFirstVisibleElement(tinyEditorLinkSaveButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals("NewWindow_"+text, driver.getFirstElement("//body[@id='tinymce']/p[2]/a").getText().trim()); 
		Assert.assertEquals(title, driver.getFirstElement("//body[@id='tinymce']/p[2]/a").getAttribute("title").trim());
		log.info("Successfully Verified By Default URL of Link is Displayed in Tiny Editor");
		switchToTopFrame();
	}
	/**
	 * Method to verify Inserted Link inside Description after creating Community.
	 * Check the Link is Inserted with Current Window and New Window Opening Functionality
	 * 
	 */
	public void verifyInsertedLinkinCommunityDescription(boolean isRichContent)
	{
		if(isRichContent)
		{
			tinyEditorDOMPara = tinyEditorRichContentDOMPara;
		}	
		
		List<Element> elem = driver.getVisibleElements(tinyEditorDOMPara);
		WebDriver wdriver = (WebDriver) driver.getBackingObject();
		String whandleold = wdriver.getWindowHandle();
		log.info("Window Handle1 - " + whandleold);
		
		for(int i=1;i<=elem.size();i++)
		{
			Element e = this.getFirstVisibleElement(tinyEditorDOMPara + "["+i+"]/a");
			log.info("Value of Para/a - " + e.getText());
			waitForPageLoaded(driver);
			fluentWaitPresent(tinyEditorDOMPara + "["+i+"]/a");
			clickLinkWithJavascript(tinyEditorDOMPara + "["+i+"]/a");
			sleep(2000);
			Set<String> whandleset = wdriver.getWindowHandles();
			log.info("No of Windows - " + whandleset.size());
			if(whandleset.size()>1)
			{
				for(String s:whandleset)
				{
					log.info("Window After Click - " + s);
					if(!(s.equals(whandleold)))
					{
						wdriver.switchTo().window(s);
						fluentWaitPresent("css=body>img");
						log.info("different window");
						Assert.assertEquals(1, driver.getVisibleElements("css=body>img").size());
						Assert.assertEquals("https://image.shutterstock.com/image-vector/see-think-do-different-business-600w-1577732878.jpg", this.getFirstVisibleElement("css=body>img").getAttribute("src").trim());
						wdriver.close();
					}
				}
				wdriver.switchTo().window(whandleold);
			}
			else
			{
				for(String s:whandleset)
				{
					waitForPageLoaded(driver);
					log.info("Window After Click - " + s);
					if(s.equals(whandleold))
					{
						log.info("same window");
						fluentWaitPresent("css=body>img");
						Assert.assertEquals(1, driver.getVisibleElements("css=body>img").size());
						Assert.assertEquals("https://image.shutterstock.com/image-vector/see-think-do-different-business-600w-1577732878.jpg", this.getFirstVisibleElement("css=body>img").getAttribute("src").trim());
					}
					wdriver.navigate().back();
				}
			}
		}	
	}
	

	/**
	 * Method to verify Text Color Functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyTextColorInTinyEditor(String text) 
	{
		this.clearInTinyEditor();
		this.typeandSelectInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorTextColorLink).click();
		this.getFirstVisibleElement(tinyEditorFontColor).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getAttribute("style").contains("color: rgb(252, 29, 0)"));
		log.info("Successfully Verified Text Color Functionality in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorTextColorLink).click();
		this.getFirstVisibleElement(tinyEditorFontColor).click();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span>span").getAttribute("style").contains("color: rgb(252, 29, 0)"));
		log.info("Successfully Verified Text Color Functionality in Tiny Editor");
		switchToTopFrame();
	}
	
	/**
	 * Method to verify BackGround Color Functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyBackGroundColorInTinyEditor(String text) 
	{
		this.clearInTinyEditor();
		this.typeandSelectInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorBackgroundColorLink).click();
		this.getFirstVisibleElement(tinyEditorFontColor).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getAttribute("style").contains("background-color: rgb(252, 29, 0)"));
		log.info("Successfully Verified BackGround Color Functionality in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorFontMenuButton).click();
		this.getFirstVisibleElement(tinyEditorBackgroundColorLink).click();
		this.getFirstVisibleElement(tinyEditorFontColor).click();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span").getText().toLowerCase(),
				text.toLowerCase());
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>span>span").getAttribute("style").contains("background-color: rgb(252, 29, 0)"));
		log.info("Successfully Verified BackGround Color Functionality in Tiny Editor");
		switchToTopFrame();
	}
	
	/**
	 * Method to verify Word Count Functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyWordCountInTinyEditor(String text)
	{
		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		this.getFirstVisibleElement(tinyEditorSettingsMenuButton).click();
		this.getFirstVisibleElement(tinyEditorSettingWordCountlink).click();
		int size = text.trim().split(" ").length;
		log.info(this.getFirstVisibleElement(tinyEditorSettingsWordCountValue + "/td[2]").getText());
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorSettingsWordCountValue + "/td[2]").getText(),size+"");
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorSettingsWordCountValue + "/td[1]").getText(),"Words");
		log.info("Successfully Verified Word Count Functionality in Tiny Editor");
		this.getFirstVisibleElement(tinyEditorSettingsCloseButton).click();
		switchToTopFrame();
	}

	
	/**
	 * Method to verify Upload image from Disk Functionality of Tiny Editor
	 * @throws Exception 
	 */
	public void verifyUploadImageFromDiskInTinyEditor()
	{

		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(tinyEditorImageLink).click();
		try
		{
			uploadImageFromWindow();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		fluentWaitElementVisible(TinyEditorUploadButton);
		this.getFirstVisibleElement(TinyEditorUploadButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertTrue(driver.isElementPresent(tinyEditorBodyDOM+" img"));
		if(picturecount==1)
			Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM+" img").getAttribute("src").contains("Desert.jpg"));
		else
			Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM+" img").getAttribute("src").contains("Lighthouse.jpg"));
		
		switchToTopFrame();
	}
	
	public void uploadImageFromWindow()
	{
		try {
			if(picturecount==0)
				filesUI.fileToUpload(Data.getData().file1,FileInputField);
			else
				filesUI.fileToUpload(Data.getData().file2,FileInputField);	
			picturecount++;
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void uploadImageFromWindowForums()
	{
		try {
			filesUI.fileToUpload(Data.getData().file1,FileInputField3);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Method to verify block quote functionality from Disk Functionality of Tiny Editor
	 * @param text is description type in Tiny Editor body 
	 * @throws Exception 
	 */
	public void verifyBlockQuoteInTinyEditor(String text) {
		
		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		this.selectAllFromTinyEditor();
		this.getFirstVisibleElement(tinyEditorBlockquoteButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">blockquote").getText().toLowerCase(),
				text.toLowerCase());
		log.info("Successfully Verified block quote Color Functionality in Tiny Editor");
		switchToTopFrame();

		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorBlockquoteButton).click();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">blockquote").getText().toLowerCase(),
				text.toLowerCase());
		log.info("Successfully Verified Blockquote Functionality in Tiny Editor");
		switchToTopFrame();
	}
	
	/**
	 * Method to verify Insert Media/Video Functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyInsertMediaInTinyEditor(String text)
	{
		String VideoLink = "https://www.youtube.com/embed/owsfdh4gxyc"; 
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(TinyEditorMediaLink).click();
		this.getFirstVisibleElement(tinyEditorLinkURL).type(VideoLink);
		this.getFirstVisibleElement(TinyEditorMediaSaveButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(VideoLink, this.getFirstVisibleElement(tinyEditorBodyDOM + ">p>a").getAttribute("href").trim());
		log.info("Successfully Verified Insert Media/Video Functionality in Tiny Editor");
		switchToTopFrame();
	}

	public void verifyEditDescription(String text) {
		this.clearInTinyEditor();
		typeInTinyEditor(text);
	}

	public void addDescriptionInrichContent(String text) {
		log.info("Adding content in Rich Text Tiny Editor");
		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		log.info("Added content in Rich Text Tiny Editor");
		switchToTopFrame();
		
	}

	/**
	 * Method to verify Adding Sample Code Functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyCodeSampleIntinyEditor(String text) {
		
		log.info("Verifying insert code Functionality in Tiny Editor");
		String html_code ="<html><body><h1></h1></body></html>";
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(tinyEditorCodeSampleButton).click();
		this.getFirstVisibleElement(tinyEditorSelectLaunguageButton).click();
		Select LangOptions=new Select((WebElement) driver.getFirstElement(tinyEditorSelectLaunguageDropdown).getBackingObject());
		LangOptions.selectByVisibleText("HTML/XML");
		this.getFirstVisibleElement(tinyEditorInsertCodeTextArea).type(html_code);
		this.getFirstVisibleElement(TinyEditorMediaSaveButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">pre[class='language-markup']").getText(),
				html_code);
		log.info("Successfully Verified insert code Functionality in Tiny Editor");
		switchToTopFrame();
		
	}
	
	/**
	 * Method to set image name for identifying it while selecting for link to connection file in community RC TE
	 * @param text is description type in Tiny Editor body
	 * @param image  created by api 
	 */
	public static void setImageName(String desc, String name) {
		if (desc.contains("Community"))
			imageThisCommunity = name;
		else {
			imageFile = name;
		}
	}
	
	/**
	 * Method to add images from files/this community from link to connections files option in Tiny editor
	 * 
	 * @param text can contain either be one of two words
	 * 1. Upload - For uploading from computer
	 * 2. Community - For upload from community files 
	 * 3. else it will select link from connection files for random text
	 */
	public void addLinkToConnectionsFilesInTinyEditor(String text)
 {
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(tinyEditorLinkToconnectionsFiles).click();
		if (text.contains("Community")) {
			this.getFirstVisibleElement(tinyEditorLinkToconnectionsFilesThisCommunity).click();
			for (Element e : driver.getVisibleElements(listOfImagesInInsertLinkToFilesPopup)) {
				if (e.getText().contains(imageThisCommunity)) {
					e.click();
				}
			}
		} else if (text.contains("Upload")) {
			this.getFirstVisibleElement(tinyEditorLinkToconnectionsFilesUpload).click();
			try {
				uploadImageFromWindow();
			} catch (Exception e) {
				e.printStackTrace();
			}

			fluentWaitElementVisible(TinyEditorLinktoConnection_UploadButton);
			this.clickLinkWithJavascript(TinyEditorLinktoConnection_UploadButton);
			switchToFrameBySelector(tinyEditorFrame);
			log.info(tinyEditorBodyDOM+" img");
			Assert.assertTrue(driver.isElementPresent(tinyEditorBodyDOM+" img"));
			if(picturecount==1)
				Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM+" a:first-child").getAttribute("href").contains("Desert.jpg"));
			else
				Assert.assertTrue(this.getFirstVisibleElement(tinyEditorBodyDOM+" a:first-child").getAttribute("href").contains("Lighthouse.jpg"));
			
		}else {
			for (Element e : driver.getVisibleElements(listOfImagesInInsertLinkToFilesPopup)) {
				if (e.getText().contains(imageFile)) {
					e.click();
				}
			}
		}
		
		if(text.contains("Forums")){
			clickLinkWithJavascript(insertLinkOkButtonForums);
		}else if(!text.contains("Upload")){
			clickLinkWithJavascript(insertLinkButtonInInsertLinkToFilesPopup);
		}
		switchToTopFrame();
	}
	
	/**
	 * Method to verify Insert/Edit iFrame Functionality of Tiny Editor
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyInsertiFrameInTinyEditor(String text)
	{
		String videoLink = "https://www.youtube.com/embed/X-mVN_4V6lk"; 
		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(TinyEditorInserIFrameOption).click();
		this.getFirstVisibleElement(TinyEditorURLTextArea).type(videoLink);
		this.getFirstVisibleElement(TinyEditoriFrameSaveButton).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertNotNull(driver.getFirstElement("//iframe[@src='"+ videoLink +"']"));
		log.info("Successfully Verified Insert/Edit iFrame Functionality in Tiny Editor");
		switchToTopFrame();
	}	
	
	/**
	 * Method to verify Insert/Edit iFrame Functionality of Tiny Editor
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyDefaultCaseInTinyEditor(String text)
	{
		this.clearInTinyEditor();
		this.typeInTinyEditor(text);
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertEquals(this.getFirstVisibleElement(tinyEditorBodyDOM + ">p").getText().toLowerCase(),
				text.toLowerCase());
		log.info("Successfully Verified Default Case in Tiny Editor");
		switchToTopFrame();
	}
	
	/**
	 * Method to verify existing Image Functionality of Tiny Editor
	 * @param text is description type in Tiny Editor body
	 */
	public void verifyExistingImagekInTinyEditor()
	{
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		clickLinkWait(tinyEditorImageLink);
		clickLinkWait(tinyEditorExistingImageLink);
		Assert.assertTrue(this.getFirstVisibleElement(tinyEditorExistingImage).getAttribute("title").equals(Data.getData().file1));
		this.getFirstVisibleElement(tinyEditorExistingImage).click();
		this.getFirstVisibleElement(TinyEditorInsertImageButton).click();	
		
	}	
	
	/**
	 * Method to verify Mention User functionality of Tiny Editor
	 * 
	 * @param String text is description type in Tiny Editor body
	 * @param STring username is User Name used to mention
	 */
	public void verifyMentionUserInTinyEditor(String text, String username) 
	{
		String name="@"+username.split(" ")[0];
		this.clearInTinyEditor();
		switchToFrameBySelector(tinyEditorFrame);
		this.getFirstVisibleElement(tinyEditorBody).typeWithDelay(name);
		switchToTopFrame();
		List<Element> userList = driver.getVisibleElements(TinyEditorMentionUserList+"/div[1]/div");
		for(int i=1;i<=userList.size();i++)
		{
			String userName=this.getFirstVisibleElement(TinyEditorMentionUserList+"/div[1]/div["+i+"]").getAttribute("title");
			Assert.assertTrue(("@"+userName).contains(name));
			log.info("Mentioned User List Contains - " + userName);
		}
		log.info("Successfully Verified mentioned user name List Appered in Description of Tiny Editor");
		
		String searchPerson=this.getFirstVisibleElement(TinyEditorMentionUserList+"/div[2]/div[1]").getAttribute("title");
		Assert.assertEquals("Person not listed? Use full search...",searchPerson);
		log.info("Successfully Verified 'Person not listed? Use full search...' Appered in Description of Tiny Editor on mentioning User");
		switchToTopFrame();
	}

	/**
	 * Method to verify Inserted Link inside Description after creating Community.
	 * Check the Link is Inserted with Current Window and New Window Opening Functionality
	 * 
	 */
	public void verifyInsertedLinkinDescription(String name)
	{
		String linktext[] = name.split("~");	
		WebDriver wdriver = (WebDriver) driver.getBackingObject();
		String whandleold = wdriver.getWindowHandle();
		log.info("Window Handle1 - " + whandleold);
		int i=0;
		boolean toDoItemFlag=false;
		String insertedImageLinkXpath="//a[text()='PLACEHOLDER']";
		if(linktext[0].toUpperCase().contains("verifyActivityTinyEditorInsertLinkFunctionalitytoDo".toUpperCase()))
		{
			i=1;
			insertedImageLinkXpath= ActivitiesUIConstants.activityToDoItemDOM.replace("PLACEHOLDER", linktext[0])+"//a[text()='PLACEHOLDER']";
			toDoItemFlag=true;
		}	
		
		for(;i<linktext.length;i++)
		{
			if (name.contains("description") || name.contains("experience"))

			{
				this.clickLinkWithJavascript(ProfilesUIConstants.BackgroundTab);
				fluentWaitElementVisible(ProfilesUIConstants.backGroundDescription);
			}
			String str = insertedImageLinkXpath.replace("PLACEHOLDER", linktext[i]);
			
			Element e = this.getFirstVisibleElement(str);

			log.info("Value of Para/a - " + e.getText());
			
			fluentWaitPresent(insertedImageLinkXpath.replace("PLACEHOLDER", linktext[i]));
			clickLinkWithJavascript(insertedImageLinkXpath.replace("PLACEHOLDER", linktext[i]));
			
			waitForPageLoaded(driver);
			sleep(2000);
			
			Set<String> whandleset = wdriver.getWindowHandles();
			log.info("No of Windows - " + whandleset.size());
			if(whandleset.size()>1)
			{
				for(String s:whandleset)
				{
					log.info("Window After Click - " + s);
					if(!(s.equals(whandleold)))
					{
						wdriver.switchTo().window(s);
						log.info("different window");
						Assert.assertEquals(1, driver.getVisibleElements("css=body>img").size());
						Assert.assertEquals("https://image.shutterstock.com/image-vector/see-think-do-different-business-600w-1577732878.jpg", this.getFirstVisibleElement("css=body>img").getAttribute("src").trim());
						wdriver.close();
					}
				}
				wdriver.switchTo().window(whandleold);
			}
			else
			{
				for(String s:whandleset)
				{
					waitForPageLoaded(driver);
					log.info("Window After Click - " + s);
					if(s.equals(whandleold))
					{
						log.info("same window");
						fluentWaitPresent("css=body>img");
						Assert.assertEquals(1, driver.getVisibleElements("css=body>img").size());
						Assert.assertEquals("https://image.shutterstock.com/image-vector/see-think-do-different-business-600w-1577732878.jpg", this.getFirstVisibleElement("css=body>img").getAttribute("src").trim());
					}
					wdriver.navigate().back();
					if(name.contains("Forum")){
						wdriver.navigate().back();
					}
					if(name.contains("verifyActivityTinyEditorInsertLinkFunctionality") )
					{
						String strg = null;
						if(toDoItemFlag)
						{
							strg= ActivitiesUIConstants.activityToDoItemLink.replace("PLACEHOLDER", linktext[0]);
						}
						else
						{	
							strg = ActivitiesUIConstants.activityToDoItemLink.replace("PLACEHOLDER", linktext[i].split("_")[1].trim());
							this.getFirstVisibleElement(TinyEditorActivityOutlineLink).click();
						}
						
						fluentWaitPresent(strg);
						this.getFirstVisibleElement(strg).click();
					}
					
				}
			}
		}	
	}


	public void verifyUploadImageFromDiskInTinyEditorForums() {

		this.clearInTinyEditor();
		this.getFirstVisibleElement(tinyEditorInsertButton).click();
		this.getFirstVisibleElement(tinyEditorImageLink).click();
		try
		{
			uploadImageFromWindowForums();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		fluentWaitElementVisible(TinyEditorUploadButtonForums);
		this.getFirstVisibleElement(TinyEditorUploadButtonForums).click();
		switchToFrameBySelector(tinyEditorFrame);
		Assert.assertTrue(driver.isElementPresent(tinyEditorBodyDOM+" img"));
		switchToTopFrame();
		
	}

	/**
	 * Method to verify Mention User Name Functionality of Activity Stream in Tiny Editor.
	 */
	public void verifyMentionUserNameinActivityStream(String username)
	{
		String shareBoxText = username;
		String name;
		if(username.contains("Sharebox")){
			name = username.substring(0,username.indexOf("Sharebox")).trim();
		}
		
		this.sleep(2000);
		name="@"+username.substring(0, 5);
		
		fluentWaitPresent(tinyEditorFrame);
		this.switchToFrameBySelector(tinyEditorFrame);
		
		this.getFirstVisibleElement(ForumsUIConstants.forumReplyToCkEditor_body).typeWithDelay(name);
		switchToTopFrame();
		
		if (shareBoxText.contains("Sharebox")) {
			driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		}
		
		List<Element> userList = driver.getVisibleElements(TinyEditorMentionUserList+"/div[1]/div");
		for(int i=1;i<=userList.size();i++)
		{
			String userName=this.getFirstVisibleElement(TinyEditorMentionUserList+"/div[1]/div["+i+"]").getAttribute("title");
			Assert.assertTrue(("@"+userName).contains(name));
			log.info("Mentioned User List Contains - " + userName);
		}
		log.info("Successfully Verified mentioned user name List Appered in Description of Tiny Editor");
		
		String searchPerson=this.getFirstVisibleElement(TinyEditorMentionUserList+"/div[2]/div[1]").getAttribute("title");
		Assert.assertEquals("Person not listed? Use full search...",searchPerson);	
		this.switchToTopFrame();
		
		if(shareBoxText.contains("Sharebox")){
			driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		}
	}

	/**
	 * Method to verify URL Preview and Video Preview Functionality of Activity Stream in Tiny Editor
	 * 
	 * @param value of 'aria-label' Attribute in Locator.
	 */
	public void verifyURL_VideoPreviewinActivyStream(String text)
	{
		String shareBoxText = text;
		String textLocal = text;
		
		if(text.contains("Sharebox")){
			textLocal = text.substring(text.indexOf("What"));
		}
		
		String url="http://www.disney.com";
		String videoLink="https://www.youtube.com/watch?v=tjM7s5wJCUk";
		String Preview = PreviewTitle.replace("PLACEHOLDER", textLocal);
		
		//Check URL
		this.switchToFrameBySelector(tinyEditorFrame);
		this.getFirstVisibleElement(ForumsUIConstants.forumReplyToCkEditor_body).clear();
		this.getFirstVisibleElement(ForumsUIConstants.forumReplyToCkEditor_body).type(url + " ");
		this.switchToTopFrame();
		
		if(shareBoxText.contains("Sharebox")){
			driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		}
		
		fluentWaitPresent(Preview);
		String PreviewURL=this.getFirstVisibleElement(Preview+">a").getAttribute("href").trim();
		Assert.assertTrue(PreviewURL.contains(url));
		
		//Check Video Link
		if(!shareBoxText.contains("Sharebox")){
			
			//Click on post button
			this.getFirstVisibleElement(HomepageUIConstants.PostLink).click();
			
			this.switchToFrameBySelector(tinyEditorFrame);
			this.getFirstVisibleElement(ForumsUIConstants.forumReplyToCkEditor_body).clear();
			this.getFirstVisibleElement(ForumsUIConstants.forumReplyToCkEditor_body).type(videoLink + " ");
			this.switchToTopFrame();
			
			fluentWaitPresent(Preview);
			PreviewURL=this.getFirstVisibleElement(Preview+">a").getAttribute("href").trim();
			Assert.assertTrue(PreviewURL.contains(videoLink));
			this.getFirstVisibleElement(HomepageUIConstants.PostLink).click();
		}
			
	}
	
	/**
	 * Method to verify Spell Check Functionality of Activity Stream in Tiny Editor
	 */
	public void verifySpellCheckinActivityStream()
	{
		String word="htis";
		
		this.switchToFrameBySelector(tinyEditorFrame);
		this.getFirstVisibleElement(ForumsUIConstants.forumReplyToCkEditor_body).clear();
		this.getFirstVisibleElement(ForumsUIConstants.forumReplyToCkEditor_body).typeWithDelay(word+ " ");
		//Assert.assertTrue(this.getFirstVisibleElement(ForumsUI.forumReplyToCkEditor_body+">p>span").getAttribute("aria-invalid").equalsIgnoreCase("spelling"));
		
		Assert.assertTrue(this.getFirstVisibleElement(ForumsUIConstants.forumReplyToCkEditor_body+">p").getText().contains("this"));
		this.getFirstVisibleElement(ForumsUIConstants.forumReplyToCkEditor_body).clear();
		this.switchToTopFrame();
	}

	public void verifyInsertedLinkinHighlightsDescription(BaseHighlights highlights, String communityName) {
		WebDriver wdriver = (WebDriver) driver.getBackingObject();
		String whandleold = wdriver.getWindowHandle();

		this.getFirstVisibleElement(IcecUI.richContentWidgetText.replace("PLACEHOLDER", communityName) + "//p[1]").click();

		Set<String> whandleset = wdriver.getWindowHandles();

		if (whandleset.size()==1) {
			waitForPageLoaded(driver);

			log.info("same window");
			fluentWaitPresent("css=body>img");
			Assert.assertEquals(1, driver.getVisibleElements("css=body>img").size());
			Assert.assertEquals("https://image.shutterstock.com/image-vector/see-think-do-different-business-600w-1577732878.jpg",
					this.getFirstVisibleElement("css=body>img").getAttribute("src").trim());
		}
		wdriver.navigate().back();

		waitForPageLoaded(driver);

		whandleold = wdriver.getWindowHandle();

		this.getFirstVisibleElement(IcecUI.richContentWidgetText.replace("PLACEHOLDER", communityName) + "//p[2]").click();

		whandleset = wdriver.getWindowHandles();
		
		if(whandleset.size()>1)
		{
			for(String s:whandleset)
			{
				log.info("Window After Click - " + s);
				if(!(s.equals(whandleold)))
				{
					wdriver.switchTo().window(s);
					fluentWaitPresent("css=body>img");
					log.info("different window");
					Assert.assertEquals(1, driver.getVisibleElements("css=body>img").size());
					Assert.assertEquals("https://image.shutterstock.com/image-vector/see-think-do-different-business-600w-1577732878.jpg", this.getFirstVisibleElement("css=body>img").getAttribute("src").trim());
					wdriver.close();
				}
			}
			wdriver.switchTo().window(whandleold);
		}
	}

	/**
	 * Method to chick on Download Image for the Image Uploaded via Tiny Editor
	 * 
	 * @param value of Locator (link) to be clicked to download Image.
	 */
	public void ImageDownload(String desc)
	{
		this.clickLinkWithJavascript(desc + " a:nth-of-type(1)");
		this.waitForPageLoaded(driver);
		this.fluentWaitPresent(desc);
		this.fluentWaitElementVisible(desc);
	}
	/**
	 * Method to navigate to TE frame, type and select text  and move back to top frame
	 * 
	 * @param text is description type in Tiny Editor body
	 */
	public void typeandSelectInTinyEditor(String text) {
		log.info("Swith to Tiny Editor, type text and move to Top frame");
		switchToFrameBySelector(tinyEditorFrame);
		Element e1 = this.getFirstVisibleElement(tinyEditorBody);
		e1.type(text);
		this.getFirstVisibleElement(tinyEditorBodyDOM+">p").click();
		this.getFirstVisibleElement(tinyEditorBodyDOM+">p").doubleClick();
		switchToTopFrame();
	}
	
	
}
