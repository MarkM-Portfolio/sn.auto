package com.ibm.conn.auto.webui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Forum_Action_Menu;
import com.ibm.conn.auto.webui.cloud.ForumsUICloud;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.conn.auto.webui.onprem.ForumsUIOnPrem;

public abstract class ForumsUI extends HCBaseUI {
	
	public ForumsUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	protected static Logger log = LoggerFactory.getLogger(ForumsUI.class);


	/**
	 * getForumsTopicReply -
	 * @param topic
	 * @return
	 */
	public static String getForumsTopicReply (BaseForumTopic topic){
		return "css=span[class='entry-title forumPostTitle']:contains("+ topic.getTitle() + ")";
	}
	
	/**
	 * selectForumTopic 
	 * @param topic
	 */
	public static String selectForumTopic(BaseForumTopic topic){
		return "//a[@class='bidiAware'][contains(text(),\'" + topic.getTitle() + "\')]";
		
	}
	
	/**
	 * selectComForumTopic -
	 * @param topic
	 * @return
	 */
	public static String selectComForumTopic (BaseForumTopic topic){
		return "css=h4 a:contains(" + topic.getTitle() + ")";
	}
	
	/**
	 * getForumTopicTitle
	 * @param topic
	 * @return
	 */
	public static String getForumTopicTitle(BaseForumTopic topic){	
		return "css=span[class='entry-title forumPostTitle']:contains("+ topic.getTitle() + ")";
	}
	
	/**
	 * getForumLink -
	 * @param forum
	 * @return
	 */
	public static String getForumLink(BaseForum forum){
		return "css=a:contains(Forum: " + forum.getName() + ")";
	}
	
	/**
	 * getTopicSelector -
	 * @param topic
	 * @return
	 */
	public String getTopicSelector(BaseForumTopic topic){
		return "link=" + topic.getTitle();
	}
	
	/**
	 * getTopicContainer - Returns the container element for a Forum Topic
	 * @param topicTitle - Title of the Forum Topic
	 * @return xpath of the container of the Forum Topic
	 */
	public static String getTopicContainer(String topicTitle){
		return "xpath=//span[@class='entry-title forumPostTitle'][text()='" + topicTitle + "']//ancestor::div[@class='hentry lotusPost ']";
	}
	
	/**
	 * getTopicReplyContainer - Returns the container element for the reply on a Forum Topic
	 * @param blogComment - Title of the reply
	 * @return xpath of the container for the reply
	 */
	public static String getTopicReplyContainer(String topicReply){
		return "xpath=//div[@class='entry-content lotusPostDetails'][contains(text(),'" + topicReply + "')]//ancestor::div[@class='hentry lotusPost ']";
	}

	/**
	 * getBackToCommunityForum -
	 * @param community
	 * @return
	 */
	public String getBackToCommunityForum(BaseCommunity community){
		return "link=Forum: " + community.getName();
	}
	
	/** end of selectors */

	public void createTopic(BaseForumTopic topic){
		
		//Select Start a topic button
		if(topic.getPartOfCommunity() != null){
			log.info("INFO: Topic is inside a community for widget");
			fluentWaitElementVisible(ForumsUIConstants.Start_A_Topic_Community);
			if(driver.isElementPresent(ForumsUIConstants.Start_A_Topic_Community)) {
				clickLinkWithJavascript(ForumsUIConstants.Start_A_Topic_Community);
			}
			else clickLink(ForumsUIConstants.Start_A_Topic);
		}else{
			if (!cfg.getUseNewUI()) {

				clickLinkWait(ForumsUIConstants.Start_A_Topic);
			}
			else {
				clickLinkWait(ForumsUIConstants.StartATopic);
			}
		}
		//Add title
		log.info("INFO: Entering title of new forum topic");
		this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputText_Title).type(topic.getTitle());
		
		//Mark this topic a question
		if(topic.getMarkAsQuestion()){
			log.info("INFO: Marking topic as a question");
			this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputCheckbox_MarkAsQuestion).click();
		}else{
			log.info("INFO: Topic is not a question");
		}
		
		//Enter forum topic tags if provided
		if(topic.getTags() != null){
			log.info("INFO: Entering any forum topic tags");
			this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputText_Tags).type(topic.getTags());
		}else{
			log.info("INFO: No tags provided");
		}

		//Enter forum topic description if provided
		if(topic.getDescription() != null){
			log.info("INFO: Entering forum topic description");
			if(!topic.getTinyEditorEnabled()){
				log.info("INFO: Entering forum topic description in CkEditor");
				typeNativeInCkEditor(topic.getDescription());
			}else{
				log.info("INFO: Entering forum topic description in TinyEditor");
				TinyEditorUI tui = new TinyEditorUI(driver);
				tui.typeInTinyEditor(topic.getDescription());
			}
		}else{
			log.info("INFO: No description provided");
		}
		
		//Check if topic has an attachment
		if (!topic.getAttachment().isEmpty()){
			log.info("INFO: Adding attachment file name " + topic.getAttachment());
			addAttachmentToTopic(topic.getAttachment());
		}else{
			log.info("INFO: no attachment provided");	
		}
		
		//Get page title
		String pageTitle = driver.getTitle();
		
		//Select Save
		log.info("INFO: Attempting to save new topic");
		scrollToElementWithJavaScriptWd(findElement(createByFromSizzle(ForumsUIConstants.Save_Forum_Topic_Button)));
		clickLinkWait(ForumsUIConstants.Save_Forum_Topic_Button);
		//wait until leaving forum creation page
		fluentWaitTitleChange(pageTitle);
	}
	/**
	 * Add Attachment (File) to a topic	
	 * @param fileName
	 */
	public void addAttachmentToTopic(String fileName){
	
		log.info("INFO: Click Attach a File link");
		clickLink(ForumsUIConstants.Attach_A_File);
		//set the path and file name of the file to upload
		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		driver.getSingleElement(ForumsUIConstants.AttachInput).typeFilePath(FilesUI.getFileUploadPath(fileName, cfg));
		clickLink(ForumsUIConstants.AttachOKBtn);
		log.info("INFO: Attached file to the topic");
		
	}
	
	/**
	 * Reply to a Topic already created
	 * @param topic
	 */
	public void replyToTopic(BaseForumTopic topic){

		// Reply to topic
		log.info("INFO: Click top Reply to Topic button");
		replyToTopicButtonCNX8(topic);
		waitForPageLoaded(driver);

		if (!topic.getTinyEditorEnabled()) {
			// Validate that the ckeditor has loaded by switching to the frame
			// and checking
			// if body webelement contenteditable='true' before proceeding
			switchToFrame(ForumsUIConstants.forumReplyToCkEditor_frame, ForumsUIConstants.forumReplyToCkEditor_body);
			switchToTopFrame();

			// Type text using native keystrokes (typing will occur in focused
			// window
			log.info("INFO: Type text into reply");
			typeNativeInCkEditor("Reply to topic... " + topic.getTitle());
		}else{
			log.info("INFO: Entering forum topic description in TinyEditor");
			TinyEditorUI tui = new TinyEditorUI(driver);
			tui.typeInTinyEditor(topic.getDescription());
		}
		
		// Save form
		log.info("INFO: Select the save button");
		fluentWaitElementVisible(ForumsUIConstants.Save_Topic_Reply);
		clickLink(ForumsUIConstants.Save_Topic_Reply);
		
		if (!topic.getTinyEditorEnabled()){
			fluentWaitTextPresent("Re: " + topic.getTitle());
		}
	}
	
	public void gotoStartATopic() {
		clickLink(ForumsUIConstants.Start_A_Topic);
		try {
			fluentWaitPresent(CommunitiesUIConstants.TopicTitle);
		} catch (TimeoutException e) {
			log.info("WARN: Failed to click Start A Topic, clicking with javascript");
			clickLinkWithJavascript(ForumsUIConstants.Start_A_Topic);
		}
	}

	public void delete(BaseForum forum) {
		TestConfigCustom cfg = TestConfigCustom.getInstance();
		if (!cfg.getUseNewUI()) {
			log.info("INFO: Select Delete from menu");
			selectForumActionMenu(Forum_Action_Menu.DELETE);
		} else {
			clickLinkWait(ForumsUIConstants.deleteLink);
		}
		log.info("INFO: Select delete button");
		clickLinkWait(ForumsUIConstants.deleteButton);
	}
	
	public void selectForumActionMenu(Forum_Action_Menu choice) {
		
		log.info("INFO: Select Forum Actions Menu");
		driver.executeScript("window.scrollBy(3000,0)");
		clickLinkWait(ForumsUIConstants.Forum_ActionMember);
		
		List<Element> options = driver.getVisibleElements(ForumsUIConstants.Forum_ActionMember_Opt);
		Element option = null;		
		Iterator<Element> optionList = options.iterator();
		while(optionList.hasNext()){			
		    option = optionList.next();
		    if(option.getText().contains(choice.getMenuItemText())){
		    	log.info("INFO: Option found " + choice.getMenuItemText());
		    	option.click();
		    }
		}
		
		
	}

	public void create(BaseForum forum){
		
		//Select Forum tab
		log.info("INFO: Select Forum tab");
		clickLink(ForumsUIConstants.Forum_Tab);
		
		//click on start a new forum button
		log.info("INFO: Select Start a Forum button");
		clickLink(ForumsUIConstants.Start_A_Forum);
		
		//Add name
		log.info("INFO: Entering name of new Forum");;
		fluentWaitElementVisible(ForumsUIConstants.Start_A_Forum_InputText_Name);
		this.driver.getSingleElement(ForumsUIConstants.Start_A_Forum_InputText_Name).type(forum.getName());
		
		//Enter forum tags if provided
		if(forum.getTags() != null){
			log.info("INFO: Entering any forum tags");
			this.driver.getSingleElement(ForumsUIConstants.Start_A_Forum_InputText_Tags).type(forum.getTags());
		}else{
			log.info("INFO: No tags provided");
		}

		//Enter forum description if provided
		if(forum.getDescription() != null){
			log.info("INFO: Entering forum description");
			this.driver.getSingleElement(ForumsUIConstants.Start_A_Forum_Textarea_Description).type(forum.getDescription());
		}else{
			log.info("INFO: No description provided");
		}
		
		//Select to follow new topic or not
		if(forum.getNewTopicNotify() == false){
			log.info("INFO: Author is selecting to not follow new topics in this forum");
			this.driver.getSingleElement(ForumsUIConstants.Forum_Auto_Follow_Option).click();
		}else{
			log.info("INFO: Author is selecting to follow new topics in this forum (which is default)");
		}
		
		//Save New forum
		log.info("INFO: Clicking on Save Button");
		scrollToElementWithJavaScriptWd(findElement(createByFromSizzle(ForumsUIConstants.Save_Forum_Button)));
		clickLinkWait(ForumsUIConstants.Save_Forum_Button);
	}
	
	public void clickPinTopic(BaseForumTopic topic){
		clickLinkWait(getTopicSelector(topic));
		clickLinkWait(ForumsUIConstants.PinTopicLink);
	}
	
	public void clickUnPinTopic(BaseForumTopic topic){
		clickLinkWait(getTopicSelector(topic));
		clickLinkWait(ForumsUIConstants.UnPinTopicLink);
	}
	
	public static ForumsUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  ForumsUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  ForumsUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new  ForumsUICloud(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  ForumsUIOnPrem(driver);
		} else if(product.toLowerCase().equals("multi")) {
			return new  ForumsUIOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
	/**
	 *
	 * @param uuid
	 * @return the string of <tr> which includes the topic's uuid
	 */
	public static String getTopicTr(String uuid){
		return "css=tr[uuid='"+ uuid +"']";
	}
	
	/**
	 * 
	 * @param communityName
	 * @return
	 */
	public static String getRelatedCommunityLink(String communityName){
		// This selector is not written as div.recommDiv.rc_recomm_item because there exist
		// invisible <div class="lotusHidden recommDiv rc_recomm_item"> elements
		return "css=div[class='recommDiv rc_recomm_item'] a[title='"+communityName+"']";
	}
	
	/**
	 * 
	 */
	public static String getLikeDivOnOverview(String widgetID){
		return "css=div#"+ widgetID+"Section div.lotusLike";
	}
	public abstract void verifyBizCard();
	

	/**
	 *  Open Start Forum Topic Page and verify Tiny Editor functionality
	 * 
	 * @param Base Forum object
	 * @return String Text present in Description of Tiny Editor.
	 */
	public String verifyTinyEditor(BaseForumTopic topic) {
		TinyEditorUI tui = new TinyEditorUI(driver);

		if (!topic.getDescription().contains("Reply")) {
			createTopicForTinyEditor(topic);
		} else {
			// Reply to topic
			log.info("INFO: Click top Reply to Topic button");
			clickLink(ForumsUIConstants.ReplyTopic);
		}
		tui.clickOnMoreLink();

		log.info("INFO: Entering a description and validating the functionality of Tiny Editor");
		if (topic.getDescription() != null) {

			String TE_Functionality[] = topic.getTinyEditorFunctionalitytoRun().split(",");

			for (String functionality : TE_Functionality) {
				switch (functionality) {
				case "verifyParaInTinyEditor":
					log.info("INFO: Validate Paragragh and header functionality of Tiny Editor");
					tui.verifyParaInTinyEditor(topic.getDescription());
					break;
				case "verifyAttributesInTinyEditor":
					log.info("INFO: Validate Attributes functionality of Tiny Editor");
					tui.verifyAttributesInTinyEditor(topic.getDescription());
					break;
				case "verifyPermanentPenInTinyEditor":
					log.info("INFO: Validate Permanent Pen functionality of Tiny Editor");
					tui.verifyPermanentPenInTinyEditor(topic.getDescription());
					break;
				case "verifyUndoRedoInTinyEditor":
					log.info("INFO: Validate Undo and Redo functionality of Tiny Editor");
					tui.verifyUndoRedoInTinyEditor(topic.getDescription());
					break;
				case "verifyAlignmentInTinyEditor":
					log.info("INFO: Validate Alignment functionality of Tiny Editor");
					tui.verifyAlignmentInTinyEditor(topic.getDescription());
					break;
				case "verifyIndentsInTinyEditor":
					log.info("INFO: Validate Indents functionality of Tiny Editor");
					tui.verifyIndentsInTinyEditor(topic.getDescription());
					break;
				case "verifyBulletsAndNumbersInTinyEditor":
					log.info("INFO: Validate Bullets and Numbers functionality of Tiny Editor");
					tui.verifyBulletsAndNumbersInTinyEditor(topic.getDescription());
					break;
				case "verifyHorizontalLineInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tui.verifyHorizontalLineInTinyEditor(topic.getDescription());
					break;
				case "verifySpecialCharacterInTinyEditor":
					log.info("INFO: Validate Special character functionality of Tiny Editor");
					tui.verifySpecialSymbolsInTinyEditor("SpecialChar");
					break;
				case "verifyEmotionsInTinyEditor":
					log.info("INFO: Validate Emoticons functionality of Tiny Editor");
					tui.verifySpecialSymbolsInTinyEditor("Emotions");
					break;
				case "verifySpellCheckInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tui.verifySpellCheckInTinyEditor(topic.getDescription());
					break;
				case "verifyRowsCoulmnOfTableInTinyEditor":
					log.info("INFO: Validate Rows and Columns of Table in Tiny Editor");
					tui.verifyRowsCoulmnOfTableInTinyEditor(topic.getDescription());
					break;
				case "verifyFormatPainterInTinyEditor":
					log.info("INFO: Validate Format Painter in Tiny Editor");
					tui.verifyFormatPainterInTinyEditor(topic.getDescription());
					break;
				case "verifyFontInTinyEditor":
					log.info("INFO: Validate font functionality of Tiny Editor");
					tui.verifyFontInTinyEditor(topic.getDescription());
					break;
				case "verifyFontSizeInTinyEditor":
					log.info("INFO: Validate font Size functionality of Tiny Editor");
					tui.verifyFontSizeInTinyEditor(topic.getDescription());
					break;
				case "verifyLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyLinkImageInTinyEditor(topic.getDescription());
					break;
				case "verifyRightLeftParagraphInTinyEditor":
					log.info("INFO: Validate Left to Right paragraph functionality of Tiny Editor");
					tui.verifyRightLeftParagraphInTinyEditor(topic.getDescription());
					break;
				case "verifyOtherTextAttributesAndFullScreenInTinyEditor":
					log.info("INFO: Validate other text attributes functionality of Tiny Editor");
					tui.verifyOtherTextAttributesAndFullScreenInTinyEditor(topic.getDescription());
					break;
				case "verifyFindReplaceInTinyEditor":
					log.info("INFO: Validate Find and Replace functionality of Tiny Editor");
					tui.verifyFindReplaceInTinyEditor(topic.getDescription());
					break;
				case "verifyInsertLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyInsertLinkImageInTinyEditor(topic.getDescription());
					break;
				case "verifyTextColorInTinyEditor":
					log.info("INFO: Validate Font Text Color functionality of Tiny Editor");
					tui.verifyTextColorInTinyEditor(topic.getDescription());
					break;
				case "verifyBackGroundColorInTinyEditor":
					log.info("INFO: Validate Font BackGround Color functionality of Tiny Editor");
					tui.verifyBackGroundColorInTinyEditor(topic.getDescription());
					break;
				case "verifyWordCountInTinyEditor":
					log.info("INFO: Validate Word Count functionality of Tiny Editor");
					tui.verifyWordCountInTinyEditor(topic.getDescription());
					break;
				case "verifyUploadImageFromDiskInTinyEditor":
					log.info("INFO: Validate Upload image from Disk functionality of Tiny Editor");
					tui.verifyUploadImageFromDiskInTinyEditor();
					break;
				case "verifyUploadImageFromDiskInTinyEditorForums":
					log.info("INFO: Validate Upload image from Disk functionality of Tiny Editor");
					tui.verifyUploadImageFromDiskInTinyEditorForums();
					break;
				case "verifyBlockQuoteInTinyEditor":
					log.info("INFO: Validate Block quote functionality of Tiny Editor");
					tui.verifyBlockQuoteInTinyEditor(topic.getDescription());
					break;
				case "verifyInsertMediaInTinyEditor":
					log.info("INFO: Validate Insert Media functionality of Tiny Editor");
					tui.verifyInsertMediaInTinyEditor(topic.getDescription());
					break;
				case "verifyLinkToConnectionsFilesInTinyEditor":
					log.info("INFO: Validate Link to connections files from files in Tiny Editor");
					tui.addLinkToConnectionsFilesInTinyEditor(topic.getDescription());
					break;
				case "verifyCodeSampleIntinyEditor":
					log.info("INFO: Validate Code Sample functionality of Tiny Editor");
					tui.verifyCodeSampleIntinyEditor(topic.getDescription());
					break;
				case "verifyInsertiFrameInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyInsertiFrameInTinyEditor(topic.getDescription());
					break;
				}
			}
		}

		String TEText = tui.getTextFromTinyEditor();
		log.info("INFO: Get the text from Tiny Editor body" + TEText);

		if (!topic.getAttachment().isEmpty()) {
			log.info("INFO: Adding attachment file name " + topic.getAttachment());
			addAttachmentToTopic(topic.getAttachment());
		} else {
			log.info("INFO: no attachment provided");
		}

		// Select Save
		log.info("INFO: Attempting to save new topic");
		scrolltoViewElement((WebElement) (getFirstVisibleElement(ForumsUIConstants.Save_Forum_Topic_Button)).getBackingObject(),
				(WebDriver) driver.getBackingObject());
		clickLink(ForumsUIConstants.Save_Forum_Topic_Button);

		log.info("INFO: " + topic.getTitle() + " was created successfully");

		return TEText;
	}
	
	
	private void createTopicForTinyEditor(BaseForumTopic topic) {

		// Select Start a topic button
		if (topic.getPartOfCommunity() != null) {
			log.info("INFO: Topic is inside a community for widget");
			if (driver.isElementPresent(ForumsUIConstants.Start_A_Topic_Community)) {
				clickLinkWait(ForumsUIConstants.Start_A_Topic_Community);
			} else
				clickLink(ForumsUIConstants.Start_A_Topic);
		} else {
			clickLinkWait(ForumsUIConstants.Start_A_Topic);
		}

		// Add title
		log.info("INFO: Entering title of new forum topic");
		this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputText_Title).type(topic.getTitle());

		// Mark this topic a question
		if (topic.getMarkAsQuestion()) {
			log.info("INFO: Marking topic as a question");
			this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputCheckbox_MarkAsQuestion).click();
		} else {
			log.info("INFO: Topic is not a question");
		}

		// Enter forum topic tags if provided
		if (topic.getTags() != null) {
			log.info("INFO: Entering any forum topic tags");
			this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputText_Tags).type(topic.getTags());
		} else {
			log.info("INFO: No tags provided");
		}

	}

	public void verifyInsertedLink(String topic)
    {
        TinyEditorUI tui = new TinyEditorUI(driver);
        tui.verifyInsertedLinkinDescription(topic);
    }

	public String getForumText() {
		this.waitForPageLoaded(driver);
		String description = getFirstVisibleElement(ForumsUIConstants.ForumDesc).getText();
		return description;
	}

	public String getForumDescText() {
		this.waitForPageLoaded(driver);
		this.fluentWaitElementVisibleOnce(ForumsUIConstants.ForumDesc);
		return this.getFirstVisibleElement(ForumsUIConstants.ForumDesc).getText();
	}
	
	public String getForumReplyText(){
		this.waitForPageLoaded(driver);
		this.fluentWaitElementVisibleOnce(ForumsUIConstants.forumReplyDesc);
		return this.getFirstVisibleElement(ForumsUIConstants.forumReplyDesc).getText();
	}

	public String editDescriptionInTinyEditor(BaseForumTopic topic, String editedDescripton) {

		if (!editedDescripton.contains("Reply")) {
			log.info("INFO: Edit topic description in TinyEditor");
			clickLinkWait(ForumsUIConstants.EditTopic);
		} else {
			log.info("INFO: Edit topic description in TinyEditor");
			clickLinkWait(ForumsUIConstants.EditReplyTopic);
		}

		log.info("INFO: Entering forum topic description in TinyEditor");
		TinyEditorUI tui = new TinyEditorUI(driver);
		tui.clearInTinyEditor();
		tui.typeInTinyEditor(editedDescripton);

		// Select Save
		log.info("INFO: Attempting to save new topic");
		scrolltoViewElement((WebElement) (getFirstVisibleElement(ForumsUIConstants.Save_Forum_Topic_Button)).getBackingObject(),
				(WebDriver) driver.getBackingObject());
		clickLink(ForumsUIConstants.Save_Forum_Topic_Button);

		log.info("INFO: " + topic.getTitle() + " was edited successfully");

		String editedDesc = "";
		if (!editedDescripton.contains("Reply")) {
			editedDesc = getFirstVisibleElement(ForumsUIConstants.ForumDesc).getText().trim();
		} else {
			editedDesc = getFirstVisibleElement(ForumsUIConstants.forumReplyDesc).getText().trim();
		}

		return editedDesc;
	}

	/**
	 *  This function will open image url in new tab and copy it
	 * 
	 *  @return String URL of the copies image to verify.
	 */
	public String loadUrlInNewtAndCopyImage() {
		Actions action = new Actions((WebDriver) driver.getBackingObject()); 
    	
		String imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQw1L39-k24Hyzoi0Sy7Bgg8auce5a8udLnZlFh-7ogvGq1pCB9";
		
		WebDriver wd = (WebDriver) driver.getBackingObject();
		
		JavascriptExecutor jse = (JavascriptExecutor)wd;
		jse.executeScript("window.open()");
		
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		wd.switchTo().window(tabs.get(1));
		
		wd.get(imageUrl);
				
		action.moveToElement(wd.findElement(By.tagName("img"))).doubleClick().build().perform();
		action.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).build().perform();
		action.keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).build().perform();
			
		wd.close();
		
		wd.switchTo().window(tabs.get(0));
		
		return imageUrl;
		
	}

	
	/**
	 *  This function will paste content from clip board
	 * 
	 */
	public void pasteFromClipboard() {
		Actions action = new Actions((WebDriver) driver.getBackingObject());

		action.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).build().perform();

	}
	
	/**
	 * editDescriptionInForumTopic - This method will edit the Forum Topic description
	 * @param - topic,editedDescripton
	 * @return - editedDesc
	 */

	public String editDescriptionInForumTopic(BaseForumTopic topic, String editedDescripton) {

		log.info("INFO: Click on the Edit button");
		clickLink(ForumsUIConstants.editForumTopic);
		clearCkEditor();
		typeInCkEditor(editedDescripton);

		// Select Save
		log.info("INFO: Attempting to save new topic");
		scrolltoViewElement(
				(WebElement) (getFirstVisibleElement(ForumsUIConstants.Save_Forum_Topic_Button)).getBackingObject(),
				(WebDriver) driver.getBackingObject());
		clickLink(ForumsUIConstants.Save_Forum_Topic_Button);

		log.info("INFO: " + topic.getTitle() + " was edited successfully");
		
		waitForPageLoaded(driver);

		String editedDesc = "";
		if (!editedDescripton.contains("Reply")) {
			fluentWaitPresentWithRefresh(ForumsUIConstants.ForumDesc);
			editedDesc = getFirstVisibleElement(ForumsUIConstants.ForumDesc).getText().trim();
		} else {
			editedDesc = getFirstVisibleElement(ForumsUIConstants.forumReplyDesc).getText().trim();
		}

		return editedDesc;
	}
	
	/**
	 * Validate Add Owner
	 */
	public void validateAddOwner() {
		if (!cfg.getUseNewUI()) {

			Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.AddOwners),"Verify the 'Add Owners' link in the forum");
		}

	}
	
	/**
	 * Click on 'Forums' link in the top left corner on CNX7UI 
	 */
	public void clickCornerTopLeftForumsLink() {
		if (!cfg.getUseNewUI()) {

			clickLinkWait(ForumsUIConstants.topComponentForumLink);
		}
	}	
	
	/**
	 * Click Reply to Topic button locator on CNX8UI / CNX7UI
	 */
	public void replyToTopicButtonCNX8(BaseForumTopic topic) {
		if (!cfg.getUseNewUI()) {

			clickLink(ForumsUIConstants.Reply_to_topic);
		}
		else {
			clickLink(ForumsUIConstants.replyToTopicBtnnewUI.replace("PLACEHOLDER", topic.getTitle()));
		}
	}

}
